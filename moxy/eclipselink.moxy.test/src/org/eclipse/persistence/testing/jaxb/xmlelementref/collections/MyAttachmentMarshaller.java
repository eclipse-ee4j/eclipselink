/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
