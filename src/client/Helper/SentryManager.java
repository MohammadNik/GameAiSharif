package client.Helper;

import client.model.*;

import java.util.ArrayList;

public class SentryManager implements HeroManager {
    private World world;
    private Hero sentry;

    @Override
    public void preProcess(World world) {
        // TODO: 2/22/2019 what to put here?!
    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.sentry = currentHero;
        if (moveToAttackPosition()) ;
        else if (moveToObjectiveZone()) ;
    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.sentry = currentHero;
        if (sentryRay()) ;
        else if (sentryAttack()) ;
        else if (sentryDodge()) ;
    }

    /*************************************move sentry to objective zone "methods"**************************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final move to objective zone method
    private boolean moveToObjectiveZone() {
        try {
            if (sentry.getCurrentCell().isInObjectiveZone()) return false;
            Cell nearestCellFromObjectiveZone = Helper.nearestCellFromOZ(world, sentry.getCurrentCell());
            Direction[] directions;
            directions = world.getPathMoveDirections(sentry.getCurrentCell(), nearestCellFromObjectiveZone);
            for (Direction direction : directions) {
                world.moveHero(sentry, direction);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /************************************move sentry to attack position "methods"**************************************/ // TODO: 2/21/2019 functionality improvement is needed
    // get all visible enemy heroes cells
    private ArrayList<Cell> getVisibleEnemyHeroesCells() {
        ArrayList<Cell> enemyCells = new ArrayList<>();

        // check all map cells to find visible enemies cells
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                if (world.getOppHero(cell) != null) enemyCells.add(cell);
            } // FIXME: 2/21/2019 also make it a method for multi-use if it's possible
        return enemyCells;
    }

    // get nearest visible enemy hero from sentry hero
    private Cell getNearestEnemyHero() {
        ArrayList<Cell> enemyCells = getVisibleEnemyHeroesCells();
        // check which enemy is nearest to sentry hero
        Cell nearestEnemyCell = enemyCells.stream().reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
        if (isInAttackRange(nearestEnemyCell)) return null;
        return nearestEnemyCell;
    }

    // gets all non-wall cells around an enemy within range of "7"
    private ArrayList<Cell> getAttackPositionCells() {
        Cell enemyCell = getNearestEnemyHero();
        ArrayList<Cell> attackCells = new ArrayList<>();
        // check which cells are within range of 7
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                if (!(cell.isWall()) && world.manhattanDistance(cell, enemyCell) == 7) // FIXME: 2/21/2019 no name found to create a check method :(
                    attackCells.add(cell);
            } // FIXME: 2/21/2019 also make it a method for multi-use if it's possible
        return attackCells;
    }

    // get nearest cell in attack range of sentry of an enemy
    private Cell getNearestAttackCell() {
        ArrayList<Cell> attackCells = getAttackPositionCells();
        // check which attack cell is nearest to sentry hero
        return attackCells.stream().reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }

    // final move to attack position method
    private boolean moveToAttackPosition() {
        try {
            if (sentry.getCurrentCell().isInObjectiveZone()) return false;
            Cell attackCell = getNearestAttackCell();
            world.moveHero(sentry, world.getPathMoveDirections(sentry.getCurrentCell(), attackCell)[0]); // FIXME: 2/23/2019 stuck in moving loop?!
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /***************************************normal attack ability "methods"********************************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final normal attack method
    private boolean sentryAttack() {
        try {
            world.castAbility(sentry, AbilityName.SENTRY_ATTACK, getBestTargetAttack());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Cell getBestTargetAttack() {
        ArrayList<Cell> enemyCells = new ArrayList<>();
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                if (world.getOppHero(cell) != null) enemyCells.add(cell);
            }
        return enemyCells.stream()
                .filter(cell -> world.isInVision(sentry.getCurrentCell(), cell))
                .filter(cell -> world.manhattanDistance(sentry.getCurrentCell(), cell) <= 7)
                .reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }

    /*******************************special sentry offensive ability 'RAY' "method"s***********************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final special offensive ability method "ray"
    private boolean sentryRay() {
        try {
            if (isReady(sentry.getAbility(AbilityName.SENTRY_RAY))) {
                world.castAbility(sentry, AbilityName.SENTRY_RAY, getBestTargetRay());
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Cell getBestTargetRay() {
        ArrayList<Cell> enemyCells = new ArrayList<>();
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                if (world.getOppHero(cell) != null) enemyCells.add(cell);
            }
        return enemyCells.stream()
                .filter(cell -> world.isInVision(sentry.getCurrentCell(), cell))
                .reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }

    /******************************offensive/defensive sentry ability 'dodge' "method"*********************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final offensive/defensive ability method "dodge"
    private boolean sentryDodge() {
        try {
            if (isReady(sentry.getAbility(AbilityName.SENTRY_DODGE))) {
                if (defensiveDodge()) ;
                else if (offensiveDodge()) ;
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    // defensive use of ability method "dodge"
    private boolean defensiveDodge() {
        if (nearestEnemyDistance() <= 4) {
            world.castAbility(sentry, AbilityName.SENTRY_DODGE, getNearestResZone());
            return true;
        }
        return false;
    }

    // offensive use of ability method "dodge'
    private boolean offensiveDodge() {
        if (sentry.getCurrentCell().isInObjectiveZone()) return false;
        world.castAbility(sentry, AbilityName.SENTRY_DODGE, getNearestAttackCell());
        return true;
    }

    /**********************************************misc "methods*******************************************************/
    // get nearer cell from two given cells and a hero
    private Cell getNearerCellFromHero(Cell firstCell, Cell secondCell) {
        if (world.manhattanDistance(sentry.getCurrentCell(), firstCell) <= world.manhattanDistance(sentry.getCurrentCell(), secondCell))
            return firstCell;
        else return secondCell;
    }

    // check if given cell is in normal attack range of sentry which is "7"
    private boolean isInAttackRange(Cell targetCell) {
        return world.manhattanDistance(sentry.getCurrentCell(), targetCell) <= 7;
    }

    // check an ability remaining cooldown then return false if it's ready or true if it's not ready
    private boolean isReady(Ability ability) {
        return ability.getRemCooldown() == 0;
    }

    // get manhattan distance of sentry and nearest enemy hero
    private int nearestEnemyDistance() {
        return world.manhattanDistance(sentry.getCurrentCell(), getNearestEnemyHero());
    }

    // get nearest ally respawn zone cell in map
    private Cell getNearestResZone() {
        Map map = world.getMap();
        Cell[] resZoneCells = map.getMyRespawnZone();
        Cell nearestCell = resZoneCells[0]; // FIXME: 2/23/2019
        for (Cell cell : resZoneCells) {
            nearestCell = getNearerCellFromHero(nearestCell, cell);
        }
        return nearestCell;
    }

    // get best enemy cell as a target
    private Cell getBestTargetCell() {
        Hero lowestEnemyHP = getVisibleEnemyHeroes().get(0);
        for (Hero hero : getVisibleEnemyHeroes()) {
            if (hero.getCurrentHP() < lowestEnemyHP.getCurrentHP()) lowestEnemyHP = hero;
        }

        return lowestEnemyHP.getCurrentCell();
    }

    // get all visible enemy heroes
    private ArrayList<Hero> getVisibleEnemyHeroes() {
        ArrayList<Hero> visibleHeroes = new ArrayList<>();
        for (Cell cell : getVisibleEnemyHeroesCells()) {
            visibleHeroes.add(world.getOppHero(cell));
        }
        return visibleHeroes;
    }
}