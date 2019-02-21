package client.Helper;

import client.model.Hero;

public interface HeroManager {

    public void preProcess();

    public void move();

    public void takeAction(Hero currentHero);
}
