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

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWSerializedObjectConverter extends MWConverter {

	// **************** Static methods *************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWSerializedObjectConverter.class);
		descriptor.getInheritancePolicy().setParentClass(MWConverter.class);

		return descriptor;
	}


	// ************* Constructors **************
	
	/** Default constructor - for TopLink use only */
	private MWSerializedObjectConverter() {
		super();
	}

	public MWSerializedObjectConverter(MWConverterMapping parent) {
		super(parent);
	}


	// *********** MWConverter implementation ***********
	
	/** Should ONLY be used in one place - the UI */
	public String accessibleNameKey() {
		return "ACCESSIBLE_SERIALIZED_MAPPING_NODE";
	}

	public String getType() {
		return SERIALIZED_OBJECT_CONVERTER;
	}

	public String iconKey() {
		return "mapping.serialized";
	}

	// ************* Runtime Conversion ************

	public Converter runtimeConverter(DatabaseMapping mapping) {
		return new SerializedObjectConverter(mapping);
	}

}
