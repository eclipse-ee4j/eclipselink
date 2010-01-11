/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappings;

import java.io.File;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * This class provides a command-line interface for generating
 * project Java source from an existing MW project.
 * 
 * usage:
 *     java JavaSourceGenerator inputFile outputFile [logFile]
 */
public class JavaSourceGenerator
	implements Generator.Adapter
{

	public static void main(String args[]) {
		new Generator(new JavaSourceGenerator()).execute(args);
	}

	public JavaSourceGenerator() {
		super();
	}

	/**
	 * @see Generator.Adapter#export(org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject, java.io.File)
	 */
	public void export(MWProject project, File outputFile) {
		project.setProjectSourceDirectoryName(outputFile.getParent());
		project.setProjectSourceClassName(FileTools.stripExtension(outputFile.getName()));
		project.exportProjectSource();
	}

}
