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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWManyToManyMapping extends MWCollectionMapping {

	private MWReferenceHandle targetReferenceHandle;  // holds and persists the target table reference...
		public final static String TARGET_REFERENCE_PROPERTY = "targetReference";
	
	private MWTableHandle relationTableHandle;
		public final static String RELATION_TABLE_PROPERTY = "relationTable";


	// ********** constructors **********
	
	/** Default constructor - for TopLink use only */
	private MWManyToManyMapping() {
		super();
	}

	MWManyToManyMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.targetReferenceHandle = new MWReferenceHandle(this, this.buildTargetReferenceScrubber());
		this.relationTableHandle = new MWTableHandle(this, this.buildRelationTableScrubber());
	}


	// ********** accessors **********
	
	public MWTable getRelationTable() {
		return this.relationTableHandle.getTable();
	}
	
	public void setRelationTable(MWTable newValue) {
		Object oldValue = this.relationTableHandle.getTable();
		this.relationTableHandle.setTable(newValue);
		firePropertyChanged(RELATION_TABLE_PROPERTY, oldValue, newValue);
	}
	public void setSourceReference(MWReference ref) {
		setReference(ref);
	}

	public MWReference getSourceReference() {
		return getReference();
	}
	
	public MWReference getTargetReference() {
		return this.targetReferenceHandle.getReference();	
	}

	public void setTargetReference(MWReference newValue) {
		MWReference oldValue = getTargetReference();
		this.targetReferenceHandle.setReference(newValue);
		firePropertyChanged(TARGET_REFERENCE_PROPERTY, oldValue, newValue);
	}

	public boolean isManyToManyMapping(){
		return true;
	}


	// ************ morphing **************
	
	/**
	 * IMPORTANT:  See MWRMapping class comment.
	 */
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWManyToManyMapping(this);
	}


	public MWManyToManyMapping asMWManyToManyMapping() {
		return this;
	}


	// ************ containment hierarchy **************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.targetReferenceHandle);
		children.add(this.relationTableHandle);
	}
		
	private NodeReferenceScrubber buildTargetReferenceScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWManyToManyMapping.this.setTargetReference(null);
			}
			public String toString() {
				return "MWManyToManyMapping.buildTargetReferenceScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildRelationTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWManyToManyMapping.this.setRelationTable(null);
			}
			public String toString() {
				return "MWManyToManyMapping.buildRelationTableScrubber()";
			}
		};
	}
	
	
	// ************ MWMapping implementation **************
		
	public String iconKey() {
		return "mapping.manyToMany";
	}


	// ************* Automap Support ****************
	
	public void automap() {
		super.automap();
		this.automapRelationTable();
		this.automapSourceReference();
		this.automapTargetReference();
	}
	
	protected void automapTableReference() {
		// override to do nothing - in a many-to-many mapping the 'table
		// reference' field holds the the 'source reference', and the source
		// reference is automapped below (and has nothing to do with the
		// reference descriptor's table)
	}

	/**
	 * find a relation table
	 */
	private void automapRelationTable() {
		if (this.getRelationTable() != null) {
			return;	// if we already have a relation table, do nothing
		}

		Iterator tables = this.candidateRelationTables();
		if (tables.hasNext()) {
			// take the first one
			this.setRelationTable((MWTable) tables.next());
		}
	}
	
	/**
	 * find a reference from the relation table to the mapping's descriptor's table
	 */
	private void automapSourceReference() {
		if (this.getSourceReference() != null) {
			return;	// if we already have a source reference, do nothing
		}

		Iterator references = this.candidateRelationTableSourceReferences();
		if (references.hasNext()) {
			// take the first one
			this.setSourceReference((MWReference) references.next());
		}
	}

	/**
	 * find a reference from the relation table to the reference descriptor's table
	 */
	private void automapTargetReference() {
		if (this.getTargetReference() != null) {
			return;	// if we already have a target reference, do nothing
		}

		Iterator stream = this.candidateRelationTableTargetReferences();
		if (stream.hasNext()) {
			// take the first one
			this.setTargetReference((MWReference) stream.next());
		}
	}

	/**
	 * return the relation table's source and target references
	 */
	protected Set buildCandidateReferences() {
		Set references = new HashSet();
		references.addAll(this.buildCandidateRelationTableSourceReferences());
		references.addAll(this.buildCandidateRelationTableTargetReferences());
		return references;
	}

	/**
	 * return all the tables that might be relation tables for the mapping
	 */
	public Iterator candidateRelationTables() {
		MWTableDescriptor sourceDescriptor = (MWTableDescriptor) this.getParentDescriptor();
		MWTableDescriptor referenceDescriptor = (MWTableDescriptor) this.getReferenceDescriptor();

		if (referenceDescriptor == null) {
			// if we don't have a reference descriptor, return *all* the tables
			return this.getProject().getDatabase().tables();
		}
	
		Set sourceTables = CollectionTools.set(sourceDescriptor.associatedTables());
		Set targetTables = CollectionTools.set(referenceDescriptor.associatedTables());
		Collection candidateRelationTables = new ArrayList();
		for (Iterator stream = this.unmappedTables(); stream.hasNext(); ) {
			MWTable unmappedTable = (MWTable) stream.next();
			if (this.tableIsPossibleRelationTable(unmappedTable, sourceTables, targetTables)) {
				candidateRelationTables.add(unmappedTable);
			}
		}	
		return candidateRelationTables.iterator();
	}

	/**
	 * return the references from the relation table to the mapping's descriptor's table
	 */
	private Set buildCandidateRelationTableSourceReferences() {
		MWTable relationTable = this.getRelationTable();
		if (relationTable == null) {
			return Collections.EMPTY_SET;
		}

		Set references = new HashSet();
		Set targetTables = CollectionTools.set(this.getParentRelationalDescriptor().candidateTablesIncludingInherited());
		for (Iterator stream = relationTable.references(); stream.hasNext();) {
			MWReference reference = (MWReference) stream.next();
			if (targetTables.contains(reference.getTargetTable())) {
				references.add(reference);
			}
		}
		return references;
	}
	
	public Iterator candidateRelationTableSourceReferences() {
		return this.buildCandidateRelationTableSourceReferences().iterator();
	}

	/**
	 * return the references from the relation table to the reference descriptor's table
	 */
	private Set buildCandidateRelationTableTargetReferences() {
		MWTable relationTable = this.getRelationTable();
		MWRelationalDescriptor referenceDescriptor = (MWRelationalDescriptor) this.getReferenceDescriptor();
		if ((relationTable == null) || (referenceDescriptor == null)) {
			return Collections.EMPTY_SET;
		}

		Set references = new HashSet();
		Set targetTables = CollectionTools.set(referenceDescriptor.candidateTablesIncludingInherited());
		for (Iterator stream = relationTable.references(); stream.hasNext();) {
			MWReference reference = (MWReference) stream.next();
			if (targetTables.contains(reference.getTargetTable())) {
				references.add(reference);
			}
		}
		return references;
	}
	
	public Iterator candidateRelationTableTargetReferences() {
		return this.buildCandidateRelationTableTargetReferences().iterator();
	}

	/**
	 * return whether the specified table could be a relational table for the mapping
	 */
	public boolean tableIsPossibleRelationTable(MWTable table) {
		return CollectionTools.contains(this.candidateRelationTables(), table);
	}

	/**
	 * return all the tables that are not associated with a descriptor
	 */
	private Iterator unmappedTables() {
		Set tables = CollectionTools.set(this.getDatabase().tables());
		for (Iterator stream = this.getProject().descriptors(); stream.hasNext(); ) {
			MWRelationalDescriptor descriptor = (MWRelationalDescriptor) stream.next();
			CollectionTools.removeAll(tables, descriptor.associatedTables());
		}
		return tables.iterator();
	}

	/**
	 * return whether the specified unmapped table can be used as a relation table
	 * between the two specified sets of tables
	 */
	private boolean tableIsPossibleRelationTable(MWTable unmappedTable, Set sourceTables, Set targetTables) {
		boolean sourceFound = false;
		boolean targetFound = false;

		for (Iterator references = unmappedTable.references(); references.hasNext(); ) {
			MWReference reference = (MWReference) references.next();
			boolean workingWithSource = false;
			boolean workingWithTarget = false;

			if ( ! sourceFound && sourceTables.contains(reference.getTargetTable())) {
				workingWithSource = true;
			}

			if ( ! targetFound && targetTables.contains(reference.getTargetTable())) {
				workingWithTarget = true;
			}

			if ( ! workingWithSource && ! workingWithTarget) {
				continue;	// skip to the next reference
			}

			boolean allTargetColumnsHaveUniqueConstraint = true;
			for (Iterator columnPairs = reference.columnPairs(); columnPairs.hasNext(); ) {
				MWColumnPair columnPair = (MWColumnPair) columnPairs.next();
				MWColumn targetColumn = columnPair.getTargetColumn();
				// check whether the target column is unique (primary keys are always marked 'unique')
				allTargetColumnsHaveUniqueConstraint &= targetColumn.isUnique();
			}

			if (allTargetColumnsHaveUniqueConstraint) {	
				if (workingWithSource) {
					sourceFound = true;
				}
				if (workingWithTarget) {
					targetFound = true;
				}
			}
			if (sourceFound && targetFound) {
				return true;
			}
		}
		return false;
	}


	//************** Problem Handling ***************

	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.addRelationTableNotSpecifiedProblemTo(newProblems);
		this.addRelationTableNotDedicatedProblemTo(newProblems);
		this.addSourceReferenceNotSpecifiedProblemTo(newProblems);
		this.addTargetReferenceNotSpecifiedProblemTo(newProblems);
	}
	
	private void addRelationTableNotSpecifiedProblemTo(List newProblems) {
		if(getRelationTable() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_RELATION_TABLE_NOT_SPECIFIED));
		}
	}
	
	private void addRelationTableNotDedicatedProblemTo(List newProblems) {
		if (this.isReadOnly()) {
			return;
		}
		
		for (Iterator mappings = ((MWRelationalProject) getProject()).allWriteableManyToManyMappings(); mappings.hasNext(); ) {
			MWManyToManyMapping mapping = (MWManyToManyMapping) mappings.next();	
			if (mapping != this && mapping.getRelationTable() == this.getRelationTable()) {
				if (mapping.getParentRelationalDescriptor().getPrimaryTable() != null && 
						(mapping.getParentRelationalDescriptor().getPrimaryTable() != this.getParentRelationalDescriptor().getPrimaryTable()))
				newProblems.add(buildProblem(ProblemConstants.MAPPING_RELATION_TABLE_NOT_DEDICATED));
			}
		}
	}
	
	private void addSourceReferenceNotSpecifiedProblemTo(List newProblems) {
		if (this.getSourceReference() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_SOURCE_TABLE_REFERENCE_NOT_SPECIFIED));
		}
	}
	
	private void addTargetReferenceNotSpecifiedProblemTo(List newProblems) {
		if (this.getTargetReference() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_TARGET_TABLE_REFERENCE_NOT_SPECIFIED));
		}
	}
	
	
	// ************ runtime conversion **************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new ManyToManyMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		ManyToManyMapping manyToManyMapping = (ManyToManyMapping) super.runtimeMapping();
  
		MWTable relationTable = getRelationTable();
		if (relationTable != null) {
			manyToManyMapping.setRelationTableName(relationTable.getName());
		}

		if (getSourceReference() != null) {
			for (Iterator stream = getSourceReference().columnPairs(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				MWColumn sourceColumn = pair.getSourceColumn();
				MWColumn targetColumn = pair.getTargetColumn();
				if ((sourceColumn != null) && (targetColumn != null)) {
					if (!parentDescriptorIsAggregate()) {
						manyToManyMapping.addSourceRelationKeyFieldName(sourceColumn.qualifiedName(), targetColumn.qualifiedName());
					}
					else {
						manyToManyMapping.addSourceRelationKeyFieldName(sourceColumn.qualifiedName(), getName() + "->" + targetColumn.getName() + "_IN_REFERENCE_" + getSourceReference().getName());
					}
				}
			}
		}
		if (getTargetReference() != null) {
			for (Iterator stream = getTargetReference().columnPairs(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				MWColumn sourceColumn = pair.getSourceColumn();
				MWColumn targetColumn = pair.getTargetColumn();
				if ((sourceColumn != null) && (targetColumn != null)) {
					manyToManyMapping.addTargetRelationKeyFieldName(sourceColumn.qualifiedName(), targetColumn.qualifiedName());
				}
			}
		}
		
		return manyToManyMapping;
	}


	// ************* TopLink methods *************	
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWManyToManyMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWCollectionMapping.class);

		XMLCompositeObjectMapping relationTableMapping = new XMLCompositeObjectMapping();
		relationTableMapping.setAttributeName("relationTableHandle");
		relationTableMapping.setGetMethodName("getRelationTableHandleForTopLink");
		relationTableMapping.setSetMethodName("setRelationTableHandleForTopLink");
		relationTableMapping.setReferenceClass(MWTableHandle.class);
		relationTableMapping.setXPath("relation-table-handle");
		descriptor.addMapping(relationTableMapping);

		XMLCompositeObjectMapping targetReferenceHandleMapping = new XMLCompositeObjectMapping();
		targetReferenceHandleMapping.setAttributeName("targetReferenceHandle");
		targetReferenceHandleMapping.setGetMethodName("getTargetReferenceHandleForTopLink");
		targetReferenceHandleMapping.setSetMethodName("setTargetReferenceHandleForTopLink");
		targetReferenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		targetReferenceHandleMapping.setXPath("target-reference-handle");
		descriptor.addMapping(targetReferenceHandleMapping);
	
		return descriptor;
	}	
	
	/**
	 * check for null
	 */
	private MWTableHandle getRelationTableHandleForTopLink() {
		return (this.relationTableHandle.getTable() == null) ? null : this.relationTableHandle;
	}
	private void setRelationTableHandleForTopLink(MWTableHandle relationTableHandle) {
		NodeReferenceScrubber scrubber = this.buildRelationTableScrubber();
		this.relationTableHandle = ((relationTableHandle == null) ? new MWTableHandle(this, scrubber) : relationTableHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWReferenceHandle getTargetReferenceHandleForTopLink() {
		return (this.targetReferenceHandle.getReference() == null) ? null : this.targetReferenceHandle;
	}
	private void setTargetReferenceHandleForTopLink(MWReferenceHandle targetReferenceHandle) {
		NodeReferenceScrubber scrubber = this.buildTargetReferenceScrubber();
		this.targetReferenceHandle = ((targetReferenceHandle == null) ? new MWReferenceHandle(this, scrubber) : targetReferenceHandle.setScrubber(scrubber));
	}
}
