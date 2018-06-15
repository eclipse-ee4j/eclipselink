/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Cubicle interface.
 *
 * Define behavior for Cubicle objects.
 *
 * @author        Rick Barkhouse
 * @since        08/15/2000 15:51:05
 */
public interface Cubicle {
    public Employee getEmployee();

    public Computer getComputer();

    public float getHeight();

    public int getID();

    public float getLength();

    public float getWidth();

    public void setEmployee(Employee value);

    public void setComputer(Computer value);

    public void setHeight(float value);

    public void setID(int value);

    public void setLength(float value);

    public void setWidth(float value);
}
