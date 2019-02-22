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

            if (isAnyEnemyInRange() == null) moveToObjectiveZone();
        }else {

            if (isLowestHpForMY(healerHero.getCurrentCell())) moveToNearestWall();
            else moveToLowestHpHero(getCellOfLowestHpHero());

        }

    }

    @Override
    public void takeAction(World world,Hero healerHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.healerHero = healerHero;

        if (isAllHpInRange(HP_H,HP_F)){

            if (isAnyEnemyInRange() != null) takeActionDamageEnemy();

        }else {

            if (isLowestHpForMY(healerHero.getCurrentCell())) takeActionHealMY();
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
        world.castAbility(healerHero, AbilityName.HEALER_ATTACK,isAnyEnemyInRange().getCurrentCell());
    }



    private void moveToLowestHpHero(Cell targetCell){
        // TODO: 2019-02-23 complete me
    }
    // ~~
    private void moveToNearestWall(){
        // TODO: 2019-02-23 complete me

    }
    // ~~
    private void moveToObjectiveZone(){
        for (Direction dir: world.getPathMoveDirections(healerHero.getCurrentCell(),Helper.nearestCellFromOZ(world,healerHero.getCurrentCell())))
            world.moveHero(healerHero,dir);
    }


    private boolean isLowestHpForMY(Cell MYCell){
        return getCellOfLowestHpHero() == MYCell;
    }

    private Cell getCellOfLowestHpHero(){
        return getLowestHpHero().getCurrentCell();
    }

    private Hero getLowestHpHero(){
        return Arrays.stream(world.getMyHeroes()).sorted(Comparator.comparing(Hero::getCurrentHP)).findFirst().get();
    }

    // return nearest Enemy
    private Hero isAnyEnemyInRange(){
        List<Cell> cellsInRange = Helper.cellInRangeOfSpot(world,healerHero.getCurrentCell(),RANGE);

        Cell cellOfOpp = cellsInRange.stream()
                .filter(cell -> world.getOppHero(cell) != null && world.getMyHero(cell) == null)
                .findFirst().orElse(null);

        return world.getOppHero(cellOfOpp);
    }

    private boolean isAllHpInRange(double startOfRange, double endOfRange){
        for (Hero hero : world.getMyHeroes()){
            if (!isHpInRange(hero,startOfRange,endOfRange)) return false;
        }

        return true;
    }

    private boolean isHpInRange(Hero hero, double startOfRange, double endOfRange){
        return HCH(hero) >= HMH(hero)*startOfRange && HCH(hero) <= HMH(hero)*endOfRange;
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
