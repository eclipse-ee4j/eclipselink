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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.io.File;

import javax.swing.ButtonModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel.FileHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public abstract class ProjectOptionsPropertiesPage
	extends ScrollablePropertiesPage
{

	public static final String MODEL_SOURCE_DIRECTORY_PREFERENCE = "project options-model source directory";
	public static final String PROJECT_DEPLOYMENT_XML_DIRECTORY_PREFERENCE = "project options-project deployment xml directory";

	protected ProjectOptionsPropertiesPage(PropertyValueModel projectNodeHolder, WorkbenchContextHolder contextHolder) {
		super(projectNodeHolder, contextHolder);
	}


	// ********** File Chooser convenience methods **********

	protected ValueModel projectHolder() {
		return this.getSelectionHolder();
	}

	protected MWProject project() {
		return (MWProject) this.projectHolder().getValue();
	}

	/**
	 * the "root" directory for relative files is the project save directory
	 */
	protected final FileHolder buildFileChooserRootFileHolder() {
		return new FileHolder() {
			public File getFile() {
				return ProjectOptionsPropertiesPage.this.project().getSaveDirectory();
			}
			public void setFile(File file) {
				// the project save directory cannot be changed
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * the default directory for the file chooser is one of the following:
	 * - project save directory
	 * - preference setting
	 * - user home directory
	 */
	protected final FileHolder buildFileChooserDefaultDirectoryHolder(final String prefKey) {
		return new FileHolder() {
			public File getFile() {
				File projectSaveDir = ProjectOptionsPropertiesPage.this.project().getSaveDirectory();
				if (projectSaveDir != null) {
					return projectSaveDir;
				}

				String prefDirName = ProjectOptionsPropertiesPage.this.preferences().get(prefKey, null);
				if (prefDirName != null) {
					return new File(prefDirName);
				}

				return FileTools.userHomeDirectory();
			}

			public void setFile(File file) {
				if (file.equals(FileTools.userHomeDirectory())) {
					// don't save the user home
					file = null;
				} else if (file.equals(ProjectOptionsPropertiesPage.this.project().getSaveDirectory())) {
					// don't save the project save directory
					file = null;
				}

				// if the directory is special, save it as a preference, otherwise clear the preference
				if (file == null) {
					ProjectOptionsPropertiesPage.this.preferences().remove(prefKey);
				} else {
					ProjectOptionsPropertiesPage.this.preferences().put(prefKey, file.getPath());
				}
			}
		};
	}


	// ********** Deployment XML **********

	protected PropertyValueModel buildDeploymentXMLFileNameHolder(ValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder, MWProject.DEPLOYMENT_XML_FILE_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWProject) this.subject).getDeploymentXMLFileName();
			}
			protected void setValueOnSubject(Object value) {
				((MWProject) this.subject).setDeploymentXMLFileName((String) value);
			}
		};
	}


	// ********** Model Java Source **********

	protected PropertyValueModel buildModelSourceDirectoryNameHolder(ValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder, MWProject.MODEL_SOURCE_DIRECTORY_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWProject) this.subject).getModelSourceDirectoryName();
			}
			protected void setValueOnSubject(Object value) {
				((MWProject) this.subject).setModelSourceDirectoryName((String) value);
			}
		};
	}

	//******************************** Weaving  *********************************
	
	protected ButtonModel buildWeavingButtonModel() {
		return new CheckBoxModelAdapter(buildWeavingAdapter(), false);
	}
	
	protected PropertyValueModel buildWeavingAdapter() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWProject.USES_WEAVING_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWProject) this.subject).usesWeaving());
			}
			protected void setValueOnSubject(Object value) {
				((MWProject) this.subject).setUsesWeaving(((Boolean) value).booleanValue());
			}
		};
	}

}
