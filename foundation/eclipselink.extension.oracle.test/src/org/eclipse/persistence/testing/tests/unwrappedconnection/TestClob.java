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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

public class TestClob implements Clob {

    private Clob clob;

    public TestClob(Clob clob){
        this.clob = clob;
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return clob.getAsciiStream();
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return clob.getCharacterStream();
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        return clob.getSubString(pos, length);
    }

    @Override
    public long length() throws SQLException {
        return clob.length();
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        return clob.position(searchstr, start);
    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        return clob.position(searchstr, start);
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return clob.setAsciiStream(pos);
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        return clob.setCharacterStream(pos);
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return clob.setString(pos, str, offset, len);
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        return clob.setString(pos, str);
    }

    @Override
    public void truncate(long len) throws SQLException {
        clob.truncate(len);
    }

    @Override
    public void free() throws SQLException {
        clob.free();
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return clob.getCharacterStream(pos, length);
    }
}
