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
package org.eclipse.persistence.tools.workbench.uitools.chooser;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

/**
 * This implementation of ListChooser.Browser uses a
 * JOptionPane to prompt the user for the selection. Subclasses 
 * can change the dialog's title, message, and/or icon.
 */
public class SimpleListBrowser
	implements ListChooser.ListBrowser 
{
	/** Default constructor */
	protected SimpleListBrowser() {
		super();
	}
	
	/**
	 * Prompt the user using a JOptionPane.
	 */
	public void browse(ListChooser chooser) {
		Object selection = 
			JOptionPane.showInputDialog(
				chooser, 
				this.message(chooser), 
				this.title(chooser), 
				this.messageType(chooser), 
				this.icon(chooser), 
				this.selectionValues(chooser), 
				this.initialSelectionValue(chooser)
			);
		
		if (selection != null) {
			chooser.getModel().setSelectedItem(selection);
		}
	}
	
	protected Object message(JComboBox comboBox) {
		return null;
	}
	
	protected String title(JComboBox comboBox) {
		return null;
	}
	
	protected int messageType(JComboBox comboBox) {
		return JOptionPane.QUESTION_MESSAGE;
	}
	
	protected Icon icon(JComboBox comboBox) {
		return null;
	}
	
	protected Object[] selectionValues(JComboBox comboBox) {
		return this.convertToArray(comboBox.getModel());
	}
	
	protected Object initialSelectionValue(JComboBox comboBox) {
		return comboBox.getModel().getSelectedItem();
	}
	
	/**
	 * Convert the list of objects in the specified list model
	 * into an array.
	 */
	protected Object[] convertToArray(ListModel model) {
		int size = model.getSize();
		Object[] result = new Object[size];
		for (int i = 0; i < size; i++) {
			result[i] = model.getElementAt(i);
		}
		return result;
	}
}
