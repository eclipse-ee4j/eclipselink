/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyelement;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAnyElement;
import java.util.Collection;
import java.util.Iterator;
//import java.util.List;

import org.w3c.dom.Element;
import org.eclipse.persistence.platform.xml.XMLComparer;

@XmlRootElement(name="xmlanyelementcollectionmodel")
public class XmlAnyElementCollectionModel {
	public int a;
	public int b;
	
	@XmlAnyElement
	public Collection any;

	
	public boolean equals(Object obj) {
		if(!(obj instanceof XmlAnyElementCollectionModel)) {
			return false;
		}
		
		XmlAnyElementCollectionModel example = ((XmlAnyElementCollectionModel)obj);
		if(!(a == example.a)) {
			return false;
		}
		if(!(b == example.b)) {
			return false;
		}
		if(!(any.size() == example.any.size())) {
			return false;
		}
		
		Iterator any1 = this.any.iterator();
		Iterator any2 = example.any.iterator();
		XMLComparer comparer = new XMLComparer();
		
		while(any1.hasNext()) {
			Object next1 = any1.next();
			Object next2 = any2.next();
			
			if((next1 instanceof org.w3c.dom.Element) && (next2 instanceof Element)) {
				Element nextElem1 = (Element)next1;
				Element nextElem2 = (Element)next2;
				if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
					return false;
				}
			} else {
				if(!(next1.equals(next2))) {
					return false;
				}
			}
		}
		return true;
	}

}
