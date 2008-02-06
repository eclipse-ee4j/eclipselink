/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;
import org.eclipse.persistence.jaxb.javamodel.Helper;

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
	
}
