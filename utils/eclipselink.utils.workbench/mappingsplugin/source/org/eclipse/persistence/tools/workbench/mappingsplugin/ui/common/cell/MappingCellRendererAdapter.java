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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyAttributeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlCollectionReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlObjectReferenceMapping;


public class MappingCellRendererAdapter extends NoneSelectedCellRendererAdapter {
	
	public MappingCellRendererAdapter(ResourceRepository repository) {
		super(repository);
	}

    protected Icon buildNonNullValueIcon(Object value) {
		return resourceRepository().getIcon(iconKey((MWMapping) value));
	}

	protected String buildNonNullValueText(Object value) {
		return ((MWMapping) value).getName();
	}
	
	private String iconKey(MWMapping mapping) {
		if (mapping instanceof MWRelationalDirectMapping) {
			return ((MWRelationalDirectMapping) mapping).iconKey();
		}
		else if (mapping instanceof MWRelationalDirectCollectionMapping) {
			return "mapping.directCollection";
		}
		else if (mapping instanceof MWRelationalDirectMapMapping) {
			return "mapping.directMap";
		}
		else if (mapping instanceof MWOneToOneMapping) {
			return "mapping.oneToOne";
		}
		else if (mapping instanceof MWVariableOneToOneMapping) {
			return "mapping.variableOneToOne";
		}
		else if (mapping instanceof MWOneToManyMapping) {
			return "mapping.oneToMany";
		}
		else if (mapping instanceof MWManyToManyMapping) {
			return "mapping.manyToMany";
		}
		else if (mapping instanceof MWAggregateMapping) {
			return "mapping.aggregate";
		}
		else if (mapping instanceof MWTransformationMapping) {
			return "mapping.transformation";
		}
		else if (mapping instanceof MWXmlDirectMapping) {
			return "mapping.xmlDirect";
		}
		else if (mapping instanceof MWXmlDirectCollectionMapping) {
			return "mapping.xmlDirectCollection";
		}
		else if (mapping instanceof MWCompositeObjectMapping) {
			return "mapping.compositeObject";
		}
		else if (mapping instanceof MWCompositeCollectionMapping) {
			return "mapping.compositeCollection";
		}
		else if (mapping instanceof MWAnyObjectMapping) {
			return "mapping.anyObject";
		}
		else if (mapping instanceof MWAnyCollectionMapping) {
			return "mapping.anyCollection";
		}
		else if (mapping instanceof MWEisOneToOneMapping) {
			return "mapping.eisOneToOne";
		}
		else if (mapping instanceof MWEisOneToManyMapping) {
			return "mapping.eisOneToMany";
		}
		else if (mapping instanceof MWAnyAttributeMapping) {
			return "mapping.anyAttribute";
		}
		else if (mapping instanceof MWXmlObjectReferenceMapping) {
			return "mapping.objectReference";
		}
		else if (mapping instanceof MWXmlCollectionReferenceMapping) {
			return "mapping.collectionReference";
		}
		else if (mapping instanceof MWXmlFragmentMapping) {
			return "mapping.xmlFragment";
		}
		else if (mapping instanceof MWXmlFragmentCollectionMapping) {
			return "mapping.xmlFragmentCollection";
		}
		else {
			return null;
		}
	}	

	public String buildAccessibleName(Object value) {
		MWMapping mapping = (MWMapping) value;
		StringBuffer sb = new StringBuffer();

		sb.append(resourceRepository().getString(iconKey(mapping)));
		sb.append(" ");
		sb.append(buildText(value));

		return sb.toString();
	}

}
