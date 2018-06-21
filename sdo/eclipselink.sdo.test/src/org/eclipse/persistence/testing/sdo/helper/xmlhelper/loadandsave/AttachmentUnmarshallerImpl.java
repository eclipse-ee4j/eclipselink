/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import javax.activation.DataHandler;
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
