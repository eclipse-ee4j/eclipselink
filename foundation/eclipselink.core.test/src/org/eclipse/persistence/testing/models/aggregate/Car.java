/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;


/**
 * This type was created in VisualAge.
 */
public class Car extends Vehicle {
    public String make;
    public String model;

    /**
     * Car constructor comment.
     */
    public Car() {
        super();
    }

    public static Car example1() {
        Car example = new Car();
        example.setMake("Volkswagen");
        example.setModel("Beetle");
        example.setColour("Rust & Primer");
        example.setCapacity(23);
        return example;
    }

    public static Car example2() {
        //		session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("INSERT INTO AGGVEHICLE VALUES (2, 1, 'Red', 4, NULL, 'Mazda', '323')"));
        Car example = new Car();
        example.setMake("Ford");
        example.setModel("Escort");
        example.setColour("Red");
        example.setCapacity(4);
        return example;
    }

    public static Car example3() {
        Car example = new Car();
        example.setMake("Mazda");
        example.setModel("323");
        example.setColour("Red");
        example.setCapacity(4);
        return example;
    }

	public static Car example4() {
        Car example = new Car();
        example.setMake("Toyata");
        example.setModel("Echo");
        example.setColour("Silver");
        example.setCapacity(4);
        example.setTransientValue("This is my transient Value");
        return example;
	}

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getMake() {
        return make;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getModel() {
        return model;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue java.lang.String
     */
    public void setMake(String newValue) {
        this.make = newValue;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue java.lang.String
     */
    public void setModel(String newValue) {
        this.model = newValue;
    }
}
