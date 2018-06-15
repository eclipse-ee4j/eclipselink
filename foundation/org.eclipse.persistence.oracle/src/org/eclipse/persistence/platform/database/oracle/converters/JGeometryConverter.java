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
package org.eclipse.persistence.platform.database.oracle.converters;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

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
    private final static String JGEOMETRY_DB_TYPE = "MDSYS.SDO_GEOMETRY";
    private final Class JGEOMETRY_CLASS;
    private final MethodHandle loadJSMethod;
    private final MethodHandle storeJSMethod;

    public JGeometryConverter() {
        try {
            JGEOMETRY_CLASS = Class.forName("oracle.spatial.geometry.JGeometry");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            loadJSMethod = lookup.unreflect(JGEOMETRY_CLASS.getMethod("loadJS", Struct.class));
            storeJSMethod = lookup.unreflect(JGEOMETRY_CLASS.getMethod("storeJS", JGEOMETRY_CLASS, Connection.class));
        } catch (IllegalAccessException|NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getStructName() {
        return JGEOMETRY_DB_TYPE;
    }

    @Override
    public Class getJavaType() {
        return JGEOMETRY_CLASS;
    }

    @Override
    public Object convertToObject(Struct struct) throws SQLException {
        if (struct == null){
            return null;
        }
        try {
            return loadJSMethod.invokeWithArguments(struct);
        } catch (Throwable throwable) {
            throw new SQLException(throwable);
        }
    }

    @Override
    public Struct convertToStruct(Object geometry, Connection connection) throws SQLException {
        if (geometry == null){
            return null;
        }
        try {
            return (Struct) storeJSMethod.invokeWithArguments(JGEOMETRY_CLASS.cast(geometry), connection);
        } catch (Throwable throwable) {
            throw new SQLException(throwable);
        }
    }
}
