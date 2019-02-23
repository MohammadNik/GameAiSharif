package client.Helper;

import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;
import java.util.List;


public class MapManager {

    static int radius = 2;

    // TODO: 2019-02-23 fix this.
    // return the best cell for hiding, takes in makeAttemptHideTable()
    public static Cell findHidingCell(World world, Cell myHeroCell, Cell enemyCell) {
        // TODO: 2019-02-20 Can you use remaining phasesCount to provide a more efficient solution?
        //boolean[][] hidingTable = new boolean[radius * 2 + 1][radius * 2 + 1];
        Cell[][] table = makeAttemptHideTable(myHeroCell,world);
        Cell choiceCell = table[0][0];
        int distance = 100;

        for(int i = 0; i < radius * 2 + 1; i++)
        {
            for(int j = 0; j < radius * 2 + 1; j++)
            {
                if(table[i][j] != null)
                {
                    if(world.isInVision(table[i][j], enemyCell))
                    {
                        //hidingTable[i][j] = true;
                        if(world.manhattanDistance((table[i][j]), enemyCell) < distance
                                && world.manhattanDistance((table[i][j]), enemyCell) != 0)
                        {
                            choiceCell = enemyCell;
                            distance = world.manhattanDistance((table[i][j]), enemyCell);
                        }
                    }
                    else
                    {
                        //hidingTable[i][j] = false;
                    }
                }
            }
        }

        return choiceCell;
    }

    // returns a table around a designated Cell, mainly used when an enemy is seen and you'd like to hide from it.
    public static Cell[][] makeAttemptHideTable(Cell curCell, World world) {

        //character's coordinates may be in a way that we may not be able to make the table well, like 0,0!
        //SOLVED: ypu don't need to count that in, we simply make our checking area smaller.(instead of i * j, have i * (j - 1) table.
        int curCellX = curCell.getColumn(),
                curCellY = curCell.getRow();

        Cell[][] table = new Cell[radius * 2 + 1][radius * 2 + 1];

        for(int i = curCellY - radius; i < curCellY + radius; i++)
        {
            for(int j = curCellX - radius; j < curCellX + radius; j++)
            {
                if(world.getMap().isInMap(i, j))
                {
                    table[i - curCellY + radius][j - curCellX + radius] = world.getMap().getCell(i, j);
                }
                else
                {
                    table[j][i] = null;
                }
            }
        }

        return table;
    }

    // TODO: 2019-02-23 fix this.
    public static Cell[] findCellsOnLine(Cell from, Cell to, World world){
        int diff;
        Cell[] cells;
        // TODO: 23/02/2019 swap from & to if they don't follow default settings for this method.

        switch (declareTypeOfSeeing(from, to)){
            case 0:
                return new Cell[] { from };
            case 1:
                diff = abs(from.getColumn() - to.getColumn());
                cells = new Cell[diff];
                for(int i = 0; i <= diff; i++){
                    cells[i] = world.getMap().getCell(from.getRow(), from.getColumn() + i);
                }
                return cells;
            case 2:
                diff = abs(from.getRow() - to.getRow());
                cells = new Cell[diff];
                for(int i = 0; i <= diff; i++){
                    cells[i] = world.getMap().getCell(from.getRow() + i, from.getColumn());
                }
                return cells;
            case 5:
                diff = abs(from.getRow() - to.getRow());
                cells = new Cell[diff];
                for(int i = 0; i <= diff; i++) {
                    cells[i] = world.getMap().getCell(from.getRow() + i, from.getColumn() + i);
                }
                return cells;
            case 3:
                diff = abs(from.getRow() - to.getRow() + from.getColumn() - to.getColumn());
                cells = new Cell[diff];
                for(int i = 0; i < diff / 2; i += 2){
                    cells[i] = world.getMap().getCell(from.getRow() + i, from.getColumn() + i);
                    cells[i + 1] = world.getMap().getCell(from.getRow() + i, from.getColumn() + i + 1);
                    cells[diff - i - 1] = world.getMap().getCell(to.getRow() - from.getRow() - i,
                            to.getColumn() - from.getColumn() - i);
                    cells[diff - i - 1 - 1] = world.getMap().getCell(to.getRow() - from.getRow() - i,
                            to.getColumn() - from.getColumn() - i - 1);
                }
                return cells;
            case 4:
                diff = abs(from.getRow() - to.getRow() + from.getColumn() - to.getColumn());
                cells = new Cell[diff];
                for(int i = 0; i < diff / 2; i += 2){
                    cells[i] = world.getMap().getCell(from.getRow() + i, from.getColumn() + i);
                    cells[i + 1] = world.getMap().getCell(from.getRow() + i + 1, from.getColumn() + i);
                    cells[diff - i - 1] = world.getMap().getCell(to.getRow() - from.getRow() - i,
                            to.getColumn() - from.getColumn() - i);
                    cells[diff - i - 1 - 1] = world.getMap().getCell(to.getRow() - from.getRow() - i - 1,
                            to.getColumn() - from.getColumn() - i);
                }
                return cells;
        }

        return new Cell[] { from };
    }

