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
package org.eclipse.persistence.jaxb.attachment;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of the TopLink OX XMLAttachmentUnmarshaller
 * interface that wraps an implementation of the JAXB AttachmentUnmarshaller interface. 
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the XMLAttachmentUnmarshaller interface</li>
 * <li>Adapt events from the TopLink OX Attachment API to the JAXB 2.0 Attachment API</li>
 * </ul>
 * <p>This class allows TopLink OXM to do attachment unmarshalling callback events to a JAXB
 * 2.0 Listener without adding a dependancy on JAXB 2.0 into core TopLink. The Adapter class
 * wraps a javax.xml.bin.attachment.AttachmentUnmarshaller and passes on the events as they're raised
 * 
 * @see javax.xml.bind.attachment.AttachmentUnmarshaller
 * @see org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller
 * @since Oracle TopLink 11.1.1.0.0
 * @author mmacivor
 *
 */
public class AttachmentUnmarshallerAdapter implements org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller {

    private AttachmentUnmarshaller attachmentUnmarshaller;
    
    public AttachmentUnmarshallerAdapter(AttachmentUnmarshaller at) {
        this.attachmentUnmarshaller = at;
    }
    public byte[] getAttachmentAsByteArray(String id) {
        return attachmentUnmarshaller.getAttachmentAsByteArray(id);
    }
    
    public DataHandler getAttachmentAsDataHandler(String id) {
        return attachmentUnmarshaller.getAttachmentAsDataHandler(id);
    }
    
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return attachmentUnmarshaller;
    }
    
    public boolean isXOPPackage() {
        return attachmentUnmarshaller.isXOPPackage();
    }

}
