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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeObjectMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.eis.mappings.EISTransformationMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;


public abstract class MWEisDescriptor 
	extends MWXmlDescriptor 
{	
	// **************** Constructors **************
	
	MWEisDescriptor() {
		super();
	}

	protected MWEisDescriptor(MWEisProject project, MWClass type, String name) {
		super(project, type, name);
	}


	// **************** Inheritance policy ************************************
	
	protected MWDescriptorInheritancePolicy buildInheritancePolicy() {
		return new MWEisDescriptorInheritancePolicy(this);
	}


	// **************** morphing **************
	
	public MWCompositeEisDescriptor asCompositeEisDescriptor() throws InterfaceDescriptorCreationException {
		MWCompositeEisDescriptor newDescriptor= (MWCompositeEisDescriptor) this.getProject().addDescriptorForType(this.getMWClass());

		this.initializeDescriptorAfterMorphing(newDescriptor);
		return newDescriptor;
	}

	public MWRootEisDescriptor asRootEisDescriptor() {
		MWRootEisDescriptor newDescriptor = ((MWEisProject) this.getProject()).addRootEisDescriptorForType(this.getMWClass());	
		this.initializeDescriptorAfterMorphing(newDescriptor);
		return newDescriptor;
	}
	

	public void initializeOn(MWDescriptor newDescriptor) {
		((MWEisDescriptor) newDescriptor).initializeFromMWEisDescriptor(this);
	}	
	
	protected void initializeFromMWEisDescriptor(MWEisDescriptor oldDescriptor) {
		super.initializeFromMWXmlDescriptor(oldDescriptor);
	}
	
	// **************** runtime conversion ************************************
	
	protected ClassDescriptor buildBasicRuntimeDescriptor() {
		EISDescriptor runtimeDescriptor = new EISDescriptor();
		runtimeDescriptor.setJavaClassName(getMWClass().getName());
		return runtimeDescriptor;
	}
	
	public ClassDescriptor buildRuntimeDescriptor() {
		EISDescriptor runtimeDescriptor = (EISDescriptor) super.buildRuntimeDescriptor();
		
		runtimeDescriptor.setNamespaceResolver(this.runtimeNamespaceResolver());
		
		return runtimeDescriptor;
	}
	
	protected void adjustRuntimeDescriptorRootProperties(ClassDescriptor runtimeDescriptor) {
		super.adjustRuntimeDescriptorRootProperties(runtimeDescriptor);
		
		EISDescriptor runtimeEisDescriptor = (EISDescriptor) runtimeDescriptor;
		
		if (this.getDefaultRootElement() != null) {
			runtimeEisDescriptor.setDataTypeName(this.getDefaultRootElement().qName());
		}
		else if (this.getSchemaContext() != null) {
			// an eis descriptor always needs a data type - use the schema context element/complex type
			runtimeEisDescriptor.setDataTypeName(this.getSchemaContext().qName());
		}
	}
	
	public AbstractDirectMapping buildDefaultRuntimeDirectMapping() {
		return new EISDirectMapping();
	}
	
	public AbstractCompositeDirectCollectionMapping buildDefaultRuntimeDirectCollectionMapping() {
		return new EISCompositeDirectCollectionMapping();
	}
	
	public AbstractCompositeObjectMapping buildDefaultRuntimeCompositeObjectMapping() {
		return new EISCompositeObjectMapping();
	}
	
	public AbstractCompositeCollectionMapping buildDefaultRuntimeCompositeCollectionMapping() {
		return new EISCompositeCollectionMapping();
	}
	
	public AbstractTransformationMapping buildDefaultRuntimeTransformationMapping() {
		return new EISTransformationMapping();
	}

	//	**************** mappings ***************
	
	public MWMappingFactory mappingFactory() {
		return MWEisMappingFactory.instance();
	}

	public MWEisOneToOneMapping addEisOneToOneMapping(MWClassAttribute attribute) {
		MWEisOneToOneMapping mapping = 
			((MWEisMappingFactory) mappingFactory()).createEisOneToOneMapping(this, attribute, attribute.getName());
		addMapping(mapping);
		return mapping;
	}

	public MWEisOneToManyMapping addEisOneToManyMapping(MWClassAttribute attribute) {
		MWEisOneToManyMapping mapping = 
			((MWEisMappingFactory) mappingFactory()).createEisOneToManyMapping(this, attribute, attribute.getName());
		addMapping(mapping);
		return mapping;
	}
	
	@Override
	public boolean isEisDescriptor() {
		return true;
	}
}
