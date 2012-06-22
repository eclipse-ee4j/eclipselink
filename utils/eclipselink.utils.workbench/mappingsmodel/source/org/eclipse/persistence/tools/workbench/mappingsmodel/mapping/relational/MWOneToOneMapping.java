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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnPairHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWProxyIndirectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Record;


public final class MWOneToOneMapping
	extends MWAbstractTableReferenceMapping
    implements MWProxyIndirectionMapping
{
		
	private Collection targetForeignKeyHandles;
		public final static String TARGET_FOREIGN_KEYS_COLLECTION = "targetForeignKeys";
		private NodeReferenceScrubber targetForeignKeyScrubber;
		
	
	
	// ********** constructors **********
	
	/** Default constructor - for TopLink use only */
	private MWOneToOneMapping() {
		super();
	}
	
	MWOneToOneMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String mappingName) {
		super(descriptor, attribute, mappingName);
	}
	
	
	// **************** Initialization ***************

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.targetForeignKeyHandles = new Vector();
	}
	
	protected void initialize(MWClassAttribute attribute, String name) {
		super.initialize(attribute, name);
		if (!getInstanceVariable().isValueHolder() && getInstanceVariable().getType().isInterface()) {
			this.indirectionType = PROXY_INDIRECTION;
		} 
		if (getProject().usesWeaving()) {
			this.indirectionType = VALUE_HOLDER_INDIRECTION;
		}
	}
			

	// **************** Containment Hierarchy ***************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.targetForeignKeyHandles) { children.addAll(this.targetForeignKeyHandles); }
	}

	private NodeReferenceScrubber targetForeignKeyScrubber() {
		if (this.targetForeignKeyScrubber == null) {
			this.targetForeignKeyScrubber = this.buildTargetForeignKeyScrubber();
		}
		return this.targetForeignKeyScrubber;
	}

	private NodeReferenceScrubber buildTargetForeignKeyScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWOneToOneMapping.this.removeTargetForeignKeyHandle((MWColumnPairHandle) handle);
			}
			public String toString() {
				return "MWOneToOneMapping.buildTargetForeignKeyScrubber()";
			}
		};
	}
	
	@Override
	protected void automapIndirection() {
		if (this.getInstanceVariable().isValueHolder() || getProject().usesWeaving()) {
			this.setUseValueHolderIndirection();
		}
	}
	
	// **************** Accessors ********************
	
	private Iterator targetForeignKeyHandles() {
		return new CloneIterator(this.targetForeignKeyHandles) {
			protected void remove(Object current) {
				MWOneToOneMapping.this.removeTargetForeignKeyHandle((MWColumnPairHandle) current);
			}
		};
	}

	void removeTargetForeignKeyHandle(MWColumnPairHandle handle) {
		this.targetForeignKeyHandles.remove(handle);
		this.fireItemRemoved(TARGET_FOREIGN_KEYS_COLLECTION, handle.getColumnPair());
	}

	public Iterator targetForeignKeys() {
		return new TransformationIterator(this.targetForeignKeyHandles()) {
			protected Object transform(Object next) {
				return ((MWColumnPairHandle) next).getColumnPair();
			}
		};
	}

	public int targetForeignKeysSize() {
		return this.targetForeignKeyHandles.size();
	}

	public void addTargetForeignKey(MWColumnPair targetForeignKey) {
		this.targetForeignKeyHandles.add(new MWColumnPairHandle(this, targetForeignKey, this.targetForeignKeyScrubber()));
		this.fireItemAdded(TARGET_FOREIGN_KEYS_COLLECTION, targetForeignKey);
	}

	public void removeTargetForeignKey(MWColumnPair targetForeignKey) {
		for (Iterator stream = this.targetForeignKeys(); stream.hasNext(); ) {
			if (stream.next() == targetForeignKey) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(targetForeignKey.toString());
	}

	public void clearTargetForeignKeys() {
		for (Iterator stream = this.targetForeignKeyHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}
	
	public void setReference(MWReference reference) {
		if (this.getReference() != reference) {
			this.clearTargetForeignKeys();
		}
		super.setReference(reference);
	}
	
    // *********** MWProxyIndirectionMapping implementation ***********
    
    public boolean usesProxyIndirection() {
        return getIndirectionType() == PROXY_INDIRECTION;
    }

    public void setUseProxyIndirection() {
        setIndirectionType(PROXY_INDIRECTION);  
    }
    
    
	// **************** Queries ******************
	
	public boolean containsTargetForeignKey(MWColumnPair targetForeignKey) {
		return CollectionTools.contains(this.targetForeignKeys(), targetForeignKey);
	}
	
	protected boolean fieldIsWritten(MWColumnPair columnPair) {
		return ! this.containsTargetForeignKey(columnPair);
	}
	
	
	// **************** Automap Support **********

	/**
	 * one-to-one mappings can be either "source foreign key"
	 * or "target foreign key"
	 */
	protected Set buildCandidateReferences() {
		Set references = new HashSet();
		references.addAll(this.buildCandidateSourceReferences());
		references.addAll(this.buildCandidateTargetReferences());
		return references;
	}

	/**
	 * Returns true if this mapping behaves like a 1-1, where the source table (e.g. EMPLOYEE) has
	 * a foreign key to the target table (e.g., ADDRESS);
	 **/
	public boolean sourceReferenceFieldsAreFromSourceDescriptorTables() {
		return true;
	}

	public boolean isOneToOneMapping(){
		return true;
	}

	// **************** Mapping Morphing support ******************

	public MWOneToOneMapping asMWOneToOneMapping() {
		return this;
	}
	
	/**
	 * IMPORTANT:  See MWMapping class comment.
	 */
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWOneToOneMapping(this);
	}
		
//	protected void initializeFromMWAggregateMapping(MWAggregateMapping oldMapping) {
//		super.initializeFromMWAggregateMapping(oldMapping);
//		// set up default indirection policy
//		if (this.getInstanceVariable() != null) {
//			this.getIndirectionPolicy().setUsesIndirection(this.getInstanceVariable().isValueHolder());
//		}
//	}
//	
//	protected void initializeFromMWCollectionMapping(MWCollectionMapping oldMapping) {
//		super.initializeFromMWCollectionMapping(oldMapping);
//		getIndirectionPolicy().setUsesIndirection(!oldMapping.usesTransparentIndirection());
//	}
//	
//	protected void initializeFromMWDirectCollectionMapping(MWDirectCollectionMapping oldMapping) {
//		super.initializeFromMWDirectCollectionMapping(oldMapping);
//		getIndirectionPolicy().setUsesIndirection(!oldMapping.usesTransparentIndirection());
//	}

	public void initializeFromMWDirectMapping(MWDirectMapping oldMapping) {
		super.initializeFromMWDirectMapping(oldMapping);
		// set up default indirection policy
		//TODO this doesn't seem necessary, it happens in the initialization
//		if (this.getInstanceVariable() != null) {
//			this.getIndirectionPolicy().setUsesIndirection(this.getInstanceVariable().isValueHolder());
//		}
	}
	
//	public void initializeFromMWTransformationMapping(MWTransformationMapping oldMapping) {
//		super.initializeFromMWTransformationMapping(oldMapping);
//		this.setUsesIndirection(oldMapping.usesIndirection());
//	}
	
	public void initializeFromMWVariableOneToOneMapping(MWVariableOneToOneMapping oldMapping) {
		super.initializeFromMWVariableOneToOneMapping(oldMapping);
		if (oldMapping.usesValueHolderIndirection()) {
			setUseValueHolderIndirection();
		}
		else if (oldMapping.usesNoIndirection()){
			setUseNoIndirection();
		}
	}
	
	
	// **************** Code generation support ******************
	
	/**
	 * Used for code gen.
	 * See MWRMapping.initialValue()
	 */
	public String initialValue(MWClassCodeGenPolicy classCodeGenPolicy) {
		String initialValue = super.initialValue(classCodeGenPolicy);
		
		if (this.isResponsibleForWritingPrimaryKey()) {
			initialValue = StringTools.CR
						   + "\t\t" 
						   + classCodeGenPolicy.oneToOneMappingThatControlsWritingOfPrimaryKeyComment(this)
						   + StringTools.CR
						   + "\t\t" 
						   + this.getInstanceVariable().initialValueSourceCodeFor(null);
		}
		
		return initialValue;
	}
	
	protected boolean isResponsibleForWritingPrimaryKey() {
		if (parentDescriptorIsAggregate() || this.isReadOnly())
			return false;
		Collection writableFields = new ArrayList();
		addWrittenFieldsTo(writableFields);
		
		for (Iterator stream = ((MWTableDescriptor) getParentDescriptor()).primaryKeyPolicy().primaryKeys(); stream.hasNext(); ) {
			if (writableFields.contains(stream.next())) {
				return true;
			}
		}
		
		return false;
	}

	
	// **************** Aggregate Support **************
	
	protected Collection buildAggregateFieldNameGenerators() {
		Collection generators = super.buildAggregateFieldNameGenerators();
		if (getReference() != null) { 
			for (Iterator stream = getReference().columnPairs(); stream.hasNext(); ) {
				generators.add(new ColumnPairAggregateRuntimeFieldNameGenerator(this, (MWColumnPair) stream.next(), true));
			}
		}
		
		return generators;
	}


	// *************** MWQueryable implementation ************************
	
	public boolean usesAnyOf() {
		return false;
	}
	
	public boolean isTraversableForReadAllQueryOrderable() {
		return true;
	}
		
	public String iconKey() {
		return "mapping.oneToOne";
	}
	
	
	// *************** Problem Handling ************************

	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.addUsesIndirectionWhileMaintainsBiDirectionalRelationship(newProblems);
 	}	

	private void addUsesIndirectionWhileMaintainsBiDirectionalRelationship(List newProblems) {
		if (this.maintainsBidirectionalRelationship() && this.usesNoIndirection()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_MAINTAINS_BIDI_BUT_NO_INDIRECTION));
		}
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		if (isReadOnly()) {
			return;
		}
		if (getReference() != null) { 
			for (Iterator stream = getReference().columnPairs(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				if ( ! containsTargetForeignKey(pair) && pair.getSourceColumn() != null) {
					writtenFields.add(pair.getSourceColumn());
				}
			}
		}
		
	}
	
	// **************** Runtime Conversion *****************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new OneToOneMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		OneToOneMapping runtimeMapping = (OneToOneMapping) super.runtimeMapping();
			
		if (this.getReference() == null)
			return runtimeMapping;
		
		for (Iterator stream = getReference().columnPairs(); stream.hasNext(); ) {
			MWColumnPair pair = (MWColumnPair) stream.next();
			MWColumn foreignKeyColumn = pair.getSourceColumn();
			MWColumn primaryKeyColumn = pair.getTargetColumn();
			
			if ((foreignKeyColumn != null) && (primaryKeyColumn != null)) {
				if (this.containsTargetForeignKey(pair)) {	
					// Bug fix: If the user has set up a reference from an owning-descriptor associated table
					// to a target-descriptor associated table, but also marked it as target foreign key
					// (in essence reversing the reference by context), we should reverse the two fields.
					if (CollectionTools.contains(getParentRelationalDescriptor().candidateTables(), pair.sourceTable())
						&& ! CollectionTools.contains(getParentRelationalDescriptor().candidateTables(), pair.targetTable()))
					{
						foreignKeyColumn = pair.getTargetColumn();
						primaryKeyColumn = pair.getSourceColumn();
					}
					// End bug fix
					if ( ! parentDescriptorIsAggregate()) {
						runtimeMapping.addTargetForeignKeyFieldName(foreignKeyColumn.qualifiedName(), primaryKeyColumn.qualifiedName());
					} else {
						runtimeMapping.addTargetForeignKeyFieldName(foreignKeyColumn.qualifiedName(), getName() + "->" + primaryKeyColumn.qualifiedName() + "_IN_REFERENCE_" + getReference().getName());
					}
				} else {
					if ( ! parentDescriptorIsAggregate()) {
						runtimeMapping.addForeignKeyFieldName(foreignKeyColumn.qualifiedName(), primaryKeyColumn.qualifiedName());
					} else {
						runtimeMapping.addForeignKeyFieldName(getName() + "->" + foreignKeyColumn.getName() + "_IN_REFERENCE_" + getReference().getName(), primaryKeyColumn.qualifiedName());
					}
				}
			}
		}
        if (usesProxyIndirection()) {
            runtimeMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
        }
		return runtimeMapping;	
	}


	// *************** TopLink methods *************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWOneToOneMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractTableReferenceMapping.class);

		XMLCompositeCollectionMapping targetForeignKeyHandlesMapping = new XMLCompositeCollectionMapping();
		targetForeignKeyHandlesMapping.setAttributeName("targetForeignKeyHandles");
		targetForeignKeyHandlesMapping.setGetMethodName("getTargetForeignKeyHandlesForTopLink");
		targetForeignKeyHandlesMapping.setSetMethodName("setTargetForeignKeyHandlesForTopLink");
		targetForeignKeyHandlesMapping.setReferenceClass(MWColumnPairHandle.class);
		targetForeignKeyHandlesMapping.setXPath("target-foreign-key-handles/column-pair-handle");
		descriptor.addMapping(targetForeignKeyHandlesMapping);

		return descriptor;
	}
	
	private Collection getTargetForeignKeyHandlesForTopLink() {
		synchronized (this.targetForeignKeyHandles) {
			return new TreeSet(this.targetForeignKeyHandles);
		}
	}
	private void setTargetForeignKeyHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWColumnPairHandle) stream.next()).setScrubber(this.targetForeignKeyScrubber());
		}
		this.targetForeignKeyHandles = handles;
	}
	
	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWOneToOneMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractTableReferenceMapping.class);

		((XMLDirectMapping)descriptor.addDirectMapping("usesJoining", "legacyGetUsesJoiningForToplink", "legacySetUsesJoiningForToplink", "uses-joining")).setNullValue(Boolean.FALSE);

		XMLCompositeCollectionMapping targetForeignKeyHandlesMapping = new XMLCompositeCollectionMapping();
		targetForeignKeyHandlesMapping.setAttributeName("targetForeignKeyHandles");
		targetForeignKeyHandlesMapping.setGetMethodName("getTargetForeignKeyHandlesForTopLink");
		targetForeignKeyHandlesMapping.setSetMethodName("setTargetForeignKeyHandlesForTopLink");
		targetForeignKeyHandlesMapping.setReferenceClass(MWColumnPairHandle.class);
		targetForeignKeyHandlesMapping.setXPath("target-foreign-key-handles/column-pair-handle");
		descriptor.addMapping(targetForeignKeyHandlesMapping);

		return descriptor;
	}
	
	private boolean legacyGetUsesJoiningForToplink() {
		//should not be called
		throw new UnsupportedOperationException();
	}
	
	private void legacySetUsesJoiningForToplink(boolean usesJoining) {
		if (usesJoining) {
			this.setJoinFetchingForToplink(MWJoinFetchableMapping.JOIN_FETCH_INNER);
		} else {
			this.setJoinFetchingForToplink(MWJoinFetchableMapping.JOIN_FETCH_NONE);
		}
	}
}
