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
package org.eclipse.persistence.tools.workbench.framework.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This dialog is similar to it superclass, AbstractDialog, with the added 
 * value of an error message label below the main panel. A subclass
 * can set this error message as needed so that it can inform the user 
 * something incorrect has been entered.
 * 
 * <pre>
 *   ________________________________________
 *   | Title                                |
 *   |¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯|
 *   | ____________________________________ |
 *   | |                                  | |
 *   | |                                  | |
 *   | |          Main panel              | |
 *   | |                                  | |
 *   | |                                  | |
 *   | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 *   | Error Message (or Warning Message)   |
 *   | ____________________________________ |
 *   | ______        ________ ____ ________ |
 *   | |Help|        |Custom| |OK| |Cancel| |
 *   | ¯¯¯¯¯¯        ¯¯¯¯¯¯¯¯ ¯¯¯¯ ¯¯¯¯¯¯¯¯ |
 *   ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * </pre>
 * If there is an error message, it will be shown.  If there is a warning
 * message, it will only be shown if there is no error message.
 * Warning messages have a different icon than error messages.
 * 
 * Subclasses can CALL the following methods:
 *   - setErrorMessage(String)
 *   - setErrorMessageKey(String)
 *   - clearErrorMessage()
 *   - setWarningMessage(String)
 *   - setWarningMessageKey(String)
 *   - clearWarningMessage()
 */
