/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import javax.xml.bind.annotation.DomHandler;

/**
 *  INTERNAL:
 *  <p><b>Purpose:</b>Subclass Property to add XmlAnyElement specific information
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Indicate that a property represents an xsd:any construct</li>
 *  <li>Store additional XmlAnyElement specific information</li>
 *  </ul>
 *  <p>This class is a subclass of Property and is used to indicate that
 *  a property maps to an xs:any structure in the schema. This will be 
 *  created by the AnnotationsProcessor for any property that is annotated
 *  with the XmlAnyElement annotation.
 *  
 *  @see org.eclipse.persistence.jaxb.compiler.Property
 *  @see org.eclipse.persistence.jaxb.compiler.TypeInfo
 *  @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 *  
 *  @author mmacivor
 */
public class AnyProperty extends Property {
	private boolean lax;
	private Class<? extends DomHandler> domHandlerClass;
	
	public AnyProperty(Helper helper) {
		super(helper);
	}
	
	public boolean isAny() {
		return true;
	}
	
	public boolean isLax() {
		return lax;
	}
	
	public void setLax(boolean b) {
		lax = b;
	}
	
	public void setDomHandlerClass(Class<? extends DomHandler> domHandlerClass) {
		this.domHandlerClass = domHandlerClass;
	}
	
	public Class<? extends DomHandler> getDomHandlerClass() {
		return this.domHandlerClass;
	}
	
}
