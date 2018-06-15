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

public class SuperCow extends Cow {
    protected int speed;

    public SuperCow() {
        super();
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public static SuperCow getSuperCow1(){
        SuperCow cow = new SuperCow();
        cow.setAge(1);
        cow.setAgeId(111);
        cow.setCalfCount(2);
        cow.setCalfCountId(222);
        cow.setName("CudChewer");
        cow.setWeight(432);
        cow.setWeightId(444);
        cow.setSpeed(10);
        return cow;
    }
 }
