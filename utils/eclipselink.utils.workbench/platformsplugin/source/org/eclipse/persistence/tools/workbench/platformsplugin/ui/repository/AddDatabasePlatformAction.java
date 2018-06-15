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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * add a platform to the selected repositories
 */
final class AddDatabasePlatformAction extends AbstractFrameworkAction {

    public AddDatabasePlatformAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        this.initializeTextAndMnemonic("ADD_DATABASE_PLATFORM");
        // no accelerator
        this.initializeIcon("ADD_DATABASE_PLATFORM");
        this.initializeToolTipText("ADD_DATABASE_PLATFORM.TOOL_TIP");
    }

    protected void execute() {
        NewNameDialog.Builder builder = this.buildNewNameDialogBuilder();
        ApplicationNode[] nodes = this.selectedNodes();
        for (int i = nodes.length; i-- > 0; ) {
            this.execute(((DatabasePlatformRepositoryNode) nodes[i]).getDatabasePlatformRepository(), builder);
        }
    }

    private NewNameDialog.Builder buildNewNameDialogBuilder() {
        // set the properties that stay the same for every node
        NewNameDialog.Builder builder = new NewNameDialog.Builder();
        builder.setTitle(this.resourceRepository().getString("ADD_DATABASE_PLATFORM_DIALOG_TITLE"));
        builder.setTextFieldDescription(this.resourceRepository().getString("ADD_DATABASE_PLATFORM_DIALOG_DESCRIPTION"));
        builder.setHelpTopicId("dialog.dbPlatformAdd");
        return builder;
    }

    protected void execute(DatabasePlatformRepository repository, NewNameDialog.Builder builder) {
        builder.setExistingNames(this.existingTypeNames(repository));
        NewNameDialog dialog = builder.buildDialog(this.getWorkbenchContext());
        dialog.show();
        if (dialog.wasConfirmed()) {
            repository.addPlatform(dialog.getNewName(), dialog.getNewName() + ".xml");
        }
    }

    private Iterator existingTypeNames(DatabasePlatformRepository repository) {
        return new TransformationIterator(repository.platforms()) {
            protected Object transform(Object next) {
                return ((DatabasePlatform) next).getName();
            }
        };
    }

}
