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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


/**
 * This panel looks like a ListChooser, but the button launches a
 * class chooser dialog. The list of classes presented to the user
 * is derived from the specified class description repository.
 * 
 * Since this panel simply selects a type, it will *not* refresh
 * the chosen type. This should be OK in most situations.
 * If you need to use the selected class to generate a list
 * of methods, or some such, you will need to listen for the
 * selection to change and check whether the selection is
 * a "stub". If it is, and you need a list of methods, you will
 * need to refresh the selected type and notify the user of
 * any problems. Alternatively, you could provide the user with
 * a manual way to refresh the list of methods.
 */
public class ClassChooserPanel extends AbstractPanel {

	/** The current selection (taken from the "class description" repository). */
	private PropertyValueModel selectionHolder;

	/** The repository passed to the class chooser dialog. */
	private ClassDescriptionRepositoryFactory classDescriptionRepositoryFactory;

	/** An adapter for the repository and selection. */
	ClassDescriptionAdapter classDescriptionAdapter;

	/** Whether the user is allowed to clear the selection - default is false. */
	private boolean allowNullSelection;
	
	/** Allows for custom button label text and mnemonic */
	private String buttonKey; 


	// ********** constructor/initialization **********

	/**
	 * the default allows primitives to be included in the pick list
	 */
	public ClassChooserPanel(
			PropertyValueModel selectionHolder,
			ClassDescriptionRepositoryFactory classDescriptionRepositoryFactory,
			ClassDescriptionAdapter classDescriptionAdapter,
			WorkbenchContextHolder contextHolder
	) {
		this(selectionHolder, classDescriptionRepositoryFactory, classDescriptionAdapter, null, contextHolder);
	}

	/**
	 * the default allows primitives to be included in the pick list
	 */
	public ClassChooserPanel(
			PropertyValueModel selectionHolder,
			ClassDescriptionRepositoryFactory classDescriptionRepositoryFactory,
			ClassDescriptionAdapter classDescriptionAdapter,
			JLabel label,
			WorkbenchContextHolder contextHolder
	) {
		this(selectionHolder, classDescriptionRepositoryFactory, classDescriptionAdapter, label, contextHolder, "CLASS_CHOOSER_BROWSE_BUTTON");
	}

	/**
	 * the default allows primitives to be included in the pick list
	 * allows custom button key primarily for differentiation of mnemonic, key must exist in FrameworkResourceBundle
	 */
	public ClassChooserPanel(
			PropertyValueModel selectionHolder,
			ClassDescriptionRepositoryFactory classDescriptionRepositoryFactory,
			ClassDescriptionAdapter classDescriptionAdapter,
			JLabel label,
			WorkbenchContextHolder contextHolder,
			String buttonKey
	) {
		super(contextHolder);
		this.selectionHolder = selectionHolder;
		this.classDescriptionRepositoryFactory = classDescriptionRepositoryFactory;
		this.classDescriptionAdapter = classDescriptionAdapter;
		this.allowNullSelection = false;
		this.buttonKey = buttonKey;
		this.initializeLayout(label);
	}

	private void initializeLayout(JLabel label) {
		GridBagConstraints constraints = new GridBagConstraints();

		// the (read-only) text field that displays the current selection
		JTextField textField = new JTextField(new DocumentAdapter(this.buildClassNameHolder()), null, 1);
		textField.setEditable(false);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);

		this.add(textField, constraints);

		if (label != null) {
			textField.putClientProperty("labeledBy", label);
		}

		// the button that brings up the class chooser dialog
		JButton button = buildButton(this.buttonKey);
		button.addActionListener(this.buildActionListener());

		if (label != null) {
			label.setLabelFor(button);
			SwingComponentFactory.updateButtonAccessibleName(label, button);
		}
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 0);

		this.add(button, constraints);
		this.addAlignRight(button);
	}

	private PropertyValueModel buildClassNameHolder() {
		return new TransformationPropertyValueModel(this.selectionHolder) {
			protected Object transform(Object value) {
				return (value == null) ? "" : ClassChooserPanel.this.classDescriptionAdapter.className(value).replace('$', '.');
			}
			protected Object reverseTransform(Object value) {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * this listener opens up the class chooser dialog
	 * when the button is pressed
	 */
	private ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClassChooserPanel.this.promptUserToSelectClass();
			}
		};
	}


	// ********** queries **********

	private ClassDescriptionRepository buildRepository() {
		return this.classDescriptionRepositoryFactory.createClassDescriptionRepository();
	}

	private Object getSelection() {
		return this.selectionHolder.getValue();
	}


	// ********** behavior **********

	/**
	 * Do NOT automatically refresh the types when opening the class chooser dialog.
	 * Allow the user to control when the list of available types is refreshed, with the "Refresh" button.
	 * @see MWClassRepository#refreshExternalClassDescriptions()
	 */
	void promptUserToSelectClass() {
		ClassChooserDialog dialog = this.buildDialog();
		dialog.setAllowNullSelection(this.allowNullSelection);
		dialog.setInitialSelection(this.getSelection());
		dialog.show();
		if (dialog.wasConfirmed()) {
			this.selectionHolder.setValue(dialog.selection());
		}
		// try to force all the objects generated by the dialog to be garbage-collected
		dialog = null;
		ClassChooserDialog.gc();
	}

	private ClassChooserDialog buildDialog() {
		return ClassChooserDialog.createDialog(
				this.buildRepository(),
				this.classDescriptionAdapter,
				this.getWorkbenchContext()
		);
	}
	

	// ********** public API **********

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (int i = this.getComponentCount(); i-- > 0; ) {
			this.getComponent(i).setEnabled(enabled);
		}
	}

	/**
	 * Set whether the user is allowed to clear the selection.
	 * The default is false.
	 */
	public void setAllowNullSelection(boolean allowNullSelection) {
		this.allowNullSelection = allowNullSelection;
	}

}
