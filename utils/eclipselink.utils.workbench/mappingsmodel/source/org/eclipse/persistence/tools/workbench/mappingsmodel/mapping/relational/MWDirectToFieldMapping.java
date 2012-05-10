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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;

public final class MWDirectToFieldMapping 
	extends MWRelationalDirectMapping
{	
	// **************** Constructors ***************

	/** Default constructor - for TopLink use only */
	private MWDirectToFieldMapping() {
		super();
	}

	MWDirectToFieldMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	// **************** MWQueryable implementation ***************

	/** Direct mappings can never have sub queryable elements */
	public boolean allowsChildren() {
		return false;
	}
	
	public boolean allowsOuterJoin() {
		return allowsChildren();
	}

	/** A direct mapping will always be a leaf */
	public boolean isLeaf(Filter queryableFilter) {
		return true;
	}

	public boolean isTraversableForReadAllQueryOrderable() {
		return true;
	}
	
	public boolean isValidForReadAllQueryOrderable() {
		return true;
	}
	
	public boolean isValidForReportQueryAttribute() {
	    return true;
	}
    
    public boolean isTraversableForReportQueryAttribute() {
        return true;
    }
    
    public boolean isTraversableForQueryExpression() {
        return true;
    }
    
    public boolean isValidForQueryExpression() {
        return true;
    }
    
	public String iconKey() {
		return getConverter().iconKey();		
	}
	
 	public MWMappingDescriptor getParentDescriptor() {
		return (MWMappingDescriptor) getParent();
	}
	
	public String accessibleNameKey() {
		return getConverter().accessibleNameKey();
	}

	
	// ***************** Morphing ******************
	
	public MWDirectMapping asMWDirectMapping() {
		if (!getConverter().getType().equals(MWConverter.NO_CONVERTER)) {
			this.setNullConverter();
		}
		return this;
	}
	
	public MWDirectMapping asMWObjectTypeMapping() {
		if (!getConverter().getType().equals(MWConverter.OBJECT_TYPE_CONVERTER)) {
			this.setObjectTypeConverter();
		}
		return this;
	}
	
	public MWDirectMapping asMWTypeConversionMapping() {
		if (!getConverter().getType().equals(MWConverter.TYPE_CONVERSION_CONVERTER)) {
			this.setTypeConversionConverter();
		}
		return this;
	}
	
	public MWDirectMapping asMWSerializedMapping() {
		if (!getConverter().getType().equals(MWConverter.SERIALIZED_OBJECT_CONVERTER)) {
			this.setSerializedObjectConverter();
		}
			
		return this;
	}

	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWDirectToFieldMapping(this);
	}
	
	
	protected void initializeFromMWConverterMapping(MWConverterMapping converterMapping) {
		// only initialize my converter if I already have the same type of converter
		if (this.getConverter().getType() == converterMapping.getConverter().getType()) {
			super.initializeFromMWConverterMapping(converterMapping);
		}
	}
	
	// **************** Runtime Conversion ****************
	
	protected DatabaseMapping buildRuntimeMapping() {
		if (((MWRelationalProject) getProject()).isGenerateDeprecatedDirectMappings()) {
			if (getConverter().getType().equals((MWConverter.NO_CONVERTER))) {
				return new DirectToFieldMapping();
			}
			else if (getConverter().getType().equals((MWConverter.TYPE_CONVERSION_CONVERTER))) {
				DirectToFieldMapping mapping = new DirectToFieldMapping();
				mapping.setConverter(new TypeConversionConverter());
				return mapping;
			}
			else if (getConverter().getType().equals((MWConverter.OBJECT_TYPE_CONVERTER))) {
				DirectToFieldMapping mapping = new DirectToFieldMapping();
				mapping.setConverter(new ObjectTypeConverter());
				return mapping;			
			} 
			else {
				DirectToFieldMapping mapping = new DirectToFieldMapping();
				mapping.setConverter(new SerializedObjectConverter());
				return mapping;
			}
		}
		else {
			return new DirectToFieldMapping();
		}
	}
	
	
	//********** display methods **********
	
	public void toString(StringBuffer sb) {
		sb.append(this.getName());
		sb.append(" => ");
		sb.append(this.getColumn() == null ? "null" : this.getColumn().qualifiedName());
	}
	
	
	// **************** TopLink Methods *****************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWDirectToFieldMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWRelationalDirectMapping.class);
		
		return descriptor;	
	}
	
}
