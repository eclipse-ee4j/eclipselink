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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * this is the panel used to pick (and/or build) the reference from the
 * "relation" table back to the mapping's "parent" descriptor
 */
final class ManyToManySourceReferencePanel
	extends AbstractTableReferencePanel
{

	ManyToManySourceReferencePanel(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}

	/**
	 * override because a many-to-many mapping does not have a single
	 * set of candidate references; it has two, in the relation table:
	 * source and target
	 */
	protected Iterator candidateReferences(MWTableReferenceMapping mapping) {
		return ((MWManyToManyMapping) mapping).candidateRelationTableSourceReferences();
	}

	/**
	 * setting the reference on a many-to-many mapping will set the
	 * mapping's "source" reference
	 */
	protected void setReference(MWReference reference) {
		super.setReference(reference);
		if (this.mapping().getRelationTable() == null) {
			this.mapping().setRelationTable(reference.getSourceTable());
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
	 * the reference target is one of the "parent" descriptor's tables
	 */
	protected MWTable defaultNewReferenceTargetTable() {
		Iterator candidateTables = this.mapping().getParentRelationalDescriptor().candidateTables();
		return candidateTables.hasNext() ? (MWTable) candidateTables.next() : null;
	}

	private MWManyToManyMapping mapping() {
		return (MWManyToManyMapping) this.subject();
	}

}
