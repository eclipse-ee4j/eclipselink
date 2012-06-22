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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public final class MWRelationalReturningPolicy 
	extends MWReturningPolicy 
{	
	/** The implementation of the implied collection on the superclass */
	private Collection updateFieldHandles;
		private NodeReferenceScrubber updateFieldScrubber;
	
	
	// ********** constructors/initialization **********
	
	/** Default constructor - for TopLink use only */
	private MWRelationalReturningPolicy() {
		super();
	}
	
	MWRelationalReturningPolicy(MWRelationalClassDescriptor parent) {
		super(parent);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.updateFieldHandles = new Vector();
	}

	
	// **************** Insert fields *****************************************
	
	public MWRelationalReturningPolicyInsertFieldReturnOnlyFlag addInsertFieldReadOnlyFlag(MWColumn insertColumn) {
		MWRelationalReturningPolicyInsertFieldReturnOnlyFlag insertFieldReturnOnlyFlag = new MWRelationalReturningPolicyInsertFieldReturnOnlyFlag(this, insertColumn);
		this.addInsertFieldReadOnlyFlag(insertFieldReturnOnlyFlag);
		return insertFieldReturnOnlyFlag;
	}
	
	
	// **************** Update fields *****************************************
	
	private Iterator updateFieldHandles() {
		return new CloneIterator(this.updateFieldHandles) {
			protected void remove(Object current) {
				MWRelationalReturningPolicy.this.removeUpdateFieldHandle((MWColumnHandle) current);
			}
		};
	}
	
	void removeUpdateFieldHandle(MWColumnHandle handle) {
		this.updateFieldHandles.remove(handle);
		this.fireItemRemoved(UPDATE_FIELDS_COLLECTION, handle.getColumn());
	}

	public Iterator updateFields() {
		return new TransformationIterator(this.updateFieldHandles()) {
			protected Object transform(Object next) {
				return ((MWColumnHandle) next).getColumn();
			}
		};
	}	

	public int updateFieldsSize() {
		return this.updateFieldHandles.size();
	}
	
	public void addUpdateField(MWColumn updateField) {
		if (CollectionTools.contains(this.updateFields(), updateField)) {
			throw new IllegalArgumentException(updateField.toString());
		}
		this.updateFieldHandles.add(new MWColumnHandle(this, updateField, this.updateFieldScrubber()));
		this.fireItemAdded(UPDATE_FIELDS_COLLECTION, updateField);
	}

	public void removeUpdateField(MWDataField updateField) {
		this.removeUpdateField((MWColumn) updateField);
	}

	public void removeUpdateField(MWColumn updateField) {
		for (Iterator stream = this.updateFields(); stream.hasNext(); ) {
			if (stream.next() == updateField) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(updateField.toString());
	}
	
	
	// ********** convenience **********
	
	private MWRelationalClassDescriptor relationalDescriptor() {
		return (MWRelationalClassDescriptor) this.getParent();
	}
	
	
	// ********** problems **********
	
	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkPlatformForNativeReturningSupport(problems);
		this.checkUpdateFields(problems);
		this.checkSequenceColumn(problems);
		this.checkOneToOneForeignKey(problems);
	}
	
	private void checkPlatformForNativeReturningSupport(List newProblems) {
		if ( ! this.getProject().getDatabase().getDatabasePlatform().supportsNativeReturning()) {
			newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_NATIVE_RETURNING_NOT_SUPPORTED));
		}
	}
	
	private void checkUpdateFields(List problems) {
		for (Iterator stream = this.updateFields(); stream.hasNext(); ) {
			MWDataField field = (MWDataField) stream.next();
			if ( ! CollectionTools.contains(this.relationalDescriptor().allAssociatedColumns(), field)) {
				problems.add(buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_FIELDS_NOT_VALID, field.fieldName()));
			}
		}
	}

	private void checkSequenceColumn(List newProblems) {
		MWColumn sequenceColumn = ((MWTableDescriptor) this.getOwningDescriptor()).getSequenceNumberColumn();
		if (CollectionTools.contains(this.updateFields(), sequenceColumn)) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_SEQUENCING_FIELD, sequenceColumn.qualifiedName()));		
		}
		if (CollectionTools.contains(this.insertFields(), sequenceColumn)) {
			newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_SEQUENCING_FIELD, sequenceColumn.qualifiedName()));
		}
	}

	private void checkOneToOneForeignKey(List newProblems) {
		for (Iterator stream = this.getProject().allMappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if (mapping.isOneToOneMapping()) {
				MWOneToOneMapping oneToOneMapping = (MWOneToOneMapping) mapping;
				if ((oneToOneMapping.getReference() != null) && (oneToOneMapping.getReference().isForeignKeyReference())) {
					for (Iterator stream2 = oneToOneMapping.getReference().columnPairs(); stream2.hasNext(); ) {
						MWColumn sourceColumn = ((MWColumnPair) stream2.next()).getSourceColumn();
						if (CollectionTools.contains(this.updateFields(), sourceColumn)) {
							newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_ONE_TO_ONE_FORIEGN_KEY, sourceColumn.qualifiedName(), oneToOneMapping.getName()));
						}
						if (CollectionTools.contains(this.insertFields(), sourceColumn)) {
							newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_ONE_TO_ONE_FORIEGN_KEY, sourceColumn.qualifiedName(), oneToOneMapping.getName()));
						}
					}
				}
			}
		}
	}


	// ********** Model synchronization **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.updateFieldHandles) { children.addAll(this.updateFieldHandles); }
	}
	
	private NodeReferenceScrubber updateFieldScrubber() {
		if (this.updateFieldScrubber == null) {
			this.updateFieldScrubber = this.buildUpdateFieldScrubber();
		}
		return this.updateFieldScrubber;
	}

	private NodeReferenceScrubber buildUpdateFieldScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalReturningPolicy.this.removeUpdateFieldHandle((MWColumnHandle) handle);
			}
			public String toString() {
				return "MWRelationalReturningPolicy.buildUpdateFieldScrubber()";
			}
		};
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalReturningPolicy.class);
	
		XMLCompositeCollectionMapping insertFieldReturnOnlyFlagsMapping = new XMLCompositeCollectionMapping();
		insertFieldReturnOnlyFlagsMapping.setAttributeName("insertFieldReturnOnlyFlags");
		insertFieldReturnOnlyFlagsMapping.setReferenceClass(MWRelationalReturningPolicyInsertFieldReturnOnlyFlag.class);
		insertFieldReturnOnlyFlagsMapping.setGetMethodName("getInsertFieldReturnOnlyFlagsForTopLink");
		insertFieldReturnOnlyFlagsMapping.setSetMethodName("setInsertFieldReturnOnlyFlagsForTopLink");
		insertFieldReturnOnlyFlagsMapping.setXPath("insert-field-read-only-flags/insert-field-read-only-flag");
		descriptor.addMapping(insertFieldReturnOnlyFlagsMapping);
	
		XMLCompositeCollectionMapping updateFieldHandlesMapping = new XMLCompositeCollectionMapping();
		updateFieldHandlesMapping.setAttributeName("updateFieldHandles");
		updateFieldHandlesMapping.setGetMethodName("getUpdateFieldHandlesForTopLink");
		updateFieldHandlesMapping.setSetMethodName("setUpdateFieldHandlesForTopLink");
		updateFieldHandlesMapping.setReferenceClass(MWColumnHandle.class);
		updateFieldHandlesMapping.setXPath("update-field-handles/column-handle");
		descriptor.addMapping(updateFieldHandlesMapping);
				
		return descriptor;
	}

	/**
	 * sort the collection for TopLink
	 */
	private Collection getUpdateFieldHandlesForTopLink() {
		synchronized (this.updateFieldHandles) {
			return new TreeSet(this.updateFieldHandles);
		}
	}
	private void setUpdateFieldHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWColumnHandle) stream.next()).setScrubber(this.updateFieldScrubber());
		}
		this.updateFieldHandles = handles;
	}

}
