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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Record;

public abstract class MWTransformationMapping
	extends MWMapping
	implements MWIndirectableMapping, MWTransformer.Parent
{
	
	/** Indicates whether the attribute has a simple value that may be copied rather than built */
	private volatile boolean mutable;
		public final static String MUTABLE_PROPERTY = "mutable";
	
	/** The AttributeTransformer implementation used to translate from data record to the attribute */
	private volatile MWTransformer attributeTransformer;
		public final static String ATTRIBUTE_TRANSFORMER_PROPERTY = "attributeTransformer";
	
	/** 
	 * The list of FieldTransformer implementations used to translate from the object to field values
	 * (Order is stored mainly for XML transformation mappings) 
	 */
	private List fieldTransformerAssociations;
		public final static String FIELD_TRANSFORMER_ASSOCIATIONS_LIST = "fieldTransformerAssociations";
	
        
	/** Used on all subclasses - uses the MWIndirectableMapping INDIRECTION_PROPERTY */
	private volatile String indirectionType;
	
	
	// **************** Constructors ******************************************
	
	protected MWTransformationMapping() {
		super();
	}
	
	protected MWTransformationMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	
	// **************** Initialization ****************************************
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.attributeTransformer = new MWNullTransformer(this);
		this.fieldTransformerAssociations = new Vector();
		this.indirectionType = NO_INDIRECTION;
        this.mutable = true;
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.attributeTransformer);
		synchronized (this.fieldTransformerAssociations) { children.addAll(this.fieldTransformerAssociations); }
	}
	
	// **************** Mutable API *******************************************
	
	public boolean isMutable() {
		return this.mutable;
	}
	
	public void setMutable(boolean newValue) {
		boolean oldValue = this.mutable;
		this.mutable = newValue;
		this.firePropertyChanged(MUTABLE_PROPERTY, oldValue, newValue);
	}
	
	
	// **************** Attribute Transformer *********************************
	
	public MWTransformer getAttributeTransformer() {
		return this.attributeTransformer;
	}
	
	private void setAttributeTransformer(MWTransformer newAttributeTransformer) {
		MWTransformer oldAttributeTransformer = this.attributeTransformer;
		this.attributeTransformer = newAttributeTransformer;
		this.firePropertyChanged(ATTRIBUTE_TRANSFORMER_PROPERTY, oldAttributeTransformer, newAttributeTransformer);
	}
	
	public void clearAttributeTransformer() {
		this.setAttributeTransformer(new MWNullTransformer(this));
	}
	
	public void setAttributeTransformer(MWMethod newAttributeTransformerMethod) {
		this.setAttributeTransformer(new MWMethodBasedTransformer(this, newAttributeTransformerMethod));
	}
	
	public void setAttributeTransformer(MWClass newAttributeTransformerClass) {
		this.setAttributeTransformer(new MWClassBasedTransformer(this, newAttributeTransformerClass));
	}
	
	public Iterator candidateAttributeTransformationMethods() {
		// These are more than just the methods that are "valid" attribute transformation methods
		return this.getParentDescriptor().getMWClass().allInstanceMethods();
	}
	
	
	// **************** Field Transformer Associations ************************
	
	public ListIterator fieldTransformerAssociations() {
		return new CloneListIterator(this.fieldTransformerAssociations);
	}
	
	public int fieldTransformerAssociationsSize() {
		return this.fieldTransformerAssociations.size();
	}
	
	public void addFieldTransformerAssociation(MWFieldTransformerAssociation fieldTransformerAssociation) {
	    this.addItemToList(fieldTransformerAssociation, this.fieldTransformerAssociations, FIELD_TRANSFORMER_ASSOCIATIONS_LIST);
		this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
	}
	
	public void removeFieldTransformerAssociation(MWFieldTransformerAssociation fieldTransformerAssociation) {
		int index = this.fieldTransformerAssociations.indexOf(fieldTransformerAssociation);
		if (index != -1) {
			this.removeNodeFromList(index, this.fieldTransformerAssociations, FIELD_TRANSFORMER_ASSOCIATIONS_LIST);
			this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
		}
	}
	
	public void clearFieldTransformerAssociations() {
		if (this.clearList(this.fieldTransformerAssociations, FIELD_TRANSFORMER_ASSOCIATIONS_LIST)) {
			this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
		}
	}
	
	public Iterator candidateFieldTransformationMethods() {
		// These are more than just the methods that are "valid" field transformation methods
		return this.getParentDescriptor().getMWClass().allInstanceMethods();
	}
	
	
	// **************** Indirection *******************************************
	
	public boolean usesValueHolderIndirection() {
		return this.indirectionType == VALUE_HOLDER_INDIRECTION;
	}
	
	public boolean usesNoIndirection() {
		return this.indirectionType == NO_INDIRECTION;
	}
	
	
	public void setUseNoIndirection() {
		this.setIndirectionType(NO_INDIRECTION);
	}
	
	public void setUseValueHolderIndirection() {
		this.setIndirectionType(VALUE_HOLDER_INDIRECTION);
	}
    
    private void setIndirectionType(String indirectionType) {
        Object oldValue = this.indirectionType;
        this.indirectionType = indirectionType;
        firePropertyChanged(INDIRECTION_PROPERTY, oldValue, indirectionType);
    }
    
	// **************** MWTransformer.Parent **********************************************

    public MWTransformationMapping transformationMapping() {
    	return this;
    }
	
	// **************** Morphing **********************************************
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWTransformationMapping(this);
	}
	
	protected void initializeFromMWIndirectableMapping(MWIndirectableMapping oldMapping) {
		super.initializeFromMWIndirectableMapping(oldMapping);
		
		if (oldMapping.usesValueHolderIndirection()) {
			this.setUseValueHolderIndirection();
		}
		else if (oldMapping.usesNoIndirection()) {
			this.setUseNoIndirection();
		}
	}
	
	
	// **************** Problems *********************************************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.attributeTransformer.addAttributeTransformerProblemsForMapping(newProblems, this);
		this.addFieldTransformerProblemsTo(newProblems);
		this.addIndirectionProblemsTo(newProblems);
	}
	
	private void addFieldTransformerProblemsTo(List newProblems) {
		// field transformers must be specified unless mapping is read only
		if (this.fieldTransformerAssociations.size() == 0 && ! this.isReadOnly()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_ASSOCIATIONS_NOT_SPECIFIED));
		}
	}
	
	private void addIndirectionProblemsTo(List newProblems) {
		if (this.getInstanceVariable().isValueHolder()) {
			if (! this.usesValueHolderIndirection()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION));
			}
		}
		else {
			if (!this.getProject().usesWeaving() && this.usesValueHolderIndirection()) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE));
			}
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	public DatabaseMapping runtimeMapping() {
		AbstractTransformationMapping mapping = (AbstractTransformationMapping) super.runtimeMapping();
		
		// attribute transformer
		this.attributeTransformer.setRuntimeAttributeTransformer(mapping);
		
		// field transformers
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			((MWFieldTransformerAssociation) stream.next()).addRuntimeFieldTransformer(mapping);
		}
		
		// indirection
        if (this.usesValueHolderIndirection()) {
            mapping.setUsesIndirection(true);
        }
		
		// mutability
		mapping.setIsMutable(this.mutable);
		
		return mapping;
	}
	
	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWTransformationMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWMapping.class);
		
		// attribute transformer class
		XMLCompositeObjectMapping attributeTransformerMapping = new XMLCompositeObjectMapping();
		attributeTransformerMapping.setAttributeName("attributeTransformer");
		attributeTransformerMapping.setGetMethodName("getAttributeTransformerForTopLink");
		attributeTransformerMapping.setSetMethodName("setAttributeTransformerForTopLink");
		attributeTransformerMapping.setReferenceClass(MWTransformer.class);
		attributeTransformerMapping.setXPath("attribute-transformer");
		descriptor.addMapping(attributeTransformerMapping);
		
		// field transformer associations
		XMLCompositeCollectionMapping fieldTransformerAssociationsMapping = new XMLCompositeCollectionMapping();
		fieldTransformerAssociationsMapping.setAttributeName("fieldTransformerAssociations");
		fieldTransformerAssociationsMapping.setReferenceClass(MWFieldTransformerAssociation.class);
		fieldTransformerAssociationsMapping.setXPath("field-transformer-associations");
		descriptor.addMapping(fieldTransformerAssociationsMapping);
	
		// indirection
        ObjectTypeConverter indirectionTypeConverter = new ObjectTypeConverter();
        indirectionTypeConverter.addConversionValue(NO_INDIRECTION, NO_INDIRECTION);
        indirectionTypeConverter.addConversionValue(VALUE_HOLDER_INDIRECTION, VALUE_HOLDER_INDIRECTION);
        XMLDirectMapping indirectionTypeMapping = new XMLDirectMapping();
        indirectionTypeMapping.setAttributeName("indirectionType");
        indirectionTypeMapping.setXPath("indirection-type/text()");
        indirectionTypeMapping.setNullValue(NO_INDIRECTION);
        indirectionTypeMapping.setConverter(indirectionTypeConverter);
        descriptor.addMapping(indirectionTypeMapping);


		// atomic
		descriptor.addDirectMapping("mutable", "mutable/text()");

		return descriptor;	
	}
    
	/**
	 * check for null transformer
	 */
	private MWTransformer getAttributeTransformerForTopLink() {
		return this.attributeTransformer.valueForTopLink();
	}
	private void setAttributeTransformerForTopLink(MWTransformer transformer) {
		this.attributeTransformer = MWTransformer.buildTransformerForTopLink(transformer);
	}

}
