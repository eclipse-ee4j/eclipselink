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
}
