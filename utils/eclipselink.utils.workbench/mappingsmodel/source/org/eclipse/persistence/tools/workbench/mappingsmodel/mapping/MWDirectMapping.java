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

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Record;

public abstract class MWDirectMapping 
	extends MWMapping
	implements MWConverterMapping
{
	// **************** Variables *********************************************
	
	private volatile MWNullValuePolicy nullValuePolicy;
		public final static String USES_NULL_VALUE_PROPERTY = "usesNullValue";
	
	private volatile MWConverter converter;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWDirectMapping() {
		super();
	}
	
	protected MWDirectMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	// **************** Initialization ****************************************

	/** initialize persistent state */
	protected void initialize(Node parent) {
		super.initialize(parent);	
		this.nullValuePolicy = new MWNullNullValuePolicy(this);
		this.converter = new MWNullConverter(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.converter);
		children.add(this.nullValuePolicy);
	}
	
	
	// **************** Null value policy *************************************
	
	public MWNullValuePolicy getNullValuePolicy() {
		return this.nullValuePolicy;
	}
	
	public boolean usesNullValue() {
		return this.nullValuePolicy.usesNullValue();
	}
	
	public void setUseNullValue(boolean useNullValue) {
		boolean oldValue = this.usesNullValue();
		
		if (oldValue == useNullValue) {
			return;
		}
		
		if (useNullValue) {
			this.nullValuePolicy = new MWDefaultNullValuePolicy(this);
		}
		else {
			this.nullValuePolicy = new MWNullNullValuePolicy(this);
		}
		
		this.firePropertyChanged(USES_NULL_VALUE_PROPERTY, oldValue, useNullValue);
	}
	
	
	// **************** Converter *********************************************
	
	public MWConverter getConverter() {
		return this.converter;
	}
	
	public MWNullConverter setNullConverter() {
		MWNullConverter nullConverter = new MWNullConverter(this);
		this.setConverter(nullConverter);
		return nullConverter;
	}
	
	public MWObjectTypeConverter setObjectTypeConverter() {
		MWObjectTypeConverter objectTypeConverter = new MWObjectTypeConverter(this);
		this.setConverter(objectTypeConverter);
		return objectTypeConverter;
	}
	
	public MWSerializedObjectConverter setSerializedObjectConverter() {
		MWSerializedObjectConverter serializedObjectConverter = new MWSerializedObjectConverter(this);
		this.setConverter(serializedObjectConverter);
		return serializedObjectConverter;
	}
	
	public MWTypeConversionConverter setTypeConversionConverter() {
		MWTypeConversionConverter typeConversionConverter = buildTypeConversionConverter();
		this.setConverter(typeConversionConverter);
		return typeConversionConverter;
	}
	
	protected abstract MWTypeConversionConverter buildTypeConversionConverter();
	
	private void setConverter(MWConverter newConverter) {
		MWConverter oldConverter = this.converter;
		this.converter = newConverter;
		newConverter.setParent(this); // This step only important when morphing the mapping
		this.firePropertyChanged(CONVERTER_PROPERTY, oldConverter, newConverter);
	}
	
	
	// **************** Morphing **********************************************
	
	protected void initializeFromMWDirectMapping(MWDirectMapping oldMapping) {
		super.initializeFromMWDirectMapping(oldMapping);
		
		this.setUseNullValue(oldMapping.usesNullValue());
		
		if (this.usesNullValue()) {
			this.getNullValuePolicy().setNullValue(oldMapping.getNullValuePolicy().getNullValue());
			this.getNullValuePolicy().setNullValueType(oldMapping.getNullValuePolicy().getNullValueType());
		}
	}
	
	protected void initializeFromMWConverterMapping(MWConverterMapping converterMapping) {
		super.initializeFromMWConverterMapping(converterMapping);
		
		this.setConverter(converterMapping.getConverter());
	}
		
	// **************** Runtime conversion ************************************
	
	public DatabaseMapping runtimeMapping() {
		AbstractDirectMapping mapping = (AbstractDirectMapping) super.runtimeMapping();
		this.nullValuePolicy.adjustRuntimeMapping(mapping);
		mapping.setConverter(this.getConverter().runtimeConverter(mapping));
		return mapping;
	}

	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWDirectMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		XMLCompositeObjectMapping nullValuePolicyMapping = new XMLCompositeObjectMapping();
		nullValuePolicyMapping.setReferenceClass(MWDefaultNullValuePolicy.class);
		nullValuePolicyMapping.setAttributeName("nullValuePolicy");
		nullValuePolicyMapping.setGetMethodName("getNullValuePolicyForTopLink");
		nullValuePolicyMapping.setSetMethodName("setNullValuePolicyForTopLink");
		nullValuePolicyMapping.setXPath("null-value-policy");
		descriptor.addMapping(nullValuePolicyMapping);	
		
		XMLCompositeObjectMapping converterMapping = new XMLCompositeObjectMapping();
		converterMapping.setReferenceClass(MWConverter.class);
		converterMapping.setAttributeName("converter");
		converterMapping.setGetMethodName("getConverterForTopLink");
		converterMapping.setSetMethodName("setConverterForTopLink");
		converterMapping.setXPath("converter");
		descriptor.addMapping(converterMapping);	
		
		return descriptor;	
	}
	
	private MWNullValuePolicy getNullValuePolicyForTopLink() {
		return (this.nullValuePolicy == null) ? null : this.nullValuePolicy.getValueForTopLink();
	}
	
	private void setNullValuePolicyForTopLink(MWNullValuePolicy nullValuePolicy) {
		if (nullValuePolicy == null) {
			this.nullValuePolicy = new MWNullNullValuePolicy(this);
		}
		else {
			this.nullValuePolicy = nullValuePolicy;
		}
	}

	private MWConverter getConverterForTopLink() {
		return (this.converter == null) ? null : this.converter.getValueForTopLink();
	}
	
	private void setConverterForTopLink(MWConverter converter) {
		if (converter == null) {
			this.converter = new MWNullConverter(this);
		}
		else {
			this.converter = converter;
		}
	}
	
}
