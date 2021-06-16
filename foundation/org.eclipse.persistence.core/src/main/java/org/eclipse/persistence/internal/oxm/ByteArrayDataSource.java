/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import jakarta.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

/**
 * INTERNAL:
 * @author  mmacivor
 */

public class ByteArrayDataSource implements DataSource {
    String contentType;
    byte[] bytes;

    public ByteArrayDataSource(byte[] data, String mimeType) {
        this.contentType = mimeType;
        bytes = data;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public java.io.OutputStream getOutputStream() {
        return new ByteArrayOutputStream(bytes.length);
    }

    @Override
    public java.io.InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public String getContentType() {
        if(contentType.startsWith("multipart") && contentType.indexOf("boundary") == -1) {
            //parse the bytes for the header and extract the boundary. Add it into the content type.
            StringBuilder buffer = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
            while(true) {
                try {
                    int next = reader.read();
                    if(next == -1) {
                        break;
                    } else if(((char)next) == '\n') {
                        if(!(buffer.length() == 0)) {
                            break;
                        }
                    } else {
                        buffer.append((char)next);
                    }
                } catch(Exception ex) {}
            }
            //buffer should contain the header string here.
            int index = buffer.indexOf("boundary");
            if(index != -1) {
                contentType += ";" + buffer.substring(index);
            }
        }
        return contentType;
    }
}
