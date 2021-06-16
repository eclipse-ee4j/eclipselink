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
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

/**
 * Provide a testing implementation of AttachmentMarshaller that normally would be provided by the application server
 */
public class AttachmentUnmarshallerImpl implements XMLAttachmentUnmarshaller {

    private byte[] bytes;

    public AttachmentUnmarshallerImpl(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getAttachmentAsByteArray(String cid) {
        return bytes;
    }

    public DataHandler getAttachmentAsDataHandler(String id) {
        throw new UnsupportedOperationException("getAttachmentAsDataHandler not supported");

    }

    public boolean isXOPPackage() {
        // force attachment usage
        return true;
    }
}
