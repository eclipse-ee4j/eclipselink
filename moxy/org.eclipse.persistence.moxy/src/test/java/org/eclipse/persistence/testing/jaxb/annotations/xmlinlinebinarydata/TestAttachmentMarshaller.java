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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentMarshaller;

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
