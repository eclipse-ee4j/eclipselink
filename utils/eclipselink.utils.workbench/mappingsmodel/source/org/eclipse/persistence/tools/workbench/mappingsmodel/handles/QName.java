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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroupDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;

import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


/**
 * For now, this is a utility class for use 
 * within the various schema component handles
 * by TopLink.
 */
public final class QName 
{
	private String componentType;
		public final static String ATTRIBUTE_TYPE 		= "attribute";
		public final static String ELEMENT_TYPE 		= "element";
		public final static String COMPLEX_TYPE_TYPE 	= "complex-type";
		public final static String GROUP_TYPE			= "group";
		
	private String namespaceURI;
	
	private String localName;
	
	
	// ********** static methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(QName.class);
		
		XMLDirectMapping componentTypeMapping = new XMLDirectMapping();
		componentTypeMapping.setAttributeName("componentType");
		componentTypeMapping.setXPath("@component-type");
		
		ObjectTypeConverter componentTypeConverter = new ObjectTypeConverter();
		componentTypeConverter.addConversionValue(QName.ATTRIBUTE_TYPE, QName.ATTRIBUTE_TYPE);
		componentTypeConverter.addConversionValue(QName.ELEMENT_TYPE, QName.ELEMENT_TYPE);
		componentTypeConverter.addConversionValue(QName.COMPLEX_TYPE_TYPE, QName.COMPLEX_TYPE_TYPE);
		componentTypeConverter.addConversionValue(QName.GROUP_TYPE, QName.GROUP_TYPE);
		componentTypeMapping.setConverter(componentTypeConverter);
		
		descriptor.addMapping(componentTypeMapping);
		
		((XMLDirectMapping)descriptor.addDirectMapping("namespaceURI", "@namespace-uri")).setNullValue("");
		descriptor.addDirectMapping("localName", "@local-name");
		
		return descriptor;
	}
	
	
	private QName() {
		super();
	}
	
	QName(MWNamedSchemaComponent namedSchemaComponent) {
		this();
		
		if (namedSchemaComponent instanceof MWAttributeDeclaration) {
			this.componentType = ATTRIBUTE_TYPE;
		}
		else if (namedSchemaComponent instanceof MWElementDeclaration) {
			this.componentType = ELEMENT_TYPE;
		}
		else if (namedSchemaComponent instanceof MWComplexTypeDefinition) {
			this.componentType = COMPLEX_TYPE_TYPE;
		}
		else if (namedSchemaComponent instanceof MWModelGroupDefinition) {
			this.componentType = GROUP_TYPE;
		}
		else {
			throw new IllegalArgumentException("Unsupported component type");
		}
		
		this.namespaceURI = namedSchemaComponent.getNamespaceUrl();
		this.localName = namedSchemaComponent.getName();
	}
	
	public String getComponentType() {
		return this.componentType;
	}
	
	public String getNamespaceURI() {
		return this.namespaceURI;
	}
	
	public String getLocalName() {
		return this.localName;
	}
}
