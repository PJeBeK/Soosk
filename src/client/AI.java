package client;

import client.model.*;

import java.util.*;


/**
 * AI class.
 * You should fill body of the method {@link #doTurn}.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class AI {

    private static World game;
    static int[][][][][][] distances;
    static Cell[][] cells;
    static int[][][][] strategies;
    private static HashMap<Integer , Integer> timeRemaining;
    private static Beetle changingBeetle;
    private static Node[][][] nodes;
    private static Boolean isDone = false;

    private static void myChangeStrategy(BeetleType beetleType , CellState x , CellState y , CellState z , Move move){
        System.out.print("__!__");
        System.out.print(beetleType);
        System.out.print(x);
        System.out.print(y);
        System.out.print(z);
        System.out.println(move);

        CellState c = x;
        x = z;
        z = c;

        if (strategies[beetleType.getValue()][x.getValue()][y.getValue()][z.getValue()] == -1){
            game.changeStrategy(beetleType , x , y , z , move);
//            System.out.println("done");
        }else{
            if (strategies[beetleType.getValue()][x.getValue()][y.getValue()][z.getValue()] != move.getValue()){
                game.changeStrategy(beetleType , x , y , z , move);
//                System.out.println("done");
            }
        }
        strategies[beetleType.getValue()][x.getValue()][y.getValue()][z.getValue()] = move.getValue();
    }

    public static World getGame(){
        return game;
    }

    //calculates the score
    public static double calScore(Beetle beetle, Move move, Beetle beetle2){
        int w = game.getMap().getWidth();
        int h = game.getMap().getHeight();
        double INF = 1000.0;
        int dis = 0;
        int dis1 = 0;
        if (move.getValue() != 1){
            int directionValue = 0;
            switch (move.getValue()){
                case 0:
                    directionValue = (beetle.getDirection().getValue() + 3) %4;
                    break;
                case 2:
                    directionValue = (beetle.getDirection().getValue() + 1) %4;
                    break;
            }
            Direction newDir = null;
            switch (directionValue){
                case 0:
                    newDir = Direction.Right;
                    break;
                case 1:
                    newDir = Direction.Up;
                    break;
                case 2:
                    newDir = Direction.Left;
                    break;
                case 3:
                    newDir = Direction.Down;
                    break;
            }
//            System.out.print("{");
//            System.out.print(beetle.getPosition().getX());
//            System.out.print(",");
//            System.out.print(beetle.getPosition().getY());
//            System.out.print(",");
//            System.out.print(beetle.getDirection().getValue());
//            System.out.print(",");
//            System.out.print(move.getValue());
//            System.out.print("}");

//            System.out.print("\t-->\t");

//            System.out.print("{");
//            System.out.print(beetle.getPosition().getX());
//            System.out.print(",");
//            System.out.print(beetle.getPosition().getY());
//            System.out.print(",");
//            System.out.print(newDir.getValue());
//            System.out.print("}");
            dis = distance(beetle.getPosition().getX(), beetle.getPosition().getY(), newDir, beetle2.getPosition().getX(), beetle2.getPosition().getY());
            dis1 = distance(beetle2.getPosition().getX(), beetle2.getPosition().getY(), beetle2.getDirection() , beetle.getPosition().getX() , beetle.getPosition().getY());
//            System.out.print("\t-->\t");
//            System.out.print(dis);
//            System.out.println();
        }
        else if (move.getValue() == 1){
//            System.out.println(beetle2);
//            System.out.println(beetle2.getPosition());
            switch (beetle.getDirection()){
                case Right:
                    dis = distance(beetle.getPosition().getX(), (beetle.getPosition().getY()+1)%w, beetle.getDirection(), beetle2.getPosition().getX(), beetle2.getPosition().getY());
                    dis1 = distance(beetle2.getPosition().getX(), beetle2.getPosition().getY() , beetle2.getDirection() , beetle.getPosition().getX(), (beetle.getPosition().getY()+1)%w);
                    break;
                case Left:
                    dis = distance(beetle.getPosition().getX(), (beetle.getPosition().getY()+w-1)%w, beetle.getDirection(), beetle2.getPosition().getX(), beetle2.getPosition().getY());
                    dis1 = distance(beetle2.getPosition().getX(), beetle2.getPosition().getY() , beetle2.getDirection() , beetle.getPosition().getX(), (beetle.getPosition().getY()+w-1)%w);
                    break;
                case Up:
                    dis = distance((beetle.getPosition().getX()+h-1)%h, beetle.getPosition().getY(), beetle.getDirection(), beetle2.getPosition().getX(), beetle2.getPosition().getY());
                    dis1 = distance(beetle2.getPosition().getX(), beetle2.getPosition().getY() , beetle2.getDirection() , (beetle.getPosition().getX()+h-1)%h, beetle.getPosition().getY());
                    break;
                case Down:
                    dis = distance((beetle.getPosition().getX()+1)%h, beetle.getPosition().getY(), beetle.getDirection(), beetle2.getPosition().getX(), beetle2.getPosition().getY());
                    dis1 = distance(beetle2.getPosition().getX(), beetle2.getPosition().getY() , beetle2.getDirection() , (beetle.getPosition().getX()+1)%h, beetle.getPosition().getY());
                    break;
            }
        }
        if (dis1 > 3){
            if (beetle.getPower() < beetle2.getPower()) {
                return 0;
            }
        }
        dis = dis * dis;
//        dis1 = dis;
        dis1 = dis1 * dis1;
        if(beetle.getPower() > 2 * beetle2.getPower()){
            if (dis == 0)
                return INF * getKillingScore(beetle2);
            return getKillingScore(beetle2) * 1.0 / dis;
        }
        else if(beetle.getPower() > beetle2.getPower()){
            if (dis == 0)
                return INF * getKillingScore(beetle2);
            return getKillingScore(beetle2) * (double) (beetle.getPower() - beetle2.getPower()) / (beetle2.getPower() * dis);
        }
        else if(beetle.getPower() == beetle2.getPower()){
            if (beetle.getPower() == 0) return 0.0;
            return 0.0;
        }
        else if(beetle.getPower() > 0.5 * beetle2.getPower()){
            if (dis1 == 0)
                return -INF * getKillingScore(beetle);
            return getKillingScore(beetle) * (double) (beetle.getPower() - beetle2.getPower()) / (beetle.getPower() * dis1);
        }
        else{
            if (dis1 == 0)
                return -INF * getKillingScore(beetle);
            return -1.0 * getKillingScore(beetle) / dis1;
        }
    }

    public static double getKillingScore(Beetle beetle){
        if (beetle.has_winge()){
            return (double) game.getConstants().getKillQueenScore();
        }else{
            return (double) game.getConstants().getKillFishScore();
        }
    }

    public static double getEatingScore(Beetle beetle){
        if (beetle.has_winge()){
            return (double) game.getConstants().getQueenFoodScore();
        }else{
            return (double) game.getConstants().getFishFoodScore();
        }
    }

    public static double calBeetleScore(Beetle beetle, Move move){
//        System.out.println("hooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooly shit");
        Cell[] oppCells = game.getMap().getOppCells();
        double ans = 0;
        for (Cell c: oppCells) {
            if (c == null)
                continue;
            Beetle b = (Beetle) c.getBeetle();
            //TODO :server is wrong
//            System.out.println("^");
//            System.out.println(c);
//            System.out.println(c.getX());
//            System.out.println(c.getY());
//            System.out.println(c.getBeetle().toString());
//            System.out.println(b.getPosition());
//            System.out.println(c.getBeetle().getPosition());
//            System.out.println(calScore(beetle, move, b));
            ans += calScore(beetle, move, b);
        }

        //TODO: is it good?
        for (Cell c:game.getMap().getFoodCells()){
            if (c == null) continue;
            ans += calFoodScore(beetle , move , c.getItem());
        }

        //TODO: is it good?
        for (Cell c : game.getMap().getTrashCells()){
            if (c == null) continue;
            ans += calTrashScore(beetle , move , c.getItem());
        }


        //slipper //TODO not debuged
        for (Cell c : game.getMap().getSlipperCells()){
            if (c == null) continue;
            ans += calSlipperScore(beetle , move , (Slipper) c.getSlipper());
        }

        ans /= oppCells.length;
        return ans;
    }

    public static double calFoodScore(Beetle beetle, Move move , Entity food){
        int w = game.getMap().getWidth();
        int h = game.getMap().getHeight();
        double INF = 1000.0;
        int dis = 0;
        if (move.getValue() != 1){
            int directionValue = 0;
            switch (move.getValue()){
                case 0:
                    directionValue = (beetle.getDirection().getValue() + 3) %4;
                    break;
                case 2:
                    directionValue = (beetle.getDirection().getValue() + 1) %4;
                    break;
            }
            Direction newDir = null;
            switch (directionValue){
                case 0:
                    newDir = Direction.Right;
                    break;
                case 1:
                    newDir = Direction.Up;
                    break;
                case 2:
                    newDir = Direction.Left;
                    break;
                case 3:
                    newDir = Direction.Down;
                    break;
            }
//            System.out.print("{");
//            System.out.print(beetle.getPosition().getX());
//            System.out.print(",");
//            System.out.print(beetle.getPosition().getY());
//            System.out.print(",");
//            System.out.print(beetle.getDirection().getValue());
//            System.out.print(",");
//            System.out.print(move.getValue());
//            System.out.print("}");

//            System.out.print("\t-->\t");

//            System.out.print("{");
//            System.out.print(beetle.getPosition().getX());
//            System.out.print(",");
//            System.out.print(beetle.getPosition().getY());
//            System.out.print(",");
//            System.out.print(newDir.getValue());
//            System.out.print("}");
            dis = distance(beetle.getPosition().getX(), beetle.getPosition().getY(), newDir, food.getPosition().getX(), food.getPosition().getY());
//            System.out.print("\t-->\t");
//            System.out.print(dis);
//            System.out.println();
        }
        else if (move.getValue() == 1){
            switch (beetle.getDirection()){
                case Right:
                    dis = distance(beetle.getPosition().getX(), (beetle.getPosition().getY()+1)%w, beetle.getDirection(), food.getPosition().getX(), food.getPosition().getY());
                    break;
                case Left:
                    dis = distance(beetle.getPosition().getX(), (beetle.getPosition().getY()+w-1)%w, beetle.getDirection(), food.getPosition().getX(), food.getPosition().getY());
                    break;
                case Up:
                    dis = distance((beetle.getPosition().getX()+h-1)%h, beetle.getPosition().getY(), beetle.getDirection(), food.getPosition().getX(), food.getPosition().getY());
                    break;
                case Down:
                    dis = distance((beetle.getPosition().getX()+1)%h, beetle.getPosition().getY(), beetle.getDirection(), food.getPosition().getX(), food.getPosition().getY());
                    break;
            }
        }
        if (timeRemaining.get(new Integer(food.getId())) <= dis){
            return 0;
        }
        dis = dis * dis;
        if (dis == 0){
            return INF * getEatingScore(beetle);
        }
        return ((double) getEatingScore(beetle)) / dis;
    }

    public static double calTrashScore(Beetle beetle, Move move , Entity trash){
        int w = game.getMap().getWidth();
        int h = game.getMap().getHeight();
        double INF = 1000.0;
        int dis = 0;
        if (move.getValue() != 1){
            int directionValue = 0;
            switch (move.getValue()){
                case 0:
                    directionValue = (beetle.getDirection().getValue() + 3) %4;
                    break;
                case 2:
                    directionValue = (beetle.getDirection().getValue() + 1) %4;
                    break;
            }
            Direction newDir = null;
            switch (directionValue){
                case 0:
                    newDir = Direction.Right;
                    break;
                case 1:
                    newDir = Direction.Up;
                    break;
                case 2:
                    newDir = Direction.Left;
                    break;
                case 3:
                    newDir = Direction.Down;
                    break;
            }
            dis = distance(beetle.getPosition().getX(), beetle.getPosition().getY(), newDir, trash.getPosition().getX(), trash.getPosition().getY());
        }
        else if (move.getValue() == 1){
            switch (beetle.getDirection()){
                case Right:
                    dis = distance(beetle.getPosition().getX(), (beetle.getPosition().getY()+1)%w, beetle.getDirection(), trash.getPosition().getX(), trash.getPosition().getY());
                    break;
                case Left:
                    dis = distance(beetle.getPosition().getX(), (beetle.getPosition().getY()+w-1)%w, beetle.getDirection(), trash.getPosition().getX(), trash.getPosition().getY());
                    break;
                case Up:
                    dis = distance((beetle.getPosition().getX()+h-1)%h, beetle.getPosition().getY(), beetle.getDirection(), trash.getPosition().getX(), trash.getPosition().getY());
                    break;
                case Down:
                    dis = distance((beetle.getPosition().getX()+1)%h, beetle.getPosition().getY(), beetle.getDirection(), trash.getPosition().getX(), trash.getPosition().getY());
                    break;
            }
        }
        dis = dis * dis;
        if (dis == 0){
            return -INF * getKillingScore(beetle) * 0.999;
        }
        if (dis == 1){
            return - getKillingScore(beetle) * 0.999;
        }

        return 0;
    }


    public static double calSlipperScore(Beetle beetle, Move move , Slipper slipper){
//        System.out.println("____");
//        System.out.println(beetle.getPosition().getX());
//        System.out.println(beetle.getPosition().getY());
//        System.out.println(beetle.getDirection());
//        System.out.println(move);
//        System.out.println("____");
        int w = game.getMap().getWidth();
        int h = game.getMap().getHeight();
        double INF = 1000.0;
        int dis = 0;
        if (move.getValue() != 1){
            int directionValue = 0;
            switch (move.getValue()){
                case 0:
                    directionValue = (beetle.getDirection().getValue() + 3) %4;
                    break;
                case 2:
                    directionValue = (beetle.getDirection().getValue() + 1) %4;
                    break;
            }
            Direction newDir = null;
            switch (directionValue){
                case 0:
                    newDir = Direction.Right;
                    break;
                case 1:
                    newDir = Direction.Up;
                    break;
                case 2:
                    newDir = Direction.Left;
                    break;
                case 3:
                    newDir = Direction.Down;
                    break;
            }
            dis = distSlipper(beetle.getPosition().getX(), beetle.getPosition().getY(), newDir, slipper.getPosition().getX(), slipper.getPosition().getY());
        }
        else if (move.getValue() == 1){
            switch (beetle.getDirection()){
                case Right:
                    dis = distSlipper(beetle.getPosition().getX(), (beetle.getPosition().getY()+1)%w, beetle.getDirection(), slipper.getPosition().getX(), slipper.getPosition().getY());
                    break;
                case Left:
                    dis = distSlipper(beetle.getPosition().getX(), (beetle.getPosition().getY()+w-1)%w, beetle.getDirection(), slipper.getPosition().getX(), slipper.getPosition().getY());
                    break;
                case Up:
                    dis = distSlipper((beetle.getPosition().getX()+h-1)%h, beetle.getPosition().getY(), beetle.getDirection(), slipper.getPosition().getX(), slipper.getPosition().getY());
                    break;
                case Down:
                    dis = distSlipper((beetle.getPosition().getX()+1)%h, beetle.getPosition().getY(), beetle.getDirection(), slipper.getPosition().getX(), slipper.getPosition().getY());
                    break;
            }
        }
//        System.out.println("*");
//        System.out.println(dis);
        if (slipper.getRemainingTurns() < dis){
            return -INF * getKillingScore(beetle);
        }
        if (slipper.getRemainingTurns() == dis){
            return -INF * getKillingScore(beetle) * 0.5;
        }
        return 0;
    }

    public static void updateTimeRemaining(){
        if (timeRemaining == null){
            timeRemaining = new HashMap<>();
        }
        for (Cell c : game.getMap().getFoodCells()){
            Integer id = new Integer(c.getItem().getId());
            if (!timeRemaining.containsKey(id)){
                timeRemaining.put(id , game.getConstants().getFoodValidTime());
            }else{
                timeRemaining.put(id , timeRemaining.get(id) - 1);
            }
        }
    }

    public static void setDistances(){
        int height = game.getMap().getHeight();
        int width = game.getMap().getWidth();
        distances = new int[height][width][4][height][width][4];
        nodes = new Node[height][width][4];
        Node.setWidth(width);
        Node.setHeight(height);
        for (int rowSrc = 0;rowSrc < height; rowSrc++){
            for (int colSrc = 0; colSrc < width; colSrc++){
                for (int dir = 0;dir < 4;dir++){
                    nodes[rowSrc][colSrc][dir] = new Node(rowSrc , colSrc , Direction.values()[dir]);
                }
            }
        }
        for (int rowSrc = 0;rowSrc < height; rowSrc++){
            for (int colSrc = 0; colSrc < width; colSrc++){
                for (int dir = 0;dir < 4;dir++){
                    for (int i = 0;i < height;i++){
                        for (int j = 0; j < width; j++){
                            for (int k = 0;k < 4;k++){
                                if (Node.isNeig(nodes[rowSrc][colSrc][dir] , nodes[i][j][k])){
                                    nodes[rowSrc][colSrc][dir].addNeig(nodes[i][j][k]);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int rowSrc = 0;rowSrc < height; rowSrc++){
            for (int colSrc = 0; colSrc < width; colSrc++){
                for (int dir = 0;dir < 4;dir++){
                    Node.reset();
                    for (int i = 0;i < height;i++){
                        for (int j = 0; j < width; j++){
                            for (int k = 0;k < 4;k++){
                                nodes[i][j][k].resetIsVisited();
                            }
                        }
                    }
                    Node.queuePush(nodes[rowSrc][colSrc][dir]);

//                    if (nodes[rowSrc][colSrc][dir] == null){
//                        System.out.println("EXCEPTION : { ");
//                        System.out.println(rowSrc);
//                        System.out.println(" , ");
//                        System.out.println(colSrc);
//                        System.out.println(" , ");
//                        System.out.println(dir);
//                        System.out.println("}");
//                    }


                    while(!Node.isQueueEmpty()){
                        Node a = Node.pull();
                        a.bfs();
                        if (Node.queue.size() == 0){
                            Node.queue = Node.newQueue;
                            Node.newQueue = new LinkedList<>();
                            Node.level++;
                        }
//                        if(rowSrc == 0 && colSrc == 4 && dir == 2) {
//                            System.out.println("level:");
//                            System.out.println(Node.level);
//                            System.out.println("Queue:");
//                            for (Node n : Node.queue) {
//                                System.out.print("(");
//                                System.out.print(n.getRow());
//                                System.out.print(",");
//                                System.out.print(n.getColumn());
//                                System.out.print(",");
//                                System.out.print(n.getDirection().getValue());
//                                System.out.print(",");
//                                System.out.print(n.getIsVisited());
//                                System.out.println(")");
//                            }
//                            System.out.println("newQueue:");
//                            for (Node n : Node.newQueue) {
//                                System.out.print("(");
//                                System.out.print(n.getRow());
//                                System.out.print(",");
//                                System.out.print(n.getColumn());
//                                System.out.print(",");
//                                System.out.print(n.getDirection().getValue());
//                                System.out.print(",");
//                                System.out.print(n.getIsVisited());
//                                System.out.println(")");
//                            }
//                        }
                    }
                    for (int i = 0;i < height;i++){
                        for (int j = 0; j < width; j++){
                            for (int k = 0;k < 4;k++){
                                distances[rowSrc][colSrc][dir][i][j][k] = nodes[i][j][k].getLabel();
                            }
                        }
                    }
                }
            }
        }
//        for (int rowSrc = 0;rowSrc < height; rowSrc++){
//            for (int colSrc = 0; colSrc < width; colSrc++){
//                for (int dir = 0;dir < 4;dir++){
//                    for (int i = 0;i < height;i++){
//                        for (int j = 0; j < width; j++){
//                            for (int k = 0;k < 4;k++){
//                                if(!(i == 3 && j == 3 && colSrc == 4)) continue;
//                                System.out.print("{");
//                                System.out.print(rowSrc);
//                                System.out.print(",");
//                                System.out.print(colSrc);
//                                System.out.print(",");
//                                System.out.print(dir);
//                                System.out.print(",");
//                                System.out.print(i);
//                                System.out.print(",");
//                                System.out.print(j);
//                                System.out.print(",");
//                                System.out.print(k);
//                                System.out.print("\t-->");
//                                System.out.print(distances[rowSrc][colSrc][dir][i][j][k]);
//                                System.out.println("}");
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    public static int distance(int rowSrc , int colSrc , Direction dirSrc , int rowDest , int colDest){
//        int c = colSrc;
//        colSrc = rowSrc;
//        rowSrc = c;
//        c = colDest;
//        colDest = rowDest;
//        rowDest = c;
        int dist = distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][0];
        dist = Math.min(dist , distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][1]);
        dist = Math.min(dist , distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][2]);
        dist = Math.min(dist , distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][3]);
        return dist;
    }

    public static int distSlipper(int rowSrc , int colSrc , Direction dirSrc , int rowDest , int colDest){
        int height = game.getMap().getHeight();
        int width = game.getMap().getWidth();
        Boolean flag = false;
        for (int i = -1;i < 2;i++){
            for (int j = -1;j <2;j++){
                if (rowSrc == (rowDest + i + height) % height && colSrc == (colDest + j + width) % width){
                    flag = true;
                }
            }
        }
        if (!flag) return 0;
        int dist = 4;
        for (int j = -1;j < 2;j++){
            dist = Math.min(dist , distance(rowSrc , colSrc , dirSrc , (rowDest + j + height) % height , (colDest + 2) % width));
            dist = Math.min(dist , distance(rowSrc , colSrc , dirSrc , (rowDest + j + height) % height , (colDest + width - 2) % width));
            dist = Math.min(dist , distance(rowSrc , colSrc , dirSrc , (rowDest + 2) % height , (colDest + j + width) % width));
            dist = Math.min(dist , distance(rowSrc , colSrc , dirSrc , (rowDest + height - 2) % height , (colDest + j + width) % width));
        }
//        System.out.println("_!!_");
//        System.out.println(rowSrc);
//        System.out.println(colSrc);
//        System.out.println(dirSrc);
//        System.out.println(rowDest);
//        System.out.println(colDest);
//        System.out.println(dist);
        return dist;
    }

    public static CellState beetleState(Beetle beetle)
    {
        if(beetle == null)
            return CellState.Blank;
        //Todo:fix!
        else if(beetle.getTeam() == game.getTeamID())
            return CellState.Ally;
        return CellState.Enemy;
    }

    public static State cellState(Cell cell)
    {
        Beetle beetle = (Beetle) cell.getBeetle();
        Beetle X,Y,Z;
        //Todo: asK :)
        //TODO: Y has problem
        int n=game.getMap().getHeight(), m=game.getMap().getWidth();
        int i;
        switch (beetle.getDirection()) {

            case Right:
                X = (Beetle) cells[(cell.getX()+n-1)%n][(cell.getY()+1)%m].getBeetle();
                Y = (Beetle) cells[cell.getX()][(cell.getY()+1)%m].getBeetle();
                Z = (Beetle) cells[(cell.getX()+1)%n][(cell.getY()+1)%m].getBeetle();
                i = 1;
                do{
                    Y = (Beetle) cells[cell.getX()][(cell.getY()+i)%m].getBeetle();
                    i++;
                }while(Y == null);
                break;
            case Left:
                X = (Beetle) cells[(cell.getX()+1)%n][(cell.getY()+m-1)%m].getBeetle();
                Y = (Beetle) cells[cell.getX()][(cell.getY()+m-1)%m].getBeetle();
                Z = (Beetle) cells[(cell.getX()+n-1)%n][(cell.getY()+m-1)%m].getBeetle();
                i = 1;
                do{
                    Y = (Beetle) cells[cell.getX()][(cell.getY()+m-i)%m].getBeetle();
                    i++;
                }while(Y == null);
                break;
            case Up:
                X = (Beetle) cells[(cell.getX()+n-1)%n][(cell.getY()+m-1)%m].getBeetle();
                Y = (Beetle) cells[(cell.getX()+n-1)%n][cell.getY()].getBeetle();
                Z = (Beetle) cells[(cell.getX()+n-1)%n][(cell.getY()+1)%m].getBeetle();
                i = 1;
                do{
                    Y = (Beetle) cells[(cell.getX()+n-i)%n][cell.getY()].getBeetle();
                    i++;
                }while(Y == null);
                break;
            default:
                X = (Beetle) cells[(cell.getX()+1)%n][(cell.getY()+1)%m].getBeetle();
                Y = (Beetle) cells[(cell.getX()+1)%n][cell.getY()].getBeetle();
                Z = (Beetle) cells[(cell.getX()+1)%n][(cell.getY()+m-1)%m].getBeetle();
                i = 1;
                do{
                    Y = (Beetle) cells[(cell.getX()+i)%n][cell.getY()].getBeetle();
                    i++;
                }while(Y == null);
                break;
        }
        if (beetle != changingBeetle) {
            return new State(beetle.getBeetleType(), beetleState(X), beetleState(Y), beetleState(Z));
        }
        return new State(BeetleType.values()[(beetle.getBeetleType().getValue() + 1)%2], beetleState(X), beetleState(Y), beetleState(Z));
    }


    public static double stateScore(State state, Move move)
    {
//        System.out.println("(");
        double ret = 0;
        Cell[] myCell = game.getMap().getMyCells();
        for(int i=0;i<myCell.length;i++){
            if (myCell[i] == null) continue;
            if(cellState(myCell[i]).compareTo(state) == 0)
            {
//                System.out.println(calBeetleScore((Beetle) ((myCell[i]).getBeetle()), move));
                ret += calBeetleScore((Beetle) ((myCell[i]).getBeetle()), move);
            }
        }
//        System.out.println(")");
        return ret;
    }


    public void doTurn(World game) {
//        try {
            double epsilon = game.getConstants().getUpdateCost();
            AI.game = game;
            // fill this method, we've presented a stupid AI for example!
            System.out.println();
            System.out.println(game.getCurrentTurn());
            AI.updateTimeRemaining();
            cells = game.getMap().getCells();

        /*strategies = new int[2][3][2][3];

        for(int i = 0;i < 2;i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 2; k++) {
                    for (int l = 0; l < 3; l++) {
                        strategies[i][j][k][l] = -1;
                        if (i == 0 && l == 2 && k == 1 && j == 2 ){
                            AI.myChangeStrategy(BeetleType.LOW , CellState.Blank , CellState.Enemy , CellState.Blank , Move.turnRight);
                        }
                        else {
                            AI.myChangeStrategy(BeetleType.values()[i], CellState.values()[j], CellState.values()[k], CellState.values()[l], Move.stepForward);
                        }
                    }
                }
            }
        }*/


            if (strategies == null) {
                strategies = new int[2][3][2][3];
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        for (int k = 0; k < 2; k++) {
                            for (int l = 0; l < 3; l++) {
                                strategies[i][j][k][l] = -1;
                                game.changeStrategy(BeetleType.values()[i], CellState.values()[j], CellState.values()[k], CellState.values()[l], Move.stepForward);
                            }
                        }
                    }
                }
                setDistances();
                isDone = true;
                System.out.println("!3");
                System.out.println("done");
            }

            if (!isDone) {
                return;
            }


