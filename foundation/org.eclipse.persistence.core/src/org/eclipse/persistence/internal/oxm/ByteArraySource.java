/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - 2.4.2 Initial Implementation
package org.eclipse.persistence.internal.oxm;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.transform.stream.StreamSource;


/**
 * INTERNAL:<p>
 * <b>Purpose</b>: Provides an implementation of Source that can act on a byte[]. Overrides the
 * getInputStream and getReader methods to ensure a new stream is created each time (to prevent the
 * one use restriction of StreamSource).
 * @author mmacivor
 *
 */
public class ByteArraySource extends StreamSource {

    private ByteArrayDataSource source;

    public ByteArraySource(byte[] bytes) {
        this.source = new ByteArrayDataSource(bytes, "text/html");
    }

    public ByteArraySource(byte[] bytes, String mimeType) {
        this.source = new ByteArrayDataSource(bytes, mimeType);
    }

    public ByteArraySource(ByteArrayDataSource dataSource) {
        this.source = dataSource;
    }

    @Override
    public InputStream getInputStream() {
        return source.getInputStream();
    }

    @Override
    public Reader getReader() {
        return new InputStreamReader(source.getInputStream());
    }
}
