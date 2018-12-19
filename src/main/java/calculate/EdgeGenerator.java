package calculate;

import javafx.concurrent.Task;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class EdgeGenerator extends Task<List<Edge>> {
    private float hue;
    private int nrOfEdges;
    private double ax;
    private double ay;
    private double bx;
    private double by;
    private int n;
    private ArrayList<Edge> edges;

    public EdgeGenerator(float hue, int nrOfEdges, double ax, double ay, double bx, double by, int n) {
        this.hue = hue;
        this.nrOfEdges = nrOfEdges;
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.n = n;
        this.edges = new ArrayList<>();
    }

    @Override
    protected List<Edge> call() throws Exception {
        drawKochEdge(this.ax, this.ay, this.bx, this.by, this.n);
        return this.edges;
    }

    private void drawKochEdge(double ax, double ay, double bx, double by, int n) {
        //check if executorservice has asked thread to stop.
        if(!Thread.interrupted()) {
            if (n == 1) {
                hue = hue + 1.0f / nrOfEdges;
                Edge e = new Edge(ax, ay, bx, by, Color.hsb(hue*360.0, 1.0, 1.0));
                //add edge to the list.
                edges.add(e);
            } else {
                double angle = Math.PI / 3.0 + Math.atan2(by - ay, bx - ax);
                double distabdiv3 = Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay)) / 3;
                double cx = Math.cos(angle) * distabdiv3 + (bx - ax) / 3 + ax;
                double cy = Math.sin(angle) * distabdiv3 + (by - ay) / 3 + ay;
                final double midabx = (bx - ax) / 3 + ax;
                final double midaby = (by - ay) / 3 + ay;
                drawKochEdge(ax, ay, midabx, midaby, n - 1);
                drawKochEdge(midabx, midaby, cx, cy, n - 1);
                drawKochEdge(cx, cy, (midabx + bx) / 2, (midaby + by) / 2, n - 1);
                drawKochEdge((midabx + bx) / 2, (midaby + by) / 2, bx, by, n - 1);
            }

            //update progress and message
            updateProgress(edges.size() ,(nrOfEdges / 3));
            updateMessage("Nr of edges : " + String.valueOf(edges.size()));
        }
    }
}
