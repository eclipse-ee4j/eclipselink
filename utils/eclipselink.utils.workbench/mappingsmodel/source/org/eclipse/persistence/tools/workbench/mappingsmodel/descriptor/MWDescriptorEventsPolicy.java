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
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWDescriptorEventsPolicy extends MWAbstractDescriptorPolicy {

	private MWMethodHandle postBuildMethodHandle;
		public final static String POST_BUILD_METHOD_PROPERTY = "postBuildMethod";
	private MWMethodHandle postCloneMethodHandle;
		public final static String POST_CLONE_METHOD_PROPERTY = "postCloneMethod";
	private MWMethodHandle postMergeMethodHandle;
		public final static String POST_MERGE_METHOD_PROPERTY = "postMergeMethod";
	private MWMethodHandle postRefreshMethodHandle;
		public final static String POST_REFRESH_METHOD_PROPERTY = "postRefreshMethod";
	private MWMethodHandle preUpdateMethodHandle;
		public final static String PRE_UPDATE_METHOD_PROPERTY = "preUpdateMethod";
	private MWMethodHandle aboutToUpdateMethodHandle;
		public final static String ABOUT_TO_UPDATE_METHOD_PROPERTY = "aboutToUpdateMethod";
	private MWMethodHandle aboutToInsertMethodHandle;
		public final static String ABOUT_TO_INSERT_METHOD_PROPERTY = "aboutToInsertMethod";
	private MWMethodHandle postUpdateMethodHandle;
		public final static String POST_UPDATE_METHOD_PROPERTY = "postUpdateMethod";
	private MWMethodHandle postInsertMethodHandle;
		public final static String POST_INSERT_METHOD_PROPERTY = "postInsertMethod";
	private MWMethodHandle preInsertMethodHandle;
		public final static String PRE_INSERT_METHOD_PROPERTY = "preInsertMethod";
	private MWMethodHandle preDeletingMethodHandle;
		public final static String PRE_DELETING_METHOD_PROPERTY = "preDeletingMethod";
	private MWMethodHandle preWritingMethodHandle;
		public final static String PRE_WRITING_METHOD_PROPERTY = "preWritingMethod";	
	private MWMethodHandle postWritingMethodHandle;
		public final static String POST_WRITING_METHOD_PROPERTY = "postWritingMethod";
	private MWMethodHandle postDeletingMethodHandle;
		public final static String POST_DELETING_METHOD_PROPERTY = "postDeletingMethod";


	// ********** constructors **********

	/**
	 * for TopLink use only
	 */
	private MWDescriptorEventsPolicy() {
		super();
	}

	MWDescriptorEventsPolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}


	// ********** initialization **********

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.aboutToInsertMethodHandle = new MWMethodHandle(this, this.buildAboutToInsertMethodScrubber());
		this.aboutToUpdateMethodHandle = new MWMethodHandle(this, this.buildAboutToUpdateMethodScrubber());
		this.postBuildMethodHandle = new MWMethodHandle(this, this.buildPostBuildMethodScrubber());
		this.postDeletingMethodHandle = new MWMethodHandle(this, this.buildPostDeletingMethodScrubber());
		this.postCloneMethodHandle = new MWMethodHandle(this, this.buildPostCloneMethodScrubber());
		this.postInsertMethodHandle = new MWMethodHandle(this, this.buildPostInsertMethodScrubber());
		this.postMergeMethodHandle = new MWMethodHandle(this, this.buildPostMergeMethodScrubber());
		this.postRefreshMethodHandle = new MWMethodHandle(this, this.buildPostRefreshMethodScrubber());
		this.postWritingMethodHandle = new MWMethodHandle(this, this.buildPostWritingMethodScrubber());
		this.postUpdateMethodHandle = new MWMethodHandle(this, this.buildPostUpdateMethodScrubber());
		this.preDeletingMethodHandle = new MWMethodHandle(this, this.buildPreDeletingMethodScrubber());
		this.preInsertMethodHandle = new MWMethodHandle(this, this.buildPreInsertMethodScrubber());
		this.preUpdateMethodHandle = new MWMethodHandle(this, this.buildPreUpdateMethodScrubber());
		this.preWritingMethodHandle = new MWMethodHandle(this, this.buildPreWritingMethodScrubber());
	}
	


	// ********** accessors **********

	public MWMethod getAboutToInsertMethod() {
		return this.aboutToInsertMethodHandle.getMethod();
	}

	public MWMethod getAboutToUpdateMethod() {
		return this.aboutToUpdateMethodHandle.getMethod();
	}

	public MWMethod getPostBuildMethod() {
		return this.postBuildMethodHandle.getMethod();
	}

	public MWMethod getPostCloneMethod() {
		return this.postCloneMethodHandle.getMethod();
	}

	public MWMethod getPostDeletingMethod() {
		return this.postDeletingMethodHandle.getMethod();
	}

	public MWMethod getPostInsertMethod() {
		return this.postInsertMethodHandle.getMethod();
	}

	public MWMethod getPostMergeMethod() {
		return this.postMergeMethodHandle.getMethod();
	}

	public MWMethod getPostRefreshMethod() {
		return this.postRefreshMethodHandle.getMethod();
	}

	public MWMethod getPostUpdateMethod() {
		return this.postUpdateMethodHandle.getMethod();
	}

	public MWMethod getPostWritingMethod() {
		return this.postWritingMethodHandle.getMethod();
	}

	public MWMethod getPreDeletingMethod() {
		return this.preDeletingMethodHandle.getMethod();
	}

	public MWMethod getPreInsertMethod() {
		return this.preInsertMethodHandle.getMethod();
	}

	public MWMethod getPreUpdateMethod() {
		return this.preUpdateMethodHandle.getMethod();
	}

	public MWMethod getPreWritingMethod() {
		return this.preWritingMethodHandle.getMethod();
	}

	public void setAboutToInsertMethod(MWMethod method) {
		MWMethod old = getAboutToInsertMethod();
		this.aboutToInsertMethodHandle.setMethod(method);
		this.firePropertyChanged(ABOUT_TO_INSERT_METHOD_PROPERTY, old, method);
	}

	public void setAboutToUpdateMethod(MWMethod method) {
		MWMethod old = getAboutToUpdateMethod();
		this.aboutToUpdateMethodHandle.setMethod(method);
		this.firePropertyChanged(ABOUT_TO_UPDATE_METHOD_PROPERTY, old, method);
	}

	public void setPostBuildMethod(MWMethod method) {
		MWMethod old = getPostBuildMethod();
		this.postBuildMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_BUILD_METHOD_PROPERTY, old, method);
	}

	public void setPostCloneMethod(MWMethod method) {
		MWMethod old = getPostCloneMethod();
		this.postCloneMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_CLONE_METHOD_PROPERTY, old, method);
	}

	public void setPostDeletingMethod(MWMethod method) {
		MWMethod old = getPostDeletingMethod();
		this.postDeletingMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_DELETING_METHOD_PROPERTY, old, method);
	}

	public void setPostInsertMethod(MWMethod method) {
		MWMethod old = getPostInsertMethod();
		this.postInsertMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_INSERT_METHOD_PROPERTY, old, method);
	}

	public void setPostMergeMethod(MWMethod method) {
		MWMethod old = getPostMergeMethod();
		this.postMergeMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_MERGE_METHOD_PROPERTY, old, method);
	}

	public void setPostRefreshMethod(MWMethod method) {
		MWMethod old = getPostRefreshMethod();
		this.postRefreshMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_REFRESH_METHOD_PROPERTY, old, method);
	}

	public void setPostUpdateMethod(MWMethod method) {
		MWMethod old = getPostUpdateMethod();
		this.postUpdateMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_UPDATE_METHOD_PROPERTY, old, method);
	}

	public void setPostWritingMethod(MWMethod method) {
		MWMethod old = getPostWritingMethod();
		this.postWritingMethodHandle.setMethod(method);
		this.firePropertyChanged(POST_WRITING_METHOD_PROPERTY, old, method);
	}

	public void setPreDeletingMethod(MWMethod method) {
		MWMethod old = getPreDeletingMethod();
		this.preDeletingMethodHandle.setMethod(method);
		this.firePropertyChanged(PRE_DELETING_METHOD_PROPERTY, old, method);
	}

	public void setPreInsertMethod(MWMethod method) {
		MWMethod old = getPreInsertMethod();
		this.preInsertMethodHandle.setMethod(method);
		this.firePropertyChanged(PRE_INSERT_METHOD_PROPERTY, old, method);
	}

	public void setPreUpdateMethod(MWMethod method) {
		MWMethod old = getPreUpdateMethod();
		this.preUpdateMethodHandle.setMethod(method);
		this.firePropertyChanged(PRE_UPDATE_METHOD_PROPERTY, old, method);
	}

	public void setPreWritingMethod(MWMethod method) {
		MWMethod old = getPreWritingMethod();
		this.preWritingMethodHandle.setMethod(method);
		this.firePropertyChanged(PRE_WRITING_METHOD_PROPERTY, old, method);
	}


	// ********** MWAbstractDescriptorPolicy implementation **********

	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}
	
	public boolean isActive() {
		return true;
	}
	

	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.aboutToInsertMethodHandle);
		children.add(this.aboutToUpdateMethodHandle);
		children.add(this.postBuildMethodHandle);
		children.add(this.postDeletingMethodHandle);
		children.add(this.postCloneMethodHandle);
		children.add(this.postInsertMethodHandle);
		children.add(this.postMergeMethodHandle);
		children.add(this.postRefreshMethodHandle);
		children.add(this.postWritingMethodHandle);
		children.add(this.postUpdateMethodHandle);
		children.add(this.preDeletingMethodHandle);
		children.add(this.preInsertMethodHandle);
		children.add(this.preUpdateMethodHandle);
		children.add(this.preWritingMethodHandle);
	}

	private NodeReferenceScrubber buildAboutToInsertMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setAboutToInsertMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildAboutToInsertMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildAboutToUpdateMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setAboutToUpdateMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildAboutToUpdateMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostBuildMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostBuildMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostBuildMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostDeletingMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostDeletingMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostDeletingMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostCloneMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostCloneMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostCloneMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostInsertMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostInsertMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostInsertMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostMergeMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostMergeMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostMergeMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostRefreshMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostRefreshMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostRefreshMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostWritingMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostWritingMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostWritingMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPostUpdateMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPostUpdateMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPostUpdateMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPreDeletingMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPreDeletingMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPreDeletingMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPreInsertMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPreInsertMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPreInsertMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPreUpdateMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPreUpdateMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPreUpdateMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildPreWritingMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorEventsPolicy.this.setPreWritingMethod(null);
			}
			public String toString() {
				return "MWDescriptorEventsPolicy.buildPreWritingMethodScrubber()";
			}
		};
	}


	// ********** converstion to runtime **********

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		DescriptorEventManager manager = (DescriptorEventManager) runtimeDescriptor.getEventManager();
		if (getAboutToInsertMethod() != null){
			manager.setAboutToInsertSelector(getAboutToInsertMethod().getName());
		}
		if (getAboutToUpdateMethod() != null){
			manager.setAboutToUpdateSelector(getAboutToUpdateMethod().getName());
		}
		if (getPostBuildMethod() != null){
			manager.setPostBuildSelector(getPostBuildMethod().getName());
		}
		if (getPostCloneMethod() != null){
			manager.setPostCloneSelector(getPostCloneMethod().getName());
		}
		if (getPostDeletingMethod() != null){
			manager.setPostDeleteSelector(getPostDeletingMethod().getName());
		}
		if (getPostInsertMethod() != null){
			manager.setPostInsertSelector(getPostInsertMethod().getName());
		}
		if (getPostMergeMethod() != null){
			manager.setPostMergeSelector(getPostMergeMethod().getName());
		}
		if (getPostRefreshMethod() != null){
			manager.setPostRefreshSelector(getPostRefreshMethod().getName());
		}
		if (getPostUpdateMethod() != null){
			manager.setPostUpdateSelector(getPostUpdateMethod().getName());
		}
		if (getPostWritingMethod() != null){
			manager.setPostWriteSelector(getPostWritingMethod().getName());
		}
		if (getPreInsertMethod() != null){
			manager.setPreInsertSelector(getPreInsertMethod().getName());
		}
		if (getPreDeletingMethod() != null){
			manager.setPreDeleteSelector(getPreDeletingMethod().getName());
		}
		if (getPreUpdateMethod() != null){
			manager.setPreUpdateSelector(getPreUpdateMethod().getName());
		}
		if (getPreWritingMethod() != null){
			manager.setPreWriteSelector(getPreWritingMethod().getName());
		}
	}
	

	// ********** problems **********

	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkPostBuildMethod(problems);
		this.checkPostCloneMethod(problems);
		this.checkPostMergeMethod(problems);
		this.checkPostRefreshMethod(problems);
		this.checkPreUpdateMethod(problems);
		this.checkAboutToUpdateMethod(problems);
		this.checkPostUpdateMethod(problems);
		this.checkPreInsertMethod(problems);
		this.checkAboutToInsertMethod(problems);
		this.checkPostInsertMethod(problems);
		this.checkPreWritingMethod(problems);
		this.checkPostWritingMethod(problems);
		this.checkPreDeletingMethod(problems);
		this.checkPostDeletingMethod(problems);

	}
	
	private void checkPostBuildMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostBuildMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_BUILD,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_BUILD_VALID
		);
	}
	
	private void checkPostCloneMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostCloneMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_CLONE,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_CLONE_VALID
		);
	}
	
	private void checkPostMergeMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostMergeMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_MERGE,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_MERGE_VALID
		);
	}	
	
	private void checkPostRefreshMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostRefreshMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_REFRESH,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_REFRESH_VALID
		);
	}
	
	private void checkPreUpdateMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPreUpdateMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_UPDATE,
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_UPDATE_VALID
		);
	}
	
	private void checkAboutToUpdateMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getAboutToUpdateMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_ABOUT_TO_UPDATE,
			ProblemConstants.DESCRIPTOR_EVENTS_ABOUT_TO_UPDATE_VALID
		);
	}
	
	private void checkPostUpdateMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostUpdateMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_UPDATE,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_UPDATE_VALID
		);
	}
	
	private void checkPreInsertMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPreInsertMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_INSERT,
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_INSERT_VALID
		);
	}
	
	private void checkAboutToInsertMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getAboutToInsertMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_ABOUT_TO_INSERT,
			ProblemConstants.DESCRIPTOR_EVENTS_ABOUT_TO_INSERT_VALID
		);
	}
	
	private void checkPostInsertMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostInsertMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_INSERT,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_INSERT_VALID
		);
	}
	
	private void checkPreWritingMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPreWritingMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_WRITING,
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_WRITING_VALID
		);
	}
	
	private void checkPostWritingMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostWritingMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_WRITING,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_WRITING_VALID
		);
	}
	
	private void checkPreDeletingMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPreDeletingMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_DELETING,
			ProblemConstants.DESCRIPTOR_EVENTS_PRE_DELETING_VALID
		);
	}

	private void checkPostDeletingMethod(List newProblems) {
		this.checkMethod(
			newProblems,
			this.getPostDeletingMethod(),
			ProblemConstants.DESCRIPTOR_EVENTS_POST_DELETING,
			ProblemConstants.DESCRIPTOR_EVENTS_POST_DELETING_VALID
		);
	}

	private void checkMethod(List newProblems, MWMethod method, String messageKey1, String messageKey2) {
		if (method == null) {
			return;
		}
		if ( ! CollectionTools.contains(this.getOwningDescriptor().getMWClass().allMethods(), method)) {
			newProblems.add(this.buildProblem(messageKey1));
		}
		if ( ! method.isCandidateDescriptorEventMethod()) {
			newProblems.add(this.buildProblem(messageKey2));
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorEventsPolicy.class);
		
		XMLCompositeObjectMapping postBuildMethodHandleMapping = new XMLCompositeObjectMapping();
		postBuildMethodHandleMapping.setAttributeName("postBuildMethodHandle");
		postBuildMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postBuildMethodHandleMapping.setGetMethodName("getPostBuildMethodHandleForTopLink");
		postBuildMethodHandleMapping.setSetMethodName("setPostBuildMethodHandleForTopLink");
		postBuildMethodHandleMapping.setXPath("post-build-method-handle");
		descriptor.addMapping(postBuildMethodHandleMapping);
		
		XMLCompositeObjectMapping postCloneMethodHandleMapping = new XMLCompositeObjectMapping();
		postCloneMethodHandleMapping.setAttributeName("postCloneMethodHandle");
		postCloneMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postCloneMethodHandleMapping.setGetMethodName("getPostCloneMethodHandleForTopLink");
		postCloneMethodHandleMapping.setSetMethodName("setPostCloneMethodHandleForTopLink");
		postCloneMethodHandleMapping.setXPath("post-clone-method-handle");
		descriptor.addMapping(postCloneMethodHandleMapping);
		
		XMLCompositeObjectMapping postMergeMethodHandleMapping = new XMLCompositeObjectMapping();
		postMergeMethodHandleMapping.setAttributeName("postMergeMethodHandle");
		postMergeMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postMergeMethodHandleMapping.setGetMethodName("getPostMergeMethodHandleForTopLink");
		postMergeMethodHandleMapping.setSetMethodName("setPostMergeMethodHandleForTopLink");
		postMergeMethodHandleMapping.setXPath("post-merge-method-handle");
		descriptor.addMapping(postMergeMethodHandleMapping);
		
		XMLCompositeObjectMapping postRefreshMethodHandleMapping = new XMLCompositeObjectMapping();
		postRefreshMethodHandleMapping.setAttributeName("postRefreshMethodHandle");
		postRefreshMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postRefreshMethodHandleMapping.setGetMethodName("getPostRefreshMethodHandleForTopLink");
		postRefreshMethodHandleMapping.setSetMethodName("setPostRefreshMethodHandleForTopLink");
		postRefreshMethodHandleMapping.setXPath("post-refresh-method-handle");
		descriptor.addMapping(postRefreshMethodHandleMapping);
		
		XMLCompositeObjectMapping preInsertMethodHandleMapping = new XMLCompositeObjectMapping();
		preInsertMethodHandleMapping.setAttributeName("preInsertMethodHandle");
		preInsertMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		preInsertMethodHandleMapping.setGetMethodName("getPreInsertMethodHandleForTopLink");
		preInsertMethodHandleMapping.setSetMethodName("setPreInsertMethodHandleForTopLink");
		preInsertMethodHandleMapping.setXPath("pre-insert-method-handle");
		descriptor.addMapping(preInsertMethodHandleMapping);
		
		XMLCompositeObjectMapping aboutToInsertMethodHandleMapping = new XMLCompositeObjectMapping();
		aboutToInsertMethodHandleMapping.setAttributeName("aboutToInsertMethodHandle");
		aboutToInsertMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		aboutToInsertMethodHandleMapping.setGetMethodName("getAboutToInsertMethodHandleForTopLink");
		aboutToInsertMethodHandleMapping.setSetMethodName("setAboutToInsertMethodHandleForTopLink");
		aboutToInsertMethodHandleMapping.setXPath("about-to-insert-method-handle");
		descriptor.addMapping(aboutToInsertMethodHandleMapping);
		
		XMLCompositeObjectMapping postInsertMethodHandleMapping = new XMLCompositeObjectMapping();
		postInsertMethodHandleMapping.setAttributeName("postInsertMethodHandle");
		postInsertMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postInsertMethodHandleMapping.setGetMethodName("getPostInsertMethodHandleForTopLink");
		postInsertMethodHandleMapping.setSetMethodName("setPostInsertMethodHandleForTopLink");
		postInsertMethodHandleMapping.setXPath("post-insert-method-handle");
		descriptor.addMapping(postInsertMethodHandleMapping);
		
		XMLCompositeObjectMapping preUpdateMethodHandleMapping = new XMLCompositeObjectMapping();
		preUpdateMethodHandleMapping.setAttributeName("preUpdateMethodHandle");
		preUpdateMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		preUpdateMethodHandleMapping.setGetMethodName("getPreUpdateMethodHandleForTopLink");
		preUpdateMethodHandleMapping.setSetMethodName("setPreUpdateMethodHandleForTopLink");
		preUpdateMethodHandleMapping.setXPath("pre-update-method-handle");
		descriptor.addMapping(preUpdateMethodHandleMapping);
		
		XMLCompositeObjectMapping aboutToUpdateMethodHandleMapping = new XMLCompositeObjectMapping();
		aboutToUpdateMethodHandleMapping.setAttributeName("aboutToUpdateMethodHandle");
		aboutToUpdateMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		aboutToUpdateMethodHandleMapping.setGetMethodName("getAboutToUpdateMethodHandleForTopLink");
		aboutToUpdateMethodHandleMapping.setSetMethodName("setAboutToUpdateMethodHandleForTopLink");
		aboutToUpdateMethodHandleMapping.setXPath("about-to-update-method-handle");
		descriptor.addMapping(aboutToUpdateMethodHandleMapping);
		
		XMLCompositeObjectMapping postUpdateMethodHandleMapping = new XMLCompositeObjectMapping();
		postUpdateMethodHandleMapping.setAttributeName("postUpdateMethodHandle");
		postUpdateMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postUpdateMethodHandleMapping.setGetMethodName("getPostUpdateMethodHandleForTopLink");
		postUpdateMethodHandleMapping.setSetMethodName("setPostUpdateMethodHandleForTopLink");
		postUpdateMethodHandleMapping.setXPath("post-update-method-handle");
		descriptor.addMapping(postUpdateMethodHandleMapping);
		
		XMLCompositeObjectMapping preWritingMethodHandleMapping = new XMLCompositeObjectMapping();
		preWritingMethodHandleMapping.setAttributeName("preWritingMethodHandle");
		preWritingMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		preWritingMethodHandleMapping.setGetMethodName("getPreWritingMethodHandleForTopLink");
		preWritingMethodHandleMapping.setSetMethodName("setPreWritingMethodHandleForTopLink");
		preWritingMethodHandleMapping.setXPath("pre-write-method-handle");
		descriptor.addMapping(preWritingMethodHandleMapping);
		
		XMLCompositeObjectMapping postWritingMethodHandleMapping = new XMLCompositeObjectMapping();
		postWritingMethodHandleMapping.setAttributeName("postWritingMethodHandle");
		postWritingMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postWritingMethodHandleMapping.setGetMethodName("getPostWritingMethodHandleForTopLink");
		postWritingMethodHandleMapping.setSetMethodName("setPostWritingMethodHandleForTopLink");
		postWritingMethodHandleMapping.setXPath("post-write-method-handle");
		descriptor.addMapping(postWritingMethodHandleMapping);
		
		XMLCompositeObjectMapping preDeletingMethodHandleMapping = new XMLCompositeObjectMapping();
		preDeletingMethodHandleMapping.setAttributeName("preDeletingMethodHandle");
		preDeletingMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		preDeletingMethodHandleMapping.setGetMethodName("getPreDeletingMethodHandleForTopLink");
		preDeletingMethodHandleMapping.setSetMethodName("setPreDeletingMethodHandleForTopLink");
		preDeletingMethodHandleMapping.setXPath("pre-delete-method-handle");
		descriptor.addMapping(preDeletingMethodHandleMapping);
		
		XMLCompositeObjectMapping postDeletingMethodHandleMapping = new XMLCompositeObjectMapping();
		postDeletingMethodHandleMapping.setAttributeName("postDeletingMethodHandle");
		postDeletingMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		postDeletingMethodHandleMapping.setGetMethodName("getPostDeletingMethodHandleForTopLink");
		postDeletingMethodHandleMapping.setSetMethodName("setPostDeletingMethodHandleForTopLink");
		postDeletingMethodHandleMapping.setXPath("post-delete-method-handle");
		descriptor.addMapping(postDeletingMethodHandleMapping);
				
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostBuildMethodHandleForTopLink() {
		return (this.postBuildMethodHandle.getMethod() == null) ? null : this.postBuildMethodHandle;
	}
	private void setPostBuildMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostBuildMethodScrubber();
		this.postBuildMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostCloneMethodHandleForTopLink() {
		return (this.postCloneMethodHandle.getMethod() == null) ? null : this.postCloneMethodHandle;
	}
	private void setPostCloneMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostCloneMethodScrubber();
		this.postCloneMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostMergeMethodHandleForTopLink() {
		return (this.postMergeMethodHandle.getMethod() == null) ? null : this.postMergeMethodHandle;
	}
	private void setPostMergeMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostMergeMethodScrubber();
		this.postMergeMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostRefreshMethodHandleForTopLink() {
		return (this.postRefreshMethodHandle.getMethod() == null) ? null : this.postRefreshMethodHandle;
	}
	private void setPostRefreshMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostRefreshMethodScrubber();
		this.postRefreshMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPreUpdateMethodHandleForTopLink() {
		return (this.preUpdateMethodHandle.getMethod() == null) ? null : this.preUpdateMethodHandle;
	}
	private void setPreUpdateMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPreUpdateMethodScrubber();
		this.preUpdateMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getAboutToUpdateMethodHandleForTopLink() {
		return (this.aboutToUpdateMethodHandle.getMethod() == null) ? null : this.aboutToUpdateMethodHandle;
	}
	private void setAboutToUpdateMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildAboutToUpdateMethodScrubber();
		this.aboutToUpdateMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getAboutToInsertMethodHandleForTopLink() {
		return (this.aboutToInsertMethodHandle.getMethod() == null) ? null : this.aboutToInsertMethodHandle;
	}
	private void setAboutToInsertMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildAboutToInsertMethodScrubber();
		this.aboutToInsertMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostUpdateMethodHandleForTopLink() {
		return (this.postUpdateMethodHandle.getMethod() == null) ? null : this.postUpdateMethodHandle;
	}
	private void setPostUpdateMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostUpdateMethodScrubber();
		this.postUpdateMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostInsertMethodHandleForTopLink() {
		return (this.postInsertMethodHandle.getMethod() == null) ? null : this.postInsertMethodHandle;
	}
	private void setPostInsertMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostInsertMethodScrubber();
		this.postInsertMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPreInsertMethodHandleForTopLink() {
		return (this.preInsertMethodHandle.getMethod() == null) ? null : this.preInsertMethodHandle;
	}
	private void setPreInsertMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPreInsertMethodScrubber();
		this.preInsertMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPreDeletingMethodHandleForTopLink() {
		return (this.preDeletingMethodHandle.getMethod() == null) ? null : this.preDeletingMethodHandle;
	}
	private void setPreDeletingMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPreDeletingMethodScrubber();
		this.preDeletingMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPreWritingMethodHandleForTopLink() {
		return (this.preWritingMethodHandle.getMethod() == null) ? null : this.preWritingMethodHandle;
	}
	private void setPreWritingMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPreWritingMethodScrubber();
		this.preWritingMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostWritingMethodHandleForTopLink() {
		return (this.postWritingMethodHandle.getMethod() == null) ? null : this.postWritingMethodHandle;
	}
	private void setPostWritingMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostWritingMethodScrubber();
		this.postWritingMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getPostDeletingMethodHandleForTopLink() {
		return (this.postDeletingMethodHandle.getMethod() == null) ? null : this.postDeletingMethodHandle;
	}
	private void setPostDeletingMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildPostDeletingMethodScrubber();
		this.postDeletingMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

}
