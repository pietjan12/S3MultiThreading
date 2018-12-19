/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fun3kochfractalfx.FUN3KochFractalFX;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import timeutil.TimeStamp;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager implements Observer {
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.koch = new KochFractal(this);
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }
    
    public void changeLevel(int nxt) {
        koch.terminateThreads();
        edges.clear();
        koch.setLevel(nxt);
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        koch.generateEdges();

        application.setTextNrEdges("" + koch.getNrOfEdges());
    }
    
    public void drawEdges() {
        //initialize draw counter
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();

        for (Edge e : edges) {
            application.drawEdge(e);
        }

        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }

    @Override
    public void update(Observable o, Object arg) {
        //update calculation time
        tsCalc.setEnd("End calculating");
        application.setTextCalc(tsCalc.toString());

        //add received edges to list for rendering
        ArrayList<Edge> receivedEdges = (ArrayList<Edge>) arg;
        this.edges = receivedEdges;

        //draw the edges.
        drawEdges();
    }

    public List<ProgressBar> progressBars() {
        return application.getProgressBarList();
    }

    public List<Label> labels() {
        return application.getLabelList();
    }
}
