/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class DefaultSequencePropertyPane extends SequencePropertyPane
{
	/**
	 * Creates a new <code>DefaultSequencePropertyPane</code>.
	 *
	 * @param subjectHolder The holder of <code>MWDefaultSequence</code>
	 * @param workbenchContextHolder The holder of the <code>WorkbenchContext</code>,
	 * used to retrieve the localized string, active window, etc
	 */
	DefaultSequencePropertyPane(ValueModel subjectHolder,
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