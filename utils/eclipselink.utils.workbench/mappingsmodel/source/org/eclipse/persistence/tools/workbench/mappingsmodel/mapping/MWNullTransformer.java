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

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

public final class MWNullTransformer 
	extends MWTransformer
{
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only (sorta) */
	MWNullTransformer() {
		super();
	}
	
	MWNullTransformer(Parent parent) {
		super(parent);
	}
	
	
	// **************** Aggregate Support *************************************
	
	public String fieldNameForRuntime() {
		return "NULL_TRANSFORMER";
	}

	public AggregateFieldDescription fullFieldDescription() {
		return new AggregateFieldDescription() {
			public String getMessageKey() {
				return "AGGREGATE_FIELD_DESCRIPTION_FOR_NULL_TRANSFORMER";
			}
			
			public Object[] getMessageArguments() {
				return new Object[0];
			}
		};
	}
	
	/** @see AggregateRuntimeFieldNameGenerator#fieldIsWritten() */
	public boolean fieldIsWritten() {
		return true;
	}
	
	
	// **************** UI support *********************************************
	
	public String transformerDisplayString() {
		return null;
	}
	
	
	// **************** Problems *********************************************
	
	public void addAttributeTransformerProblemsForMapping(List newProblems, MWTransformationMapping mapping) {
		newProblems.add(this.buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_NOT_SPECIFIED));
	}
	
	public void addFieldTransformerProblemsForAssociation(List newProblems, MWFieldTransformerAssociation association) {
		newProblems.add(this.buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_NOT_SPECIFIED, association.fieldName()));
	}
	
	
	// **************** Runtime conversion ************************************
	
	public void setRuntimeAttributeTransformer(AbstractTransformationMapping mapping) {
		// NOP
	}
	
	public void addRuntimeFieldTransformer(AbstractTransformationMapping mapping, DatabaseField runtimeField) {
		// NOP
	}


	// **************** TopLink methods ************************************

	public MWTransformer valueForTopLink() {
		return null;
	}

}
