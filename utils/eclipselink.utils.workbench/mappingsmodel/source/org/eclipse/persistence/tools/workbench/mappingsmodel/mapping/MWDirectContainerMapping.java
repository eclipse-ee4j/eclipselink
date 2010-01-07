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
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Record;


public abstract class MWDirectContainerMapping extends MWMapping 
	implements MWConverterMapping
{

	/** 
	 * For now, only used for MWXmlDirectCollectionMapping.
	 * MWRelationalDirectCollectionMapping should always have a null converter.
	 */
	private MWConverter directValueConverter;


	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWDirectContainerMapping() {
		super();
	}

	public MWDirectContainerMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.directValueConverter = new MWNullConverter(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.directValueConverter);
	}
	

	// **************** Converter *********************************************
	
	public MWConverter getConverter() {
		return this.directValueConverter;
	}
	
	public MWNullConverter setNullConverter() {
		MWNullConverter nullConverter = new MWNullConverter(this);
		this.setDirectValueConverter(nullConverter);
		return nullConverter;
	}
	
	public MWObjectTypeConverter setObjectTypeConverter() {
		MWObjectTypeConverter objectTypeConverter = new MWObjectTypeConverter(this);
		this.setDirectValueConverter(objectTypeConverter);
		return objectTypeConverter;
	}
	
	public MWSerializedObjectConverter setSerializedObjectConverter() {
		MWSerializedObjectConverter serializedObjectConverter = new MWSerializedObjectConverter(this);
		this.setDirectValueConverter(serializedObjectConverter);
		return serializedObjectConverter;
	}
	
	public MWTypeConversionConverter setTypeConversionConverter() {
		MWTypeConversionConverter typeConversionConverter = buildTypeConversionConverter();
		this.setDirectValueConverter(typeConversionConverter);
		return typeConversionConverter;
	}
	
	protected abstract MWTypeConversionConverter buildTypeConversionConverter();
	
	private void setDirectValueConverter(MWConverter newConverter) {
		MWConverter oldConverter = this.directValueConverter;
		this.directValueConverter = newConverter;
		newConverter.setParent(this); // This step only important when morphing the mapping
		this.firePropertyChanged(CONVERTER_PROPERTY, oldConverter, newConverter);
	}
 
    public boolean usesTransparentIndirection() {
        return false;
    }
    
	// **************** Morphing **********************************************
	
	protected void initializeFromMWConverterMapping(MWConverterMapping converterMapping) {
		super.initializeFromMWConverterMapping(converterMapping);
		this.setDirectValueConverter(converterMapping.getConverter());
	}
	
	
	// **************** TopLink methods *******************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWDirectContainerMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		XMLCompositeObjectMapping converterMapping = new XMLCompositeObjectMapping();
		converterMapping.setReferenceClass(MWConverter.class);
		converterMapping.setAttributeName("directValueConverter");
		converterMapping.setGetMethodName("getConverterForTopLink");
		converterMapping.setSetMethodName("setConverterForTopLink");
		converterMapping.setXPath("direct-value-converter");
		descriptor.addMapping(converterMapping);	
		
		return descriptor;
	}
	
	private MWConverter getConverterForTopLink() {
		return (this.directValueConverter == null) ? null : this.directValueConverter.getValueForTopLink();
	}
	private void setConverterForTopLink(MWConverter converter) {
		if (converter == null) {
			this.directValueConverter = new MWNullConverter(this);
		} else {
			this.directValueConverter = converter;
		}
	}

}
