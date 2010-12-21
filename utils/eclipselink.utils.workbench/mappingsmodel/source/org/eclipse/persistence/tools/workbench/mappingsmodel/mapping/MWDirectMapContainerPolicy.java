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

import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWDirectMapContainerPolicy extends MWModel implements MWContainerPolicy {

    private DefaultingContainerClass containerClass;
    
	// ********** Static Methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDirectMapContainerPolicy.class);

        XMLCompositeObjectMapping containerClassMapping = new XMLCompositeObjectMapping();
        containerClassMapping.setAttributeName("containerClass");
        containerClassMapping.setReferenceClass(DefaultingContainerClass.class);
        containerClassMapping.setXPath("container-class");
        descriptor.addMapping(containerClassMapping);
        
		return descriptor;
	}


	// ********** Constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */

	private MWDirectMapContainerPolicy() {
		super();
	}

	public MWDirectMapContainerPolicy(MWRelationalDirectMapMapping parent) {
		super(parent);
	}

    // **************** Building and Initializing *************
    
    protected void initialize(Node parent) {
        super.initialize(parent);
        this.containerClass = new DefaultingContainerClass(this);
    }

    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.containerClass);
    }
    
    public DefaultingContainerClass getDefaultingContainerClass() {
        return this.containerClass;
    }
  
	//TODO copied from MWMapContainerPolicy
	public MWClass defaultContainerClass() {
		if (this.getMapping().usesTransparentIndirection()) {
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

    private MWRelationalDirectMapMapping getMapping() {
        return (MWRelationalDirectMapMapping) getParent();
    }
    
    private MWClassAttribute getInstanceVariable() {
        return getMapping().getInstanceVariable();
    }
    
    public boolean usesSorting() {
    	return false;
    }
    
    public void setUsesSorting(boolean sort) {
    	// do nothing currently only applies to set container policies
    }
       
    public void referenceDescriptorChanged(MWDescriptor newReferenceDescriptor) {
        // this should probably never be called
    }

	public MWClass getComparatorClass() {
		return null;
	}

	public void setComparatorClass(MWClass comparatorClass) {
		// do nothing currently only applies to set container policies
	}

	// **************** Problem Handling **************************************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		MWClass containerType = this.getDefaultingContainerClass().getContainerClass();
		if ((containerType != null) && ! containerType.mightBeAssignableToMap()) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_MAP));
		}
	}
	
	
	// **************** Runtime Conversion ************************************
	
	public ContainerPolicy runtimeContainerPolicy() {
		MappedKeyMapContainerPolicy containerPolicy = new MappedKeyMapContainerPolicy();
		containerPolicy.setContainerClassName(getDefaultingContainerClass().getContainerClass().getName());
		return containerPolicy;
	}

}
