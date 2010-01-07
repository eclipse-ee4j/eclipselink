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
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWAbstractReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public abstract class MWAbstractTableReferenceMapping extends MWAbstractReferenceMapping 
	implements MWTableReferenceMapping, MWIndirectableMapping, MWJoinFetchableMapping
{
	
	private MWReferenceHandle referenceHandle;
	
	private volatile boolean maintainsBidirectionalRelationship;
		public final static String MAINTAINS_BIDIRECTIONAL_RELATIONSHIP_PROPERTY = "maintainsBidirectionalRelationship";
		
	private MWMappingHandle relationshipPartnerMappingHandle;
		public final static String RELATIONSHIP_PARTNER_MAPPING_PROPERTY = "relationshipPartnerMapping";
		
	private volatile JoinFetchOption joinFetchOption;
	private static TopLinkOptionSet joinFetchOptions;

	/** Indicates whether the referenced object should always be batch read on read all queries. */
	private volatile boolean batchReading;


	// **************** Constructors ***************

	/** Default constructor - for TopLink use only */
	protected MWAbstractTableReferenceMapping() {
		super();
	}
	
	protected MWAbstractTableReferenceMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String mappingName) {
		super(descriptor, attribute, mappingName);
	}
	
	
	// **************** Initialization ***************
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.referenceHandle = new MWReferenceHandle(this, this.buildReferenceScrubber());
		this.relationshipPartnerMappingHandle = new MWMappingHandle(this, this.buildRelationshipPartnerMappingScrubber());
		this.joinFetchOption = (JoinFetchOption)MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(JOIN_FETCH_NONE);
	}

	
	// **************** Accessors ***************

	public JoinFetchOption getJoinFetchOption() {
		if (this.joinFetchOption == null) {
			this.joinFetchOption = (JoinFetchOption)MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(JOIN_FETCH_NONE);
		}
		return this.joinFetchOption;
	}

	public void setJoinFetchOption(JoinFetchOption newJoinFetchOption) {
        JoinFetchOption old = this.joinFetchOption;
		this.joinFetchOption = newJoinFetchOption;
		firePropertyChanged(JOIN_FETCH_PROPERTY, old, this.joinFetchOption);
	}

    public void setJoinFetchOption(String joinFetchOptions) {
        setJoinFetchOption((JoinFetchOption) MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(joinFetchOptions));
    }

    public MWReference getReference() {
		return this.referenceHandle.getReference();
	}
	
	public void setReference(MWReference newValue) {
		MWReference oldValue = getReference();
		this.referenceHandle.setReference(newValue);
		this.firePropertyChanged(REFERENCE_PROPERTY, oldValue, newValue);
		this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
	}

	public boolean maintainsBidirectionalRelationship() {
		return this.maintainsBidirectionalRelationship;
	}

	public void setMaintainsBidirectionalRelationship(boolean newBoolean) {
		boolean oldBoolean = this.maintainsBidirectionalRelationship;
		this.maintainsBidirectionalRelationship = newBoolean;
		firePropertyChanged(MAINTAINS_BIDIRECTIONAL_RELATIONSHIP_PROPERTY, oldBoolean, newBoolean);
		
		if (newBoolean == false) {
			this.setRelationshipPartnerMapping(null);
		}
	}
	
	public MWMapping getRelationshipPartnerMapping() {
		return this.relationshipPartnerMappingHandle.getMapping();
	}
	
	public Collection getRelationshipPartnerMappingChoices() {
		if (getReferenceDescriptor() == null) {
			return Collections.EMPTY_SET;
		}
		Collection choices = CollectionTools.collection(getReferenceDescriptor().mappings());
		// only occurs if reference descriptor is this mapping's owning descriptor.  A mapping can't very well
		// maintain a bidirectional relationship with itself. - pwf
		choices.remove(this);
		return choices;
	}
	
	public void setRelationshipPartnerMapping(MWMapping newValue) {
		Object oldValue = getRelationshipPartnerMapping();
		this.relationshipPartnerMappingHandle.setMapping(newValue);
		firePropertyChanged(RELATIONSHIP_PARTNER_MAPPING_PROPERTY, oldValue, newValue);
	}
	
	public void setReferenceDescriptor(MWDescriptor newReferenceDescriptor) {
		if (getReferenceDescriptor() != newReferenceDescriptor) {
			setRelationshipPartnerMapping(null);
		}
		super.setReferenceDescriptor(newReferenceDescriptor);
	}
	
	public boolean descriptorIsValidReferenceDescriptor(MWDescriptor descriptor) {
		return ((MWRelationalDescriptor) descriptor).isTableDescriptor();
	}

		
	public boolean usesBatchReading() {
		return this.batchReading;
	}

	public void setUsesBatchReading(boolean newValue) {
		boolean oldValue = this.batchReading;
		this.batchReading = newValue;
		this.firePropertyChanged(BATCH_READING_PROPERTY, oldValue, newValue);
	}
	

	// **************** MWRelationalMapping implementation ***************
	
	public boolean parentDescriptorIsAggregate() {
		return ((MWRelationalDescriptor) getParentDescriptor()).isAggregateDescriptor();
	}

	public MWRelationalDescriptor getParentRelationalDescriptor() {
		return (MWRelationalDescriptor) getParentDescriptor();
	}

	
	// **************** Automap support ***************

	public void automap() {
		super.automap();
		this.automapTableReference();
		this.automapIndirection();
	}

	/**
	 * this method is overridden in MWManyToManyMapping
	 */
	protected void automapTableReference() {
		if (this.getReference() != null) {
			return;	// if we already have a reference, do nothing
		}
		MWRelationalDescriptor referenceDescriptor = (MWRelationalDescriptor) this.getReferenceDescriptor();
		if (referenceDescriptor == null) {
			return;	// if we don't have a reference descriptor, we can't find a reference
		}

		Collection candidateReferences = this.buildCandidateReferences();
		if (candidateReferences.isEmpty()) {
			return;
		}

		// find the best reference
		Set sourceTables = CollectionTools.set(this.getParentRelationalDescriptor().associatedTables());
		Set targetTables = CollectionTools.set(referenceDescriptor.associatedTables());
		for (Iterator stream = candidateReferences.iterator(); stream.hasNext();) {
			MWReference reference = (MWReference) stream.next();
			MWTable sourceTable = reference.getSourceTable();
			MWTable targetTable = reference.getTargetTable();

			if ((sourceTables.contains(sourceTable) && targetTables.contains(targetTable)) ||
				 (sourceTables.contains(targetTable) && targetTables.contains(sourceTable)))
			{
				this.setReference(reference);
				break;
			}
		}
	}
	
	/**
	 * build and return the set of candidate references
	 * that can be used by the mapping
	 */
	protected abstract Set buildCandidateReferences();

	/**
	 * convenience method for subclass implementations of #buildCandidateReferences():
	 * return all the references that point from the reference descriptor's table(s)
	 * to the mapping's descriptor's table(s) ("target foreign key")
	 */
	protected Set buildCandidateTargetReferences() {
		MWRelationalDescriptor referenceDescriptor = (MWRelationalDescriptor) this.getReferenceDescriptor();
		if (referenceDescriptor == null) {
			return Collections.EMPTY_SET;
		}

		Set candidateReferences = new HashSet();
		Set targetTables = CollectionTools.set(this.getParentRelationalDescriptor().candidateTablesIncludingInherited());
		for (Iterator sourceTables = referenceDescriptor.associatedTablesIncludingInherited(); sourceTables.hasNext(); ) {
			MWTable sourceTable = (MWTable) sourceTables.next();
			for (Iterator references = sourceTable.references(); references.hasNext(); ) {
				MWReference refererence = (MWReference) references.next();
				if (targetTables.contains(refererence.getTargetTable())) {
					candidateReferences.add(refererence);
				}
			}
		}
		return candidateReferences;
	}

	/**
	 * convenience method for subclass implementations of #buildCandidateReferences():
	 * return all the references that point from the mapping's descriptor's table(s)
	 * to the reference descriptor's table(s) ("source foreign key")
	 */
	protected Set buildCandidateSourceReferences() {
		MWRelationalDescriptor referenceDescriptor = (MWRelationalDescriptor) this.getReferenceDescriptor();
		if (referenceDescriptor == null) {
			return Collections.EMPTY_SET;
		}

		Set candidateReferences = new HashSet();
		Set targetTables = CollectionTools.set(referenceDescriptor.associatedTables());
		for (Iterator sourceTables = this.getParentRelationalDescriptor().candidateTables(); sourceTables.hasNext(); ) {
			MWTable sourceTable = (MWTable) sourceTables.next();
			for (Iterator references = sourceTable.references(); references.hasNext(); ) {
				MWReference reference = (MWReference) references.next();
				if (targetTables.contains(reference.getTargetTable())) {
					candidateReferences.add(reference);
				}
			}
		}
		return candidateReferences;
	}

	protected void automapIndirection() {
		if (this.getInstanceVariable().isValueHolder()) {
			this.setUseValueHolderIndirection();
		}
	}


	// *************  MWTableReferenceMapping implementation ************

	public Iterator candidateReferences() {
		return this.buildCandidateReferences().iterator();
	}

	public boolean referenceIsCandidate(MWReference reference) {
		return this.buildCandidateReferences().contains(reference);
	}


	// ************* MWQueryable implementation ************

	public boolean isTraversableForBatchReadAttribute() {
		return true;
	}
	
	public boolean isValidForBatchReadAttribute() {
		return true;
	}
	
	public boolean isTraversableForJoinedAttribute() {
		return true;
	}
	
	public boolean isValidForJoinedAttribute() {
		return true;
	}
	
	// *************  Aggregate support ************
	
	protected abstract boolean fieldIsWritten(MWColumnPair association);
	
	protected static class ColumnPairAggregateRuntimeFieldNameGenerator
		implements AggregateRuntimeFieldNameGenerator 
	{
		MWAbstractTableReferenceMapping mapping;
		private MWColumnPair columnPair;
		
		private boolean source;
				
		ColumnPairAggregateRuntimeFieldNameGenerator(MWAbstractTableReferenceMapping mapping, MWColumnPair columnPair, boolean source) {
			super();
			this.mapping = mapping;
			this.columnPair = columnPair;
			this.source = source;
		}	
		
		public boolean fieldIsWritten() {
			return this.mapping.fieldIsWritten(this.columnPair);
		}

		public String fieldNameForRuntime() {
			MWColumn field;
			if (this.source) {
				field = this.columnPair.getSourceColumn();
			}
			else {
				field = this.columnPair.getTargetColumn();
			}
		
			return field.getName() + "_IN_REFERENCE_" + this.mapping.getReference().getName();
		}
		
		public AggregateFieldDescription fullFieldDescription() {
			final MWColumn column;
			if (this.source) {
				column = this.columnPair.getSourceColumn();
			}
			else {
				column = this.columnPair.getTargetColumn();
			}
			return new AggregateFieldDescription() {
				public String getMessageKey() {
					return "AGGREGATE_FIELD_DESCRIPTION_FOR_REFERENCE";
				}
			
				public Object[] getMessageArguments() {
					return new Object[] {column.getName(), ColumnPairAggregateRuntimeFieldNameGenerator.this.mapping.getReference().getName()};
				}
			};
		}	
		
		public MWDescriptor owningDescriptor() {
			throw new UnsupportedOperationException();
		}

	}
	
	public boolean isTableReferenceMapping(){
		return true;
	}
	
	public boolean isManyToManyMapping(){
		return false;
	}
	
	public boolean isOneToManyMapping(){
		return false;
	}
	
	public boolean isQueryable() {
		return true;
	}
	
	// ************ MWQueryable implementation ***********	
	
	public boolean allowsChildren() {
		return true;
	}
	
	public boolean allowsOuterJoin() {
		return allowsChildren();
	}
		
	public boolean isLeaf(Filter queryableFilter) {
		return subQueryableElements(queryableFilter).size() == 0;
	}
	
	public List subQueryableElements(Filter queryableFilter) {
		List subQueryableElements = new ArrayList();
		if (getReferenceDescriptor() != null) {
			subQueryableElements = ((MWRelationalDescriptor) getReferenceDescriptor()).getQueryables(queryableFilter);
            Collections.sort(subQueryableElements, DEFAULT_COMPARATOR);
        }
		return subQueryableElements;
	}	

	public MWQueryable subQueryableElementAt(int index, Filter queryableFilter) {
		return (MWQueryable) subQueryableElements(queryableFilter).get(index);
	}
    
    public boolean isTraversableForQueryExpression() {
        return true;
    }
    
    public boolean isValidForQueryExpression() {
        return true;
    }
    
    public boolean isTraversableForReportQueryAttribute() {
        return true;
    }
    
	// ************* Mapping morphing support ************
	
	/**
	* IMPORTANT:  See MWRMapping class comment.
	*/
	public void initializeFromMWAbstractTableReferenceMapping(MWAbstractTableReferenceMapping oldMapping) {
		super.initializeFromMWAbstractTableReferenceMapping(oldMapping);
		this.setReference(oldMapping.getReference());
		this.setMaintainsBidirectionalRelationship(oldMapping.maintainsBidirectionalRelationship());
		this.setRelationshipPartnerMapping(oldMapping.getRelationshipPartnerMapping());
		this.setUsesBatchReading(oldMapping.usesBatchReading());
	}
	
	public void initializeFromMWRelationalDirectContainerMapping(MWRelationalDirectContainerMapping oldMapping) {
		super.initializeFromMWRelationalDirectContainerMapping(oldMapping);
		this.setReference(oldMapping.getReference());
		this.setUsesBatchReading(oldMapping.usesBatchReading());
	}
	
		
	public boolean isValidRelationshipPartner() {
		return true;
	}
	
	
	//********* model synchronization support *********
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.referenceHandle);
		children.add(this.relationshipPartnerMappingHandle);
	}
		
	private NodeReferenceScrubber buildRelationshipPartnerMappingScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAbstractTableReferenceMapping.this.setRelationshipPartnerMapping(null);
			}
			public String toString() {
				return "MWAbstractTableReferenceMapping.buildRelationshipPartnerMappingScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildReferenceScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAbstractTableReferenceMapping.this.setReference(null);
			}
			public String toString() {
				return "MWAbstractTableReferenceMapping.buildReferenceScrubber()";
			}
		};
	}

	public void descriptorUnmapped(Collection mappings) {
		super.descriptorUnmapped(mappings);
		if (getRelationshipPartnerMapping() != null && mappings.contains(getRelationshipPartnerMapping())) {
			setRelationshipPartnerMapping(null);
		}
	}
	
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		super.mappingReplaced(oldMapping, newMapping);
		if (oldMapping == getRelationshipPartnerMapping()) {
			setRelationshipPartnerMapping(newMapping);
		}
	}
	
	
	/**
	 * Returns true if this mapping behaves like a 1-1, where the source table (e.g. EMPLOYEE) has
	 * a foreign key to the target table (e.g., ADDRESS);
	 **/
	public boolean sourceReferenceFieldsAreFromSourceDescriptorTables() {
		return false;
	}
	
	
	// ************** Problem Handling **************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkReference(newProblems);
		this.addInvalidRelationshipPartnerProblemTo(newProblems);
		this.addHasNonMutualRelationshipPartnerProblemTo(newProblems);
	}

	protected String referenceDescriptorInvalidProblemString() {
		return ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_RELATIONAL_DESCRIPTOR;
	}

	private void checkReference(List newProblems) {
		// Verify that the mapping has a reference associated with it
		if (this.getReference() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_TABLE_REFERENCE_NOT_SPECIFIED));
			return;
		}
		if ( ! this.referenceIsCandidate(this.getReference())) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_TABLE_REFERENCE_INVALID));
		}
	}
	
	private void addInvalidRelationshipPartnerProblemTo(List newProblems) {
		if ( ! this.maintainsBidirectionalRelationship()) {
			return;
		}
		if (this.getRelationshipPartnerMapping() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_NOT_SPECIFIED));
		}
		else if ( ! this.getRelationshipPartnerMapping().isValidRelationshipPartner()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_INVALID));
		}
	}
	
	private void addHasNonMutualRelationshipPartnerProblemTo(List newProblems) {
		MWMapping relationshipPartnerMapping = this.getRelationshipPartnerMapping();
		if (this.maintainsBidirectionalRelationship() && (relationshipPartnerMapping != null)) {
			if ( ! (relationshipPartnerMapping.maintainsBidirectionalRelationship() && (relationshipPartnerMapping.getRelationshipPartnerMapping() == this))) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_NOT_MUTUAL));
			}
		} 
	}
	
	
	// ************** Runtime Conversion**************
	
	public DatabaseMapping runtimeMapping() {
		ForeignReferenceMapping runtimeMapping = (ForeignReferenceMapping) super.runtimeMapping();
		
		if (this.getReference() == null) {
			return runtimeMapping;
		}
		
		if ((getReferenceDescriptor() != null) && (getReferenceDescriptor().getMWClass() != null)) {
			runtimeMapping.setReferenceClassName(getReferenceDescriptor().getMWClass().getName());
		}
		if (maintainsBidirectionalRelationship()) {
			if (getRelationshipPartnerMapping() != null) {
				runtimeMapping.setRelationshipPartnerAttributeName(getRelationshipPartnerMapping().getInstanceVariable().getName());
			}
		}
		
        this.joinFetchOption.setMWOptionOnTopLinkObject(runtimeMapping);
		runtimeMapping.setUsesBatchReading(usesBatchReading());
		
		return runtimeMapping;	
	}

	

	// **************** TopLink methods *****************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractTableReferenceMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractReferenceMapping.class);

		XMLDirectMapping mbdrMapping = (XMLDirectMapping) descriptor.addDirectMapping("maintainsBidirectionalRelationship", "maintains-bidirectional-relationship/text()");
		mbdrMapping.setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping relationshipPartnerMapping = new XMLCompositeObjectMapping();
		relationshipPartnerMapping.setAttributeName("relationshipPartnerMappingHandle");
		relationshipPartnerMapping.setGetMethodName("getRelationshipPartnerMappingHandleForTopLink");
		relationshipPartnerMapping.setSetMethodName("setRelationshipPartnerMappingHandleForTopLink");
		relationshipPartnerMapping.setReferenceClass(MWMappingHandle.class);
		relationshipPartnerMapping.setXPath("relationship-partner-mapping-handle");
		descriptor.addMapping(relationshipPartnerMapping);

		XMLCompositeObjectMapping referenceHandleMapping = new XMLCompositeObjectMapping();
		referenceHandleMapping.setAttributeName("referenceHandle");
		referenceHandleMapping.setGetMethodName("getReferenceHandleForTopLink");
		referenceHandleMapping.setSetMethodName("setReferenceHandleForTopLink");
		referenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		referenceHandleMapping.setXPath("reference-handle");
		descriptor.addMapping(referenceHandleMapping);

		// Join Fetching
		ObjectTypeConverter joinFetchingConverter = new ObjectTypeConverter();
		MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().addConversionValuesForTopLinkTo(joinFetchingConverter);
		XMLDirectMapping joinFetchingMapping = new XMLDirectMapping();
		joinFetchingMapping.setAttributeName("joinFetchOption");
		joinFetchingMapping.setXPath("join-fetch-option/text()");
		joinFetchingMapping.setNullValue(MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(JOIN_FETCH_NONE));
		joinFetchingMapping.setConverter(joinFetchingConverter);
		descriptor.addMapping(joinFetchingMapping);		

		// Batch Reading
		XMLDirectMapping batchReadingMapping = (XMLDirectMapping) descriptor.addDirectMapping("batchReading", "uses-batch-reading/text()");
		batchReadingMapping.setNullValue(Boolean.FALSE);

		return descriptor;
	}
	
	/**
	 * check for null
	 */
	private MWMappingHandle getRelationshipPartnerMappingHandleForTopLink() {
		return (this.relationshipPartnerMappingHandle.getMapping() == null) ? null : this.relationshipPartnerMappingHandle;
	}
	private void setRelationshipPartnerMappingHandleForTopLink(MWMappingHandle handle) {
		NodeReferenceScrubber scrubber = this.buildRelationshipPartnerMappingScrubber();
		this.relationshipPartnerMappingHandle = ((handle == null) ? new MWMappingHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
		
	/**
	 * check for null
	 */
	private MWReferenceHandle getReferenceHandleForTopLink() {
		return (this.referenceHandle.getReference() == null) ? null : this.referenceHandle;
	}
	private void setReferenceHandleForTopLink(MWReferenceHandle referenceHandle) {
		NodeReferenceScrubber scrubber = this.buildReferenceScrubber();
		this.referenceHandle = ((referenceHandle == null) ? new MWReferenceHandle(this, scrubber) : referenceHandle.setScrubber(scrubber));
	}
	
	protected void setJoinFetchingForToplink(String newJoinFetching) {
		this.joinFetchOption = (JoinFetchOption) MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(newJoinFetching);
	}
	
	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWAbstractTableReferenceMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractReferenceMapping.class);

		XMLDirectMapping mbdrMapping = (XMLDirectMapping) descriptor.addDirectMapping("maintainsBidirectionalRelationship", "maintains-bidirectional-relationship/text()");
		mbdrMapping.setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping relationshipPartnerMapping = new XMLCompositeObjectMapping();
		relationshipPartnerMapping.setAttributeName("relationshipPartnerMappingHandle");
		relationshipPartnerMapping.setGetMethodName("getRelationshipPartnerMappingHandleForTopLink");
		relationshipPartnerMapping.setSetMethodName("setRelationshipPartnerMappingHandleForTopLink");
		relationshipPartnerMapping.setReferenceClass(MWMappingHandle.class);
		relationshipPartnerMapping.setXPath("relationship-partner-mapping-handle");
		descriptor.addMapping(relationshipPartnerMapping);

		XMLCompositeObjectMapping referenceHandleMapping = new XMLCompositeObjectMapping();
		referenceHandleMapping.setAttributeName("referenceHandle");
		referenceHandleMapping.setGetMethodName("getReferenceHandleForTopLink");
		referenceHandleMapping.setSetMethodName("setReferenceHandleForTopLink");
		referenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		referenceHandleMapping.setXPath("reference-handle");
		descriptor.addMapping(referenceHandleMapping);

		// Batch Reading
		XMLDirectMapping batchReadingMapping = (XMLDirectMapping) descriptor.addDirectMapping("batchReading", "uses-batch-reading/text()");
		batchReadingMapping.setNullValue(Boolean.FALSE);

		return descriptor;
	}

	@Override
	protected void legacy60PostBuild(DescriptorEvent event) {
		super.legacy60PostBuild(event);
		if (this.joinFetchOption == null) {
			this.joinFetchOption = (JoinFetchOption)MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(JOIN_FETCH_NONE);
		}
	}
	
	public TopLinkOptionSet joinFetchOptions() {
		if (joinFetchOptions == null) {
			joinFetchOptions = MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions();
		}
		return joinFetchOptions;
	}
}
