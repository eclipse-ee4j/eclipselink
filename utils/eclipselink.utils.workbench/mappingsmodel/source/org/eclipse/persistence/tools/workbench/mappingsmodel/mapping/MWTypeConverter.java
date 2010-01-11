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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;


public abstract class MWTypeConverter extends MWConverter {

	// **************** Fields ************************************************
	
	/** The data type when the object is written to the database */
	private volatile MWTypeDeclaration dataType;
		public final static String DATA_TYPE_PROPERTY = "dataType";
		
	/** The type of objects being inserted into the model objects */
	private volatile MWTypeDeclaration attributeType;
		public final static String ATTRIBUTE_TYPE_PROPERTY = "attributeType";
	

	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWTypeConverter() {
		super();
	}

	protected MWTypeConverter(MWConverterMapping parent) {
		super(parent);
	}
	
	//only used for legacy projects
	protected MWTypeConverter(MWConverterMapping parent, Map legacyValueMap) {
		super(parent);
		this.legacyInitialize(legacyValueMap);
	}
	
	
	// **************** Initialization ****************************************

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.attributeType = new MWTypeDeclaration(this, this.typeFor(String.class));
		this.dataType = new MWTypeDeclaration(this, this.typeFor(String.class));
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		
		children.add(this.attributeType);
		children.add(this.dataType);
	}
	
	protected void legacyInitialize(Map legacyValuesMap) {
		String databaseTypeString = (String) legacyValuesMap.remove("database-type");
		if (databaseTypeString != null) {
			MWClass databaseType = typeNamed(databaseTypeString);
			this.dataType = new MWTypeDeclaration(this, databaseType);
		}
		
		String attributeTypeString = (String) legacyValuesMap.remove("object-type");
		if (attributeTypeString != null) {
			MWClass attributeType = typeNamed(attributeTypeString);
			this.attributeType = new MWTypeDeclaration(this, attributeType);
		}
	}
	
	// **************** Attribute Type API ************************************
	
	public MWTypeDeclaration getAttributeType() {
		return this.attributeType;
	}
	
	public void setAttributeType(MWTypeDeclaration newAttributeType) {
		if (newAttributeType == null) {
			throw new NullPointerException("attributeType cannot be null");
		}
		MWTypeDeclaration oldAttributeType = getAttributeType();
		this.attributeType = newAttributeType;
		this.firePropertyChanged(ATTRIBUTE_TYPE_PROPERTY, oldAttributeType, newAttributeType);
		
		if (oldAttributeType != newAttributeType) {
			this.rebuildValuePairs();
		}
	}
	
	protected void rebuildValuePairs() {
		//do nothing, see MWObjectTypeConverter
	}
	
	
	// **************** Data Type API ************************************
	
	public MWTypeDeclaration getDataType() {
		return this.dataType;
	}
	
	public void setDataType(MWTypeDeclaration newValue) {
		if (newValue == null) {
			throw new NullPointerException("dataType cannot be null");
		}
		MWTypeDeclaration oldValue = getDataType();
		this.dataType = newValue;
		if (oldValue.getType() != newValue.getType()) {
			this.firePropertyChanged(DATA_TYPE_PROPERTY, oldValue, newValue);
			this.rebuildValuePairs();
		}
	}

	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWTypeConverter.class);
		descriptor.getInheritancePolicy().setParentClass(MWConverter.class);
		
		// data type
		XMLCompositeObjectMapping dataTypeMapping = new XMLCompositeObjectMapping();
		dataTypeMapping.setAttributeName("dataType");
		dataTypeMapping.setReferenceClass(MWTypeDeclaration.class);
		dataTypeMapping.setXPath("data-type");
		descriptor.addMapping(dataTypeMapping);
		


		// attribute type
		XMLCompositeObjectMapping attributeTypeMapping = new XMLCompositeObjectMapping();
		attributeTypeMapping.setAttributeName("attributeType");
		attributeTypeMapping.setReferenceClass(MWTypeDeclaration.class);
		attributeTypeMapping.setXPath("attribute-type");
		descriptor.addMapping(attributeTypeMapping);
				

		return descriptor;
	}

}
