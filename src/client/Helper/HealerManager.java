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

    private World world;
    private Hero healerHero;


    @Override
    public void preProcess(World world) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!


    }

    @Override
    public void move(World world,Hero healerHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.healerHero = healerHero;

        if (isAllHpInRange(HP_H,HP_F)){

//            if (getEnemyInRange() == null && !healerHero.getCurrentCell().isInObjectiveZone()) moveToObjectiveZone();
            if (!healerHero.getCurrentCell().isInObjectiveZone()) moveToObjectiveZone();

        }else {

            if (isLowestHpForMY()) moveToNearestSafeCell();
            else moveToLowestHpHero();

        }

    }

    @Override
    public void takeAction(World world,Hero healerHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.healerHero = healerHero;

        if (isAllHpInRange(HP_H,HP_F)){

            if (getEnemyInRange() != null) takeActionDamageEnemy();

        }else {

            if (isLowestHpForMY()) takeActionHealMY();
            else takeActionHealOT(getLowestHpHero());

        }
    }

    private void takeActionHealMY(){
        world.castAbility(healerHero,AbilityName.HEALER_HEAL,healerHero.getCurrentCell());
    }

    private void takeActionHealOT(Hero OTHero){
        world.castAbility(healerHero,AbilityName.HEALER_HEAL,OTHero.getCurrentCell());
    }

    private void takeActionDamageEnemy(){
        world.castAbility(healerHero, AbilityName.HEALER_ATTACK, getEnemyInRange().getCurrentCell());
    }


    private void moveToLowestHpHero(){
        // TODO: 2019-02-23 complete me
        Cell hCell = healerHero.getCurrentCell();
         Cell cell =Helper.cellInRangeOfSpot(world,getCellOfLowestHpHero(),4)
                 .stream()
                 .reduce( (result,eachCell)-> {
                     if ( Helper.distanceCalculator(hCell,eachCell) <= Helper.distanceCalculator(hCell,result) ) return eachCell;
                     else return result;
                 }).orElse(null);

         moveHealerTo(cell);

    }
    // ~~
    private void moveToNearestSafeCell(){
        // TODO: 2019-02-23 complete me
        Cell ECell = getEnemyInRange().getCurrentCell();

        if (ECell == null)
            return;

        moveHealerTo(MapManager.findHidingCell(world,healerHero.getCurrentCell(),ECell));

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
        return Arrays.stream(world.getMyHeroes()).sorted(Comparator.comparing(Hero::getCurrentHP)).findFirst().get();
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

    private boolean isHpInRange(Hero hero, double startOfRange, double endOfRange){
        boolean startCondition = HCH(hero) >= HMH(hero)*startOfRange;

        // dont include zero in range
        if (startOfRange == HP_Z) startCondition = HCH(hero) > HMH(hero)*startOfRange;

        return startCondition && HCH(hero) <= HMH(hero)*endOfRange;
    }
    // ~~
    // hero max hp
    private int HMH(Hero hero){
        return hero.getMaxHP();
    }
    // ~~
    // hero current hp
    private int HCH(Hero hero){
        return hero.getCurrentHP();
    }
}
