/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - January 2012 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.unmarshaller.space;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObject {

	public String theString;
	@XmlAnyElement
	public List<Object> theAny;
	
	public boolean equals(Object theObject){
		if(theObject instanceof TestObject){
			if(theString == null){
				return ((TestObject)theObject).theString == null;
			}
			return theString.equals(((TestObject)theObject).theString);
		}
		return false;
	}
}
