/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import deprecated.sdk.SDKAggregateObjectMapping;
import org.eclipse.persistence.sessions.Record;

public final class MWRelationalDescriptorInheritancePolicy extends MWDescriptorInheritancePolicy 
	implements MWRelationalClassIndicatorFieldPolicy.Parent {

	private volatile boolean readSubclassesOnQuery;
		public static String READ_SUBCLASSES_ON_QUERY_PROPERTY = "readSubclassesOnQuery";	

	private MWTableHandle readAllSubclassesViewHandle;
		public static String READ_ALL_SUBCLASSES_VIEW_PROPERTY = "readAllSubclassesView";

	private volatile boolean outerJoinAllSubclasses;
		public static String OUTER_JOIN_ALL_SUBCLASSES = "outerJoinAllSubclasses";	

	// *************** Constructors *************

	private MWRelationalDescriptorInheritancePolicy() {
		// for TopLink use only
		super();
	}
	
	MWRelationalDescriptorInheritancePolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}
	

	// *************** Initialization *************

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.readSubclassesOnQuery = true;
		this.outerJoinAllSubclasses = false;
		this.readAllSubclassesViewHandle = new MWTableHandle(this, this.buildReadAllSubclassesViewScrubber());
	}
	

	// *************** Containment Hierarchy *************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.readAllSubclassesViewHandle);
	}
		
	private NodeReferenceScrubber buildReadAllSubclassesViewScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalDescriptorInheritancePolicy.this.setReadAllSubclassesView(null);
			}
			public String toString() {
				return "MWRelationalDescriptorInheritancePolicy.buildReadAllSubclassesViewScrubber()";
			}
		};
	}
	

	// *************** Runtime conversion *************

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		super.adjustRuntimeDescriptor(runtimeDescriptor);
		InheritancePolicy runtimeInheritancePolicy = (InheritancePolicy) runtimeDescriptor.getInheritancePolicy();
		
		if (this.getReadAllSubclassesView() != null) {
			runtimeInheritancePolicy.setReadAllSubclassesViewName(this.getReadAllSubclassesView().getName());
		}
		
		runtimeInheritancePolicy.setShouldOuterJoinSubclasses(isOuterJoinAllSubclasses());
		
		runtimeInheritancePolicy.setShouldReadSubclasses(isReadSubclassesOnQuery());
	}
	
	
	// *************** Accessors *************

	protected MWClassIndicatorFieldPolicy buildClassIndicatorFieldPolicy() {
		return new MWRelationalClassIndicatorFieldPolicy(this, getAllDescriptorsAvailableForIndicatorDictionary().iterator());
	}

	public MWTable getReadAllSubclassesView() {
		return this.readAllSubclassesViewHandle.getTable();
	}
	
	public void dispose() {	 
		super.dispose();
		((MWRelationalProject)getProject()).notifyExpressionsToRecalculateQueryables();
	}
	
	public void setParentDescriptor(MWMappingDescriptor newValue) {
		super.setParentDescriptor(newValue);
		((MWRelationalProject)getProject()).notifyExpressionsToRecalculateQueryables();
	}
	
	public void setReadAllSubclassesView(MWTable newReadAllSubclassesView) {
		MWTable oldValue = this.readAllSubclassesViewHandle.getTable();
		this.readAllSubclassesViewHandle.setTable(newReadAllSubclassesView);
		firePropertyChanged(READ_ALL_SUBCLASSES_VIEW_PROPERTY, oldValue, newReadAllSubclassesView);
	}
	
	public void setReadSubclassesOnQuery(boolean newReadSubclassesOnQuery) {
		boolean oldValue = isReadSubclassesOnQuery();
		this.readSubclassesOnQuery = newReadSubclassesOnQuery;
		firePropertyChanged(READ_SUBCLASSES_ON_QUERY_PROPERTY, oldValue, isReadSubclassesOnQuery());
	}

	public boolean isReadSubclassesOnQuery() {
		return this.readSubclassesOnQuery;
	}
	
	protected void setClassIndicatorPolicy(MWClassIndicatorPolicy classIndicatorPolicy) {
		super.setClassIndicatorPolicy(classIndicatorPolicy);
		this.getProject().recalculateAggregatePathsToColumn(this.getOwningDescriptor());
	}
	
	public boolean isOuterJoinAllSubclasses() {
		return this.outerJoinAllSubclasses;
	}

	public void setOuterJoinAllSubclasses(boolean newOuterJoinAllSubclasses) {
		boolean oldValue = isOuterJoinAllSubclasses();
		this.outerJoinAllSubclasses = newOuterJoinAllSubclasses;
		firePropertyChanged(OUTER_JOIN_ALL_SUBCLASSES, oldValue, isOuterJoinAllSubclasses());
	}

	// *************** Automap Support *************

	public void automap() {
		super.automap();
		this.getClassIndicatorPolicy().automap();
	}


	//*************** Problem Handling *************
	
	protected String descendantDescriptorTypeMismatchProblemString() {
		return ProblemConstants.DESCRIPTOR_TABLE_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH;
	}

	protected boolean checkDescendantsForDescriptorTypeMismatch() {
		for (Iterator stream = this.descendentDescriptors(); stream.hasNext(); ) {
			MWRelationalDescriptor currentDescriptor = (MWRelationalDescriptor) stream.next();
			
			if ((currentDescriptor.isAggregateDescriptor() != ((MWRelationalDescriptor) getOwningDescriptor()).isAggregateDescriptor())) {
				return true;
			}
		}
		
		return false;
	}

	public void addClassIndicatorFieldNotSpecifiedProblemTo(List newProblems) {
		((MWClassIndicatorFieldPolicy) getClassIndicatorPolicy()).checkClassIndicatorField(newProblems);
	}
	
	//*************** Toplink methods *************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.getInheritancePolicy().setParentClass(MWDescriptorInheritancePolicy.class);	
		descriptor.setJavaClass(MWRelationalDescriptorInheritancePolicy.class);
		
		XMLDirectMapping readSubclassesMapping = new XMLDirectMapping();
		readSubclassesMapping.setAttributeName("readSubclassesOnQuery");
		readSubclassesMapping.setXPath("read-subclasses-on-query/text()");
		readSubclassesMapping.setNullValue(Boolean.TRUE);
		descriptor.addMapping(readSubclassesMapping);
		
		XMLDirectMapping outerJoinAllSubclassesMapping = new XMLDirectMapping();
		outerJoinAllSubclassesMapping.setAttributeName("outerJoinAllSubclasses");
		outerJoinAllSubclassesMapping.setXPath("outer-join-all-subclasses/text()");
		outerJoinAllSubclassesMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(outerJoinAllSubclassesMapping);

		XMLCompositeObjectMapping rasvhMapping = new XMLCompositeObjectMapping();
		rasvhMapping.setAttributeName("readAllSubclassesViewHandle");
		rasvhMapping.setGetMethodName("getReadAllSubclassesViewHandleForTopLink");
		rasvhMapping.setSetMethodName("setReadAllSubclassesViewHandleForTopLink");
		rasvhMapping.setReferenceClass(MWTableHandle.class);
		rasvhMapping.setXPath("read-all-subclasses-view-handle");
		descriptor.addMapping(rasvhMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWTableHandle getReadAllSubclassesViewHandleForTopLink() {
		return (readAllSubclassesViewHandle.getTable() == null) ? null : this.readAllSubclassesViewHandle;
	}
	private void setReadAllSubclassesViewHandleForTopLink(MWTableHandle handle) {
		NodeReferenceScrubber scrubber = this.buildReadAllSubclassesViewScrubber();
		this.readAllSubclassesViewHandle = ((handle == null) ? new MWTableHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
	public static ClassDescriptor legacy50BuildDescriptor()  {
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();

		descriptor.setJavaClass(MWRelationalDescriptorInheritancePolicy.class);
		descriptor.setTableName("inheritance-policy");

		descriptor.addDirectMapping("isRoot", "is-root");
		
		TransformationMapping readSubclassesMapping = new TransformationMapping();
		readSubclassesMapping.setAttributeName("readSubclassesOnQuery");
		readSubclassesMapping.setAttributeTransformation("legacy50GetReadSubclassesFromRecordForTopLink");
		descriptor.addMapping(readSubclassesMapping);

		OneToOneMapping parentDescriptorMapping = new OneToOneMapping();
		parentDescriptorMapping.setAttributeName("parentDescriptor");
		parentDescriptorMapping.setGetMethodName("legacyGetParentDescriptor");
		parentDescriptorMapping.setSetMethodName("legacySetParentDescriptor");
		parentDescriptorMapping.setReferenceClass(MWDescriptor.class);
		parentDescriptorMapping.setForeignKeyFieldName("parent-descriptor");
		parentDescriptorMapping.dontUseIndirection();
		descriptor.addMapping(parentDescriptorMapping);

		OneToOneMapping rasvMapping = new OneToOneMapping();
		rasvMapping.setAttributeName("readAllSubclassesView");
		rasvMapping.setSetMethodName("legacySetReadAllSubclassesView");
		rasvMapping.setGetMethodName("legacyGetReadAllSubclassesView");
		rasvMapping.setReferenceClass(MWTable.class);
		rasvMapping.setForeignKeyFieldName("read-all-subclasses-view");
		rasvMapping.dontUseIndirection();
		descriptor.addMapping(rasvMapping);

		SDKAggregateObjectMapping classIndicatorPolicyMapping = new SDKAggregateObjectMapping();
		classIndicatorPolicyMapping.setAttributeName("classIndicatorPolicy");
		classIndicatorPolicyMapping.setReferenceClass(MWAbstractClassIndicatorPolicy.class);
		classIndicatorPolicyMapping.setFieldName("inheritance-policy-class-indicator-policy");
		descriptor.addMapping(classIndicatorPolicyMapping);

		return descriptor;
	}

	private Boolean legacy50GetReadSubclassesFromRecordForTopLink(Record record) {
		String readSubclassesOnQuery = (String) record.get("read-subclasses-on-query");
		if (readSubclassesOnQuery == null) {
			return Boolean.TRUE;
		}
		if (readSubclassesOnQuery.equals("true")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private void legacySetReadAllSubclassesView(MWTable table) {
		this.readAllSubclassesViewHandle = new MWTableHandle(this, table, this.buildReadAllSubclassesViewScrubber());
	}
	private MWTable legacyGetReadAllSubclassesView() {
		throw new UnsupportedOperationException();
	}
	
	public static ClassDescriptor legacy45BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.setJavaClass(MWRelationalDescriptorInheritancePolicy.class);
		descriptor.setTableName("InheritancePolicy");
		descriptor.descriptorIsAggregate();

		descriptor.addDirectMapping("isRoot", "isRoot");
		
		TransformationMapping readSubclassesMapping = new TransformationMapping();
		readSubclassesMapping.setAttributeName("readSubclassesOnQuery");
		readSubclassesMapping.setAttributeTransformation("legacy45GetReadSubclassesFromRecordForTopLink");
		descriptor.addMapping(readSubclassesMapping);

		OneToOneMapping parentMapping = new OneToOneMapping();
		parentMapping.setAttributeName("parentDescriptor");
		parentMapping.setGetMethodName("legacyGetParentDescriptor");
		parentMapping.setSetMethodName("legacySetParentDescriptor");
		parentMapping.setReferenceClass(MWDescriptor.class);
		parentMapping.setForeignKeyFieldName("parent");
		parentMapping.dontUseIndirection();
		descriptor.addMapping(parentMapping);

		OneToOneMapping rasMapping = new OneToOneMapping();
		rasMapping.setAttributeName("readAllSubclassesView");
		rasMapping.setSetMethodName("legacySetReadAllSubclassesView");
		rasMapping.setGetMethodName("legacyGetReadAllSubclassesView");
		rasMapping.setReferenceClass(MWTable.class);
		rasMapping.setForeignKeyFieldName("readAllSubclassesView");
		rasMapping.dontUseIndirection();
		descriptor.addMapping(rasMapping);

		SDKAggregateObjectMapping classIndicatorPolicyMapping = new SDKAggregateObjectMapping();
		classIndicatorPolicyMapping.setAttributeName("classIndicatorPolicy");
		classIndicatorPolicyMapping.setReferenceClass(MWAbstractClassIndicatorPolicy.class);
		classIndicatorPolicyMapping.setFieldName("classIndicatorPolicy");
		descriptor.addMapping(classIndicatorPolicyMapping);

		return descriptor;
	}
	
	private Boolean legacy45GetReadSubclassesFromRecordForTopLink(Record record) {
		String readSubclassesOnQuery = (String) record.get("readSubclassesOnQuery");
		if (readSubclassesOnQuery == null) {
			return Boolean.TRUE;
		}
		if (readSubclassesOnQuery.equals("true")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

}
