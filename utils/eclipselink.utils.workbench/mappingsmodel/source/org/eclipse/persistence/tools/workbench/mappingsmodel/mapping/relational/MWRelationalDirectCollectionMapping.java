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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Record;


public final class MWRelationalDirectCollectionMapping 
	extends MWRelationalDirectContainerMapping
	implements MWDirectCollectionMapping, MWContainerMapping
{
	
    private MWContainerPolicy containerPolicy;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWRelationalDirectCollectionMapping() {
		super();
	}
	
	MWRelationalDirectCollectionMapping(MWRelationalClassDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
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
    
    
    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.containerPolicy);
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

    protected MWClass conatinerPolicyClass() {
        return getContainerPolicy().getDefaultingContainerClass().getContainerClass();
    }
    
	// **************** MWRelationalDirectContainerMapping implementation **************
	
    protected int automapNonPrimaryKeyColumnsSize() {
    	return 1;
    }
    
	// **************** Morphing **************
	
	public MWDirectCollectionMapping asMWDirectCollectionMapping() {
		return this;
	}
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWRelationalDirectCollectionMapping(this);
	}
	
	protected void initializeFromMWCollectionMapping(MWCollectionMapping oldMapping) {
		super.initializeFromMWCollectionMapping(oldMapping);
	
		if (oldMapping.getContainerPolicy().getDefaultingContainerClass().getContainerClass().isAssignableToCollection()) {
			this.getContainerPolicy().getDefaultingContainerClass().setContainerClass(oldMapping.getContainerPolicy().getDefaultingContainerClass().getContainerClass());
		}
	}
				
	// **************** MWQueryable interface ***********************
	
	public String iconKey() {
		return "mapping.directCollection";
	}

	
	// **************** runtime *************
	
	
    public DatabaseMapping runtimeMapping() {   
        DirectCollectionMapping runtimeMapping = (DirectCollectionMapping) super.runtimeMapping();
        
        runtimeMapping.setContainerPolicy(this.containerPolicy.runtimeContainerPolicy());
        
        return runtimeMapping;
    }
	

    // **************** TopLink methods **********************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWRelationalDirectCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWRelationalDirectContainerMapping.class);
        
        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("containerPolicy");
        containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
        containerPolicyMapping.setXPath("container-policy");
        descriptor.addMapping(containerPolicyMapping);
        
        return descriptor;
	}
    	   
}

