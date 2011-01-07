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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing.dialog;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Tran Le
 */
public class SCSessionCreationDialogUITest extends SCDialogUITest {

	/**
	 * Constructor for SCSessionCreationDialogUITest
	 */
	public SCSessionCreationDialogUITest() {
		super();
	}

	protected String windowTitle() {

		return "Session Creation Dialog Test";
	}

	protected Component buildTestingPanel() {
		
		JPanel testPanel = new JPanel( new GridLayout( 1, 0));
		testPanel.add( this.buildAddSessionButton());

		return testPanel;
	}

	private JButton buildAddSessionButton() {

		JButton browseButton = new JButton( resourceRepository().getString( "ADD_SESSION"));
//		browseButton.setMnemonic( resourceRepository().getMnemonic( "ADD_SESSION"));

		browseButton.addActionListener( new AddNewSessionActionUITest( this.getWorkbenchContext()));

		return browseButton;
	}
	
//	private Action AddSessionAction() {
//		Action action = new AbstractAction( "add session") {
//			public void actionPerformed( ActionEvent event) {
//				SCSessionCreationDialogUITest.this.addSession();
//			}
//		};
//		action.setEnabled(true);
//		return action;
//	}
//	protected void addSession() {
//		JOptionPane.showMessageDialog( this.currentWindow(), "Under Construction...");
//	}

	public static void main(String[] args) throws Exception {
		new SCSessionCreationDialogUITest().exec(args);
	}
	
	private void exec( String[] args) throws Exception {

		setUp();
		
		this.openWindow();
	}
	
	protected void setUp() {
		super.setUp();
		
		windowW = 200;
		windowH = 100;
	}
}
