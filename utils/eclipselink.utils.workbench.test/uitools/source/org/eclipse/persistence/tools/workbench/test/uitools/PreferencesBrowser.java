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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;

/**
 * Browse the Java preferences, both "system" and "current user".
 * Optionally, a command-line argument can specify a particular
 * set of user preferences to browse.
 */
public class PreferencesBrowser {
	private Preferences preferences;
	Collection windows;
	private Point windowLocation;
	private static final int INIT_X = 300;
	private static final int INIT_Y = 300;

	public static void main(String[] args) throws Exception {
		new PreferencesBrowser().exec(args);
	}

	private PreferencesBrowser() {
		super();
		this.windows = new HashBag();
		this.windowLocation = this.calculateNewWindowLocation();
	}

	private Point calculateNewWindowLocation() {
		if ((this.windowLocation == null) || (this.windowLocation.x == INIT_X + 100)) {
			return new Point(INIT_X, INIT_Y);
		}
		return new Point(this.windowLocation.x + 20, this.windowLocation.y + 20);
	}

	private void exec(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		if ((args == null) || (args.length == 0)) {
			this.preferences = null;
		} else {
			this.preferences = Preferences.userRoot().node(args[0]);
		}
		this.openWindow();
	}

	void openWindow() {
		JFrame window = this.buildWindow();
		this.windows.add(window);
		window.setVisible(true);
	}

	private JFrame buildWindow() {
		JFrame frame = new JFrame(ClassTools.shortClassNameForObject(this));
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this.buildWindowListener());
		frame.getContentPane().add(this.buildPreferencesPanel(), "Center");
		frame.setLocation(this.windowLocation);
		this.windowLocation = this.calculateNewWindowLocation();
		frame.setSize(400, 400);
		return frame;
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Window window = e.getWindow();
				window.setVisible(false);
				window.dispose();
				PreferencesBrowser.this.windows.remove(window);
				if (PreferencesBrowser.this.windows.isEmpty()) {
					System.exit(0);
				}
			}
		};
	}

	private JPanel buildPreferencesPanel() {
		PreferencesPanel panel = new PreferencesPanel(this.preferences);
		panel.addAction(this.buildNewWindowAction());
		return panel;
	}

	private Action buildNewWindowAction() {
		return new AbstractAction("New Window") {
			public void actionPerformed(ActionEvent event) {
				PreferencesBrowser.this.openWindow();
			}
		};
	}

}
