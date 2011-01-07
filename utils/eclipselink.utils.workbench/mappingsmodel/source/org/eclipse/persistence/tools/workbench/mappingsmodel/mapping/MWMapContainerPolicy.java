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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;


public final class MWMapContainerPolicy extends MWModel implements MWContainerPolicy {

	private MWMethodHandle keyMethodHandle;
		public final static String KEY_METHOD_PROPERTY = "keyMethod";


    private DefaultingContainerClass containerClass;
    

	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	protected MWMapContainerPolicy() {
		super();
	}

	public MWMapContainerPolicy(MWMapContainerMapping parent) {
		super(parent);
	}


	// **************** Building and Initializing *************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.keyMethodHandle = new MWMethodHandle(this, this.buildKeyMethodScrubber());
        this.containerClass = new DefaultingContainerClass(this);
	}

	
	// **************** Accessors ****************

	public MWMethod getKeyMethod() {
		return this.keyMethodHandle.getMethod();
	}
	
	public void setKeyMethod(MWMethod newValue) {
		MWMethod oldValue = this.getKeyMethod();
		this.keyMethodHandle.setMethod(newValue);
		this.firePropertyChanged(KEY_METHOD_PROPERTY, oldValue, newValue);
	}

	
    // ************ accessors ***************
    
    public DefaultingContainerClass getDefaultingContainerClass() {
        return this.containerClass;
    }

    public MWMapContainerMapping getMapContainerMapping() {
        return (MWMapContainerMapping) getParent();
    }
    
    private MWClassAttribute getInstanceVariable() {
        return ((MWMapping) getParent()).getInstanceVariable();
    }
    
    public Iterator candidateKeyMethods() {
        MWDescriptor refDescriptor = this.getMapContainerMapping().getReferenceDescriptor();
        return (refDescriptor != null) ?
        	refDescriptor.getMWClass().candidateMapContainerPolicyKeyMethods()
        :
        	NullIterator.instance();
    }
 
    public boolean usesSorting() {
    	return false;
    }
    
    public void setUsesSorting(boolean sort) {
    	// do nothing currently only applies to set container policies
    }
    
	public MWClass getComparatorClass() {
		return null;
	}

	public void setComparatorClass(MWClass comparatorClass) {
		// do nothing currently only applies to set container policies
	}

	// **************** Behavior **********************************************

    public void referenceDescriptorChanged(MWDescriptor newReferenceDescriptor) {
		if (newReferenceDescriptor == null
			|| (getKeyMethod() != null && ! CollectionTools.contains(candidateKeyMethods(), getKeyMethod())))
		{
			setKeyMethod(null);
		}
	}

	public MWClass defaultContainerClass() {
		if (this.getMapContainerMapping().usesTransparentIndirection()) {
			return this.defaultIndirectContainerClass();
		}
				
		MWClassAttribute instanceVariable = this.getInstanceVariable();
		MWClass attributeContainerType;
			
		if (instanceVariable.isValueHolder()) {
			attributeContainerType = getInstanceVariable().getValueType();
		}
		else {
			attributeContainerType = getInstanceVariable().getType();
		}
		
		if (attributeContainerType.isAssignableToMap() && attributeContainerType.isInstantiable()) {
			return attributeContainerType;
		}
			
        return typeFor(HashMap.class);
	}
	
	private MWClass defaultIndirectContainerClass() {
		return typeFor(IndirectMap.class);
	}


	// ************** Containment Hierarchy ****************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
        children.add(this.keyMethodHandle);
        children.add(this.containerClass);
	}
	
	private NodeReferenceScrubber buildKeyMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMapContainerPolicy.this.setKeyMethod(null);
			}
			public String toString() {
				return "MWMapContainerPolicy.buildKeyMethodScrubber()";
			}
		};
	}

	
	// ************** Problem Handling ****************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);

		MWMethod keyMethod = this.getKeyMethod();
		if (keyMethod == null) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_KEY_METHOD_NOT_SPECIFIED));
		} else {
			this.checkKeyMethod(keyMethod, currentProblems);
		}
		this.checkContainerClass(currentProblems);
	}

	private void checkKeyMethod(MWMethod keyMethod, List currentProblems) {
		if (keyMethod.isCandidateMapContainerPolicyKeyMethod()) {
			if ( ! CollectionTools.contains(this.candidateKeyMethods(), keyMethod)) {
				// the method, by itself, is OK but it's not in the right hierarchy
				currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_KEY_METHOD_NOT_VISIBLE));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_KEY_METHOD_NOT_VALID));
		}
	}

	protected void checkContainerClass(List currentProblems) {
		MWClass containerType = this.getDefaultingContainerClass().getContainerClass();
		if ((containerType != null) && ! containerType.mightBeAssignableToMap()) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_MAP));
		}
	}


	// **************** Runtime conversion ************************************
    
    public ContainerPolicy runtimeContainerPolicy() {
 		ContainerPolicy containerPolicy = new MapContainerPolicy(getDefaultingContainerClass().getContainerClass().getName());
		String keyMethodName = this.getKeyMethod() != null ? this.getKeyMethod().getName() : null;
		((MapContainerPolicy) containerPolicy).setKeyName(keyMethodName);
		//((MapContainerPolicy) containerPolicy).setKeyMethodName(keyMethodName);
		return containerPolicy;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWMapContainerPolicy.class);
        
        descriptor.getInheritancePolicy().setParentClass(MWContainerPolicy.MWContainerPolicyRoot.class);
		
		XMLCompositeObjectMapping keyMethodHandleMapping = new XMLCompositeObjectMapping();
		keyMethodHandleMapping.setAttributeName("keyMethodHandle");
		keyMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		keyMethodHandleMapping.setSetMethodName("setKeyMethodHandleForTopLink");
		keyMethodHandleMapping.setGetMethodName("getKeyMethodHandleForTopLink");
		keyMethodHandleMapping.setXPath("key-method-handle");
		descriptor.addMapping(keyMethodHandleMapping);
	           
        XMLCompositeObjectMapping containerClassMapping = new XMLCompositeObjectMapping();
        containerClassMapping.setAttributeName("containerClass");
        containerClassMapping.setReferenceClass(DefaultingContainerClass.class);
        containerClassMapping.setXPath("container-class");
        descriptor.addMapping(containerClassMapping);
                    
        return descriptor;
	}

	private MWMethodHandle getKeyMethodHandleForTopLink() {
		return (this.keyMethodHandle.getMethod() == null) ? null : this.keyMethodHandle;
	}
	
	private void setKeyMethodHandleForTopLink(MWMethodHandle keyMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildKeyMethodScrubber();
		this.keyMethodHandle = ((keyMethodHandle == null) ? new MWMethodHandle(this, scrubber) : keyMethodHandle.setScrubber(scrubber));
	}
	
}
