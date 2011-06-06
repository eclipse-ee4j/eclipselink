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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;

public final class MWSequencingPolicy extends MWModel {
	
	private volatile String sequencingType;
		public static final String SEQUENCING_TYPE_PROPERTY = "SequencingType";

		public static final String DEFAULT_SEQUENCING = "Use default sequencing";
		public static final String NATIVE_SEQUENCING = "Use native sequencing";
		public static final String SEQUENCE_TABLE = "Use sequencing table";


	private volatile int preallocationSize;
		public static final String PREALLOCATION_SIZE_PROPERTY = "preallocationSize";
	
	private MWTableHandle tableHandle;
		public static final String SEQUENCING_TABLE_PROPERTY = "table";
		
	private MWColumnHandle nameColumnHandle;
		public static final String NAME_COLUMN_PROPERTY = "nameColumn";
		
	private MWColumnHandle counterColumnHandle;
		public static final String COUNTER_COLUMN_PROPERTY = "counterColumn";
	

	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWSequencingPolicy() {
		super();
	}
	
	MWSequencingPolicy(MWRelationalProject project) {
		super(project);
	}
	
	// ********** initialization **********
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent){
		super.initialize(parent);
		this.tableHandle = new MWTableHandle(this, this.buildTableScrubber());
		this.nameColumnHandle = new MWColumnHandle(this, this.buildNameColumnScrubber());
		this.counterColumnHandle = new MWColumnHandle(this, this.buildCounterColumnScrubber());
		this.preallocationSize = 50;
		this.sequencingType = DEFAULT_SEQUENCING;
	}

	
	// *************** Accessors **************
	
	public MWTable getTable() {
		return this.tableHandle.getTable();
	}
	
	public void setTable(MWTable newTable) {
		MWTable oldTable = this.tableHandle.getTable();
		this.tableHandle.setTable(newTable);
		firePropertyChanged(SEQUENCING_TABLE_PROPERTY, oldTable, this.tableHandle.getTable());

		setNameColumn(null);
		setCounterColumn(null);
	}

	public MWColumn getCounterColumn() {
		return this.counterColumnHandle.getColumn();
	}
	
	public void setCounterColumn(MWColumn counterColumn) {
		MWColumn old = this.counterColumnHandle.getColumn();
		this.counterColumnHandle.setColumn(counterColumn);
		firePropertyChanged(COUNTER_COLUMN_PROPERTY, old, counterColumn);
	}

	public MWColumn getNameColumn() {
	    return this.nameColumnHandle.getColumn();
	}
	
	public void setNameColumn(MWColumn nameColumn) {
		MWColumn old = this.nameColumnHandle.getColumn();
		this.nameColumnHandle.setColumn(nameColumn);
		firePropertyChanged(NAME_COLUMN_PROPERTY, old, nameColumn);
	}
	
	public int getPreallocationSize() {
		return this.preallocationSize;
	}
	
	public void setPreallocationSize(int newPreallocationSize) {
		int oldSize = this.preallocationSize;
		this.preallocationSize = newPreallocationSize;
		firePropertyChanged(PREALLOCATION_SIZE_PROPERTY, oldSize, this.preallocationSize);
	}
	
	public String getSequencingType() {
		return this.sequencingType;
	}
		
	public void setSequencingType(String newSequencingType) {
		this.checkSequencingType(this.sequencingType);	
		String oldType = this.sequencingType;
		this.sequencingType = newSequencingType;
		firePropertyChanged(SEQUENCING_TYPE_PROPERTY, oldType, this.sequencingType);
	}
	
	private void checkSequencingType(String sequencingType) {
		if(sequencingType != DEFAULT_SEQUENCING && 
			sequencingType != NATIVE_SEQUENCING &&
			sequencingType != SEQUENCE_TABLE) 
		{
			throw new IllegalArgumentException("MWSequencingPolicy: sequencingType must be one of MWSequencingPolicy.DEFAULT_SEQUENCING, MWSequencingPolicy.NATIVE_SEQUENCING or MWSequencingPolicy.SEQUENCE_TABLE");
		}
	}
	
		
	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.nameColumnHandle);
		children.add(this.counterColumnHandle);
		children.add(this.tableHandle);
	}

	private NodeReferenceScrubber buildNameColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWSequencingPolicy.this.setNameColumn(null);
			}
			public String toString() {
				return "MWSequencingPolicy.buildNameColumnScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildCounterColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWSequencingPolicy.this.setCounterColumn(null);
			}
			public String toString() {
				return "MWSequencingPolicy.buildCounterColumnScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildTableScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWSequencingPolicy.this.setTable(null);
			}
			public String toString() {
				return "MWSequencingPolicy.buildTableScrubber()";
			}
		};
	}


	// ********** problems **********

	/**
	 * Check for any problems and add them to the specified collection.
	 */
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if (this.sequencingType == SEQUENCE_TABLE) {
			this.checkSequenceTable(currentProblems);
		}
	}

	private void checkSequenceTable(List currentProblems) {
		if (this.getCounterColumn() == null) {
			currentProblems.add(this.buildProblem(ProblemConstants.PROJECT_NO_SEQUENCE_COUNTER_FIELD_SPECIFIED));
		}
		if (this.getNameColumn() == null) {
			currentProblems.add(this.buildProblem(ProblemConstants.PROJECT_NO_SEQUENCE_NAME_FIELD_SPECIFIED));
		}
	}


	// ********** runtime conversion **********

	void adjustRuntimeLogin(DatabaseLogin login) {
		if (this.getSequencingType().equals(MWSequencingPolicy.NATIVE_SEQUENCING)) {
			login.useNativeSequencing();
		} else if (this.getSequencingType().equals(MWSequencingPolicy.SEQUENCE_TABLE))  {
			if (this.getTable() != null) {
				((TableSequence) login.getDefaultSequence()).setTableName(this.getTable().getName());
			}
			if (this.getNameColumn() != null) {
				((TableSequence) login.getDefaultSequence()).setNameFieldName(this.getNameColumn().getName());
			}
			if (this.getCounterColumn() != null) {
				((TableSequence) login.getDefaultSequence()).setCounterFieldName(this.getCounterColumn().getName());
			}
		}
		login.getDefaultSequence().setPreallocationSize(this.getPreallocationSize());
	}	
	
	
	// ********** Display methods  **********
	
	public void toString(StringBuffer sb) {
		sb.append(getSequencingType());
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWSequencingPolicy.class);

		descriptor.addDirectMapping("preallocationSize", "preallocation-size/text()");

		// use an object type mapping so we can preserve object identity
		ObjectTypeConverter sequencingTypeConverter = new ObjectTypeConverter();
		sequencingTypeConverter.addConversionValue(
				MWSequencingPolicy.DEFAULT_SEQUENCING,
				MWSequencingPolicy.DEFAULT_SEQUENCING);
		sequencingTypeConverter.addConversionValue(
				MWSequencingPolicy.NATIVE_SEQUENCING,
				MWSequencingPolicy.NATIVE_SEQUENCING);
		sequencingTypeConverter.addConversionValue(
				MWSequencingPolicy.SEQUENCE_TABLE,
				MWSequencingPolicy.SEQUENCE_TABLE);

		XMLDirectMapping sequencingTypeMapping = new XMLDirectMapping();
		sequencingTypeMapping.setAttributeName("sequencingType");
		sequencingTypeMapping.setXPath("sequencing-type/text()");
		sequencingTypeMapping.setNullValue(MWSequencingPolicy.DEFAULT_SEQUENCING);
		sequencingTypeMapping.setConverter(sequencingTypeConverter);
		descriptor.addMapping(sequencingTypeMapping);

		XMLCompositeObjectMapping nameColumnHandleMapping = new XMLCompositeObjectMapping();
		nameColumnHandleMapping.setAttributeName("nameColumnHandle");
		nameColumnHandleMapping.setSetMethodName("setNameColumnHandleForTopLink");
		nameColumnHandleMapping.setGetMethodName("getNameColumnHandleForTopLink");
		nameColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		nameColumnHandleMapping.setXPath("name-column-handle");
		descriptor.addMapping(nameColumnHandleMapping);

		XMLCompositeObjectMapping counterColumnHandleMapping = new XMLCompositeObjectMapping();
		counterColumnHandleMapping.setAttributeName("counterColumnHandle");
		counterColumnHandleMapping.setSetMethodName("setCounterColumnHandleForTopLink");
		counterColumnHandleMapping.setGetMethodName("getCounterColumnHandleForTopLink");
		counterColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		counterColumnHandleMapping.setXPath("counter-column-handle");
		descriptor.addMapping(counterColumnHandleMapping);

		XMLCompositeObjectMapping tableMapping = new XMLCompositeObjectMapping();
		tableMapping.setAttributeName("tableHandle");
		tableMapping.setSetMethodName("setTableHandleForTopLink");
		tableMapping.setGetMethodName("getTableHandleForTopLink");
		tableMapping.setReferenceClass(MWTableHandle.class);
		tableMapping.setXPath("table-handle");
		descriptor.addMapping(tableMapping);

		return descriptor;
	}
	
	/**
	 * check for null
	 */
	private MWColumnHandle getNameColumnHandleForTopLink() {
		return (this.nameColumnHandle.getColumn() == null) ? null : this.nameColumnHandle;
	}
	private void setNameColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildNameColumnScrubber();
		this.nameColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWColumnHandle getCounterColumnHandleForTopLink() {
		return (this.counterColumnHandle.getColumn() == null) ? null : this.counterColumnHandle;
	}
	private void setCounterColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildCounterColumnScrubber();
		this.counterColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWTableHandle getTableHandleForTopLink() {
		return (this.tableHandle.getTable() == null) ? null : this.tableHandle;
	}
	private void setTableHandleForTopLink(MWTableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildTableScrubber();
		this.tableHandle = ((handle == null) ? new MWTableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
