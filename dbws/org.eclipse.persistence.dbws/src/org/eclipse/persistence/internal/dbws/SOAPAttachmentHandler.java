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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.dbws;

// Javase imports
import java.util.HashMap;
import java.util.Map;
import static java.util.UUID.randomUUID;

// Java extension imports
import javax.activation.DataHandler;

// EclipseLink imports
import org.eclipse.persistence.internal.oxm.ByteArrayDataSource;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;

/**
 * <p><b>INTERNAL</b>: implementation of EclipseLink {@link XMLAttachmentMarshaller} implementation
 * handles binary attachments
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class SOAPAttachmentHandler implements XMLAttachmentMarshaller {

    private int count = 0;
    private HashMap<String, DataHandler> attachments = new HashMap<String,DataHandler>();

    public boolean hasAttachments() {
        return attachments.size() > 0;
    }

    public Map<String, DataHandler> getAttachments() {
        return attachments;
    }

    @Override
    public String addSwaRefAttachment(DataHandler data) {
        ++count;
        String name = "cid:ref" + count;
        attachments.put(name, data);
        return name;
    }

    @Override
    public String addSwaRefAttachment(byte[] data, int start, int length) {
        ++count;
        String name = "cid:ref" + count;
        DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(data,
            "application/octet-stream"));
        attachments.put(name, dataHandler);
        return name;
    }

    @Override
    public String addMtomAttachment(DataHandler data, String elementName, String namespace) {
        String name = "cid:" + randomUUID().toString();
        attachments.put(name, data);
        return name;
    }

    @Override
    public String addMtomAttachment(byte[] data, int start, int len, String mimeType,
        String elementName, String namespace) {
        String name = "cid:" + randomUUID().toString();
        DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(data,
            "application/octet-stream"));
        attachments.put(name, dataHandler);
        return name;
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }
}
