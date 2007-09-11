/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.serializedobject;

import java.io.Serializable;

public class SerializableAddress implements Serializable {
	public String theAddress;
	
	public String getTheAddress() {
		return theAddress;
	}
	public void setTheAddress(String addr) {
		theAddress = addr;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof SerializableAddress)) {
			return false;
		}
		return getTheAddress().equalsIgnoreCase(((SerializableAddress)obj).getTheAddress());
	}
}