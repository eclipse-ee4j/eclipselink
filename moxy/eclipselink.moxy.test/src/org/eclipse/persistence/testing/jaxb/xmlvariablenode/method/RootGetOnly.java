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
 *     Denise Smith - May 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlvariablenode.method;

import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

@XmlRootElement(name="root")
public class RootGetOnly {
	
	public String name;

	@XmlVariableNode(value="thingName")
	public List<ThingGetOnly> things;
	
	public boolean equals(Object obj){
		if(obj instanceof RootGetOnly){
		    return things.equals(((RootGetOnly)obj).things) && 	
		    name.equals(((RootGetOnly)obj).name);
		}
		return false;
	}
}
