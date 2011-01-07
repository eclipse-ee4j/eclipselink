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
 *     James Sutherland - Adding wrapping
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class TestArray implements Array {

    private Array array;
    
    public TestArray(Array array){
        this.array = array;
    }

    public Object getArray() throws SQLException {
        return array.getArray();
    }

    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return array.getArray(index, count, map);
    }

    public Object getArray(long index, int count) throws SQLException {
        return array.getArray(index, count);
    }

    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        return array.getArray(map);
    }

    public int getBaseType() throws SQLException {
        return array.getBaseType();
    }

    public String getBaseTypeName() throws SQLException {
        return array.getBaseTypeName();
    }

    public ResultSet getResultSet() throws SQLException {
        return array.getResultSet();
    }

    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return array.getResultSet(index, count, map);
    }

    public ResultSet getResultSet(long index, int count) throws SQLException {
        return array.getResultSet(index, count);
    }

    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        return array.getResultSet(map);
    }

    public void free() throws SQLException {
        //array.free();
    }
        
}
