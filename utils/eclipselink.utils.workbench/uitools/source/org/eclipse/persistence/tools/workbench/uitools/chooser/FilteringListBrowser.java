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
package org.eclipse.persistence.tools.workbench.uitools.chooser;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import org.eclipse.persistence.tools.workbench.uitools.FilteringListPanel;

/**
 * This implementation of LongListComponent.Browser uses a
 * JOptionPane to prompt the user for the selection. The JOPtionPane
 * is passed a FilteringListPanel to assist the user in making
 * a selection.
 */
public class FilteringListBrowser 
	implements ListChooser.ListBrowser 
{
	private FilteringListPanel panel;

	/**
	 * Default constructor.
	 */
	public FilteringListBrowser() {
		super();
		this.panel = this.buildPanel();
	}

	protected FilteringListPanel buildPanel() {
		return new LocalFilteringListPanel();
	}

	/**
	 * Prompt the user using a JOptionPane with a filtering
	 * list panel.
	 */
	public void browse(ListChooser chooser) {	
		this.initializeCellRenderer(chooser);
		
		int option = 
			JOptionPane.showOptionDialog(
				chooser, 
				this.message(chooser), 
				this.title(chooser), 
				this.optionType(chooser), 
				this.messageType(chooser), 
				this.icon(chooser), 
				this.selectionValues(chooser), 
				this.initialSelectionValue(chooser)
		);
		
		if (option == JOptionPane.OK_OPTION) {
			chooser.getModel().setSelectedItem(this.panel.getSelection());
		}
		
		// clear the text field so the list box is re-filtered
		this.panel.getTextField().setText("");
	}
	
	protected void initializeCellRenderer(JComboBox comboBox) {
		// default behavior should be to use the cell renderer from the combobox.
		this.panel.getListBox().setCellRenderer(comboBox.getRenderer());
	}

	/**
	 * the message can be anything - here we build a component
	 */
	protected Object message(JComboBox comboBox) {
		this.panel.setCompleteList(this.convertToArray(comboBox.getModel()));
		this.panel.setSelection(comboBox.getModel().getSelectedItem());
		return this.panel;
	}

	protected String title(JComboBox comboBox) {
		return null;
	}

	protected int optionType(JComboBox comboBox) {
		return JOptionPane.OK_CANCEL_OPTION;
	}

	protected int messageType(JComboBox comboBox) {
		return JOptionPane.QUESTION_MESSAGE;
	}

	protected Icon icon(JComboBox comboBox) {
		return null;
	}

	protected Object[] selectionValues(JComboBox comboBox) {
		return null;
	}

	protected Object initialSelectionValue(JComboBox comboBox) {
		return null;
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
	
	
	// ********** custom panel **********
	
	protected class LocalFilteringListPanel extends FilteringListPanel {
	
		protected LocalFilteringListPanel() {
			super(new Object[0], null);
		}
	
		/**
		 * Disable the performance tweak because JOptionPane
		 * will try open wide enough to disable the horizontal scroll bar;
		 * and it looks a bit clumsy.
		 * @see org.eclipse.persistence.tools.workbench.uitools.FilteringListPanel#prototypeCellValue()
		 */
		protected String prototypeCellValue() {
			return null;
		}
	
	}
}
