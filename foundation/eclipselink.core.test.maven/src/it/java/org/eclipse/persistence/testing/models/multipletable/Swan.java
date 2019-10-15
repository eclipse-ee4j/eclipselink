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
 * A Swan object uses multiple table foreign key.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date May 28, 2007
 */
public class Swan {
    protected int id;
    protected String name;
    protected int cygnetCount;
    protected int age;
    protected int weight;

    public Swan() {
    }

    public int getCygnetCount() {
        return this.cygnetCount;
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

    public void setCygnetCount(int cygnetCount) {
        this.cygnetCount = cygnetCount;
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

    public static Swan getSwan1(){
        Swan swan = new Swan();
        swan.setAge(4);
        swan.setCygnetCount(2);
        swan.setName("Ace");
        swan.setWeight(3);
        return swan;
    }
}
