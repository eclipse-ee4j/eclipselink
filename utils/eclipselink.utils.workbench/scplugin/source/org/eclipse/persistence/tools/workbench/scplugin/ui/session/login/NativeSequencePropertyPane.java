/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NativeSequenceAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

/**
 *
 * @see NativeSequenceAdapter
 * @see SequencingPane - The parent container
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
final class NativeSequencePropertyPane extends SequencePropertyPane
{
    /**
     * Creates a new <code>NativeSequencePropertyPane</code>.
     *
     * @param subjectHolder The holder of <code>MWNativeSequence</code>
     * @param workbenchContextHolder The holder of the <code>WorkbenchContext</code>,
     * used to retrieve the localized string, active window, etc
     */
    NativeSequencePropertyPane(ValueModel subjectHolder,
                               WorkbenchContextHolder workbenchContextHolder)
    {
        super(subjectHolder, workbenchContextHolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeLayout()
    {
        GridBagConstraints constraints = new GridBagConstraints();

        // Preallocation Size widgets
        JComponent preallocationSizeWidgets = buildPreallocationSizeWidgets();

        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(0, 0, 0, 0);

        add(preallocationSizeWidgets, constraints);
    }
}
