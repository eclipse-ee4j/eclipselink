/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.framework.ui;

import junit.framework.Test;
import org.eclipse.persistence.testing.framework.TestCollection;
import org.eclipse.persistence.testing.framework.TestModel;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Used for the tree view within the testing tool.
 */
public class TestEntityTreeModel implements TreeModel {
    protected Vector models;
    protected TestModel root;

    public TestEntityTreeModel(Vector models) {
        this.models = models;
        this.root = new TestModel();
        this.root.addTests(models);
    }

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof TestCollection) {
            return ((TestCollection)parent).getTests().get(index);
        } else if (parent instanceof junit.framework.TestSuite) {
            return ((junit.framework.TestSuite)parent).testAt(index);
        } else {
            return null;
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof TestCollection) {
            return ((TestCollection)parent).getTests().size();
        } else if (parent instanceof junit.framework.TestSuite) {
            return ((junit.framework.TestSuite)parent).testCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof TestCollection) {
            return ((TestCollection)parent).getTests().indexOf(child);
        } else if (parent instanceof junit.framework.TestSuite) {
            Enumeration<Test> tests = ((junit.framework.TestSuite)parent).tests();
            int index = 0;
            while (tests.hasMoreElements()) {
                if (tests.nextElement() == child) {
                    break;
                }
            }
            return index;
        } else {
            return 0;
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public boolean isLeaf(Object node) {
        return !(node instanceof junit.framework.TestSuite);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listner) {
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
}
