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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;

/**
 * Render <code>Boolean</code> values with localized strings.
 */
public final class BooleanCellRendererAdapter extends AbstractCellRendererAdapter
{
	/**
	 * The localized string used to decorate the <code>Boolean.FALSE</code>
	 * value.
	 */
	private final String falseText;

	/**
	 * The localized string used to decorate the <code>Boolean.TRUE</code>
	 * value.
	 */
	private final String trueText;

	/**
	 * Creates a new <code>BooleanLabelDecorator</code> decorating
	 * <code>Boolean</code> values.
	 *
	 * @param trueDecorator The localized string used to decorate
	 * <code>Boolean.TRUE</code>
	 * @param falseDecorator The localized string used to decorate
	 * <code>Boolean.FALSE</code>
	 */
	public BooleanCellRendererAdapter(String trueText,
												 String falseText)
	{
		super();

		this.trueText  = trueText;
		this.falseText = falseText;
	}

	/**
	 * Returns a string that can be used to identify the object in a textual UI
	 * setting (typically the object's name).
	 *
	 * @param value The value to decorate, which is a <code>Booleam</code> value
	 * @return The localized string decorating the given value
	 */
	public String buildText(Object value)
	{
		if (Boolean.TRUE.equals(value)) {
			return this.trueText;
		}

		if (Boolean.FALSE.equals(value)) {
			return this.falseText;
		}

		return null;
	}

}
