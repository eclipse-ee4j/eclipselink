/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - January 13/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public class MyAttachmentUnmarshaller extends AttachmentUnmarshaller {
    public boolean isXOPPackage() {
        return true;
    }

    public byte[] getAttachmentAsByteArray(String cid) {
        Object obj = MyAttachmentMarshaller.attachments.get(cid);
        if(obj instanceof byte[]){
            return (byte[])obj;
        }

        try {
            return ((String)((DataHandler)obj).getContent()).getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public DataHandler getAttachmentAsDataHandler(String cid) {
        Object obj = MyAttachmentMarshaller.attachments.get(cid);
        if (obj instanceof DataHandler) {
            return (DataHandler)obj;
        }
        return null;
    }
}
