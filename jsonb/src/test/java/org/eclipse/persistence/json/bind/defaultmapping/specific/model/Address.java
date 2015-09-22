package org.eclipse.persistence.json.bind.defaultmapping.specific.model;

/**
 * @author Roman Grigoriadi
 */
public class Address {
    private Street street;

    private String town;

    public Address() {
    }

    public Address(Street street, String town) {
        this.street = street;
        this.town = town;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
