package com.golan.amit.magicfour;

public class MagicFourResult {

    String currentDate;
    int id;
    int seconds;
    int moves;
    String player;

    public MagicFourResult(int id, int seconds, int moves, String player, String currentDate) {
        this.currentDate = currentDate;
        this.id = id;
        this.seconds = seconds;
        this.moves = moves;
        this.player = player;
    }

    public MagicFourResult() {
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "MagicFourResult{" +
                "seconds=" + seconds +
                ", moves=" + moves +
                ", player=" + player +
                ", date=" + currentDate +
                '}';
    }
}
