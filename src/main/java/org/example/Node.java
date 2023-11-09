package org.example;

import java.util.ArrayList;
import java.util.List;

class Node {
    List<Point> points;
    List<Node> children;
    Rectangle boundingBox;

    public Node(Rectangle boundingBox) {
        this.points = new ArrayList<>();
        this.children = new ArrayList<>();
        this.boundingBox = boundingBox;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}