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
package org.eclipse.persistence.oxm.attachment;

import javax.activation.DataHandler;

/**
* <p><b>Purpose:</b> Provides an interface through which EclipseLink can allow a user to do
* special handling for Binary Data. This is used for fields mapped using an XMLBinaryDataMapping
* to retrieve an id to be marshaled in place of the binary object. This id will be passed into
* an XMLAttachmentUnmarshaller when the document is unmarshalled to retrieve the original data.
*
* If isXOPPackage returns false, then no other methods on this interface will be called, and it
* will be assumed that all binary mapped fields should be inlined as base64.
*
* @see org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping
* @see XMLAttachmentUnmarshaller
*
*/

public interface XMLAttachmentMarshaller {
    public String addMtomAttachment(DataHandler data, String elementName, String namespace);

    public String addSwaRefAttachment(DataHandler data);

    public String addMtomAttachment(byte[] data, int start, int length, String mimeType, String elementName, String namespace);

    public String addSwaRefAttachment(byte[] data, int start, int length);

    public boolean isXOPPackage();

}
