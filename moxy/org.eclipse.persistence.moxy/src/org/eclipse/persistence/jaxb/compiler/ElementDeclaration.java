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

import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;

import java.util.List;
import java.util.ArrayList;

public class ElementDeclaration {
	private QName elementName;
	private QName substitutionHead;
	private String javaTypeName;
	private JavaClass javaType;
	private List<ElementDeclaration> substitutableElements;
	private boolean isXmlRootElement = false;
	
	public ElementDeclaration(QName name, JavaClass javaType, String javaTypeName) {
		this.elementName = name;
		this.javaTypeName = javaTypeName;
		this.javaType = javaType;
		this.substitutableElements = new ArrayList<ElementDeclaration>();
	}
	
	public QName getElementName() {
		return elementName;
	}
	
	public String getJavaTypeName() {
		return javaTypeName;
	}
	
	public List<ElementDeclaration> getSubstitutableElements() {
		return substitutableElements;
	}
	
	public void addSubstitutableElement(ElementDeclaration element) {
		this.substitutableElements.add(element);
	}
	
	public void setSubstitutionHead(QName rootElement) {
		this.substitutionHead = rootElement;
	}
	
	public QName getSubstitutionHead() {
		return substitutionHead;
	}
	
	public boolean isXmlRootElement() {
		return this.isXmlRootElement;
	}
	
	public void setIsXmlRootElement(boolean isXmlRoot) {
		this.isXmlRootElement = isXmlRoot;
	}
	
	public JavaClass getJavaType() {
	    return this.javaType;
	}
	
	public void setJavaType(JavaClass type) {
	    this.javaType = type;
	}
}
