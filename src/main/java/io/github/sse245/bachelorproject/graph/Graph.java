package io.github.sse245.bachelorproject.graph;

import java.util.Collection;
import java.util.HashSet;

public class Graph {

    private final Collection<Vertex> vertices = new HashSet<>();

    private boolean hasInfiniteWalk(Vertex source, Collection<? super Vertex> seen) {
        if (!seen.add(source)) {
            return true;
        }

        boolean infiniteWalk = false;

        for (Vertex vertex : source.getPointingTo()) {
            infiniteWalk |= hasInfiniteWalk(vertex, seen);
        }

        return infiniteWalk;
    }

    public Vertex getVertex(String name) {
        for (Vertex vertex : this.vertices) {
            if (vertex.getName().equals(name)) {
                return vertex;
            }
        }

        return null;
    }

    public Vertex addVertex(String name) {
        Vertex vertex = new Vertex(name);

        this.vertices.add(vertex);

        return vertex;
    }

    public boolean hasInfiniteWalk(Vertex source) {
        return hasInfiniteWalk(source, new HashSet<>());
    }

    public Collection<? extends Vertex> getAllVertices(Vertex source) {
        Collection<Vertex> vertices = new HashSet<>();

        vertices.add(source);

        for (Vertex vertex : source.getPointingTo()) {
            vertices.addAll(getAllVertices(vertex));
        }

        return vertices;
    }
}
