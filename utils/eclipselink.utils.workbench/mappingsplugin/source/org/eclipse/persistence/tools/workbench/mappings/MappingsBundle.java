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
package org.eclipse.persistence.tools.workbench.mappings;

import java.util.ListResourceBundle;

public class MappingsBundle extends ListResourceBundle {

	private static final Object[][] contents = {
		{"help", "Usage: java {0} inputfile outputfile [logfile]"},
		{"generatingMight", "Your output file may not work correctly at runtime because one or more of your active descriptors or tables is incomplete.  Look through your project for descriptors or tables and assure everything is complete.  Continuing..."},
		{"errorGenerating", "Error Generating Output File..."},
		{"errorTheFollowingDescriptor...", "Error: The following descriptors do not have corresponding class files.  Please check your classpath."},
		{"EOJ", "Generation complete."},
		{"generationError", "An error occured during generation..."},
		{"projectsProblems", "{0} has {1} problem(s)"},
		{"notJ2eeProject", "{0}  is not a J2EE project"},
		{"error", "Error: "},
	};

	public Object[][] getContents() {
		return contents;
	}

}
