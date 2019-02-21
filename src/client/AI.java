package client;

import client.Helper.*;
import client.model.*;

import java.util.Random;

public class AI
{

    private Random random = new Random();
    private  int index = 0;
    private HeroName[] heroConstants = {HeroName.GUARDIAN,HeroName.BLASTER,HeroName.HEALER,HeroName.SENTRY};
    private HealerManager healerManager;
    private GuardianManager guardianManager;
    private SentryManager sentryManager;
    private BlasterManager blasterManager;

    public void preProcess(World world)
    {
        System.out.println("pre process started");
        healerManager = new HealerManager(world);
        guardianManager = new GuardianManager(world);
        sentryManager = new SentryManager(world);
        blasterManager = new BlasterManager(world);

        healerManager.preProcess();
        guardianManager.preProcess();
        sentryManager.preProcess();
        blasterManager.preProcess();
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
           switch (hero.getName()){
               case HEALER:
                   healerManager.move(hero);
                   break;
               case SENTRY:
                   sentryManager.move(hero);
                   break;
               case GUARDIAN:
                   guardianManager.move(hero);
                   break;
               case BLASTER:
                   blasterManager.move(hero);
                   break;
           }
        }
    }



    public void actionTurn(World world) {
        System.out.println("action started");
        Hero[] heroes = world.getMyHeroes();
        for (Hero hero : heroes) {

            switch (hero.getName()){
                case HEALER:
                    healerManager.takeAction(hero);
                    break;
                case SENTRY:
                    sentryManager.takeAction(hero);
                    break;
                case GUARDIAN:
                    guardianManager.takeAction(hero);
                    break;
                case BLASTER:
                    blasterManager.takeAction(hero);
                    break;
            }

        }
    }

}
