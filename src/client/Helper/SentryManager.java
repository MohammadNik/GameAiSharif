package client.Helper;

import client.model.*;

import java.util.ArrayList;

public class SentryManager implements HeroManager {
    private World world;


    @Override
    public void preProcess(World world) {
        // TODO: 2/22/2019 what to put here?!
    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

        if (moveToAttackPosition(currentHero))
            System.out.println("sentry moved to attack position"); // first attempt to move to attack position
        else if (moveToObjectiveZone(currentHero))
            System.out.println("sentry moved to objective zone"); // then if action above failed with any reason move to objective zone
    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

        if (sentryCastRay(currentHero))
            System.out.println("sentry casted ray"); // first attempt to cast ability "ray" to an enemy
        else if (sentryAttack(currentHero))
            System.out.println("sentry attacked normally"); // then if casting failed with any reason attack them normally
        else if (sentryDodge(currentHero))
            System.out.println("sentry dodged"); // if attacking failed with any reason then dodge
    }

    /*************************************move sentry to objective zone "methods"**************************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final move to objective zone method
    private boolean moveToObjectiveZone(Hero sentry) {
        try {
            // get nearest cell of hero from objective zone
            Cell nearestCellFromObjectiveZone = Helper.nearestCellFromOZ(world, sentry.getCurrentCell());
            // get next direction to move to objective zone
            Direction nextDirection;
            nextDirection = world.getPathMoveDirections(sentry.getCurrentCell(), nearestCellFromObjectiveZone)[0]; // FIXME: 2/23/2019
            world.moveHero(sentry, nextDirection);
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
    private Cell getNearestEnemyHero(Hero sentry) {
        ArrayList<Cell> enemyCells = getVisibleEnemyHeroesCells();
        // check which enemy is nearest to sentry hero
        Cell nearestEnemyCell = enemyCells.get(0); // FIXME: 2/21/2019
        for (Cell enemyCell : enemyCells) {
            nearestEnemyCell = getNearerCellFromHero(sentry, nearestEnemyCell, enemyCell);
        }
        return nearestEnemyCell;
    }

    // gets all non-wall cells around an enemy within range of "7"
    private ArrayList<Cell> getAttackPositionCells(Hero sentry) {
        Cell enemyCell = getNearestEnemyHero(sentry);
        ArrayList<Cell> attackCells = new ArrayList<>();
        // check which cells are within range of 5 to 7
        for (Cell[] cells : world.getMap().getCells())
            for (Cell cell : cells) {
                if (!(cell.isWall()) && world.manhattanDistance(cell, enemyCell) <= 7 && world.manhattanDistance(cell, enemyCell) <= 5) // FIXME: 2/21/2019 no name found to create a check method :(
                    attackCells.add(cell);
            } // FIXME: 2/21/2019 also make it a method for multi-use if it's possible
        return attackCells;
    }

    // get nearest cell in attack range of sentry of an enemy
    private Cell getNearestAttackCell(Hero sentry) {
        ArrayList<Cell> attackCells = getAttackPositionCells(sentry);
        // check which attack cell is nearest to sentry hero
        Cell nearestAttackCell = attackCells.get(0); // FIXME: 2/21/2019
        for (Cell attackCell : attackCells) {
            nearestAttackCell = getNearerCellFromHero(sentry, nearestAttackCell, attackCell);
        }
        return nearestAttackCell;
    }

    // final move to attack position method
    private boolean moveToAttackPosition(Hero sentry) {
        try {
            Cell attackCell = getNearestAttackCell(sentry);
            world.moveHero(sentry, world.getPathMoveDirections(sentry.getCurrentCell(), attackCell)[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /***************************************normal attack ability "methods"********************************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final normal attack method
    private boolean sentryAttack(Hero sentry) {
        try {
            Cell enemyCell;
            if (getBestTargetCell() != null) enemyCell = getBestTargetCell();
            else enemyCell = getNearestEnemyHero(sentry);
            // check if target is in attack range then crush 'em all :P
            if (isInAttackRange(sentry, enemyCell)) {
                world.castAbility(sentry, AbilityName.SENTRY_ATTACK, enemyCell);
                return true; // if target is in range
            } else return false; // if target is not in range
        } catch (Exception e) {
            return false;
        }
    }

    /*******************************special sentry offensive ability 'RAY' "method"s***********************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final special offensive ability method "ray"
    private boolean sentryCastRay(Hero sentry) {
        try {
            Cell enemyCell;
            if (getBestTargetCell() != null) enemyCell = getBestTargetCell();
            else enemyCell = getNearestEnemyHero(sentry);
            Ability sentryRay = sentry.getAbility(AbilityName.SENTRY_RAY);
            // check if ray ability is not on cooldown then poof 'em all :P
            if (isReady(sentryRay)) {
                world.castAbility(sentry, AbilityName.SENTRY_RAY, enemyCell);
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    /******************************offensive/defensive sentry ability 'dodge' "method"*********************************/ // TODO: 2/21/2019 functionality improvement is needed
    // final offensive/defensive ability method "dodge"
    private boolean sentryDodge(Hero sentry) {
        try {
            if (defensiveDodge(sentry)) System.out.print("defensive: ");
            else if (offensiveDodge(sentry)) System.out.print("offensive: ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // defensive use of ability method "dodge"
    private boolean defensiveDodge(Hero sentry) {
        if (nearestEnemyDistance(sentry) <= 4) {
            world.castAbility(sentry, AbilityName.SENTRY_DODGE, getNearestResZone(sentry));
            return true;
        }
        return false;
    }

    // offensive use of ability method "dodge'
    private boolean offensiveDodge(Hero sentry) {
        Cell attackCell = getNearestAttackCell(sentry);
        world.castAbility(sentry, AbilityName.SENTRY_DODGE, attackCell);
        return true;
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

    // check an ability remaining cooldown then return false if it's ready or true if it's not ready
    private boolean isReady(Ability ability) {
        return ability.getRemCooldown() == 0;
    }

    // get manhattan distance of sentry and nearest enemy hero
    private int nearestEnemyDistance(Hero sentry) {
        return world.manhattanDistance(sentry.getCurrentCell(), getNearestEnemyHero(sentry));
    }

    // get nearest ally respawn zone cell in map
    private Cell getNearestResZone(Hero sentry) {
        Map map = world.getMap();
        Cell[] resZoneCells = map.getMyRespawnZone();
        Cell nearestCell = resZoneCells[0]; // FIXME: 2/23/2019
        for (Cell cell : resZoneCells) {
            nearestCell = getNearerCellFromHero(sentry, nearestCell, cell);
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