/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.multipletable;


/**
 * A horse object uses multiple table primary key.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class Horse {
    protected int id;
    protected String name;
    protected int foalCount;
    protected int age;
    protected int weight;

    public Horse() {
    }

    public int getFoalCount() {
        return this.foalCount;
    }

    public int getId() {
        return this.id;
    }


    public int getAge() {
        return this.age;
    }

    public int getWeight() {
        return this.weight;
    }

    public String getName() {
        return this.name;
    }

    public void setFoalCount(int foalCount) {
        this.foalCount = foalCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public static Horse getHorse1(){
        Horse horse = new Horse();
        horse.setAge(1);
        horse.setFoalCount(3);
        horse.setName("Mr. Fred");
        horse.setWeight(600);
        return horse;
    }
}
