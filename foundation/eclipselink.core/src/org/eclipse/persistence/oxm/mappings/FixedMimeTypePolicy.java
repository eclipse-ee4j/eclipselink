/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.oxm.mappings;

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
