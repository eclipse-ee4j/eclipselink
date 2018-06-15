/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;




/**
 * QueryableTreeNodes are found in the QueryableTree.  The node holds on to whether this queryable
 * has allowsNull set to true or false.  After the user has selected a queryable from the tree and
 * closed the dialog, we can get the allowsNull info from the node.
 *
 * The node also knows if he is allowed to have children or not.
 */
final class QueryableTreeNode extends DefaultMutableTreeNode
{
    private boolean allowsNull;

     QueryableTreeNode(MWQueryable userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
        initialize();
      }

     private void initialize()
     {
         this.allowsNull = false;
     }

    public int getChildCount()
    {
        if (children == null) {
            return 0;
        }
        //This is a slight hack to keep Jaws (accessibility program) from getting
        //in an infinite loop when showing the queryable tree
        if (getLevel() > 10)
            return 0;

        return children.size();
    }

       MWQueryable getQueryable()
       {
           return (MWQueryable) getUserObject();
       }

       boolean isAllowsNull()
       {
           return allowsNull;
       }

       public boolean isLeaf(Filter queryableFilter)
       {
           return ((MWQueryable) getUserObject()).isLeaf(queryableFilter);
       }

       void setAllowsNull(boolean allowsNull)
       {
           this.allowsNull = allowsNull;
       }
}
