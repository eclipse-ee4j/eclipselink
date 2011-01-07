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
package org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;


import oracle.spatial.geometry.JGeometry;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.eclipse.persistence.platform.database.converters.*;
import org.eclipse.persistence.platform.database.oracle.converters.*;
import org.eclipse.persistence.testing.framework.*;


public class MyGeometryConverter implements StructConverter {
    public static final String MY_GEOMETRY_TYPE_NAME = "MY_GEOMETRY";
    public static String MY_GEOMETRY_TYPE = "SCOTT." + MY_GEOMETRY_TYPE_NAME;
    public static final Class JGEOMETRY_CLASS = MyGeometry.class;

    public String getStructName() {
        return MY_GEOMETRY_TYPE;
    }

    public Class getJavaType() {
        return JGEOMETRY_CLASS;
    }

    private StructDescriptor structDescriptor = null;
    private JGeometryConverter jGeometryConverter;

    public MyGeometryConverter() {
        this.jGeometryConverter = new JGeometryConverter();
    }

    public JGeometryConverter getJGeometryConverter() {
        return this.jGeometryConverter;
    }

    public Object convertToObject(Struct struct) throws SQLException {
        Object[] structValues = ((STRUCT)struct).getAttributes();

        int id = ((Number)structValues[0]).intValue();
        JGeometry jgeom = 
            (JGeometry)getJGeometryConverter().convertToObject((STRUCT)structValues[1]);
        return new MyGeometry(id, jgeom);
    }

    public Struct convertToStruct(Object geometry, 
                                  Connection connection) throws SQLException {
        if (geometry == null) {
            return null;
        }
        MyGeometry myGeometry = (MyGeometry)geometry;

        STRUCT geomSTRUCT = 
            (STRUCT)getJGeometryConverter().convertToStruct(myGeometry.getGeometry(), 
                                                            connection);

        return new STRUCT(getStructDescriptor(connection), connection, 
                          new Object[] { myGeometry.getId(), geomSTRUCT });
    }

    protected StructDescriptor getStructDescriptor(Connection con) {
        if (this.structDescriptor == null) {

            try {
                this.structDescriptor = 
                        new oracle.sql.StructDescriptor(MY_GEOMETRY_TYPE, con);
            } catch (SQLException e) {
                throw new TestProblemException("A problem was detected when using MyGeometryConverter", e);
            }
        }
        return this.structDescriptor;
    }
}
