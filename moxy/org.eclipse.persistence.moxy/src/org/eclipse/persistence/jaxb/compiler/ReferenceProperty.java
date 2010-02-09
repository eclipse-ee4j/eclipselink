/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.persistence.jaxb.javamodel.Helper;

public class ReferenceProperty extends Property {
	private ArrayList<ElementDeclaration> referencedElements;
	
	public ReferenceProperty(Helper helper) {
		super(helper);
	}
	
	public void addReferencedElement(ElementDeclaration element) {
		if(referencedElements == null) {
			referencedElements = new ArrayList<ElementDeclaration>();
		}
		if(!referencedElements.contains(element)) {
			referencedElements.add(element);
		}
	}
	
	public boolean isReference() {
		return true;
	}
	
	public List<ElementDeclaration> getReferencedElements() {
		return referencedElements;
	}
}
