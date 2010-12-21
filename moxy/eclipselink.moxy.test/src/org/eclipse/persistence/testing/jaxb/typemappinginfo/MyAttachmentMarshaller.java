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
 * dmccann - November 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.util.HashMap;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;

public class MyAttachmentMarshaller extends AttachmentMarshaller {
    public static int count = 0;
    public static HashMap attachments = new HashMap();
    public boolean returnNull = false;
    public String localName = null;

    public String addSwaRefAttachment(DataHandler data) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        MyAttachmentMarshaller.count++;
        attachments.put(id, data);
        return id;
    }
    
    public String addSwaRefAttachment(byte[] data, int offset, int length) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;
        MyAttachmentMarshaller.count++;
        attachments.put(id, data);
        return id;
    }

    public String addMtomAttachment(byte[] bytes, int start, int offset, String mimeType, String elemtnName, String namespaceURI) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        MyAttachmentMarshaller.count++;
        attachments.put(id, bytes);
        this.localName = elemtnName;
        return id;
    }

    public String addMtomAttachment(DataHandler data, String namespaceURI, String elementName) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        MyAttachmentMarshaller.count++;
        attachments.put(id, data);
        this.localName = elementName;
        return id;
    }

    public boolean isXOPPackage() {
        return true;
    }
    
    public void setReturnNull(boolean b) {
        this.returnNull = b;
    }
    
    public String getLocalName() {
        return this.localName;
    }
}