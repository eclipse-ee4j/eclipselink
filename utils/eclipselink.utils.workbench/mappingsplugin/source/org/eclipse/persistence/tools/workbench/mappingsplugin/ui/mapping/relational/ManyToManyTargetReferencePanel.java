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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * this is the panel used to pick (and/or build) the reference from the
 * "relation" table to the mapping's reference descriptor
 */
final class ManyToManyTargetReferencePanel
	extends AbstractTableReferencePanel
{

	ManyToManyTargetReferencePanel(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}

	/**
	 * override because a many-to-many mapping does not have a single
	 * set of candidate references; it has two, in the relation table:
	 * source and target
	 */
	protected Iterator candidateReferences(MWTableReferenceMapping mapping) {
		return ((MWManyToManyMapping) mapping).candidateRelationTableTargetReferences();
	}

	/**
	 * override because we want the "target" reference, not the
	 * "source" reference
	 */
	protected PropertyValueModel buildTableReferenceHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWManyToManyMapping.TARGET_REFERENCE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWManyToManyMapping) this.subject).getTargetReference();
			}
			protected void setValueOnSubject(Object value) {
				((MWManyToManyMapping) this.subject).setTargetReference((MWReference) value);
			}
		};
	}

	/**
	 * override because we want to set the "target" reference, not the
	 * "source" reference
	 */
	protected void setReference(MWReference reference) {
		MWManyToManyMapping mapping = this.mapping();
		mapping.setTargetReference(reference);
		if (mapping.getRelationTable() == null) {
			mapping.setRelationTable(reference.getSourceTable());
		}
	}

	/**
	 * the reference source is the "relation" table
	 */
	protected List candidateNewReferenceSourceTables() {
		MWTable relationTable = this.mapping().getRelationTable();
		if (relationTable == null) {
			return super.candidateNewReferenceSourceTables();
		}
		return Collections.singletonList(relationTable);
	}

	/**
	 * the reference source is the "relation" table
	 */
	protected MWTable defaultNewReferenceSourceTable() {
		return this.mapping().getRelationTable();
	}

	/**
	 * the reference target is one of the "reference" descriptor's tables
	 */
	protected MWTable defaultNewReferenceTargetTable() {
		MWRelationalDescriptor descriptor = (MWRelationalDescriptor) this.mapping().getReferenceDescriptor();
		if (descriptor == null) {
			return null;
		}
		Iterator candidateTables = descriptor.candidateTables();
		return candidateTables.hasNext() ? (MWTable) candidateTables.next() : null;
	}
	
	private MWManyToManyMapping mapping() {
		return (MWManyToManyMapping) this.subject();
	}

}
