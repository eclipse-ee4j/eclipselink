/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.sql.Blob;
import java.sql.SQLException;

public class TestBlob implements Blob {

    private Blob blob;
    
    public TestBlob(Blob blob){
        this.blob = blob;
    }

    public InputStream getBinaryStream() throws SQLException {
        return blob.getBinaryStream();
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        return blob.getBytes(pos, length);
    }

    public long length() throws SQLException {
        return blob.length();
    }

    public long position(Blob pattern, long start) throws SQLException {
        return blob.position(pattern, start);
    }

    public long position(byte[] pattern, long start) throws SQLException {
        return blob.position(pattern, start);
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        return blob.setBinaryStream(pos);
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return blob.setBytes(pos, bytes, offset, len);
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return blob.setBytes(pos, bytes);
    }

    public void truncate(long len) throws SQLException {
        blob.truncate(len);
    }

    public void free() throws SQLException {
        //blob.free();
    }

    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return null; //blob.getBinaryStream(pos, length);
    }
        
}
