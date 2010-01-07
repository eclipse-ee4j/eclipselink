/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;


/**
 * 
 */
final class PreferencesDialog extends AbstractDialog {

	/** the view shared throughout the application */
	private PreferencesView view;

	/** custom buttons */
	private Action importAction;
	private Action exportAction;


	// ********** constructor **********

	/**
	 * Construct a preferences dialog with the specified context and view.
	 * Each application has a single view that is passed to
	 * each new preferences dialog when it is created. We assume that
	 * there will never be more than one preferences dialog at a time.
	 */
	PreferencesDialog(WorkbenchContext context, PreferencesView view) {
		super(context, context.getApplicationContext().getResourceRepository().getString("PREFERENCES.DIALOG.TITLE"));
		this.view = view;
	}


	// ********** AbstractDialog implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#initializeActions()
	 */
	protected void initializeActions() {
		super.initializeActions();
		this.importAction = buildImportAction();
		this.exportAction = buildExportAction();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildMainPanel()
	 */
	protected Component buildMainPanel() {
		return this.view.getComponent();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildCustomActions()
	 */
	protected Iterator buildCustomActions() {
		Collection customActions = new ArrayList();
		customActions.add(this.importAction);
		customActions.add(this.exportAction);
		return customActions.iterator();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#helpTopicId()
	 */
	protected String helpTopicId() {
		return "preferences";
	}

	/**
	 * override to explicitly control size
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#prepareToShow()
	 */
	protected void prepareToShow() {
		this.setSize(600, 400);
		this.setLocationRelativeTo(this.getParent());
		//this was necessary for the custom mouse and key listeners to work on the preferences node tree
		//with out this, it gets automatically registered when the main panel is registered with its id so
		//the individual ids no longer work.
		this.helpManager().removeTopicID(this.view.getView().getTree());
		// super.prepareToShow();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#initialFocusComponent()
	 */
	protected Component initialFocusComponent() {
		return this.view.initialFocusComponent();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#okConfirmed()
	 */
	protected void okConfirmed() {
		this.view.triggerAccept();
		super.okConfirmed();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#cancelPressed()
	 */
	protected void cancelPressed() {
		this.view.triggerReset();
		super.cancelPressed();
	}


	// ********** behavior **********

	protected Action buildImportAction() {
		return new AbstractAction(this.buildImportText()) {
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.importPressed();
			}
		};
	}	

	protected String buildImportText() {
		return this.resourceRepository().getString("PREFERENCES.DIALOG.IMPORT");
	}

	protected void importPressed() {
		JFileChooser chooser = new JFileChooser() {
			public void approveSelection() {
				File file = getSelectedFile();
				if (!file.exists()) {
					JOptionPane.showMessageDialog(
						this,
						resourceRepository().getString("PREFERENCES.DIALOG.IMPORT.INVALID_IMPORT_FILE", file.getPath()),
						getApplicationContext().getApplication().getShortProductName(),
						JOptionPane.ERROR_MESSAGE
					);
				}
				else {
					super.approveSelection();
				}
			}
		};
		int result = chooser.showOpenDialog(this.currentWindow());
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}
		this.view.importPreferences(chooser.getSelectedFile());
		this.view.triggerReset();
		JOptionPane.showMessageDialog(this.currentWindow(), resourceRepository().getString("PREFERENCES.DIALOG.IMPORT.SUCCESS.DIALOG"), this.getApplicationContext().getApplication().getShortProductName(), JOptionPane.INFORMATION_MESSAGE);
	}

	protected Action buildExportAction() {
		return new AbstractAction(this.buildExportText()) {
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.exportPressed();
			}
		};
	}	

	protected String buildExportText() {
		return this.resourceRepository().getString("PREFERENCES.DIALOG.EXPORT");
	}

	protected void exportPressed() {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showSaveDialog(this.currentWindow());
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}
		this.view.exportPreferences(chooser.getSelectedFile());
		JOptionPane.showMessageDialog(this.currentWindow(), resourceRepository().getString("PREFERENCES.DIALOG.EXPORT.SUCCESS.DIALOG"), this.getApplicationContext().getApplication().getShortProductName(), JOptionPane.INFORMATION_MESSAGE);
	}

}
