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
package org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped;

import oracle.spatial.geometry.JGeometry;


public class MyGeometry {
    private Object owner;
    private int id;
    private JGeometry geometry;

    public MyGeometry() {
    }

    public MyGeometry(int id, JGeometry geometry) {
        this.id = id;
        this.geometry = geometry;
    }

    public void setGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    public JGeometry getGeometry() {
        return geometry;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public Object getOwner() {
        return owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "MyGeometry(" + getId() + ", " + getGeometry() + "))";
    }

}
