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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;


public final class MWCollectionContainerPolicy 
    extends MWModel 
    implements MWContainerPolicy {

    private DefaultingContainerClass containerClass;

	// **************** Static methods ****************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWCollectionContainerPolicy.class);
        
        descriptor.getInheritancePolicy().setParentClass(MWContainerPolicy.MWContainerPolicyRoot.class);
        
        XMLCompositeObjectMapping containerClassMapping = new XMLCompositeObjectMapping();
        containerClassMapping.setAttributeName("containerClass");
        containerClassMapping.setReferenceClass(DefaultingContainerClass.class);
        containerClassMapping.setXPath("container-class");
        descriptor.addMapping(containerClassMapping);
        		
		return descriptor;
	}


	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWCollectionContainerPolicy() {
		super();
	}

	public MWCollectionContainerPolicy(MWContainerMapping parent) {
		super(parent);
	}

	protected void initialize(Node parent) {
	    super.initialize(parent);
        this.containerClass = new DefaultingContainerClass(this);
	}
    
    protected void addChildrenTo(List list) {
        super.addChildrenTo(list);
        list.add(this.containerClass);
    }
    
    
    // ************ accessors ***************
    
    public DefaultingContainerClass getDefaultingContainerClass() {
        return this.containerClass;
    }

    private MWContainerMapping getContainerMapping() {
        return (MWContainerMapping) getParent();
    }
    
    private MWMapping getMapping() {
        return (MWMapping) getParent();
    } 
    
    private MWClassAttribute getInstanceVariable() {
        return ((MWMapping) getParent()).getInstanceVariable();
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

	public MWClass defaultContainerClass() {
		if (this.getContainerMapping().usesTransparentIndirection()) {
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
		
		if (attributeContainerType.isAssignableToCollection()) {
			if (attributeContainerType.isInstantiable()) {
				return attributeContainerType;
			}
		}
		return typeFor(Vector.class);
	}
	
	private MWClass defaultIndirectContainerClass() {
		return typeFor(IndirectList.class);
	}
	
	public void referenceDescriptorChanged(MWDescriptor newReferenceDescriptor) {
	    //nothing to do
	}
    
	// ************** Problem Handling ****************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		MWClass containerType = this.getDefaultingContainerClass().getContainerClass();
		if ((containerType != null) && ! containerType.mightBeAssignableToCollection()) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_COLLECTION));
		}
	}
	
	
	// **************** Runtime conversion *****************

	public ContainerPolicy runtimeContainerPolicy() {
        if (getContainerMapping().usesTransparentIndirection()) {
            return new ListContainerPolicy(getDefaultingContainerClass().getContainerClass().getName());
        }
		return new CollectionContainerPolicy(getDefaultingContainerClass().getContainerClass().getName());
	}
    
}
