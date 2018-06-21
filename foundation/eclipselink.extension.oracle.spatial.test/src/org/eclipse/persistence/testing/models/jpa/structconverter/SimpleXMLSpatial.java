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
//     gpelletie - Feb 19 2008, Testing Spatial Config in ORM xml.
package org.eclipse.persistence.testing.models.jpa.structconverter;

import oracle.spatial.geometry.JGeometry;

public class SimpleXMLSpatial {
    private long id;
    private JGeometry geometry;

    public SimpleXMLSpatial() {}

    public SimpleXMLSpatial(long id, JGeometry geometry) {
        this.id = id;
        this.geometry = geometry;
    }

    public long getId() {
        return id;
    }

    public JGeometry getJGeometry() {
        return geometry;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setJGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    public String toString() {
        return "SimpleXMLSpatial(" + getId() + ", " + getJGeometry() + "))";
    }
}
