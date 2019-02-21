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
        // TODO: 2/21/2019 What to put here?1 
    }

    @Override
    public void move(Hero currentHero) {
        if (moveToAttackPosition(currentHero)) ;
        else if (moveToObjectiveZone(currentHero)) ;
    }

    @Override
    public void takeAction(Hero currentHero) {
        if (sentryCastRay(currentHero)) ;
        else if (sentryAttack(currentHero)) ;
    }

    /*************************************move sentry to objective zone "methods"**************************************/ // FIXME: 2/21/2019 functionality improvement is needed
    // final move to objective zone method
    private boolean moveToObjectiveZone(Hero sentry) {
        // get nearest cell of hero from objective zone
        Cell nearestCellFromObjectiveZone = Helper.nearestCellFromOZ(world, sentry.getCurrentCell());
        // get next direction to move to objective zone
        Direction nextDirection;
        try {
            nextDirection = world.getPathMoveDirections(sentry.getCurrentCell(), nearestCellFromObjectiveZone)[0];
        } catch (NullPointerException e) {
            return false;
        }
        world.moveHero(sentry, nextDirection);
        return true;
    }

    /************************************move sentry to attack position "methods"**************************************/ // FIXME: 2/21/2019 functionality improvement is needed
    // get all visible enemy heroes
    private ArrayList<Cell> getVisibleEnemyHeroes() {
        ArrayList<Cell> enemyCells = new ArrayList<>();
        // check all map cells to find visible enemies cells
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                try {
                    if (world.getOppHero(cell) != null) enemyCells.add(cell);
                } catch (NullPointerException e) {
                    return null;
                }
            } // FIXME: 2/21/2019 also make it a method for multi-use if it's possible

        if (enemyCells.isEmpty()) return null;
        return enemyCells;
    }

    // get nearest visible enemy hero from sentry hero
    private Cell getNearestEnemyHero(Hero sentry) {
        ArrayList<Cell> enemyCells = getVisibleEnemyHeroes();
        if (enemyCells == null) return null;
        // check which enemy is nearest to sentry hero
        Cell nearestEnemyCell = enemyCells.get(0); // FIXME: 2/21/2019
        for (Cell enemyCell : enemyCells) {
            nearestEnemyCell = getNearerCellFromHero(sentry, nearestEnemyCell, enemyCell);
        }
        if (nearestEnemyCell == null) return null;
        return nearestEnemyCell;
    }

    // gets all non-wall cells around an enemy within range of "7"
    private ArrayList<Cell> getAttackPositionCells(Hero sentry) {
        Cell enemyCell = getNearestEnemyHero(sentry);
        if (enemyCell == null) return null;
        ArrayList<Cell> attackCells = new ArrayList<>();
        // check which cells are within range of 7
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                if (!(cell.isWall()) && world.manhattanDistance(cell, enemyCell) <= 7)
                    attackCells.add(cell); // FIXME: 2/21/2019 no name found to create a check method :(
            } // FIXME: 2/21/2019 also make it a method for multi-use if it's possible
        if (attackCells.isEmpty()) return null;
        return attackCells;
    }

    // get nearest cell in attack range of sentry of an enemy
    private Cell getNearestAttackCell(Hero sentry) {
        ArrayList<Cell> attackCells = getAttackPositionCells(sentry);
        if (attackCells == null) return null;
        // check which attack cell is nearest to sentry hero
        Cell nearestAttackCell = attackCells.get(0); // FIXME: 2/21/2019
        for (Cell attackCell : attackCells) {
            nearestAttackCell = getNearerCellFromHero(sentry, nearestAttackCell, attackCell);
        }
        if (nearestAttackCell == null) return null;
        return nearestAttackCell;
    }

    // final move to attack position method
    private boolean moveToAttackPosition(Hero sentry) {
        Cell attackCell = getNearestAttackCell(sentry);
        if (attackCell == null) return false;
        world.moveHero(sentry, world.getPathMoveDirections(sentry.getCurrentCell(), attackCell)[0]);
        return true;
    }

    /***************************************normal attack ability "methods"********************************************/ // FIXME: 2/21/2019 functionality improvement is needed
    // final normal attack method
    private boolean sentryAttack(Hero sentry) {
        Cell enemyCell = getNearestEnemyHero(sentry);
        // check if target is in attack range then crush 'em all :P
        if (isInAttackRange(sentry, enemyCell)) world.castAbility(sentry, AbilityName.SENTRY_ATTACK, enemyCell);
        else return false; // if target is not in range
        return true; // if target is in range
    }

    /*******************************special sentry offensive ability 'RAY' "method"s***********************************/ // FIXME: 2/21/2019 functionality improvement is needed
    // final special offensive ability method "ray"
    private boolean sentryCastRay(Hero sentry) {
        Cell enemyCell = getNearestEnemyHero(sentry);
        // check if target is in attack range then poof 'em all :P
        if (isInAttackRange(sentry, enemyCell)) world.castAbility(sentry, AbilityName.SENTRY_RAY, enemyCell);
        else return false; // if target is not in range
        return true; // if target is range
    }

    /**********************************************misc "methods*******************************************************/
    // get nearer cell from two given cells and a hero
    private Cell getNearerCellFromHero(Hero sentry, Cell firstCell, Cell secondCell) {
        if (world.manhattanDistance(sentry.getCurrentCell(), firstCell) <= world.manhattanDistance(sentry.getCurrentCell(), secondCell))
            return firstCell;
        else return secondCell;
    }

    // check if given cell is in normal attack range of sentry which is "7"
    private boolean isInAttackRange(Hero sentry, Cell targetCell) {
        return world.manhattanDistance(sentry.getCurrentCell(), targetCell) <= 7;
    }
}

// TODO: 2/21/2019 make null execptions handeled with try-catch statement(or throws?!) 