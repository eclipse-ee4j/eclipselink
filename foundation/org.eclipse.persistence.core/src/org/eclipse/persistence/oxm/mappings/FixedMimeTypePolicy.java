/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.oxm.mappings;

/**
 * <p><b>Purpose:</b> Provides a default implementation of MimeTypePolicy to be used for java
 * properties that have a single static mime type. 
 * 
 *  @see MimeTypePolicy
 *  @see XMLBinaryDataMapping
 *  @see XMLBinaryDataCollectionMapping
 *
 */
public class FixedMimeTypePolicy implements MimeTypePolicy {
	
	private String aMimeType;

	public FixedMimeTypePolicy() {
	}
	
	public FixedMimeTypePolicy(String aMimeTypeParameter) {
		aMimeType = aMimeTypeParameter;
	}
	
	public String getMimeType(Object anObject) {
		return aMimeType;
	}

	public String getMimeType() {
		return aMimeType;
	}
	
	public void setMimeType(String aString) {
		aMimeType = aString;
	}
}
