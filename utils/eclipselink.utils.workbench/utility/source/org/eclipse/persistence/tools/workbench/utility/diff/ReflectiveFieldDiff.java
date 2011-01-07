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
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.lang.reflect.Field;
import java.text.Collator;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * Wrap a field-specific diff and associate it with a field.
 */
public class ReflectiveFieldDiff extends DiffWrapper implements Comparable {
	private final Field field;


	public ReflectiveFieldDiff(Field field, Diff diff, Differentiator differentiator) {
		super(diff, differentiator);
		this.field = field;
	}

	/**
	 * @see Diff#appendDescription(org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter)
	 */
	public void appendDescription(IndentingPrintWriter pw) {
		if (this.different()) {
			pw.print("The values in the field '");
			pw.print(this.field.getName());
			pw.print("' are different");
			pw.println();
			pw.indent();
				super.appendDescription(pw);
			pw.undent();
		}
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		// sorted on field name
		return Collator.getInstance().compare(this.fieldName(), ((ReflectiveFieldDiff) o).fieldName());
	}

	public Field getField() {
		return this.field;
	}

	public String fieldName() {
		return this.field.getName();
	}

}
