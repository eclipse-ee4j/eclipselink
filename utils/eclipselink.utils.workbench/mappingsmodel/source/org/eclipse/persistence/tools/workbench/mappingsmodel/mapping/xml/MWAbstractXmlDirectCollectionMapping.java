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

import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public abstract class MWAbstractXmlDirectCollectionMapping
	extends MWDirectContainerMapping
	implements MWXmlMapping, MWXpathedMapping, MWXpathContext, MWDirectCollectionMapping, MWContainerMapping
{
	// **************** Variables *********************************************
	
	/** Aggregately mapped, so no property change */
	private MWXmlField xmlField;
	
    private MWContainerPolicy containerPolicy;
    
    
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWAbstractXmlDirectCollectionMapping() {
		super();
	}
	
	MWAbstractXmlDirectCollectionMapping(MWXmlDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}
	
	
	// **************** Initialization ****************************************
	
	@Override
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.xmlField = new MWXmlField(this);
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
		children.add(this.xmlField);
        children.add(this.containerPolicy);
	}
	
	
    public MWContainerPolicy getContainerPolicy() {
        return this.containerPolicy;
    }
    
    private void setContainerPolicy(MWContainerPolicy containerPolicy) {
        Object oldValue = this.containerPolicy;
        this.containerPolicy = containerPolicy;
        firePropertyChanged(CONTAINER_POLICY_PROPERTY, oldValue, containerPolicy);
    }
 
    private MWContainerPolicy getContainerPolicyForToplink() {
        return this.containerPolicy;
    }
    
    private void setContainerPolicyForToplink(MWContainerPolicy containerPolicy) {
        this.containerPolicy = containerPolicy;
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
    
    // **************** MWXpathedMapping implementation  **********************
	
	public MWXmlField getXmlField() {
		return this.xmlField;
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWXmlField firstMappedXmlField() {
		if (! "".equals(this.getXmlField().getXpath())) {
			return this.getXmlField();
		}	
        return null;
	}
	
	@Override
	public void addWrittenFieldsTo(Collection writtenXpaths) {
		if (! this.isReadOnly() && ! "".equals(this.getXmlField().getXpath())) {
			writtenXpaths.add(this.getXmlField());
		}
	}
	
	// **************** MWXmlMapping contract *********************************
	
	public MWSchemaContextComponent schemaContext() {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	// **************** MWXpathContext implementation  ************************
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.xmlDescriptor().getSchemaContext();
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec();
	}
	
	protected MWXpathSpec buildXpathSpec() {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return true;
			}
			
			public boolean mayUseComplexData() {
				return false;
			}
			
			public boolean mayUseSimpleData() {
				return true;
			}
		};
	}
	
	
	// **************** MWDirectMapping implementation ****************
	
	@Override
	protected MWTypeConversionConverter buildTypeConversionConverter() {
		return new MWXmlTypeConversionConverter(this);
	}
		
	// **************** Convenience *******************************************
	
	protected MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.getParent();
	}
	
	
	// **************** Morphing **********************************************
	
	@Override
	protected void initializeFromMWXpathedMapping(MWXpathedMapping oldMapping) {
		super.initializeFromMWXpathedMapping(oldMapping);
		this.getXmlField().setXpath(oldMapping.getXmlField().getXpath());
		this.getXmlField().setTyped(oldMapping.getXmlField().isTyped());
	}
	
	
	// **************** Problem handling **************************************
	
	@Override
	protected void addProblemsTo(List newProblems) {
		// would like to add xpath problems first
		this.addXpathNotSpecifiedProblemTo(newProblems);
		this.addXmlFieldNotDirectProblemTo(newProblems);
		super.addProblemsTo(newProblems);
	}
	
	private void addXpathNotSpecifiedProblemTo(List newProblems) {
		if (! this.getXmlField().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SPECIFIED));
		}
	}
	
	private void addXmlFieldNotDirectProblemTo(List newProblems) {
		if (this.getXmlField().isValid() && ! this.getXmlField().isDirect()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_DIRECT, this.getXmlField().getXpath()));
		}
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		this.xmlField.resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		this.xmlField.schemaChanged(change);
	}
	
	
	// **************** Runtime conversion ************************************
	
	@Override
	protected DatabaseMapping buildRuntimeMapping() {
		return this.xmlDescriptor().buildDefaultRuntimeDirectCollectionMapping();
	}
	
	@Override
	public DatabaseMapping runtimeMapping() {
		AbstractCompositeDirectCollectionMapping runtimeMapping = 
			(AbstractCompositeDirectCollectionMapping) super.runtimeMapping();
		// need to set the converter here, because it can't be done *generally* in the superclass
		runtimeMapping.setValueConverter(this.getConverter().runtimeConverter(runtimeMapping));
		runtimeMapping.setField(this.getXmlField().runtimeField());
        
        runtimeMapping.setContainerPolicy(containerPolicy.runtimeContainerPolicy());
		return runtimeMapping;
	}
	
	
	// **************** TopLink Methods *****************
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractXmlDirectCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWDirectContainerMapping.class);
		
		XMLCompositeObjectMapping xmlFieldMapping = new XMLCompositeObjectMapping();
		xmlFieldMapping.setReferenceClass(MWXmlField.class);
		xmlFieldMapping.setAttributeName("xmlField");
		xmlFieldMapping.setGetMethodName("getXmlFieldForTopLink");
		xmlFieldMapping.setSetMethodName("setXmlFieldForTopLink");
		xmlFieldMapping.setXPath("xpath");
		descriptor.addMapping(xmlFieldMapping);
		
        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("containerPolicy");
        containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
        containerPolicyMapping.setGetMethodName("getContainerPolicyForToplink");
        containerPolicyMapping.setSetMethodName("setContainerPolicyForToplink");
        containerPolicyMapping.setXPath("container-policy");
        descriptor.addMapping(containerPolicyMapping);
        
        return descriptor;	
	}
	
	private MWXmlField getXmlFieldForTopLink() {
		return (this.xmlField.isSpecified()) ? this.xmlField: null;
	}
	
	private void setXmlFieldForTopLink(MWXmlField xmlField) {
		this.xmlField = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
