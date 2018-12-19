/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Peter Boots
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochFractal extends Observable{
    private KochManager manager;
    private int counter = 0; // counter that gets incremented on completion of a task.
    private int level = 1;      // The current level of the fractal
    private int nrOfEdges = 3;  // The number of edges in the current level of the fractal

    private ExecutorService executorService;
    //List of java FX tasks
    private ArrayList<EdgeGenerator> edgeGenerators;
    //List of edges rendered so far, held by the KochFractal until all tasks are completed.
    private ArrayList<Edge> edges;

    public KochFractal(KochManager manager) {
        this.manager = manager;
        addObserver(manager);
        executorService = Executors.newFixedThreadPool(3);
        edgeGenerators = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /**
     * Creates the edgeGenerator tasks and sends them off to the executorservice
     */
    public void generateEdges() {
        //Reset values to their defaults
        edgeGenerators.clear();
        counter = 0;

        //fill list of Tasks with necessary parameters
        edgeGenerators.add(new EdgeGenerator(0f,nrOfEdges,0.5, 0.0, (1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, level));
        edgeGenerators.add(new EdgeGenerator(1f/3f,nrOfEdges,(1 - Math.sqrt(3.0) / 2.0) / 2, 0.75, (1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, level));
        edgeGenerators.add(new EdgeGenerator(2f/3f,nrOfEdges,(1 + Math.sqrt(3.0) / 2.0) / 2, 0.75, 0.5, 0.0, level));


        for(int i = 0; i < edgeGenerators.size(); i++) {
            EdgeGenerator e = edgeGenerators.get(i);
            //get matching UI elements and bind properties.
            manager.progressBars().get(i).progressProperty().bind(e.progressProperty());
            manager.labels().get(i).textProperty().bind(e.messageProperty());

            //create callback for javaFX task onsuccess
            e.setOnSucceeded(event -> addToManager(e.getValue()));
            //Add task to executorservice pool
            executorService.execute(e);
        }
    }

    /**
     * adds the received edges from an async task to the storage list.
     * @param receivedEdges
     */
    public void addToManager(List<Edge> receivedEdges) {
        counter++;
        edges.addAll(receivedEdges);

        if(counter == 3) {
            //all tasks have finished, send a notify to the observer
            setChanged();
            //notify observers with created edges.
            notifyObservers(this.edges);
        }

    }

    public void terminateThreads() {
        try{
            //shut down all running threads.
            executorService.shutdown();
            executorService.awaitTermination(3000, TimeUnit.MILLISECONDS);
            executorService = Executors.newFixedThreadPool(3);
        } catch(InterruptedException e) {
            System.out.println(e);
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
