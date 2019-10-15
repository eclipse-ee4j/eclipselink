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
// dmccann - November 18/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref;

import java.util.HashMap;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;

public class MyAttachmentMarshaller extends AttachmentMarshaller {
    public static int count = 0;
    public static HashMap attachments = new HashMap();
    public boolean returnNull = false;

    public String addSwaRefAttachment(DataHandler data) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        //String id = MyAttachmentUnmarshaller.ATTACHMENT_PREFIX +  MyAttachmentMarshaller.count;
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

        //String id = MyAttachmentUnmarshaller.ATTACHMENT_PREFIX +  MyAttachmentMarshaller.count;
        MyAttachmentMarshaller.count++;
        attachments.put(id, bytes);

        return id;
    }

    public String addMtomAttachment(DataHandler data, String namespaceURI, String elementName) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        //String id = MyAttachmentUnmarshaller.ATTACHMENT_PREFIX +  MyAttachmentMarshaller.count;
        MyAttachmentMarshaller.count++;
        attachments.put(id, data);
        return id;
    }

    public boolean isXOPPackage() {
        return true;
    }

    public void setReturnNull(boolean b) {
        this.returnNull = b;
    }
}
