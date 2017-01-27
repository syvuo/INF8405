package com.example.sly.a8405_tp1.model;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game {
    private static Game singletonInstance = new Game();
    private static boolean isStarted = false;


    private Game(){}

    public static void setIsStarted(boolean value) {isStarted = value;}



}
