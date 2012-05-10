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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.CollectionStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.SimplePartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher.StringHolderScore;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWAggregateMapping 
	extends MWMapping 
	implements MWReferenceObjectMapping 
{
	private volatile boolean allowsNull;
		public final static String ALLOWS_NULL_PROPERTY = "allowsNull";

	private Collection pathsToFields;
		public final static String PATHS_TO_FIELDS_COLLECTION = "pathsToFields";

	private MWDescriptorHandle referenceDescriptorHandle;


	//************** constructors *******************

	/** Default constructor - for TopLink use only */
	private MWAggregateMapping() {
		super();
	}

	MWAggregateMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}


	//************** initialization *******************

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.pathsToFields = new Vector();
		this.referenceDescriptorHandle = new MWDescriptorHandle(this, this.buildReferenceDescriptorScrubber());
	}


	//************** containment hierarchy *******************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.pathsToFields) { children.addAll(this.pathsToFields); }
		children.add(this.referenceDescriptorHandle);
	}

	private NodeReferenceScrubber buildReferenceDescriptorScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAggregateMapping.this.setReferenceDescriptor(null);
			}
			public String toString() {
				return "MWAggregateMapping.buildReferenceDescriptorScrubber()";
			}
		};
	}

	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		if (this.getReferenceDescriptor() == oldDescriptor) {
			this.setReferenceDescriptor(newDescriptor);
		}
	}


	// **************** MWRelationalMapping implementation ***************
	
	public boolean parentDescriptorIsAggregate() {
		return ((MWRelationalDescriptor) getParentDescriptor()).isAggregateDescriptor();
	}

	public MWRelationalDescriptor getParentRelationalDescriptor() {
		return (MWRelationalDescriptor) getParentDescriptor();
	}


	// **************** Accessors ***************

	public boolean allowsNull() {
		return this.allowsNull;
	}
	
	public void setAllowsNull(boolean newValue) {
		boolean oldValue = this.allowsNull;
		this.allowsNull = newValue;
		this.firePropertyChanged(ALLOWS_NULL_PROPERTY, oldValue, newValue);
	}

	public Iterator pathsToFields() {
		return new CloneIterator(this.pathsToFields);
	}

	public int pathsToFieldsSize() {
		return this.pathsToFields.size();
	}

	private Collection getPathsToFieldsDeepCopy(MWAggregateMapping newParent) {
		Collection result = new Vector(this.pathsToFields.size());
		for (Iterator stream = this.pathsToFields(); stream.hasNext();) {
			result.add(((MWAggregatePathToColumn) stream.next()).copy(newParent));
		}
		return result;
	}

	public MWDescriptor getReferenceDescriptor() {
		return this.referenceDescriptorHandle.getDescriptor();
	}

	public void setReferenceDescriptor(MWDescriptor referenceDescriptor) {
		Object oldValue = getReferenceDescriptor();
		this.referenceDescriptorHandle.setDescriptor(referenceDescriptor);
		firePropertyChanged(REFERENCE_DESCRIPTOR_PROPERTY, oldValue, referenceDescriptor);

		updatePathsToFields();
		getProject().recalculateAggregatePathsToColumn(getParentDescriptor(), this);

		//newValue will not be null if the method resetReferenceDescriptor called this method.
		//resetReferenceDescriptor is called when morphing mappings.  The mapping's parent would
		//not be set at this point, so we must get the project from a different object.
		if (referenceDescriptor != null)
			referenceDescriptor.getProject().notifyExpressionsToRecalculateQueryables();
		else 	
			getProject().notifyExpressionsToRecalculateQueryables();
	}

	public boolean descriptorIsValidReferenceDescriptor(MWDescriptor descriptor) {
		return ((MWRelationalDescriptor) descriptor).isAggregateDescriptor();
	}

	// **************** morphing ***************

	/**
	 * IMPORTANT:  See MWRMapping class comment.
	 */
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWAggregateMapping(this);
	}
	
	protected void initializeFromMWReferenceObjectMapping(MWReferenceObjectMapping oldMapping) {
		super.initializeFromMWReferenceObjectMapping(oldMapping);
	
		this.setReferenceDescriptor(oldMapping.getReferenceDescriptor());
	}

	public MWAggregateMapping asMWAggregateMapping() {
		return this;
	}


	// **************** pathToFields behavior ***************
	/**
	 * Insert the given rootNode into the mappingNodes list of all the given subAssociations
	 */
	private void insertRootNodeIntoAll(MWMapping rootNode, Collection subAPTFs) {
		for (Iterator stream = subAPTFs.iterator(); stream.hasNext();) {
			MWAggregatePathToColumn aptf = (MWAggregatePathToColumn) stream.next();
			aptf.insertRootMappingNode(rootNode);
		}
	}

	private boolean compareAPTFs(MWAggregatePathToColumn aptf1, MWAggregatePathToColumn aptf2) {
		return (aptf1.getMappingNodes().containsAll(aptf2.getMappingNodes()))
			&& (aptf2.getMappingNodes().containsAll(aptf1.getMappingNodes()))
			&& (aptf1.getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime().equals(aptf2.getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime()));
	}
	/**
	 * Remove the specified APTF from the specified collection.
	 * Return whether the collection changed.
	 */
	private boolean collectionRemoveAPTF(Collection collection, MWAggregatePathToColumn aptf) {
		for (Iterator stream = collection.iterator(); stream.hasNext();) {
			if (this.compareAPTFs((MWAggregatePathToColumn) stream.next(), aptf)) {
				stream.remove();
				return true;
			}
		}
		return false;
	}
	/**
	 * Sets the pathsToFields to the union of the existing
	 * pathsToFields and the argument, plus any additional
	 * paths in the argument.
	 *
	 * In other words, if the existing path contains A, B, and C, and the
	 * argument contains C', D', and E', (where C and C' have the same
	 * path), then the new pathsToFields will contain C, D', and E'.
	 */
	private void mergePathsToFields(Collection recalculatedPathsToFields) {
		// remove any APTFs not in the recalculated set,
		// while simultaneously removing the duplicates from the recalculated set
		for (Iterator stream = this.pathsToFields.iterator(); stream.hasNext();) {
			MWAggregatePathToColumn aptf = (MWAggregatePathToColumn) stream.next();
			if (!this.collectionRemoveAPTF(recalculatedPathsToFields, aptf)) {
				stream.remove();
			}
		}
		// add whatever is left in the recalculated set
		addItemsToCollection(recalculatedPathsToFields, this.pathsToFields, PATHS_TO_FIELDS_COLLECTION);
	}

	private MWAggregatePathToColumn buildAPTF(MWMapping node, AggregateRuntimeFieldNameGenerator aggregateFieldNameGenerator) {
		MWAggregatePathToColumn newAPTF = new MWAggregatePathToColumn(this);
		newAPTF.addMappingNode(node);
		newAPTF.setAggregateRuntimeFieldNameGenerator(aggregateFieldNameGenerator);
		return newAPTF;
	}
	
	private MWAggregatePathToColumn buildAPTF(AggregateRuntimeFieldNameGenerator aggregateFieldNameGenerator) {
		MWAggregatePathToColumn newAPTF = new MWAggregatePathToColumn(this);
		newAPTF.setAggregateRuntimeFieldNameGenerator(aggregateFieldNameGenerator);
		return newAPTF;
	}

	/**
	 * Updates the paths to fields for this aggregate mapping.
	 */
	public void updatePathsToFields() {
		Collection newAPTFs = new ArrayList();
		if (this.getReferenceDescriptor() != null && getReferenceDescriptor() != getParentDescriptor()) {
			for (Iterator stream1 = this.getReferenceDescriptor().mappingsIncludingInherited(); stream1.hasNext();) {
				MWMapping subMapping = (MWMapping) stream1.next();
				if (subMapping instanceof MWAggregateMapping) {
					((MWAggregateMapping) subMapping).updatePathsToFields();
					Collection subAPTFs = ((MWAggregateMapping) subMapping).getPathsToFieldsDeepCopy(this);

					insertRootNodeIntoAll(subMapping, subAPTFs);
					newAPTFs.addAll(subAPTFs);
					
				}
				else {
					for (Iterator stream2 = subMapping.aggregateFieldNameGenerators(); stream2.hasNext();) {
						newAPTFs.add(this.buildAPTF(subMapping, (AggregateRuntimeFieldNameGenerator) stream2.next()));
					}

				}
			}
			for (Iterator stream = ((MWRelationalDescriptor) getReferenceDescriptor()).buildAggregateFieldNameGenerators().iterator(); stream.hasNext(); ) {
				newAPTFs.add(this.buildAPTF((AggregateRuntimeFieldNameGenerator) stream.next()));
			}
		}
		this.mergePathsToFields(newAPTFs);
		
		this.fireCollectionChanged(PATHS_TO_FIELDS_COLLECTION);
	}


	// ************** Code Generation **************
	
	/**
	 * Used for code gen.
	 * See MWRMapping.initialValue()
	 */
	public String initialValue(MWClassCodeGenPolicy classCodeGenPolicy) {
		String initialValue = super.initialValue(classCodeGenPolicy);
	
		if (! this.allowsNull()) {
			if (getReferenceDescriptor() != null) {
				initialValue = getInstanceVariable().initialValueSourceCodeFor(getReferenceDescriptor().getMWClass());
			}
	
			if ("".equals(initialValue)) {
				// the initial value could not be determined
				initialValue = classCodeGenPolicy.aggregateMappingDoesNotAllowNullImplementationClassNotDeterminedComment()
							   + StringTools.CR
							   + "\t\t" + null;
			} 
		
			initialValue = StringTools.CR
						   + "\t\t" +  classCodeGenPolicy.aggregateMappingDoesNotAllowNullComment(this)
						   + StringTools.CR
						   + "\t\t" + initialValue;
		}
	
		return initialValue;
	}


	
	// ************** MWQueryable implementation **************
	public boolean allowsChildren() {
		return true;
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


	public boolean isTraversableForReadAllQueryOrderable() {
		return true;
	}
	
	public boolean isTraversableForBatchReadAttribute() {
		return true;
    }
	
	public boolean isTraversableForJoinedAttribute() {
		return true;
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
    
	public String iconKey() {
		return "mapping.aggregate";
	}


	// ************* Automap Support *************************

	public void automap() {
		super.automap();
		this.automapReferenceDescriptor();
	}

	private void automapReferenceDescriptor() {
		if (this.getReferenceDescriptor() != null) {
			return;
		}

		MWDescriptor referenceDescriptor = this.findReferenceDescriptor();
		if (referenceDescriptor != null) {
			this.setReferenceDescriptor(referenceDescriptor);
		}
	}

	private MWDescriptor findReferenceDescriptor() {
		MWClass type = this.getInstanceVariable().getType();
		// calculate a name to use for finding a reference descriptor;
		// if the type is "non-descript", use the attribute name;
		// otherwise, use the "short" name of the attribute's declared type
		String name;
		if (type.isPrimitive() ||
			 type.isValueHolder() ||
			 type.isAssignableToMap() ||
			 type.isAssignableToCollection() ||
			 type.isAssignableTo(this.typeFor(Number.class)) ||
			 type == this.typeFor(String.class))
		{
			name = this.getName();
		} else {
			name = type.shortName();
		}

		CollectionStringHolder[] holders = this.buildMultiDescriptorStringHolders();
		StringHolderScore shs = this.match(name.toLowerCase(), holders);
		if (shs.getScore() < 0.80) {	// ???
			return null;
		}
		// look for a descriptor in the same package as this descriptor
		String packageName = this.getParentDescriptor().packageName();
		MWDescriptor descriptor = null;
		for (Iterator stream = ((CollectionStringHolder) shs.getStringHolder()).iterator(); stream.hasNext(); ) {
			descriptor = (MWDescriptor) stream.next();
			if (descriptor.packageName().equals(packageName)) {
				return descriptor;
			}
		}
		// if none of the descriptors is in the same package, take the last one
		return descriptor;
	}

	/**
	 * gather together all the "candidate" descriptors that have
	 * the same "short" name but different packages
	 */
	private CollectionStringHolder[] buildMultiDescriptorStringHolders() {
		Collection descriptors = this.candidateReferenceDescriptors();
		Map holders = new HashMap(descriptors.size());
		for (Iterator stream = descriptors.iterator(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			String shortName = descriptor.shortName().toLowerCase();
			CollectionStringHolder holder = (CollectionStringHolder) holders.get(shortName);
			if (holder == null) {
				holder = new CollectionStringHolder(shortName);
				holders.put(shortName, holder);
			}
			holder.add(descriptor);
		}
		return (CollectionStringHolder[]) holders.values().toArray(new CollectionStringHolder[holders.size()]);
	}

	private Collection candidateReferenceDescriptors() {
		Collection descriptors = new ArrayList();
		for (Iterator stream = this.getProject().descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (this.descriptorIsCandidateReferenceDescriptor(descriptor)) {
				descriptors.add(descriptor);
			}
		}
		return descriptors;
	}

	private boolean descriptorIsCandidateReferenceDescriptor(MWDescriptor descriptor) {
		return (descriptor != this.getParentDescriptor()) &&
				 ((MWRelationalDescriptor) descriptor).isAggregateDescriptor();
	}

	private StringHolderScore match(String string, CollectionStringHolder[] multiDescriptorStringHolders) {
		return PARTIAL_STRING_MATCHER.match(string, multiDescriptorStringHolders);
	}

	private static final PartialStringMatcher PARTIAL_STRING_MATCHER = new SimplePartialStringMatcher(PartialStringComparator.DEFAULT_COMPARATOR);


	//************** Problem Handling *******************

	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkReferenceDescriptorIsValid(newProblems);
		this.checkColumnsAreUnique(newProblems);
		this.checkColumnsAreValid(newProblems);		
	}

	private void checkReferenceDescriptorIsValid(List newProblems)	{
		if (this.getReferenceDescriptor() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED));
		}
		else if ( ! this.getReferenceDescriptor().isActive()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE, 
				this.getInstanceVariable().getName(), this.getReferenceDescriptor().getMWClass().shortName()));
		}
		else if ( ! this.descriptorIsValidReferenceDescriptor(this.getReferenceDescriptor())) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_AGGREGATE_DESCRIPTOR));
		}		
	}

	private void checkColumnsAreUnique(List newProblems) {
		if (this.getParentDescriptor() instanceof MWAggregateDescriptor)
			return;

		int numFields = this.pathsToFieldsSize();
		HashSet writeableFieldsSet = new HashSet(numFields);
		for (Iterator stream = this.pathsToFields(); stream.hasNext(); ) {
			MWAggregatePathToColumn pathToField = (MWAggregatePathToColumn) stream.next();
			MWColumn field = pathToField.getColumn();
			if (field != null && pathToField.fieldIsWritten() && ! writeableFieldsSet.add(field)) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_UNIQUE));
			}
		}
	}

	private void checkColumnsAreValid(List newProblems) {
		if (this.parentDescriptorIsAggregate()) {
			return;
		}
		for (Iterator stream = this.pathsToFields(); stream.hasNext(); ) {
			MWColumn field = ((MWAggregatePathToColumn) stream.next()).getColumn();
			if (field == null) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_SPECIFIED));
			}
			else if ( ! CollectionTools.contains(((MWTableDescriptor) this.getParentDescriptor()).allAssociatedColumns(), field)) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_VALID));
			}
		}
	}

	
	// **************** MWRelationalMapping implementation *****************

	public void addWrittenFieldsTo(Collection writtenFields) {
		for (Iterator stream = pathsToFields(); stream.hasNext(); ) {
			MWAggregatePathToColumn pathToField = (MWAggregatePathToColumn) stream.next();
			if (pathToField.getColumn() != null && pathToField.fieldIsWritten()) {
				writtenFields.add(pathToField.getColumn());
			}
		}
	}

	//************** Runtime Conversion *******************

	protected DatabaseMapping buildRuntimeMapping() {
		return new AggregateObjectMapping();
	}

	public DatabaseMapping runtimeMapping() {
		AggregateObjectMapping runtimeMapping = (AggregateObjectMapping) super.runtimeMapping();

		if (getReferenceDescriptor() != null) {
			runtimeMapping.setReferenceClassName(getReferenceDescriptor().getMWClass().getName());
		}

		runtimeMapping.setIsNullAllowed(allowsNull());

		convertPathsToFieldsToRuntime(runtimeMapping);
		
		return runtimeMapping;
	}

	private void convertPathsToFieldsToRuntime(AggregateObjectMapping runtimeMapping) {
		for (Iterator i = pathsToFields(); i.hasNext(); ) {
			MWAggregatePathToColumn pathToField = (MWAggregatePathToColumn) i.next();
			pathToField.adjustRuntimeMapping(runtimeMapping);
		}		
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAggregateMapping.class);

		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);

		XMLCompositeObjectMapping referenceDescriptorHandleMapping = new XMLCompositeObjectMapping();
		referenceDescriptorHandleMapping.setAttributeName("referenceDescriptorHandle");
		referenceDescriptorHandleMapping.setGetMethodName("getReferenceDescriptorHandleForTopLink");
		referenceDescriptorHandleMapping.setSetMethodName("setReferenceDescriptorHandleForTopLink");
		referenceDescriptorHandleMapping.setReferenceClass(MWDescriptorHandle.class);
		referenceDescriptorHandleMapping.setXPath("reference-descriptor-handle");
		descriptor.addMapping(referenceDescriptorHandleMapping);

		XMLDirectMapping allowsNullMapping = (XMLDirectMapping) descriptor.addDirectMapping("allowsNull", "allows-null/text()");
		allowsNullMapping.setNullValue(Boolean.FALSE);

		XMLCompositeCollectionMapping pathsToFieldsMapping = new XMLCompositeCollectionMapping();
		pathsToFieldsMapping.setAttributeName("pathsToFields");
		pathsToFieldsMapping.setGetMethodName("getPathsToFieldsForTopLink");
		pathsToFieldsMapping.setSetMethodName("setPathsToFieldsForTopLink");
		pathsToFieldsMapping.setReferenceClass(MWAggregatePathToColumn.class);
		pathsToFieldsMapping.setXPath("paths-to-fields/aggregate-path-to-field");
		descriptor.addMapping(pathsToFieldsMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWDescriptorHandle getReferenceDescriptorHandleForTopLink() {
		return (this.referenceDescriptorHandle.getDescriptor() == null) ? null : this.referenceDescriptorHandle;
	}
	private void setReferenceDescriptorHandleForTopLink(MWDescriptorHandle referenceDescriptorHandle) {
		NodeReferenceScrubber scrubber = this.buildReferenceDescriptorScrubber();
		this.referenceDescriptorHandle = ((referenceDescriptorHandle == null) ? new MWDescriptorHandle(this, scrubber) : referenceDescriptorHandle.setScrubber(scrubber));
	}

	//TODO should pathsToFields be sorted?
	private Collection getPathsToFieldsForTopLink() {
		return this.pathsToFields;
	}
	private void setPathsToFieldsForTopLink(Collection pathsToFields) {
		this.pathsToFields = pathsToFields;
	}

}
