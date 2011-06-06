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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.uitools.LabelArea;


/**
 * A dialog that can be displayed in its own thread to display a message to user
 * while waiting for some process to end.
 */
public final class WaitDialog extends JDialog {

	private LabelArea message;

	public WaitDialog(Frame owner, Icon icon, String title, String message) {
		super(owner, true); // true = modal
		this.initialize(owner, icon, title, message);
	}

	public WaitDialog(Dialog owner, Icon icon, String title, String message) {
		super(owner, true); // true = modal
		this.initialize(owner, icon, title, message);
	}

	private void initialize(Window owner, Icon icon, String title, String specificMessage) {
		this.setTitle(title);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setResizable(false);

		Container contentPane = this.getContentPane();
		GridBagConstraints constraints = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		// icon
		JLabel iconLabel = new JLabel();
		iconLabel.setIcon(icon);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(15, 20, 10, 0);
		contentPane.add(iconLabel, constraints);

		// message
		this.message = new LabelArea(specificMessage);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(20, 15, 15, 15);
		contentPane.add(this.message, constraints);

		this.pack();
		this.setSize(Math.max(400, this.getWidth()), this.getHeight());
			
		// center on the owner
		Point ownerLocation = owner.getLocationOnScreen();
		int x = ownerLocation.x + ((owner.getWidth() - this.getWidth()) / 2);
		x = Math.max(0, x);
		int y = ownerLocation.y + ((owner.getHeight() - this.getHeight()) / 2);
		y = Math.max(0, y);
		this.setLocation(x, y);
	}

	/**
	 * Allows the text to be changed during the process.
	 */
	public void setMessage(String text) {
		this.message.setText(text);
	}
}
