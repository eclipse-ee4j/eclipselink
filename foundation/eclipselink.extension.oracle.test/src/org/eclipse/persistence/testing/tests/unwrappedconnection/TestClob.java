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
 *     James Sutherland - Adding wrapping
 ******************************************************************************/  
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

    public InputStream getAsciiStream() throws SQLException {
        return clob.getAsciiStream();
    }

    public Reader getCharacterStream() throws SQLException {
        return clob.getCharacterStream();
    }

    public String getSubString(long pos, int length) throws SQLException {
        return clob.getSubString(pos, length);
    }

    public long length() throws SQLException {
        return clob.length();
    }

    public long position(Clob searchstr, long start) throws SQLException {
        return clob.position(searchstr, start);
    }

    public long position(String searchstr, long start) throws SQLException {
        return clob.position(searchstr, start);
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        return clob.setAsciiStream(pos);
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        return clob.setCharacterStream(pos);
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return clob.setString(pos, str, offset, len);
    }

    public int setString(long pos, String str) throws SQLException {
        return clob.setString(pos, str);
    }

    public void truncate(long len) throws SQLException {
        clob.truncate(len);
    }

    public void free() throws SQLException {
        //clob.free();
    }

    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return null; //clob.getCharacterStream(pos, length);
    }
}
