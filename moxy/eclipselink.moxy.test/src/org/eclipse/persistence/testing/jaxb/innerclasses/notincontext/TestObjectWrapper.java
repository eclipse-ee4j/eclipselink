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
 *     Denise Smith - February 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.innerclasses.notincontext;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObjectWrapper {

	public TestObject testObject;
	
	public boolean equals (Object compareObject){
		if(compareObject instanceof TestObjectWrapper){
			if(testObject == null){
				return ((TestObjectWrapper)compareObject) == null;
			}
			return testObject.equals(((TestObjectWrapper)compareObject).testObject);
		}
		return false;
	}
}
