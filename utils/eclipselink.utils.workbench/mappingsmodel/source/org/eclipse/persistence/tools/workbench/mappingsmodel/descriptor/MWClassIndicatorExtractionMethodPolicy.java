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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWClassIndicatorExtractionMethodPolicy extends MWAbstractClassIndicatorPolicy {

	private MWMethodHandle methodHandle;
		public final static String METHOD_PROPERTY = "classExtractionMethod";


	// *************** Constructors ****************

	private MWClassIndicatorExtractionMethodPolicy() {
		super();
	}

	public MWClassIndicatorExtractionMethodPolicy(MWClassIndicatorPolicy.Parent parent) {
		super(parent);
	}


	// *************** Initialization ****************

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.methodHandle = new MWMethodHandle(this, this.buildMethodScrubber());
	}


	// *************** Accessors ****************

	public String getType() {
		return CLASS_EXTRACTION_METHOD_TYPE;
	}

	public MWMethod getClassExtractionMethod() {
		return this.methodHandle.getMethod();
	}
	
	public void setClassExtractionMethod(MWMethod classExtractionMethod) {
		MWMethod old = getClassExtractionMethod();
		this.methodHandle.setMethod(classExtractionMethod);
		firePropertyChanged(METHOD_PROPERTY, old, classExtractionMethod);
	}


	// *************** Model Synchronization Support ****************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.methodHandle);
	}

	private NodeReferenceScrubber buildMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassIndicatorExtractionMethodPolicy.this.setClassExtractionMethod(null);
			}
			public String toString() {
				return "MWClassIndicatorExtractionMethodPolicy.buildMethodScrubber()";
			}
		};
	}


	//*************** Problem Handling *************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		checkClassExtractionMethod(newProblems);
	}

	private void checkClassExtractionMethod(List newProblems) {
		if (getClassExtractionMethod() == null) {
			newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_INHERITANCE_CLASS_EXTRACTION_METHOD_NOT_SPECIFIED));
		}
		else if (!CollectionTools.contains(getContainingDescriptor().getMWClass().methods(), getClassExtractionMethod())) {
			newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_INHERITANCE_CLASS_EXTRACTION_METHOD_NOT_VISIBLE));			
		}
		else if(!getClassExtractionMethod().isCandidateClassExtractionMethod()) {
			newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_INHERITANCE_CLASS_EXTRACTION_METHOD_NOT_VALID));
		}
	}
	

	// *************** Runtime Conversion ****************

	public void adjustRuntimeInheritancePolicy(InheritancePolicy runtimeInheritancePolicy) {
		if (getClassExtractionMethod() != null) {
			runtimeInheritancePolicy.setClassExtractionMethodName(getClassExtractionMethod().getName());
		}
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWClassIndicatorExtractionMethodPolicy.class);	
		descriptor.getInheritancePolicy().setParentClass(MWAbstractClassIndicatorPolicy.class);


		XMLCompositeObjectMapping classExtractionMethodHandleMapping = new XMLCompositeObjectMapping();
		classExtractionMethodHandleMapping.setAttributeName("methodHandle");
		classExtractionMethodHandleMapping.setGetMethodName("getMethodHandleForTopLink");
		classExtractionMethodHandleMapping.setSetMethodName("setMethodHandleForTopLink");
		classExtractionMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		classExtractionMethodHandleMapping.setXPath("method-handle");
		descriptor.addMapping(classExtractionMethodHandleMapping);

		return descriptor;
	}

	private MWMethodHandle getMethodHandleForTopLink() {
		return (this.methodHandle.getMethod() == null) ? null : this.methodHandle;
	}
	private void setMethodHandleForTopLink(MWMethodHandle handle) {
		NodeReferenceScrubber scrubber = this.buildMethodScrubber();
		this.methodHandle = ((handle == null) ? new MWMethodHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
