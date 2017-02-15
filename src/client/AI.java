package client;

import client.model.*;

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

    //calculates the score
    public static void calScore(Beetle beetle, Move move, Beetle beetle2){

    }

    //this is useless
    public static int distance(Beetle beetle1, Beetle beetle2){
        return distance(beetle1, beetle2.getRow(), beetle2.getColumn());
    }

    //Todo: directions need to be taken into account
    public static int distance(Beetle beetle , int rowDest , int colDest){
        Map map = game.getMap();
        int minMov = 0;
        int colDef;
        int rowDef;

        int move;
        colDef = colDest - beetle.getColumn();
        rowDef = rowDest - beetle.getRow();
        if(colDef < 0){
            colDef += map.getWidth();
        }
        if(rowDef < 0){
            rowDef += map.getHeight();
        }

        return Math.min(rowDef, map.getHeight() - 1 - rowDef) + Math.min(colDef, map.getWidth() - 1 - colDef);

    }

    //Todo: has bugs for dis=0, can't use distance
    public static double score(Beetle a, Beetle b){
        if(a.getPower() > 2 * b.getPower()){
            return 1 / distance(a, b);
        }
        if(a.getPower() > b.getPower()){
            return (a.getPower() - b.getPower()) / (b.getPower() * distance(a , b));
        }
        if(a.getPower() > 0.5 * b.getPower()){
            return (a.getPower() - b.getPower()) / (a.getPower() * distance(a , b));
        }
        return -1 / distance(a , b);
    }


    public void doTurn(World game) {
        AI.game = game;
        // fill this method, we've presented a stupid AI for example!
        System.out.println(game.getCurrentTurn());
        Random rand = new Random();


        Cell[][] cells = game.getMap().getCells();

        if (game.getCurrentTurn() == 0) {
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
                    int kk = r.nextInt() % 3;
                    while(kk<0) kk+=3;
                    game.changeStrategy(BeetleType.LOW, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[kk]);
                    game.changeStrategy(BeetleType.HIGH, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[kk]);
                }
            }
        }
        }

    }

}
