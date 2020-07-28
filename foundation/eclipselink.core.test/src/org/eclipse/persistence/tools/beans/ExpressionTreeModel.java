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
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.tools.beans;

import javax.swing.event.*;
import javax.swing.tree.*;

import org.eclipse.persistence.internal.expressions.*;

/**
 * Used for the tree view within expression editor.
 */
public class ExpressionTreeModel implements TreeModel {
    protected ExpressionNode root;

    public ExpressionTreeModel(ExpressionNode expression) {
        this.root = expression;
    }

    public void addTreeModelListener(TreeModelListener listener) {
    }

    public Object getChild(Object parent, int index) {
        parent = ((ExpressionNode)parent).getExpression();
        if (parent instanceof CompoundExpression) {
            CompoundExpression expression = (CompoundExpression)parent;
            if (index == 0) {
                return new ExpressionNode(expression.getFirstChild());
            } else if (index == 1) {
                return new ExpressionNode(expression.getSecondChild());
            }
        } else if (parent instanceof FunctionExpression) {
            FunctionExpression expression = (FunctionExpression)parent;
            return new ExpressionNode(expression.getChildren().elementAt(index));
        }

        return null;
    }

    public int getChildCount(Object parent) {
        parent = ((ExpressionNode)parent).getExpression();
        if (parent instanceof CompoundExpression) {
            return 2;
        } else if (parent instanceof FunctionExpression) {
            FunctionExpression expression = (FunctionExpression)parent;
            return expression.getChildren().size();
        } else {
            return 0;
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        parent = ((ExpressionNode)parent).getExpression();
        child = ((ExpressionNode)child).getExpression();
        if (parent instanceof CompoundExpression) {
            CompoundExpression expression = (CompoundExpression)parent;
            if (expression.getFirstChild() == child) {
                return 0;
            } else if (expression.getSecondChild() == child) {
                return 1;
            }
        } else if (parent instanceof FunctionExpression) {
            FunctionExpression expression = (FunctionExpression)parent;
            return expression.getChildren().indexOf(child);
        }

        return 0;
    }

    /**
     * getRoot method comment.
     */
    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        node = ((ExpressionNode)node).getExpression();
        return !((node instanceof CompoundExpression) ||
                 (node instanceof FunctionExpression));
    }

    public void removeTreeModelListener(TreeModelListener listner) {
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }
}
