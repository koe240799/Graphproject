package at.spengergasse.service;

import java.util.List;

public class BFSStep {

    private final int step;
    private final List<Integer> discovered;
    private final List<Integer> processed;
    private final String dependencies;

    public BFSStep(int step,
                   List<Integer> discovered,
                   List<Integer> processed,
                   String dependencies) {

        this.step = step;
        this.discovered = discovered;
        this.processed = processed;
        this.dependencies = dependencies;
    }

    public int getStep() {
        return step;
    }

    public List<Integer> getDiscovered() {
        return discovered;
    }

    public List<Integer> getProcessed() {
        return processed;
    }

    public String getDependencies() {
        return dependencies;
    }
}