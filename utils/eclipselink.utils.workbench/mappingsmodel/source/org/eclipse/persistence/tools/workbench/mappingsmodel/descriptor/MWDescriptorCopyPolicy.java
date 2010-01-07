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
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWDescriptorCopyPolicy extends MWAbstractDescriptorPolicy {

	private MWMethodHandle methodHandle;

	private volatile String policyType;
		public final static String COPY_POLICY_PROPERTY = "copyPolicy";
		public final static String CLONE = "clone";
		public final static String INSTANTIATION_POLICY = "instantiation";
		public final static String CONSTRUCTOR = "constructor"; // keep
		public final static String CONSTRUCTOR_PROPERTY = CONSTRUCTOR;
		public final static String COPY_POLICY_TYPE_PROPERTY = "copyPolicyType";

		// around for backward compatibility
		public final static String COPY_METHOD_PROPERTY = "copyMethod";
	

	// ********** constructors **********

	private MWDescriptorCopyPolicy() {
		// for TopLink use only
		super();
	}

	public MWDescriptorCopyPolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}


	// ********** initialization **********

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.methodHandle = new MWMethodHandle(this, this.buildMethodScrubber());
		this.policyType = INSTANTIATION_POLICY;
	}


	// ********** accessors **********

	public MWMethod getMethod() {
		return this.methodHandle.getMethod();
	}

	public void setMethod(MWMethod newMethod) {
		MWMethod oldMethod = this.methodHandle.getMethod(); 
		this.methodHandle.setMethod(newMethod);
		firePropertyChanged(COPY_METHOD_PROPERTY, oldMethod, newMethod);
	}

	public String getPolicyType() {
		return this.policyType;
	}

	public void setPolicyType(String policyType) {
		String oldValue = this.policyType;
		this.policyType = policyType;
		firePropertyChanged(COPY_POLICY_TYPE_PROPERTY, oldValue, this.policyType);
	}


	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.methodHandle);
	}

	private NodeReferenceScrubber buildMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorCopyPolicy.this.setMethod(null);
			}
			public String toString() {
				return "MWDescriptorCopyPolicy.buildMethodScrubber()";
			}
		};
	}


	// ********** MWAbstractDescriptorPolicy implementation **********

	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}

	public boolean isActive() {
		return true;
	}


	// ********** runtime conversion **********

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		if (this.policyType == MWDescriptorCopyPolicy.INSTANTIATION_POLICY) {
			runtimeDescriptor.useInstantiationCopyPolicy();

		} else if (this.policyType == MWDescriptorCopyPolicy.CLONE) {
			MWMethod method = getMethod();
			if (method != null) {
				runtimeDescriptor.useCloneCopyPolicy(method.getName());
			}
		}
	}


	// ********** problems **********

	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkCloneCopyPolicy(problems);
	}
	
	private void checkCloneCopyPolicy(List newProblems) {
		if (this.getPolicyType() == CLONE) {
			if (this.getMethod() == null) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_COPYING_NO_METHOD_SPECIFIED));
			} else {
				if ( ! this.checkIfMethodIsInOwningClass()) {
					newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_COPYING_METHOD_NOT_VISIBLE));
				}
	            else if ( ! this.getMethod().isCandidateCloneMethod()) {
	                newProblems.add(buildProblem(ProblemConstants.DESCRIPTOR_COPYING_METHOD_NOT_VALID));               
	            }
			}
		}
	}

	public boolean checkIfMethodIsInOwningClass() {
		return CollectionTools.contains(getOwningDescriptor().getMWClass().allMethods(), getMethod());
	}

	
	// ********** printing **********

	public void toString(StringBuffer sb) {
		sb.append(this.getPolicyType());
		sb.append(", method=");
		sb.append(this.getMethod());
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		// Same as 5.0 for now
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorCopyPolicy.class);

		// use an object type mapping so we can preserve object identity
		ObjectTypeConverter policyTypeConverter = new ObjectTypeConverter();
		policyTypeConverter.addConversionValue(
				MWDescriptorCopyPolicy.INSTANTIATION_POLICY,
				MWDescriptorCopyPolicy.INSTANTIATION_POLICY);
		policyTypeConverter.addConversionValue(
				MWDescriptorCopyPolicy.CLONE,
				MWDescriptorCopyPolicy.CLONE);
		// convert the old "constructor" values to "instantiation"
		policyTypeConverter
				.setDefaultAttributeValue(MWDescriptorCopyPolicy.INSTANTIATION_POLICY);
		XMLDirectMapping policyTypeMapping = new XMLDirectMapping();
		policyTypeMapping.setAttributeName("policyType");
		policyTypeMapping.setXPath("policy-type/text()");
		policyTypeMapping.setNullValue(MWDescriptorCopyPolicy.INSTANTIATION_POLICY);
		policyTypeMapping.setConverter(policyTypeConverter);
		descriptor.addMapping(policyTypeMapping);
		
		XMLCompositeObjectMapping methodHandleMapping = new XMLCompositeObjectMapping();
		methodHandleMapping.setAttributeName("methodHandle");
		methodHandleMapping.setGetMethodName("getMethodHandleForTopLink");
		methodHandleMapping.setSetMethodName("setMethodHandleForTopLink");
		methodHandleMapping.setReferenceClass(MWMethodHandle.class);
		methodHandleMapping.setXPath("copy-policy-method-handle");
		descriptor.addMapping(methodHandleMapping);
		
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getMethodHandleForTopLink() {
		return (this.methodHandle.getMethod() == null) ? null : this.methodHandle;
	}
	private void setMethodHandleForTopLink(MWMethodHandle handle) {
		NodeReferenceScrubber scrubber = this.buildMethodScrubber();
		this.methodHandle = ((handle == null) ? new MWMethodHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
