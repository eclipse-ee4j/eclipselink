/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/08/2010 Andrei Ilitchev 
 *       Bug 300512 - Add FUNCTION support to extended JPQL
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.jgeometry;

import java.util.List;
import java.util.Map;

import oracle.spatial.geometry.JGeometry;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleSpatial;

/**
 * Helper class to read and compare SimpleSpatial results read using SQL versus
 * those read using Eclipselink.
 * Extended the base class to support jpa.structconverter.SimpleSpatial
 * Note: fields we renamed from {GID, GEOMETRY} to {ID, JGEOMETRY}
 */
public class SQLReader extends org.eclipse.persistence.testing.tests.spatial.jgeometry.SQLReader {

    public SQLReader(Session session, String sql) {
        super(session, sql);
    }

    public SQLReader(Session session, String sql, List<String> argumentNames, List argumentValues) {
        super(session, sql, argumentNames, argumentValues);
    }
    
    protected SimpleSpatial createSpatial(Map rawResult) {
        long gid = ((Number)rawResult.get("ID")).longValue();
        Object geom = rawResult.get("JGEOMETRY");            
        return new SimpleSpatial(gid, (JGeometry)geom);
    }
}
