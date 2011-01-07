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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.ClasspathPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public abstract class AbstractProjectGeneralPropertiesPage extends ScrollablePropertiesPage {

	private PropertyValueModel projectSaveDirectoryHolder;
	
	protected AbstractProjectGeneralPropertiesPage(PropertyValueModel projectNodeHolder, WorkbenchContextHolder contextHolder) {
		super(projectNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel selectionNodeHolder) {
		super.initialize(selectionNodeHolder);
		this.projectSaveDirectoryHolder = this.buildProjectSaveDirectoryHolder();
	}
	
	protected final JComponent buildProjectSaveLocationPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());

		//  label
		JLabel projectSaveLocationLabel = this.buildLabel("SAVE_LOCATION_TEXT_FIELD_LABEL");

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);

		panel.add(projectSaveLocationLabel, constraints);

		// text field
		JTextField projectSaveLocationField = new JTextField(this.buildSaveLocationDocumentAdapter(), null, 1);	
		projectSaveLocationField.setEditable(false);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);

		panel.add(projectSaveLocationField, constraints);
		this.addHelpTopicId(projectSaveLocationField, this.helpTopicId() + ".location");
		projectSaveLocationLabel.setLabelFor(projectSaveLocationField);

		this.addHelpTopicId(panel, this.helpTopicId());
		return panel;
	}

	protected final JComponent buildCommentPanel() {
		return SwingComponentFactory.buildCommentPanel(this.getSelectionHolder(), this.resourceRepository());
	}

	protected final AbstractPanel buildClassPathPanel() {
		ClasspathPanel classpathPanel = new ClasspathPanel(this.getApplicationContext(), this.buildClasspathHolder(), this.projectSaveDirectoryHolder);
		classpathPanel.setDefaultClasspathDirectoryHolder(this.buildDefaultClasspathDirectoryHolder());
		return classpathPanel;
	}

	protected MWProject getProject() {
		return (MWProject) this.getSelectionHolder().getValue();
	}

	File projectSaveDirectory() {
		return this.getProject().getSaveDirectory();
	}

	String mostRecentClasspathDirectoryPreference() {
		return this.preferences().get(ClasspathPanel.MOST_RECENT_CLASSPATH_DIRECTORY_PREFERENCE, null);
	}

	void setMostRecentClasspathDirectoryPreference(String dirName) {
		this.preferences().put(ClasspathPanel.MOST_RECENT_CLASSPATH_DIRECTORY_PREFERENCE, dirName);
	}

	private ClasspathPanel.DefaultClasspathDirectoryHolder buildDefaultClasspathDirectoryHolder() {
		return new ClasspathPanel.DefaultClasspathDirectoryHolder() {
			public File getDefaultClasspathDirectory() {
				String dirName = AbstractProjectGeneralPropertiesPage.this.mostRecentClasspathDirectoryPreference();
				if (dirName != null) {
					return new File(dirName);
				}
				File dir = AbstractProjectGeneralPropertiesPage.this.projectSaveDirectory();
				if (dir != null) {
					return dir;
				}
				return FileTools.userHomeDirectory();
			}

			public void setDefaultClasspathDirectory(File defaultClasspathDirectory) {
				AbstractProjectGeneralPropertiesPage.this.setMostRecentClasspathDirectoryPreference(defaultClasspathDirectory.getPath());
			}
		};
	}

	private PropertyValueModel buildProjectSaveDirectoryHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), MWProject.SAVE_DIRECTORY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWProject) this.subject).getSaveDirectory();
			}
		};
	}
	
	private Document buildSaveLocationDocumentAdapter() {
		return new DocumentAdapter(
			new PropertyAspectAdapter(this.projectSaveDirectoryHolder) {
				protected Object getValueFromSubject() {
					return ((File) this.subject).getAbsolutePath();
				}
			}
		);
	}

	private PropertyValueModel buildRepositoryHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWProject) this.subject).getRepository();
			}
		};
	}

	private ListValueModel buildClasspathHolder() {
		return new ListAspectAdapter(this.buildRepositoryHolder(), MWClassRepository.CLASSPATH_ENTRIES_LIST) {
			public void addItem(int index, Object item) {
				((MWClassRepository) this.subject).addClasspathEntry(index, (String) item);
			}
			protected ListIterator getValueFromSubject() {
				return ((MWClassRepository) this.subject).classpathEntries();
			}
			public Object removeItem(int index) {
				return ((MWClassRepository) this.subject).removeClasspathEntry(index);
			}
			public Object replaceItem(int index, Object item) {
				return ((MWClassRepository) this.subject).replaceClasspathEntry(index, (String) item);
			}
			protected int sizeFromSubject() {
				return ((MWClassRepository) this.subject).classpathEntriesSize();
			}
		};
	}

	protected final String helpTopicId() {
		return "project.general";
	}

}
