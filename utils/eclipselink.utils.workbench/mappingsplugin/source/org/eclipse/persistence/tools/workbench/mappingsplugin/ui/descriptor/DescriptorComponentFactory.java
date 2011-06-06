/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