//        for (Cell c : game.getMap().getMyCells()){
//            if (c == null) continue;
//            System.out.println("!");
//            System.out.println(((Beetle)c.getBeetle()).getPosition().getX());
//            System.out.println(((Beetle)c.getBeetle()).getPosition().getY());
//        }

//        for (Cell c : game.getMap().getSlipperCells()){
//            if (c == null) continue;
//            System.out.print("Slipper : ");
//            System.out.print(c.getSlipper().getPosition().getX());
//            System.out.print(" ");
//            System.out.print(c.getSlipper().getPosition().getY());
//            System.out.println();
//        }

            State[] states = new State[36];
            for (int i = 0; i < 36; i++)
                states[i] = new State(i / 18, (i / 6) % 3, (i / 3) % 2, i % 3);
/*        Arrays.sort(states, new StatesComparator());



        Move[] s = new Move[36];
        Move[] bestMoves = new Move[36];
        double beetleMAX = -10000000;
        Beetle bestChange = null;
        double bsum = 0;
        changingBeetle = null;
        for (int i = 0; i < 36; i++) {
            Move bestMove = Move.values()[1];
            double MAX = stateScore(states[i], Move.values()[1]);
            //Todo: effect previous moves
            for (int j = 0; j < 3; j++) {
                double tmp = stateScore(states[i], Move.values()[j]);
                System.out.print("\t : {");
                System.out.print(Move.values()[j]);
                System.out.print(" : ");
                System.out.print(tmp);
                System.out.print("}");
                int a = 0;
                if (tmp > MAX + epsilon) {
                    MAX = tmp;
                    bestMove = Move.values()[j];
                }
            }
            System.out.print("\t  ");
            System.out.print(i);
            System.out.print(" : ");
            System.out.print(states[i].type);
            System.out.print(" (");
            System.out.print(states[i].X);
            System.out.print(" , ");
            System.out.print(states[i].Y);
            System.out.print(" , ");
            System.out.print(states[i].Z);
            System.out.print(") : ");
            System.out.print(bestMove);
            System.out.print(" -->");
            System.out.print(MAX);
            bsum += MAX;
            bestMoves[i] = bestMove;
        }
        beetleMAX = bsum;
        System.out.println();
        System.out.print("# ");
        System.out.println(beetleMAX);

        for (Cell c : game.getMap().getMyCells()) {
            bsum = 0;
            if (c == null) continue;
            changingBeetle = (Beetle) c.getBeetle();
            for (int i = 0; i < 36; i++) {
                Move bestMove = Move.values()[1];
                double MAX = stateScore(states[i], Move.values()[1]);
                //Todo: effect previous moves
                for (int j = 0; j < 3; j++) {
                    double tmp = stateScore(states[i], Move.values()[j]);
                    System.out.print("\t : {");
                    System.out.print(Move.values()[j]);
                    System.out.print(" : ");
                    System.out.print(tmp);
                    System.out.print("}");
                    if (tmp > MAX + epsilon) {
                        MAX = tmp;
                        bestMove = Move.values()[j];
                    }
                }
                System.out.print("\t  ");
                System.out.print(i);
                System.out.print(" : ");
                System.out.print(states[i].type);
                System.out.print(" (");
                System.out.print(states[i].X);
                System.out.print(" , ");
                System.out.print(states[i].Y);
                System.out.print(" , ");
                System.out.print(states[i].Z);
                System.out.print(") : ");
                System.out.print(bestMove);
                System.out.print(" -->");
                System.out.print(MAX);
                bsum += MAX;
                s[i] = bestMove;
//                AI.myChangeStrategy(states[i].type, states[i].X, states[i].Y, states[i].Z, bestMove);
            }
            System.out.println();
            System.out.print("#: (");
            System.out.print(changingBeetle.getPosition().getX());
            System.out.print(" , ");
            System.out.print(changingBeetle.getPosition().getY());
            System.out.print(") : ");
            System.out.print(bsum);
            System.out.print(" ? ");
            System.out.println(beetleMAX);
            if (bsum > beetleMAX + epsilon){
                beetleMAX = bsum;
                bestChange = changingBeetle;
                for (int j = 0;j < 36;j++){
                    bestMoves[j] = s[j];
                }
            }
        }
//        System.out.println("#");
//        System.out.println(beetleMAX);
        changingBeetle = bestChange;
        if (bestChange != null) {
            game.changeType(bestChange, BeetleType.values()[(bestChange.getBeetleType().getValue() + 1) % 2]);
        }
        for (int i = 0;i < 36;i++){
            if (states[i].num() == 0)continue;
            if (bestMoves[i] != null) {
                AI.myChangeStrategy(states[i].type, states[i].X, states[i].Y, states[i].Z, bestMoves[i]);
            }
        }
*/


            HashMap<Integer, Double>[] moveScore = new HashMap[3];
            moveScore[0] = new HashMap<>();
            moveScore[1] = new HashMap<>();
            moveScore[2] = new HashMap<>();

            for (Cell c : game.getMap().getMyCells()) {
                if (c == null) continue;
                Beetle b = (Beetle) c.getBeetle();
            System.out.print("( ");
            System.out.print(b.getPosition().getX());
            System.out.print(" , ");
            System.out.print(b.getPosition().getY());
            System.out.print(") : ");
                for (int j = 0; j < 3; j++) {
                    moveScore[j].put(new Integer(b.getId()), new Double(calBeetleScore(b, Move.values()[j])));
                System.out.print("\t");
                System.out.print(Move.values()[j]);
                System.out.print(" -> ");
                System.out.print(moveScore[j].get(b.getId()));
                }
            System.out.println();


            }
            double maxScore = -100000000;
            int[] bestJ = new int[18];
            int[] bestK = new int[18];
            for (int i = 0; i < 18; i++) {

                maxScore = 0;
                int minChange = 0;
                for (Cell c : game.getMap().getMyCells()) {
                    if (c == null) continue;
                    Beetle b = (Beetle) c.getBeetle();
                    if (states[i].compareTo(cellState(c)) == 0 || states[i + 18].compareTo(cellState(c)) == 0) {
                        double s1 = moveScore[1].get(b.getId());
                        double s2 = moveScore[1].get(b.getId());
                        maxScore += Math.max(s1, s2);
                        if (states[i].compareTo(cellState(c)) == 0 && s2 > s1 + epsilon) {
                            minChange++;
                        } else if (states[i + 18].compareTo(cellState(c)) == 0 && s1 > s2 + epsilon) {
                            minChange++;
                        }

                    }
                }
                bestJ[i] = 1;
                bestK[i] = 1;


                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        double score = 0;
                        int change = 0;
                        for (Cell c : game.getMap().getMyCells()) {
                            if (c == null) continue;
                            Beetle b = (Beetle) c.getBeetle();
                            if (states[i].compareTo(cellState(c)) == 0 || states[i + 18].compareTo(cellState(c)) == 0) {
                                double s1 = moveScore[j].get(b.getId());
                                double s2 = moveScore[k].get(b.getId());

                                if (s1 > s2 + epsilon){
                                    score += s1;
                                }else if(s2 > s1 + epsilon){
                                    score += s2;
                                }else{
                                    if (states[i].compareTo(cellState(c)) == 0){
                                        score += s1;
                                    }else if (states[i + 18].compareTo(cellState(c)) == 0){
                                        score += s2;
                                    }
                                }


                                if (states[i].compareTo(cellState(c)) == 0 && s2 > s1 + epsilon) {
                                    change++;
                                } else if (states[i + 18].compareTo(cellState(c)) == 0 && s1 > s2 + epsilon) {
                                    change++;
                                }
                            }
                        }
                        score -= change * game.getConstants().getColorCost();
                        if (j == strategies[0][states[i].X.getValue()][states[i].Y.getValue()][states[i].Z.getValue()]) score += game.getConstants().getUpdateCost();
                        if (k == strategies[1][states[i].X.getValue()][states[i].Y.getValue()][states[i].Z.getValue()]) score += game.getConstants().getUpdateCost();
                        if (score > maxScore + epsilon) {
                            maxScore = score;
                            bestJ[i] = j;
                            bestK[i] = k;
                            minChange = change;
                        } else if (score < maxScore + epsilon && score > maxScore - epsilon) {
                            if (bestJ[i] == 1 && bestK[i] == 1) continue;
                            if ((j == 1 || k == 1) && bestJ[i] != 1 && bestK[i] != 1) {
                                maxScore = score;
                                bestJ[i] = j;
                                bestK[i] = k;
                                minChange = change;
                            } else if (j == 1 || k == 1) {
                                if (change < minChange) {
                                    maxScore = score;
                                    bestJ[i] = j;
                                    bestK[i] = k;
                                    minChange = change;
                                }
                            }
                        }
                    }
                }
            }


        System.out.println("______");
        for (int i = 0;i < 18;i++){
            System.out.print(bestJ[i]);
            System.out.print(" , ");
        }
        System.out.println();
        for (int i = 0;i < 18;i++){
            System.out.print(bestK[i]);
            System.out.print(" , ");
        }
        System.out.println();


        Boolean[] hasState = new Boolean[36];
        for (int i = 0;i < 36;i++){
            hasState[i] = false;
        }

            for (int i = 0; i < 18; i++) {
                for (Cell c : game.getMap().getMyCells()) {
                    if (c == null) continue;
                    if (states[i].compareTo(cellState(c)) == 0 || states[i + 18].compareTo(cellState(c)) == 0) {
                        Beetle b = (Beetle) c.getBeetle();
                        double s1 = moveScore[bestJ[i]].get(b.getId());
                        double s2 = moveScore[bestK[i]].get(b.getId());
                        BeetleType finalType = b.getBeetleType();
                        if (s1 > s2 + epsilon) {
                            if (b.getBeetleType().getValue() == 1) {
                                game.changeType(b, BeetleType.values()[0]);
                                finalType = BeetleType.values()[0];
                            }
                        } else if (s2 > s1 + epsilon) {
                            if (b.getBeetleType().getValue() == 0) {
                                game.changeType(b, BeetleType.values()[1]);
                                finalType = BeetleType.values()[1];
                            }
                        } else {
                            /*if (bestJ[i] == 1 && bestK[i] != 1) {
                                if (b.getBeetleType().getValue() == 1) {
                                    game.changeType(b, BeetleType.values()[0]);
                                    finalType = BeetleType.values()[0];
                                }
                            } else if (bestJ[i] != 1 && bestK[i] == 1) {
                                if (b.getBeetleType().getValue() == 0) {
                                    game.changeType(b, BeetleType.values()[1]);
                                    finalType = BeetleType.values()[1];
                                }
                            }*/
                        }
                        hasState[finalType.getValue() * 18 + i] = true;
                    }
                }
            }

            for (int i = 0; i < 18; i++) {
                if (!hasState[i])continue;
                AI.myChangeStrategy(BeetleType.values()[0], states[i].X, states[i].Y, states[i].Z, Move.values()[bestJ[i]]);
            }

            for (int i = 0; i < 18; i++) {
                if (!hasState[i + 18])continue;
                AI.myChangeStrategy(BeetleType.values()[1], states[i].X, states[i].Y, states[i].Z, Move.values()[bestK[i]]);
            }


