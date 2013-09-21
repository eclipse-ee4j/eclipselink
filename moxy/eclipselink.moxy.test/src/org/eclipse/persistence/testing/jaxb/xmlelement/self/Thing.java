/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - September 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement.self;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

public class Thing {
   public Object content;
   
   public boolean equals(Object obj){
	   if(obj instanceof Thing){
		   XMLComparer comparer = new XMLComparer();
		   return (content == null && ((Thing)obj).content == null) || 
		   (content != null && content.equals(((Thing)obj).content)) ||
		   (content != null && content instanceof Node && ((Thing)obj).content instanceof Node && comparer.isNodeEqual((Node)content, (Node)((Thing)obj).content));
	   }
	   return false;
   }
}
