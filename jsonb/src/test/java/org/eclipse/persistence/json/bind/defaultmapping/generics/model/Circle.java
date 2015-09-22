package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

/**
 * @author Roman Grigoriadi
 */
public class Circle extends Shape {
    public Double radius;

    public Circle() {
        super();
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
