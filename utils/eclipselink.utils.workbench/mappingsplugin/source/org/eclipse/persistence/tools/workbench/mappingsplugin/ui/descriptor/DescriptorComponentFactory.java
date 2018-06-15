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

import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;



public class DescriptorComponentFactory extends SwingComponentFactory {

    public static ListCellRenderer buildMethodRenderer(ResourceRepository resourceRepository) {
        return new AdaptableListCellRenderer(new MethodCellRendererAdapter(resourceRepository));
    }

    /** We want to display the signature, but we want filtering based only on the method name */
    public static StringConverter buildMethodStringConverter() {
        return new StringConverter() {
            public String convertToString(Object o) {
                return (o == null) ? "" : ((MWMethod) o).getName();
            }
        };
    }

    public static NodeSelector buildMethodNodeSelector(final WorkbenchContextHolder contextHolder) {
        return new NodeSelector() {
            public void selectNodeFor(Object item) {
                ProjectNode projectNode = (ProjectNode) contextHolder.getWorkbenchContext().getNavigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectMethod((MWMethod) item, contextHolder.getWorkbenchContext());
            }
        };
    }
}
