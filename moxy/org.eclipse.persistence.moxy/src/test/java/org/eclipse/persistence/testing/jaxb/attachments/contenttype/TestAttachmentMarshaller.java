/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.attachments.contenttype;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentMarshaller;

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
