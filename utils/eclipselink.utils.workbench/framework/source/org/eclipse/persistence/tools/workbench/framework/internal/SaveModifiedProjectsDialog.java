/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.CheckList;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;


/**
 * This dialog displays a list of all the nodes that
 * are dirty and need to be saved. The user can select which
 * nodes are to be saved by checking or unchecking nodes in the list.
 * <p>
 * Here is the layout of the dialog:
 * <pre>
 * ______________________________________________
 * |                                            |
 * | Select the projects to save:               |
 * | __________________________________________ |
 * | |x Project1 [Location]                 |^| |
 * | |" Project2 [Location]                 | | |
 * | |x ...                                 ||| |
 * | |                                      | | |
 * | |                                      |v| |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * |  ______________ ________________           |
 * |  | Select All | | Unselect All |           |
 * |  ¯¯¯¯¯¯¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯           |
 * | ------------------------------------------ |
 * | ________             __________ __________ |
 * | | Help |             |   OK   | | Cancel | |
 * | ¯¯¯¯¯¯¯¯             ¯¯¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * </pre>
 */
final class SaveModifiedProjectsDialog
	extends AbstractDialog
{
	/** the list of dirty nodes */
	private Collection dirtyNodes;

	/** the nodes selected from the list of dirty nodes, above, to be saved */
	private ObjectListSelectionModel selectionModel;

	/** hold the check list so we can give it the initial focus */
	private CheckList checkList;

	/** hold these actions so we can enable/disable them */
	private Action selectAllAction;
	private Action unselectAllAction;


	// ********** constructors **********

	/**
	 * by default, initially select all the dirty nodes
	 */
	SaveModifiedProjectsDialog(WorkbenchContext context, Collection dirtyNodes) {
		this(context, dirtyNodes, new ArrayList(dirtyNodes));
	}

	SaveModifiedProjectsDialog(WorkbenchContext context, Collection dirtyNodes, Collection initialSelection) {
		super(context);
		this.initialize(dirtyNodes, initialSelection);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.setName("File.SaveAll");
		this.setTitle(this.resourceRepository().getString("saveModifiedDocuments.title")); 
	} 

	private void initialize(Collection dirtyNodes, Collection initialSelection) {
		this.dirtyNodes = dirtyNodes;
		CollectionValueModel dirtyNodesHolder = new ReadOnlyCollectionValueModel(dirtyNodes);
		selectionModel = new ObjectListSelectionModel(new ListModelAdapter(dirtyNodesHolder));
		checkList = new CheckList(dirtyNodesHolder, selectionModel, this.buildCellRendererAdapter());

		// wait until after the check list is listening to the selection model to set the selected values
		selectionModel.setSelectedValues(dirtyNodes);
	} 

	private CellRendererAdapter buildCellRendererAdapter() {
		return new AbstractCellRendererAdapter() {

			public String buildAccessibleName(Object value) {
				ApplicationNode node = (ApplicationNode) value;
				String accessibleName = node.accessibleName();
				File saveLocation = node.saveFile();

				// saveLocation for untitled document is actually the display string
				if ((accessibleName != null) &&
					 (saveLocation != null) &&
					 (!saveLocation.toString().equals(node.displayString())))
				{
					accessibleName += " [" + saveLocation + "]";
				}

				return accessibleName;
			}

			public Icon buildIcon(Object value) {
				return ((Displayable) value).icon();
			}

			public String buildText(Object value) {
				ApplicationNode node = (ApplicationNode) value;
				String display = node.displayString();
				File saveLocation = node.saveFile();

				// saveLocation for untitled document is actually the display string
				if ((saveLocation != null) && ( ! saveLocation.toString().equals(display))) {
					display = display + " [" + saveLocation + "]";
				}

				return display;
			}
		};
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildMainPanel()
	 */
	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(405, 250));	// use Golden Ratio

		GridBagConstraints constraints = new GridBagConstraints();

		JLabel label = new JLabel(this.resourceRepository().getString("saveModifiedDocuments.message"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("saveModifiedDocuments.message"));
		label.setDisplayedMnemonicIndex(this.resourceRepository().getMnemonicIndex("saveModifiedDocuments.message"));
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 0, 5, 0);
		mainPanel.add(label, constraints);

		label.setLabelFor(checkList);

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH; 
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		mainPanel.add(checkList, constraints);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START; 
		constraints.insets		= new Insets(5,0, 5, 0);
		mainPanel.add(buttonPanel, constraints);

			// Select All button
			selectAllAction = this.buildSelectAllAction();
			JButton selectAllButton = new JButton(selectAllAction);
			selectAllButton.setText(this.resourceRepository().getString("selectAll"));
			selectAllButton.setMnemonic(this.resourceRepository().getMnemonic("selectAll"));
			selectAllButton.setDisplayedMnemonicIndex(this.resourceRepository().getMnemonicIndex("selectAll"));

			constraints.gridx			= 0;
			constraints.gridy			= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);

			buttonPanel.add(selectAllButton, constraints);

			// Unselect All button
			unselectAllAction = this.buildUnselectAllAction();
			JButton unselectAllButton = new JButton(unselectAllAction);
			unselectAllButton.setText(this.resourceRepository().getString("deselectAll"));
			unselectAllButton.setMnemonic(this.resourceRepository().getMnemonic("deselectAll"));
			unselectAllButton.setDisplayedMnemonicIndex(this.resourceRepository().getMnemonicIndex("deselectAll"));

			constraints.gridx			= 1;
			constraints.gridy			= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);
			buttonPanel.add(unselectAllButton, constraints);
		
		return mainPanel;
	}

	protected String helpTopicId() {
		return "dialog.file.saveAll";
	}

	protected Component initialFocusComponent() {
		return checkList;
	}


	// ********** actions **********

	private Action buildSelectAllAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SaveModifiedProjectsDialog.this.selectAllPressed();
			}
		};
	}

	private void selectAllPressed() {
		selectionModel.setSelectionInterval(0, dirtyNodes.size() - 1);
	}

	private Action buildUnselectAllAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SaveModifiedProjectsDialog.this.unselectAllPressed();
			}
		};
	}

	private void unselectAllPressed() {
		selectionModel.clearSelection();
	}


	// ********** public API **********

	/**
	 * Return the dirty nodes selected by the user to be saved.
	 */
	Collection selectedNodes() {
		if ( ! this.wasConfirmed()) {
			throw new IllegalStateException();
		}
		return Arrays.asList(selectionModel.getSelectedValues());
	}

}
