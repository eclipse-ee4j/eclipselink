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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;


/**
 * provide hooks for rendering a null value, which typically is used
 * to indicate nothing in the list is selected
 */
public abstract class NoneSelectedCellRendererAdapter
	extends AbstractCellRendererAdapter
{
	private final ResourceRepository resourceRepository;

	protected NoneSelectedCellRendererAdapter(ResourceRepository repository) {
		super();
		this.resourceRepository = repository;
	}

	/**
	 * return the value used to indicate a "null" value;
	 * by default, this is null
	 */
	protected Object nullValue() {
		return null;
	}

	protected ResourceRepository resourceRepository() {
		return this.resourceRepository;
	}

	// ********** icon **********

	public Icon buildIcon(Object value) {
		return (value == this.nullValue()) ? this.buildNullValueIcon() : this.buildNonNullValueIcon(value);
	}

	protected Icon buildNullValueIcon() {
		return null;
	}
  
	protected Icon buildNonNullValueIcon(Object value) {
		return null;
	}
  
	// ********** text **********

	public String buildText(Object value) {
		return (value == this.nullValue()) ? this.buildNullValueText() : this.buildNonNullValueText(value);
	}

	protected String buildNullValueText() {
		return this.resourceRepository().getString(this.nullValueTextKey());
	}

	protected String nullValueTextKey() {
		return "NONE_SELECTED";
	}

	protected String buildNonNullValueText(Object value) {
		return null;
	}

	// ********** tool tip text **********

	public String buildToolTipText(Object value) {
		return (value == this.nullValue()) ? this.buildNullValueToolTipText() : this.buildNonNullValueToolTipText(value);
	}

	protected String buildNullValueToolTipText() {
		return null;
	}

	protected String buildNonNullValueToolTipText(Object value) {
		return null;
	}

	// ********** accessible name **********

	public String buildAccessibleName(Object value) {
		return (value == this.nullValue()) ? this.buildNullValueAccessibleName() : this.buildNonNullValueAccessibleName(value);
	}

	protected String buildNullValueAccessibleName() {
		return null;
	}

	protected String buildNonNullValueAccessibleName(Object value) {
		return null;
	}

}
