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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class MoveDescriptorAction extends AbstractFrameworkAction {

    MoveDescriptorAction(WorkbenchContext workbenchContext) {
        super(workbenchContext);
    }

    protected void initialize() {
        super.initialize();
        this.setIcon(EMPTY_ICON);
        initializeTextAndMnemonic("MOVE_DESCRIPTOR_ACTION");
        initializeToolTipText("MOVE_DESCRIPTOR_ACTION.tooltip");
    }

    protected void execute() {
        String packageName = promptForPackageName(selectedNodes()[0]);
        if (packageName != null) {
            ApplicationNode[] selectedNodes = selectedNodes();
            int size = selectedNodes.length;
            TreePath[] selectionPath = new TreePath[size];
            for (int i =0; i < size; i++) {
                selectionPath[i] = (renameDescriptor((DescriptorNode) selectedNodes[i], packageName));
            }
            navigatorSelectionModel().setSelectionPaths(selectionPath);
        }
    }

    private TreePath renameDescriptor(DescriptorNode descriptorNode, String packageName) {
        MWDescriptor descriptor= (MWDescriptor) descriptorNode.getValue();
        MWClass mwClass = descriptor.getMWClass();
        mwClass.setName(packageName + "." + mwClass.shortName());
        descriptor.setName(packageName + "." + mwClass.shortName());
        return new TreePath(descriptorNode.getProjectRoot().descendantNodeForValue(descriptor).path());
    }

    private String promptForPackageName(ApplicationNode node) {
        PackageNameDialog packageNameDialog = new PackageNameDialog(getWorkbenchContext(), buildPackageNamesCollectionHolder(node));
        packageNameDialog.show();

        if (packageNameDialog.wasConfirmed()) {
            return packageNameDialog.getPackageName();
        }

        return null;
    }

    private CollectionValueModel buildPackageNamesCollectionHolder(ApplicationNode selectedNode) {
        return new SimpleCollectionValueModel(CollectionTools.collection(((MWProject) selectedNode.getProjectRoot().getValue()).packageNames()));
    }

}
