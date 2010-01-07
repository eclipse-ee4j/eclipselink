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
package org.eclipse.persistence.testing.framework.ui;

import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import org.eclipse.persistence.testing.framework.*;

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

    public void addTreeModelListener(TreeModelListener listener) {
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof TestCollection) {
            return ((TestCollection)parent).getTests().elementAt(index);
        } else if (parent instanceof junit.framework.TestSuite) {
            return ((junit.framework.TestSuite)parent).testAt(index);
        } else {
            return null;
        }
    }

    public int getChildCount(Object parent) {
        if (parent instanceof TestCollection) {
            return ((TestCollection)parent).getTests().size();
        } else if (parent instanceof junit.framework.TestSuite) {
            return ((junit.framework.TestSuite)parent).testCount();
        } else {
            return 0;
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof TestCollection) {
            return ((TestCollection)parent).getTests().indexOf(child);
        } else if (parent instanceof junit.framework.TestSuite) {
            Enumeration tests = ((junit.framework.TestSuite)parent).tests();
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

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return !(node instanceof junit.framework.TestSuite);
    }

    public void removeTreeModelListener(TreeModelListener listner) {
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }
}
