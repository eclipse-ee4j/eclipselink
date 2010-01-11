/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.failover.emulateddriver;

import java.sql.*;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;

public class EmulatedResultSetMetaData implements ResultSetMetaData {

    protected EmulatedResultSet resultSet;
    
    public EmulatedResultSetMetaData(EmulatedResultSet resultSet) {
        this.resultSet = resultSet;
    }
    

    public int getColumnCount() {
        return ((DatabaseRecord)resultSet.getRows().get(0)).getFields().size();
    }


    public boolean isAutoIncrement(int column) {
        return false;
    }


    public boolean isCaseSensitive(int column) {
        return true;
    }	


    public boolean isSearchable(int column) {
        return true;
    }


    public boolean isCurrency(int column) {
        return false;
    }


    public int isNullable(int column) {
        return 0;
    }


    public boolean isSigned(int column) {
        return true;
    }


    public int getColumnDisplaySize(int column) {
        return 0;
    }


    public String getColumnLabel(int column) {
        return "";
    }


    public String getColumnName(int column) {
        return ((DatabaseField)((DatabaseRecord)resultSet.getRows().get(0)).getFields().get(column - 1)).getName();
    }


    public String getSchemaName(int column) {
        return "";
    }


    public int getPrecision(int column) {
        return 0;
    }

    public int getScale(int column) {
        return 0;
    }	

    public String getTableName(int column) {
        return "";
    }

    public String getCatalogName(int column) {
        return "";
    }

    public int getColumnType(int column) {
        return 0;
    }

    public String getColumnTypeName(int column) {
        return "";
    }

    public boolean isReadOnly(int column) {
        return false;
    }

    public boolean isWritable(int column) {
        return true;
    }

    public boolean isDefinitelyWritable(int column) {
        return true;
    }

    public String getColumnClassName(int column) {
        return "";
    }

    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }
    
}
