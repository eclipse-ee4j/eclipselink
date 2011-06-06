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
package org.eclipse.persistence.tools.workbench.scplugin.ui.broker;

// JDK
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.CheckList;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;

// Mapping Workbench

/**
 * This dialog shows the non-managed sessions.
 * <p>
 * Here the layout of this dialog:<br>
 * _____________________________________
 * | Sessions                       x  |
 * |¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯|
 * | Select the session to manage:     |
 * | _________________________________ |
 * | |                             |^| |
 * | | x Session1                  | | |
 * | | o Session2                  ||| |
 * | | x Session3                  ||| |
 * | |   ...                       | | |
 * | |                             |v| |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | --------------------------------- |
 * | __________   _________ __________ |
 * | |  Help  |   |  OK   | | Cancel | |
 * | ¯¯¯¯¯¯¯¯¯¯   ¯¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
final class SessionsListDialog extends AbstractDialog
{
	/**
	 * Keeps a reference unil the main pane is initialized.
	 */
	private final CollectionValueModel itemHolder;

	/**
	 * Keeps a reference unil the main pane is initialized.
	 */
	private final CellRendererAdapter labelDecorator;

	/**
	 * Keeps a reference unil the main pane is initialized.
	 */
	private ObjectListSelectionModel selectionModel;

	/**
	 * Creates a new <code>SessionsListDialog</code>.
	 *
	 * @param context
	 * @param itemHolder The holder of the non-managed {@link SessionAdapter}s
	 * @param selectionModel The model used to store the selected items
	 * @param labelDecorator The {@link LabelDecorator} used to decorate each
	 * item of the list
	 */
	SessionsListDialog(WorkbenchContext context,
							 CollectionValueModel itemHolder,
							 ObjectListSelectionModel selectionModel,
							 CellRendererAdapter labelDecorator)
	{
		super(context, context.getApplicationContext().getResourceRepository().getString("SESSIONS_LIST_DIALOG_TITLE"));

		this.itemHolder = itemHolder;
		this.labelDecorator = labelDecorator;
		this.selectionModel = selectionModel;
	}

	/**
	 * Returns the id identifying this dialog.
	 *
	 * @return The topic ID
	 */
	protected String helpTopicId()
	{
		return "dialog.sessions";
	}

	/**
	 * Initializes the layout of this dialog's main pane.
	 *
	 * @return The fully initialize pane with its widgets
	 */
	protected Component buildMainPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());

		// Sessions list label
		JLabel sessionsListLabel = new JLabel(resourceRepository().getString("SESSIONS_LIST_DIALOG_SESSIONS_LIST"));
		sessionsListLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("SESSIONS_LIST_DIALOG_SESSIONS_LIST"));
		sessionsListLabel.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("SESSIONS_LIST_DIALOG_SESSIONS_LIST"));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(sessionsListLabel, constraints);

		// Sessions list
		CheckList checkList = new CheckList(this.itemHolder, this.selectionModel, this.labelDecorator);
		installSelectionModelListener();

		Dimension size = checkList.getPreferredSize();
		size.width = Math.max(300, size.width);
		size.height = Math.max(100, size.height);
		checkList.setPreferredSize(size);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 0, 0, 0);

		container.add(checkList, constraints);
		sessionsListLabel.setLabelFor(checkList);

		return container;
	}

	/**
	 * Installs a <code>ListSelectionListener</code> on the {@link #selectionModel}
	 * in order to update the enable state of the OK button.
	 */
	private void installSelectionModelListener()
	{
		this.selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting())
					return;

				getOKAction().setEnabled(SessionsListDialog.this.selectionModel.getSelectedValues().length > 0);
			}
		});
	}

	/**
	 * Prepares this dialog to be shown on screen.
	 */
	protected void prepareToShow()
	{
		super.prepareToShow();
		getOKAction().setEnabled(false);
	}
}
