package org.example;

class Rectangle {
    double minX;
    double minY;
    double maxX;
    double maxY;

    public Rectangle(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean contains(Point p) {
        return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
    }

    public boolean intersects(Rectangle other) {
        return !(other.minX > maxX || other.maxX < minX || other.minY > maxY || other.maxY < minY);
    }
}
