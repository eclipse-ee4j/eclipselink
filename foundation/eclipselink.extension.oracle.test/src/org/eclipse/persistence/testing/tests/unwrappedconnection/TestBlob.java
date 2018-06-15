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
import java.sql.Blob;
import java.sql.SQLException;

public class TestBlob implements Blob {

    private Blob blob;

    public TestBlob(Blob blob) {
        this.blob = blob;
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return blob.getBinaryStream();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return blob.getBytes(pos, length);
    }

    @Override
    public long length() throws SQLException {
        return blob.length();
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return blob.position(pattern, start);
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        return blob.position(pattern, start);
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return blob.setBinaryStream(pos);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return blob.setBytes(pos, bytes, offset, len);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return blob.setBytes(pos, bytes);
    }

    @Override
    public void truncate(long len) throws SQLException {
        blob.truncate(len);
    }

    @Override
    public void free() throws SQLException {
        blob.free();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return blob.getBinaryStream(pos, length);
    }

}
