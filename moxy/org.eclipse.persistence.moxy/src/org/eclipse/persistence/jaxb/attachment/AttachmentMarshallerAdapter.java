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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.attachment;

import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.activation.DataHandler;

import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of the TopLink OX XMLAttachmentMarshaller
 * interface that wraps an implementation of the JAXB AttachmentMarshaller interface.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the XMLAttachmentMarshaller interface</li>
 * <li>Adapt events from the TopLink OX Attachment API to the JAXB 2.0 Attachment API</li>
 * </ul>
 * <p>This class allows TopLink OXM to do attachment marshalling callback events to a JAXB
 * 2.0 Listener without adding a dependancy on JAXB 2.0 into core TopLink. The Adapter class
 * wraps a javax.xml.bin.attachment.AttachmentMarshaller and passes on the events as they're raised
 *
 * @see javax.xml.bind.attachment.AttachmentMarshaller
 * @see org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller
 * @since Oracle TopLink 11.1.1.0.0
 * @author mmacivor
 *
 */
public class AttachmentMarshallerAdapter implements XMLAttachmentMarshaller {

    private AttachmentMarshaller attachmentMarshaller;

    public AttachmentMarshallerAdapter(AttachmentMarshaller a) {
        this.attachmentMarshaller = a;
    }

    @Override
    public boolean isXOPPackage() {
        return attachmentMarshaller.isXOPPackage();
    }

    @Override
    public String addMtomAttachment(javax.activation.DataHandler data, String elementName, String namespaceURI) {
        return this.attachmentMarshaller.addMtomAttachment(data,  namespaceURI, elementName);
    }

    @Override
    public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementName, String namespace) {
        return this.attachmentMarshaller.addMtomAttachment(data, offset, length, mimeType, namespace, elementName);
    }

    @Override
    public String addSwaRefAttachment(DataHandler data) {
        return this.attachmentMarshaller.addSwaRefAttachment(data);
    }

    @Override
    public String addSwaRefAttachment(byte[] data, int offset, int length) {
        return null;
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        return attachmentMarshaller;
    }


}
