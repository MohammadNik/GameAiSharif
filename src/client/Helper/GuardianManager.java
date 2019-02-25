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
    private Hero Guardian;


    @Override
    public void preProcess(World world) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

        currentHero=Guardian;
        if(!(Guardian.getCurrentCell().isInObjectiveZone())){
            moveToObjectiveZone(Guardian);
        }
        else{
            moveInObjectiveZone(Guardian);
        }

    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        action(Guardian);
    }

    private void moveToObjectiveZone(Hero Gurdian) {
        world.moveHero(Gurdian,world.getPathMoveDirections(Gurdian.getCurrentCell(),Helper.nearestCellFromOZ(world,Gurdian.getCurrentCell()))[0]);
    }
    private void moveInObjectiveZone(Hero Guardian){
        if(world.getAP()>(world.getCurrentTurn()-1)*14+100){
            if(nearestEnemyInOb(Guardian) != null){
                world.moveHero(Guardian,world.getPathMoveDirections(Guardian.getCurrentCell(),nearestEnemyInOb(Guardian).getCurrentCell())[0]);
            }

        }

    }
    private Hero nearestEnemyInOb(Hero Guardian ){
        Hero nearestEnemy=null;
        int min=30;
        List<Hero> enemies=new ArrayList<>();
        enemies=Helper.getEnemiesInObjectiveZone(world);
        for(Hero En : enemies){
            if(world.manhattanDistance(Guardian.getCurrentCell(),En.getCurrentCell())<min){
                nearestEnemy=En;
                min=world.manhattanDistance(Guardian.getCurrentCell(),En.getCurrentCell());
            }
        }
        return nearestEnemy;

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
        int max=0,x,y,temp=0;
        Hero theBest = null;
        List<Hero> enemies = new ArrayList<>();
        enemies = Helper.getEnemiesInRange(world, Guardian, 1);
        for(Hero enemy: enemies){
           temp= Helper.getEnemiesInRange(world, enemy, 1).size();
           if(temp>max) {
               theBest = enemy;
               max=temp;
           }
        }
        if(theBest != null)
            return theBest.getCurrentCell();
        for(Hero enemy: Helper.getEnemiesInRange(world,Guardian,2) ) {
            x = (enemy.getCurrentCell().getRow() + Guardian.getCurrentCell().getRow()) / 2;
            y = (enemy.getCurrentCell().getColumn() + Guardian.getCurrentCell().getColumn()) / 2;
            return world.getMap().getCell(x, y);
        }
        return null;
    }
}

