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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWClassBasedTransformer 
	extends MWTransformer
{
	// **************** Variables *********************************************
	
	private MWClassHandle transformerClassHandle;
		public final static String TRANSFORMER_CLASS_PROPERTY = "transformerClass";
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWClassBasedTransformer() {
		super();
	}
	
	MWClassBasedTransformer(Parent parent, MWClass transformerClass) {
		super(parent);
		this.setTransformerClass(transformerClass);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parentNode) {
		super.initialize(parentNode);
		this.transformerClassHandle = new MWClassHandle(this, this.buildTransformerClassScrubber());
	}
	

	// **************** Containment hierarchy ****************************************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.transformerClassHandle);
	}
	
	private NodeReferenceScrubber buildTransformerClassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassBasedTransformer.this.setTransformerClass(null);
			}
			public String toString() {
				return "MWClassBasedTransformer.buildTransformerClassScrubber()";
			}
		};
	}

	
	// **************** Transformer class *************************************
	
	public MWClass getTransformerClass() {
	    return this.transformerClassHandle.getType();  
	}
	
	public void setTransformerClass(MWClass newTransformerClass) {
	    MWClass oldTransformerClass = this.getTransformerClass();
	    this.transformerClassHandle.setType(newTransformerClass);
	    if (this.attributeValueHasChanged(oldTransformerClass, newTransformerClass)) {
		    this.firePropertyChanged(TRANSFORMER_CLASS_PROPERTY, oldTransformerClass, newTransformerClass);
		    this.firePropertyChanged(TRANSFORMER_PROPERTY, oldTransformerClass, newTransformerClass);
			this.getProject().recalculateAggregatePathsToColumn(this.parentDescriptor());
	    }
	}
	
	
	// **************** Aggregate Support *************************************
	
	public String fieldNameForRuntime() {
		return "CLASS_TRANSFORMER " + ((getTransformerClass() == null) ? null : getTransformerClass().getName());
	}

	public AggregateFieldDescription fullFieldDescription() {
		return getDescriptionForTransformer(getTransformerClass());
	}
	
	private AggregateFieldDescription getDescriptionForTransformer(MWClass mwClass) {
		final String classDescription = (mwClass == null) ? null : mwClass.getName();
		return new AggregateFieldDescription() {
			public String getMessageKey() {
				return "AGGREGATE_FIELD_DESCRIPTION_FOR_CLASS_BASED_TRANSFORMER";
			}
			
			public Object[] getMessageArguments() {
				return new Object[] {classDescription};
			}
		};
	}
	
	public boolean fieldIsWritten() {
		return true;
	}

	
	// **************** UI support *********************************************
	
	public String transformerDisplayString() {
		MWClass type = this.getTransformerClass();
		return (type == null) ? null : type.displayStringWithPackage();
	}
	

	// **************** Problems *********************************************
	
	public void addAttributeTransformerProblemsForMapping(List newProblems, MWTransformationMapping mapping) {
		if (this.getTransformerClass() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_CLASS_MISSING));
		}
		
		else {
			if (! this.getTransformerClass().mightBeAssignableToAttributeTransformer()) {
				newProblems.add(buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_CLASS_INVALID,
											 this.getTransformerClass().getName()));
			}
		}
	}
	
	public void addFieldTransformerProblemsForAssociation(List newProblems, MWFieldTransformerAssociation association) {
		if (this.getTransformerClass() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_CLASS_MISSING,
										 association.fieldName()));
		}

		else {
			if (! this.getTransformerClass().mightBeAssignableToFieldTransformer()) {
				newProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_CLASS_INVALID,
											 this.getTransformerClass().getName(),
											 association.fieldName()));
			}
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void setRuntimeAttributeTransformer(AbstractTransformationMapping mapping) {
		if (this.getTransformerClass() != null) {
			mapping.setAttributeTransformerClassName(this.getTransformerClass().getName());
		}
	}
	
	public void addRuntimeFieldTransformer(AbstractTransformationMapping mapping, DatabaseField runtimeField) {
		if (this.getTransformerClass() != null) {
			mapping.addFieldTransformerClassName(runtimeField, this.getTransformerClass().getName());
		}
	}
	
	
	// **************** Misc. *************************************************
	
	public void toString(StringBuffer sb) {
		this.transformerClassHandle.toString(sb);
	}

	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWClassBasedTransformer.class);
		descriptor.getInheritancePolicy().setParentClass(MWTransformer.class);
		
		// class handle
		XMLCompositeObjectMapping transformerClassHandleMapping = new XMLCompositeObjectMapping();
		transformerClassHandleMapping.setAttributeName("transformerClassHandle");
		transformerClassHandleMapping.setGetMethodName("getTransformerClassHandleForTopLink");
		transformerClassHandleMapping.setSetMethodName("setTransformerClassHandleForTopLink");
		transformerClassHandleMapping.setReferenceClass(MWClassHandle.class);
		transformerClassHandleMapping.setXPath("transformer-class-handle");
		descriptor.addMapping(transformerClassHandleMapping);
		
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWClassHandle getTransformerClassHandleForTopLink() {
		return (this.transformerClassHandle.getType() == null) ? null : this.transformerClassHandle;
	}
	private void setTransformerClassHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildTransformerClassScrubber();
		this.transformerClassHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
