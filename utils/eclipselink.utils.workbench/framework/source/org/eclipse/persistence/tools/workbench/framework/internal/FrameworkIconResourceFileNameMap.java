/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.resources.AbstractIconResourceFileNameMap;

// TODO move to public package or make package-accessible and change tests...
public final class FrameworkIconResourceFileNameMap extends AbstractIconResourceFileNameMap {

	private static final String[][] entries = {
		
		{"problem.small", "basic/misc/Warning.small.gif"},

		// *** Common file types ***
		{"file.xml",               "basic/file/File.xml.gif"},
		{"file.xml.large",         "basic/file/File.xml.large.gif"},
		{"file.xml.multi",         "basic/file/File.xml.multi.gif"},
		{"file.xml.new",           "basic/file/File.xml.new.gif"},
		{"file.xml.multi.refresh", "basic/file/File.xml.multi.refresh.gif"},
		{"file.xml.refresh",       "basic/file/File.xml.refresh.gif"},
		{"file.mwp.large",         "basic/file/File.large.gif"},
		
		{"navigator", "basic/file/Projects.gif"},
		{"editor", "basic/misc/Text.gif"},
		{"problems", "basic/misc/Problems.gif"},
		{"error", "basic/misc/Stop.gif"},
		

		{"EXIT", "basic/misc/Exit.gif"},
		{"file", "basic/file/File.gif"},
		{"file.close", "basic/file/File.close.gif"},
		{"file.closeAll", "basic/file/File.closeAll.gif"},
		{"file.open", "basic/file/File.open.gif"},
		{"file.save", "basic/file/File.save.gif"},
		{"file.saveAll", "basic/file/File.saveAll.gif"},
		{"tools.problemReport", "basic/misc/ProblemsReport.gif"},
		{"window.showProblems", "basic/misc/Problems.gif"},
		
		{"PREFERENCES", "basic/misc/Preferences.gif"},
		{"HELP_TOPIC_ID_WINDOW", "basic/help/Note.gif"},
		{"HELP", "basic/help/Help.gif"},
		{"WARNING", "basic/misc/Warning.gif"},

		{"class.public", "basic/meta/Class.public.gif"},
		{"package", "basic/meta/Package.gif"},

		{"oracle.logo",       "logo/MappingWorkbench.gif"},
		{"oracle.logo.large", "logo/MappingWorkbench.large.gif"},
		{"mw.about", "logo/MappingWorkbench.about.gif"},
	};

	protected String[][] getEntries() {
		return entries;
	}

}
