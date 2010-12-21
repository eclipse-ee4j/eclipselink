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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.ColumnStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping.JoinFetchOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.SimplePartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher.StringHolderScore;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


public abstract class MWRelationalDirectContainerMapping
	extends MWDirectContainerMapping
	implements MWTableReferenceMapping, MWIndirectableContainerMapping, MWJoinFetchableMapping
{

	private MWTableHandle targetTableHandle;
		public final static String TARGET_TABLE_PROPERTY = "targetTable";

	private MWColumnHandle directValueColumnHandle;
		public final static String DIRECT_VALUE_COLUMN_PROPERTY = "directValueColumn";
	
	private volatile JoinFetchOption joinFetchOption;
	private static TopLinkOptionSet joinFetchOptions;

	private MWReferenceHandle referenceHandle;

	/** see MWIndirectableContainerMapping */
	private volatile String indirectionType;		
	
	/** Indicates whether the referenced object should always be batch read on read all queries. */
	private volatile boolean batchReading;


	// **************** Constructors *****************

	protected MWRelationalDirectContainerMapping() {
		super();
	}

	public MWRelationalDirectContainerMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	
	// **************** Initialization *****************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.directValueColumnHandle = new MWColumnHandle(this, this.buildDirectValueColumnScrubber());
		this.referenceHandle = new MWReferenceHandle(this, this.buildReferenceScrubber());
		this.targetTableHandle = new MWTableHandle(this, this.buildTargetTableScrubber());
        this.joinFetchOption = (JoinFetchOption) MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(JOIN_FETCH_NONE);
	}
	
	protected void initialize(MWClassAttribute attribute, String name) {
		super.initialize(attribute, name);
		
		if (attribute.isEjb20Attribute()  || ! attribute.isValueHolder()) {
			this.setUseTransparentIndirection();
		} 
		else {
			this.setUseValueHolderIndirection(); 
		}
	}
	
	
	// **************** Containment Hierarchy *****************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.directValueColumnHandle);
		children.add(this.referenceHandle);
		children.add(this.targetTableHandle);
	}
	
	private NodeReferenceScrubber buildDirectValueColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalDirectContainerMapping.this.setDirectValueColumn(null);
			}
			public String toString() {
				return "MWRelationalDirectContainerMapping.buildDirectValueColumnScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildReferenceScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalDirectContainerMapping.this.setReference(null);
			}
			public String toString() {
				return "MWRelationalDirectContainerMapping.buildReferenceScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildTargetTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalDirectContainerMapping.this.setTargetTable(null);
			}
			public String toString() {
				return "MWRelationalDirectContainerMapping.buildTargetTableScrubber()";
			}
		};
	}

	
	// **************** Morphing ***************
	
	public void initializeFromMWRelationalDirectContainerMapping(MWRelationalDirectContainerMapping oldMapping) {
		super.initializeFromMWRelationalDirectContainerMapping(oldMapping);
		this.setTargetTable(oldMapping.getTargetTable());
		this.setDirectValueColumn(oldMapping.getDirectValueColumn());
		this.setReference(oldMapping.getReference());
		this.setTargetTable(oldMapping.getTargetTable());
		this.setUsesBatchReading(oldMapping.usesBatchReading());
	}
	

	public void initializeFromMWAbstractTableReferenceMapping(MWAbstractTableReferenceMapping oldMapping) {
		super.initializeFromMWAbstractTableReferenceMapping(oldMapping);
		this.setReference(oldMapping.getReference());
		this.setUsesBatchReading(oldMapping.usesBatchReading());
	}
	
	protected void initializeFromMWIndirectableMapping(MWIndirectableMapping oldMapping) {
		super.initializeFromMWIndirectableMapping(oldMapping);
		
		if (oldMapping.usesValueHolderIndirection()) {
			this.setUseValueHolderIndirection();
		}
		else if (oldMapping.usesNoIndirection()) {
			this.setUseNoIndirection();
		}
	}
	
	protected void initializeFromMWIndirectableContainerMapping(MWIndirectableContainerMapping oldMapping) {
		super.initializeFromMWIndirectableContainerMapping(oldMapping);
		
		if (oldMapping.usesTransparentIndirection()) {
			this.setUseTransparentIndirection();
		}
	}

	
	// **************** MWDirectMapping implementation ****************
	
	protected MWTypeConversionConverter buildTypeConversionConverter() {
		return new MWRelationalTypeConversionConverter(this);
	}


	// **************** MWRelationalMapping implementation ****************
	
	public boolean parentDescriptorIsAggregate() {
		return ((MWRelationalDescriptor) getParentDescriptor()).isAggregateDescriptor();
	}
	
	public MWRelationalDescriptor getParentRelationalDescriptor() {
		return (MWRelationalDescriptor) getParentDescriptor();
	}
	
	
	// **************** Target Table ******************************************

	public MWTable getTargetTable() {
		return this.targetTableHandle.getTable();
	}
	
	public void setTargetTable(MWTable newValue) {
		Object oldValue = this.targetTableHandle.getTable();
		this.targetTableHandle.setTable(newValue);
		firePropertyChanged(TARGET_TABLE_PROPERTY, oldValue, newValue);
		
		if (oldValue != newValue) {
			setDirectFieldsNull();
	}
	}

	protected void setDirectFieldsNull() {
		setDirectValueColumn(null);
	}

	// **************** Direct field ******************************************
	
	public MWColumn getDirectValueColumn() {
		return this.directValueColumnHandle.getColumn();
	}
	
	public void setDirectValueColumn(MWColumn newValue) {
		checkColumn(newValue);
		Object oldValue = this.directValueColumnHandle.getColumn();
		this.directValueColumnHandle.setColumn(newValue);
		firePropertyChanged(DIRECT_VALUE_COLUMN_PROPERTY, oldValue, newValue);
	}
	
	protected void checkColumn(MWColumn field) {
		if (field == null) {
			return;
		}
		if (getTargetTable() == null) {
			throw new IllegalStateException("The target table must be set first"); 
		}
		if (getTargetTable().columnNamed(field.getName()) == null) {
			throw new IllegalArgumentException("The field must be in the target table");
		}
	}
	
	// **************** JoinFetch *********************************************
	
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

	// **************** Reference *********************************************
	
	public MWReference getReference() {
		return this.referenceHandle.getReference();
	}
	
	public void setReference(MWReference newValue) {
		MWReference oldValue = getReference();
		this.referenceHandle.setReference(newValue);
		this.firePropertyChanged(REFERENCE_PROPERTY, oldValue, newValue);
		this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
	}
	
	
	// **************** Indirection type **************************************
	
	public boolean usesNoIndirection() {
		return this.indirectionType == NO_INDIRECTION;
	}
	
	public boolean usesValueHolderIndirection() {
		return this.indirectionType == VALUE_HOLDER_INDIRECTION;
	}
	
	public boolean usesTransparentIndirection() {
		return this.indirectionType == TRANSPARENT_INDIRECTION;
	}
	
	public void setUseNoIndirection() {
		setIndirectionType(NO_INDIRECTION);
	}
	
	public void setUseValueHolderIndirection() {
		setIndirectionType(VALUE_HOLDER_INDIRECTION);	
	}
	
	public void setUseTransparentIndirection() {
		setIndirectionType(TRANSPARENT_INDIRECTION);	
	}
	
	private void setIndirectionType(String indirectionType) {
		Object oldValue = this.indirectionType;
		this.indirectionType = indirectionType;
		firePropertyChanged(INDIRECTION_PROPERTY, oldValue, indirectionType);
	}
	
	
	// **************** Batch reading ****************
	
	public boolean usesBatchReading() {
		return this.batchReading;
	}
	
	public void setUsesBatchReading(boolean newValue) {
		boolean oldValue = this.batchReading;
		this.batchReading = newValue;
		this.firePropertyChanged(BATCH_READING_PROPERTY, oldValue, newValue);
	}
	

	// *************** MWQueryable interface *****************
	
	/**
	 * direct collection mapping can never have sub queryable elements(children)
	 */
	public boolean allowsChildren() {
		return false;
	}

	public boolean allowsOuterJoin() {
		return allowsChildren();
	}
		
	/**
	 * A directCollectionMapping will always be a leaf.  You cannot joined 
	 * anything to a directCollectionMapping
	 */
	public boolean isLeaf(Filter queryableFilter) {
		return true;
	}
	
	public boolean usesAnyOf() {
		return true;
	}	

	public boolean isValidForReadAllQueryOrderable() {
		return false;
	}

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
	
    public boolean isTraversableForQueryExpression() {
        return true;
    }
    
    public boolean isValidForQueryExpression() {
        return true;
    }
    

	// **************** MWTableReferenceMapping implementation *************************************
	
	public boolean referenceIsCandidate(MWReference reference) {
		if (getTargetTable() != null) {			
			if (reference.getSourceTable() == getTargetTable()) {
				if (CollectionTools.contains(
						getParentRelationalDescriptor().candidateTables(), 
						reference.getTargetTable())
					) 
				{
					return true;
			}
		}
	}
		return false;
	}

	public Iterator candidateReferences() {
		return this.buildCandidateReferences().iterator();
	}

	public Collection buildCandidateReferences() {
		HashSet references = new HashSet();
		boolean inClassDescriptor = ! this.parentDescriptorIsAggregate();
		MWColumn column = this.getDirectValueColumn();

		if ((column != null) && inClassDescriptor) {
			// be specific and use the direct value column's table
			MWTable sourceTable = column.getTable();
			MWTable targetTable = ((MWTableDescriptor) this.getParentDescriptor()).getPrimaryTable();
			if (targetTable != null) {
				CollectionTools.addAll(references, sourceTable.referencesBetween(targetTable));
			}
		} else {
			// be general and use all the most suitable references from all the tables
			for (Iterator stream1 = getDatabase().tables(); stream1.hasNext(); ) {
				MWTable table1 = (MWTable) stream1.next();
				if (inClassDescriptor) {
					MWTableDescriptor descriptor = (MWTableDescriptor) getParentDescriptor();
					for (Iterator stream2 = descriptor.candidateTables(); stream2.hasNext(); ) {
						MWTable table2 = (MWTable) stream2.next();
						CollectionTools.addAll(references, table1.referencesTo(table2));
					}
				} else {
					CollectionTools.addAll(references, table1.references());
				}
			}
		}

		return references;
	}


	// ********** MWMapping implementation **********

	protected DatabaseMapping buildRuntimeMapping() {
		return new DirectCollectionMapping();
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		//no written database fields in the owning table to add
	}


	// ************* Automap Support *******************

	/**
	 * Attempts to perform an automapping of this mapping.
	 */
	public void automap() {
		super.automap();
		this.automapIndirection();
		this.automapReference();
		this.automapDirectValueColumn();
	}

	public void automapIndirection() {
		if (this.getInstanceVariable().isValueHolder()) {
			this.setUseValueHolderIndirection();
		}
	}

	/**
	 * look for a reference from a table with just enough columns to hold
	 * the join key and the data (one column in the case of a direct
	 * collection; two, direct map)
	 */
	private void automapReference() {
		if ((this.getReference() != null) || this.parentDescriptorIsAggregate()) {
			return;
		}
		Collection references = this.buildCandidateReferences();
		if (references.isEmpty()) {
			return;
		}
		MWReference reference = this.findReasonableReference(references);
		this.setTargetTable(reference.getSourceTable());
		this.setReference(reference);
	}

	private MWReference findReasonableReference(Collection references) {
		MWReference result = null;
		for (Iterator stream = references.iterator(); stream.hasNext(); ) {
			MWReference reference = (MWReference) stream.next();
			if (result == null) {
				// take the first reference by default...
				result = reference;
			}
			// ...but continue to look for something better
			MWTable sourceTable = reference.getSourceTable();
			int refColumnPairsSize = reference.columnPairsSize();
			if (sourceTable.columnsSize() - refColumnPairsSize == this.automapNonPrimaryKeyColumnsSize()) {
				result = reference;
				break;
			}
		}
		return result;
	}

	protected abstract int automapNonPrimaryKeyColumnsSize();

	/**
	 * this isn't the most helpful automap: we need a good reference before we
	 * can do anything;
	 * look for a column in the table that matches the attribute's name
	 */
	private void automapDirectValueColumn() {
		if ((this.getDirectValueColumn() != null) || (this.getReference() == null)) {
			return;
		}

		ColumnStringHolder[] columnStringHolders = ColumnStringHolder.buildHolders(this.candidateDirectValueColumns());
		StringHolderScore shs = COLUMN_NAME_MATCHER.match(this.getName().toLowerCase(), columnStringHolders);
		if (shs.getScore() > 0.50) {		// 0.50 ???
			this.setDirectValueColumn(((ColumnStringHolder) shs.getStringHolder()).getColumn());
		}
	}

	private Iterator candidateDirectValueColumns() {
		return this.getReference().getSourceTable().columns();
	}

	private static final PartialStringMatcher COLUMN_NAME_MATCHER = new SimplePartialStringMatcher(PartialStringComparator.DEFAULT_COMPARATOR);


	// *************  Aggregate support ************
	
	protected Collection buildAggregateFieldNameGenerators() {
		Collection generators = super.buildAggregateFieldNameGenerators();
		if (getReference() != null) { 
			for (Iterator stream = getReference().columnPairs(); stream.hasNext(); ) {
				generators.add(new ColumnPairAggregateRuntimeFieldNameGenerator(this, (MWColumnPair) stream.next()));
			}
		}
		
		return generators;
	}
	

	private static class ColumnPairAggregateRuntimeFieldNameGenerator
		implements AggregateRuntimeFieldNameGenerator 
	{
		MWRelationalDirectContainerMapping mapping;
		private MWColumnPair columnPair;
		
		
		ColumnPairAggregateRuntimeFieldNameGenerator(MWRelationalDirectContainerMapping mapping, MWColumnPair columnPair) {
			super();
			this.mapping = mapping;
			this.columnPair = columnPair;
		}	
		
		public boolean fieldIsWritten() {
			return false;
		}
		
		public String fieldNameForRuntime() {
			return this.columnPair.getTargetColumn().getName() + "_IN_REFERENCE_" + this.mapping.getReference().getName();
		}
		
		public AggregateFieldDescription fullFieldDescription() {
			final MWColumn column = this.columnPair.getTargetColumn();
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
		


	//************** Problem Handling **********
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkDirectValueColumn(newProblems);
		this.checkReference(newProblems);
		this.checkValueHolderInstanceVariable(newProblems);
		this.checkNonValueHolderInstanceVariable(newProblems);
		this.checkTransparentIndirection(newProblems);
	}

	
	private void checkDirectValueColumn(List newProblems) {
		if (this.parentDescriptorIsAggregate()) {
			return;
		}
		if (this.getDirectValueColumn() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_DIRECT_VALUE_FIELD_NOT_SPECIFIED));
		}
	}
	
	private void checkReference(List newProblems) {
		if (this.getReference() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_TABLE_REFERENCE_NOT_SPECIFIED));
		} else {
			if ( ! this.referenceIsCandidate(this.getReference())) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_TABLE_REFERENCE_INVALID));
			}
		}
	}

	private void checkValueHolderInstanceVariable(List newProblems) {
		if (this.getInstanceVariable().isValueHolder() && ( ! this.usesValueHolderIndirection())) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION));
		}
	}
	
	private void checkNonValueHolderInstanceVariable(List newProblems) {
		if (( ! this.getProject().usesWeaving() && ! this.getInstanceVariable().isValueHolder()) && this.usesValueHolderIndirection()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE));
		}
	}
	
	private void checkTransparentIndirection(List newProblems) {
		if (this.usesTransparentIndirection()) {
			MWClass collectionType = this.conatinerPolicyClass();
			if ((collectionType != null) && ! collectionType.mightBeAssignableToIndirectContainer()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_INVALID_FOR_TRANSPARENT_INDIRECTION));
			}
		}
	}	
	
    protected abstract MWClass conatinerPolicyClass();
    


	// ************* Runtime Conversion *************
	
	public DatabaseMapping runtimeMapping() {
		DirectCollectionMapping directCollectionMapping = (DirectCollectionMapping) super.runtimeMapping();
		if (getDirectValueColumn() != null) {
			directCollectionMapping.setDirectFieldName(getDirectValueColumn().qualifiedName());
		}
		directCollectionMapping.setValueConverter(getConverter().runtimeConverter(directCollectionMapping));			
	
		if (usesValueHolderIndirection()) {
			directCollectionMapping.setIndirectionPolicy(new BasicIndirectionPolicy());
		} else if (usesTransparentIndirection()) {
			directCollectionMapping.setIndirectionPolicy(new TransparentIndirectionPolicy());
		} else {
			directCollectionMapping.setIndirectionPolicy(new NoIndirectionPolicy());
		}
        this.joinFetchOption.setMWOptionOnTopLinkObject(directCollectionMapping);
		directCollectionMapping.setUsesBatchReading(usesBatchReading());
		MWReference ref = getReference();

		if (ref != null) {
			directCollectionMapping.setReferenceTableName(ref.getSourceTable().getName());
			for (Iterator stream = ref.columnPairs(); stream.hasNext(); ) {
				MWColumnPair pair = (MWColumnPair) stream.next();
				if (pair.getSourceColumn() != null && pair.getTargetColumn() != null) {
					if (!parentDescriptorIsAggregate()) {
						directCollectionMapping.addReferenceKeyFieldName(pair.getSourceColumn().qualifiedName(), pair.getTargetColumn().qualifiedName());
					}
					else {
						directCollectionMapping.addReferenceKeyFieldName(pair.getSourceColumn().qualifiedName(), getName() + "->" + pair.getTargetColumn().getName() + "_IN_REFERENCE_"+ getReference().getName());
					}
				}
			}
		}
				
		return directCollectionMapping;
	}


	// ************* TopLink only methods *************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWRelationalDirectContainerMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWDirectContainerMapping.class);
		
		XMLCompositeObjectMapping targetTableHandleMapping = new XMLCompositeObjectMapping();
		targetTableHandleMapping.setAttributeName("targetTableHandle");
		targetTableHandleMapping.setGetMethodName("getTargetTableHandleForTopLink");
		targetTableHandleMapping.setSetMethodName("setTargetTableHandleForTopLink");
		targetTableHandleMapping.setReferenceClass(MWTableHandle.class);
		targetTableHandleMapping.setXPath("target-table-handle");
		descriptor.addMapping(targetTableHandleMapping);

		XMLCompositeObjectMapping directValueColumnHandleMapping = new XMLCompositeObjectMapping();
		directValueColumnHandleMapping.setAttributeName("directValueColumnHandle");
		directValueColumnHandleMapping.setGetMethodName("getDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setSetMethodName("setDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		directValueColumnHandleMapping.setXPath("direct-value-column-handle");
		descriptor.addMapping(directValueColumnHandleMapping);
		
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

		ObjectTypeConverter indirectionTypeConverter = new ObjectTypeConverter();
		indirectionTypeConverter.addConversionValue(NO_INDIRECTION, NO_INDIRECTION);
		indirectionTypeConverter.addConversionValue(VALUE_HOLDER_INDIRECTION, VALUE_HOLDER_INDIRECTION);
		indirectionTypeConverter.addConversionValue(TRANSPARENT_INDIRECTION, TRANSPARENT_INDIRECTION);
		
		
		XMLDirectMapping indirectionTypeMapping = new XMLDirectMapping();
		indirectionTypeMapping.setAttributeName("indirectionType");
		indirectionTypeMapping.setXPath("indirection-type/text()");
		indirectionTypeMapping.setNullValue(NO_INDIRECTION);
		indirectionTypeMapping.setConverter(indirectionTypeConverter);
		descriptor.addMapping(indirectionTypeMapping);
		
		XMLDirectMapping batchReadingMapping = (XMLDirectMapping) descriptor.addDirectMapping("batchReading", "uses-batch-reading/text()");
		batchReadingMapping.setNullValue(Boolean.FALSE);

		return descriptor;
	}

	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWRelationalDirectContainerMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWDirectContainerMapping.class);
		
		XMLCompositeObjectMapping targetTableHandleMapping = new XMLCompositeObjectMapping();
		targetTableHandleMapping.setAttributeName("targetTableHandle");
		targetTableHandleMapping.setGetMethodName("getTargetTableHandleForTopLink");
		targetTableHandleMapping.setSetMethodName("setTargetTableHandleForTopLink");
		targetTableHandleMapping.setReferenceClass(MWTableHandle.class);
		targetTableHandleMapping.setXPath("target-table-handle");
		descriptor.addMapping(targetTableHandleMapping);

		XMLCompositeObjectMapping directValueColumnHandleMapping = new XMLCompositeObjectMapping();
		directValueColumnHandleMapping.setAttributeName("directValueColumnHandle");
		directValueColumnHandleMapping.setGetMethodName("getDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setSetMethodName("setDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		directValueColumnHandleMapping.setXPath("direct-value-column-handle");
		descriptor.addMapping(directValueColumnHandleMapping);
		
		XMLCompositeObjectMapping referenceHandleMapping = new XMLCompositeObjectMapping();
		referenceHandleMapping.setAttributeName("referenceHandle");
		referenceHandleMapping.setGetMethodName("getReferenceHandleForTopLink");
		referenceHandleMapping.setSetMethodName("setReferenceHandleForTopLink");
		referenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		referenceHandleMapping.setXPath("reference-handle");
		descriptor.addMapping(referenceHandleMapping);

		ObjectTypeConverter indirectionTypeConverter = new ObjectTypeConverter();
		indirectionTypeConverter.addConversionValue(NO_INDIRECTION, NO_INDIRECTION);
		indirectionTypeConverter.addConversionValue(VALUE_HOLDER_INDIRECTION, VALUE_HOLDER_INDIRECTION);
		indirectionTypeConverter.addConversionValue(TRANSPARENT_INDIRECTION, TRANSPARENT_INDIRECTION);
		
		
		XMLDirectMapping indirectionTypeMapping = new XMLDirectMapping();
		indirectionTypeMapping.setAttributeName("indirectionType");
		indirectionTypeMapping.setXPath("indirection-type/text()");
		indirectionTypeMapping.setNullValue(NO_INDIRECTION);
		indirectionTypeMapping.setConverter(indirectionTypeConverter);
		descriptor.addMapping(indirectionTypeMapping);
		
		XMLDirectMapping batchReadingMapping = (XMLDirectMapping) descriptor.addDirectMapping("batchReading", "uses-batch-reading/text()");
		batchReadingMapping.setNullValue(Boolean.FALSE);

		return descriptor;
	}
	
	@Override
	protected void legacy60PostBuild(DescriptorEvent event) {
		super.legacy60PostBuild(event);
		this.joinFetchOption = (JoinFetchOption)MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().topLinkOptionForMWModelOption(JOIN_FETCH_NONE);
	}
	
	private MWTableHandle getTargetTableHandleForTopLink() {
		return (this.targetTableHandle.getTable() == null) ? null : this.targetTableHandle;
	}	
	private void setTargetTableHandleForTopLink(MWTableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildTargetTableScrubber();
		this.targetTableHandle = ((handle == null) ? new MWTableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	private MWColumnHandle getDirectValueColumnHandleForTopLink() {
		return (this.directValueColumnHandle.getColumn() == null) ? null : this.directValueColumnHandle;
	}	
	private void setDirectValueColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildDirectValueColumnScrubber();
		this.directValueColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
	private MWReferenceHandle getReferenceHandleForTopLink() {
		return (this.referenceHandle.getReference() == null) ? null : this.referenceHandle;
	}	
	private void setReferenceHandleForTopLink(MWReferenceHandle handle) {
		NodeReferenceScrubber scrubber = this.buildReferenceScrubber();
		this.referenceHandle = ((handle == null) ? new MWReferenceHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
	public TopLinkOptionSet joinFetchOptions() {
		if (joinFetchOptions == null) {
			joinFetchOptions = MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions();
		}
		return joinFetchOptions;
	}
	
}
