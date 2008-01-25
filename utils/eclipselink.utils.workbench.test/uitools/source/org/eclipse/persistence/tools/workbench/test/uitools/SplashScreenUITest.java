/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.uitools.SplashScreen;

/**
 * Display a splash screen until the user confirms the prompt.
 */
public class SplashScreenUITest {

	public static void main(String[] args) throws Exception {
		new SplashScreenUITest().exec(args);
	}

	public SplashScreenUITest() {
		super();
	}

	public void exec(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		String copyright = "© 1997, 2004 Oracle Corporation. All rights reserved.";
		Icon image = new ImageIcon(this.getClass().getResource("/test.splash.screen.gif"));
		SplashScreen ss = new SplashScreen(new Frame(), copyright, image, 5000);
		ss.start();
		JOptionPane.showMessageDialog(ss, "Press OK to close the splash screen.");
		ss.stop();
		System.exit(0);
	}

}
