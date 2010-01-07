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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateRuntimeFieldNameGenerator;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;


public abstract class MWTransformer extends MWModel
	implements AggregateRuntimeFieldNameGenerator
{
	public final static String TRANSFORMER_PROPERTY = "transformer";

	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	protected MWTransformer() {
		super();
	}
	
	protected MWTransformer(Parent parent) {
		super(parent);
	}
	
	
	// **************** Containment *********************************************
	
	protected Parent getTransformerParent() {
		return (Parent) this.getParent();
	}
	
	private MWTransformationMapping transformationMapping() {
		return this.getTransformerParent().transformationMapping();
	}
	
	protected MWMappingDescriptor parentDescriptor() {
		return this.transformationMapping().getParentDescriptor();
	}
	

	// **************** Aggregate Support *************************************
	
	public MWDescriptor owningDescriptor() {
		throw new UnsupportedOperationException();
	}

	
	// **************** UI support *********************************************
	
	public abstract String transformerDisplayString();
	
	
	// **************** Problems *********************************************
	
	public abstract void addAttributeTransformerProblemsForMapping(List newProblems, MWTransformationMapping mapping);
	
	public abstract void addFieldTransformerProblemsForAssociation(List newProblems, MWFieldTransformerAssociation association);
	
	
	// **************** Runtime conversion ************************************
	
	public abstract void setRuntimeAttributeTransformer(AbstractTransformationMapping mapping);
	
	public abstract void addRuntimeFieldTransformer(AbstractTransformationMapping mapping, DatabaseField runtimeField);
	

	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWTransformer.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWMethodBasedTransformer.class, "method-based");
		ip.addClassIndicator(MWClassBasedTransformer.class, "class-based");
		
		return descriptor;
	}

	public MWTransformer valueForTopLink() {
		return this;
	}

	public static MWTransformer buildTransformerForTopLink(MWTransformer transformer) {
		return (transformer == null) ? new MWNullTransformer() : transformer;
	}


	// **************** Member Interface ***************************************

	/**
	 * the transformer's parent should be able to return the appropriate
	 * transformation mapping
	 */
	public interface Parent extends MWNode {
		MWTransformationMapping transformationMapping();
	}

}
