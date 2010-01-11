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
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWDescriptorInstantiationPolicy extends MWAbstractDescriptorPolicy {

	private volatile String policyType;
		public static final String POLICY_TYPE_PROPERTY = "policyType";
		// Instantiation Policy types
		public final static String DEFAULT_CONSTRUCTOR = "Default constructor";
		public final static String METHOD = "Method";
		public final static String FACTORY = "Factory";

	private MWClassHandle factoryTypeHandle;
		public static final String FACTORY_TYPE_PROPERTY = "factoryType";
	private MWMethodHandle useMethodHandle;
		public static final String USE_METHOD_PROPERTY = "useMethod";
	private MWMethodHandle factoryMethodHandle;
		public static final String FACTORY_METHOD_PROPERTY = "factoryMethod";
	private MWMethodHandle instantiationMethodHandle;
		public static final String INSTANTIATION_METHOD_PROPERTY = "instantiationMethod";	

	
	// ********** constructors **********

	private MWDescriptorInstantiationPolicy() {
		// for TopLink use only
		super();
	}

	MWDescriptorInstantiationPolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}

	
	// ********** initialization **********

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.policyType = DEFAULT_CONSTRUCTOR;
		this.factoryTypeHandle = new MWClassHandle(this, this.buildFactoryTypeScrubber());
		this.useMethodHandle = new MWMethodHandle(this, this.buildUseMethodScrubber());
		this.factoryMethodHandle = new MWMethodHandle(this, this.buildFactoryMethodScrubber());
		this.instantiationMethodHandle = new MWMethodHandle(this, this.buildInstantiationMethodScrubber());
	}

	
	// ********** accessors **********

	public MWMethod getFactoryMethod() {
		return this.factoryMethodHandle.getMethod();
	}

	public void setFactoryMethod(MWMethod factoryMethod) {
		Object old = this.getFactoryMethod();
		this.factoryMethodHandle.setMethod(factoryMethod);
		this.firePropertyChanged(FACTORY_METHOD_PROPERTY, old, factoryMethod);
	}

	public MWClass getFactoryType() {
		return this.factoryTypeHandle.getType();
	}

	public void setFactoryType(MWClass factoryType) {
		Object old = this.factoryTypeHandle.getType();
		this.factoryTypeHandle.setType(factoryType);
		this.firePropertyChanged(FACTORY_TYPE_PROPERTY, old, factoryType);
	}

	public MWMethod getInstantiationMethod() {
		return this.instantiationMethodHandle.getMethod();
	}

	public void setInstantiationMethod(MWMethod instantiationMethod) {
		Object old = this.getInstantiationMethod();
		this.instantiationMethodHandle.setMethod(instantiationMethod);
		this.firePropertyChanged(INSTANTIATION_METHOD_PROPERTY, old, instantiationMethod);
	}

	public MWMethod getUseMethod() {
		return this.useMethodHandle.getMethod();
	}

	public void setUseMethod(MWMethod useMethod) {
		Object old = this.getUseMethod();
		this.useMethodHandle.setMethod(useMethod);
		this.firePropertyChanged(USE_METHOD_PROPERTY, old, useMethod);
	}

	public String getPolicyType() {
		return this.policyType;
	}

	public void setPolicyType(String policyType) {
		Object old = this.policyType;
		this.policyType = policyType;
		this.firePropertyChanged(POLICY_TYPE_PROPERTY, old, policyType);
	}


	// ********** behavior **********

	public boolean checkUseMethodLegitimacy() {
		return CollectionTools.contains(this.getOwningDescriptor().getMWClass().allMethods(), this.getUseMethod());
	}

	public boolean checkFactoryInstantiationMethodLegitimacy() {
		return CollectionTools.contains(this.getFactoryType().allMethods(), this.getInstantiationMethod());
	}

	public boolean checkFactoryMethodLegitimacy() {
		return CollectionTools.contains(this.getFactoryType().allMethods(), this.getFactoryMethod());
	}

	public void toString(StringBuffer sb) {
		sb.append(this.getPolicyType());
	}
	
	
	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.useMethodHandle);
		children.add(this.factoryMethodHandle);
		children.add(this.instantiationMethodHandle);
		children.add(this.factoryTypeHandle);
	}

	private NodeReferenceScrubber buildUseMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorInstantiationPolicy.this.setUseMethod(null);
			}
			public String toString() {
				return "MWDescriptorInstantiationPolicy.buildUseMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildFactoryMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorInstantiationPolicy.this.setFactoryMethod(null);
			}
			public String toString() {
				return "MWDescriptorInstantiationPolicy.buildFactoryMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildInstantiationMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorInstantiationPolicy.this.setInstantiationMethod(null);
			}
			public String toString() {
				return "MWDescriptorInstantiationPolicy.buildInstantiationMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildFactoryTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorInstantiationPolicy.this.setFactoryType(null);
			}
			public String toString() {
				return "MWDescriptorInstantiationPolicy.buildFactoryTypeScrubber()";
			}
		};
	}

	
	// ********** run-time **********

	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		String policyType = getPolicyType();
		if (policyType == MWDescriptorInstantiationPolicy.DEFAULT_CONSTRUCTOR) {
			runtimeDescriptor.useDefaultConstructorInstantiationPolicy();
		} 
		else if (policyType == MWDescriptorInstantiationPolicy.METHOD) {
			MWMethod instantiationMethod = getUseMethod();
			if (instantiationMethod != null) {
				runtimeDescriptor.useMethodInstantiationPolicy(instantiationMethod.getName());
			}

		} 
		else if (policyType == MWDescriptorInstantiationPolicy.FACTORY) {
			// at least the factory class and an instantiation method must be selected
			MWClass factoryType = getFactoryType();
			if (factoryType == null) {
				return;
			}
			MWMethod instantiationMethod = getInstantiationMethod();
			if (instantiationMethod == null) {
				return;
			}

			String factoryClassName = factoryType.getName();
			MWMethod factoryMethod = getFactoryMethod();
			if (factoryMethod == null) {
				runtimeDescriptor.useFactoryInstantiationPolicy(factoryClassName, instantiationMethod.getName());
			} else {
				runtimeDescriptor.useFactoryInstantiationPolicy(factoryClassName, instantiationMethod.getName(), factoryMethod.getName());
			}
		} else {
			throw new IllegalStateException(policyType);
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
		this.checkDefaultConstructor(problems);
		this.checkInstantiationMethod(problems);
		this.checkFactoryMethod(problems);
		this.checkFactoryInstantiationMethod(problems);
	}
	
	private void checkDefaultConstructor(List newProblems) {
		if (this.getPolicyType() == MWDescriptorInstantiationPolicy.DEFAULT_CONSTRUCTOR) {
			if ( ! this.getOwningDescriptor().getMWClass().hasAccessibleZeroArgumentConstructor()) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_NO_ZERO_ARG_CONSTRUCTOR));
			}
		}
	}

	private void checkInstantiationMethod(List newProblems) {
		if (this.getPolicyType() == MWDescriptorInstantiationPolicy.METHOD) {
			if (this.getUseMethod() == null) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_USE_METHOD));
			} else {
				if ( ! this.checkUseMethodLegitimacy()) {
					newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_INSTANTIATION_METHOD_NOT_VISIBLE));
				}
	            else if ( ! this.getUseMethod().isCandidateInstantiationMethod()) {
	                newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_INSTANTIATION_METHOD_NOT_VALID));               
	            }
			}
		}
	}

	private void checkFactoryMethod(List newProblems) {
		if (this.getPolicyType() == MWDescriptorInstantiationPolicy.FACTORY) {
			if ((this.getFactoryMethod() != null) && (this.getFactoryType() != null)) {
				if ( ! this.checkFactoryMethodLegitimacy()) {
					newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_FACTORY_METHOD_NOT_VISIBLE));
				}
	            else if ( ! this.getFactoryMethod().isCandidateFactoryMethod()) {
	                newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_FACTORY_METHOD_NOT_VALID));               
	            }
			}
		}
	}
	
	private void checkFactoryInstantiationMethod(List newProblems) {
		if (this.getPolicyType() == MWDescriptorInstantiationPolicy.FACTORY) {
			if (this.getInstantiationMethod() == null) {
				newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_USE_FACTORY));
			} else {
				if (this.getFactoryType() != null) {
					if ( ! this.checkFactoryInstantiationMethodLegitimacy()) {
						newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_FACTORY_INSTANTIATION_METHOD_NOT_VISIBLE));
					}
		            else if ( ! this.getInstantiationMethod().isCandidateFactoryInstantiationMethodFor(this.getFactoryType())) {
		                newProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_FACTORY_INSTANTIATION_METHOD_NOT_VALID));               
		            }
				}
			}
        }
	}

	
	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorInstantiationPolicy.class);

		ObjectTypeConverter policyTypeConverter = new ObjectTypeConverter();
		policyTypeConverter.addConversionValue(
				MWDescriptorInstantiationPolicy.DEFAULT_CONSTRUCTOR,
				MWDescriptorInstantiationPolicy.DEFAULT_CONSTRUCTOR);
		policyTypeConverter.addConversionValue(
				MWDescriptorInstantiationPolicy.FACTORY,
				MWDescriptorInstantiationPolicy.FACTORY);
		policyTypeConverter.addConversionValue(
				MWDescriptorInstantiationPolicy.METHOD,
				MWDescriptorInstantiationPolicy.METHOD);
		XMLDirectMapping policyTypeMapping = new XMLDirectMapping();
		policyTypeMapping.setAttributeName("policyType");
		policyTypeMapping.setXPath("policy-type/text()");
		/*policyTypeMapping.setNullValue(MWDescriptorInstantiationPolicy.DEFAULT_CONSTRUCTOR);*/
		policyTypeMapping.setConverter(policyTypeConverter);
		descriptor.addMapping(policyTypeMapping);

		XMLCompositeObjectMapping factoryTypeHandleMapping = new XMLCompositeObjectMapping();
		factoryTypeHandleMapping.setAttributeName("factoryTypeHandle");
		factoryTypeHandleMapping.setSetMethodName("setFactoryTypeHandleForTopLink");
		factoryTypeHandleMapping.setGetMethodName("getFactoryTypeHandleForTopLink");
		factoryTypeHandleMapping.setReferenceClass(MWClassHandle.class);
		factoryTypeHandleMapping.setXPath("factory-type-handle");
		descriptor.addMapping(factoryTypeHandleMapping);
		
		XMLCompositeObjectMapping useMethodHandleMapping = new XMLCompositeObjectMapping();
		useMethodHandleMapping.setAttributeName("useMethodHandle");
		useMethodHandleMapping.setSetMethodName("setUseMethodHandleForTopLink");
		useMethodHandleMapping.setGetMethodName("getUseMethodHandleForTopLink");
		useMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		useMethodHandleMapping.setXPath("use-method-handle");
		descriptor.addMapping(useMethodHandleMapping);
		
		XMLCompositeObjectMapping factoryMethodHandleMapping = new XMLCompositeObjectMapping();
		factoryMethodHandleMapping.setAttributeName("factoryMethodHandle");
		factoryMethodHandleMapping.setSetMethodName("setFactoryMethodHandleForTopLink");
		factoryMethodHandleMapping.setGetMethodName("getFactoryMethodHandleForTopLink");
		factoryMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		factoryMethodHandleMapping.setXPath("factory-method-handle");
		descriptor.addMapping(factoryMethodHandleMapping);
		
		XMLCompositeObjectMapping instantiationMethodHandleMapping = new XMLCompositeObjectMapping();
		instantiationMethodHandleMapping.setAttributeName("instantiationMethodHandle");
		instantiationMethodHandleMapping.setSetMethodName("setInstantiationMethodHandleForTopLink");
		instantiationMethodHandleMapping.setGetMethodName("getInstantiationMethodHandleForTopLink");
		instantiationMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		instantiationMethodHandleMapping.setXPath("isntantiation-method-handle");
		descriptor.addMapping(instantiationMethodHandleMapping);
		
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWClassHandle getFactoryTypeHandleForTopLink() {
		return (this.factoryTypeHandle.getType() == null) ? null : this.factoryTypeHandle;
	}
	private void setFactoryTypeHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildFactoryTypeScrubber();
		this.factoryTypeHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getUseMethodHandleForTopLink() {
		return (this.useMethodHandle.getMethod() == null) ? null : this.useMethodHandle;
	}
	private void setUseMethodHandleForTopLink(MWMethodHandle handle) {
		NodeReferenceScrubber scrubber = this.buildUseMethodScrubber();
		this.useMethodHandle = ((handle == null) ? new MWMethodHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
	/**
	 * check for null
	 */
	private MWMethodHandle getFactoryMethodHandleForTopLink() {
		return (this.factoryMethodHandle.getMethod() == null) ? null : this.factoryMethodHandle;
	}
	private void setFactoryMethodHandleForTopLink(MWMethodHandle handle) {
		NodeReferenceScrubber scrubber = this.buildFactoryMethodScrubber();
		this.factoryMethodHandle = ((handle == null) ? new MWMethodHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
	/**
	 * check for null
	 */
	private MWMethodHandle getInstantiationMethodHandleForTopLink() {
		return (this.instantiationMethodHandle.getMethod() == null) ? null : this.instantiationMethodHandle;
	}
	private void setInstantiationMethodHandleForTopLink(MWMethodHandle handle) {
		NodeReferenceScrubber scrubber = this.buildInstantiationMethodScrubber();
		this.instantiationMethodHandle = ((handle == null) ? new MWMethodHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
	
}