//        System.out.println(game.getMyScore());
//        }catch (Exception ignored){}
    }

}

class Node{
    private int row;
    private int col;
    private Direction dir;
    private ArrayList<Node> negs;
    public static LinkedList<Node> queue;
    public static LinkedList<Node> newQueue;
    public static int level;
    private Boolean isVisited;
    private int lbl;
    private static int width;
    private static int height;

    Node(int row , int col , Direction dir){
        this.row = row;
        this.col = col;
        this.dir = dir;
        negs = new ArrayList<Node>();
    }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.col;
    }

    public Direction getDirection(){
        return this.dir;
    }

    public int getLabel(){
        return this.lbl;
    }

    public Boolean getIsVisited() {return this.isVisited;}

    public void resetIsVisited() {this.isVisited = false;}

    public static void setWidth(int width) {Node.width = width;}

    public static void setHeight(int height) {Node.height = height;}

    public static void reset(){
        queue = new LinkedList<>();
        newQueue = new LinkedList<>();
        level = 0;
    }

    public void bfs(){
        if (isVisited) {
            return ;
        }
        isVisited = true;
        lbl = level;
        for (Node neg : negs) {
//            if (neg == null){
//                System.out.println("EXCEPTION : { ");
//                System.out.println(row);
//                System.out.println(" , ");
//                System.out.println(col);
//                System.out.println(" , ");
//                System.out.println(dir);
//                System.out.println("}");
//            }
            if (!neg.getIsVisited()){
                newQueue.push(neg);

            }
        }
    }

    public static Boolean isNeig(Node a , Node b){ //can move from a to b
        if (a.getRow() == b.getRow() && a.getColumn() == b.getColumn()){
            return (a.getDirection().getValue() + b.getDirection().getValue()) %2 == 1;
        }
        if (a.getDirection() == Direction.Down && b.getDirection() == Direction.Down){
            return  (a.getRow() == (b.getRow() + Node.height - 1) % Node.height && a.getColumn() == b.getColumn());
        }
        if (a.getDirection() == Direction.Up && b.getDirection() == Direction.Up){
            return  (a.getRow() == (b.getRow() + 1) % Node.height && a.getColumn() == b.getColumn());
        }
        if (a.getDirection() == Direction.Left && b.getDirection() == Direction.Left){
            return  (a.getRow() == (b.getRow()) && a.getColumn() == (b.getColumn() + 1) % Node.width);
        }
        if (a.getDirection() == Direction.Right && b.getDirection() == Direction.Right){
            return  (a.getRow() == (b.getRow()) && a.getColumn() == (b.getColumn() + Node.width - 1) % Node.width);
        }
        return false;
    }

    public void addNeig(Node a){
        this.negs.add(a);
    }

    public static Boolean isQueueEmpty(){return queue.isEmpty();}

    public static void queuePush(Node a){
        queue.push(a);
    }

    public static Node pull(){
        return queue.removeFirst();
    }
}

