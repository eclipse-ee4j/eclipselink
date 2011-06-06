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

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepositoryFactory;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClasspathClassDescription;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


/**
 * Simple test class for playing around with the ClassChooserPanel.
 * 
 * Optional command line parm:
 * 	a classpath to use to populate the "available" classes list
 */
public class ClassChooserPanelUITest {
	PropertyValueModel selectionHolder = new SimplePropertyValueModel();
	ClassDescriptionRepository repository;


	public static void main(String[] args) {
		new ClassChooserPanelUITest().exec(args);
	}

	private ClassChooserPanelUITest() {
		super();
	}

	private void exec(String[] args) {
		this.repository = this.buildClassDescriptionRepository(args);
		JFrame frame = new JFrame(this.getClass().getName());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this.buildWindowListener());
		frame.getContentPane().add(this.buildClassChooserPanel(), "Center");
		frame.setSize(500, 100);
		frame.setVisible(true);
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.out.println("selected class: " +
						ClassChooserPanelUITest.this.selectionHolder.getValue());
				System.exit(0);
			}
		};
	}

	private ClassChooserPanel buildClassChooserPanel() {
		ClassChooserPanel panel = new ClassChooserPanel(
				this.selectionHolder,
				this.buildClassDescriptionRepositoryFactory(),
				this.buildClassDescriptionAdapter(),
				this.buildWorkbenchContextHolder()
		);
		panel.setAllowNullSelection(true);
		return panel;
	}
	
	private ClassDescriptionRepositoryFactory buildClassDescriptionRepositoryFactory() {
		return new ClassDescriptionRepositoryFactory() {
			public ClassDescriptionRepository createClassDescriptionRepository() {
				return ClassChooserPanelUITest.this.repository;
			}
		};
	}
	
	ClassDescriptionRepository buildClassDescriptionRepository(String[] args) {
		String classpath;
		if ((args == null) || (args.length == 0)) {
			classpath = System.getProperty("java.class.path");
		} else {
			classpath = args[0];
		}
		return ClassChooserDialogUITest.buildClassDescriptionRepository(classpath);
	}

	private ClassDescriptionAdapter buildClassDescriptionAdapter() {
		return new ClasspathClassDescription.Adapter();
	}

	private WorkbenchContextHolder buildWorkbenchContextHolder() {
		return new DefaultWorkbenchContextHolder(new TestWorkbenchContext());
	}
	
}
