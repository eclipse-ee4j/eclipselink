package org.eclipse.persistence.jaxb.compiler;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.persistence.jaxb.javamodel.Helper;

public class ReferenceProperty extends Property {
	private ArrayList<ElementDeclaration> referencedElements;
	
	public ReferenceProperty(Helper helper) {
		super();
	}
	
	public void addReferencedElement(ElementDeclaration element) {
		if(referencedElements == null) {
			referencedElements = new ArrayList<ElementDeclaration>();
		}
		referencedElements.add(element);
	}
	
	public boolean isReference() {
		return true;
	}
	
	public ArrayList<ElementDeclaration> getReferencedElements() {
		return referencedElements;
	}
}
