package client.Helper;

import client.model.*;

public class BlasterManager implements HeroManager {
    private World world;


    @Override
    public void preProcess(World world) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

        //moveToObjectiveZone(currentHero);

        entrenchment(currentHero);

    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

        /***************************************OFFENCE*****************************************/


        /***************************************DODGE*******************************************/

    }

    private void moveToObjectiveZone(Hero blaster) {
        for (Direction dir :
                world.getPathMoveDirections(blaster.getCurrentCell(), Helper.nearestCellFromOZ(world, blaster.getCurrentCell()))) {
            world.moveHero(blaster, dir);
        }
    }

    private int distanceCalculator(Cell cell1, Cell cell2) {
        return Math.abs(cell1.getColumn() - cell2.getColumn()) + Math.abs(cell1.getRow() - cell2.getRow());

    }


    //returns the nearest wall cell from the nearest OZ
    private Cell nearestWalltoOZ() {

        int minWallDis = 31;
        Cell minWallDisCell = null;
        for (Cell[] cells : world.getMap().getCells()) {
            for (Cell cell : cells) {
                if (cell.isWall()) {
                    if (distanceCalculator(cell, Helper.nearestCellFromOZ(world, cell)) <= minWallDis)
                        minWallDis = distanceCalculator(cell, Helper.nearestCellFromOZ(world, cell));
                    minWallDisCell = cell;
                }
            }
        }

        return minWallDisCell;
    }

    //moves the blaster to the trench
    private void entrenchment(Hero blaster) { // FIXME: 2/22/2019 find more appropriate trench cells
        Cell trench = nearestWalltoOZ();
        world.moveHero(blaster, Helper.nearestToCell(world, trench)); // FIXME: 2/22/2019 move to the bottom row of trench, not itself

    }

    //returns an array of hero cells ( if the cell is in vision )
    public Cell[] opponentHeroCell(){

        Cell[] HeroCells = null;
        int i = 0;
        for (Cell[] cells : world.getMap().getCells()) {
            for (Cell cell : cells) {
                    if(world.getOppHero(cell).getName() == HeroName.SENTRY || world.getOppHero(cell).getName() == HeroName.BLASTER ||
                     world.getOppHero(cell).getName() == HeroName.GUARDIAN || world.getOppHero(cell).getName() == HeroName.HEALER) {
                        HeroCells[i] = cell;
                        i ++;
                    }
                }
            }

        return HeroCells;
    }


    //attacking if the opponent heroes are in our ideal position
    public void beneficialAttack(Cell[] heroCell){



    }


    //Bombing if the opponent heroes are in our ideal position
    public void beneficialBomb (Cell[] heroCell){

    }


    //attacking
    public void normalAttack(Hero blaster){

        Cell tempH = null;
        Cell tempB = null;
        Cell tempG = null;

        Cell[] heroCell = opponentHeroCell();
        for (int i = 0; i< heroCell.length; i++){
            if( distanceCalculator(heroCell[i], blaster.getCurrentCell()) <= 4){ //near enough to attack
                if(world.getOppHero(heroCell[i]).getCurrentHP() <= 20){ //fastest to be killed
                    switch (world.getOppHero(heroCell[i]).getName()){
                        case SENTRY:
                            world.castAbility(world.getOppHero(heroCell[i]), AbilityName.BLASTER_ATTACK, heroCell[i]);
                            break;
                        case HEALER:
                            tempH = heroCell[i];
                            break;
                        case BLASTER:
                            tempB = heroCell[i];
                            break;
                        case GUARDIAN:
                            tempG = heroCell[i];
                            break;
                        default:
                                break;
                    }
                    world.castAbility(world.getOppHero(tempH), AbilityName.BLASTER_ATTACK, tempH);
                    world.castAbility(world.getOppHero(tempB), AbilityName.BLASTER_ATTACK, tempB);
                    world.castAbility(world.getOppHero(tempG), AbilityName.BLASTER_ATTACK, tempG);
                }
            }
        }

    }


    //Bombing
    public void normalBomb(Cell[] heroCell){

    }

}


