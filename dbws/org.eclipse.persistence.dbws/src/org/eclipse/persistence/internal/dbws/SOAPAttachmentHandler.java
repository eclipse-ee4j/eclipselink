/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

    public String addSwaRefAttachment(DataHandler data) {
        ++count;
        String name = "cid:ref" + count;
        attachments.put(name, data);
        return name;
    }

    public String addSwaRefAttachment(byte[] data, int start, int length) {
        ++count;
        String name = "cid:ref" + count;
        DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(data,
            "application/octet-stream"));
        attachments.put(name, dataHandler);
        return name;
    }

    public String addMtomAttachment(DataHandler data, String elementName, String namespace) {
        String name = "cid:" + randomUUID().toString();
        attachments.put(name, data);
        return name;
    }

    public String addMtomAttachment(byte[] data, int start, int len, String mimeType,
        String elementName, String namespace) {
        String name = "cid:" + randomUUID().toString();
        DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(data,
            "application/octet-stream"));
        attachments.put(name, dataHandler);
        return name;
    }

    public boolean isXOPPackage() {
        return true;
    }
}