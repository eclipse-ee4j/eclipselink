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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public abstract class ChangeDescriptorTypeAction extends AbstractToggleFrameworkAction
{
    protected ChangeDescriptorTypeAction(WorkbenchContext context) {
        super(context);
    }

    protected String[] enabledPropertyNames() {
        return new String[] {MWDescriptor.ACTIVE_PROPERTY};
    }

    protected void execute() {
        Collection selectionPaths = new ArrayList();
        for (Iterator i = CollectionTools.iterator(selectedNodes()); i.hasNext(); ) {
            navigatorSelectionModel().pushExpansionState();
            ApplicationNode selectedNode = (ApplicationNode) i.next();
            ApplicationNode morphedNode = morphNode(selectedNode);
            selectionPaths.add(new TreePath(morphedNode.path()));
            navigatorSelectionModel().popAndRestoreExpansionState(selectedNode, morphedNode);
        }

        navigatorSelectionModel().setSelectionPaths((TreePath[]) CollectionTools.array(selectionPaths.iterator(), new TreePath[selectionPaths.size()]));
    }

    private ApplicationNode morphNode(ApplicationNode selectedNode) {
        MWDescriptor selectedDescriptor = (MWDescriptor) selectedNode.getValue();
        MWDescriptor descriptor = morphDescriptor(selectedDescriptor);

        return ((DescriptorPackageNode) ((DescriptorNode) selectedNode).getParent()).descendantNodeForValue(descriptor);
    }

    protected abstract MWDescriptor morphDescriptor(MWDescriptor descriptor);

    protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
        return ((DescriptorNode) selectedNode).isActive();
    }

}
