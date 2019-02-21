package client;

import client.Helper.HealerManager;
import client.Helper.Helper;
import client.model.*;

import java.util.Random;

public class AI
{

    private Random random = new Random();
    private  int index = 0;
    private HeroName[] heroConstants = {HeroName.GUARDIAN,HeroName.GUARDIAN,HeroName.HEALER,HeroName.SENTRY};
    private HealerManager healerManager;

    public void preProcess(World world)
    {
        System.out.println("pre process started");
        healerManager = new HealerManager(world);
    }

    public void pickTurn(World world)
    {
        System.out.println("pick started");

        world.pickHero(heroConstants[index++]);
    }

    public void moveTurn(World world)
    {
        System.out.println("move started");
        Hero[] heroes = world.getMyHeroes();

        for (Hero hero : heroes)
        {
            Cell current = hero.getCurrentCell();
            for (Direction dir : world.getPathMoveDirections(current,Helper.nearestCellFromOZ(world,current)))
                world.moveHero(hero,dir);

        }
    }



    public void actionTurn(World world) {
        System.out.println("action started");
        Hero[] heroes = world.getMyHeroes();
        Map map = world.getMap();
        for (Hero hero : heroes) {

            if (hero.getName() == HeroName.HEALER)
                healerManager.takeAction(hero);
            else {
                int row = random.nextInt(map.getRowNum());
                int column = random.nextInt(map.getColumnNum());

                world.castAbility(hero, hero.getAbilities()[random.nextInt(3)], row, column);

            }
        }
    }

}
