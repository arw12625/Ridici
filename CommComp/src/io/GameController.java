/*
 * The MIT License
 *
 * Copyright 2017 Andrew_2.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io;

import java.util.HashMap;
import java.util.Map;
import net.java.games.input.*;

/**
 * An interface for obtaining physical game controller inputs through JInput
 *
 * @author Andrew_2
 */
public class GameController {

    private Controller controller;
    private Map<String, Component> components;

    public static final String GAMECUBE_NAME = "MAYFLASH GameCube Controller Adapter";

    /**
     * Construct a game controller from the given JInput controller Should use
     * static methods to obtain controller
     *
     * @param c the JInput controller
     */
    private GameController(Controller c) {
        this.controller = c;

        components = new HashMap<>();
        Component[] comps = c.getComponents();
        for (Component comp : comps) {
            components.put(comp.getName(), comp);
        }
    }

    /**
     * Update the polled values of the controller inputs
     *
     * @return if the poll was successful
     */
    public boolean update() {
        return controller.poll();
    }

    /**
     * Get the value of named controller component
     *
     * @param name the name of the desired controller component
     * @return
     */
    public float getValue(String name) {
        Component c = components.get(name);
        if (c != null) {
            return c.getPollData();
        } else {
            System.err.println("No such component: " + name);
            return 0;
        }
    }

    /**
     * Create a thread for updating a given game controller
     *
     * @param gc
     */
    public static void startGameControllerThread(GameController gc) {
        GameControllerThread gct = new GameControllerThread(gc);
        gct.start();

    }

    /**
     * A class for creating a thread for updating a given game controller
     */
    private static class GameControllerThread implements Runnable {

        private GameController gc;
        private int loopTime;
        private boolean running;
        private Thread thread;

        public static final int defaultLoopTime = 20;

        public GameControllerThread(GameController gc) {
            this(gc, defaultLoopTime);
        }

        public GameControllerThread(GameController gc, int loopTime) {
            this.gc = gc;
            this.loopTime = loopTime;
        }

        public void start() {
            running = true;
            thread = new Thread(this);
            thread.start();
        }

        public void stop() {
            running = false;
        }

        @Override
        public void run() {

            long last = System.currentTimeMillis();
            while (running) {
                long time = System.currentTimeMillis();
                if (time - last > loopTime) {
                    if (!gc.update()) {
                        System.err.println("GameController could not poll");
                    }
                    last = time;
                }
                Thread.yield();
            }
        }

    }

    /**
     * A utility function for obtaining the ith gamecube controller recognized
     * by JInput. As the Mayflash adapter used has only four ports, i should be
     * less than 4 unless multiple adapters are used.
     *
     * @param i the index of the desired game cube controller
     * @return the desired game controller for the specified game cube
     * controller
     */
    public static GameController getGameCubeController(int i) {

        if (i < 0 || i > 3) {
            System.err.println("There are only 4 gamecube controllers");
        }
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        /*
         run over controllers until the ith controller with the appropriate
         name is encountered
         */
        int j = 0;
        while (j < ca.length && i > -1) {
            if (ca[j].getName().equals(GAMECUBE_NAME)) {
                i--;
            }
            j++;
        }

        if (i == -1) {
            return new GameController(ca[j - 1]);
        } else {
            System.err.println("Gamecube controller not found.");
            return null;
        }

    }
}
