package client.Helper;

import client.model.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

        if(wallInObjectiveZone() != null){
            entrenchment(1);
        }else {
            entrenchment(0);
        }

        //blaster.getCurrentCell().

    }

    @Override
    public void takeAction(World world, Hero currentHero) {
        this.world = world; // WARNING: DON'T CHANGE THIS !!
        this.blaster = currentHero;  // WARNING: DON'T CHANGE THIS !!

        /******************************************OFFENCE********************************************/

        switch (enemiesNexttoEachOther().size()){
            case 2:
                normalAttack();
                break;
            case 4:
                beneficialAttack(beneficialTarget());
                break;
            case 6:
                bomb(beneficialTarget());
                if(!bomb(beneficialTarget()))
                    beneficialAttack(beneficialTarget());
                break;
            case 8:
                bomb(beneficialTarget());
                if(!bomb(beneficialTarget()))
                    beneficialAttack(beneficialTarget());
                break;
            default:
                break;

        }

        /********************************************DODGE**********************************************/
        if(!anyReadyDefAbility())
            defensiveDodge();
        if(world.getAP() < 15)
            defensiveDodge();

        if(enemiesNexttoEachOther().size() >=6 && world.manhattanDistance(blaster.getCurrentCell(), beneficialTarget()) >5)
            offensiveDodge();

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
                    if (world.manhattanDistance(blaster.getCurrentCell(), cell) <= minWallDis) {
                        minWallDis = world.manhattanDistance(blaster.getCurrentCell(), cell);
                        minWallDisCell = cell;
                    }
                }
            }
        }

        return minWallDisCell; //nearest wall cell
    }

    private Cell wallInObjectiveZone(){
        return Arrays.stream(world.getMap().getObjectiveZone()).parallel()
                .filter(Cell::isWall).findFirst().orElse(null);
    }

    private Direction[] findTrenchCellPath(int state){

        Direction[] dir;
        if(state == 1) {
            dir = world.getPathMoveDirections(blaster.getCurrentCell(), wallInObjectiveZone());
//            dir = Arrays.stream(dir).limit(dir.length).toArray(Direction[]::new); //delete the last direction in array dir

        }else {
            dir = world.getPathMoveDirections(blaster.getCurrentCell(), nearestWalltoOZ());
//            dir = Arrays.stream(dir).limit(dir.length).toArray(Direction[]::new); //delete the last direction in array dir
        }

        return dir;
    }

    //moves the blaster to the trench
    private void entrenchment(int state) {
        //Cell trench = wallInObjectiveZone();
        /*if( state == 1) {  //wall is in OZ
            for (Direction dir : findTrenchCellPath(1)) {
                world.moveHero(blaster, dir);
            }
        }else {
            for (Direction dir : findTrenchCellPath(0)) {
                world.moveHero(blaster, dir);
            }
        }*/

        for (Direction dir : findTrenchCellPath(state))
            world.moveHero(blaster, dir);

    }

    /******************************************************************************************************/
    /********************************************ATTACK AND BOMB*******************************************/
    /******************************************************************************************************/

    //returns an array of hero cells ( if the cell is in vision )
    private ArrayList<Cell> opponentHeroCell(){

        ArrayList<Cell> enemyCells = new ArrayList<>();
        for (Cell[] cells : world.getMap().getCells()) {
            for (Cell cell : cells)
                if (world.getOppHero(cell) == null)
                    continue;
                else {
                    enemyCells.add(cell);
                }
        }

        return enemyCells;
    }

    //////////////////////////////////////////////////BENEFICIAL////////////////////////////////////////////////////////

    //attacking if the opponent heroes are in our ideal position
    //Parameter "heroCell" is beneficialTarget()
    private void beneficialAttack(Cell heroCell){

        if(blaster.getAbility(AbilityName.BLASTER_ATTACK).isReady()) {
            if(world.getAP() >= 15)
                if(world.manhattanDistance(heroCell, blaster.getCurrentCell()) <= 4)
                    world.castAbility(blaster, AbilityName.BLASTER_ATTACK, heroCell);
        }
    }

    private ArrayList<Cell> enemiesNexttoEachOther(){

        ArrayList<Cell> enemies = new ArrayList<>(opponentHeroCell());
        ArrayList<Cell> nextTo = new ArrayList<>();
        for ( int i = 0; i <enemies.size(); i++) {
            for (int j = i+1; j<enemies.size(); j++) {
                if (world.manhattanDistance(enemies.get(i), enemies.get(j)) == 1) {
                    nextTo.add(enemies.get(i));
                    nextTo.add(enemies.get(j));
                }
            }

        }
        return nextTo;
    }

    //Find the Best Target to Attack
    private Cell  beneficialTarget(){

        ArrayList<Cell> nextTo = new ArrayList<>(enemiesNexttoEachOther());
        for(int i = 0; i <nextTo.size(); i++){
            for(int j = i+1; j <nextTo.size(); j++){
                if(nextTo.get(i) == nextTo.get(j))
                    return nextTo.get(i);
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////NORMAL//////////////////////////////////////////////////////////

    //attacking
    private void normalAttack(){

        ArrayList<Cell> heroCell = new ArrayList<>(opponentHeroCell());
        if(opponentHeroCell().size() != 0)
            for (int i = 0; i< heroCell.size(); ++i){
                if(blaster.getAbility(AbilityName.BLASTER_ATTACK).isReady()){
                    if(world.manhattanDistance(heroCell.get(i), blaster.getCurrentCell()) <= 4){ //near enough to attack
                        if(world.getOppHero(heroCell.get(i)).getCurrentHP() <= 20) {//fastest to be killed
                            KillByPriority(heroCell.get(i));
                        }else {
                            world.castAbility(world.getOppHero(heroCell.get(i)), AbilityName.BLASTER_ATTACK, heroCell.get(i));
                        }
                    }

                }
            }
    }
    //Attack Fast Killed Enemy by Priority
    private void KillByPriority( Cell enemy ){

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
    //heroCell is beneficialTarget()
    private boolean bomb(Cell heroCell){
        
        if(blaster.getAbility(AbilityName.BLASTER_BOMB).isReady()) {
            if(world.getAP() >= 25)
                if(Helper.distanceCalculator(heroCell, blaster.getCurrentCell()) <= 4)
                    world.castAbility(blaster, AbilityName.BLASTER_BOMB, heroCell);
            return true; 
        }
        else
            return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void defensiveDodge(){
        for (Cell[] cells : world.getMap().getCells()){
            for (Cell cell : cells){
                if(cell.isWall() && (world.manhattanDistance(cell, blaster.getCurrentCell()) <=5
                        && world.manhattanDistance(cell, blaster.getCurrentCell()) >=3)){
                    world.castAbility(blaster, AbilityName.BLASTER_DODGE, cell);
                }
            }
        }

    }

    private boolean anyReadyDefAbility(){

        if( blaster.getAbility(AbilityName.BLASTER_ATTACK).isReady() || blaster.getAbility(AbilityName.BLASTER_BOMB).isReady())
            return true;
        return false;

    }

    private void offensiveDodge(){
        for (Cell[] cells : world.getMap().getCells()){
            for (Cell cell : cells){
                if (world.manhattanDistance(blaster.getCurrentCell(), cell) == 5)
                    world.castAbility(blaster, AbilityName.BLASTER_DODGE, cell);
            }
        }
    }

}


