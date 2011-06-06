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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import org.eclipse.persistence.tools.workbench.framework.resources.AbstractIconResourceFileNameMap;

/**
 * 
 */
public class UIToolsIconResourceFileNameMap extends AbstractIconResourceFileNameMap {

	private static final String[][] entries = {
		// Edit
		{"copy", "basic/edit/Copy.gif"},
		{"copy.large", "basic/edit/Copy.large.gif"},
		{"delete", "basic/edit/Delete.gif"},
		{"delete.large", "basic/edit/Delete.large.gif"},

		// Meta
		{"class.public", "basic/meta/Class.public.gif"},
		{"package", "basic/meta/Package.gif"},

		// Miscelleneous
		{"error", "basic/misc/Error.gif"},
		{"error.small", "basic/misc/Stop.small.gif"},
		{"error.large", "basic/misc/Error.large.gif"},
		{"exit", "basic/misc/Exit.gif"},
		{"feedback", "basic/help/Feedback.gif"},
		{"remove", "basic/misc/Remove.gif"},
		{"rename", "basic/misc/Rename.gif"},
		{"refresh", "basic/misc/Refresh.gif"},
		{"stop", "basic/misc/Stop.gif"},
		{"warning", "basic/misc/Warning.gif"},
		{"warning.small", "basic/misc/Warning.small.gif"},
		{"warning.large", "basic/misc/Warning.large.gif"},

		// Help
		{"about", "basic/help/About.gif"},
		{"about.large", "basic/help/About.large.gif"},
		{"annoted", "basic/help/Annoted.gif"},
		{"annoted.remove", "basic/help/Annoted.remove.gif"},
		{"back", "basic/help/Back.gif"},
		{"back.large", "basic/help/Back.large.gif"},
		{"contents", "basic/help/Contents.gif"},
		{"demo", "basic/help/Demo.gif"},
		{"favorite", "basic/help/Favorite.gif"},
		{"favorite.folder", "basic/help/Favorite.folder.gif"},
		{"favorite.folder.large", "basic/help/Favorite.folder.large.gif"},
		{"favorite.tab", "basic/help/Favorite.tab.gif"},
		{"favorite.tab.large", "basic/help/Favorite.tab.large.gif"},
		{"forward", "basic/help/Forward.gif"},
		{"forward.large", "basic/help/Forward.large.gif"},
		{"help", "basic/help/Help.gif"},
		{"help.large", "basic/help/Help.large.gif"},
		{"help.topic", "basic/help/HelpTopic.gif"},
		{"home", "basic/help/Home.gif"},
		{"home.large", "basic/help/Home.large.gif"},
		{"history", "basic/help/History.large.gif"},
		{"history.large", "basic/help/History.large.gif"},
		{"index", "basic/help/Index.gif"},
		{"index2", "basic/help/Index2.gif"},
		{"navigation.tab.hide", "basic/help/NavigationTab.hide.gif"},
		{"navigation.tab.hide.large", "basic/help/NavigationTab.hide.large.gif"},
		{"navigation.tab.show", "basic/help/NavigationTab.show.gif"},
		{"navigation.tab.show.large", "basic/help/NavigationTab.show.large.gif"},
		{"next", "basic/help/Next.gif"},
		{"next.large", "basic/help/Next.large.gif"},
		{"note", "basic/help/Note.gif"},
		{"previous", "basic/help/Previous.gif"},
		{"previous.large", "basic/help/Previous.large.gif"},
		{"relatedBook", "basic/help/RelatedBook.gif"},
		{"relatedBook.large", "basic/help/RelatedBook.large.gif"},
		{"search", "basic/help/Search.gif"},
		{"search.tab", "basic/help/SearchTab.gif"},
		{"search.tab.large", "basic/help/SearchTab.large.gif"},
		{"toc", "basic/help/TOC.gif"},

		// File System
		{"file", "basic/file/File.gif"},
		{"file.xml", "basic/file/File.xml.gif"},
		{"file.xml.large", "basic/file/File.xml.large.gif"},
		{"file.mwp.large", "basic/file/File.large.gif"},
		{"file.close", "basic/file/File.close.gif"},
		{"file.closeAll", "basic/file/File.closeAll.gif"},
		{"file.export", "basic/file/File.export.gif"},
		{"file.import", "basic/file/File.import.gif"},
		{"file.open", "basic/file/File.open.gif"},
		{"file.save", "basic/file/File.save.gif"},
		{"file.saveAll", "basic/file/File.saveAll.gif"},
		{"file.saveAs", "basic/file/File.saveAs.gif"},
		{"folder", "basic/file/Folder.gif"},
	};

	protected String[][] getEntries() {
		return entries;
	}

}
