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
 * A cow object uses multiple table foreign key.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class Cow {
    protected int cowId;
    protected int calfCount;
    protected int calfCountId;
    protected String name;
    protected int age;
    protected int ageId;
    protected int weight;
    protected int weightId;

    public Cow() {
    }

    public int getCalfCount() {
        return this.calfCount;
    }

    public int getCalfCountId() {
        return calfCountId;
    }

    public int getCowId() {
        return this.cowId;
    }

    public int getAge() {
        return this.age;
    }

    public int getAgeId() {
        return ageId;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getWeightId() {
        return weightId;
    }

    public String getName() {
        return this.name;
    }

    public void setCalfCountId(int calfCountId) {
        this.calfCountId = calfCountId;
    }

    public void setCalfCount(int calfCount) {
        this.calfCount = calfCount;
    }

    public void setCowId(int cowId) {
        this.cowId = cowId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAgeId(int ageId) {
        this.ageId = ageId;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setWeightId(int weightId) {
        this.weightId = weightId;
    }

    public static Cow getCow1(){
        Cow cow = new Cow();
        cow.setAge(1);
        cow.setAgeId(112);
        cow.setCalfCount(2);
        cow.setCalfCountId(223);
        cow.setName("MooMam");
        cow.setWeight(432);
        cow.setWeightId(445);
        return cow;
    }
}