public abstract class AbstractValidatingDialog 
	extends AbstractDialog 
{
	/** Shown if there is an error */
	private JLabel errorMessageLabel;
	
	/** Shown if there is a warning, but only if there is also no error */
	private JLabel warningMessageLabel;

	/** Used to fire the accessible event that JAWS will use to read dynamic text. */
	JLabel accessibleLabel;


	// ********** constructors **********

	protected AbstractValidatingDialog(WorkbenchContext context) {
		super(context);
	}

	protected AbstractValidatingDialog(WorkbenchContext context, String title) {
		super(context, title);
	}

	protected AbstractValidatingDialog(WorkbenchContext context, String title, Dialog owner) {
		super(context, title, owner);
	}

	// ********** initialization **********

	protected void initializeContentPane() {
		this.container.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		container.setOpaque(false);
		contentPane.add(container, BorderLayout.CENTER);

		// main panel
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;	
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.container.add(this.buildMainPanel(), constraints);
		
		// error message
		this.errorMessageLabel = this.buildErrorMessageLabel();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.container.add(this.errorMessageLabel, constraints);
		
		// warning message
		this.warningMessageLabel = this.buildWarningMessageLabel();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.container.add(this.warningMessageLabel, constraints);
		
		// separator
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;	
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 5, 10, 5);
		this.container.add(new JSeparator(), constraints);

		// button panel
		JPanel buttonPanel = this.buildButtonPanel();
		buttonPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;	
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		this.container.add(buttonPanel, constraints);

		// Used for accessibility purposes
		StatusBarPane statusBarPane = new StatusBarPane();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;	
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		this.container.add(statusBarPane, constraints);
	}

	// ********** opening **********

	/* 
	 * Set the errorMessageLabel to not visible so the dialog
	 * will not resize or get squished when the error is shown.
	 * If the label is made invisible before the dialog is
	 * opened, its size will not be taken into account.
	 * Blame GridBagLayout....
	 */
	protected void windowOpened() {
		super.windowOpened();
		
		if (StringTools.stringIsEmpty(this.errorMessageLabel.getText())) {
			this.errorMessageLabel.setVisible(false);
		}
		
		if (this.errorMessageLabel.isVisible() || StringTools.stringIsEmpty(this.warningMessageLabel.getText())) {
			this.warningMessageLabel.setVisible(false);
		}
	}


	// ********** error message **********

	protected JLabel buildErrorMessageLabel() {
		JLabel label = new JLabel();
		label.setIcon(this.resourceRepository().getIcon("error"));
		label.setPreferredSize(new Dimension(300, 16));
		return label;
	}

	protected void clearErrorMessage() {
		this.setErrorMessage(null);
	}

	protected JLabel getErrorMessageLabel() {
		return this.errorMessageLabel;
	}

	protected void setErrorMessage(String message) {
		this.errorMessageLabel.setText(message);

		boolean visible = !StringTools.stringIsEmpty(message);

		if (this.errorMessageLabel.isVisible() != visible && this.isVisible()) {
			this.errorMessageLabel.setVisible(visible);
		}
		
		// set the warning message to invisible if the error message is visible
		if (this.errorMessageLabel.isVisible() && this.warningMessageLabel.isVisible()) {
			this.warningMessageLabel.setVisible(false);
		}

		this.updateAccessibleMessage(message);
	}

	/**
	 * convenience method for simple error message
	 */
	protected void setErrorMessageKey(String key) {
		this.setErrorMessage(this.resourceRepository().getString(key));
	}

	/**
	 * convenience method for simple error message
	 */
	protected void setErrorMessageKey(String key, Object argument) {
		this.setErrorMessage(this.resourceRepository().getString(key, argument));
	}


	// ********** warning message **********

	protected JLabel buildWarningMessageLabel() {
		JLabel label = new JLabel();
		label.setIcon(this.resourceRepository().getIcon("warning"));
		label.setPreferredSize(new Dimension(300, 16));
		return label;
	}

	protected void clearWarningMessage() {
		this.setWarningMessage(null);
	}

	protected JLabel getWarningMessageLabel() {
		return this.warningMessageLabel;
	}

	protected void setWarningMessage(String message) {
		this.warningMessageLabel.setText(message);

		boolean visible = ! StringTools.stringIsEmpty(message) && ! this.errorMessageLabel.isVisible();
		
		if (this.warningMessageLabel.isVisible() != visible && this.isVisible()) {
			this.warningMessageLabel.setVisible(visible);
		}			

		this.updateAccessibleMessage(message);
	}

	/**
	 * convenience method for simple error message
	 */
	protected void setWarningMessageKey(String key) {
		this.setWarningMessage(this.resourceRepository().getString(key));
	}

	/**
	 * convenience method for simple error message
	 */
	protected void setWarningMessageKey(String key, Object argument) {
		this.setWarningMessage(this.resourceRepository().getString(key, argument));
	}

	/**
	 * Updates the accessible description. This method does something only if the
	 * accessible label was initialized.
	 */
	protected void updateAccessibleMessage(String message) {
		if (this.accessibleLabel != null) {
			this.accessibleLabel.setText(message);
		}
	}

	
	/**
	 * This panel takes care to support accessibility regarding the title and
	 * description to be read by JAWS.
	 */
	private class StatusBarPane extends JPanel {

		public StatusBarPane() {
			super(new BorderLayout());
		}

		private void buildStatusBar() {
			AbstractValidatingDialog.this.accessibleLabel = new AccessibleLabel();
			AbstractValidatingDialog.this.accessibleLabel.setVisible(false);

			StatusBar statusBar = new StatusBar();
			statusBar.setVisible(false);
			statusBar.add(AbstractValidatingDialog.this.accessibleLabel);

			add(statusBar, BorderLayout.CENTER);
			validate();
		}

		/**
		 * Returns the <code>AccessibleContext</code> associated with this
		 * <code>ContentPane</code>.
		 * 
		 * @return An <code>AccessibleContentPane</code> that serves as the
		 * <code>AccessibleContext</code> of this <code>ContentPane</code>
		 */
		public AccessibleContext getAccessibleContext() {
			if (this.accessibleContext == null) {
				this.accessibleContext = new AccessibleStatusBarPane();
				buildStatusBar();
			}

			return this.accessibleContext;
		}

		/**
		 * The <code>AccessibleContext</code> of this pane.
		 */
		protected class AccessibleStatusBarPane extends AccessibleJPanel {
			// nada
		}

		/**
		 * This <code>JLabel</code> is intended to fire an event that will ask
		 * JAWS to read the description.
		 */
		private class AccessibleLabel extends JLabel {

			public void setText(String text) {
				String oldText = getText();
				super.setText(text);

				if (this.accessibleContext != null) {
					this.accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, oldText, text);
				}
			}
		}

		/**
		 * This <code>StatusBar</code> is required for JAWS to read the accessible
		 * label when a new text has been set.
		 */
		private class StatusBar extends JPanel {

			public AccessibleContext getAccessibleContext() {
				if (this.accessibleContext == null) {
					this.accessibleContext = new AccessibleStatusBar();
				}
				return this.accessibleContext;
			}

			protected class AccessibleStatusBar extends AccessibleJPanel {
				public AccessibleRole getAccessibleRole() {
					return AccessibleRole.STATUS_BAR;
				}
			}
		}
	}
}
