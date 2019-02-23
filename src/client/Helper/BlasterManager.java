package client.Helper;

import client.model.*;
import com.sun.java.util.jar.pack.Instruction;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

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

        switch (enemiesNexttoEachOther().length){
            case 2:
                normalAttack();
                break;
            case 4:
                beneficialAttack(beneficialTarget());
                break;
            /*case 6:
                bomb();
                break;
            case 8:
                bomb();
                break;*/
            default:
                break;

        }

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

    //////////////////////////////////////////////////BENEFICIAL////////////////////////////////////////////////////////
    //attacking if the opponent heroes are in our ideal position
    //Parameter "heroCell" is beneficialTarget()
    public void beneficialAttack(Cell heroCell){

        world.castAbility(world.getOppHero(heroCell), AbilityName.BLASTER_ATTACK, heroCell);
    }

    public Cell[] enemiesNexttoEachOther(){

        Cell[] enemies = opponentHeroCell();
        Cell[] Nextto = null;
        int index = 0;
        for ( int i = 0; i <enemies.length; i++) {
            for (int j = i+1; j<enemies.length; j++) {
                if (world.manhattanDistance(enemies[i], enemies[i + j]) == 1) {
                    Nextto[index] = enemies[i];
                    Nextto[index+1] = enemies[i+j];
                    index +=2;

                }
            }

        }

        return Nextto;
    }

    //Find the Best Target to Attack
    public Cell beneficialTarget(){

        Cell[] Nextto = enemiesNexttoEachOther();
        for(int i = 0; i <Nextto.length; i++){
            for(int j = i+1; j <Nextto.length; j++){
                if(Nextto[i] == Nextto[i+j])
                    return Nextto[i];
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////NORMAL//////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //Bombing
    public void bomb(Cell[] heroCell){

    }

}


