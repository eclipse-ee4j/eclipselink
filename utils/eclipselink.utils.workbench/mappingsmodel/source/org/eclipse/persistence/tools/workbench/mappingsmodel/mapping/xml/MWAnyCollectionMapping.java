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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWAnyCollectionMapping 
	extends MWAbstractAnyMapping
    implements MWContainerMapping, MWXmlElementTypeableMapping
{
	// **************** Variables *********************************************
	
	private volatile MWContainerPolicy containerPolicy;
	
	private MWNamedSchemaComponentHandle elementTypeHandle;
		public final static String ELEMENT_TYPE_PROPERTY = "elementType";

	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWAnyCollectionMapping() {
		super();
	}
	
	MWAnyCollectionMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	// **************** Initialization ****************************************
    
    protected void initialize(MWClassAttribute attribute, String name) {
        super.initialize(attribute, name);
        
        if (attribute.isAssignableToList()) {
            this.containerPolicy = new MWListContainerPolicy(this);
        }
        else if (attribute.isAssignableToSet()) {
            this.containerPolicy = new MWSetContainerPolicy(this);
        }
        else if (attribute.isAssignableToCollection()){
            this.containerPolicy = new MWCollectionContainerPolicy(this);
        }
        else { //This is the default in the runtime
            this.containerPolicy = new MWListContainerPolicy(this);
        }
    }
    
    @Override
    protected void initialize(Node parent) {
    	super.initialize(parent);
		this.elementTypeHandle = new MWNamedSchemaComponentHandle(this, this.buildElementTypeScrubber());
    }
    
	private NodeReferenceScrubber buildElementTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAnyCollectionMapping.this.setElementType(null);
			}
			public String toString() {
				return "MWAbstractCompositeMapping.buildElementTypeScrubber()";
			}
		};
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.containerPolicy);
		children.add(this.elementTypeHandle);
	}
	
	// **************** Element type ******************************************
	
	public MWComplexTypeDefinition getElementType() {
		return (MWComplexTypeDefinition) this.elementTypeHandle.getComponent();
	}
	
	public void setElementType(MWComplexTypeDefinition newElementType) {
		MWComplexTypeDefinition oldElementType = this.getElementType();
		this.elementTypeHandle.setComponent(newElementType);
		this.firePropertyChanged(ELEMENT_TYPE_PROPERTY, oldElementType, newElementType);
	}

	// **************** Container policy **************************************
	
	public MWContainerPolicy getContainerPolicy() {
		return this.containerPolicy;
	}
    private void setContainerPolicy(MWContainerPolicy containerPolicy) {
        Object oldValue = this.containerPolicy;
        this.containerPolicy = containerPolicy;
        firePropertyChanged(CONTAINER_POLICY_PROPERTY, oldValue, containerPolicy);
    }
    
    public MWCollectionContainerPolicy setCollectionContainerPolicy() {
        if (this.containerPolicy instanceof MWCollectionContainerPolicy) {
            return (MWCollectionContainerPolicy) this.containerPolicy;
        }
        MWCollectionContainerPolicy cp = new MWCollectionContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
    
    public MWListContainerPolicy setListContainerPolicy() {
        if (this.containerPolicy instanceof MWListContainerPolicy) {
            return (MWListContainerPolicy) this.containerPolicy;
        }
        MWListContainerPolicy cp = new MWListContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
    
    public MWSetContainerPolicy setSetContainerPolicy() {
        if (this.containerPolicy instanceof MWSetContainerPolicy) {
            return (MWSetContainerPolicy) this.containerPolicy;
        }
        MWSetContainerPolicy cp = new MWSetContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
	  
    public boolean usesTransparentIndirection() {
        return false;
    }
    
    
	// **************** MWXpathContext implementation *************************
	
	protected boolean mayUseCollectionData() {
		return true;
	}
	
	
	// **************** Morphing **********************************************
	
	public MWAnyCollectionMapping asAnyCollectionMapping() {
		return this;
	}
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWAnyCollectionMapping(this);
	}
    
    public void initializeFromMWCompositeCollectionMapping(MWCompositeCollectionMapping oldMapping) {
        super.initializeFromMWCompositeCollectionMapping(oldMapping);
        if (oldMapping.getContainerPolicy().getDefaultingContainerClass().usesDefaultContainerClass()) {
            this.containerPolicy.getDefaultingContainerClass().setContainerClass(oldMapping.getContainerPolicy().getDefaultingContainerClass().getContainerClass());
        }
    }

	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new XMLAnyCollectionMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		XMLAnyCollectionMapping runtimeMapping = (XMLAnyCollectionMapping) super.runtimeMapping();
		runtimeMapping.setField(this.getXmlField().runtimeField());
        runtimeMapping.setContainerPolicy(getContainerPolicy().runtimeContainerPolicy());
		if (this.getElementType() != null && runtimeMapping.getField() != null) {
			((XMLField)runtimeMapping.getField()).setLeafElementType(new QName(this.getElementType().qName()));
		}
		return runtimeMapping;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAnyCollectionMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWAbstractAnyMapping.class);
		
		XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
		containerPolicyMapping.setAttributeName("containerPolicy");
		containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
		containerPolicyMapping.setXPath("container-policy");
		descriptor.addMapping(containerPolicyMapping);
		
		XMLCompositeObjectMapping elementTypeHandleMapping = new XMLCompositeObjectMapping();
		elementTypeHandleMapping.setAttributeName("elementTypeHandle");
		elementTypeHandleMapping.setGetMethodName("getElementTypeHandleForTopLink");
		elementTypeHandleMapping.setSetMethodName("setElementTypeHandleForTopLink");
		elementTypeHandleMapping.setReferenceClass(MWNamedSchemaComponentHandle.class);
		elementTypeHandleMapping.setXPath("element-type-handle");
		descriptor.addMapping(elementTypeHandleMapping);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWNamedSchemaComponentHandle getElementTypeHandleForTopLink() {
		return (this.elementTypeHandle.getComponent() == null) ? null : this.elementTypeHandle;
	}
	private void setElementTypeHandleForTopLink(MWNamedSchemaComponentHandle handle) {
		NodeReferenceScrubber scrubber = this.buildElementTypeScrubber();
		this.elementTypeHandle = ((handle == null) ? new MWNamedSchemaComponentHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
