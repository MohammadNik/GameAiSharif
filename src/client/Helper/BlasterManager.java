package client.Helper;

import client.model.Cell;
import client.model.Direction;
import client.model.Hero;
import client.model.World;

public class BlasterManager implements HeroManager {
    private World world;

    public BlasterManager(World world){
        this.world = world;
    }

    @Override
    public void preProcess() {

    }

    @Override
    public void move(Hero currentHero) {
        moveToObjectiveZone(currentHero);

    }

    @Override
    public void takeAction(Hero currentHero) {

        /***************************************OFFENCE*****************************************/




        /***************************************DODGE*******************************************/

    }

    private void moveToObjectiveZone(Hero blaster){
        for (Direction dir :
                world.getPathMoveDirections(blaster.getCurrentCell(),Helper.nearestCellFromOZ(world,blaster.getCurrentCell()))){
            world.moveHero(blaster,dir);
        }
    }

    private static int distanceCalculator(Cell cell1, Cell cell2){
        return Math.abs(cell1.getColumn() - cell2.getColumn()) + Math.abs(cell1.getRow()-cell2.getRow());

    }


    //returns the nearest wall cell from the nearest OZ
    private Cell nearestWalltoOZ(){

        int minWallDis = 31;
        Cell minWallDisCell = null;
        for (Cell[] cells : world.getMap().getCells()){
            for (Cell cell : cells){
                if(cell.isWall()){
                    if(distanceCalculator(cell, Helper.nearestCellFromOZ(world, cell)) <= minWallDis)
                        minWallDis = distanceCalculator(cell, Helper.nearestCellFromOZ(world, cell));
                        minWallDisCell = cell;
                }
            }
        }

        return minWallDisCell;
    }

}
