/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 13/2010 - 2.0 - Initial implementation
 ******************************************************************************/
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
