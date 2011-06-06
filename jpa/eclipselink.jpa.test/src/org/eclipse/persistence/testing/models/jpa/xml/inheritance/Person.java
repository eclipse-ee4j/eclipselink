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


package org.eclipse.persistence.testing.models.jpa.xml.inheritance;

import java.io.*;

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
    public Car car;
    public Boat boat;
    public Engineer bestFriend;
    public Lawyer representitive;

    public Number getId() {
        return id;
    }

	public void setId(Number id) { 
        this.id = id; 
    }

	public Boat getBoat() {
        return boat;
    }
	
	public void setBoat(Boat boat) {
	    this.boat = boat;
	}
	
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public Engineer getBestFriend() {
        return bestFriend;
    }

    public void setBestFriend(Engineer friend) {
        bestFriend = friend;
    }

    public Lawyer getRepresentitive() {
        return representitive;
    }

    public void setRepresentitive(Lawyer representitive) {
        this.representitive = representitive;
    }

    public String toString() {
        return this.name;
    }
}
