/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Peter Boots
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochFractal {

    private int level = 1;      // The current level of the fractal
    private int nrOfEdges = 3;  // The number of edges in the current level of the fractal
    private KochManager manager;

    //Threads
    private Thread thread1;
    private Thread thread2;
    private Thread thread3;

    //Runnables
    private EdgeGenerator leftRunnable;
    private EdgeGenerator bottomRunnable;
    private EdgeGenerator rightRunnable;

    public KochFractal(KochManager manager) {
        this.manager = manager;
    }

    public void generateEdges() {
        leftRunnable = new EdgeGenerator(0f,nrOfEdges,0.5, 0.0, (1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, level);
        bottomRunnable = new EdgeGenerator(1f/3f,nrOfEdges,(1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, (1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, level);
        rightRunnable = new EdgeGenerator(2f/3f,nrOfEdges,(1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, 0.5, 0.0, level);

        thread1 = new Thread(leftRunnable);
        thread2 = new Thread(bottomRunnable);
        thread3 = new Thread(rightRunnable);

        thread1.start();
        thread2.start();
        thread3.start();

        try{
            thread1.join();
            thread2.join();
            thread3.join();

            manager.addEdges(leftRunnable.getEdges());
            manager.addEdges(bottomRunnable.getEdges());
            manager.addEdges(rightRunnable.getEdges());
        }catch(InterruptedException e) {

        }
    }

    public void terminateThreads() {
        if(thread1 != null && thread2 != null && thread3 != null) {
            thread1.interrupt();
            thread2.interrupt();
            thread3.interrupt();
        }
    }

    public void setLevel(int lvl) {
        level = lvl;
        nrOfEdges = (int) (3 * Math.pow(4, level - 1));
    }

    public int getLevel() {
        return level;
    }

    public int getNrOfEdges() {
        return nrOfEdges;
    }
}