class State{
    public CellState X, Y, Z;
    public BeetleType type;

    public State(BeetleType type, CellState X, CellState Y, CellState Z){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.type = type;
    }
    public State(int type, int X, int Y, int Z){
        this.X = CellState.values()[X];
        this.Y = CellState.values()[Y];
        this.Z = CellState.values()[Z];
        this.type = BeetleType.values()[type];
    }

    public int value(){
        World game = AI.getGame();
        int ret = 0;
        for (Cell c : game.getMap().getMyCells()){
            if (c == null) continue;
            if (this.compareTo(AI.cellState(c)) == 0){
                int p = ((Beetle)(c.getBeetle())).getPower();
                ret += p * p;
            }
        }
        return ret;
    }

    public int num(){
        World game = AI.getGame();
        int ret = 0;
        for (Cell c : game.getMap().getMyCells()){
            if (c == null) continue;
            if (this.compareTo(AI.cellState(c)) == 0){;
                ret ++;
            }
        }
        return ret;
    }


    public int compareTo(State state)
    {
        if(X.getValue() == state.X.getValue() && Y.getValue() == state.Y.getValue() && Z.getValue() == state.Z.getValue() && type.getValue() == state.type.getValue())
            return 0;
        else
            return 1;
    }
}

class StatesComparator implements Comparator<State>{
    @Override
    public int compare(State s1, State s2)
    {
        return s2.value() - s1.value();
    }
}
