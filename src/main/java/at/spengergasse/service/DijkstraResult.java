package at.spengergasse.service;

import java.util.ArrayList;
import java.util.List;

public class DijkstraResult {

    private final int[] dist;
    private final int[] prev;
    private final int start;

    public DijkstraResult(int[] dist, int[] prev, int start) {
        this.dist = dist;
        this.prev = prev;
        this.start = start;
    }

    public int[] getDistances() {
        return dist;
    }

    public int[] getPrevious() {
        return prev;
    }

    public List<Integer> getPathTo(int target) {

        List<Integer> path = new ArrayList<>();

        for (int at = target; at != -1; at = prev[at]) {
            path.add(0, at);
        }

        return path;
    }

    public int getStart() {
        return start;
    }
}