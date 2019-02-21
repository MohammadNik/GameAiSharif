package client.Helper;

import client.model.*;

import java.util.ArrayList;

public class SentryManager implements HeroManager {
    private World world;

    public SentryManager(World world) {
        this.world = world;
    }

    @Override
    public void preProcess() {

    }

    @Override
    public void move(Hero currentHero) {
        if (moveToAttackPosition(currentHero)) ;
        else if (moveToObjectiveZone(currentHero)) ;
    }

    @Override
    public void takeAction(Hero currentHero) {
        if (sentryAttack(currentHero)) ;
    }

    /**********************************************************************************************************************/
    // move sentry to objective zone
    public boolean moveToObjectiveZone(Hero sentry) {
        Cell nearestCellFromObjectiveZone = Helper.nearestCellFromOZ(world, sentry.getCurrentCell());
        if (world.getPathMoveDirections(sentry.getCurrentCell(), nearestCellFromObjectiveZone) == null) return false;
        Direction nextDirection = world.getPathMoveDirections(sentry.getCurrentCell(), nearestCellFromObjectiveZone)[0];
        world.moveHero(sentry, nextDirection);
        return true;
    }

    /**********************************************************************************************************************/
    // move sentry to attack position
    public ArrayList<Cell> getVisibleEnemyHeroes() {
        ArrayList<Cell> enemyCells = new ArrayList<>();

        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells)
                try {
                    if (world.getOppHero(cell) != null) enemyCells.add(cell); // FIXME: 2/21/2019
                } catch (NullPointerException e) {
                    return null;
                }

        if (enemyCells.isEmpty()) return null;
        return enemyCells;
    }

    public Cell getNearestEnemyHero(Hero sentry) {
        ArrayList<Cell> enemyCells = getVisibleEnemyHeroes();
        if (enemyCells == null) return null;
        Cell nearestEnemyCell = enemyCells.get(0);
        for (Cell enemyCell : enemyCells) {
            if (world.manhattanDistance(enemyCell, sentry.getCurrentCell()) < world.manhattanDistance(nearestEnemyCell, sentry.getCurrentCell()))
                nearestEnemyCell = enemyCell;
        }
        if (nearestEnemyCell == null) return null;
        return nearestEnemyCell;
    }

    public ArrayList<Cell> getAttackPositionCells(Hero sentry) {
        Cell enemyCell = getNearestEnemyHero(sentry);
        if (enemyCell == null) return null;
        ArrayList<Cell> attackCells = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            for (Cell cell : world.getMap().getCells()[i]) {
                if (!(cell.isWall()) && world.manhattanDistance(cell, enemyCell) <= 7) attackCells.add(cell);
            }
        }
        if (attackCells.isEmpty()) return null;
        return attackCells;
    }

    public Cell getNearestAttackCell(Hero sentry) {
        ArrayList<Cell> attackCells = getAttackPositionCells(sentry);
        if (attackCells == null) return null;
        Cell nearestAttackCell = attackCells.get(0);
        for (Cell attackCell : attackCells) {
            if (world.manhattanDistance(attackCell, sentry.getCurrentCell()) < world.manhattanDistance(nearestAttackCell, sentry.getCurrentCell()))
                nearestAttackCell = attackCell;
        }
        if (nearestAttackCell == null) return null;
        return nearestAttackCell;
    }

    public boolean moveToAttackPosition(Hero sentry) {
        Cell attackCell = getNearestAttackCell(sentry);
        if (attackCell == null) return false;
        world.moveHero(sentry, world.getPathMoveDirections(sentry.getCurrentCell(), attackCell)[0]);
        return true;
    }

    /**************************s********************************************************************************************/
    // attack an enemy hero
    public boolean sentryAttack(Hero sentry) {
        Cell enemyCell = getNearestEnemyHero(sentry);
        if (isInAttackRange(sentry, enemyCell)) world.castAbility(sentry, AbilityName.SENTRY_ATTACK, enemyCell);
        else return false;
        return true;
    }

    public boolean isInAttackRange(Hero sentry, Cell targetCell) {
        return world.manhattanDistance(sentry.getCurrentCell(), targetCell) <= 7;
    }

    /**********************************************************************************************************************/
    // TODO: 2/21/2019 cast ray spell
    public boolean sentryRay(Hero sentry) {

        return true;
    }
}