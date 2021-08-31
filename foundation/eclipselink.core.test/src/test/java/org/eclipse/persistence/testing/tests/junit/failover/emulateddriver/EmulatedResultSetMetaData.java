/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.junit.failover.emulateddriver;

import java.sql.*;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;

public class EmulatedResultSetMetaData implements ResultSetMetaData {

    protected EmulatedResultSet resultSet;

    public EmulatedResultSetMetaData(EmulatedResultSet resultSet) {
        this.resultSet = resultSet;
    }


    @Override
    public int getColumnCount() {
        return ((DatabaseRecord)resultSet.getRows().get(0)).getFields().size();
    }


    @Override
    public boolean isAutoIncrement(int column) {
        return false;
    }


    @Override
    public boolean isCaseSensitive(int column) {
        return true;
    }


    @Override
    public boolean isSearchable(int column) {
        return true;
    }


    @Override
    public boolean isCurrency(int column) {
        return false;
    }


    @Override
    public int isNullable(int column) {
        return 0;
    }


    @Override
    public boolean isSigned(int column) {
        return true;
    }


    @Override
    public int getColumnDisplaySize(int column) {
        return 0;
    }


    @Override
    public String getColumnLabel(int column) {
        return "";
    }


    @Override
    public String getColumnName(int column) {
        return ((DatabaseRecord)resultSet.getRows().get(0)).getFields().get(column - 1).getName();
    }


    @Override
    public String getSchemaName(int column) {
        return "";
    }


    @Override
    public int getPrecision(int column) {
        return 0;
    }

    @Override
    public int getScale(int column) {
        return 0;
    }

    @Override
    public String getTableName(int column) {
        return "";
    }

    @Override
    public String getCatalogName(int column) {
        return "";
    }

    @Override
    public int getColumnType(int column) {
        return 0;
    }

    @Override
    public String getColumnTypeName(int column) {
        return "";
    }

    @Override
    public boolean isReadOnly(int column) {
        return false;
    }

    @Override
    public boolean isWritable(int column) {
        return true;
    }

    @Override
    public boolean isDefinitelyWritable(int column) {
        return true;
    }

    @Override
    public String getColumnClassName(int column) {
        return "";
    }

    @Override
    public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

    @Override
    public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }

}
