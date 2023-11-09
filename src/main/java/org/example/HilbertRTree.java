package org.example;

import java.util.ArrayList;
import java.util.List;
public class HilbertRTree {
    private Node root;
    private int maxChildren = 4; // Максимальное количество детей у узла

    public HilbertRTree(Rectangle boundingBox) {
        this.root = new Node(boundingBox);
    }

    public void insert(Point p) {
        insert(root, p);
    }

    private void insert(Node node, Point p) {
        if (node.isLeaf()) {
            node.points.add(p);
            if (node.points.size() > maxChildren) {
                splitNode(node);
            }
        } else {
            Node child = chooseSubtree(node, p);
            insert(child, p);
            adjustBoundingBox(node, p);
        }
    }

    private Node chooseSubtree(Node node, Point p) {
        Node bestChild = null;
        double minEnlargement = Double.MAX_VALUE;

        for (Node child : node.children) {
            Rectangle enlarged = new Rectangle(
                    Math.min(child.boundingBox.minX, p.x),
                    Math.min(child.boundingBox.minY, p.y),
                    Math.max(child.boundingBox.maxX, p.x),
                    Math.max(child.boundingBox.maxY, p.y)
            );
            double enlargement = enlarged.maxX - enlarged.minX + enlarged.maxY - enlarged.minY - child.boundingBox.maxX + child.boundingBox.minX - child.boundingBox.maxY + child.boundingBox.minY;
            if (enlargement < minEnlargement || (enlargement == minEnlargement && bestChild == null)) {
                bestChild = child;
                minEnlargement = enlargement;
            }
        }

        return bestChild != null ? bestChild : node.children.get(0);
    }

    private void splitNode(Node node) {
        if (node.points.size() <= maxChildren) {
            return;
        }

        int splitAxis = (node.boundingBox.maxX - node.boundingBox.minX > node.boundingBox.maxY - node.boundingBox.minY) ? 0 : 1;
        node.points.sort((p1, p2) -> Double.compare(p1.x, p2.x));
        int splitIndex = node.points.size() / 2;

        List<Point> leftPoints = node.points.subList(0, splitIndex);
        List<Point> rightPoints = node.points.subList(splitIndex, node.points.size());

        Rectangle leftBoundingBox = calculateBoundingBox(leftPoints);
        Rectangle rightBoundingBox = calculateBoundingBox(rightPoints);

        Node leftChild = new Node(leftBoundingBox);
        leftChild.points.addAll(leftPoints);
        Node rightChild = new Node(rightBoundingBox);
        rightChild.points.addAll(rightPoints);

        node.children.clear();
        node.children.add(leftChild);
        node.children.add(rightChild);

        if (node != root) {
            adjustBoundingBox(node, null);
        }
    }

    private void adjustBoundingBox(Node node, Point p) {
        while (node != null) {
            if (p != null) {
                node.boundingBox.minX = Math.min(node.boundingBox.minX, p.x);
                node.boundingBox.minY = Math.min(node.boundingBox.minY, p.y);
                node.boundingBox.maxX = Math.max(node.boundingBox.maxX, p.x);
                node.boundingBox.maxY = Math.max(node.boundingBox.maxY, p.y);
            } else {
                node.boundingBox = calculateBoundingBox(node.points);
            }

            node = node != root ? findParent(node) : null;
        }
    }

    private Node findParent(Node child) {
        return findParent(root, child);
    }

    private Node findParent(Node currentNode, Node child) {
        if (currentNode.isLeaf() || currentNode.children.contains(child)) {
            return currentNode;
        } else {
            for (Node node : currentNode.children) {
                if (node.boundingBox.intersects(child.boundingBox)) {
                    Node result = findParent(node, child);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    private Rectangle calculateBoundingBox(List<Point> points) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        return new Rectangle(minX, minY, maxX, maxY);
    }

    public List<Point> search(Rectangle queryRectangle) {
        List<Point> result = new ArrayList<>();
        search(root, queryRectangle, result);
        return result;
    }

    private void search(Node node, Rectangle queryRectangle, List<Point> result) {
        if (node.boundingBox.intersects(queryRectangle)) {
            if (node.isLeaf()) {
                for (Point p : node.points) {
                    if (queryRectangle.contains(p)) {
                        result.add(p);
                    }
                }
            } else {
                for (Node child : node.children) {
                    search(child, queryRectangle, result);
                }
            }
        }
    }
}
