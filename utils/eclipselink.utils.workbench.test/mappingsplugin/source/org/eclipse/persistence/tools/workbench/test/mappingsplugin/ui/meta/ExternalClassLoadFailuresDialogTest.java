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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassLoadFailuresDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.UiMetaBundle;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

/**
 * Simple test class for playing around with the ExternalClassLoadFailuresDialog.
 */
public class ExternalClassLoadFailuresDialogTest {

	public static void main(String[] args) throws IOException {
		new ExternalClassLoadFailuresDialogTest().exec(args);
	}

	private ExternalClassLoadFailuresDialogTest() {
		super();
	}

	private void exec(String[] args) throws IOException {
		MWRelationalProject project = this.buildProject();
		ExternalClassLoadFailureContainer failures = this.buildFailures(project);
		
		ExternalClassLoadFailuresDialog dialog = new ExternalClassLoadFailuresDialog(buildWorkbenchContext(), failures);
		dialog.show();

		System.exit(0);
	}

	private MWRelationalProject buildProject() throws IOException {
		File pkgDir = FileTools.emptyTemporaryDirectory("bogus");
		this.buildBogusClassFile(pkgDir, "Foo");
		this.buildBogusClassFile(pkgDir, "Bar");
		this.buildBogusClassFile(pkgDir, "Baz");
		MWRelationalProject project = new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);
		project.getRepository().addClasspathEntry(pkgDir.getParentFile().getAbsolutePath());
		return project;
	}

	private void buildBogusClassFile(File pkgDir, String shortClassName) throws IOException {
		File bogusClassFile = new File(pkgDir, shortClassName + ".class");
		Writer writer = new FileWriter(bogusClassFile);
		writer.write("bogus class file - used for testing classloading problems");
		writer.close();
	}

	private ExternalClassLoadFailureContainer buildFailures(MWRelationalProject project) {
		return project.getRepository().refreshTypesFor(this.buildExternalClassDescriptions(project.getRepository()));
	}

	private Iterator buildExternalClassDescriptions(MWClassRepository repository) {
		Collection exTypes = new ArrayList();
		exTypes.add(this.externalClassDescriptionNamed("bogus.Foo", repository));
		exTypes.add(this.externalClassDescriptionNamed("bogus.Bar", repository));
		exTypes.add(this.externalClassDescriptionNamed("java.lang.Object", repository));
		exTypes.add(this.externalClassDescriptionNamed("java.util.Collection", repository));
		exTypes.add(this.externalClassDescriptionNamed("bogus.Baz", repository));
		return exTypes.iterator();
	}

	private Object externalClassDescriptionNamed(String name, MWClassRepository repository) {
		for (Iterator stream = repository.externalClassDescriptions(); stream.hasNext(); ) {
			ExternalClassDescription exType = (ExternalClassDescription) stream.next();
			if (exType.getName().equals(name)) {
				return exType;
			}
		}
		throw new IllegalArgumentException(name);
	}
	
	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext(UiMetaBundle.class, "org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginIconResourceFileNameMap");
	}

}
