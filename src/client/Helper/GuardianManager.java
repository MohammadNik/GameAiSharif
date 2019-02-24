package client.Helper;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private void moveInObjectiveZone(){
        if(world.getAP()>(world.getCurrentTurn()-1)*14){

        }

    }
    private void action(Hero Guardian) {
        //Guardian_Fortify


           Hero target = Helper.getAllyInRange(world,Guardian,4)
                    .stream()
                    .sorted(Comparator.comparing(Hero::getCurrentHP))
                    .findFirst().get();

           double hpExceed = 25.0/100;

           if (target.getCurrentHP() <= hpExceed*target.getMaxHP()){
               world.castAbility(Guardian, AbilityName.GUARDIAN_FORTIFY,target.getCurrentCell());
           }

        //Guardian_Dodge
        if(!(Helper.cellInRangeOfSpot(world,Guardian.getCurrentCell(),4).isEmpty())){
            world.castAbility(Guardian,AbilityName.GUARDIAN_DODGE,Guardian.getCurrentCell());
        }
        //Guardian_Attac
        if(Attac(Guardian) != null){
            world.castAbility(Guardian,AbilityName.GUARDIAN_ATTACK,Attac(Guardian));
        }

    }
    private Cell Attac(Hero Guardian) {
        int max=0;
        Hero theBest = null;
        int temp=0;
        List<Hero> enemies = new ArrayList<>();
        enemies = Helper.getEnemiesInRange(world, Guardian, 1);
        for(Hero enemy: enemies){
           temp= Helper.getEnemiesInRange(world, enemy, 1).size();
           if(temp>max)
               theBest=enemy;
        }
        if(theBest != null)
            return theBest.getCurrentCell();
        for(Hero enemy: Helper.getEnemiesInRange(world,Guardian,2) ) {
            return enemy.getCurrentCell();
        }
        return null;
    }
}

