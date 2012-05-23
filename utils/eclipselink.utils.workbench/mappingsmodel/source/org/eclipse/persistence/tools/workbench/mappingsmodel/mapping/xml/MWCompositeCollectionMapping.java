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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;

public final class MWCompositeCollectionMapping
	extends MWAbstractCompositeMapping
    implements MWMapContainerMapping
{
	// **************** Variables *********************************************
	
	private volatile MWContainerPolicy containerPolicy;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWCompositeCollectionMapping() {
		super();
	}
	
	MWCompositeCollectionMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	// **************** Initialization ****************************************

    protected void initialize(MWClassAttribute attribute, String name) {
        super.initialize(attribute, name);
        
        if (attribute.isAssignableToMap()) {
            this.containerPolicy = new MWMapContainerPolicy(this);
        }
        else if (attribute.isAssignableToList()) {
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
	
	public MWMapContainerPolicy setMapContainerPolicy() {
		if (this.containerPolicy instanceof MWMapContainerPolicy) {
			return (MWMapContainerPolicy) this.containerPolicy;
		}
		MWMapContainerPolicy cp = new MWMapContainerPolicy(this);
		this.setContainerPolicy(cp);
		return cp;
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
    
	public Iterator candidateKeyMethods(Filter keyMethodFilter) {
		if (this.getReferenceDescriptor() != null) {
			return new FilteringIterator(this.getReferenceDescriptor().getMWClass().allInstanceMethods(), keyMethodFilter);
		}
			
        return NullIterator.instance();
	}
	  
    public boolean usesTransparentIndirection() {
        return false;
    }
    
	// **************** MWXpathContext implementation *************************
	
	protected boolean mayUseCollectionData() {
		return true;
	}
	
	
	// **************** Morphing **********************************************
	
	public MWCompositeCollectionMapping asCompositeCollectionMapping() {
		return this;
	}
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWCompositeCollectionMapping(this);
	}
	
    public void initializeFromMWAnyCollectionMapping(MWAnyCollectionMapping oldMapping) {
        super.initializeFromMWAnyCollectionMapping(oldMapping);
        this.getContainerPolicy().getDefaultingContainerClass().setContainerClass(oldMapping.getContainerPolicy().getDefaultingContainerClass().getContainerClass());
    }
	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return this.xmlDescriptor().buildDefaultRuntimeCompositeCollectionMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		AbstractCompositeCollectionMapping runtimeMapping = 
			(AbstractCompositeCollectionMapping) super.runtimeMapping();
		ClassTools.invokeMethod(runtimeMapping, "setField", new Class[] {DatabaseField.class}, new Object[] {this.getXmlField().runtimeField()});
        runtimeMapping.setContainerPolicy(this.containerPolicy.runtimeContainerPolicy());
        
        if (runtimeMapping.isXMLMapping()) {
        	this.getContainerAccessor().adjustRuntimeMapping(runtimeMapping);
        }
        
		return runtimeMapping;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWCompositeCollectionMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWAbstractCompositeMapping.class);
		
		XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
		containerPolicyMapping.setAttributeName("containerPolicy");
		containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
		containerPolicyMapping.setXPath("container-policy");
		descriptor.addMapping(containerPolicyMapping);
		
		return descriptor;
	}
}
