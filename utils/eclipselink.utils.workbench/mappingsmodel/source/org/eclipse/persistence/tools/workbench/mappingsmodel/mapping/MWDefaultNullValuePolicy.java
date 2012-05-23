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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWDefaultNullValuePolicy 
	extends MWModel 
	implements MWNullValuePolicy {

	private volatile String nullValue;
	
	private volatile MWTypeDeclaration nullValueType;
	 


	// **************** Static methods **************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWDefaultNullValuePolicy.class);
						
		XMLCompositeObjectMapping nullValueTypeMapping = new XMLCompositeObjectMapping();
		nullValueTypeMapping.setAttributeName("nullValueType");
		nullValueTypeMapping.setReferenceClass(MWTypeDeclaration.class);
		nullValueTypeMapping.setXPath("null-value-type");
		descriptor.addMapping(nullValueTypeMapping);
		
		XMLDirectMapping nullValueMapping = (XMLDirectMapping) descriptor.addDirectMapping("nullValue", "null-value/text()");
		nullValueMapping.setNullValue("");
		
		return descriptor;	
	}


	// ************* Constructors **************
	
	/** Default constructor - for TopLink use only */
	private MWDefaultNullValuePolicy() {
		super();
	}

	public MWDefaultNullValuePolicy(MWModel parent) {
		super(parent);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.nullValueType = new MWTypeDeclaration(this, this.typeFor(java.lang.String.class));
		this.nullValue = "";
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.nullValueType);
	}


	// *************** Accessors **************
	
	public boolean usesNullValue() {
		return true;
	}
	
	public String getNullValue() {
		return this.nullValue;
	}
	
	public void setNullValue(String newValue) {
		String oldValue = this.nullValue;
		this.nullValue = newValue;
		this.firePropertyChanged(NULL_VALUE_PROPERTY, oldValue, newValue);
	}
	
	public MWTypeDeclaration getNullValueType() {
		return this.nullValueType;
	}
	
	public void setNullValueType(MWTypeDeclaration newNullType) {
		if (newNullType == null) {
			throw new NullPointerException();
		}
		MWTypeDeclaration oldValue = getNullValueType();
		this.nullValueType = newNullType;
		this.firePropertyChanged(NULL_VALUE_TYPE_PROPERTY, oldValue, newNullType);
	}
	
	
	// **************** Runtime conversion ****************
    
	public void adjustRuntimeMapping(AbstractDirectMapping mapping) {
		mapping.setNullValue(this.nullValue());
	}
	
	protected /* private-protected */ Object nullValue() {
		if (this.nullValue == null || getNullValueType() == null)
			return null;
    	
		Class nullClass = null;
		
		try {
			nullClass = Class.forName(getNullValueType().typeName());
		}
		catch (ClassNotFoundException cnfe) {
			// can't do anything with it, just don't do anything
			return null;
		}
		
		ConversionManager cm = ConversionManager.getDefaultManager();
		
		try {
			return cm.convertObject(this.nullValue, nullClass);
		}
		catch (ConversionException ce) {
		 // can't do anything with it, just don't do anything
			return null;
		}
	}


	// **************** TopLink Only Methos ***************
 
	public MWNullValuePolicy getValueForTopLink() {
		return this;
	}	

}
