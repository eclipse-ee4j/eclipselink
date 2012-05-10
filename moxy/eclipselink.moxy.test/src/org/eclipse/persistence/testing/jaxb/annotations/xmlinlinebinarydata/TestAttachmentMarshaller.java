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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;

public class TestAttachmentMarshaller extends AttachmentMarshaller {

    private int attachmentCount = 0;

    public int getAttachmentCount() {
        return attachmentCount;
    }

    @Override
    public String addMtomAttachment(DataHandler arg0, String arg1, String arg2) {
        attachmentCount++;
        return null;
    }

    @Override
    public String addSwaRefAttachment(DataHandler arg0) {
        attachmentCount++;
        return null;
    }

    @Override
    public String addMtomAttachment(byte[] arg0, int arg1, int arg2, String arg3, String arg4, String arg5) {
        attachmentCount++;
        return null;
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }

}