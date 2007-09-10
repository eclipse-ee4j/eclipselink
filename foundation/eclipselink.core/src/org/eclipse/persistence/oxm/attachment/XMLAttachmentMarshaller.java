/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.attachment;

import javax.activation.DataHandler;

public interface XMLAttachmentMarshaller {
    public String addMtomAttachment(DataHandler data, String elementName, String namespace);    
    
    public String addSwaRefAttachment(DataHandler data);
    
    public String addMtomAttachment(byte[] data, int start, int length, String mimeType, String elementName, String namespace);    

    public String addSwaRefAttachment(byte[] data, int start, int length);
    
    public boolean isXOPPackage();

}
