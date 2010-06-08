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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import javax.activation.DataHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

/**
 * Provide a testing implementation of AttachmentMarshaller that normally would be provided by the application server
 */
public class AttachmentMarshallerImpl implements XMLAttachmentMarshaller {
    // maintain a global counter
    public String addMtomAttachment(byte[] bytes, int start, int offset, String namespaceURI, String elemtnName, String mimeType) {
        return "c_id0";
    }

    public String addSwaRefAttachment(byte[] bytes, int start, int offset) {
        throw new UnsupportedOperationException("addSwaRefAttachment not supported");
    }

    public String addMtomAttachment(DataHandler data, String elementName, String namespace) {
        throw new UnsupportedOperationException("addMtomAttachment not supported");
    }

    public String addSwaRefAttachment(DataHandler data) {
        throw new UnsupportedOperationException("addSwaRefAttachment not supported");
    }

    public boolean isXOPPackage() {
        // force attachment usage
        return true;
    }
    
    
}
