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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;

public abstract class MWReturningPolicy 
	extends MWAbstractDescriptorPolicy 
{
	/** each insert field has a "return only" flag */
	private Collection insertFieldReturnOnlyFlags;
		public static final String INSERT_FIELD_RETURN_ONLY_FLAGS_COLLECTION = "insertFieldReturnOnlyFlags";

	/** Subclasses control the implied collection "updateFields" */
		public static final String UPDATE_FIELDS_COLLECTION = "updateFields";


	// ********** constructors/initialization **********

	/** Default constructor - for TopLink use only */
	protected MWReturningPolicy() {
		super();
	}
	
	protected MWReturningPolicy(MWMappingDescriptor parent) {
		super(parent);	
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.insertFieldReturnOnlyFlags = new Vector();
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.insertFieldReturnOnlyFlags) { children.addAll(this.insertFieldReturnOnlyFlags); }
	}
	
	
	// ********** insert fields/return-only flags **********

	public Iterator insertFieldReturnOnlyFlags() {
		return new CloneIterator(this.insertFieldReturnOnlyFlags) {
			protected void remove(Object current) {
				MWReturningPolicy.this.removeInsertFieldReturnOnlyFlag((MWReturningPolicyInsertFieldReturnOnlyFlag) current);
			}
		};
	}
	
	public int insertFieldReturnOnlyFlagsSize() {
		return this.insertFieldReturnOnlyFlags.size();
	}
	
	public void addInsertFieldReadOnlyFlag(MWReturningPolicyInsertFieldReturnOnlyFlag insertFieldReturnOnlyFlag) {
		this.addItemToCollection(insertFieldReturnOnlyFlag, this.insertFieldReturnOnlyFlags, INSERT_FIELD_RETURN_ONLY_FLAGS_COLLECTION);
	}

	public void removeInsertFieldReturnOnlyFlag(MWReturningPolicyInsertFieldReturnOnlyFlag insertFieldReturnOnlyFlag) {
		this.removeItemFromCollection(insertFieldReturnOnlyFlag, this.insertFieldReturnOnlyFlags, INSERT_FIELD_RETURN_ONLY_FLAGS_COLLECTION);
	}

	/** Return only the fields (MWDataFields), less their flags */
	public Iterator insertFields() {
		return new TransformationIterator(this.insertFieldReturnOnlyFlags()) {
			protected Object transform(Object next) {
				return ((MWReturningPolicyInsertFieldReturnOnlyFlag) next).getField(); 
			}
		};
	}
	
	
	// **************** Update fields *****************************************
	
	public abstract Iterator updateFields();
	
	public abstract int updateFieldsSize();
	
	public abstract void removeUpdateField(MWDataField updateField);
	
	
	// ********** MWDescriptorPolicy implementation **********

	public boolean isActive() {
		return true;
	}

	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}


	// ********** problems **********
	
	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkTypeIndicatorField(problems);
		this.checkOptimisticLockingField(problems);
		this.checkReturningFieldIsmapped(problems);
	}
	
	private void checkTypeIndicatorField(List newProblems){	
		MWClassIndicatorPolicy classIndicatorPolicy = this.getOwningDescriptor().getInheritancePolicy().getClassIndicatorPolicy();
		if (classIndicatorPolicy.getType() == MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE) {
			MWDataField classIndicatorField = ((MWClassIndicatorFieldPolicy) classIndicatorPolicy).getField();
			
			if (CollectionTools.contains(this.updateFields(), classIndicatorField)) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_CLASS_INDICATOR_FIELD, classIndicatorField.fieldName()));
			}

			if (CollectionTools.contains(this.insertFields(), classIndicatorField)) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_CLASS_INDICATOR_FIELD, classIndicatorField.fieldName()));
			}
		}
	}
	
	private void checkOptimisticLockingField(List newProblems) {
		MWLockingPolicy lockingPolicy = this.getOwningDescriptor().getLockingPolicy();
		MWDataField lockingField = lockingPolicy.getVersionLockField();
		if (lockingField != null) {
			
			if (CollectionTools.contains(this.updateFields(), lockingField)) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_LOCKING_FIELD, lockingField.fieldName()));
			}

			if (CollectionTools.contains(this.insertFields(), lockingField)) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_LOCKING_FIELD, lockingField.fieldName()));
			}			
		}
	}
		
	// TODO This message does not correspond to a DescriptorException. If we someday support
	// different levels of messages, this should be a warning.  See the 10.1.3 returningPolicy spec for details
	private void checkReturningFieldIsmapped(List newProblems) {
		for (Iterator stream = this.updateFields(); stream.hasNext(); ) {
			MWDataField field = (MWDataField) stream.next();
			if (this.getOwningDescriptor().allWritableMappingsForField(field).isEmpty()) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_UNMAPPED, field.fieldName()));
			}
		}
		
		for (Iterator stream = this.insertFields(); stream.hasNext(); ) {
			MWDataField field = (MWDataField) stream.next();
			if (this.getOwningDescriptor().allWritableMappingsForField(field).isEmpty()) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_UNMAPPED, field.fieldName()));
			}
		}
	}	


	// ********** runtime conversion **********
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		ReturningPolicy returningPolicy = new ReturningPolicy();
		runtimeDescriptor.setReturningPolicy(returningPolicy);
		for (Iterator stream = this.insertFieldReturnOnlyFlags(); stream.hasNext(); ) {
			((MWReturningPolicyInsertFieldReturnOnlyFlag) stream.next()).adjustRuntimeReturningPolicy(returningPolicy);		
		}
		for (Iterator stream = this.updateFields(); stream.hasNext(); ) {
			returningPolicy.addFieldForUpdate(((MWDataField) stream.next()).runtimeField());
		}
	}


	// ********** TopLink methods **********

	/**
	 * sort the collection for TopLink
	 */
	private Collection getInsertFieldReturnOnlyFlagsForTopLink() {
		synchronized (this.insertFieldReturnOnlyFlags) {
			return new TreeSet(this.insertFieldReturnOnlyFlags);
		}
	}

	private void setInsertFieldReturnOnlyFlagsForTopLink(Collection insertFieldReturnOnlyFlags) {
		this.insertFieldReturnOnlyFlags = insertFieldReturnOnlyFlags;
	}
}
