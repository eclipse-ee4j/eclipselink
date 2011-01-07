/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTypeConversionConverter;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class MWConverter 
	extends MWModel 
{
	// **************** Variables *********************************************
	
	/** Used for converter type */
	public static final String NO_CONVERTER 				= "no-converter";
	public static final String OBJECT_TYPE_CONVERTER 		= "object-type-converter";
	public static final String SERIALIZED_OBJECT_CONVERTER 	= "serialized-object-converter";
	public static final String TYPE_CONVERSION_CONVERTER 	= "type-conversion-converter";
	
	
	// ************* Constructors **************
	
	/** Default constructor - for TopLink use only */
	protected MWConverter() {
		super();
	}

	MWConverter(MWConverterMapping parent) {
		super(parent);
	}
	
	
	// **************** API ******************
	
	/** Should ONLY be used in one place - the UI */
	public abstract String getType();

	public abstract String iconKey();

	public abstract String accessibleNameKey();

	
	// *************** Runtime Conversion **************

	public abstract Converter runtimeConverter(DatabaseMapping mapping);
		

	// ************** TopLink Methods ***************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWConverter.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWObjectTypeConverter.class, "object-type");
		ip.addClassIndicator(MWSerializedObjectConverter.class, "serialized-object");
		ip.addClassIndicator(MWRelationalTypeConversionConverter.class, "relational-type-conversion");
		ip.addClassIndicator(MWXmlTypeConversionConverter.class, "xml-type-conversion");
		
		return descriptor;
	}
	
	public /* package-protected */ MWConverter getValueForTopLink() {
		return this;
	}
}
