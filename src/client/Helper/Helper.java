package client.Helper;

import client.model.Cell;
import client.model.Direction;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {

    public static Direction nearestToCell(World world,Cell cell){
        int c = cell.getColumn();
        int r = cell.getRow();

        Cell up = world.getMap().getCell(r-1,c);
        boolean upNotAvailable = up.isWall() || !world.getMap().isInMap(r-1,c);

        Cell down = world.getMap().getCell(r+1,c);
        boolean downNotAvailable = down.isWall() || !world.getMap().isInMap(r+1,c);

        Cell left = world.getMap().getCell(r,c-1);
        boolean leftNotAvailable = left.isWall() || !world.getMap().isInMap(r,c-1);

        Cell right = world.getMap().getCell(r,c+1);
        boolean rightNotAvailable = right.isWall() || !world.getMap().isInMap(r,c+1);

        Cell nearest = nearestCellFromOZ(world,cell);

        Direction direction = Direction.UP;
        Cell nextCell = cell;
        if (!upNotAvailable && distanceCalculator(nearest,nextCell) < distanceCalculator(nearest,up)){
            nextCell = up;
            direction = Direction.UP;
        }

        if (!downNotAvailable && distanceCalculator(nearest,nextCell) < distanceCalculator(nearest,down)){
            nextCell = down;
            direction = Direction.DOWN;
        }
        if (!leftNotAvailable && distanceCalculator(nearest,nextCell) < distanceCalculator(nearest,left)){
            nextCell = left;
            direction = Direction.LEFT;
        }
        if (!rightNotAvailable && distanceCalculator(nearest,nextCell) < distanceCalculator(nearest,right)){
            direction = Direction.RIGHT;
        }


        return direction;
    }

    private static int distanceCalculator(Cell cell1, Cell cell2){
        return Math.abs(cell1.getColumn() - cell2.getColumn()) + Math.abs(cell1.getRow()-cell2.getRow());

    }


    // return nearest cell from objective zone to current cell
    public static Cell nearestCellFromOZ(World world,Cell cell){
       return Arrays.stream(world.getMap().getObjectiveZone())
               .filter(cell1 -> world.getMyHero(cell1) == null)
                .reduce( (result, eachCell) -> {
                    int distanceFromResult = Math.abs(cell.getColumn()-result.getColumn()) + Math.abs(cell.getRow()-result.getRow());
                    int distanceFromEachCell = Math.abs(cell.getColumn()-eachCell.getColumn()) + Math.abs(cell.getRow()-eachCell.getRow());

                    return (distanceFromEachCell - distanceFromResult < 0) ? eachCell : result;
                } ).orElse(cell);
    }


    public static List<Cell> cellInRangeOfSpot(World world,Cell cell, int range){
        List<Cell> list = new ArrayList<>();

        for (int mx = 0 ; mx < world.getMap().getColumnNum() ; mx++)
            for (int my=0 ; my < world.getMap().getRowNum() ; my++){
                int fx = Math.abs(cell.getColumn()-mx);
                int fy = Math.abs(cell.getRow()-my);
                if (fx + fy <= range) list.add(world.getMap().getCell(my,mx));

            }
        return list;
    }

    // return nearest Enemy
    public static List<Hero> getEnemiesInRange(World world,Hero hero, int RANGE){

        return cellInRangeOfSpot(world,hero.getCurrentCell(),RANGE)
                .stream()
                .filter( cell -> world.getMyHero(cell) == null)
                .filter( cell -> world.getOppHero(cell) != null)
                .map(world::getOppHero)
                .collect(Collectors.toList());
    }

}
