package client.Helper;

import client.model.Hero;
import client.model.World;

public class GuardianManager implements HeroManager {
    private World world;


    public GuardianManager(World world) {
        this.world = world;
    }

    @Override
    public void preProcess() {

    }

    @Override
    public void move(Hero currentHero) {

    }

    @Override
    public void takeAction(Hero currentHero) {

    }
}
