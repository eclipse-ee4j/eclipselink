/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
