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
package org.eclipse.persistence.tools.workbench.test.scplugin.app;

import java.util.ArrayList;

import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;

public class SCTestNodeManager implements NodeManager {

    private ArrayList projectNodes;

    public SCTestNodeManager( ApplicationNode projectNode) {
        super();
        initialize( projectNode);
    }

    public void initialize( ApplicationNode projectNode) {

        this.projectNodes = new ArrayList( 5);
        this.addProjectNode( projectNode);
    }

    public void addProjectNode( ApplicationNode node) {

        int i = this.projectNodes.size();

        this.projectNodes.add( i, node);
    }

    public ApplicationNode[] projectNodesFor( Plugin plugin) {

        return (ApplicationNode[]) this.projectNodes.toArray(new ApplicationNode[this.projectNodes.size()]);
    }

    public TreeNodeValueModel getRootNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public NavigatorSelectionModel getTreeSelectionModel() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean save(ApplicationNode node, WorkbenchContext workbenchContext) {
        return false;
    }


    public void removeProjectNode(ApplicationNode node) {
        // TODO Auto-generated method stub

    }
}
