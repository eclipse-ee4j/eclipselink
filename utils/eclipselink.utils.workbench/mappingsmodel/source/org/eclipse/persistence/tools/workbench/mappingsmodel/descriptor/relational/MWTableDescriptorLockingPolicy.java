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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.AllFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ChangedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.FieldsLockingPolicy;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWTableDescriptorLockingPolicy extends MWDescriptorLockingPolicy {
 
	private MWColumnHandle versionLockingColumnHandle;
        public final static String VERSION_LOCKING_COLUMN_PROPERTY = "versionLockingColumn";
	
	private volatile String optimisticLockingType;
		public final static String OPTIMISTIC_LOCKING_TYPE_PROPERTY = "optimisticLockingType";	
		public final static String OPTIMISTIC_COLUMNS_LOCKING_TYPE = "Columns Locking";
		public final static String OPTIMISTIC_VERSION_LOCKING_TYPE = "Version Locking";
		public final static String DEFAULT_OPTIMISTIC_LOCKING_TYPE = OPTIMISTIC_VERSION_LOCKING_TYPE;
	
	private volatile String optimisticColumnsLockingType;
		public final static String OPTIMISTIC_COLUMNS_LOCKING_TYPE_PROPERTY = "optimisticColumnsLockingType";
		public final static String OPTIMISTIC_COLUMNS_ALL_COLUMNS = "All Columns";
		public final static String OPTIMISTIC_COLUMNS_CHANGED_COLUMNS = "Changed Columns";
		public final static String OPTIMISTIC_COLUMNS_SELECTED_COLUMNS = "Selected Columns";
		public final static String DEFAULT_OPTIMISTIC_COLUMNS_LOCKING_TYPE = OPTIMISTIC_COLUMNS_ALL_COLUMNS;
	
	private Collection columnLockColumnHandles;
		public final static String COLUMN_LOCK_COLUMNS_COLLECTION = "columnLockColumns";
		private NodeReferenceScrubber columnLockColumnScrubber;


	// ********** constructors **********

	protected MWTableDescriptorLockingPolicy() {
		super();
	}
	
	public MWTableDescriptorLockingPolicy(MWRelationalTransactionalPolicy descriptor) {
		super(descriptor);
	}
	

	// ********** initialization **********

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.versionLockingColumnHandle = new MWColumnHandle(this, this.buildVersionLockingColumnScrubber());
		this.columnLockColumnHandles = new Vector();
	}

	
	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.versionLockingColumnHandle);
		synchronized (this.columnLockColumnHandles) { children.addAll(this.columnLockColumnHandles); }
	}
	
	private NodeReferenceScrubber buildVersionLockingColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWTableDescriptorLockingPolicy.this.setVersionLockingColumn(null);
			}
			public String toString() {
				return "MWTableDescriptorLockingPolicy.buildVersionLockingColumnScrubber()";
			}
		};
	}

	private NodeReferenceScrubber columnLockColumnScrubber() {
		if (this.columnLockColumnScrubber == null) {
			this.columnLockColumnScrubber = this.buildColumnLockColumnScrubber();
		}
		return this.columnLockColumnScrubber;
	}

	private NodeReferenceScrubber buildColumnLockColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWTableDescriptorLockingPolicy.this.removeColumnLockColumnHandle((MWColumnHandle) handle);
			}
			public String toString() {
				return "MWTableDescriptorLockingPolicy.buildColumnLockColumnScrubber()";
			}
		};
	}

	
	// ********** accessors **********

	public MWTableDescriptor getOwningTableDescriptor() {
		return (MWTableDescriptor) getOwningDescriptor();
	}
	
	public MWColumn getVersionLockingColumn() {
		return this.versionLockingColumnHandle.getColumn();
	}

	public void setVersionLockingColumn(MWColumn column) {
        if ((column != null) && (this.getLockingType() != OPTIMISTIC_LOCKING)) {
            throw new IllegalStateException("LockingType must be Optimistic Locking");
        }
		MWColumn old = this.versionLockingColumnHandle.getColumn();
		this.versionLockingColumnHandle.setColumn(column);
		this.firePropertyChanged(VERSION_LOCKING_COLUMN_PROPERTY, old, column);
	}
	
	public MWDataField getVersionLockField() {
		return this.getVersionLockingColumn();
	}

	public void setVersionLockField(MWDataField lockField) {
		this.setVersionLockingColumn((MWColumn) lockField);
	}


	// ***** columnLockColumns
	private Iterator columnLockColumnHandles() {
		return new CloneIterator(this.columnLockColumnHandles) {
			protected void remove(Object current) {
				MWTableDescriptorLockingPolicy.this.removeColumnLockColumnHandle((MWColumnHandle) current);
			}
		};
	}

	void removeColumnLockColumnHandle(MWColumnHandle handle) {
		this.columnLockColumnHandles.remove(handle);
		this.fireItemRemoved(COLUMN_LOCK_COLUMNS_COLLECTION, handle.getColumn());
	}

	public Iterator columnLockColumns() {
		return new TransformationIterator(this.columnLockColumnHandles()) {
			protected Object transform(Object next) {
				return ((MWColumnHandle) next).getColumn();
			}
		};
	}

	public int columnLockColumnsSize() {
		return this.columnLockColumnHandles.size();
	}

	public void addColumnLockColumn(MWColumn column) {
		this.columnLockColumnHandles.add(new MWColumnHandle(this, column, this.columnLockColumnScrubber()));
		this.fireItemAdded(COLUMN_LOCK_COLUMNS_COLLECTION, column);
	}
	
	public void removeColumnLockColumn(MWColumn column) {
		for (Iterator stream = this.columnLockColumns(); stream.hasNext(); ) {
			if (stream.next() == column) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(column.toString());
	}
	
	private void clearColumnLockColumns() {
		for (Iterator stream = this.columnLockColumnHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}
	
	public Iterator columnLockColumnsNames() {
		return new TransformationIterator(this.columnLockColumns()) {
			protected Object transform(Object next) {
				return ((MWColumn) next).qualifiedName();
			}
		};
	}
		
    public void setLockingType(String newLockingType) {
        String oldLockingType = getLockingType();
        super.setLockingType(newLockingType);
        if (attributeValueHasChanged(newLockingType, oldLockingType)) {
            if (newLockingType != OPTIMISTIC_LOCKING) {
                setOptimisticLockingType(null);
            }
            else {
                setOptimisticLockingType(DEFAULT_OPTIMISTIC_LOCKING_TYPE);  
            }
        }
    }
    
    public String getOptimisticColumnsLockingType() {
		return this.optimisticColumnsLockingType;
	}

	public void setOptimisticColumnsLockingType(String fieldsType){
		String oldValue = this.optimisticColumnsLockingType; 
		this.optimisticColumnsLockingType = fieldsType;
		firePropertyChanged(OPTIMISTIC_COLUMNS_LOCKING_TYPE_PROPERTY, oldValue, this.optimisticColumnsLockingType);

		
		if (attributeValueHasChanged(oldValue, this.optimisticColumnsLockingType)) {
			if (this.optimisticColumnsLockingType == OPTIMISTIC_COLUMNS_SELECTED_COLUMNS) {
				//do nothing
			}
			else {
				clearColumnLockColumns();
			}
		}
}
	
	public String getOptimisticLockingType() {
		return this.optimisticLockingType;
	}

	public void setOptimisticLockingType(String optimisticLockingType)
	{
		String oldLockingType = this.optimisticLockingType;
		this.optimisticLockingType = optimisticLockingType;
		firePropertyChanged(OPTIMISTIC_LOCKING_TYPE_PROPERTY, oldLockingType, this.optimisticLockingType);
		if (attributeValueHasChanged(oldLockingType, this.optimisticLockingType)) {
			if (this.optimisticLockingType == OPTIMISTIC_VERSION_LOCKING_TYPE) {
				this.setOptimisticColumnsLockingType(null);
				setOptimisticVersionLockingType(DEFAULT_OPTIMISTIC_VERSION_LOCKING_TYPE);
				this.clearColumnLockColumns();
			}
			else if (this.optimisticLockingType == null) {
				this.setOptimisticColumnsLockingType(null);
				setOptimisticVersionLockingType(null);
				this.setVersionLockField(null);
				this.clearColumnLockColumns();
			}
			else {
				this.setOptimisticColumnsLockingType(DEFAULT_OPTIMISTIC_COLUMNS_LOCKING_TYPE);
				this.setVersionLockField(null);
				this.setOptimisticVersionLockingType(null);
			}
		}
	}
	

	// ************* problems *************
	
	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.selectedColumnsLockingNotMapped(problems);
		this.selectedColumnsLockingContainsPrimaryKey(problems);
	}

	//override for column locking types don't want to do check this if we are column locking
	protected void checkWriteLockFieldWritable(List newProblems) {
		if (this.getOptimisticLockingType() == OPTIMISTIC_VERSION_LOCKING_TYPE) {
			super.checkWriteLockFieldWritable(newProblems);
		}
	}
	
	private void selectedColumnsLockingNotMapped(List newProblems) {
		if (this.getLockingType() == OPTIMISTIC_LOCKING
				&& this.getOptimisticLockingType() == OPTIMISTIC_COLUMNS_LOCKING_TYPE
				&& this.getOptimisticColumnsLockingType() == OPTIMISTIC_COLUMNS_SELECTED_COLUMNS)
		{
			List unmappedColumnNames = new ArrayList();
			List invalidColumnNames = new ArrayList();
			for (Iterator stream = columnLockColumns(); stream.hasNext(); ) {
				MWColumn column = (MWColumn) stream.next();
				if ( ! CollectionTools.contains(this.getOwningTableDescriptor().allAssociatedColumns(), column)) {
					invalidColumnNames.add(column.getName());			
				}
				if (this.getOwningTableDescriptor().allWritableMappingsForField(column).size() == 0) {
					unmappedColumnNames.add(column.getName());
				}
			}
			if (unmappedColumnNames.size() > 0) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_SELECTED_FIELDS_NOT_MAPPED, unmappedColumnNames));
			}
			if (invalidColumnNames.size() > 0) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_SELECTED_FIELDS_NOT_VALID, invalidColumnNames));
			}
		}
	}
	
	protected void checkLockFieldSpecifiedForLockingPolicy(List newProblems) {
		if (this.getLockingType() == OPTIMISTIC_LOCKING
				&& this.getOptimisticLockingType() == OPTIMISTIC_VERSION_LOCKING_TYPE) 
		{
			if (this.getVersionLockField() == null) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_SPECIFIED));				
			}
			else if ( ! CollectionTools.contains(this.getOwningTableDescriptor().allAssociatedColumns(), this.getVersionLockField())) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_VALID));			
			}
					
		}
	}
	
	private void selectedColumnsLockingContainsPrimaryKey(List newProblems) {
		if(this.getLockingType() == OPTIMISTIC_LOCKING
				&& this.getOptimisticLockingType() == OPTIMISTIC_COLUMNS_LOCKING_TYPE
				&& this.getOptimisticColumnsLockingType() == OPTIMISTIC_COLUMNS_SELECTED_COLUMNS)
		{
			Collection primaryKeys = CollectionTools.collection(this.getOwningTableDescriptor().primaryKeys());
			ArrayList illegalColumnNames = new ArrayList();
			for (Iterator stream = columnLockColumns(); stream.hasNext(); ) {
				MWColumn column = (MWColumn) stream.next();
				if (primaryKeys.contains(column)) {
					illegalColumnNames.add(column.getName());
				}
			}
			if (illegalColumnNames.size() > 0) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_LOCKING_SELECTED_FIELDS_ARE_PKS, illegalColumnNames));
			}
				
		}
	}
	

	// ************* runtime conversion *************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		super.adjustRuntimeDescriptor(runtimeDescriptor);
		if (getLockingType() == OPTIMISTIC_LOCKING) {
			if (getOptimisticLockingType() == OPTIMISTIC_VERSION_LOCKING_TYPE) {
				VersionLockingPolicy lockingPolicy;
				if (getOptimisticVersionLockingType() == OPTIMISTIC_VERSION_VERSION) {
					lockingPolicy = new VersionLockingPolicy();
				} else {
					lockingPolicy = new TimestampLockingPolicy();
					if (this.usesLocalTime()) {
						((TimestampLockingPolicy) lockingPolicy).useLocalTime();
					} else {
						((TimestampLockingPolicy) lockingPolicy).useServerTime();
					}
				}
				if (getVersionLockField() != null) {
					lockingPolicy.setWriteLockFieldName(getVersionLockField().runtimeField().getQualifiedName());
				}
				lockingPolicy.setIsStoredInCache(shouldStoreVersionInCache());
				runtimeDescriptor.setOptimisticLockingPolicy(lockingPolicy);
			} else if (getOptimisticLockingType() == OPTIMISTIC_COLUMNS_LOCKING_TYPE) {
				FieldsLockingPolicy lockingPolicy;
				if (getOptimisticColumnsLockingType() == OPTIMISTIC_COLUMNS_ALL_COLUMNS) {
					lockingPolicy = new AllFieldsLockingPolicy();
				} else if (getOptimisticColumnsLockingType() == OPTIMISTIC_COLUMNS_CHANGED_COLUMNS) {
					lockingPolicy = new ChangedFieldsLockingPolicy();
				} else {
					lockingPolicy = new SelectedFieldsLockingPolicy();
					((SelectedFieldsLockingPolicy) lockingPolicy).setLockFieldNames(CollectionTools.vector(columnLockColumnsNames()));
				}
				runtimeDescriptor.setOptimisticLockingPolicy(lockingPolicy);
			}
		}
	}


	// ************* TopLink methods *************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWTableDescriptorLockingPolicy.class);
		descriptor.getInheritancePolicy().setParentClass(MWDescriptorLockingPolicy.class);
		
		XMLCompositeObjectMapping versionLockingColumnHandleMapping = new XMLCompositeObjectMapping();
		versionLockingColumnHandleMapping.setAttributeName("versionLockingColumnHandle");
		versionLockingColumnHandleMapping.setGetMethodName("getVersionLockingColumnHandleForTopLink");
		versionLockingColumnHandleMapping.setSetMethodName("setVersionLockingColumnHandleForTopLink");
		versionLockingColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		versionLockingColumnHandleMapping.setXPath("version-locking-column-handle");
		descriptor.addMapping(versionLockingColumnHandleMapping);
		
		ObjectTypeConverter optimisticLockingTypeConverter = new ObjectTypeConverter();
		optimisticLockingTypeConverter.addConversionValue(
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_LOCKING_TYPE,
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_LOCKING_TYPE);
		optimisticLockingTypeConverter.addConversionValue(
				MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE,
				MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE);
		XMLDirectMapping optimisticLockingTypeMapping = new XMLDirectMapping();
		optimisticLockingTypeMapping.setAttributeName("optimisticLockingType");
		optimisticLockingTypeMapping.setXPath("optimistic-locking-type/text()");
		optimisticLockingTypeMapping.setConverter(optimisticLockingTypeConverter);
		descriptor.addMapping(optimisticLockingTypeMapping);
		
		ObjectTypeConverter optimisticColumnsLockingTypeConverter = new ObjectTypeConverter();
		optimisticColumnsLockingTypeConverter.addConversionValue(
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_ALL_COLUMNS,
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_ALL_COLUMNS);
		optimisticColumnsLockingTypeConverter.addConversionValue(
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_CHANGED_COLUMNS,
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_CHANGED_COLUMNS);
		optimisticColumnsLockingTypeConverter.addConversionValue(
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_SELECTED_COLUMNS,
				MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_SELECTED_COLUMNS);
		XMLDirectMapping optimisticColumnsLockingTypeMapping = new XMLDirectMapping();
		optimisticColumnsLockingTypeMapping.setAttributeName("optimisticColumnsLockingType");
		optimisticColumnsLockingTypeMapping.setXPath("optimistic-columns-locking-type/text()");
		optimisticColumnsLockingTypeMapping.setConverter(optimisticColumnsLockingTypeConverter);
		descriptor.addMapping(optimisticColumnsLockingTypeMapping);
		
		XMLCompositeCollectionMapping columnLockColumnHandlesMapping = new XMLCompositeCollectionMapping();
		columnLockColumnHandlesMapping.setAttributeName("columnLockColumnHandles");
		columnLockColumnHandlesMapping.setGetMethodName("getColumnLockColumnHandlesForTopLink");
		columnLockColumnHandlesMapping.setSetMethodName("setColumnLockColumnHandlesForTopLink");
		columnLockColumnHandlesMapping.setReferenceClass(MWColumnHandle.class);
		columnLockColumnHandlesMapping.setXPath("column-lock-column-handles/column-handle");
		descriptor.addMapping(columnLockColumnHandlesMapping);
		
		return descriptor;
	}	
	
	/**
	 * check for null
	 */
	private MWColumnHandle getVersionLockingColumnHandleForTopLink() {
		return (this.versionLockingColumnHandle.getColumn() == null) ? null : this.versionLockingColumnHandle;
	}
	private void setVersionLockingColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildVersionLockingColumnScrubber();
		this.versionLockingColumnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * sort the collection for TopLink
	 */
	private Collection getColumnLockColumnHandlesForTopLink() {
		synchronized (this.columnLockColumnHandles) {
			return new TreeSet(this.columnLockColumnHandles);
		}
	}
	private void setColumnLockColumnHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWColumnHandle) stream.next()).setScrubber(this.columnLockColumnScrubber());
		}
		this.columnLockColumnHandles = handles;
	}

}
