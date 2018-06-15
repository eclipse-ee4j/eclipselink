/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - February 2012
package org.eclipse.persistence.testing.jaxb.xmlelementref.collections;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public class MyAttachmentUnmarshaller extends AttachmentUnmarshaller{
    public static DataHandler theDataHandler = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");

    @Override
    public DataHandler getAttachmentAsDataHandler(String cid) {
        return theDataHandler;
    }

    @Override
    public byte[] getAttachmentAsByteArray(String cid) {
        return null;
    }

}
