package client.Helper;

import client.model.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuardianManager implements HeroManager {
    private World world;
    private Hero guardianHero;

    public static final int RANGE_ATTACK = 1;
    public static final int RANGE_FORTIFY = 4;

    @Override
    public void preProcess(World world) {
        this.world = world;


    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world;
        this.guardianHero = currentHero;


        if (!getCurrentCell().isInObjectiveZone()){
            moveToObjectiveZone();
        }else {
            Hero lowestHpHero = getLowestHpHero();
            if ( !Helper.isInRangeOfCell1(lowestHpHero.getCurrentCell(),guardianHero.getCurrentCell(),RANGE_FORTIFY) && HealerManager.isHpInRange(lowestHpHero,HealerManager.HP_Z,HealerManager.HP_L) && (lowestHpHero.getId() != guardianHero.getId()) ){
                moveToRangeOfLowestHpHero(lowestHpHero);
            }else {
                handleMovingInObjectiveZone();
            }

        }

    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world;
        this.guardianHero = currentHero;

        Hero lowestHpHero = getLowestHpHero();

        if (Helper.isInRangeOfCell1(guardianHero.getCurrentCell(),lowestHpHero.getCurrentCell(),RANGE_FORTIFY) && isFortifyReady()){
            takeActionFortify(lowestHpHero);
        }else{

            Hero enemyInRange = getEnemyInRangeOfAttack();

            if (enemyInRange == null) return;

            world.castAbility(guardianHero,AbilityName.GUARDIAN_ATTACK,enemyInRange.getCurrentCell());

        }

    }


    private void takeActionFortify(Hero lowestHpHero){
        world.castAbility(guardianHero,AbilityName.GUARDIAN_FORTIFY,lowestHpHero.getCurrentCell());
    }

    private boolean isFortifyReady(){
        return guardianHero.getAbility(AbilityName.GUARDIAN_FORTIFY).isReady();
    }

    private void handleMovingInObjectiveZone(){
        List<Hero> enemies = Helper.getEnemiesInObjectiveZone(world);

        if (enemies.isEmpty()) return;

        Cell nearestEnemyCell = enemies.parallelStream()
                .map(Hero::getCurrentCell)
                .reduce(Helper.getCellReduce(guardianHero.getCurrentCell())).orElse(null);

        if (nearestEnemyCell == null || Helper.isInRangeOfCell1(guardianHero.getCurrentCell(),nearestEnemyCell,RANGE_ATTACK)) return;


        Cell nearestCellToEnemy = Helper.cellInRangeOfSpot(world,nearestEnemyCell,RANGE_ATTACK)
                .parallelStream()
                .filter(cell -> world.getMyHero(cell) == null)
                .reduce(Helper.getCellReduce(guardianHero.getCurrentCell())).orElse(null);

        if (nearestCellToEnemy == null) return;

        moveTo(nearestCellToEnemy);

    }

    private Hero getEnemyInRangeOfAttack(){
        List<Hero> heroes = Helper.getEnemiesInRange(world,guardianHero,RANGE_ATTACK);

        if (heroes.isEmpty()) return null;

        return heroes.get(0);
    }

    private void moveToRangeOfLowestHpHero(Hero lowestHpHero){
        Cell target = Helper.cellInRangeOfSpot(world,lowestHpHero.getCurrentCell(),RANGE_FORTIFY)
                .parallelStream()
                .filter(cell -> world.getMyHero(cell) == null)
                .reduce(Helper.getCellReduce(guardianHero.getCurrentCell()))
                .orElse(null);

        if (target == null) return;

        moveTo(target);
    }

    private void moveToObjectiveZone(){
        moveTo(Helper.nearestCellFromOZ(world,guardianHero.getCurrentCell()));

    }

    private void moveTo(Cell target){
        for (Direction dir: world.getPathMoveDirections(guardianHero.getCurrentCell(),target))
            world.moveHero(guardianHero,dir);
    }

    private Cell getCurrentCell(){
        return guardianHero.getCurrentCell();
    }

    public Hero getLowestHpHero(){
        return Arrays.stream(world.getMyHeroes()).min(Comparator.comparing(Hero::getCurrentHP)).orElse(null);
    }
}
