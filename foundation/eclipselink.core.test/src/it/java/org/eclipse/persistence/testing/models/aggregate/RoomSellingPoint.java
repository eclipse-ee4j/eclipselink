/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.aggregate;

public class RoomSellingPoint extends SellingPoint {
    private int squareFeet;

    public int getSquareFeet() {
        return squareFeet;
    }

    public void setSquareFeet(int size) {
        squareFeet = size;
    }

    public static SellingPoint example1() {
        SellingPoint example1 = new RoomSellingPoint();

        example1.setArea("back yard");
        example1.setDescription("Large open space with beautiful lawn.");
        return example1;
    }

    public static SellingPoint example2() {
        SellingPoint example2 = new RoomSellingPoint();

        example2.setArea("living room");
        example2.setDescription("Antique room with oak floors and ten foot ceiling.");

        return example2;
    }

    public static SellingPoint example3() {
        SellingPoint example3 = new RoomSellingPoint();

        example3.setArea("master bedroom");
        example3.setDescription("Luxury room with red carpet and gold wallpaper.");

        return example3;
    }

    public static RoomSellingPoint example4() {
        RoomSellingPoint example4 = new RoomSellingPoint();

        example4.setArea("master bedroom");
        example4.setDescription("Luxury room with red carpet and gold wallpaper.");
        example4.setSquareFeet(200);
        return example4;
    }
}
