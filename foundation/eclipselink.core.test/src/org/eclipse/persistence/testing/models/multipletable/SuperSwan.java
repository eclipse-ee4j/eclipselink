/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

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
