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

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWMethodBasedTransformer 
	extends MWTransformer 
{
    // **************** Variables *********************************************
    
    private MWMethodHandle methodHandle;
    	public final static String METHOD_PROPERTY = "method";
    
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWMethodBasedTransformer() {
	    super();
	}
	
	public MWMethodBasedTransformer(Parent parent, MWMethod method) {
		super(parent);
		this.setMethod(method);
	}
	
	/** Used for backward compatibility only */
	MWMethodBasedTransformer(Parent parent, MWMethodHandle methodHandle) {
		super(parent);
		this.methodHandle = methodHandle.setScrubber(this.buildMethodScrubber());
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.methodHandle = new MWMethodHandle(this, this.buildMethodScrubber());
	}
	
	
	// **************** Containment hierarchy *********************************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.methodHandle);
	}
	
	private NodeReferenceScrubber buildMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMethodBasedTransformer.this.setMethod(null);
			}
			public String toString() {
				return "MWMethodBasedTransformer.buildMethodScrubber()";
			}
		};
	}


	// **************** Method ************************************************
	
	public MWMethod getMethod() {
	    return this.methodHandle.getMethod();  
	}
	
	public void setMethod(MWMethod newMethod) {
	    MWMethod oldMethod = this.getMethod();
	    this.methodHandle.setMethod(newMethod);
	    if (this.attributeValueHasChanged(oldMethod, newMethod)) {
		    this.firePropertyChanged(METHOD_PROPERTY, oldMethod, newMethod);
		    this.firePropertyChanged(TRANSFORMER_PROPERTY, oldMethod, newMethod);
			this.getProject().recalculateAggregatePathsToColumn(this.parentDescriptor());
	    }
	}
	
	
	// **************** Aggregate Support *************************************
	
	public String fieldNameForRuntime() {
		return "METHOD_TRANSFORMER " + ((getMethod() == null) ? null : getMethod().getName());
	}

	public AggregateFieldDescription fullFieldDescription() {
		return getDescriptionForMethodSignature(getMethod());
	}
	
	private AggregateFieldDescription getDescriptionForMethodSignature(MWMethod method) {
		final String signature = (method == null) ? null : method.signature();
		return new AggregateFieldDescription() {
			public String getMessageKey() {
				return "AGGREGATE_FIELD_DESCRIPTION_FOR_METHOD_BASED_TRANSFORMER";
			}
			
			public Object[] getMessageArguments() {
				return new Object[] {signature};
			}
		};
	}

	public boolean fieldIsWritten() {
		return true;
	}

	
	// **************** UI support *********************************************
	
	public String transformerDisplayString() {
		MWMethod method = this.getMethod();
		return (method == null) ? null : method.shortSignature();
	}
	

	// **************** Problems *********************************************
	
	public void addAttributeTransformerProblemsForMapping(List newProblems, MWTransformationMapping mapping) {
		// transformer method must be specified
		if (this.getMethod() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_MISSING));
		}
		else {
			// method must have correct signature
			if (! this.getMethod().isCandidateAttributeTransformerMethod()) {
				newProblems.add(buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_INVALID,
											 this.getMethod().signature()));
			}
			// method must be visible to descriptor class
			if (! CollectionTools.contains(mapping.getParentDescriptor().getMWClass().allMethods(), this.getMethod())) {
				newProblems.add(buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_NOT_VISIBLE,
											 this.getMethod().signature()));
			}
		}
	}
	
	public void addFieldTransformerProblemsForAssociation(List newProblems, MWFieldTransformerAssociation association) {
		// transformer method must be specified
		if (this.getMethod() == null) {
			newProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_METHOD_MISSING,
										 association.fieldName()));
		}
		else {
			// method must have correct signature
			if (! this.getMethod().isCandidateFieldTransformerMethod()) {
				newProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_METHOD_INVALID,
											 this.getMethod().signature(),
											 association.fieldName()));
			}
			// method must be visible to descriptor class
			if (! CollectionTools.contains(association.getMapping().getParentDescriptor().getMWClass().allMethods(), this.getMethod())) {
				newProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_METHOD_NOT_VISIBLE,
											 this.getMethod().signature(),
											 association.fieldName()));
			}
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void setRuntimeAttributeTransformer(AbstractTransformationMapping mapping) {
		if (this.getMethod() != null) {
			mapping.setAttributeTransformation(this.getMethod().getName());
		}
	}
	
	public void addRuntimeFieldTransformer(AbstractTransformationMapping mapping, DatabaseField runtimeField) {
		if (this.getMethod() != null) {
			mapping.addFieldTransformation(runtimeField, this.getMethod().getName());
		}
	}
	
	
	// **************** Misc. *************************************************
	
	public void toString(StringBuffer sb) {
		this.getMethod().toString(sb);
	}

	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWMethodBasedTransformer.class);
		descriptor.getInheritancePolicy().setParentClass(MWTransformer.class);

		XMLCompositeObjectMapping methodHandleMapping = new XMLCompositeObjectMapping();
		methodHandleMapping.setAttributeName("methodHandle");
		methodHandleMapping.setGetMethodName("getMethodHandleForTopLink");
		methodHandleMapping.setSetMethodName("setMethodHandleForTopLink");
		methodHandleMapping.setReferenceClass(MWMethodHandle.class);
		methodHandleMapping.setXPath("method-handle");
		descriptor.addMapping(methodHandleMapping);
		
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getMethodHandleForTopLink() {
		return (this.methodHandle.getMethod() == null) ? null : this.methodHandle;
	}
	private void setMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildMethodScrubber();
		this.methodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

}
