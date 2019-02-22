package client.Helper;

import client.model.*;

import java.util.Arrays;
import java.util.Comparator;

/**
 *  Strategy
 *   1- if all the heroes hp where higher than a value(dependant to each hero) healer use it's ability to attack if anyone was near to it
 *   2- else start to heal
 *      2.1- if first lowest hero was itself(the healer) then it heal itself and go away if any enemy was near
 *      2.2- else go to nearest cell that can heal first lowest hero heal
 *          2.2.1- if heal was in LOW_STATE use blink
 *          2.2.1- else move to hero normally
 *   note: blink only used for healing
 *   */

public class HealerManager implements HeroManager {
    public static final int HEALTH = 200;
    public static final int HEAL_POWER = 30;
    public static final int DAMAGE_POWER = 25;
    public static final int MAX_RANGE = 4;



    public static final double HP_GOOD_PERCENT = 90.0/100;
    public static final double HP_MEDIUM_PERCENT = 75.0/100;
    public static final double HP_LOW_PERCENT = 45.0/100;

    public static final int ENEMY_MAX_RANGE = 4;


    private World world;

    @Override
    public void preProcess(World world) {
        this.world = world;
    }

    @Override
    public void move(World world,Hero currentHero) {
        this.world = world;
    }

    @Override
    public void takeAction(World world,Hero currentHero) {
        this.world = world;
         // currentHero is healer one of the healer heroes
        if (isAllHpsGoodToFull()) allHpsOkayAction(currentHero);
        else if (isHeroHpMediumToGood(currentHero)) healForMyselfAction(currentHero);
        else healAction(currentHero);

    }

    private void allHpsOkayAction(Hero healerHero){
        // TODO: 2019-02-21 TAKE ACTION TO ATTACK ENEMY IF ANYONE WAS IN A RANGE
        if (getEnemyCellInRange() != null){
            //  damage enemy
            world.castAbility(healerHero,AbilityName.HEALER_ATTACK,getEnemyCellInRange());
        }else {
            // move to objective zone
            moveToObjectiveZone(healerHero);
        }
    }

    public void healForMyselfAction(Hero healerHero){
        // TODO: 2019-02-21 move to nearest cell that can heal low hp hero and heal itself(mystic the healer)

        healThisHero(healerHero,healerHero);
        if (getEnemyCellInRange() != null){
            // move to nearest low hp hero

        }else {
            // move to objective zone
           moveToObjectiveZone(healerHero);
        }

    }

    private void healAction(Hero healerHero){
        Hero[] heroes = getHeroesByHealDescExceptHealer();
        // healing others
        // TODO: 2019-02-20 Move to a cell which can heal the hero
        Hero targetHero = heroes[0];
        healThisHero(healerHero,targetHero);
    }

    private void moveToObjectiveZone(Hero healerHero){
        for (Direction dir :
                world.getPathMoveDirections(healerHero.getCurrentCell(),Helper.nearestCellFromOZ(world,healerHero.getCurrentCell()))){
            world.moveHero(healerHero,dir);
        }
    }

    private void healThisHero(Hero healerHero, Hero hero){
        world.castAbility(healerHero, AbilityName.HEALER_HEAL,hero.getCurrentCell());
    }

    public boolean isAllHpsGoodToFull(){
        for (Hero hero : world.getMyHeroes()){
            if (!isHeroHpMediumToGood(hero)) return false;
        }

        return true;
    }
    // ~~
    public boolean isHeroHpGoodToFull(Hero hero){
        return hero.getCurrentHP() >= hero.getMaxHP()*HP_GOOD_PERCENT;
    }
    // ~~
    public boolean isAllHpsMediumToGood(){
        for (Hero hero : world.getMyHeroes()){
            if (!isHeroHpMediumToGood(hero)) return false;
        }

            return true;
    }
    // ~~
    public boolean isHeroHpMediumToGood(Hero hero){
        return hero.getCurrentHP() >= (hero.getMaxHP()*HP_MEDIUM_PERCENT) && hero.getCurrentHP() < (hero.getMaxHP()*HP_GOOD_PERCENT);
    }
    // ~~
    public boolean isHeroHpLowToMedium(Hero hero){
        return hero.getCurrentHP() >= hero.getMaxHP()* HP_LOW_PERCENT && hero.getCurrentHP() < hero.getMaxHP()*HP_MEDIUM_PERCENT;
    }
    // ~~
    public Cell getEnemyCellInRange(){
        // TODO: 2019-02-21 complete this method later
        return null;
    }

    public Hero[] getHeroesByHealDescExceptHealer(){
        return Arrays.stream(getHeroesByHealDesc()).filter(hero -> hero.getName() != HeroName.HEALER).toArray(size -> new Hero[3]);
    }

    public Hero[] getHeroesByHealDesc(){
        return Arrays.stream(world.getMyHeroes())
                .sorted(Comparator.comparing(Hero::getCurrentHP).reversed())
                .toArray(size-> new Hero[4]);
    }

//    public Hero findHealer(){
//        return Arrays.stream(world.getMyHeroes()).filter(hero -> hero.getName() == HeroName.HEALER).findFirst().orElse(null);
//    }
}
