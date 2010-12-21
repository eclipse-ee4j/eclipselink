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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWRelationalFieldTransformerAssociation
		extends MWFieldTransformerAssociation
{
	// **************** Variables *********************************************
	
	/** The column associated with the field transformer */
	private MWColumnHandle columnHandle;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWRelationalFieldTransformerAssociation() {
		super();
	}
	
	MWRelationalFieldTransformerAssociation(MWRelationalTransformationMapping parent) {
		super(parent);
	}
	
	MWRelationalFieldTransformerAssociation(MWRelationalTransformationMapping parent, 
											MWColumn column, 
											MWClass fieldTransformerClass) {
		super(parent, fieldTransformerClass);
		this.setColumn(column);
	}
	
	MWRelationalFieldTransformerAssociation(MWRelationalTransformationMapping parent, 
											MWColumn databaseField, 
											MWMethod fieldTransformerMethod) {
		super(parent, fieldTransformerMethod);
		this.setColumn(databaseField);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
	}
	

	// **************** Containment hierarchy *****************************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.columnHandle);
	}
	
	private NodeReferenceScrubber buildColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalFieldTransformerAssociation.this.setColumn(null);
			}
			public String toString() {
				return "MWRelationalFieldTransformerAssociation.buildColumnScrubber()";
			}
		};
	}

	
	// **************** column *************************************************
	
	public MWColumn getColumn() {
		return this.columnHandle.getColumn();
	}
	
	public void setColumn(MWColumn column) {
		MWColumn old = this.columnHandle.getColumn();
		this.columnHandle.setColumn(column);
		this.firePropertyChanged(FIELD_PROPERTY, old, column);
	}
	
	protected void setFieldTransformer(MWTransformer fieldTransformer) {
		super.setFieldTransformer(fieldTransformer);
		this.getProject().recalculateAggregatePathsToColumn(this.getParentDescriptor());
	}
	
	public String fieldName() {
		if (this.relationalDescriptor().isAggregateDescriptor()) {
			return this.getFieldTransformer().fieldNameForRuntime();
		}
		return super.fieldName();
	}

	public MWDataField getField() {
		return this.getColumn();
	}
	
	
	// **************** Convenience *******************************************
	
	MWRelationalTransformationMapping relationalTransformationMapping() {
		return (MWRelationalTransformationMapping) this.getParent();
	}
	
	MWRelationalDescriptor relationalDescriptor() {
		return this.relationalTransformationMapping().getParentRelationalDescriptor();
	}
	
	
	// **************** Problems *********************************************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if (! this.relationalDescriptor().isAggregateDescriptor() && this.getColumn() == null) {
			currentProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_FIELD_MISSING));
		}
	}
	
	/** Return true if the field specified is included in another field transformer association */
	public boolean duplicateField(MWColumn field) {
		if (field == null) {
			return false;
		}
		
		for (Iterator stream = this.relationalTransformationMapping().fieldTransformerAssociations(); stream.hasNext(); ) {
			MWRelationalFieldTransformerAssociation association = (MWRelationalFieldTransformerAssociation) stream.next();
			
			if (association != this && association.getColumn() == field) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseField runtimeField() {
		if (this.relationalDescriptor().isAggregateDescriptor()) {
			return new DatabaseField(getMapping().getName() + "->" + getFieldTransformer().fieldNameForRuntime());
		}
		return super.runtimeField();
	}
	

	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWRelationalFieldTransformerAssociation.class);
		descriptor.getInheritancePolicy().setParentClass(MWFieldTransformerAssociation.class);
		
		XMLCompositeObjectMapping columnHandleMapping = new XMLCompositeObjectMapping();
		columnHandleMapping.setAttributeName("columnHandle");
		columnHandleMapping.setGetMethodName("getColumnHandleForTopLink");
		columnHandleMapping.setSetMethodName("setColumnHandleForTopLink");
		columnHandleMapping.setReferenceClass(MWColumnHandle.class);
		columnHandleMapping.setXPath("column-handle");
		descriptor.addMapping(columnHandleMapping);
		
		return descriptor;
	}
	
	/**
	 * check for null
	 */
	private MWColumnHandle getColumnHandleForTopLink() {
		return (this.columnHandle.getColumn() == null) ? null : this.columnHandle;
	}
	private void setColumnHandleForTopLink(MWColumnHandle columnHandle) {
		NodeReferenceScrubber scrubber = this.buildColumnScrubber();
		this.columnHandle = ((columnHandle == null) ? new MWColumnHandle(this, scrubber) : columnHandle.setScrubber(scrubber));
	}
	
}
