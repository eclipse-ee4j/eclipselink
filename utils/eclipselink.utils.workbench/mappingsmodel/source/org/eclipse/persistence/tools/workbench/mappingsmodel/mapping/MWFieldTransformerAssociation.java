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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.text.Collator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * Abstract class used in MWTransformationMapping.
 * Subclasses of MWTransformationMapping must also use 
 *  subclasses of this class.
 */
public abstract class MWFieldTransformerAssociation 
	extends MWModel
	implements MWTransformer.Parent
{
	// **************** Variables *********************************************
	
	/** 
	 * The field associated with the field transformer
	 * 	(actual instance variable is responsibility of subclass)
	 */
		public final static String FIELD_PROPERTY = "field";
	
	
	/** The FieldTransformer used to translate from Object to data value */
	private volatile MWTransformer fieldTransformer;
		public final static String FIELD_TRANSFORMER_PROPERTY = "fieldTransformer";
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWFieldTransformerAssociation() {
		super();
	}
	
	protected MWFieldTransformerAssociation(MWTransformationMapping parent) {
		super(parent);
		this.fieldTransformer = new MWNullTransformer(this);
	}
	
	protected MWFieldTransformerAssociation(MWTransformationMapping parent, MWMethod fieldTransformerMethod) {
		super(parent);
		this.fieldTransformer = new MWMethodBasedTransformer(this, fieldTransformerMethod);
	}
	
	protected MWFieldTransformerAssociation(MWTransformationMapping parent, MWClass fieldTransformerClass) {
		super(parent);
		this.fieldTransformer = new MWClassBasedTransformer(this, fieldTransformerClass);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.fieldTransformer);
	}
	
	
	// **************** Containment *******************************************
	
	public MWTransformationMapping getMapping() {
		return (MWTransformationMapping) this.getParent();
	}
	
	public MWMappingDescriptor getParentDescriptor() {
		return getMapping().getParentDescriptor();
	}
	
	// **************** MWTransformer.Parent **********************************************

    public MWTransformationMapping transformationMapping() {
    	return this.getMapping();
    }
	
	
	// **************** Field *************************************************
	
	public String fieldName() {
		if (getField() == null) {
			return "";
		}
		return this.getField().fieldName();
	}
	
	public abstract MWDataField getField();
	
	
	// **************** Field transformer *************************************
	
	public MWTransformer getFieldTransformer() {
	    return this.fieldTransformer;
	}
	
	protected void setFieldTransformer(MWTransformer newFieldTransformer) {
		MWTransformer oldFieldTransformer = this.fieldTransformer;
		this.fieldTransformer = newFieldTransformer;
		this.firePropertyChanged(FIELD_TRANSFORMER_PROPERTY, oldFieldTransformer, newFieldTransformer);
	}
	
	public void clearFieldTransformer() {
		this.setFieldTransformer(new MWNullTransformer(this));
	}
	
	public void setFieldTransformer(MWMethod newFieldTransformerMethod) {
		this.setFieldTransformer(new MWMethodBasedTransformer(this, newFieldTransformerMethod));
	}
	
	public void setFieldTransformer(MWClass newFieldTransformerClass) {
		this.setFieldTransformer(new MWClassBasedTransformer(this, newFieldTransformerClass));
	}
	
	public void toString(StringBuffer sb) {
		MWDataField field = this.getField();
		sb.append((field == null) ? "null" : field.fieldName());
		sb.append(" => ");
		this.fieldTransformer.toString(sb);
	}
	
	// **************** Problems *********************************************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.fieldTransformer.addFieldTransformerProblemsForAssociation(currentProblems, this);
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void addRuntimeFieldTransformer(AbstractTransformationMapping mapping) {
		DatabaseField runtimeField = this.runtimeField();
		
		if (runtimeField != null) {
			this.fieldTransformer.addRuntimeFieldTransformer(mapping, runtimeField);
		}
	}
	
	protected DatabaseField runtimeField() {
		return (this.getField() == null) ? null : this.getField().runtimeField();
	}
	
	
	// **************** Comparable contract ***********************************
	
	public int compareTo(Object o) {
		return Collator.getInstance().compare(this.fieldName(), ((MWFieldTransformerAssociation) o).fieldName());
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWFieldTransformerAssociation.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWXmlFieldTransformerAssociation.class, "xml");
		ip.addClassIndicator(MWRelationalFieldTransformerAssociation.class, "relational");
		
		XMLCompositeObjectMapping fieldTransformerMapping = new XMLCompositeObjectMapping();
		fieldTransformerMapping.setAttributeName("fieldTransformer");
		fieldTransformerMapping.setGetMethodName("getFieldTransformerForTopLink");
		fieldTransformerMapping.setSetMethodName("setFieldTransformerForTopLink");
		fieldTransformerMapping.setReferenceClass(MWTransformer.class);
		fieldTransformerMapping.setXPath("field-transformer");
		descriptor.addMapping(fieldTransformerMapping);
	
		return descriptor;
	}
	
	/**
	 * check for null transformer
	 */
	private MWTransformer getFieldTransformerForTopLink() {
		return this.fieldTransformer.valueForTopLink();
	}
	private void setFieldTransformerForTopLink(MWTransformer transformer) {
		this.fieldTransformer = MWTransformer.buildTransformerForTopLink(transformer);
	}

}
