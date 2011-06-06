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
 *     gpelletie - Feb 19 2008, Testing Spatial Config in ORM xml.
 ******************************************************************************/  
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
