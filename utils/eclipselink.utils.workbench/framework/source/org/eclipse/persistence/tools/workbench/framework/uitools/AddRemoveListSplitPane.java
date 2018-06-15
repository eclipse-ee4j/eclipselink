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
package org.eclipse.persistence.tools.workbench.framework.uitools;

// JDK
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

// UITools

/**
 * @version 1.0
 * @author Pascal Filion
 */
public class AddRemoveListSplitPane extends JSplitPane
{
    /**
     * The left side of this split pane.
     */
    private AddRemoveListPanel addRemoveListPanel;

    /**
     * Creates a new split pane that has the <code>AddRemoveListPanel</code> on
     * the left side and a properties page on the right side.
     */
    public AddRemoveListSplitPane(AddRemoveListPanel addRemoveListPanel)
    {
        super();
        this.addRemoveListPanel = addRemoveListPanel;
        initialize();
    }

    /**
     * Adds the given listener to the list. The listener will be notified when
     * the list's selection will changed.
     *
     * @param listener The listener to be added
     */
    public void addListSelectionListener(ListSelectionListener listener)
    {
        getAddRemoveListPanel().addListSelectionListener(listener);
    }

    /**
     * Returns th left side component of this split pane.
     *
     * @return A list with Add/Remove buttons
     */
    public AddRemoveListPanel getAddRemoveListPanel()
    {
        return this.addRemoveListPanel;
    }

    /**
     * Initializes this pane.
     */
    protected void initialize()
    {
        setBorder(BorderFactory.createEmptyBorder());

        setLeftComponent(this.addRemoveListPanel);

        BasicSplitPaneDivider divider = ((BasicSplitPaneUI) getUI()).getDivider();
        divider.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Removes the given listener to the list.
     *
     * @param listener The listener to be removed
     */
    public void removeListSelectionListener(ListSelectionListener listener)
    {
        getAddRemoveListPanel().removeListSelectionListener(listener);
    }

    /**
     * Sets the renderer to be used by the list.
     *
     * @param renderer The renderer used to render the items in the list
     */
    public void setCellRenderer(ListCellRenderer renderer)
    {
        getAddRemoveListPanel().setCellRenderer(renderer);
    }
}
