/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributor:
 *     Andrei Ilitchev - bug 248858: Problem with Multiple tables in EclipseLink.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

public class SuperSwan extends Swan {
    protected int speed;
    protected int wingSpan;

    public SuperSwan() {
        super();
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public int getWingSpan() {
        return wingSpan;
    }

    public void setWingSpan(int wingSpan) {
        this.wingSpan = wingSpan;
    }
    
    public static SuperSwan getSuperSwan1(){
        SuperSwan swan = new SuperSwan();
        swan.setAge(4);
        swan.setCygnetCount(2);
        swan.setName("Queen");
        swan.setWeight(3);
        swan.setSpeed(4);
        swan.setWingSpan(5);
        return swan;
    }
 }
