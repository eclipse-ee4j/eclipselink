/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Cubicle implementation.
 *
 * Implementation of the Cubicle interface.
 *
 * @author        Rick Barkhouse
 * @since        08/15/2000 15:51:05
 */
public class CubicleImpl implements Cubicle {
    public int id;
    public float _length;
    public float width;
    public float height;
    public Employee employee;
    public Computer computer;

    @Override
    public Employee getEmployee() {
        return this.employee;
    }

    @Override
    public Computer getComputer() {
        return this.computer;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public float getLength() {
        return this._length;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public void setComputer(Computer value) {
        this.computer = value;
    }

    @Override
    public void setEmployee(Employee value) {
        this.employee = value;
    }

    @Override
    public void setHeight(float value) {
        this.height = value;
    }

    @Override
    public void setID(int value) {
        this.id = value;
    }

    @Override
    public void setLength(float value) {
        this._length = value;
    }

    @Override
    public void setWidth(float value) {
        this.width = value;
    }

    public String toString() {
        return "[Cubicle #" + getID() + "] " + getLength() + " x " + getWidth() + " x " + getHeight() + ", belongs to " + getEmployee();
    }
}
