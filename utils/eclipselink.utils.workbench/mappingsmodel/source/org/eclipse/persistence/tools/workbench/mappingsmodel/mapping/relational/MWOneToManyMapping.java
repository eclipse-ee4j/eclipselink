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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodCodeGenPolicy;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWOneToManyMapping 
	extends MWCollectionMapping 
{

	private MWOneToManyMapping() {
		super();
	}
	
	MWOneToManyMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}
	
	public MWOneToManyMapping asMWOneToManyMapping() {
		return this;
	}
		
	/**
	* IMPORTANT:  See MWRMapping class comment.
	*/
	protected void initializeOn(MWMapping newMapping)  {
		newMapping.initializeFromMWOneToManyMapping(this);
	}
	
	/**
	 * Return a 1-1 mapping in the reference descriptor that uses the same MWReference as this mapping
	 */
	protected MWOneToOneMapping backPointerMapping() {
		if (getReferenceDescriptor() == null)
			return null;
		
		for (Iterator it = getReferenceDescriptor().mappings(); it.hasNext(); ) {
			MWMapping mapping = (MWMapping) it.next();
			
			if (mapping instanceof MWOneToOneMapping) {	
				MWOneToOneMapping oneToOneMapping = (MWOneToOneMapping) mapping;
				
				if (oneToOneMapping.getReference() != null && oneToOneMapping.getReference() == this.getReference())
					return oneToOneMapping;
			}
		}
		
		return null;
	}
	
	
	public boolean isOneToManyMapping(){
		return true;
	}
	
	/**
	 * Used for code gen.
	 * See MWMapping.accessorCodeGenPolicy(MWMethod)
	 */
	public MWMethodCodeGenPolicy accessorCodeGenPolicy(MWMethod accessor, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		// If TopLink maintains the relationship, we don't care about what attribute 
		//  is the back pointer for this mapping's attribute.
		if (! this.maintainsBidirectionalRelationship())
		{
			MWMapping backPointerMapping = backPointerMapping();
		
			if (backPointerMapping != null)
				return getInstanceVariable().accessorCodeGenPolicy(accessor, backPointerMapping.getInstanceVariable(), this.isPrivateOwned(), classCodeGenPolicy);
		}
		
		return super.accessorCodeGenPolicy(accessor, classCodeGenPolicy);
	}

	// ************* MWQueryable implementation **************
	
	public String iconKey() {
		return "mapping.oneToMany";
	}	
	
	
	// **************** Automap support ***************

	/**
	 * one-to-many mappings are always "target foreign key"
	 */
	protected Set buildCandidateReferences() {
		return this.buildCandidateTargetReferences();
	}

	
	// ************* runtime conversion **************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new OneToManyMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		OneToManyMapping runtimeMapping = (OneToManyMapping) super.runtimeMapping();
		if (getReference() != null) {
			for (Iterator stream = getReference().columnPairs(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				MWColumn sourceColumn = pair.getSourceColumn();
				MWColumn targetColumn = pair.getTargetColumn();
				if ((sourceColumn != null) && (targetColumn != null)) {
					if (!parentDescriptorIsAggregate()) {
						runtimeMapping.addTargetForeignKeyFieldName(sourceColumn.qualifiedName(), targetColumn.qualifiedName());
					}
					else {
						runtimeMapping.addTargetForeignKeyFieldName(sourceColumn.qualifiedName(), getName() + "->" + targetColumn.getName() + "_IN_REFERENCE_" + getReference().getName());
					}
				}
			}
		}	
		return runtimeMapping;
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWOneToManyMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWCollectionMapping.class);

		return descriptor;
	}
	
}
