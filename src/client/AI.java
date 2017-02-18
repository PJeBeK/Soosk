package client;

import client.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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

    static World game;
    static int[][][][][][] distances;

    //calculates the score
    public static double calScore(Beetle beetle, Move move, Beetle beetle2){
        int INF = 1000;
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
            dis = distance(beetle.getRow(), beetle.getColumn(), newDir, beetle2.getRow(), beetle2.getColumn());
        }
        else if (move.getValue() == 1){
            switch (beetle.getDirection()){
                case Right:
                    dis = distance(beetle.getRow(), beetle.getColumn()+1, beetle.getDirection(), beetle2.getRow(), beetle2.getColumn());
                    break;
                case Left:
                    dis = distance(beetle.getRow(), beetle.getColumn()-1, beetle.getDirection(), beetle2.getRow(), beetle2.getColumn());
                    break;
                case Up:
                    dis = distance(beetle.getRow()-1, beetle.getColumn(), beetle.getDirection(), beetle2.getRow(), beetle2.getColumn());
                    break;
                case Down:
                    dis = distance(beetle.getRow()+1, beetle.getColumn(), beetle.getDirection(), beetle2.getRow(), beetle2.getColumn());
                    break;
            }
        }

        if(beetle.getPower() > 2 * beetle2.getPower()){
            if (dis == 0)
                return INF;
            return 1 / dis;
        }
        else if(beetle.getPower() > beetle2.getPower()){
            if (dis == 0)
                return INF;
            return (beetle.getPower() - beetle2.getPower()) / (beetle2.getPower() * dis);
        }
        else if(beetle.getPower() > 0.5 * beetle2.getPower()){
            if (dis == 0)
                return -INF;
            return (beetle.getPower() - beetle2.getPower()) / (beetle.getPower() * dis);
        }
        else{
            if (dis == 0)
                return -INF;
            return -1 / dis;
        }
    }

    public static double calFinalScore(Beetle beetle, Move move){
        Cell[] cells = game.getMap().getOppCells();
        double ans = 0;
        for (Cell c: cells) {
            Beetle b = (Beetle) c.getBeetleEntity();
            ans += calScore(beetle, move, b);
        }
        ans /= cells.length;
        return ans;
    }


    public static void setDistances(){
        int height = game.getMap().getHeight();
        int width = game.getMap().getWidth();
        distances = new int[height][width][4][height][width][4];
        Node[][][] nodes = new Node[height][width][4];
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
                    while(!Node.isQueueEmpty()){
                        Node a = Node.pull();
                        a.bfs();
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

    }

    public static int distance(int rowSrc , int colSrc , Direction dirSrc , int rowDest , int colDest){
        int dist = distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][0];
        dist = Math.min(dist , distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][1]);
        dist = Math.min(dist , distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][2]);
        dist = Math.min(dist , distances[rowSrc][colSrc][dirSrc.getValue()][rowDest][colDest][3]);
        return dist;
    }



    public void doTurn(World game) {
        AI.game = game;
        // fill this method, we've presented a stupid AI for example!
        System.out.println(game.getCurrentTurn());
        Random rand = new Random();


        Cell[][] cells = game.getMap().getCells();

        if (game.getCurrentTurn() == 0) {
            setDistances();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 3; k++) {
                        game.changeStrategy(BeetleType.LOW, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[1]);
                        game.changeStrategy(BeetleType.HIGH, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[1]);
                    }
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 3; k++) {
                    Random r = new Random();
                    int kk = r.nextInt() % 20;
                    if (kk >= 3) kk =1;
                    while(kk<0) kk+=3;
                    game.changeStrategy(BeetleType.LOW, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[kk]);
                    game.changeStrategy(BeetleType.HIGH, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[kk]);
                }
            }
        }
        }

    }

}

class Node{
    private int row;
    private int col;
    private Direction dir;
    private ArrayList<Node> negs;
    private static LinkedList<Node> queue;
    private static LinkedList<Node> newQueue;
    private static int level;
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
            if (!neg.getIsVisited()){
                newQueue.push(neg);
            }
        }
        if (queue.size() == 0){
            queue = newQueue;
            newQueue = new LinkedList<>();
            level++;
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
