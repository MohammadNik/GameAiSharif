package client.Helper;
import client.model.AbilityName;
import client.model.Hero;
import client.model.World;

import java.util.Comparator;

public class GuardianManager implements HeroManager {
    private World world;


    @Override
    public void preProcess(World world) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!


    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
    }

    private void moveToObjectiveZone(Hero Gurdian) {
        world.moveHero(Gurdian,world.getPathMoveDirections(Gurdian.getCurrentCell(),Helper.nearestCellFromOZ(world,Gurdian.getCurrentCell()))[0]);
    }
    public void moveInObjectiveZone(){
        if(world.getAP()>(world.getCurrentTurn()-1)*14){

        }

    }
    public void action(Hero Guardian) {
        //Guardian_Fortify
        if(Helper.getEnemiesInRange(world,Guardian,4).isEmpty()){

           Hero target = Helper.getAllyInRange(world,Guardian,4)
                    .stream()
                    .sorted(Comparator.comparing(Hero::getCurrentHP))
                    .findFirst().get();

           double hpExceed = 25.0/100;

           if (target.getCurrentHP() <= hpExceed*target.getMaxHP()){
               world.castAbility(Guardian, AbilityName.GUARDIAN_FORTIFY,target.getCurrentCell());

           }


        }
        //Guardian_Dodge
        if(!(Helper.cellInRangeOfSpot(world,Guardian.getCurrentCell(),4).isEmpty())){
            world.castAbility(Guardian,AbilityName.GUARDIAN_DODGE,Guardian.getCurrentCell());
        }
        //Guardian_Attac
        if(!(Helper.getEnemiesInRange(world,Guardian,2).isEmpty()){
            world.castAbility(Guardian,AbilityName.GUARDIAN_ATTACK,);
        }
    }
}

