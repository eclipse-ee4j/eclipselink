/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.attachments.contenttype;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;

public class TestAttachmentMarshaller extends AttachmentMarshaller {

    private String mimeType;

    @Override
    public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName) {
        return null;
    }

    @Override
    public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName) {
        this.mimeType = mimeType;
        return "FIXED";
    }

    @Override
    public String addSwaRefAttachment(DataHandler data) {
        return null;
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }

    public String getMimeType() {
        return mimeType;
    }

}