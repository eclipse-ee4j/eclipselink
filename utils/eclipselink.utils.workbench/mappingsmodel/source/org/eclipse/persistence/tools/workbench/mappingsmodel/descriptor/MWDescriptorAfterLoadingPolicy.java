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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWDescriptorAfterLoadingPolicy extends MWAbstractDescriptorPolicy {

	private MWMethodHandle postLoadMethodHandle;
		public static final String POST_LOAD_METHOD_PROPERTY = "postLoadMethod";
	
	private MWClassHandle postLoadMethodClassHandle;
		public static final String POST_LOAD_METHOD_CLASS_PROPERTY = "postLoadMethodClass";
		

	// ********** constructors **********

	private MWDescriptorAfterLoadingPolicy() {
		// for TopLink use only
		super();
	}

	MWDescriptorAfterLoadingPolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}


	// ********** initialization **********


	protected void initialize(Node parent) {
		super.initialize(parent);
		this.postLoadMethodHandle = new MWMethodHandle(this, this.buildPostLoadMethodScrubber());
		this.postLoadMethodClassHandle = new MWClassHandle(this, this.buildPostLoadMethodClassScrubber());
	}

	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.postLoadMethodHandle);
		children.add(this.postLoadMethodClassHandle);
	}

	private NodeReferenceScrubber buildPostLoadMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorAfterLoadingPolicy.this.setPostLoadMethod(null);
			}
			public String toString() {
				return "MWDescriptorAfterLoadingPolicy.buildPostLoadMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostLoadMethodClassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorAfterLoadingPolicy.this.setPostLoadMethodClass(null);
			}
			public String toString() {
				return "MWDescriptorAfterLoadingPolicy.buildPostLoadMethodClassScrubber()";
			}
		};
	}


	// ********** accessors **********

	public MWMethod getPostLoadMethod() {
		return this.postLoadMethodHandle.getMethod();
	}

	public void setPostLoadMethod(MWMethod newMethod) {
		MWMethod oldMethod = this.postLoadMethodHandle.getMethod();
		this.postLoadMethodHandle.setMethod(newMethod);
		firePropertyChanged(POST_LOAD_METHOD_PROPERTY, oldMethod, newMethod);		
	}

	public MWClass getPostLoadMethodClass() {
		return this.postLoadMethodClassHandle.getType();
	}
	
	public void setPostLoadMethodClass(MWClass newPostLoadMethodClass) {
		MWClass oldClass = this.postLoadMethodClassHandle.getType();
		this.postLoadMethodClassHandle.setType(newPostLoadMethodClass);
		firePropertyChanged(POST_LOAD_METHOD_CLASS_PROPERTY, oldClass, newPostLoadMethodClass);
	}
	
	
	// ********** run-time **********
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		if(getPostLoadMethod() != null) {
			runtimeDescriptor.setAmendmentMethodName(getPostLoadMethod().getName());
		}
		if (getPostLoadMethodClass() != null) {
			runtimeDescriptor.setAmendmentClassName(getPostLoadMethodClass().getName());			
		}
	}
	
	
	// ********** MWAbstractDescriptorPolicy implementation **********
	
	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}
	
	public boolean isActive() {
		return true;
	}
	

	// ********** problems **********
	
	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkAfterLoadClass(problems);
		this.checkAfterLoadMethod(problems);
	}
	
	private void checkAfterLoadClass(List problems) {
		if (this.getPostLoadMethodClass() == null) {
			problems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_AFTER_LOADING_CLASS_MUST_BE_SPECIFIED));
		} else {
			if (this.getPostLoadMethod() != null) {
				if ( ! CollectionTools.contains(this.getPostLoadMethodClass().methods(), this.getPostLoadMethod())) {
					problems.add(buildProblem(ProblemConstants.DESCRIPTOR_AFTER_LOADING_METHOD_NOT_IN_SPECIFIED_CLASS));
				}
			}
		}
	}
	
	private void checkAfterLoadMethod(List problems) {
		if (this.getPostLoadMethod() == null) {
			problems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_AFTER_LOADING_METHOD_MUST_BE_SPECIFIED));
		} else {
			if ( ! this.getPostLoadMethod().isCandidateDescriptorAfterLoadMethod()) {
				problems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_AFTER_LOADING_METHOD_NOT_VALID));
			}
		}
	}
	

	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorAfterLoadingPolicy.class);

		XMLCompositeObjectMapping postLoadMethodClassHandleMapping = new XMLCompositeObjectMapping();
		postLoadMethodClassHandleMapping.setAttributeName("postLoadMethodClassHandle");
		postLoadMethodClassHandleMapping.setGetMethodName("getPostLoadMethodClassHandleForTopLink");
		postLoadMethodClassHandleMapping.setSetMethodName("setPostLoadMethodClassHandleForTopLink");
		postLoadMethodClassHandleMapping.setReferenceClass(MWClassHandle.class);
		postLoadMethodClassHandleMapping.setXPath("post-load-class-handle");
		descriptor.addMapping(postLoadMethodClassHandleMapping);
		
		XMLCompositeObjectMapping postLoadMethodHandleMapping = new XMLCompositeObjectMapping();
		postLoadMethodHandleMapping.setAttributeName("postLoadMethodHandle");
		postLoadMethodHandleMapping.setGetMethodName("getPostLoadMethodHandleForTopLink");
		postLoadMethodHandleMapping.setSetMethodName("setPostLoadMethodHandleForTopLink");
		postLoadMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postLoadMethodHandleMapping.setXPath("post-load-method-handle");
		descriptor.addMapping(postLoadMethodHandleMapping);
		
		return descriptor;
	}
	
	private void setPostLoadMethodClassHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildPostLoadMethodClassScrubber();
		this.postLoadMethodClassHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	private MWClassHandle getPostLoadMethodClassHandleForTopLink() {
		return (this.postLoadMethodClassHandle.getType() == null) ? null : this.postLoadMethodClassHandle;
	}
	
	private void setPostLoadMethodHandleForTopLink(MWMethodHandle handle) {
		NodeReferenceScrubber scrubber = this.buildPostLoadMethodScrubber();
		this.postLoadMethodHandle = ((handle == null) ? new MWMethodHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	private MWMethodHandle getPostLoadMethodHandleForTopLink() {
		return (this.postLoadMethodHandle.getMethod() == null) ? null : this.postLoadMethodHandle;
	}
	
}
