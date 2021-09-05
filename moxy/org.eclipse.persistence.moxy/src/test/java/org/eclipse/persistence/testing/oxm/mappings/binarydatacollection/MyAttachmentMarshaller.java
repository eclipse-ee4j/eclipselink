/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection;

import java.util.HashMap;
import jakarta.activation.DataHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;

public class MyAttachmentMarshaller implements XMLAttachmentMarshaller {
    public HashMap attachments = new HashMap();
    public boolean returnNull = false;
    private String localName = null;

    @Override
    public String addSwaRefAttachment(DataHandler data) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        attachments.put(id, data);
        return id;
    }

    @Override
    public String addSwaRefAttachment(byte[] data, int offset, int length) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;
        attachments.put(id, data);
        return id;
    }

    @Override
    public String addMtomAttachment(byte[] bytes, int start, int offset, String mimeType, String elementName, String namespaceURI) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        attachments.put(id, bytes);
        this.localName = elementName;
        return id;
    }

    @Override
    public String addMtomAttachment(DataHandler data, String elementName, String namespaceURI) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        attachments.put(id, data);
        this.localName = elementName;
        return id;
    }

    @Override
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
