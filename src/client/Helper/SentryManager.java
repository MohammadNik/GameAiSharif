package client.Helper;

import client.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SentryManager implements HeroManager {
    private World world;
    private Hero sentry;

    @Override
    public void preProcess(World world) {
    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world;
        this.sentry = currentHero;
        if (stayInPosition()) System.out.println("sentry stay");
        else if (moveToAttackPosition()) System.out.println("sentry attack move");
        else if (moveToObjectiveZone()) System.out.println("sentry objective move");
    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world;
        this.sentry = currentHero;
        if (sentryRay()) System.out.println("sentry ray");
        else if (sentryAttack()) System.out.println("sentry attack");
        else if (sentryDodge()) System.out.println("sentry dodge");
    }

    /*************************************move sentry to objective zone "methods"**************************************/
    private boolean moveToObjectiveZone() {
        try {
            if (sentry.getCurrentCell().isInObjectiveZone()) return false;
            Cell nearestCellFromObjectiveZone = Helper.nearestCellFromOZ(world, sentry.getCurrentCell());
            Direction[] directions = world.getPathMoveDirections(sentry.getCurrentCell(), nearestCellFromObjectiveZone);
            for (Direction direction : directions) world.moveHero(sentry, direction);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean stayInPosition() {
        return (getBestTargetRay() != null) && isReady(sentry.getAbility(AbilityName.SENTRY_RAY));
    }
    /************************************move sentry to attack position "methods"**************************************/
    private ArrayList<Cell> getVisibleEnemyHeroesCells() {
        return Arrays.stream(world.getMap().getCells()).flatMap(Arrays::stream)
                .filter(cell -> world.getOppHero(cell) != null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Cell getNearestEnemyHero() {
        ArrayList<Cell> enemyCells = getVisibleEnemyHeroesCells();
        Cell nearestEnemyCell = enemyCells.stream().reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
        if (isInAttackRange(nearestEnemyCell)) return null;
        return nearestEnemyCell;
    }

    private ArrayList<Cell> getAttackPositionCells() {
        Cell enemyCell = getNearestEnemyHero();
        return Arrays.stream(world.getMap().getCells()).flatMap(Arrays::stream)
                .filter(cell -> !(cell.isWall()) && world.manhattanDistance(cell, enemyCell) == 7)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Cell getNearestAttackCell() {
        return getAttackPositionCells().stream().reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }

    private boolean moveToAttackPosition() {
        try {
            if (sentry.getCurrentCell().isInObjectiveZone()) return false;
            Cell attackCell = getNearestAttackCell();
            world.moveHero(sentry, world.getPathMoveDirections(sentry.getCurrentCell(), attackCell)[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /***************************************normal attack ability "methods"********************************************/
    private boolean sentryAttack() {
        try {
            world.castAbility(sentry, AbilityName.SENTRY_ATTACK, getBestTargetAttack());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Cell getBestTargetAttack() {
        return Arrays.stream(world.getMap().getCells()).flatMap(Arrays::stream)
                .filter(cell -> world.getOppHero(cell) != null)
                .filter(cell -> world.isInVision(sentry.getCurrentCell(), cell))
                .filter(cell -> world.manhattanDistance(sentry.getCurrentCell(), cell) <= 7)
                .reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }

    /*******************************special sentry offensive ability 'RAY' "method"s***********************************/
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
        return Arrays.stream(world.getMap().getCells()).flatMap(Arrays::stream)
                .filter(cell -> world.getOppHero(cell) != null)
                .filter(cell -> world.isInVision(sentry.getCurrentCell(), cell))
                .reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }

    /******************************offensive/defensive sentry ability 'dodge' "method"*********************************/
    private boolean sentryDodge() {
        try {
            if (isReady(sentry.getAbility(AbilityName.SENTRY_DODGE))) {
                if (defensiveDodge()) System.out.print("defensive: ");
                else if (offensiveDodge()) System.out.print("offensive: ");
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean defensiveDodge() {
        if (nearestEnemyDistance() <= 4) {
            world.castAbility(sentry, AbilityName.SENTRY_DODGE, getNearestResZone());
            return true;
        }
        return false;
    }

    private boolean offensiveDodge() {
        if (sentry.getCurrentCell().isInObjectiveZone()) return false;
        world.castAbility(sentry, AbilityName.SENTRY_DODGE, getNearestAttackCell());
        return true;
    }

    /**********************************************misc "methods*******************************************************/
    private boolean isInAttackRange(Cell targetCell) {
        return world.manhattanDistance(sentry.getCurrentCell(), targetCell) <= 7;
    }

    private boolean isReady(Ability ability) {
        return ability.getRemCooldown() == 0;
    }

    private int nearestEnemyDistance() {
        return world.manhattanDistance(sentry.getCurrentCell(), getNearestEnemyHero());
    }

    private Cell getNearestResZone() {
        Cell[] resZoneCells = world.getMap().getMyRespawnZone();
        return Arrays.stream(resZoneCells).reduce(Helper.getNearestCellReduce(sentry.getCurrentCell())).orElse(null);
    }
}