    // 0: same cell, 1: linearHorizontal, 2: linearVertical, 3: rectHor, 4: rectVer, 5: Square
    public static int declareTypeOfSeeing(Cell begin, Cell end){
        int horizontalDiff = abs(begin.getColumn() - end.getColumn());
        int verticalDiff = abs(begin.getRow() - end.getRow());

        if(horizontalDiff == 0 && verticalDiff == 0)
            return 0;
        else if (verticalDiff == 0)
            return 1;
        else if (horizontalDiff == 0)
            return 2;
        else if (horizontalDiff == verticalDiff)
            return 5;
        else if (horizontalDiff != verticalDiff){
            if(isVertical(begin, end))
                return 4;
            else
                return 3;
        }

        return 0;
    }

    //okay, I used a Simplify thingy and it made it look like this... what happened?!
    public static boolean isVertical(Cell begin, Cell end){
        return abs(begin.getColumn() - end.getColumn()) <= abs(begin.getRow() - end.getRow());
    }

    public static int abs(int input){
        input *= (input < 0) ? -1 : 1;
        return input;
    }

    // TODO: 2019-02-23 complete this method based on prioritization of the 2 heroes.
    // prioritizes the 2 heroes when forming a linear formation to tell who stands closer to (in front of) the enemy.
    public static Cell[] prioritizeCells(Hero friendly1, Hero freindly2, Cell[] suggestedPositions){
        return suggestedPositions;
    }

    //returns 2 cells indicating for 2 friendly heroes where to stand to form a linear formation.
    //cells[0] is always closer to enemyPos.
    public static Cell[] findInBetweenCell(Cell friendlyPos1, Cell friendlyPos2, Cell enemyPos, World world){
        Cell[] cells = new Cell[2];

        cells[0] =  world.getMap().getCell(
                abs(friendlyPos1.getRow() - friendlyPos2.getRow()),
                abs(friendlyPos1.getColumn() - friendlyPos2.getColumn()));

        // if enemy can't see from above.
        if(!world.isInVision(enemyPos, world.getMap().getCell(cells[0].getRow() - 1, cells[0].getColumn())))
            cells[1] = world.getMap().getCell(cells[0].getRow() - 1, cells[0].getColumn());
            // if enemy can't see from below.
        else if (!world.isInVision(enemyPos, world.getMap().getCell(cells[0].getRow() + 1, cells[0].getColumn())))
            cells[1] = world.getMap().getCell(cells[0].getRow() + 1, cells[0].getColumn());
            //if enemy can't see from right.
        else if (!world.isInVision(enemyPos, world.getMap().getCell(cells[0].getRow(), cells[0].getColumn() - 1)))
            cells[1] = world.getMap().getCell(cells[0].getRow(), cells[0].getColumn() - 1);
            //if enemy can't see from left.
        else if (!world.isInVision(enemyPos, world.getMap().getCell(cells[0].getRow(), cells[0].getColumn() + 1)))
            cells[1] = world.getMap().getCell(cells[0].getRow(), cells[0].getColumn() + 1);
        else
            cells[1] = cells[0];

        return cells;
    }

    // takes in a range(manhattan), range is based upon a center, and a world for access to map and heroes.
    // returns a List<List<Cell>>:
        //.get(0).get(i); indicates i-th friendly hero if and only if i-th friendly hero is in that position, otherwise it's null.
        //.get(1).get(j) ; indicates i-th enemy hero if and only if i-th enemy hero is in that position, otherwise it's null.
    // TODO: 2019-02-23 return a Cell[][] instead of a list for ease of use, just notify others of how to access it without knowing its length.
    public static List<List<Cell>> findHeroesCellsInManhattanRange(int range, Cell center, World world) {
        Hero[] myHeroes = world.getMyHeroes();
        Hero[] enemyHeroes = world.getOppHeroes();

        List<List<Cell>> heroes = new ArrayList<>();
        heroes.add(new ArrayList<>());
        heroes.add(new ArrayList<>());

        for (Hero myHero : myHeroes) {
            int dist = world.manhattanDistance(myHero.getCurrentCell(), center);
            if (dist != -1 && dist <= range) {
                heroes.get(0).add(0, myHero.getCurrentCell());
            }
        }

        for (Hero enemyHero : enemyHeroes) {
            int dist = world.manhattanDistance(enemyHero.getCurrentCell(), center);
            if (dist != -1 && dist <= range) {
                heroes.get(1).add(0, enemyHero.getCurrentCell());
            }
        }

        return heroes;
    }
}