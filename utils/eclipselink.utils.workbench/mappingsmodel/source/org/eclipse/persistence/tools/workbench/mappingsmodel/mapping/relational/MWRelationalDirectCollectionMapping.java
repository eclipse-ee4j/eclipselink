/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
import deprecated.sdk.SDKAggregateObjectMapping;
import deprecated.sdk.SDKFieldValue;
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
	
	
	public static ClassDescriptor legacy50BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWRelationalDirectCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		SDKAggregateObjectMapping directValueColumnHandleMapping = new SDKAggregateObjectMapping();
		directValueColumnHandleMapping.setAttributeName("directValueColumnHandle");
		directValueColumnHandleMapping.setGetMethodName("getDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setSetMethodName("setDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		directValueColumnHandleMapping.setFieldName("direct-field-handle");
		descriptor.addMapping(directValueColumnHandleMapping);
		
		SDKAggregateObjectMapping referenceHandleMapping = new SDKAggregateObjectMapping();
		referenceHandleMapping.setAttributeName("referenceHandle");
		referenceHandleMapping.setGetMethodName("getReferenceHandleForTopLink");
		referenceHandleMapping.setSetMethodName("setReferenceHandleForTopLink");
		referenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		referenceHandleMapping.setFieldName("table-reference-mapping-reference-handle");
		descriptor.addMapping(referenceHandleMapping);
		
		XMLTransformationMapping containerPolicyMapping = new XMLTransformationMapping();
		containerPolicyMapping.setAttributeName("containerPolicy");
		containerPolicyMapping.setAttributeTransformation("legacy50GetContainerPolicyFromRow");
		descriptor.addMapping(containerPolicyMapping);
		
		XMLTransformationMapping converterMapping = new XMLTransformationMapping();
		converterMapping.setAttributeName("directValueConverter");
		converterMapping.setAttributeTransformation("legacyGetConverterForTopLink");
		descriptor.addMapping(converterMapping);
			
		XMLTransformationMapping indirectionTypeMapping = new XMLTransformationMapping();
		indirectionTypeMapping.setAttributeName("indirectionType");
		indirectionTypeMapping.setAttributeTransformation("legacy50GetIndirectionTypeFromRowForTopLink");
		descriptor.addMapping(indirectionTypeMapping);
	
		descriptor.addDirectMapping("batchReading","uses-batch-reading");
		
		return descriptor;
	}	
	
	private String legacy50GetIndirectionTypeFromRowForTopLink(Record row) {
		SDKFieldValue indirectionPolicy = (SDKFieldValue) row.get("collection-mapping-indirection-policy");
		String usesIndirection = (String) ((Record) indirectionPolicy.getElements().get(0)).get("uses-indirection");
		String usesTransparentIndirection = (String) ((Record) indirectionPolicy.getElements().get(0)).get("uses-transparent-indirection");
		
		if (usesIndirection.equals("true")) {
			if (usesTransparentIndirection.equals("true")) {
				return TRANSPARENT_INDIRECTION;
			}
			return VALUE_HOLDER_INDIRECTION;
		}
		return NO_INDIRECTION;
	}

    private MWContainerPolicy legacy50GetContainerPolicyFromRow(Record row) {
        MWContainerPolicy containerPolicy = new MWCollectionContainerPolicy(this);
        String usesDefaultContainerClass = (String) row.get("uses-default-container-class");
        this.legacyValuesMap.put("usesDefaultContainerClass", usesDefaultContainerClass);
       
        if (! (usesDefaultContainerClass.equals("true"))) {
            String containerClass = (String) row.get("container-class");
            this.legacyValuesMap.put("containerClass", containerClass);
        }
        
        return containerPolicy;
    }
    
    public void legacy50PostPostProjectBuild() {
        super.legacy50PostPostProjectBuild();
        String usesDefaultContainerClass = (String) this.legacyValuesMap.remove("usesDefaultContainerClass");
        if (usesDefaultContainerClass.equals("false")) {
            this.containerPolicy.getDefaultingContainerClass().setUseDefaultContainerClass(false);
        }
        String containerTypeString = (String) this.legacyValuesMap.remove("containerClass");
        legacyAdjustContainerPolicy(containerTypeString);
    }

    private void legacyAdjustContainerPolicy(String containerTypeString) {
        if (containerTypeString != null) {//will be null if usesDefaultContainerClass is true
            MWClass containerType = typeNamed(containerTypeString);
            if (containerType.isAssignableToSet()) {
                setSetContainerPolicy();
            }
            else if (containerType.isAssignableToList() && !usesTransparentIndirection()) {
                setListContainerPolicy();
            }
            else if (!containerType.isAssignableToCollection()) {
                this.containerPolicy = new MWListContainerPolicy(this);
            }
            this.containerPolicy.getDefaultingContainerClass().setContainerClass(containerType);
        }
        else if (this.containerPolicy instanceof MWCollectionContainerPolicy){
            if (getInstanceVariable().isAssignableToSet()) {
                this.containerPolicy = new MWSetContainerPolicy(this);
            }
            else if (getInstanceVariable().isAssignableToList() && !usesTransparentIndirection()) {
                this.containerPolicy = new MWListContainerPolicy(this);
            }
            else if (!getInstanceVariable().isAssignableToCollection()) {
                this.containerPolicy = new MWListContainerPolicy(this);
            }
        }
    }
    
	
	public static ClassDescriptor legacy45BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWRelationalDirectCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		SDKAggregateObjectMapping directValueColumnHandleMapping = new SDKAggregateObjectMapping();
		directValueColumnHandleMapping.setAttributeName("directValueColumnHandle");
		directValueColumnHandleMapping.setGetMethodName("getDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setSetMethodName("setDirectValueColumnHandleForTopLink");
		directValueColumnHandleMapping.setReferenceClass(MWColumnHandle.class);
		directValueColumnHandleMapping.setFieldName("fieldHandle");
		descriptor.addMapping(directValueColumnHandleMapping);
		
		SDKAggregateObjectMapping referenceHandleMapping = new SDKAggregateObjectMapping();
		referenceHandleMapping.setAttributeName("referenceHandle");
		referenceHandleMapping.setGetMethodName("getReferenceHandleForTopLink");
		referenceHandleMapping.setSetMethodName("setReferenceHandleForTopLink");
		referenceHandleMapping.setReferenceClass(MWReferenceHandle.class);
		referenceHandleMapping.setFieldName("referenceHandle");
		descriptor.addMapping(referenceHandleMapping);

		XMLTransformationMapping containerPolicyMapping = new XMLTransformationMapping();
		containerPolicyMapping.setAttributeName("containerPolicy");
		containerPolicyMapping.setAttributeTransformation("legacy45GetContainerPolicyFromRow");
		descriptor.addMapping(containerPolicyMapping);

		XMLTransformationMapping converterMapping = new XMLTransformationMapping();
		converterMapping.setAttributeName("directValueConverter");
		converterMapping.setAttributeTransformation("legacyGetConverterForTopLink");
		descriptor.addMapping(converterMapping);
			
		XMLTransformationMapping indirectionTypeMapping = new XMLTransformationMapping();
		indirectionTypeMapping.setAttributeName("indirectionType");
		indirectionTypeMapping.setAttributeTransformation("legacy45GetIndirectionTypeFromRowForTopLink");
		descriptor.addMapping(indirectionTypeMapping);
	
		descriptor.addDirectMapping("batchReading","usesBatchReading");
		
		return descriptor;
	}
	
	private String legacy45GetIndirectionTypeFromRowForTopLink(Record row) {
		SDKFieldValue indirectionPolicy = (SDKFieldValue) row.get("indirectionPolicy");
		String usesIndirection = (String) ((Record) indirectionPolicy.getElements().get(0)).get("usesIndirection");
		String usesTransparentIndirection = (String) ((Record) indirectionPolicy.getElements().get(0)).get("usesTransparentIndirection");

		if (usesIndirection.equals("true")) {
			if (usesTransparentIndirection.equals("true")) {
				return TRANSPARENT_INDIRECTION;
			}
			return VALUE_HOLDER_INDIRECTION;
		}
			
        return NO_INDIRECTION;
	}
	   
    private MWContainerPolicy legacy45GetContainerPolicyFromRow(Record row) {
        String containerType = (String) row.get("collectionType");
        this.legacyValuesMap.put("collectionType", containerType);
        
        return new MWCollectionContainerPolicy(this);
    }
    
    public void legacy45PostPostProjectBuild() {
        super.legacy45PostPostProjectBuild();
        String containerTypeString = (String) this.legacyValuesMap.remove("collectionType");
        legacyAdjustContainerTypeAndUsesDefaultContainerClassAttributes(containerTypeString);
    }

    public void legacyAdjustContainerTypeAndUsesDefaultContainerClassAttributes(String containerType) {   
        legacyAdjustContainerPolicy(containerType);
        this.containerPolicy.legacyAdjustUsesDefaultContainerClassAttributes();
    }
    
}

