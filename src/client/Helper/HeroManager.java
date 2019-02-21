package client.Helper;

import client.model.Hero;

public interface HeroManager {

    public void preProcess();

    public void move(Hero currentHero);

    public void takeAction(Hero currentHero);
}
