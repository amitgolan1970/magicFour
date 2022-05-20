package com.golan.amit.magicfour;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MagicFourHelper {

    public static final int NUMARR = 4 * 4;
    int[] mgf;
    int[][] genTemplate;
    private int touches;
    private int moves;
    private int[] choices;
    public static final String DEFAULT_PLAYER = "Amit";
    private String player;
    private int bestSeconds, bestMoves;

    /**
     * Constructor
     */

    public MagicFourHelper() {

        this.player = DEFAULT_PLAYER;
        bestMoves = -1;
        bestSeconds = -1;
        choices = new int[4];

        genTemplate = new int[][]{
                {6, 9, 2, 3, 1, 7, 13, 11, 14, 15, -1, 4, 10, 5, 12, 8},
                {15, 7, 13, 6, 14, 1, 12, 2, 10, -1, 8, 3, 5, 4, 9, 11},
                {6, 14, 13, 2, 15, 1, 7, 3, 10, 9, 4, 11, 5, -1, 12, 8},
                {10, 15, 3, 14, 5, 6, -1, 13, 1, 11, 7, 2, 4, 9, 12, 8},
                {14, 6, 11, 4, 10, 9, 13, 2, 1, 3, 5, 8, 15, -1, 7, 12},
                {10, 13, 8, 2, -1, 14, 12, 11, 3, 6, 4, 7, 15, 1, 5, 9},
                {13, 10, -1, 6, 14, 9, 2, 11, 15, 1, 3, 4, 7, 5, 12, 8},
                {14, 13, -1, 11, 2, 9, 10, 6, 1, 12, 5, 4, 15, 7, 8, 3},
                {10, 13, 4, 8, 3, 12, 7, 2, 6, 1, 14, 11, 15, -1, 5, 9},
                {6, 3, 13, 7, 14, -1, 12, 4, 15, 10, 2, 8, 5, 1, 9, 11},
                {15, 3, 13, 14, 10, 1, 2, 8, 4, 5, 9, -1, 11, 6, 12, 7},
                {13, 7, 5, 11, 6, 3, 15, -1, 10, 9, 2, 4, 1, 14, 12, 8},
                {7, 15, 11, 9, 6, -1, 8, 3, 13, 2, 10, 4, 14, 5, 1, 12},

        };

        mgf = new int[NUMARR];
        for(int i = 1; i < mgf.length; i++) {
            mgf[i] = i;
        }
        mgf[0] = -1;
        this.touches = 0;
        this.moves = 0;
    }

    public void generateRandomChoicesSeq() {
        for(int i = 0; i < choices.length; i++) {
            choices[i] = i;
        }
        List<Integer> li = new ArrayList<>();
        for(int i = 0; i < choices.length; i++) {
            li.add(choices[i]);
        }
        Collections.shuffle(li);
        for(int i = 0; i < choices.length; i++) {
            this.choices[i] = li.get(i);
        }
    }

    public void displayChoices() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < choices.length; i++) {
            sb.append(choices[i]);
            if(i < (choices.length - 1))
                sb.append(",");
        }
        sb.append("]");
        Log.i(MainActivity.DEBUGTAG, sb.toString());
    }

    public int getChoiceByIndex(int ind) {
        if(ind < 0 || ind > (choices.length-1)) {
            return -1;
        }
        return choices[ind];
    }

    public int getChoicesLength() {
        return this.choices.length;
    }

    public void shuffle() {
        /*List<Integer> li = new ArrayList<>();
        for(int i = 0; i < mgf.length; i++) {
            li.add(mgf[i]);
        }
        Collections.shuffle(li);
        for(int i = 0; i < mgf.length; i++) {
            this.mgf[i] = li.get(i);
        }*/

        //  ordered (possitive win possibility):
        int rnd = (int)(Math.random() * genTemplate.length);
        mgf = genTemplate[rnd];
    }

    public void displayArray() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < mgf.length; i++) {
            sb.append(mgf[i]);
            if(i < (mgf.length - 1)) {
                sb.append(",");
            }
        }
        Log.i(MainActivity.DEBUGTAG, sb.toString());
    }

    public String getElementByIndex(int ind) {
        if(ind < 0 || ind > (mgf.length-1)) {
            return "Error";
        }
        String tmp;
        try {
            tmp = String.valueOf(mgf[ind]);
        } catch (Exception e) {
            return "Error";
        }
        return tmp;
    }

    public void increaseTouches() {
        this.touches++;
    }

    public void resetToches() {
        this.touches = 0;
    }

    public void increaseMoves() {
        this.moves++;
    }

    public void resetMoves() {
        this.moves = 0;
    }

    public int getTouches() {
        return touches;
    }

    public int getMoves() {
        return moves;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getBestSeconds() {
        return bestSeconds;
    }

    public void setBestSeconds(int bestSeconds) {
        this.bestSeconds = bestSeconds;
    }

    public int getBestMoves() {
        return bestMoves;
    }

    public void setBestMoves(int bestMoves) {
        this.bestMoves = bestMoves;
    }
}
