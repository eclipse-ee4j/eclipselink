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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpression;



/**
 * Abstract node class used in the ExpressionTree.  The tree nodes hold on to the treeModel
 * so they can notify it when a propertyChange occurs.
 */
abstract class ExpressionTreeNode
    extends DefaultMutableTreeNode
{
    private DefaultTreeModel model;

     protected ExpressionTreeNode(MWExpression userObject, boolean allowsChildren) {
        this(userObject, allowsChildren, null);
     }

     protected ExpressionTreeNode(MWExpression userObject, boolean allowsChildren, DefaultTreeModel model) {
        super(userObject, allowsChildren);
        setModel(model);
         engageListeners();
    }

    DefaultTreeModel getModel() {
        return model;
    }

    public abstract void initializeChildren();

    protected abstract void engageListeners();

    protected abstract void disengageListeners();

       protected void setModel(DefaultTreeModel model) {
           this.model = model;
       }

    public String toString() {
           return ((MWExpression)getUserObject()).getIndex() + ((MWExpression)getUserObject()).displayString();
       }
}
