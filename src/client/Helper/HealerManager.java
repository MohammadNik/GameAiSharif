package client.Helper;

import client.model.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Names:
 *      HP-Name-Convention:
 *          1- Z:Zero:{@link #HP_Z}
 *          2- L:Low:{@link #HP_L}
 *          3- M:Medium:{@link #HP_M}
 *          4- H:High:{@link #HP_H}
 *          5- F:Full:{@link #HP_F}
 *
 *      Hero-Convention:
 *          1- MY:Healer Hero
 *          2- OT:Others Hero*/
public class HealerManager implements HeroManager {

    public static final double HP_Z = 0;
    public static final double HP_L = 45.0/100;
    public static final double HP_M = 75.0/100;
    public static final double HP_H = 90.0/100;
    public static final double HP_F = 100;

    public static final int RANGE = 4;

    private static World world;
    private Hero healerHero;


    @Override
    public void preProcess(World world) {
        HealerManager.world = world; // WARNING: DON'T CHANGE THIS !!
    }

    @Override
    public void move(World world,Hero healerHero) {
        HealerManager.world = world; // WARNING: DON'T CHANGE THIS !!
        this.healerHero = healerHero;

        if (isAllHpInRange(HP_H,HP_F)){

            if (!getHeroCell().isInObjectiveZone()){
                moveToObjectiveZone();
            }else {
                // we are in objective zone
                handleEnemyInObjectiveZone();
            }
        }else {
            if (isLowestHpForMY()){
                if (isHpInRange(healerHero,HP_Z,HP_M)){ moveToNearestSafeCell(); }
            }else{
                if (isHpInRange(getLowestHpHero(),HP_Z,HP_M)) { moveToLowestHpHero(); }
            }
        }

    }

    @Override
    public void takeAction(World world,Hero healerHero) {
        HealerManager.world = world; // WARNING: DON'T CHANGE THIS !!
        this.healerHero = healerHero;


        if (isAllHpInRange(HP_F,HP_F)){
            takeActionDamageEnemy();
        }else {

            if (isHealReady()){
                if (isLowestHpForMY()) takeActionHealMY();
                else takeActionHealOT(getLowestHpHero());
            }else {
                takeActionDamageEnemy();
            }

        }

    }

    private void takeActionHealMY(){
        world.castAbility(healerHero,AbilityName.HEALER_HEAL,healerHero.getCurrentCell());
        System.out.println("Heal MY");
    }

    private void takeActionHealOT(Hero OTHero){
        if ( Helper.isInRangeOfCell1(OTHero.getCurrentCell(),healerHero.getCurrentCell(),RANGE) )
        world.castAbility(healerHero,AbilityName.HEALER_HEAL,OTHero.getCurrentCell());
        System.out.println(String.format("Heal %d name %s",OTHero.getId(), OTHero.getName()));
    }

    private void takeActionDamageEnemy(){
        Hero enemyInRange = getEnemyInRange();

        if (enemyInRange == null || !isAttackReady()) return;

            if ( Helper.isInRangeOfCell1(enemyInRange.getCurrentCell(),healerHero.getCurrentCell(), RANGE) )
                world.castAbility(healerHero, AbilityName.HEALER_ATTACK, enemyInRange.getCurrentCell());
            System.out.println("Damage enemy");

    }

    private void handleEnemyInObjectiveZone(){
        List<Hero> enemies = Helper.getEnemiesInObjectiveZone(world);

        if (enemies.isEmpty()) return;

        Cell nearestEnemy = enemies.parallelStream()
                .map(Hero::getCurrentCell)
                .reduce(Helper.getNearestCellReduce(healerHero.getCurrentCell())).orElse(null);

        if ( nearestEnemy == null || Helper.isInRangeOfCell1(healerHero.getCurrentCell(),nearestEnemy,RANGE)) return;

        Cell farCellFromEnemy = Helper.cellInRangeOfSpot(world,nearestEnemy,RANGE)
                .stream()
                .filter(cell -> !cell.isWall())
                .filter(cell -> world.getMyHero(cell) == null)
                .reduce(Helper.getNearestCellReduce(healerHero.getCurrentCell())).orElse(null);

        if (farCellFromEnemy == null) return;

        moveHealerTo(farCellFromEnemy);
    }
    // ~~
    private void moveToLowestHpHero(){
         Cell cell =Helper.cellInRangeOfSpot(world,getCellOfLowestHpHero(),4)
                 .stream()
                 .filter(cell1 -> world.getMyHero(cell1) == null)
                 .reduce(Helper.getNearestCellReduce(healerHero.getCurrentCell())).orElse(null);

         moveHealerTo(cell);

        System.out.println("Move to lowest hp hero");

    }
    // ~~
    private void moveToNearestSafeCell(){
        try {
            moveHealerTo(MapManager.findNearestHidingCell(healerHero.getCurrentCell(),RANGE,world));

        }catch (NullPointerException ignored){}

        System.out.println("moveToNearestSafeCell");


    }
    // ~~
    private void moveToObjectiveZone(){
        moveHealerTo( Helper.nearestCellFromOZ(world,healerHero.getCurrentCell()) );
    }

    private void moveHealerTo(Cell target){
        for (Direction dir: world.getPathMoveDirections(healerHero.getCurrentCell(),target))
            world.moveHero(healerHero,dir);
    }


    private boolean isLowestHpForMY(){
        return getCellOfLowestHpHero() == healerHero.getCurrentCell();
    }

    private Cell getCellOfLowestHpHero(){
        return getLowestHpHero().getCurrentCell();
    }

    public Hero getLowestHpHero(){
        return Arrays.stream(world.getMyHeroes()).min(Comparator.comparing(Hero::getCurrentHP)).orElse(null);
    }

    private boolean isHealReady(){
        return healerHero.getAbility(AbilityName.HEALER_HEAL).isReady();
    }

    private boolean isAttackReady(){
        return healerHero.getAbility(AbilityName.HEALER_ATTACK).isReady();
    }


    // return nearest Enemy
    private Hero getEnemyInRange(){
        List<Hero> heroes = Helper.getEnemiesInRange(world,healerHero,RANGE);

        if (heroes.isEmpty()) return null;

        return heroes.get(0);
    }

    private boolean isAllHpInRange(double startOfRange, double endOfRange){
        for (Hero hero : world.getMyHeroes()){
            if (!isHpInRange(hero,startOfRange,endOfRange)) return false;
        }

        return true;
    }

    public static boolean isHpInRange(Hero hero, double startOfRange, double endOfRange){
        boolean startCondition = HCH(hero) >= HMH(hero)*startOfRange;

        // dont include zero in range
        if (startOfRange == HP_Z) startCondition = HCH(hero) > HMH(hero)*startOfRange;

        return startCondition && HCH(hero) <= HMH(hero)*endOfRange;
    }
    // ~~
    // hero max hp
    private static int HMH(Hero hero){
        return hero.getMaxHP();
    }
    // ~~
    // hero current hp
    private static int HCH(Hero hero){
        return hero.getCurrentHP();
    }

    private Cell getHeroCell() {
        return healerHero.getCurrentCell();
    }
}
