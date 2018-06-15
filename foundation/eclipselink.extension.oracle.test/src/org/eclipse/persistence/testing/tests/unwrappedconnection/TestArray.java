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
//     James Sutherland - Adding wrapping
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class TestArray implements Array {

    private Array array;

    public TestArray(Array array) {
        this.array = array;
    }

    @Override
    public Object getArray() throws SQLException {
        return array.getArray();
    }

    @Override
    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return array.getArray(index, count, map);
    }

    @Override
    public Object getArray(long index, int count) throws SQLException {
        return array.getArray(index, count);
    }

    @Override
    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        return array.getArray(map);
    }

    @Override
    public int getBaseType() throws SQLException {
        return array.getBaseType();
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        return array.getBaseTypeName();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return array.getResultSet();
    }

    @Override
    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return array.getResultSet(index, count, map);
    }

    @Override
    public ResultSet getResultSet(long index, int count) throws SQLException {
        return array.getResultSet(index, count);
    }

    @Override
    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        return array.getResultSet(map);
    }

    @Override
    public void free() throws SQLException {
        array.free();
    }

}
