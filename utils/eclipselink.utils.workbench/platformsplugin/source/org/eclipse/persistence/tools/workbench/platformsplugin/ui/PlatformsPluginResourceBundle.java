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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import java.util.ListResourceBundle;

/**
 * resource bundles must be public, since they are instantiated reflectively by the JDK
 */
public final class PlatformsPluginResourceBundle extends ListResourceBundle {

	private static final Object[][] contents = { 
		// Plug-in actions
		{"NEW_DATABASE_PLATFORM_REPOSITORY",					"&Database Platform Repository..."},
		{"NEW_DATABASE_PLATFORM_REPOSITORY.TOOL_TIP",		"Create New Database Platform Repository"},
		{"NEW_DATABASE_PLATFORM_REPOSITORY.DIALOG.TITLE",								"Database Platform Repository Name"},
		{"NEW_DATABASE_PLATFORM_REPOSITORY.DIALOG.TEXT_FIELD_DESCRIPTION",	"Database Platform Repository Name"},
		{"NEW_DATABASE_PLATFORM_REPOSITORY.DIALOG.ORIGINAL_NAME",					"New Repository"},

		// Repository node
		{"REPOSITORY_SAVE_FILE_TITLE", "Choose Save File for Repository: {0}"},
		{"UNSUPPORTED_FILE_TYPE", "Unsupported File Type"},

		// Repository pages
		{"DATABASE_PLATFORM_REPOSITORY_GENERAL_TAB_TITLE",				"&General"},
		{"DATABASE_PLATFORM_REPOSITORY_JDBC_MAPPINGS_TAB_TITLE",	"J&DBC Mappings"},
		{"DATABASE_PLATFORM_REPOSITORY_JAVA_MAPPINGS_TAB_TITLE",	"&Java Mappings"},

		{"DATABASE_PLATFORM_REPOSITORY_FILE_TEXT_FIELD",							"&File:"},
		{"DATABASE_PLATFORM_REPOSITORY_DEFAULT_PLATFORM_COMBO_BOX",	"&Default Platform:"},
		{"DATABASE_PLATFORM_REPOSITORY_COMMENT_TEXT_FIELD",					"&Comment:"},

		{"DATABASE_PLATFORM_REPOSITORY_JDBC_TO_JAVA_TABLE",	"&JDBC to Java Type Mappings"},
		{"DATABASE_PLATFORM_REPOSITORY_JAVA_TO_JDBC_LIST_BOX",	"J&ava to JDBC Type Mappings"},
		{"DATABASE_PLATFORM_REPOSITORY_JDBC_TYPE_COLUMN",	"JDBC Type"},
		{"DATABASE_PLATFORM_REPOSITORY_JAVA_TYPE_COLUMN",	"Java Type"},

		// Repository actions
		{"RENAME_DATABASE_PLATFORM_REPOSITORY",									"Re&name Database Platform Repository ..."},
		{"RENAME_DATABASE_PLATFORM_REPOSITORY.TOOL_TIP",						"Rename Selected Database Platform Repository"},
		{"RENAME_DATABASE_PLATFORM_REPOSITORY_DIALOG_TITLE",				"Rename Database Platform Repository"},
		{"RENAME_DATABASE_PLATFORM_REPOSITORY_DIALOG_DESCRIPTION",	"Enter new name for database platform repository:"},

		{"ADD_DATABASE_PLATFORM",												"&Add Database Platform"},
		{"ADD_DATABASE_PLATFORM.TOOL_TIP",								"Add Database Platform to Selected Database Platform Repository"},
		{"ADD_DATABASE_PLATFORM_DIALOG_TITLE",			"Add Database Platform"},
		{"ADD_DATABASE_PLATFORM_DIALOG_DESCRIPTION",	"Enter a name for database platform:"},

		// Platform pages
		{"DATABASE_PLATFORM_GENERAL_TAB_TITLE",				"&General"},
		{"DATABASE_PLATFORM_JDBC_MAPPINGS_TAB_TITLE",		"&JDBC Mappings"},

		{"DATABASE_PLATFORM_SHORT_FILE_NAME_TEXT_FIELD",									"Short &File Name:"},
		{"DATABASE_PLATFORM_RUNTIME_PLATFORM_CLASS_NAME_TEXT_FIELD",			"Runtime &Platform Class:"},
		{"DATABASE_PLATFORM_SUPPORTS_NATIVE_SEQUENCING_CHECK_BOX",				"&Native Sequencing Supported"},
		{"DATABASE_PLATFORM_SUPPORTS_NATIVE_RETURNING_CHECK_BOX",				"Native &Returning Supported"},
		{"DATABASE_PLATFORM_SUPPORTS_IDENTITY_CLAUSE_CHECK_BOX",					"&IDENTITY Clause Supported"},
		{"DATABASE_PLATFORM_AUTO_INCREMENT_DATABASE_TYPES_LIST_BOX",			"Auto-&Increment Types"},
		{"DATABASE_PLATFORM_AUTO_INCREMENT_DATABASE_TYPES_ADD_BUTTON",		"&Add"},
		{"DATABASE_PLATFORM_AUTO_INCREMENT_DATABASE_TYPES_REMOVE_BUTTON",	"&Remove"},
		{"DATABASE_PLATFORM_COMMENT_TEXT_FIELD",												"&Comment:"},

		{"DATABASE_PLATFORM_JDBC_MAPPINGS_LIST_BOX", "JDBC &Mappings"},
		{"DATABASE_PLATFORM_JDBC_TYPE_COLUMN", "JDBC Type"},
		{"DATABASE_PLATFORM_DATABASE_TYPE_COLUMN", "Database Type"},

		// Auto Increment dialog
		{"DATABASE_TYPE_DIALOG.title", "Select a database type"},

		// Platform actions
		{"RENAME_DATABASE_PLATFORM",									"&Rename Database Platform ..."},
		{"RENAME_DATABASE_PLATFORM.TOOL_TIP",					"Rename Selected Database Platform"},
		{"RENAME_DATABASE_PLATFORM_DIALOG_TITLE",				"Rename Database Platform"},
		{"RENAME_DATABASE_PLATFORM_DIALOG_DESCRIPTION",	"Enter new name for database platform:"},

		{"DELETE_DATABASE_PLATFORM",									"&Delete Database Platform"},
		{"DELETE_DATABASE_PLATFORM.TOOL_TIP",					"Delete Selected Database Platform"},
		{"DELETE_DATABASE_PLATFORM_DIALOG_TITLE",				"Delete Database Platforms"},
		{"DELETE_DATABASE_PLATFORM_DIALOG_MESSAGE",		"Delete the selected database platforms?"},

		{"CLONE_DATABASE_PLATFORM",									"&Clone Database Platform"},
		{"CLONE_DATABASE_PLATFORM.TOOL_TIP",						"Clone Selected Database Platforms"},

		{"ADD_DATABASE_TYPE",												"&Add Database Type"},
		{"ADD_DATABASE_TYPE.TOOL_TIP",								"Add Database Type to Selected Database Platforms"},
		{"ADD_DATABASE_TYPE_DIALOG_TITLE",			"Add Database Type"},
		{"ADD_DATABASE_TYPE_DIALOG_DESCRIPTION",	"Enter a name for database type:"},

		// Type pages
		{"DATABASE_TYPE_JDBC_TYPE_COMBO_BOX",				"&JDBC Type:"},
		{"DATABASE_TYPE_ALLOWS_SIZE_CHECK_BOX",			"Size &Allowed"},
		{"DATABASE_TYPE_REQUIRES_SIZE_CHECK_BOX",			"Size &Required"},
		{"DATABASE_TYPE_INITIAL_SIZE_SPINNER",				"&Initial Size:"},
		{"DATABASE_TYPE_ALLOWS_SUB_SIZE_CHECK_BOX",	"S&ub Size Allowed"},
		{"DATABASE_TYPE_ALLOWS_NULL_CHECK_BOX",			"&Null Allowed"},
		{"DATABASE_TYPE_COMMENT_TEXT_FIELD",				"&Comment:"},

		// Type actions
		{"RENAME_DATABASE_TYPE",								"&Rename Database Type ..."},
		{"RENAME_DATABASE_TYPE.TOOL_TIP",					"Rename Selected Database Type"},
		{"RENAME_DATABASE_TYPE_DIALOG_TITLE",			"Rename Database Type"},
		{"RENAME_DATABASE_TYPE_DIALOG_DESCRIPTION",	"Enter new name for database type:"},

		{"DELETE_DATABASE_TYPE",									"&Delete Database Type"},
		{"DELETE_DATABASE_TYPE.TOOL_TIP",					"Delete Selected Database Type"},
		{"DELETE_DATABASE_TYPE_DIALOG_TITLE",				"Delete Database Types"},
		{"DELETE_DATABASE_TYPE_DIALOG_MESSAGE",		"Delete the selected database types?"},

		// preferences
		{"PREFERENCES.PLATFORMS",			"Platforms"},
		{"PREFERENCES.PLATFORMS.VISIBLE_IN_PRODUCTION",			"Visible in Production"},

		// Help Action
		{"HELP", "&Help"},
		{"HELP.tooltip", "Help for the selected item(s)"},

		// problems
		{"001", "The database platform repository is empty."},
		{"002", "The JDBC type ''{0}'' has not been mapped to a database type."},
		{"003", "{0}: The database type ''{1}'' requires a size but does not have an initial size specified."},

	};

	protected Object[][] getContents() {
		return contents;
	}

}
