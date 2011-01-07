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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin.ui.meta;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.ClasspathPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginResourceBundle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * Simple test class for playing around with the ClasspathPanel.
 */
public class ClasspathPanelTest {
	MWRelationalProject project;

	public static void main(String[] args) {
		new ClasspathPanelTest().exec(args);
	}

	private ClasspathPanelTest() {
		super();
	}

	private void exec(String[] args) {
		this.project = this.buildProject();
		JFrame frame = new JFrame(this.getClass().getName());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this.buildWindowListener());
		frame.getContentPane().add(this.buildMainPanel(), "Center");
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	private MWRelationalProject buildProject() {
		MWRelationalProject result = new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);
		Classpath cp = Classpath.javaClasspath();
		Classpath.Entry[] entries = cp.getEntries();
		for (int i = 0; i < entries.length; i++) {
			result.getRepository().addClasspathEntry(entries[i].fileName());
		}
		return result;
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.out.println("classpath:");
				for (Iterator stream = ClasspathPanelTest.this.project.getRepository().classpathEntries(); stream.hasNext(); ) {
					System.out.print("\t");
					System.out.print(stream.next());
					System.out.println();
				}
				System.out.println("*****");
				System.exit(0);
			}
		};
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.buildClasspathPanel(), BorderLayout.CENTER);
		mainPanel.add(this.buildBackdoorButton(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private ClasspathPanel buildClasspathPanel() {
		return new ClasspathPanel(this.buildWorkbenchContext().getApplicationContext(), this.buildEntriesHolder(), buildLocationHolder());
	}

	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext(MappingsPluginResourceBundle.class, "org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginIconResourceFileNameMap");
	}
	
	private ListValueModel buildEntriesHolder() {
		return new ListAspectAdapter(MWClassRepository.CLASSPATH_ENTRIES_LIST, this.project.getRepository()) {
			protected ListIterator getValueFromSubject() {
				return ((MWClassRepository) this.subject).classpathEntries();
			}
			protected int sizeFromSubject() {
				return ((MWClassRepository) this.subject).classpathEntriesSize();
			}

			public void addItem(int index, Object item) {
				((MWClassRepository) this.subject).addClasspathEntry(index, (String) item);
			}
			
			public void addItems(int index, List items) {
				((MWClassRepository) this.subject).addClasspathEntries(index, items);
			}
			
			public Object removeItem(int index) {
				return ((MWClassRepository) this.subject).removeClasspathEntry(index);
			}
			
			public List removeItems(int index, int length) {
				return ((MWClassRepository) this.subject).removeClasspathEntries(index, length);
			}
			
			public Object replaceItem(int index, Object item) {
				return ((MWClassRepository) this.subject).replaceClasspathEntry(index, (String) item);
			}
		};
	}
	
	private PropertyValueModel buildLocationHolder() {
		return new SimplePropertyValueModel(this.project.getSaveDirectory());
	}
	
	private JButton buildBackdoorButton() {
		return new JButton(this.buildBackdoorAction());
	}

	private Action buildBackdoorAction() {
		Action action = new AbstractAction("test backdoor") {
			public void actionPerformed(ActionEvent event) {
				ClasspathPanelTest.this.testBackdoor();
			}
		};
		action.setEnabled(true);
		return action;
	}

	/**
	 * Add an entry to the repository directly;
	 * the UI should update appropriately.
	 */
	void testBackdoor() {
		this.project.getRepository().addClasspathEntry(FileTools.CURRENT_WORKING_DIRECTORY_NAME);
	}

}
