/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - February 2012
 ******************************************************************************/
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
