/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
 * @see XMLBinaryDataMapping
 * @see XMLAttachmentMarshaller
 */
public interface XMLAttachmentUnmarshaller {
    public DataHandler getAttachmentAsDataHandler(String id);

    public byte[] getAttachmentAsByteArray(String id);
    
    public boolean isXOPPackage();
}
