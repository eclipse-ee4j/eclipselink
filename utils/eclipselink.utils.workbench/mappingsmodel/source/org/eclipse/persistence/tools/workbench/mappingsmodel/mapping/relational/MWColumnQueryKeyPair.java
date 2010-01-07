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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Record;

public final class MWColumnQueryKeyPair extends MWModel
	implements AggregateRuntimeFieldNameGenerator
{
	private MWColumnHandle columnHandle;
		public final static String COLUMN_PROPERTY = "column";
	
	private volatile String queryKeyName;
		public final static String QUERY_KEY_NAME_PROPERTY = "queryKeyName";


	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWColumnQueryKeyPair() {
		super();
	}
	
	MWColumnQueryKeyPair(MWVariableOneToOneMapping parent, MWColumn source, String targetQueryKeyName) {
		super(parent);
		this.columnHandle.setColumn(source);
		this.queryKeyName = targetQueryKeyName;
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
	}

	
	//	********** containment hierarchy **********
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.columnHandle);
	}

	public MWRelationalDescriptor getParentDescriptor() {
		return (MWRelationalDescriptor) getMapping().getParentDescriptor();
	}
	
	public MWVariableOneToOneMapping getMapping() {
		return (MWVariableOneToOneMapping) getParent();	
	}

	private NodeReferenceScrubber buildColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWColumnQueryKeyPair.this.setColumn(null);
			}
			public String toString() {
				return "MWColumnQueryKeyPair.buildColumnScrubber()";
			}
		};
	}


	//	********** accessors **********
	
	public MWColumn getColumn() {
		return this.columnHandle.getColumn();
	}	

	public void setColumn(MWColumn column) {
		Object old = this.columnHandle.getColumn();
		this.columnHandle.setColumn(column);
		this.firePropertyChanged(COLUMN_PROPERTY, old, column);
	}

	public String getQueryKeyName() {
		return this.queryKeyName;
	}
	
	public void setQueryKeyName(String newQueryKeyName) {
		String oldQueryKeyNameName = this.queryKeyName;
		this.queryKeyName = newQueryKeyName;
		firePropertyChanged(QUERY_KEY_NAME_PROPERTY, oldQueryKeyNameName, newQueryKeyName);
	}
	
	
	//	********** Aggregate Support **********
	
	public String fieldNameForRuntime() {
		return "QUERY_KEY " + getQueryKeyName();
	}
	
	public AggregateFieldDescription fullFieldDescription() {
		return new AggregateFieldDescription() {
			public String getMessageKey() {
				return "AGGREGATE_FIELD_DESCRIPTION_FOR_FIELD_QUERY_KEY_ASSOCIATION";
			}
			
			public Object[] getMessageArguments() {
				return new Object[] {getQueryKeyName()};
			}
		};
	}	
	
	public boolean fieldIsWritten() {
		return true;
	}

	public MWDescriptor owningDescriptor() {
		throw new UnsupportedOperationException();
	}


	//**************** Runtime Conversion ************
	
	void adjustRuntimeMapping(VariableOneToOneMapping runtimeMapping) {			
		if (getColumn() != null) {
		   runtimeMapping.addForeignQueryKeyName(getColumn().qualifiedName(), getQueryKeyName());
		}
			
		else if (getParentDescriptor().isAggregateDescriptor()) {
			runtimeMapping.addForeignQueryKeyName(runtimeMapping.getAttributeName() + "->" + fieldNameForRuntime(), getQueryKeyName());
		}
	}
	
	
	// ********** displaying and printing **********

	public void toString(StringBuffer sb) {
		sb.append(this.getColumn() == null ? "null" : getColumn().getName());
		sb.append("=>");
		sb.append(this.getQueryKeyName());
	}
	
	
	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWColumnQueryKeyPair.class);

		XMLCompositeObjectMapping columnHandleMapping = new XMLCompositeObjectMapping();
		columnHandleMapping.setAttributeName("columnHandle");
		columnHandleMapping.setGetMethodName("getColumnHandleForTopLink");
		columnHandleMapping.setSetMethodName("setColumnHandleForTopLink");
		columnHandleMapping.setReferenceClass(MWColumnHandle.class);
		columnHandleMapping.setXPath("column-handle");
		descriptor.addMapping(columnHandleMapping);

		descriptor.addDirectMapping("queryKeyName", "query-key-name/text()");

		return descriptor;
	}
	
	/**
	 * check for null
	 */
	private MWColumnHandle getColumnHandleForTopLink() {
		return (this.columnHandle.getColumn() == null) ? null : this.columnHandle;
	}
	private void setColumnHandleForTopLink(MWColumnHandle columnHandle) {
		NodeReferenceScrubber scrubber = this.buildColumnScrubber();
		this.columnHandle = ((columnHandle == null) ? new MWColumnHandle(this, scrubber) : columnHandle.setScrubber(scrubber));
	}

}
