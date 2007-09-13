/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.dbws;

// Javase imports
import java.util.HashMap;
import java.util.Map;

// Java extension imports
import javax.activation.DataHandler;

// EclipseLink imports
import org.eclipse.persistence.internal.oxm.ByteArrayDataSource;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;

/**
 * <p><b>INTERNAL</b>: implementation of TopLink {@link XMLAttachmentMarshaller} implementation
 * handles binary attachments
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
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
        return null;
    }

    public String addMtomAttachment(byte[] data, int start, int offset, String mimeType, String elementName, String namespace) {
        return null;
    }

    public boolean isXOPPackage() {
        return false;
    }
}
