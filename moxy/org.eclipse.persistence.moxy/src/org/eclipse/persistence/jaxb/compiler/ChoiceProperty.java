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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;

import java.util.Collection;
import org.eclipse.persistence.jaxb.javamodel.Helper;
/**
 *  INTERNAL:
 *  <p><b>Purpose:</b>Subclass Property to add XmlElements specific information
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Indicate that a property represents an xsd:choice construct</li>
 *  <li>Store additional XmlElements specific information</li>
 *  </ul>
 *  <p>This class is a subclass of Property and is used to indicate that
 *  a property maps to a choice structure in the schema. This will be 
 *  created by the AnnotationsProcessor for any property that is annotated
 *  with the XmlElements annotation. Information about the nested choice
 *  elements is stored here for later processing.
 *  
 *  @see org.eclipse.persistence.jaxb.compiler.Property
 *  @see org.eclipse.persistence.jaxb.compiler.TypeInfo
 *  @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 *  
 *  @author mmacivor
 */
public class ChoiceProperty extends Property {
    private Collection<Property> choiceProperties;

	public ChoiceProperty(Helper helper) {
		super(helper);
	}
    public Collection<Property> getChoiceProperties() {
        return this.choiceProperties;
    }
    
    public void setChoiceProperties(Collection<Property> properties) {
        this.choiceProperties = properties;
    }
    
    public boolean isChoice() {
    	return true;
    }
	
}
