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

    public Employee getEmployee() {
        return this.employee;
    }

    public Computer getComputer() {
        return this.computer;
    }

    public float getHeight() {
        return this.height;
    }

    public int getID() {
        return this.id;
    }

    public float getLength() {
        return this._length;
    }

    public float getWidth() {
        return this.width;
    }

    public void setComputer(Computer value) {
        this.computer = value;
    }

    public void setEmployee(Employee value) {
        this.employee = value;
    }

    public void setHeight(float value) {
        this.height = value;
    }

    public void setID(int value) {
        this.id = value;
    }

    public void setLength(float value) {
        this._length = value;
    }

    public void setWidth(float value) {
        this.width = value;
    }

    public String toString() {
        return "[Cubicle #" + getID() + "] " + getLength() + " x " + getWidth() + " x " + getHeight() + ", belongs to " + getEmployee();
    }
}
