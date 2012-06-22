/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

/* $Header: MyAttachmentMarshaller.java 08-feb-2007.15:04:44 mmacivor Exp $ */
/* Copyright (c) 2006, 2007, Oracle. All rights reserved.  */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mmacivor    02/08/07 - 
    mfobrien    10/19/06 - Creation
 */

/**
 *  @version $Header: MyAttachmentMarshaller.java 08-feb-2007.15:04:44 mmacivor Exp $
 *  @author  mfobrien
 *  @since  11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection;

import java.util.HashMap;
import javax.activation.DataHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;

public class MyAttachmentMarshaller implements XMLAttachmentMarshaller {
    public static int count = 0;
    public static HashMap attachments = new HashMap();
    public boolean returnNull = false;
    private String localName = null;

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

    public String addMtomAttachment(byte[] bytes, int start, int offset, String mimeType, String elementName, String namespaceURI) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        //String id = MyAttachmentUnmarshaller.ATTACHMENT_PREFIX +  MyAttachmentMarshaller.count;
        MyAttachmentMarshaller.count++;
        attachments.put(id, bytes);
        this.localName = elementName;
        return id;
    }

    public String addMtomAttachment(DataHandler data, String elementName, String namespaceURI) {
        if(returnNull) {
            return null;
        }
        String id = MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID;

        //String id = MyAttachmentUnmarshaller.ATTACHMENT_PREFIX +  MyAttachmentMarshaller.count;
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
