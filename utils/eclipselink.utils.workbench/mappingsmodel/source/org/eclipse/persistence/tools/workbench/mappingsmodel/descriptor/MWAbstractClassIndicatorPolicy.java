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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class MWAbstractClassIndicatorPolicy
	extends MWModel 
	implements MWClassIndicatorPolicy 
{

	// ************* constructors *************

	/**
	 * for TopLink use only
	 */
	protected MWAbstractClassIndicatorPolicy() {
		super();
	}

	protected MWAbstractClassIndicatorPolicy(MWClassIndicatorPolicy.Parent parent) {
		super(parent);
	}
	

	// ************* MWClassIndicatorPolicy implementation *************
	
	public void setDescriptorsAvailableForIndicatorDictionary(Iterator descriptors) {
        //do nothing       
	}

	public void setDescriptorsAvailableForIndicatorDictionaryForTopLink(Iterator descriptors) {	
        //do nothing       
	}
	
	public void rebuildClassIndicatorValues(Collection descriptors) {
        //do nothing       
	}
	

	// ************* aggregate support *************
	
	public void addToAggregateFieldNameGenerators(Collection generators) {
		//don't add self to Collection, overridden in MWRelationalClassIndicatorFieldPolicy
	}

	// ************* Containment Hierarchy *************
	
	public MWMappingDescriptor getContainingDescriptor() {
		return ((MWClassIndicatorPolicy.Parent) this.getParent()).getContainingDescriptor();
	}
	
	
	// ************* Model Synchronization *************
	 
	public void parentDescriptorMorphedToAggregate() {
        //do nothing       
	}

	
	// ************* Automap Support *************

	public void automap() {
        //do nothing       
	}


	// ************* Runtime Conversion *************

	public void adjustRuntimeInheritancePolicy(org.eclipse.persistence.descriptors.InheritancePolicy runtimeInheritancePolicy) {
        //do nothing       
	}


	// ************* Persistence *************
	
	public MWAbstractClassIndicatorPolicy getValueForTopLink() {
		return this;
	}


    // **************** Model synchronization *********************************
    
    /** @see MWXmlNode#resolveXpaths() */
    public void resolveXpaths() {
        //do nothing       
    }
    
    /** @see MWXmlNode.schemaChanged(SchemaChange) */
    public void schemaChanged(SchemaChange change) {
        //do nothing
    }
    

    // ************* TopLink methods *************
	
    public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractClassIndicatorPolicy.class);
		
		org.eclipse.persistence.descriptors.InheritancePolicy ip = (org.eclipse.persistence.descriptors.InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWClassIndicatorExtractionMethodPolicy.class, "extraction-method");
		ip.addClassIndicator(MWRelationalClassIndicatorFieldPolicy.class, "relational-field");
		ip.addClassIndicator(MWXmlClassIndicatorFieldPolicy.class, "xml-field");

		return descriptor;
	}
	
}
