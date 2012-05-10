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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.ColumnStringHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateRuntimeFieldNameGenerator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.SimplePartialStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringMatcher.StringHolderScore;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * Class indicator that uses a relational database column
 */
public final class MWRelationalClassIndicatorFieldPolicy extends MWClassIndicatorFieldPolicy 
	implements AggregateRuntimeFieldNameGenerator
{

	private MWColumnHandle columnHandle;
		public final static String FIELD_PROPERTY = "field";


	 // ********** constructors **********

	/**
	 * Toplink use only
	 */
	private MWRelationalClassIndicatorFieldPolicy() {
		super();
	}

	public MWRelationalClassIndicatorFieldPolicy(MWClassIndicatorPolicy.Parent parent) {
		this(parent, NullIterator.instance());
	}

	public MWRelationalClassIndicatorFieldPolicy(MWClassIndicatorPolicy.Parent parent, Iterator descriptorsAvailableForIndication) {
		super(parent);
		setDescriptorsAvailableForIndicatorDictionary(descriptorsAvailableForIndication);
	}


	//	********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
	}


	//	********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.columnHandle);
	}

	private NodeReferenceScrubber buildColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalClassIndicatorFieldPolicy.this.setColumn(null);
			}
			public String toString() {
				return "MWRelationalClassIndicatorFieldPolicy.buildColumnScrubber()";
			}
		};
	}


	//	********** Accessors **********

	public MWDataField getField() {
		return this.getColumn();
	}

	public void setField(MWDataField field) {
		this.setColumn((MWColumn) field);
	}

	public MWColumn getColumn() {
		return this.columnHandle.getColumn();
	}

	public void setColumn(MWColumn column) {
		Object old = this.columnHandle.getColumn();
		this.columnHandle.setColumn(column);
		this.firePropertyChanged(FIELD_PROPERTY, old, column);
	}

	protected boolean fieldSpecified() {
		return ((MWRelationalDescriptor) this.getContainingDescriptor()).isAggregateDescriptor() || getField() != null;
	}


	//	********** Aggregate Support **********

	public String fieldNameForRuntime() {
		return "CLASS_INDICATOR_FIELD";
	}

	public AggregateFieldDescription fullFieldDescription() {
		return new AggregateFieldDescription() {
			public String getMessageKey() {
				if (getParent() instanceof MWDescriptorInheritancePolicy) {
					return "AGGREGATE_FIELD_DESCRIPTION_FOR_CLASS_INDICATOR_FIELD_FOR_INHERITANCE";
				}
				return "AGGREGATE_FIELD_DESCRIPTION_FOR_CLASS_INDICATOR_FIELD";
			}

			public Object[] getMessageArguments() {
				return new Object[] {};
			}
		};
	}

	public void parentDescriptorMorphedToAggregate() {
		setField(null);
	}

	public boolean fieldIsWritten() {
		return true;
	}

	public MWDescriptor owningDescriptor() {
		return this.getContainingDescriptor();
	}

	public void addToAggregateFieldNameGenerators(Collection generators) {
		generators.add(this);
	}


	// ************* Automap Support *************

	public void automap() {
		super.automap();
		this.automapColumn();
	}

	/**
	 * look for a column named something like "TYPE" or "CLASS".
	 */
	private void automapColumn() {
		MWRelationalDescriptor descriptor = (MWRelationalDescriptor) this.getContainingDescriptor();
		if ( ! descriptor.isAggregateDescriptor()) {
			return;
		}

		ColumnStringHolder[] columnStringHolders = this.allUnmappedColumnStringHolders((MWTableDescriptor) descriptor);
		if (columnStringHolders.length == 0) {
			return;
		}
		StringHolderScore shs1 = this.match("type", columnStringHolders);
		StringHolderScore shs2 = this.match("class", columnStringHolders);
		MWColumn column1 = ((ColumnStringHolder) shs1.getStringHolder()).getColumn();
		MWColumn column2 = ((ColumnStringHolder) shs2.getStringHolder()).getColumn();
		double score1 = shs1.getScore();
		double score2 = shs2.getScore();

		if (Math.max(score1, score2) > 0.50) {		// 0.50 ???
			this.setField((score1 >= score2) ? column1 : column2);
		}
	}

	private ColumnStringHolder[] allUnmappedColumnStringHolders(MWTableDescriptor descriptor) {
		Collection unmappedColumns = new HashSet();
		for (Iterator stream = descriptor.associatedTables(); stream.hasNext(); ) {
			CollectionTools.addAll(unmappedColumns, ((MWTable) stream.next()).columns());
		}

		Collection mappedColumns = new HashSet();
		for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
			MWMapping mapping = (MWMapping) mappings.next();
			mapping.addWrittenFieldsTo(mappedColumns);
		}
		unmappedColumns.removeAll(mappedColumns);
		return ColumnStringHolder.buildHolders(unmappedColumns);
	}

	private StringHolderScore match(String string, ColumnStringHolder[] columnStringHolders) {
		return COLUMN_NAME_MATCHER.match(string, columnStringHolders);
	}

	private static final PartialStringMatcher COLUMN_NAME_MATCHER = new SimplePartialStringMatcher(PartialStringComparator.DEFAULT_COMPARATOR);

	// ********* Problems ***************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		addClassIndicatorFieldNotSpecifiedProblemTo(newProblems);
	}
	
	public void addClassIndicatorFieldNotSpecifiedProblemTo(List newProblems) {
		((Parent) getParent()).addClassIndicatorFieldNotSpecifiedProblemTo(newProblems);
	}
	

	//	********** Runtime Conversion **********

	//runtime conversion for when this is used in MWRelationalInheritancePolicy
	public void adjustRuntimeInheritancePolicy(InheritancePolicy runtimeInheritancePolicy) {
		super.adjustRuntimeInheritancePolicy(runtimeInheritancePolicy);
		if (getColumn() != null) {
			runtimeInheritancePolicy.setClassIndicatorFieldName(getColumn().qualifiedName());
		} else {
			runtimeInheritancePolicy.setClassIndicatorFieldName(fieldNameForRuntime());
		}
	}

	//runtime conversion for when this is used in MWVariableOneToOneMapping
	//this make me like this sharing of the relationaClassIndicatorFieldPolicy less and less :(  ~kfm
	public void adjustRuntimeMapping(VariableOneToOneMapping runtimeMapping) {
		String classIndicatorFieldName = null;

		if (getField() != null) {
			classIndicatorFieldName = getColumn().qualifiedName();
		}
		else if (((MWVariableOneToOneMapping) getParent()).parentDescriptorIsAggregate()) {
			classIndicatorFieldName = runtimeMapping.getAttributeName() + "->" + fieldNameForRuntime();
		}

		if (classIndicatorFieldName != null && ! classIndicatorFieldName.equals("")) {
			runtimeMapping.setTypeFieldName(classIndicatorFieldName);

			for (Iterator indicatorValues = includedClassIndicatorValues(); indicatorValues.hasNext(); ) {
				Object value = ((MWClassIndicatorValue) indicatorValues.next()).getIndicatorValue();
				runtimeMapping.addClassNameIndicator(getDescriptorForIndicator(value).getMWClass().getName(), value);
			}
		} else
			runtimeMapping.setTypeFieldName("");
	}


	//	********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalClassIndicatorFieldPolicy.class);
		descriptor.getInheritancePolicy().setParentClass(MWClassIndicatorFieldPolicy.class);

		XMLCompositeObjectMapping columnHandleMapping = new XMLCompositeObjectMapping();
		columnHandleMapping.setAttributeName("columnHandle");
		columnHandleMapping.setGetMethodName("getColumnHandleForTopLink");
		columnHandleMapping.setSetMethodName("setColumnHandleForTopLink");
		columnHandleMapping.setReferenceClass(MWColumnHandle.class);
		columnHandleMapping.setXPath("column-handle");
		descriptor.addMapping(columnHandleMapping);

		return descriptor;
	}

	private MWColumnHandle getColumnHandleForTopLink() {
		return (this.columnHandle.getColumn() == null) ? null : this.columnHandle;
	}
	private void setColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildColumnScrubber();
		this.columnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	public static interface Parent {
		void addClassIndicatorFieldNotSpecifiedProblemTo(List newProblems);
	}
}
