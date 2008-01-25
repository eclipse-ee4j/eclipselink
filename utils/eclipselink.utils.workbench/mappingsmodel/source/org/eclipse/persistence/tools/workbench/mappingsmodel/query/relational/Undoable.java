/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

/**
 * This interface is currently used to undo changes made to an expression
 * in the ExpressionBuilderDialog.  Each expression object implements Undoable.
 * When a class fires a property change event it also informs the root expression
 * of what changed(propertyName, oldValue, and newValue)
 * This information is stored until the expression builder dialog is canceled.
 * All of the saved 'changes' are then iterated through and the undoChange method is called.
 */
interface Undoable
{
	public void undoChange(String propertyName, Object oldValue, Object newValue);
}
