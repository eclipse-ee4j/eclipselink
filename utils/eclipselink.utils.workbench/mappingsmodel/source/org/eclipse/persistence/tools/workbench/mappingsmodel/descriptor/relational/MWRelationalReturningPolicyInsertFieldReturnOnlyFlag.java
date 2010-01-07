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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


public class MWRelationalReturningPolicyInsertFieldReturnOnlyFlag
	extends MWReturningPolicyInsertFieldReturnOnlyFlag
{

	private MWColumnHandle columnHandle;


	// ********** constructors/initialization **********

	/** Default constructor - for TopLink use only */
	private MWRelationalReturningPolicyInsertFieldReturnOnlyFlag() {
		super();
	}

	MWRelationalReturningPolicyInsertFieldReturnOnlyFlag(MWRelationalReturningPolicy parent, MWColumn column) {
		super(parent);
		this.columnHandle.setColumn(column);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
	}


	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.columnHandle);
	}

	private NodeReferenceScrubber buildColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalReturningPolicyInsertFieldReturnOnlyFlag.this.columnRemoved();
			}
			public String toString() {
				return "MWRelationalReturningPolicyInsertFieldReturnOnlyFlag.buildColumnScrubber()";
			}
		};
	}

	void columnRemoved() {
		// we don't really need to clear the column;
		// and some listeners would really appreciate it if we kept it around
		// this.columnHandle.setColumn(null);
		this.getPolicy().removeInsertFieldReturnOnlyFlag(this);
	}

	private MWRelationalReturningPolicy getPolicy() {
		return (MWRelationalReturningPolicy) this.getParent();
	}

	// ********** MWReturningPolicyInsertFieldReturnOnlyFlag implementation **********

	public MWDataField getField() {
		return this.columnHandle.getColumn();
	}


	// ********** problems **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if ( ! CollectionTools.contains(((MWTableDescriptor) this.getOwningDescriptor()).allAssociatedColumns(), this.getField())) {
			currentProblems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_FIELD_NOT_VALID, this.getField().fieldName()));					
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalReturningPolicyInsertFieldReturnOnlyFlag.class);

		XMLCompositeObjectMapping columnHandleMapping = new XMLCompositeObjectMapping();
		columnHandleMapping.setAttributeName("columnHandle");
		columnHandleMapping.setGetMethodName("getColumnHandleForTopLink");
		columnHandleMapping.setSetMethodName("setColumnHandleForTopLink");
		columnHandleMapping.setReferenceClass(MWColumnHandle.class);
		columnHandleMapping.setXPath("column-handle");
		descriptor.addMapping(columnHandleMapping);

		((XMLDirectMapping)descriptor.addDirectMapping("returnOnly", "return-only/text()")).setNullValue(Boolean.FALSE);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWColumnHandle getColumnHandleForTopLink() {
		return (this.columnHandle.getColumn() == null) ? null : this.columnHandle;
	}
	private void setColumnHandleForTopLink(MWColumnHandle handle) {
		NodeReferenceScrubber scrubber = this.buildColumnScrubber();
		this.columnHandle = ((handle == null) ? new MWColumnHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
