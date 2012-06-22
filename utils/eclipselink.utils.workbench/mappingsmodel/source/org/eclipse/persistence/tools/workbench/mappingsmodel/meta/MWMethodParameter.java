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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWMethodParameter extends MWModel {

	private MWTypeDeclaration typeDeclaration;		// pseudo-final
		public static final String TYPE_PROPERTY = "type";
		public static final String DIMENSIONALITY_PROPERTY = "dimensionality";

	
	//	********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWMethodParameter() {
		super();
	}
	
	private MWMethodParameter(MWMethod parent) {
		super(parent);
	}
	
	MWMethodParameter(MWMethod parent, MWClass type) {
		this(parent, type, 0);
	}
	
	MWMethodParameter(MWMethod parent, MWClass type, int dimensionality) {
		this(parent);
		this.typeDeclaration = new MWTypeDeclaration(this, type, dimensionality);
	}	
	
	MWMethodParameter(MWMethod parent, ExternalClassDescription externalClassDescription) {
		this(parent);
		this.typeDeclaration = new MWTypeDeclaration(this, externalClassDescription);
	}

	// only for 5.0 backward compatibility
	MWMethodParameter(MWMethod parent, MWTypeDeclaration typeDeclaration) {
		this(parent);
		this.typeDeclaration = typeDeclaration;
	}


	// ********** accessors **********

	MWMethod getMethod() {
		return (MWMethod) this.getParent();
	}

	MWTypeDeclaration getTypeDeclaration() {
		return this.typeDeclaration;
	}
	
	public MWClass getType() {
		return this.typeDeclaration.getType();
	}
	
	public void setType(MWClass type) {
		if (type.isVoid()) {
			throw new IllegalArgumentException("A method parameter cannot have a type of 'void'");
		}		
		Object old = this.typeDeclaration.getType();
		this.typeDeclaration.setType(type);
		this.firePropertyChanged(TYPE_PROPERTY, old, type);
		if (this.attributeValueHasChanged(old, type)) {
			this.getMethod().parameterChanged();
		}
	}
	
	public int getDimensionality() {
		return this.typeDeclaration.getDimensionality();
	}

	public void setDimensionality(int dimensionality) {
		int old = this.typeDeclaration.getDimensionality();
		this.typeDeclaration.setDimensionality(dimensionality);
		this.firePropertyChanged(DIMENSIONALITY_PROPERTY, old, dimensionality);
		if (old != dimensionality) {
			this.getMethod().parameterChanged();
		}
	}


	// ********** queries **********

	boolean isAssignableFrom(MWClass otherType) {
		return this.typeDeclaration.isAssignableFrom(otherType);
	}

	boolean mightBeAssignableFrom(MWClass otherType) {
		return this.typeDeclaration.mightBeAssignableFrom(otherType);
	}

	boolean isAssignableFrom(MWClass otherType, int otherDimensionality) {
		return this.typeDeclaration.isAssignableFrom(otherType, otherDimensionality);
	}
	
	boolean mightBeAssignableFrom(MWClass otherType, int otherDimensionality) {
		return this.typeDeclaration.mightBeAssignableFrom(otherType, otherDimensionality);
	}
	
	boolean isAssignableFrom(MWTypeDeclaration otherTypeDeclaration) {
		return this.typeDeclaration.isAssignableFrom(otherTypeDeclaration);
	}

	boolean mightBeAssignableFrom(MWTypeDeclaration otherTypeDeclaration) {
		return this.typeDeclaration.mightBeAssignableFrom(otherTypeDeclaration);
	}

	boolean isAssignableTo(MWClass otherType) {
		return this.typeDeclaration.isAssignableTo(otherType);
	}

	boolean mightBeAssignableTo(MWClass otherType) {
		return this.typeDeclaration.mightBeAssignableTo(otherType);
	}

	boolean isAssignableTo(MWClass otherType, int otherDimensionality) {
		return this.typeDeclaration.isAssignableTo(otherType, otherDimensionality);
	}
	
	boolean mightBeAssignableTo(MWClass otherType, int otherDimensionality) {
		return this.typeDeclaration.mightBeAssignableTo(otherType, otherDimensionality);
	}
	
	boolean isAssignableTo(MWTypeDeclaration otherTypeDeclaration) {
		return this.typeDeclaration.isAssignableTo(otherTypeDeclaration);
	}

	boolean mightBeAssignableTo(MWTypeDeclaration otherTypeDeclaration) {
		return this.typeDeclaration.mightBeAssignableTo(otherTypeDeclaration);
	}

	boolean matches(MWTypeDeclaration other) {
		return this.typeDeclaration.matches(other);
	}

	boolean hasSameSignatureAs(ExternalClassDescription externalClassDescription) {
		return this.typeDeclaration.hasSameSignatureAs(externalClassDescription);
	}

	boolean hasSameSignatureAs(MWMethodParameter methodParameter) {
		return this.typeDeclaration.hasSameSignatureAs(methodParameter);
	}

	public String declaration() {
		return this.typeDeclaration.declaration();
	}


	// ********** behavior **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.typeDeclaration);
	}

	public void nodeRenamed(Node node) {
		super.nodeRenamed(node);
		if (this.getType() == node) {
			this.getMethod().parameterChanged();
		}
	}


	// ********** displaying and printing **********

	public void toString(StringBuffer sb) {
		this.typeDeclaration.toString(sb);
	}

	void printSignatureOn(StringBuffer sb) {
		this.typeDeclaration.printSignatureOn(sb);
	}

	void printShortSignatureOn(StringBuffer sb) {
		this.typeDeclaration.printShortSignatureOn(sb);
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWMethodParameter.class);
		
		XMLCompositeObjectMapping returnTypeDeclarationMapping = new XMLCompositeObjectMapping();
		returnTypeDeclarationMapping.setAttributeName("typeDeclaration");
		returnTypeDeclarationMapping.setReferenceClass(MWTypeDeclaration.class);
		returnTypeDeclarationMapping.setXPath("type-declaration");
		descriptor.addMapping(returnTypeDeclarationMapping);

		return descriptor;
	}
	
}
