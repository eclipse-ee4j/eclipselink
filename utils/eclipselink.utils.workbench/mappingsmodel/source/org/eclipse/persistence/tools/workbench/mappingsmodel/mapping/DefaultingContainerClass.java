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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class DefaultingContainerClass extends MWModel {

	private volatile boolean usesDefaultContainerClass;
		public static final String USES_DEFAULT_CONTAINER_CLASS_PROPERTY = "usesDefaultContainerClass";

	private MWClassHandle containerClassHandle;
		public static final String CONTAINER_CLASS_PROPERTY = "containerClass";




	// ********** constructors **********

	/** Default constructor - for TopLink use only. */
	private DefaultingContainerClass() {
		super();
	}

	DefaultingContainerClass(MWContainerPolicy parent) {
		super(parent);
	}


	// **************** Building and Initializing *************

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.containerClassHandle = new MWClassHandle(this, this.buildContainerClassScrubber());
		this.usesDefaultContainerClass = true;
	}


	// **************** Containment hierarchy *************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.containerClassHandle);
	}

	private NodeReferenceScrubber buildContainerClassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				DefaultingContainerClass.this.setContainerClass(null);
			}
			public String toString() {
				return "DefaultingContainerClass.buildContainerClassScrubber()";
			}
		};
	}


	// **************** Accessors *************
	private MWContainerPolicy getContainerPolicy() {
		return (MWContainerPolicy) getParent();
	}

	public boolean usesDefaultContainerClass() {
		return this.usesDefaultContainerClass;
	}

	public void setUseDefaultContainerClass(boolean newUsesDefaultContainerClass) {
		if (newUsesDefaultContainerClass == this.usesDefaultContainerClass) {
			return;
		}

		boolean oldUsesDefaultContainerClass = this.usesDefaultContainerClass;
		MWClass oldContainerClass = getContainerClass();

		setUseDefaultContainerClassInternal(newUsesDefaultContainerClass);

		// if we use the default, the container class is always calculated
		if (this.usesDefaultContainerClass) {
			setContainerClassInternal(null);
		}
		// if we *used* to use the default, the container class should initially be set
		// to the old calculated value
		else if (oldUsesDefaultContainerClass) {
			setContainerClassInternal(oldContainerClass);
		}
	}

	private void setUseDefaultContainerClassInternal(boolean newUsesDefaultContainerClass) {
		boolean oldUsesDefaultContainerClass = this.usesDefaultContainerClass;
		this.usesDefaultContainerClass = newUsesDefaultContainerClass;
		firePropertyChanged(USES_DEFAULT_CONTAINER_CLASS_PROPERTY, oldUsesDefaultContainerClass, newUsesDefaultContainerClass);
	}

	/**
	 * if this mapping uses the default container class, this is always calculated
	 */
	public MWClass getContainerClass() {
		if (this.usesDefaultContainerClass()) {
			return getContainerPolicy().defaultContainerClass();
		}
		return this.containerClassHandle.getType(); 
	}

	public void setContainerClass(MWClass newContainerClass) {
		setContainerClassInternal(newContainerClass);

		// if we are setting the container class using this method (i.e. not directly)
		// we assume that we are not using the default container class
		if (newContainerClass != null)
			setUseDefaultContainerClassInternal(false);
	}

	private void setContainerClassInternal(MWClass newContainerClass) {
		MWClass oldContainerClass = this.getContainerClass();
		this.containerClassHandle.setType(newContainerClass);
		this.firePropertyChanged(CONTAINER_CLASS_PROPERTY, oldContainerClass, newContainerClass);
	}

	// ************** Problem Handling ****************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkContainerClass(currentProblems);
	}

	private void checkContainerClass(List currentProblems) {
		if (this.getContainerClass() == null) {
			currentProblems.add(buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_SPECIFIED));
		} else {
			if ( ! this.getContainerClass().isInstantiable()) {
				currentProblems.add(buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_INSTANTIABLE));
			}
		}
	}


	// **************** TopLink methods ***************************************

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(DefaultingContainerClass.class);

		XMLDirectMapping usesDefaultContainerClassMapping = new XMLDirectMapping();
		usesDefaultContainerClassMapping.setAttributeName("usesDefaultContainerClass");
		usesDefaultContainerClassMapping.setXPath("uses-default-container-class/text()");
		usesDefaultContainerClassMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(usesDefaultContainerClassMapping);

		XMLCompositeObjectMapping containerClassMapping = new XMLCompositeObjectMapping();
		containerClassMapping.setAttributeName("containerClassHandle");
		containerClassMapping.setSetMethodName("setContainerClassHandleForTopLink");
		containerClassMapping.setGetMethodName("getContainerClassHandleForTopLink");
		containerClassMapping.setReferenceClass(MWClassHandle.class);
		containerClassMapping.setXPath("container-class-handle");
		descriptor.addMapping(containerClassMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWClassHandle getContainerClassHandleForTopLink() {
		return (this.containerClassHandle.getType() == null) ? null : this.containerClassHandle;
	}
	private void setContainerClassHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildContainerClassScrubber();
		this.containerClassHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
