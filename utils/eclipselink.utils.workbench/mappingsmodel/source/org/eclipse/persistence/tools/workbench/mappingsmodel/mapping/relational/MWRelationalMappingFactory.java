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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

public final class MWRelationalMappingFactory implements MWMappingFactory {

	private static MWRelationalMappingFactory INSTANCE;
	

	//	****************** static methods **************
	
	public static synchronized MWRelationalMappingFactory instance() {
		if (INSTANCE == null) {
			INSTANCE = new MWRelationalMappingFactory();
		}
		return INSTANCE;
	}

	public MWDirectMapping createDirectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWDirectToFieldMapping(descriptor, attribute, name);
	}
	
	public MWDirectCollectionMapping createDirectCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWRelationalDirectCollectionMapping((MWRelationalClassDescriptor) descriptor, attribute, name);
	}
	
	public MWDirectMapMapping createDirectMapMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWRelationalDirectMapMapping((MWRelationalClassDescriptor) descriptor, attribute, name);
	}
	
	public MWTransformationMapping createTransformationMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWRelationalTransformationMapping(descriptor, attribute, name);
	}

	public MWOneToOneMapping createOneToOneMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWOneToOneMapping((MWRelationalClassDescriptor) descriptor, attribute, name);
	}
	
	public MWDirectToXmlTypeMapping createDirectToXmlTypeMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWDirectToXmlTypeMapping(descriptor, attribute, name);
	}
	
	public MWVariableOneToOneMapping createVariableOneToOneMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWVariableOneToOneMapping((MWRelationalClassDescriptor) descriptor, attribute, name);
	}
	
	public MWAggregateMapping createAggregateMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWAggregateMapping(descriptor, attribute, name);
	}
	
	public MWOneToManyMapping createOneToManyMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWOneToManyMapping((MWRelationalClassDescriptor) descriptor, attribute, name);
	}
	
	public MWManyToManyMapping createManyToManyMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWManyToManyMapping((MWRelationalClassDescriptor) descriptor, attribute, name);
	}
	
}
