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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.uitools.swing.ArrowIcon;
import org.eclipse.persistence.tools.workbench.uitools.swing.CompositeIcon;


/**
 * When clicked, this button displays a pop-up menu just below it;
 * sorta like a combo-box.
 */
public class DropDownButton extends JButton {
	/** the pop-up menu displayed when the button is clicked */
	private JPopupMenu menu;


	public DropDownButton() {
		super();
		this.addActionListener(this.buildActionListener());
	}

	/**
	 * Construct a "drop-down" button based on the specified action.
	 * The specified menu will be displayed when the button is clicked.
	 * The action will provide the button's text, icon, etc. and does
	 * not necessarily need to implement any behavior in the method
	 * #actionPerformed(ActionEvent).
	 */
	public DropDownButton(Action action, JPopupMenu menu) {
		this();
		this.setAction(action);
		this.setMenu(menu);
	}

	private ActionListener buildActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DropDownButton.this.displayPopupMenu();
			}
		};
	}

	/**
	 * The button was clicked, display the pop-up menu.
	 */
	void displayPopupMenu() {
		this.menu.show(this, 0, this.getSize().height);
	}

	/**
	 * Append a down-pointing arrow to the specified icon.
	 */
	public void setIcon(Icon defaultIcon) {	
		super.setIcon(new CompositeIcon(defaultIcon, 4, new ArrowIcon(7, 7, SwingConstants.SOUTH)));
	}

	public JPopupMenu getMenu() {
		return this.menu;
	}

	public void setMenu(JPopupMenu menu) {
		this.menu = menu;
	}

}
