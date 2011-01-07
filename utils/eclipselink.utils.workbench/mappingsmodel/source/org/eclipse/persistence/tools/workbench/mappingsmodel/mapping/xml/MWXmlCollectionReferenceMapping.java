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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWXmlCollectionReferenceMapping extends MWAbstractXmlReferenceMapping implements MWContainerMapping {

    private MWContainerPolicy containerPolicy;

    // **************** Constructors ******************************************

	/**
	 * Default constructor, TopLink use only.
	 */
	private MWXmlCollectionReferenceMapping() {
		super();
	}

	MWXmlCollectionReferenceMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

    @Override
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
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.containerPolicy);
	}

	// **************** Morphing **********************************************
	
	public MWXmlCollectionReferenceMapping asMWXmlCollectionReferenceMapping() {
		return this;
	}

	@Override
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWXmlCollectionReferenceMapping(this);
	}

	// **************** Runtime Conversion ************************************
	
	@Override
	public DatabaseMapping buildRuntimeMapping() {
		return new XMLCollectionReferenceMapping();
	}
	
	@Override
	public DatabaseMapping runtimeMapping() {
		XMLCollectionReferenceMapping mapping = (XMLCollectionReferenceMapping) super.runtimeMapping();
        mapping.setContainerPolicy(getContainerPolicy().runtimeContainerPolicy());
		return mapping;
	}

	// **************** TopLink methods ***************************************
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlCollectionReferenceMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractXmlReferenceMapping.class);
		
        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("containerPolicy");
        containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
        containerPolicyMapping.setGetMethodName("getContainerPolicyForToplink");
        containerPolicyMapping.setSetMethodName("setContainerPolicyForToplink");
        containerPolicyMapping.setXPath("container-policy");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;	
	}
		
	@Override
	public boolean sourceFieldMayUseCollectionXpath() {
		return true;
	}
	
    private MWContainerPolicy getContainerPolicyForToplink() {
        return this.containerPolicy;
    }
    
    private void setContainerPolicyForToplink(MWContainerPolicy containerPolicy) {
        this.containerPolicy = containerPolicy;
    }

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
        MWCollectionContainerPolicy containerPolicy = new MWCollectionContainerPolicy(this);
        this.setContainerPolicy(containerPolicy);
        return containerPolicy;
    }
    
    public MWListContainerPolicy setListContainerPolicy() {
        if (this.containerPolicy instanceof MWListContainerPolicy) {
            return (MWListContainerPolicy) this.containerPolicy;
        }
        MWListContainerPolicy containerPolicy = new MWListContainerPolicy(this);
        this.setContainerPolicy(containerPolicy);
        return containerPolicy;
    }
    
    public MWSetContainerPolicy setSetContainerPolicy() {
        if (this.containerPolicy instanceof MWSetContainerPolicy) {
            return (MWSetContainerPolicy) this.containerPolicy;
        }
        MWSetContainerPolicy containerPolicy = new MWSetContainerPolicy(this);
        this.setContainerPolicy(containerPolicy);
        return containerPolicy;
    }

    public boolean usesTransparentIndirection() {
     	return false;
    }
}
