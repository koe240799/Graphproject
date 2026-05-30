package at.spengergasse.service;

import java.util.ArrayList;
import java.util.List;

public class BFSResult {

    private final int[] distances;
    private final int[] previous;
    private List<BFSStep> steps;

    public BFSResult(int[] distances, int[] previous) {
        this.distances = distances;
        this.previous = previous;
    }

    public int[] getDistances() {
        return distances;
    }

    public int[] getPrevious() {
        return previous;
    }


    public List<BFSStep> getSteps() {
        return steps;
    }

    public void setSteps(List<BFSStep> steps) {
        this.steps = steps;
    }
}