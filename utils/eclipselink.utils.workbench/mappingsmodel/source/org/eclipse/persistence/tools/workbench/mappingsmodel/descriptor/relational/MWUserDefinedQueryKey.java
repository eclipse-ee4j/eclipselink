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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.Collections;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateRuntimeFieldNameGenerator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWUserDefinedQueryKey extends MWModel 
	implements MWQueryKey, MWQueryable, AggregateRuntimeFieldNameGenerator {

	private volatile String name;

	private MWColumnHandle columnHandle;
		public final static String COLUMN_PROPERTY = "column";


	// ********** Constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWUserDefinedQueryKey() {
		super();
	}

	MWUserDefinedQueryKey(String name, MWRelationalClassDescriptor parent, MWColumn column) {
		this(name, parent);
		this.columnHandle.setColumn(column);
	}

	MWUserDefinedQueryKey(String name, MWRelationalClassDescriptor parent) {
		super(parent);
		initialize(name);
	}


	// ********** Initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
	}

	private void initialize(String qkName) {
		checkName(qkName);
		this.name = qkName;
	}

	protected void checkName(String qkName) {
		if (qkName == null || qkName == "") {
			throw new IllegalArgumentException("MWUserDefinedQueryKey name cannot be null");
		}
		getDescriptor().checkQueryKeyName(qkName);
	}


	// ********** Containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.columnHandle);
	}

	private NodeReferenceScrubber buildColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWUserDefinedQueryKey.this.setColumn(null);
			}
			public String toString() {
				return "MWUserDefinedQueryKey.buildColumnScrubber()";
			}
		};
	}


	// ********** MWQueryable implementation **********

	public boolean allowsChildren() {
		return false;
	}

	public boolean allowsOuterJoin() {
		return allowsChildren();
	}

	public MWMappingDescriptor getParentDescriptor() {
		return getDescriptor();
	}

	public List subQueryableElements(Filter queryableFilter) {
		return Collections.EMPTY_LIST;
	}

	public MWQueryable subQueryableElementAt(int index, Filter queryableFilter) {
		return null;
	}

	public boolean isLeaf(Filter queryableFilter) {
		return true;
	}

	public String iconKey() {
		return "queryKey.userDefined";
	}

	public boolean usesAnyOf() {
		return false;
	}

	public boolean isTraversableForReadAllQueryOrderable() {
		return true;
	}

	public boolean isValidForReadAllQueryOrderable() {
		return true;
	}

	public boolean isTraversableForBatchReadAttribute() {
		return false;
	}

	public boolean isValidForBatchReadAttribute() {
		return false;
	}

	public boolean isTraversableForJoinedAttribute() {
		return false;
	}

	public boolean isValidForJoinedAttribute() {
		return false;
	}

	public boolean isValidForReportQueryAttribute() {
		return true;
	}

	public boolean isTraversableForReportQueryAttribute() {
		return true;
	}

	public boolean isTraversableForQueryExpression() {
		return true;
	}

	public boolean isValidForQueryExpression() {
		return true;
	}


	// ********** MWQueryKey implementation **********

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		checkName(name);
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.getProject().nodeRenamed(this);
		}
	}

	public boolean isAutoGenerated() {
		return false;
	}

	public MWRelationalClassDescriptor getDescriptor() {
		return (MWRelationalClassDescriptor) this.getParent();
	}

	public MWColumn getColumn() {
		return this.columnHandle.getColumn();
	}

	public void setColumn(MWColumn column) {
		Object old = this.columnHandle.getColumn();
		this.columnHandle.setColumn(column);
		this.firePropertyChanged(COLUMN_PROPERTY, old, column);
	}


	//************* AggregateRuntimeFieldNameGenerator implementation ************

	public String fieldNameForRuntime() {
		return "QUERY_KEY " + getName();
	}

	public AggregateFieldDescription fullFieldDescription() {
		return new AggregateFieldDescription() {
			public String getMessageKey() {
				return "AGGREGATE_FIELD_DESCRIPTION_FOR_USER_DEFINED_QUERY_KEY";
			}

			public Object[] getMessageArguments() {
				return new Object[] {getName()};
			}
		};
	}

	public boolean fieldIsWritten() {
		return false;
	}

	public MWDescriptor owningDescriptor() {
		return (MWDescriptor) this.getParent();
	}


	//*********** Problem Handling *********

	/**
	 * Check for any problems and add them to the specified collection.
	 */
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkColumns(newProblems);
	}

	private void checkColumns(List currentProblems) {
		if (((MWRelationalDescriptor) this.getParentDescriptor()).isAggregateDescriptor()) {
			return;
		}
		if (this.getColumn() == null) {
			currentProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_QUERY_KEY_NO_COLUMN_SPECIFIED, this.getName()));
		}
		else if ( ! CollectionTools.contains(((MWTableDescriptor) this.getParentDescriptor()).allAssociatedColumns(), this.getColumn())) {
			currentProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_QUERY_KEY_INVALID_COLUMN, this.getName()));
		}
	}

	// ********** Display methods **********

	public String displayString() {
		return this.getName();
	}

	public void toString(StringBuffer sb) {
		sb.append("field=");
		if (getColumn() == null) {
			sb.append("null");
		}
		else {
			getColumn().toString(sb);
		}
	}


	// ********** TopLink methods *********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWUserDefinedQueryKey.class);

		descriptor.addDirectMapping("name", "name/text()");

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
}
