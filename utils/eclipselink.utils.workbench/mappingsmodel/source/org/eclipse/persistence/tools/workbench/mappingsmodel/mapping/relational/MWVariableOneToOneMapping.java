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
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.ColumnStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWAbstractReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWProxyIndirectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.SimplePartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher.StringHolderScore;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Record;

public final class MWVariableOneToOneMapping 
	extends MWAbstractReferenceMapping
	implements MWProxyIndirectionMapping, MWClassIndicatorPolicy.Parent, MWRelationalClassIndicatorFieldPolicy.Parent
{
    private Collection columnQueryKeyPairs;
		public final static String COLUMN_QUERY_KEY_PAIRS_COLLECTION = "columnQueryKeyPairs";   	
	
	//TODO this shouldn't be a full classIndicatorFieldPolicy
	//we don't support class name is indicator
	private volatile MWRelationalClassIndicatorFieldPolicy classIndicatorPolicy;
	

	
	// ********** static methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWVariableOneToOneMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractReferenceMapping.class);
		
		XMLCompositeCollectionMapping columnQueryKeyPairsMapping = new XMLCompositeCollectionMapping();
		columnQueryKeyPairsMapping.setAttributeName("columnQueryKeyPairs");
		columnQueryKeyPairsMapping.setGetMethodName("getColumnQueryKeyPairsForTopLink");
		columnQueryKeyPairsMapping.setSetMethodName("setColumnQueryKeyPairsForTopLink");
		columnQueryKeyPairsMapping.setReferenceClass(MWColumnQueryKeyPair.class);
		columnQueryKeyPairsMapping.setXPath("column-query-key-pairs/column-query-key-pair");
		descriptor.addMapping(columnQueryKeyPairsMapping);
	
		XMLCompositeObjectMapping classIndicatorPolicyMapping = new XMLCompositeObjectMapping();
		classIndicatorPolicyMapping.setAttributeName("classIndicatorPolicy");
		classIndicatorPolicyMapping.setReferenceClass(MWRelationalClassIndicatorFieldPolicy.class);
		classIndicatorPolicyMapping.setXPath("class-indicator-policy");
		descriptor.addMapping(classIndicatorPolicyMapping);
	
		return descriptor;
	}
	
	private MWVariableOneToOneMapping() {
		super();
	}
	
	MWVariableOneToOneMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}
	
	
	// **************** Initialization **************
		
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.columnQueryKeyPairs = new Vector();
		this.classIndicatorPolicy = new MWRelationalClassIndicatorFieldPolicy(this);
	}
	
	protected void initialize(MWClassAttribute attribute, String name) {
		super.initialize(attribute, name);
		if (!getInstanceVariable().isValueHolder() && getInstanceVariable().getType().isInterface()) {
			this.indirectionType = PROXY_INDIRECTION;
		}
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.columnQueryKeyPairs) { children.addAll(this.columnQueryKeyPairs); }
		children.add(this.classIndicatorPolicy);
	}
	
	
	// ************** Accessors **************
	
	public MWRelationalClassIndicatorFieldPolicy getClassIndicatorPolicy() {
		return this.classIndicatorPolicy;
	}
	
	public void setReferenceDescriptor(MWDescriptor newValue) {
		super.setReferenceDescriptor(newValue);

		if (newValue != null)
			getClassIndicatorPolicy().resetDescriptorAvailableForIndication(((MWRelationalProject) getProject()).descriptorsThatImplement((MWRelationalDescriptor) getReferenceDescriptor()));
		else
			getClassIndicatorPolicy().resetDescriptorAvailableForIndication(NullIterator.instance());
	}
	
	public boolean descriptorIsValidReferenceDescriptor(MWDescriptor descriptor) {
		return ((MWRelationalDescriptor) descriptor).isInterfaceDescriptor();
	}
	

	public MWColumnQueryKeyPair addColumnQueryKeyPair(MWColumn column, String queryKeyName) {
		return addColumnQueryKeyPair(new MWColumnQueryKeyPair(this, column, queryKeyName));
	}
	
	private MWColumnQueryKeyPair addColumnQueryKeyPair(MWColumnQueryKeyPair columnQueryKeyPair) {
		this.addItemToCollection(columnQueryKeyPair, this.columnQueryKeyPairs, COLUMN_QUERY_KEY_PAIRS_COLLECTION);
		this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
		return columnQueryKeyPair;	
	}
	
	public void removeColumnQueryKeyPair(MWColumnQueryKeyPair columnQueryKeyPair) {
		this.removeItemFromCollection(columnQueryKeyPair, this.columnQueryKeyPairs, COLUMN_QUERY_KEY_PAIRS_COLLECTION);	
		this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
	}

	public Iterator columnQueryKeyPairs() {
		return new CloneIterator(this.columnQueryKeyPairs);
	}
	
	public int columnQueryKeyPairsSize() {
		return this.columnQueryKeyPairs.size();
	}

	
	/**
	 * Returns all possible query key names that can be used for this mapping
	 */
	public Iterator queryKeyNameChoices() {
		if (getReferenceDescriptor() == null) {
			return NullIterator.instance();
		}
		return ((MWRelationalDescriptor) getReferenceDescriptor()).allQueryKeyNames();
	}
	
	public Collection invalidQueryKeyNames() {
		Collection namesOfInvalidQueryKeys = new ArrayList();
		MWRelationalDescriptor referenceDescriptor = (MWRelationalDescriptor) getReferenceDescriptor();
		
		if (columnQueryKeyPairsSize() > 0
			&& referenceDescriptor != null)
		{
			for (Iterator associations = columnQueryKeyPairs(); associations.hasNext(); )
			{
				MWColumnQueryKeyPair pair = (MWColumnQueryKeyPair)associations.next(); 
				
				if (referenceDescriptor.queryKeyNamed(pair.getQueryKeyName()) == null)
					namesOfInvalidQueryKeys.add(pair.getQueryKeyName());
			}
		}
	
		return namesOfInvalidQueryKeys;		
	}
	
    // *********** MWProxyIndirectionMapping implementation ***********
    
    public boolean usesProxyIndirection() {
        return getIndirectionType() == PROXY_INDIRECTION;
    }

    public void setUseProxyIndirection() {
        setIndirectionType(PROXY_INDIRECTION);  
    }
    
    // *********** MWQueryable implementation ************
    
    public String iconKey() {
        return "mapping.variableOneToOne";
    }
    
	// **************** Behavior ***************
	
	//TODO change this to 2 separate calls, implementorAdded and implementorRemoved
	public void implementorsChangedFor(MWInterfaceDescriptor descriptor) {
		if (getReferenceDescriptor() == descriptor) {
			Collection implementors = CollectionTools.collection(((MWRelationalProject) getProject()).descriptorsThatImplement(descriptor));
			
			removeOldIndicators(implementors);
			addNewIndicators(implementors);
		}
	}

	private void removeOldIndicators(Collection currentImplementors) {
		Iterator currentIndicatorValues = CollectionTools.collection(this.classIndicatorPolicy.classIndicatorValues()).iterator();
		while (currentIndicatorValues.hasNext()) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) currentIndicatorValues.next();
			if (!currentImplementors.contains(value.getDescriptorValue())) {
				getClassIndicatorPolicy().removeIndicator(value);
			}
		}
	}
	
	private void addNewIndicators(Collection currentImplementors) {
		Iterator implementorsIterator = currentImplementors.iterator();
		while (implementorsIterator.hasNext()) {
			MWMappingDescriptor classDescriptor = (MWMappingDescriptor) implementorsIterator.next();
			if (getClassIndicatorPolicy().getClassIndicatorValueForDescriptor(classDescriptor) == null) {
				getClassIndicatorPolicy().addIndicator(null, classDescriptor);
			}	
		}			
	}
	
	
	// ************** Aggregate Support ************
	
	public void parentDescriptorMorphedToAggregate() {
		super.parentDescriptorMorphedToAggregate();
		for (Iterator stream = columnQueryKeyPairs(); stream.hasNext(); ) {
			((MWColumnQueryKeyPair) stream.next()).setColumn(null);
		}
		getClassIndicatorPolicy().setField(null);
	}

	protected Collection buildAggregateFieldNameGenerators() {
		Collection generators = super.buildAggregateFieldNameGenerators();
		generators.add(this.classIndicatorPolicy);
		
		for (Iterator stream = columnQueryKeyPairs(); stream.hasNext(); ) {
			generators.add(stream.next());
		}	
		
		return generators;
	}

	
	// **************** Morphing ***************
	
	public MWVariableOneToOneMapping asMWVariableOneToOneMapping() {
		return this;
	}
	
	/**
	 * IMPORTANT:  See MWMapping class comment.
	 */
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWVariableOneToOneMapping(this);
	}
	
	protected void initializeFromMWIndirectableContainerMapping(MWIndirectableContainerMapping oldMapping) {
		super.initializeFromMWIndirectableContainerMapping(oldMapping);
		if (oldMapping.usesValueHolderIndirection()) {
			this.setUseValueHolderIndirection();
		}
		else if (oldMapping.usesNoIndirection()) {
			this.setUseNoIndirection();
		}
	}
	
	protected void initializeFromMWIndirectableMapping(MWIndirectableMapping oldMapping) {
		super.initializeFromMWIndirectableMapping(oldMapping);
		if (oldMapping.usesValueHolderIndirection()) {
			this.setUseValueHolderIndirection();
		}
	}

	public void addWrittenFieldsTo(Collection writtenFields) {
		if (this.isReadOnly()) {
			return;
		}

		for (Iterator stream = this.columnQueryKeyPairs(); stream.hasNext(); ) {
			MWColumnQueryKeyPair association = (MWColumnQueryKeyPair) stream.next();
			if (association.getColumn() != null) {
				writtenFields.add(association.getColumn());
			}
		}

		if (this.getClassIndicatorPolicy().getField() != null) {
			writtenFields.add(this.getClassIndicatorPolicy().getField());
		}
	}
	
	// **************** MWRelationalMapping implementation ***************

	public boolean parentDescriptorIsAggregate() {
		return ((MWRelationalDescriptor) getParentDescriptor()).isAggregateDescriptor();
	}

	public MWRelationalDescriptor getParentRelationalDescriptor() {
		return (MWRelationalDescriptor) getParentDescriptor();
	}


	// **************** MWClassIndicatorPolicy.Parent implementation **********
	
	public MWMappingDescriptor getContainingDescriptor() {
		return this.getParentDescriptor();
	}


	//*************** Problem Handling ***************

	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.addNoQueryKeyAssociationsProblemTo(newProblems);
		this.addForeignKeyFieldsInvalidProblemTo(newProblems);
		this.addForeignKeysNotSpecifiedProblemTo(newProblems);
		this.addInvalidQueryKeysProblemsTo(newProblems);
		this.addClassIndicatorValueProblemsTo(newProblems);
 	}

	protected String referenceDescriptorInvalidProblemString() {
		return ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_INTERFACE_DESCRIPTOR;
	}

	private void addNoQueryKeyAssociationsProblemTo(List newProblems) {
		if (this.columnQueryKeyPairsSize() == 0) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_NOT_SPECIFIED));
		}
	}

	private void addForeignKeyFieldsInvalidProblemTo(List newProblems) {
		ArrayList columns = new ArrayList();
		for (Iterator stream = this.getParentRelationalDescriptor().associatedTables(); stream.hasNext(); ) {
			CollectionTools.addAll(columns, ((MWTable) stream.next()).columns());
		}
		
		for (Iterator stream = this.columnQueryKeyPairs(); stream.hasNext();) {
			MWColumnQueryKeyPair pair = (MWColumnQueryKeyPair) stream.next();
			MWColumn column = pair.getColumn();
			if ((column != null) && ! columns.contains(column)) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_FIELD_INVALID,
						column.getName(), pair.getQueryKeyName()));
			}
		}
	}

	private void addForeignKeysNotSpecifiedProblemTo(List newProblems) {
		boolean pass = true;
		for (Iterator stream = this.columnQueryKeyPairs(); stream.hasNext();) {
			MWColumnQueryKeyPair pair = (MWColumnQueryKeyPair) stream.next();
			pass &= pair.getColumn() != null;
		}
		if( ! this.parentDescriptorIsAggregate() && ! pass) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_INCOMPLETE));
		}
	}

	private void addInvalidQueryKeysProblemsTo(List newProblems) {
		ArrayList args = new ArrayList();
		for (Iterator stream = this.invalidQueryKeyNames().iterator(); stream.hasNext(); ) {
			args.add(stream.next());
		}
		
		if (! args.isEmpty()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_INVALID,
							args));
		}
	}
	
	public void addClassIndicatorFieldNotSpecifiedProblemTo(List newProblems) {
		//CR#4067 - An indicator field is not necessary as long as the foreign key field is unique
		boolean uniqueForeignKeyFields = true;
		
		if (this.columnQueryKeyPairsSize() == 0) {
			uniqueForeignKeyFields = false;
		}
		
		for (Iterator stream = this.columnQueryKeyPairs(); stream.hasNext(); ) {
			MWColumn column = ((MWColumnQueryKeyPair) stream.next()).getColumn();
			
			if (column != null) {
				uniqueForeignKeyFields &= (column.isPrimaryKey() || column.isUnique());
			}
		}
		
		if (uniqueForeignKeyFields) {
			return;
		}

		if ( ! this.parentDescriptorIsAggregate() && (this.getClassIndicatorPolicy().getField() == null)) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_CLASS_INDICATOR_FIELD_NOT_SPECIFIED));
		}
	}
	
	private void addClassIndicatorValueProblemsTo(List newProblems) {
		MWRelationalDescriptor refDescriptor = (MWRelationalDescriptor) this.getReferenceDescriptor();
		if (refDescriptor == null || ! refDescriptor.isInterfaceDescriptor()) {
			return;
		}
		for (Iterator stream = this.getClassIndicatorPolicy().classIndicatorValues(); stream.hasNext();) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) stream.next();
			if ( ! CollectionTools.collection(refDescriptor.implementors()).contains(value.getDescriptorValue())
				&& value.isInclude()) {
					newProblems.add(this.buildClassIndicatorValueInvalidProblem(value.getDescriptorValue()));
			}
			
		}
	}

	private Problem buildClassIndicatorValueInvalidProblem(MWDescriptor descriptorValue) {
		return this.buildProblem(ProblemConstants.MAPPING_CLASS_INDICATOR_VALUES_INVALID,
							descriptorValue.getName(), this.getReferenceDescriptor().getName());
	}
    
	// ************* Automap Support ***********

	public void automap() {
		super.automap();
		this.automapClassIndicatorField();
	}

	private void automapClassIndicatorField() {
		if ((this.getClassIndicatorPolicy().getField() != null) || this.parentDescriptorIsAggregate()) {
			return;
		}

		MWTable primaryTable = ((MWTableDescriptor) this.getParentDescriptor()).getPrimaryTable();
		if (primaryTable == null) {
			return;
		}
		ColumnStringHolder[] columnStringHolders = ColumnStringHolder.buildHolders(primaryTable.nonPrimaryKeyColumns());
		StringHolderScore shs = COLUMN_NAME_MATCHER.match(this.getName().toLowerCase(), columnStringHolders);
		if (shs.getScore() > 0.50) {		// 0.50 ???
			this.getClassIndicatorPolicy().setField(((ColumnStringHolder) shs.getStringHolder()).getColumn());
		}
	}

	private static final PartialStringMatcher COLUMN_NAME_MATCHER = new SimplePartialStringMatcher(PartialStringComparator.DEFAULT_COMPARATOR);


	// ************* runtime conversion ***********
	

	protected DatabaseMapping buildRuntimeMapping() {
		return new VariableOneToOneMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		VariableOneToOneMapping runtimeMapping = (VariableOneToOneMapping) super.runtimeMapping();
		if ((getReferenceDescriptor() != null) && (getReferenceDescriptor().getMWClass() != null)) {
			runtimeMapping.setReferenceClassName(getReferenceDescriptor().getMWClass().getName());
		} 
		
		for (Iterator associations = columnQueryKeyPairs(); associations.hasNext(); ) {
			((MWColumnQueryKeyPair) associations.next()).adjustRuntimeMapping(runtimeMapping);
		}
	
		this.classIndicatorPolicy.adjustRuntimeMapping(runtimeMapping);
					
        if (usesProxyIndirection()) {
            runtimeMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
        }
		return runtimeMapping;
	}
	

	// *************** TopLink methods ***************
	
	/**
	 * sort the collection for TopLink
	 */
	private Collection getColumnQueryKeyPairsForTopLink() {
		return new TreeSet(this.columnQueryKeyPairs);
	}
	private void setColumnQueryKeyPairsForTopLink(Collection columnQueryKeyPairs) {
		this.columnQueryKeyPairs = columnQueryKeyPairs;
	}

    public void postProjectBuild() {
        super.postProjectBuild();
        if (getReferenceDescriptor() != null) {
            this.classIndicatorPolicy.setDescriptorsAvailableForIndicatorDictionaryForTopLink(((MWRelationalDescriptor) getReferenceDescriptor()).implementors());
        }
        else {
            this.classIndicatorPolicy.setDescriptorsAvailableForIndicatorDictionary(NullIterator.instance());
        }
    }
	
	private MWTable findBestMatchingTable(String tableName) {
		for (Iterator i = getDatabase().tables(); i.hasNext(); ) {
			MWTable table = (MWTable) i.next();
			if (table.getShortName().equals(tableName)) {
				return table;
			}
		}
		return null;
	}
	
}
