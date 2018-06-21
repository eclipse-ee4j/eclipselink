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
 * to retrieve the binary data during an unmarshal based on a swaRef or MTOM id.
 *
 * If isXOPPackage returns false, then no other methods on this interface will be called, and it
 * will be assumed that all binary mapped fields have been inlined as base64.
 *
 * @see org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping
 * @see XMLAttachmentMarshaller
 */
public interface XMLAttachmentUnmarshaller {
    public DataHandler getAttachmentAsDataHandler(String id);

    public byte[] getAttachmentAsByteArray(String id);

    public boolean isXOPPackage();
}
