package client;

import client.Helper.BlasterManager;
import client.Helper.GuardianManager;
import client.Helper.HealerManager;
import client.Helper.SentryManager;
import client.model.Hero;
import client.model.HeroName;
import client.model.World;

public class AI
{

    private  int index = 0;
    private HeroName[] heroConstants = {HeroName.BLASTER,HeroName.BLASTER,HeroName.BLASTER,HeroName.BLASTER};
    private HealerManager healerManager;
    private GuardianManager guardianManager;
    private SentryManager sentryManager;
    private BlasterManager blasterManager;

    public void preProcess(World world)
    {
        System.out.println("pre process started");
        healerManager = new HealerManager();
        guardianManager = new GuardianManager();
        sentryManager = new SentryManager();
        blasterManager = new BlasterManager();

        healerManager.preProcess(world);
        guardianManager.preProcess(world);
        sentryManager.preProcess(world);
        blasterManager.preProcess(world);
    }

    public void pickTurn(World world) {
        System.out.println("pick started");

        world.pickHero(heroConstants[index++]);
    }

    public void moveTurn(World world) {
        System.out.println("move started");
        Hero[] heroes = world.getMyHeroes();

        for (Hero hero : heroes)
        {
           switch (hero.getName()){
               case HEALER:
                   healerManager.move(world,hero);
                   break;
               case SENTRY:
                   sentryManager.move(world,hero);
                   break;
               case GUARDIAN:
                   guardianManager.move(world,hero);
                   break;
               case BLASTER:
                   blasterManager.move(world,hero);
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
                    healerManager.takeAction(world,hero);
                    break;
                case SENTRY:
                    sentryManager.takeAction(world,hero);
                    break;
                case GUARDIAN:
                    guardianManager.takeAction(world,hero);
                    break;
                case BLASTER:
                    blasterManager.takeAction(world,hero);
                    break;
            }

        }
    }

}
