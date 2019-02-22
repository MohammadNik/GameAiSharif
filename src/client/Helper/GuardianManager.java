package client.Helper;

import client.model.Hero;
import client.model.World;

public class GuardianManager implements HeroManager {
    private World world;


    @Override
    public void preProcess(World world) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
    }

    @Override
    public void move(World world,Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
    }

    @Override
    public void takeAction(World world,Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
    }
}
