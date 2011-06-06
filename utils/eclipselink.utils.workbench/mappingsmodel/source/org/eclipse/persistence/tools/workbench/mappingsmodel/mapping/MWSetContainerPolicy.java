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

import java.util.HashSet;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


public final class MWSetContainerPolicy 
    extends MWModel 
    implements MWContainerPolicy {

    private DefaultingContainerClass containerClass;
    
    private boolean usesSorting;
   
    // only used with sorting
    private MWClassHandle comparatorClass;

	// **************** Static methods ****************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWSetContainerPolicy.class);
        
        descriptor.getInheritancePolicy().setParentClass(MWContainerPolicy.MWContainerPolicyRoot.class);
        
        XMLCompositeObjectMapping containerClassMapping = new XMLCompositeObjectMapping();
        containerClassMapping.setAttributeName("containerClass");
        containerClassMapping.setReferenceClass(DefaultingContainerClass.class);
        containerClassMapping.setXPath("container-class");
        descriptor.addMapping(containerClassMapping);
        		
		XMLDirectMapping usesSortingMapping = new XMLDirectMapping();
		usesSortingMapping.setAttributeName("usesSorting");
		usesSortingMapping.setXPath("uses-sorting/text()");
		usesSortingMapping.setNullValue(Boolean.FALSE);
		descriptor.addMapping(usesSortingMapping);

		XMLCompositeObjectMapping comparatorClassMapping = new XMLCompositeObjectMapping();
		comparatorClassMapping.setAttributeName("comparatorClass");
		comparatorClassMapping.setSetMethodName("setComparatorClassHandleForTopLink");
		comparatorClassMapping.setGetMethodName("getComparatorClassHandleForTopLink");
		comparatorClassMapping.setReferenceClass(MWClassHandle.class);
		comparatorClassMapping.setXPath("comparator-class-handle");
		descriptor.addMapping(comparatorClassMapping);

		return descriptor;
	}

	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();
		descriptor.setJavaClass(MWSetContainerPolicy.class);
        
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
	private MWSetContainerPolicy() {
		super();
	}

	public MWSetContainerPolicy(MWContainerMapping parent) {
		super(parent);
	}
	
	protected void initialize(Node parent) {
	    super.initialize(parent);
        this.containerClass = new DefaultingContainerClass(this);
        this.comparatorClass = new MWClassHandle(this, buildComparatorTypeScrubber());
	}

	private NodeReferenceScrubber buildComparatorTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				setComparatorClass(null);
			}
			public String toString() {
				return "MWSetContainerPolicy.buildComparatorTypeScrubber()";
			}
		};
	}

	protected void addChildrenTo(List list) {
        super.addChildrenTo(list);
        list.add(this.containerClass);
        list.add(this.comparatorClass);
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
		return usesSorting;
	}

	public void setUsesSorting(boolean sort) {
		boolean oldValue = this.usesSorting;
		this.usesSorting = sort;
		firePropertyChanged(SORT_PROPERTY, oldValue, this.usesSorting);
	}

	public MWClass getComparatorClass() {
		return comparatorClass.getType();
	}

	public void setComparatorClass(MWClass comparatorClass) {
		MWClass oldValue = this.comparatorClass.getType();
		this.comparatorClass.setType(comparatorClass);
		firePropertyChanged(COMPARATOR_CLASS_PROPERTY, oldValue, this.comparatorClass.getType());
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
		
        if (attributeContainerType.isAssignableToSet()) {
            if (attributeContainerType.isInstantiable()) {
                return attributeContainerType;
            }
        }
        
		return typeFor(HashSet.class);
	}
	
	private MWClass defaultIndirectContainerClass() {
		return typeFor(IndirectSet.class); 
	}
	
	public void referenceDescriptorChanged(MWDescriptor newReferenceDescriptor) {
	    //nothing to do
	}
    
	// ************** Problem Handling ****************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		MWClass containerType = this.getDefaultingContainerClass().getContainerClass();
		if ((containerType != null) && ! containerType.mightBeAssignableToSet()) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_SET));
		}
		if (this.usesSorting() && containerType != null && !containerType.mightBeAssignableToSortedSet()) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_SORTED_SET));
		}
		MWClass comparatorType = this.getComparatorClass();
		if (this.usesSorting() && comparatorType == null) {
			currentProblems.add(this.buildProblem(ProblemConstants.USES_SORTING_NO_COMPARATOR_CLASS_SELECTED));
		}
		if (this.usesSorting() && comparatorType != null && !comparatorType.mightBeAssignableToComparator()) {
			currentProblems.add(this.buildProblem(ProblemConstants.COMPARATOR_CLASS_NOT_COMPARATOR));
		}
	}
	
	
	// **************** Runtime conversion *****************

	public ContainerPolicy runtimeContainerPolicy() {
		return new CollectionContainerPolicy(getDefaultingContainerClass().getContainerClass().getName());
	}
    
    
    // **************** TopLink methods ************************

	@Override
	protected void legacy60PostBuild(DescriptorEvent event) {
		super.legacy60PostBuild(event);
        this.comparatorClass = new MWClassHandle(this, buildComparatorTypeScrubber());
	}
   
	/**
	 * check for null
	 */
	private MWClassHandle getComparatorClassHandleForTopLink() {
		return (this.comparatorClass.getType() == null) ? null : this.comparatorClass;
	}
	private void setComparatorClassHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildComparatorTypeScrubber();
		this.comparatorClass = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
