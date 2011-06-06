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
 *     Denise Smith - November 11, 2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.multiplepackage.packagea;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.multiplepackage.packageb.ClassB;

@XmlRootElement(name="theRoot")
public class Root {
	@XmlElement(name="classA")
	public ClassA theClassA;
	
	@XmlElement(name="classB")
	public ClassB theClassB;
	
	public String toString(){
		return "ClassA: " + theClassA + " ClassB:" + theClassB;
	}

	public boolean equals(Object object) {
		Root obj = ((Root)object);
		if(!obj.theClassA.equals(theClassA)){
			return false;
		}	
		if(!obj.theClassB.equals(theClassB)){
			return false;
		}
		return true;
	}
}
