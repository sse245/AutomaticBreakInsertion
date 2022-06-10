package io.github.sse245.bachelorproject.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Vertex {

    private final Collection<Vertex> edges = new HashSet<>();

    private final String name;

    public Vertex(String name) {
        this.name = name;
    }

    public Collection<? extends Vertex> getPointingTo() {
        return Collections.unmodifiableCollection(this.edges);
    }

    public void addEdge(Vertex target) {
        edges.add(target);
    }

    public String getName() {
        return this.name;
    }
}
