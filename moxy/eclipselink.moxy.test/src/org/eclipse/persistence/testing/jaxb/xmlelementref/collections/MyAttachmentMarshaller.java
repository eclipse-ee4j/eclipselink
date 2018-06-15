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
import javax.xml.bind.attachment.AttachmentMarshaller;

public class MyAttachmentMarshaller extends AttachmentMarshaller{

    public static String theString = "abc";

    @Override
    public String addMtomAttachment(DataHandler arg0, String arg1, String arg2) {

        return theString;
    }

    @Override
    public String addMtomAttachment(byte[] arg0, int arg1, int arg2,
            String arg3, String arg4, String arg5) {
        return theString;
    }

    @Override
    public String addSwaRefAttachment(DataHandler arg0) {

        return theString;
    }

    public boolean isXOPPackage() { return true; }

}
