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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.util.ListResourceBundle;

public final class UIToolsResourceBundle extends ListResourceBundle {

	private static final Object[][] contents = {

		// ***** FileChooserPanel *****
		{"FILE_CHOOSER_PANEL.BROWSE_BUTTON_TEXT", "Browse..."},
		
		// ***** DefaultListChooser *****
		{"DEFAULT_LONG_LIST_BROWSER_DIALOG.TITLE",            "Select an Item"},
		{"DEFAULT_LONG_LIST_BROWSER_DIALOG.TEXT_FIELD_LABEL", "&Select an item (? = any character, * = any string):"},
        {"DEFAULT_LONG_LIST_BROWSER_DIALOG.LIST_BOX_LABEL",   "&Matching items:"},
        {"SELECT_IN_NAVIGATOR_POPUP_MENU_ITEM",   			  "Select in Navigator"},
        
		// ***** DualListSelectorPanel *****
		{"AVAILABLE_ITEMS_LIST_BOX_LABEL",           "Available Items:"},
		{"SELECTED_ITEMS_LIST_BOX_LABEL",            "Selected Items:"},
		{"ADD_SELECTED_ITEMS_BUTTON.toolTipText",    "Add Selected Items"},  
		{"REMOVE_SELECTED_ITEMS_BUTTON.toolTipText", "Remove Selected Items"},  

		// ClasspathPanel
		{"ADD_ENTRY_BUTTON_TEXT",            "&Add Entry"},
		{"REMOVE_BUTTON_TEXT",               "Remo&ve"},	
		{"UP_BUTTON_TEXT",                   "&Up"},
		{"DOWN_BUTTON_TEXT",                 "&Down"},
		{"CLASSPATH_PANEL_TITLE",            "Classpath"},	
		{"ADD_CLASSPATH_ENTRY_DIALOG.TITLE", "Add Entries"},
		{"ADD_CLASSPATH_ENTRY_DIALOG",       "Select Desired Classpath Entries"},
		{".jar.zip",                         "Jar or Zip Files (*.jar;*.zip)"},

		// FileChooser
		{"FILECHOOSER_MAKE_RELATIVE_CHECKBOX", "&Make relative to {0}"},

		// ***** AddRemoveListPanel *****
		{"ADD_BUTTON",    "&Add..."},
		{"REMOVE_BUTTON", "&Remove"},
		{"RENAME_BUTTON", "Re&name"},	// actually not in the panel, but popular option of clients

		// ***** Checklist ****
		{"ACCESSIBLE_CHECKLIST_CHECKBOX_CHECKED",     "check box checked"},
		{"ACCESSIBLE_CHECKLIST_CHECKBOX_NOT_CHECKED", "check box not checked"},

		{"DONT_SHOW_THIS_AGAIN_CHECK_BOX", "&Remember this response and do not ask again"},
        
        // ****** NoneSelectedCellRendererAdapter ******
		{"NONE_SELECTED", "<none selected>"},

        // ****** TriStateBooleanCellRendererAdapter ******
        {"TRI_STATE_BOOLEAN_UNDEFINED", "Undefined"},
        {"TRI_STATE_BOOLEAN_TRUE", "True"},
        {"TRI_STATE_BOOLEAN_FALSE", "False"},

	};

	public Object[][] getContents() {
		return contents;
	}

}
