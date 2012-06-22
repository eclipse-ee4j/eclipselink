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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;

public final class MWAggregateDescriptor extends MWRelationalClassDescriptor 
{

	// ********** static methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAggregateDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWRelationalClassDescriptor.class);

		return descriptor;
	}
	
	// ********** constructors **********

	/** Default constructor - for TopLink use only */
	private MWAggregateDescriptor() {
		super();
	}
	
	public MWAggregateDescriptor(MWRelationalProject project, MWClass type, String name) {
		super(project, type, name);
	}

	
	// ********** MWRelationalDescriptor implementation **********
	
	public Iterator associatedTables() {
		return NullIterator.instance();
	}
	
	public int associatedTablesSize() {
		return 0;
	}
	
	public void notifyExpressionsToRecalculateQueryables() {
	}

	// ********** MWMappingDescriptor overrides **********

	public void addMapping(MWMapping mapping) {
		super.addMapping(mapping);
		this.getProject().recalculateAggregatePathsToColumn(this);
	}
	
	public void removeMapping(MWMapping mapping) {
		super.removeMapping(mapping);
		this.getProject().recalculateAggregatePathsToColumn(this);
	}

	protected void setInheritancePolicy(MWDescriptorInheritancePolicy newValue) {
		super.setInheritancePolicy(newValue);
		this.getProject().recalculateAggregatePathsToColumn(this);
	}


	//************** morphing support ***************
	
	public MWAggregateDescriptor asMWAggregateDescriptor() {
		return this;
	}

	public void initializeOn(MWDescriptor newDescriptor) {
		((MWRelationalDescriptor) newDescriptor).initializeFromMWAggregateDescriptor(this);
	}
	
	protected void initializeFromMWMappingDescriptor(MWMappingDescriptor oldDescriptor) {
		super.initializeFromMWMappingDescriptor(oldDescriptor);
		Iterator mappings = mappings();
		while (mappings.hasNext()) {
			MWMapping mapping = (MWMapping) mappings.next();
			mapping.parentDescriptorMorphedToAggregate();
		}
		getInheritancePolicy().parentDescriptorMorphedToAggregate();
	}

	public void initializeFromMWRelationalClassDescriptor(MWRelationalClassDescriptor oldDescriptor) {
		super.initializeFromMWRelationalClassDescriptor(oldDescriptor);
		
		for (Iterator i = userDefinedQueryKeys(); i.hasNext(); ){
			((MWUserDefinedQueryKey) i.next()).setColumn(null);
		}
	}
	
	public boolean isAggregateDescriptor() {
		return true;
	}


	public void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy) {
		defaultsPolicy.applyAdvancedPolicyDefaults(this);
	}	
	
	//******************* Problem Handling *******************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkReferences(newProblems);
		this.checkSharedAggregates(newProblems);
	}

	protected void checkMultipleMappingsWriteField(List newProblems) {
		// override this method to do nothing
	}

	protected String multipleMappingsWriteFieldProblemResourceStringKey() {
		throw new UnsupportedOperationException();
	}
	
	private void checkReferences(List newProblems) {
		// If the descriptor is an aggregate then other classes
		// cannot reference the aggregate with one-to-one,
		// one-to-many, or many-to-many mappings.
		// avoid null pointer exception when removing a descriptor
		for (Iterator allDescriptors = this.getProject().mappingDescriptors(); allDescriptors.hasNext(); ) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) allDescriptors.next();
			for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
				MWMapping mapping = (MWMapping) mappings.next();
				if (mapping instanceof MWAbstractTableReferenceMapping) {
					MWAbstractTableReferenceMapping tableRefMapping = (MWAbstractTableReferenceMapping) mapping;
					if (tableRefMapping.getReferenceDescriptor() == this) {
						newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_CLASSES_REFERENCE_AN_AGGREGATE_TARGET));
					}
				}
			}
		}
	}
	private void checkSharedAggregates(List newProblems) {
		// If the descriptor is an aggregate that is shared by
		// multiple source descriptors then it can't have
		// a mapping that has a target object that references it.
		// This means no one-to-many or many-to-many mappings and
		// no one-to-one mappings where the target has a
		// pointer to the aggregate.
		// NOTE: I haven't implemented the one-to-one part
		// check to see if the aggregate is shared
		int count = 0;
		// avoid null pointer exception when removing a descriptor
		for (Iterator allDescriptors = this.getProject().mappingDescriptors(); allDescriptors.hasNext(); ) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) allDescriptors.next();
			for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
				MWMapping mapping = (MWMapping) mappings.next();
				if (mapping instanceof MWAggregateMapping) {
					MWAggregateMapping aggMapping = (MWAggregateMapping) mapping;
					if (aggMapping.getReferenceDescriptor() == this) {
						count++;
					}
				}
			}
		}
		if (count < 2) {
			return;
		}
		// we know that the aggregate is shared
		// now check that it doesn't have one-to-many or
		// many-to-many mappings.
		for (Iterator mappings = this.mappings(); mappings.hasNext(); ) {
			MWMapping mapping = (MWMapping) mappings.next();
			if (mapping instanceof MWCollectionMapping) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_SHARED_AGGREGATE_HAS_1_TO_M_OR_M_TO_M_MAPPINGS));
			}
		}
	}
	
	
	// **************** runtime conversion **********
	
	protected ClassDescriptor buildBasicRuntimeDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(getMWClass().getName());
		return descriptor;
	}
	
	protected void adjustUserDefinedQueryKeys(ClassDescriptor runtimeDescriptor) {
		for (Iterator queryKeys = userDefinedQueryKeys(); queryKeys.hasNext();) {
			MWUserDefinedQueryKey queryKey = (MWUserDefinedQueryKey) queryKeys.next();
			DirectQueryKey runtimeQueryKey = new DirectQueryKey();
			runtimeQueryKey.setName(queryKey.getName());
			runtimeQueryKey.setFieldName(queryKey.fieldNameForRuntime());
			
			runtimeDescriptor.addQueryKey(runtimeQueryKey);
		}
	}	
}
