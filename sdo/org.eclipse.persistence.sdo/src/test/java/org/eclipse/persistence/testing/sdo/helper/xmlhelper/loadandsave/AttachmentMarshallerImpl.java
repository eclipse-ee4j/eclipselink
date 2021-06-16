/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import jakarta.activation.DataHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

/**
 * Provide a testing implementation of AttachmentMarshaller that normally would be provided by the application server
 */
public class AttachmentMarshallerImpl implements XMLAttachmentMarshaller {

    private String id;

    public AttachmentMarshallerImpl(String id) {
        this.id = id;
    }

    // maintain a global counter
    public String addMtomAttachment(byte[] bytes, int start, int offset, String namespaceURI, String elemtnName, String mimeType) {
        return id;
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
