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
package org.eclipse.persistence.platform.database.oracle.converters;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import org.eclipse.persistence.platform.database.converters.StructConverter;

/**
 * PUBLIC:
 * A StructConverter that can be used to convert the oracle.spatial.geometry.JGeometry as
 * it is read and written from the database.  To use this StructConverter, it must be added
 * to the DatabasePlatform either with the addStructConverter(StructConverter) method or specified in
 * sessions.xml.  It requires that the oracle.spatial.geometry.JGeometry type is available on
 * the Classpath
 */
public class JGeometryConverter implements StructConverter {
    public static final String JGEOMETRY_DB_TYPE = "MDSYS.SDO_GEOMETRY";
    public static final Class JGEOMETRY_CLASS = JGeometry.class;

    public String getStructName() {
        return JGEOMETRY_DB_TYPE;
    }

    public Class getJavaType() {
        return JGEOMETRY_CLASS;
    }

    public Object convertToObject(Struct struct) throws SQLException {
        if (struct == null){
            return null;
        }
        return JGeometry.load((STRUCT)struct);
    }

    public Struct convertToStruct(Object geometry, Connection connection) throws SQLException {
        if (geometry == null){
            return null;
        }
        return JGeometry.store((JGeometry)geometry, connection);
    }
}
