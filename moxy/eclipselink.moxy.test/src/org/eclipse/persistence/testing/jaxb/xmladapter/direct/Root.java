/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - January 2014
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Root {
	@XmlSchemaType(name="integer")
	public String myString1;	
	
	@XmlSchemaType(name="integer")
	@XmlJavaTypeAdapter(StringAdapter.class)
	public String myString2;
	
	@XmlSchemaType(name="date")
	@XmlJavaTypeAdapter(StringStringAdapter.class)
	public String myString3;
	
	public boolean equals(Object obj){
		if(obj instanceof Root){
			Root compareObj = (Root)obj;
			return  myString1.equals(compareObj.myString1) 
			     && myString2.equals(compareObj.myString2) 
			     && myString3.equals(compareObj.myString3);
		}
		return false;
	}
}
