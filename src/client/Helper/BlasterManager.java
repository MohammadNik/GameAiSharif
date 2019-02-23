package client.Helper;

import client.model.*;

import java.lang.reflect.Array;

public class BlasterManager implements HeroManager {
    private World world;
    private Hero blaster;


    @Override
    public void preProcess(World world) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!

    }

    @Override
    public void move(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.blaster = currentHero;  // WARNING: DON'T CHANGE THIS !!

        moveToObjectiveZone();

        //entrenchment(currentHero);

    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.blaster = currentHero;  // WARNING: DON'T CHANGE THIS !!

        /***************************************OFFENCE*****************************************/

        normalAttack();
        //world.castAbility(world.getOppHero(world.getOppHeroes()[0].getCurrentCell()), AbilityName.BLASTER_ATTACK, world.getOppHeroes()[0].getCurrentCell());

        /***************************************DODGE*******************************************/

    }


    /****************************************************************************************************/
    /*********************************************MOVE***************************************************/
    /****************************************************************************************************/

    private void moveToObjectiveZone() {
        for (Direction dir :
                world.getPathMoveDirections(blaster.getCurrentCell(), Helper.nearestCellFromOZ(world, blaster.getCurrentCell()))) {
            world.moveHero(blaster, dir);
        }
    }


    //returns the nearest wall cell from the nearest OZ
    private Cell nearestWalltoOZ() {

        int minWallDis = 31;
        Cell minWallDisCell = null;
        for (Cell[] cells : world.getMap().getCells()) {
            for (Cell cell : cells) {
                if (cell.isWall()) {
                    if (Helper.distanceCalculator(cell, Helper.nearestCellFromOZ(world, cell)) <= minWallDis)
                        minWallDis = Helper.distanceCalculator(cell, Helper.nearestCellFromOZ(world, cell));
                    minWallDisCell = cell;
                }
            }
        }

        return minWallDisCell;
    }

    //moves the blaster to the trench
    private void entrenchment() { // FIXME: 2/22/2019 find more appropriate trench cells and the new STRATEGY!
        Cell trench = nearestWalltoOZ();
        world.moveHero(blaster, Helper.nearestToCell(world, trench)); // FIXME: 2/22/2019 move to the bottom row of trench, not itself

    }

    /******************************************************************************************************/
    /********************************************ATTACK AND BOMB*******************************************/
    /******************************************************************************************************/


    //returns an array of hero cells ( if the cell is in vision )
    public Cell[] opponentHeroCell(){

        Cell[] enemyCells = null;
        int i = 0;
        for (Cell[] cells : world.getMap().getCells()) {
            for (Cell cell : cells)
                if (world.getOppHero(cell) == null)
                    continue;
                else {
                    enemyCells[i] = cell;
                    i++;
                }
        }

        return enemyCells;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //attacking if the opponent heroes are in our ideal position
    public void beneficialAttack(Cell[] heroCell){



    }


    /*public Cell[] sameColumnEnemy(){

        int index = 0;
        Cell[] sameColumn = null;
        Cell[] enemies = opponentHeroCell();

        for(int i = 0; i <enemies.length; i++){
            for (int j = i+1; j <enemies.length; j++){
                if(enemies[i+j].getColumn() == enemies[i].getColumn()) {
                    sameColumn[index] = enemies[i];
                    index++;
                }
            }
        }

        return sameColumn;
    }


    public Cell[] sameRowEnemy(){

        int index = 0;
        Cell[] sameRow = null;
        Cell[] enemies = opponentHeroCell();

        for(int i = 0; i <enemies.length; i++){
            for (int j = i+1; j <enemies.length; j++){
                if(enemies[i+j].getRow() == enemies[i].getRow()) {
                    sameRow[index] = enemies[i];
                    index++;
                }
            }
        }

        return sameRow;
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //Bombing if the opponent heroes are in our ideal position
    public void beneficialBomb (Cell[] heroCell){

    }


    //attacking
    public boolean normalAttack(){

        Cell[] heroCell = opponentHeroCell();
        if(heroCell != null)
            for (int i = 0; i< heroCell.length; i++){
                if( Helper.distanceCalculator(heroCell[i], blaster.getCurrentCell()) <= 4){ //near enough to attack
                    if(world.getOppHero(heroCell[i]).getCurrentHP() <= 20){ //fastest to be killed
                        if(blaster.getAbility(AbilityName.BLASTER_ATTACK).isReady()) {
                            fastKilledPriority(heroCell[i]);
                            return true;
                        }
                    }

                    else if(blaster.getAbility(AbilityName.BLASTER_ATTACK).isReady()) {
                        world.castAbility(world.getOppHero(heroCell[i]), AbilityName.BLASTER_ATTACK, heroCell[i]); // FIXME: 2/23/2019 more priority
                        return true;
                    }
                }
            }
        return false; //heroCell is null
    }
    //Attack Fast Killed Enemy by Priority
    public void fastKilledPriority( Cell enemy ){

        Cell tempH = null;
        Cell tempB = null;
        Cell tempG = null;

        switch (world.getOppHero(enemy).getName()){
            case SENTRY:
                world.castAbility(world.getOppHero(enemy), AbilityName.BLASTER_ATTACK, enemy);
                break;
            case HEALER:
                tempH = enemy;
                break;
            case BLASTER:
                tempB = enemy;
                break;
            case GUARDIAN:
                tempG = enemy;
                break;
            default:
                break;
        }
        world.castAbility(world.getOppHero(tempH), AbilityName.BLASTER_ATTACK, tempH);
        world.castAbility(world.getOppHero(tempB), AbilityName.BLASTER_ATTACK, tempB);
        world.castAbility(world.getOppHero(tempG), AbilityName.BLASTER_ATTACK, tempG);

    }


    //Bombing
    public void normalBomb(Cell[] heroCell){

    }

}


