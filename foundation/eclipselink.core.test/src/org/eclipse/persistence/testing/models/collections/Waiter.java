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
package org.eclipse.persistence.testing.models.collections;

import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.testing.models.collections.Restaurant;

public class Waiter extends Person {
    private String specialty;
    private ValueHolderInterface employer = new ValueHolder();

    public Waiter() {
        super();
    }

    public static Waiter example1(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Andre");
        aWaiter.setLastName("LaNez");
        aWaiter.setSpecialty("Looking down his nose at you.");
        return aWaiter;
    }

    public static Waiter example2(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Rene");
        aWaiter.setLastName("Petit");
        aWaiter.setSpecialty("Making you feel small.");
        return aWaiter;
    }

    public static Waiter example3(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Jean");
        aWaiter.setLastName("Trebuche");
        aWaiter.setSpecialty("Spilling soup on you.");
        return aWaiter;
    }

    public static Waiter example4(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Juan");
        aWaiter.setLastName("Rodriguez");
        aWaiter.setSpecialty("Juggling torillas");
        return aWaiter;
    }

    public static Waiter example5(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Pedro");
        aWaiter.setLastName("Manendez");
        aWaiter.setSpecialty("Eating from your plate.");
        return aWaiter;
    }

    public static Waiter example6(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Manuel");
        aWaiter.setLastName("Perez");
        aWaiter.setSpecialty("Singing Aye... Aye... Aye,Aye,Aye.");
        return aWaiter;
    }

    public static Waiter example7(Restaurant aRestaurant) {
        Waiter aWaiter = new Waiter();
        aWaiter.setEmployer(aRestaurant);
        aWaiter.setFirstName("Wo");
        aWaiter.setLastName("Chuk Yow");
        aWaiter.setSpecialty("Politeness");
        return aWaiter;
    }

    public Restaurant getEmployer() {
        return (Restaurant)getEmployerHolder().getValue();
    }

    public ValueHolderInterface getEmployerHolder() {
        return employer;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setEmployer(Restaurant newValue) {
        propertyChange("employer", getEmployerHolder().getValue(), newValue);
        getEmployerHolder().setValue(newValue);
    }

    public void setEmployerHolder(ValueHolderInterface newValue) {
        this.employer = newValue;
    }

    public void setSpecialty(String newValue) {
        propertyChange("specialty", this.specialty, newValue);
        this.specialty = newValue;
    }
}
