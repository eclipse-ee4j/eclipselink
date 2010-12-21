/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;

import java.io.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.testing.models.inheritance.SalesRep;
import org.eclipse.persistence.testing.models.inheritance.SoftwareEngineer;
import org.eclipse.persistence.testing.models.inheritance.Engineer;
import org.eclipse.persistence.testing.models.inheritance.Car;

/**
 * This tests;
 * <ul>
 * <li> the init problem
 * <li> class name indicator usage
 * <li> concreate root class
 * <li> big int as primary key
 */
public class Person implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface car;
    public Engineer bestFriend;
    public SalesRep representitive;

    public Person() {
        car = new ValueHolder();
    }

    public static Person example1() {
        Person example = new Person();

        example.setName("Raymen");
        example.getCar().setValue(Car.example1());
        return example;
    }

    public static Engineer example2() {
        Engineer example = new Engineer();

        example.setName("Steve");
        example.getCar().setValue(Car.example2());
        example.bestFriend = example5();
        ((SoftwareEngineer)example.bestFriend).boss = example;
        example.representitive = example4();
        return example;
    }

    public static SalesRep example3() {
        SalesRep example = new SalesRep();

        example.setName("Richard");
        example.getCar().setValue(Car.example3());
        return example;
    }

    public static SalesRep example4() {
        SalesRep example = new SalesRep();

        example.setName("Biff");
        example.getCar().setValue(org.eclipse.persistence.testing.models.inheritance.SportsCar.example1());
        return example;
    }

    public static SoftwareEngineer example5() {
        SoftwareEngineer example = new SoftwareEngineer();

        example.setName("Jenny");
        return example;
    }

    public static Person example6() {
        Person example = new Person();

        example.setName("Brendan");
        example.getCar().setValue(Car.example4());
        return example;
    }

    public ValueHolderInterface getCar() {
        return car;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String toString() {
        return this.name;
    }
}
