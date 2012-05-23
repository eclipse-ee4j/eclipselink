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

import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;

//TODO problem messages if the attribute type  changes and no longer matches the
//specified attributeType?  Same goes for dataType, if the databaseField type changes,
//we might not be able to convert to the specified dataType
public abstract class MWTypeConversionConverter extends MWTypeConverter {

	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWTypeConversionConverter.class);
		descriptor.getInheritancePolicy().setParentClass(MWTypeConverter.class);
		return descriptor;
	}


	// **************** Constructors ******************
	
	/** Default constructor - for TopLink use only */
	protected MWTypeConversionConverter() {
		super();
	}

	protected MWTypeConversionConverter(MWConverterMapping parent) {
		super(parent);
	}

	
	public Set getBasicTypes() {
		return this.buildBasicTypes();
	}
	
	
	// ************** MWConverter implementation *************

	public String accessibleNameKey() {
		return "ACCESSIBLE_TYPE_CONVERSION_MAPPING_NODE";
	}

	public String getType() {
		return TYPE_CONVERSION_CONVERTER;
	}

	public String iconKey() {
		return "mapping.typeConversion";
	}

	
	// ************* Runtime Conversion *************
	
	public Converter runtimeConverter(DatabaseMapping mapping) {
		TypeConversionConverter converter = new TypeConversionConverter(mapping);
		converter.setDataClassName(ClassTools.classNameForTypeDeclaration(getDataType().typeName(), getDataType().getDimensionality()));
		converter.setObjectClassName(ClassTools.classNameForTypeDeclaration(getAttributeType().typeName(), getAttributeType().getDimensionality()));
		return converter;
	}
}
