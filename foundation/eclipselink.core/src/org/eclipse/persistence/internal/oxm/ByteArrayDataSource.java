/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import javax.activation.DataSource;
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
    
    public String getName() {
        return "";
    }
    
    public java.io.OutputStream getOutputStream() {
        return new ByteArrayOutputStream(bytes.length);
    }
    
    public java.io.InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }
    
    public String getContentType() {
        if(contentType.startsWith("multipart") && contentType.indexOf("boundary") == -1) {
            //parse the bytes for the header and extract the boundary. Add it into the content type.
            StringBuffer buffer = new StringBuffer();
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
