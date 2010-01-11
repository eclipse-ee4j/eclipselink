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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;

public final class MWRelationalTransformationMapping 
	extends MWTransformationMapping
{
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWRelationalTransformationMapping() {
		super();
	}
	
	MWRelationalTransformationMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
    // *********** MWQueryable implementation ***********
    
    public String iconKey() {
        return "mapping.transformation";
    }
	
	// **************** Field Transformer Associations ************************
	
	public MWRelationalFieldTransformerAssociation fieldTransformerAssociationFor(MWColumn column) {
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			MWRelationalFieldTransformerAssociation fta = (MWRelationalFieldTransformerAssociation) stream.next();
			
			if (fta.getColumn() == column) {
				return fta;
			}
		}
		
		return null;
	}
	
	public MWRelationalFieldTransformerAssociation 
		addFieldTransformerAssociation(MWColumn column, MWMethod fieldTransformerMethod)
	{
		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
			new MWRelationalFieldTransformerAssociation(this, column, fieldTransformerMethod);
		this.addFieldTransformerAssociation(fieldTransformerAssociation);
		return fieldTransformerAssociation;
	}
	
	public MWRelationalFieldTransformerAssociation 
		addFieldTransformerAssociation(MWColumn column, MWClass fieldTransformerClass)
	{
		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
			new MWRelationalFieldTransformerAssociation(this, column, fieldTransformerClass);
		this.addFieldTransformerAssociation(fieldTransformerAssociation);
		return fieldTransformerAssociation;
	}
	
	/** Builds an empty field transformer association, but does not add it */
	public MWRelationalFieldTransformerAssociation buildEmptyFieldTransformerAssociation() {
		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
			new MWRelationalFieldTransformerAssociation(this);
		return fieldTransformerAssociation;
	}
	
	private Iterator columns() {
		return new TransformationIterator(this.fieldTransformerAssociations()) {
			protected Object transform(Object next) {
				return ((MWRelationalFieldTransformerAssociation) next).getColumn();
			}
		};
	}
	
	public Iterator candidateColumns() {
		return new CompositeIterator(
			new TransformationIterator(getParentRelationalDescriptor().associatedTables()) {
				protected Object transform(Object next) {
					return ((MWTable) next).columns();
				}
			}
		);
	}

	// **************** Problems **********************************************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			MWColumn column = ((MWRelationalFieldTransformerAssociation) stream.next()).getColumn();
			
			if (column != null && !CollectionTools.contains(relationalDescriptor().allAssociatedColumns(), column)) {
				currentProblems.add(buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_NOT_VALID, column.displayString()));
			}
		}
		
		this.addDuplicateFieldProblemsTo(currentProblems);
	}
	
	private void addDuplicateFieldProblemsTo(List currentProblems) {
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			MWRelationalFieldTransformerAssociation association = (MWRelationalFieldTransformerAssociation) stream.next();
			
			if (association.duplicateField(association.getColumn())) {
				Problem problem = this.buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_DUPLICATE_FIELD, association.fieldName());
				
				if (! currentProblems.contains(problem)) {
					currentProblems.add(problem);
				}
			}
		}
	}
	
	
	// **************** Convenience *******************************************
	
	MWRelationalClassDescriptor relationalDescriptor() {
		return (MWRelationalClassDescriptor) this.getParent();
	}
	
	
	// ************* aggregate support *************
	
	protected Collection buildAggregateFieldNameGenerators() {
		Collection aggregateFieldNameGenerators = super.buildAggregateFieldNameGenerators();
		for (Iterator i = fieldTransformerAssociations(); i.hasNext(); ) {
			MWTransformer transformer =  ((MWRelationalFieldTransformerAssociation) i.next()).getFieldTransformer();
			aggregateFieldNameGenerators.add(transformer);
		}		
			
		return aggregateFieldNameGenerators;
	}

	public void parentDescriptorMorphedToAggregate() {
		super.parentDescriptorMorphedToAggregate();
		Iterator i = fieldTransformerAssociations();
		while (i.hasNext()) {
			((MWRelationalFieldTransformerAssociation) i.next()).setColumn(null);
		}
	}
	
	public void addWrittenFieldsTo(Collection writtenFields) {
		if (this.isReadOnly()) {
			return;
		}
		for (Iterator stream = this.fieldTransformerAssociations(); stream.hasNext(); ) {
			MWColumn column = ((MWRelationalFieldTransformerAssociation) stream.next()).getColumn();
			
			if (column != null) {
				writtenFields.add(column);
			}
		}
	}

	
	// **************** MWRelationalMapping implementation ***************
	
	public boolean parentDescriptorIsAggregate() {
		return ((MWRelationalDescriptor) getParentDescriptor()).isAggregateDescriptor();
	}

	public MWRelationalDescriptor getParentRelationalDescriptor() {
		return (MWRelationalDescriptor) getParentDescriptor();
	}
	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new TransformationMapping();
	}
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalTransformationMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWTransformationMapping.class);
		
		return descriptor;	
	}
	
}
