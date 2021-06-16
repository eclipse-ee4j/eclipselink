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
// dmccann - January 13/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata;

import java.util.HashMap;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentMarshaller;

public class MyAttachmentMarshaller extends AttachmentMarshaller {
    public static int count = 0;
    public static HashMap attachments = new HashMap();
    public static final String BYTES_ID = "c_id0";
    public static final String DATA_ID = "c_id1";

    public boolean isXOPPackage() {
        return true;
    }

    // SATISFY INTERFACE IMPLEMENTATION
    public String addSwaRefAttachment(DataHandler data) { return DATA_ID; }
    public String addSwaRefAttachment(byte[] data, int offset, int length) { return BYTES_ID; }
    public String addMtomAttachment(byte[] bytes, int start, int offset, String mimeType, String elemtnName, String namespaceURI) {
        String id = BYTES_ID;
        count++;
        attachments.put(id, bytes);
        return id;
    }
    public String addMtomAttachment(DataHandler data, String namespaceURI, String elementName) {
        String id = DATA_ID;
        count++;
        attachments.put(id, data);
        return id;
    }
}
