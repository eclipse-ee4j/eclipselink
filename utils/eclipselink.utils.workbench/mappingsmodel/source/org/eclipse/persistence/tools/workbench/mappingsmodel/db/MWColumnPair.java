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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

/**
 * Simple class that pairs a source column and a target column
 * for a reference.
 */
public final class MWColumnPair extends MWModel {

	private MWColumnHandle sourceColumnHandle;
		public static final String SOURCE_COLUMN_PROPERTY = "sourceColumn";

	private MWColumnHandle targetColumnHandle;
		public static final String TARGET_COLUMN_PROPERTY = "targetColumn";
        
	
	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWColumnPair() {
		super();
	}

	MWColumnPair(MWReference parent, MWColumn sourceColumn, MWColumn targetColumn) {
		super(parent);
		this.sourceColumnHandle.setColumn(sourceColumn);
		this.targetColumnHandle.setColumn(targetColumn);
	}
	
	
	// ********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.sourceColumnHandle = new MWColumnHandle(this, this.buildSourceColumnScrubber());
		this.targetColumnHandle = new MWColumnHandle(this, this.buildTargetColumnScrubber());
	}


	// ********** accessors **********
	
	public MWReference getReference() {
		return (MWReference) this.getParent();
	}
	
	public MWColumn getSourceColumn() {
		return this.sourceColumnHandle.getColumn();
	}	

	public void setSourceColumn(MWColumn sourceColumn) {
		if ((sourceColumn != null) && (sourceColumn.getTable() != this.getReference().getSourceTable())) {
			throw new IllegalArgumentException();
		}
		Object old = this.sourceColumnHandle.getColumn();
		this.sourceColumnHandle.setColumn(sourceColumn);
		this.firePropertyChanged(SOURCE_COLUMN_PROPERTY, old, sourceColumn);
	}

	public MWColumn getTargetColumn() {
		return this.targetColumnHandle.getColumn();
	}
	
	public void setTargetColumn(MWColumn targetColumn) {
		if ((targetColumn != null) && (targetColumn.getTable() != this.getReference().getTargetTable())) {
			throw new IllegalArgumentException();
		}
		Object old = this.targetColumnHandle.getColumn();
		this.targetColumnHandle.setColumn(targetColumn);
		this.firePropertyChanged(TARGET_COLUMN_PROPERTY, old, targetColumn);
	}


	// ********** queries **********

	/**
	 * the pair's name is calculated from the two column names
	 */
	public String getName() {
		StringBuffer sb = new StringBuffer(200);
		this.appendColumn(sb, this.getSourceColumn());
		sb.append('=');
		this.appendColumn(sb, this.getTargetColumn());
		return sb.toString();
	}

	private void appendColumn(StringBuffer sb, MWColumn column) {
		if (column != null) {
			sb.append(column.qualifiedName());
		}
	}

	/**
	 * return whether the column pair matches up the specified columns
	 */
	boolean pairs(MWColumn sourceColumn, MWColumn targetColumn) {
		return (this.getSourceColumn() == sourceColumn) &&
			(this.getTargetColumn() == targetColumn);
	}

	public MWTable sourceTable() {
		return this.tableFrom(this.getSourceColumn());
	}

	public MWTable targetTable() {
		return this.tableFrom(this.getTargetColumn());
	}

	private MWTable tableFrom(MWColumn column) {
		return (column == null) ? null : column.getTable();
	}


	// ********** behavior **********

	public void setSourceAndTargetColumns(MWColumn sourceColumn, MWColumn targetColumn) {
		this.setSourceColumn(sourceColumn);
		this.setTargetColumn(targetColumn);
	}
	

	// ********** problems **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if ((this.getSourceColumn() == null) || (this.getTargetColumn() == null)) {
			currentProblems.add(this.buildProblem(ProblemConstants.INCOMPLETE_COLUMN_PAIR, this.getReference().getName()));
		}
	}

	
	// ********** containment hierarchy **********
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.sourceColumnHandle);
		children.add(this.targetColumnHandle);
	}

	private NodeReferenceScrubber buildSourceColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWColumnPair.this.setSourceColumn(null);
			}
			public String toString() {
				return "MWColumnPair.buildSourceColumnScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildTargetColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWColumnPair.this.setTargetColumn(null);
			}
			public String toString() {
				return "MWColumnPair.buildTargetColumnScrubber()";
			}
		};
	}


	// ********** importing/refreshing **********

	/**
	 * return whether the column pair corresponds to the specified
	 * "external" column pair;
	 * if the column pair matches an "external" column pair we keep,
	 * otherwise, we remove it from the reference
	 */
	boolean matches(ExternalForeignKeyColumnPair externalPair) {
		return (this.getSourceColumn() == this.sourceColumn(externalPair)) &&
				(this.getTargetColumn() == this.targetColumn(externalPair));
	}

	/**
	 * return the column in the source table with the same name as the
	 * "external" column pair's source column
	 */
	private MWColumn sourceColumn(ExternalForeignKeyColumnPair externalPair) {
		return this.getReference().sourceColumn(externalPair);
	}

	/**
	 * return the column in the target table with the same name as the
	 * "external" column pair's target column
	 */
	private MWColumn targetColumn(ExternalForeignKeyColumnPair externalPair) {
		return this.getReference().targetColumn(externalPair);
	}


	// ********** runtime conversion **********

	void configureRuntimeConstraint(ForeignKeyConstraint fkc) {
		if ((this.getSourceColumn() == null) || (this.getTargetColumn() == null)) {
			return;
		}
		fkc.addSourceField(this.getSourceColumn().getName());
		fkc.addTargetField(this.getTargetColumn().getName());
	}


	// ********** displaying and printing **********

	public String displayString() {
		return this.getName();
	}

	public void toString(StringBuffer sb) {
		super.toString(sb);
		this.printColumnNameOn(this.getSourceColumn(), sb);
		sb.append("=>");
		this.printColumnNameOn(this.getTargetColumn(), sb);
	}
	
	private void printColumnNameOn(MWColumn column, StringBuffer sb) {
		sb.append((column == null) ? "null" : column.getName());
	}



	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWColumnPair.class);

		XMLCompositeObjectMapping sourceColumnHandleMapping = new XMLCompositeObjectMapping();
		sourceColumnHandleMapping.setAttributeName("sourceColumnHandle");
		sourceColumnHandleMapping.setGetMethodName("getSourceColumnHandleForTopLink");
		sourceColumnHandleMapping.setSetMethodName("setSourceColumnHandleForTopLink");
		sourceColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		sourceColumnHandleMapping.setXPath("source-column-handle");
		descriptor.addMapping(sourceColumnHandleMapping);

		XMLCompositeObjectMapping targetColumnHandleMapping = new XMLCompositeObjectMapping();
		targetColumnHandleMapping.setAttributeName("targetColumnHandle");
		targetColumnHandleMapping.setGetMethodName("getTargetColumnHandleForTopLink");
		targetColumnHandleMapping.setSetMethodName("setTargetColumnHandleForTopLink");
		targetColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		targetColumnHandleMapping.setXPath("target-column-handle");
		descriptor.addMapping(targetColumnHandleMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWColumnHandle getSourceColumnHandleForTopLink() {
		return (this.sourceColumnHandle.getColumn() == null) ? null : this.sourceColumnHandle;
	}
	private void setSourceColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildSourceColumnScrubber();
		this.sourceColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWColumnHandle getTargetColumnHandleForTopLink() {
		return (this.targetColumnHandle.getColumn() == null) ? null : this.targetColumnHandle;
	}
	private void setTargetColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildTargetColumnScrubber();
		this.targetColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
}
