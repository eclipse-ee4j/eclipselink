package org.eclipse.persistence.testing.jaxb.schemagen.inheritance;

public class Mazda extends TransientCar {
	
    private String color;
    private final String make = "Mazda";

    public String duplicate;
    
    public String getMake() {
        return make;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}