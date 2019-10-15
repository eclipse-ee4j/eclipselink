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
package org.eclipse.persistence.testing.models.jpa.structconverter;

import javax.persistence.*;

import oracle.spatial.geometry.JGeometry;
import org.eclipse.persistence.annotations.StructConverter;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;

@Entity
@Table(name="JPA_JGEOMETRY")
@StructConverter(name="DummyType", converter="org.eclipse.persistence.testing.models.jpa.structconverter.DummyStructConverter")
public class SimpleSpatial implements Spatial {

    private long id;
    private JGeometry geometry;

    public SimpleSpatial() {
    }

    public SimpleSpatial(long id, JGeometry geometry) {
        this.id = id;
        this.geometry = geometry;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    public long getId() {
        return id;
    }

    public void setJGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    @Convert(value="JGeometry")
    @StructConverter(name="JGeometry", converter="JGEOMETRY")
    public JGeometry getJGeometry() {
        return geometry;
    }

    public String toString() {
        return "SimpleSpatial(" + getId() + ", " + getJGeometry() + "))";
    }
}
