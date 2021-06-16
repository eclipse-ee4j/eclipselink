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

import java.io.IOException;
import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;

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
