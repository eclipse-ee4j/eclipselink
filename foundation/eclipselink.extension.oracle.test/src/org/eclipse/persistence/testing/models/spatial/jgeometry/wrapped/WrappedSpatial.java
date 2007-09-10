/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped;


import oracle.spatial.geometry.JGeometry;


public class WrappedSpatial implements Spatial{
    private long id;
    private MyGeometry geometry;

    public WrappedSpatial() {
    }

    public WrappedSpatial(long id, int geomId, JGeometry geometry) {
        setId(id);
        setGeometry(new MyGeometry(geomId, geometry));
    }

    public WrappedSpatial(long id, MyGeometry geometry) {
        setId(id);
        setGeometry(geometry);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setGeometry(MyGeometry geometry) {
        this.geometry = geometry;
        if (geometry != null){
            this.geometry.setOwner(this);
        }
    }

    public MyGeometry getGeometry() {
        return this.geometry;
    }

    public String toString() {
        return "WrappedSpatial(" + getId() + ", " + getGeometry() + "))";
    }

    public JGeometry getJGeometry() {
        if (getGeometry() != null) {
            return getGeometry().getGeometry();
        }
        return null;
    }
}
