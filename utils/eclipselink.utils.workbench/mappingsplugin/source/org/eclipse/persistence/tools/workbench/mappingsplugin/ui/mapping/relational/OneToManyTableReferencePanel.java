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

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * this is the panel used to pick (and/or build) the reference from the
 * reference descriptor's table(s) back to the mapping's "parent" descriptor
 */
final class OneToManyTableReferencePanel
	extends AbstractTableReferencePanel
{

	public OneToManyTableReferencePanel(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}
	
	/**
	 * the reference source is one of the "reference" descriptor's tables
	 */
	protected MWTable defaultNewReferenceSourceTable() {
		MWRelationalDescriptor descriptor = (MWRelationalDescriptor) this.mapping().getReferenceDescriptor();
		if (descriptor == null) {
			return null;
		}
		Iterator candidateTables = descriptor.candidateTables();
		return candidateTables.hasNext() ? (MWTable) candidateTables.next() : null;
	}

	/**
	 * the reference target is one of the "parent" descriptor's tables
	 */
	protected MWTable defaultNewReferenceTargetTable() {
		Iterator candidateTables = this.mapping().getParentRelationalDescriptor().candidateTables();
		return candidateTables.hasNext() ? (MWTable) candidateTables.next() : null;
	}

	private MWOneToManyMapping mapping() {
		return (MWOneToManyMapping) this.subject();
	}

}
