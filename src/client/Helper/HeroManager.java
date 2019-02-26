package client.Helper;

import client.model.Hero;
import client.model.World;

public interface HeroManager {

    public void preProcess(World world);

    public void move(World world, Hero currentHero);

    public void takeAction(World world, Hero currentHero);


}
