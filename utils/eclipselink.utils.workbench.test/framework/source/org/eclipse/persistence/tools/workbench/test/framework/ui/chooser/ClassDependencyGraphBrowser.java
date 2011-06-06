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
package org.eclipse.persistence.tools.workbench.test.framework.ui.chooser;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassDependencyGraph;

/**
 * Simple class for playing around with the ClassDependencyGraphPanel.
 * 
 * Optional command line parm:
 * 	the name of a jar (or class folder) to use to populate the middle tree
 */
public class ClassDependencyGraphBrowser {
	private PropertyValueModel graphHolder = new SimplePropertyValueModel(null);

	public static void main(String[] args) {
		new ClassDependencyGraphBrowser().exec(args);
	}

	/**
	 * Default constructor.
	 */
	private ClassDependencyGraphBrowser() {
		super();
	}

	private void exec(String[] args) {
		this.graphHolder.setValue(this.buildGraph(args));
		JFrame frame = new JFrame(ClassTools.shortClassNameForObject(this) + ": " + this.buildClasspathEntry(args));
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this.buildWindowListener());
		frame.getContentPane().add(this.buildClassDependencyGraphPanel(), "Center");
		frame.setLocation(300, 300);
		frame.setSize(800, 400);
		frame.setVisible(true);
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}

	private ClassDependencyGraphPanel buildClassDependencyGraphPanel() {
		return new ClassDependencyGraphPanel(this.graphHolder);
	}

	private ClassDependencyGraph buildGraph(String[] args) {
		return new ClassDependencyGraph(this.buildClasspathEntry(args));
	}

	private String buildClasspathEntry(String[] args) {
		if ((args == null) || (args.length == 0)) {
			return Classpath.locationFor(ClassTools.class);
//			return ClasspathTools.javaClasspathEntryFor(ValueHolderInterface.class);
		}
		return args[0];
	}

}
