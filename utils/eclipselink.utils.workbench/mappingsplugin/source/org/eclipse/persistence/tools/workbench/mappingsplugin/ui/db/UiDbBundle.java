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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

/**
 * The resource bundle for the package: org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.
 *
 * @version 4.5
 */
public class UiDbBundle extends java.util.ListResourceBundle
{
	/**
	 * The string contents of the bundle
	 */
	static final Object[][] contents = {
		
		//MWDatabaseNode
			//AddNewTableAction
			{"ADD_NEW_TABLE_ACTION", "&Add New Table..."},
			{"ADD_NEW_TABLE_ACTION.toolTipText", "Add New Table"},
			{"NEW_TABLE_DEFAULT_NAME", "NEWTABLE"},		
			{"ADD_NEW_TABLE_DAILOG.title", "New Table"},
			{"ADD_NEW_TABLE_DAILOG.message", "Enter new table &name:"},
			{"NEW_CATALOG_LABEL", "&Catalog:"},
			{"NEW_SCHEMA_LABEL", "&Schema:"},
			{"NEW_TABLE_NAME_LABEL", "&Table Name:"},
			{"ADD_NEW_TABLE_DUPLICATE_TABLE_NAME_TITLE", "Table Already Exists"},	
			{"ADD_NEW_TABLE_DUPLICATE_TABLE_NAME_MESSAGE", "A table named ''{0}'' already exists."},
	
			
			//DatabaseLoginAction		
			{"DATABASE_LOG_IN", "&Log In to Database"},
			{"DATABASE_LOG_IN.toolTipText", "Log In to Database"},
			{"USER_NAME_OR_PASSWORD_COULD_BE_INVALID_MESSAGE", "User name or password could be invalid."},
			{"JDBC_DRIVER_ON_CLASSPATH.message", "Please ensure the JDBC driver is on the system classpath."},
			{"DATABASE_DRIVER_NOT_FOUND.message", "The class {0} was not found.{2}{2} {1}"},
			
			{"ERROR_LOGGING_IN_TO_DATABASE.title", "Error Logging in to the Database"},
			{"NO_DATABASE_DRIVER_SPECIFIED.message", "No database driver has been specified." },
			{"NO_URL_SPECIFIED.message", "No URL has been specified." },
			{"ALREADY_CONNECTED.message", "You are already connected to the database." },
			{"YOU_MUST_DEFINE_A_DEVELOPMENT_LOGIN.message", "You must define a development login." },
	
	
	
			//DatabaseLogoutAction
			{"DATABASE_LOG_OUT", "Log &Out of Database"},
			{"DATABASE_LOG_OUT.toolTipText", "Log Out of Database"},
	
	
			//AddOrRefreshTablesFromDatabaseAction
			{"ADD_OR_REFRESH_TABLES_ACTION", "Add or Update Existing Tables from &Database"},
			{"ADD_OR_REFRESH_TABLES_ACTION.toolTipText", "Add or Update Existing Tables from Database"},
			
			//TableImporterDialog
			{"IMPORT_TABLES_FROM_DATABASE_DIALOG.title", "Import Tables from Database"},
			{"TABLE_NAME_PATTERN_TEXT_FIELD_LABEL", "&Table Name Pattern:"},
			{"IGNORE", "<Ignore>"},
			{"ALL_TYPES", "<All Types>"},
			{"NO_SCHEMA", "<No Schema>"},
			{"NO_CATALOG", "<No Catalog>"},	
			{"CATALOG_COMBO_BOX_LABEL", "&Catalog:"},
			{"SCHEMA_PATTERN_COMBO_BOX_LABEL", "&Schema Pattern:"},
			{"TABLE_TYPE_COMBO_BOX_LABEL", "T&able Type:"},
			{"GET_TABLE_NAMES_BUTTON_TEXT", "&Get Table Names"},
			{"FULLY_QUALIFIED_CHECK_BOX", "&Import Fully Qualified Names"},
			{"AVAILABLE_TABLES_LIST_BOX_LABEL", "Available Tables:"},
			{"SELECTED_TABLES_LIST_BOX_LABEL", "Selected Tables:"},
			{"ADD_SELECTED_TABLES_BUTTON.toolTipText", "Add Selected Tables"},  
			{"REMOVE_SELECTED_TABLES_BUTTON.toolTipText", "Remove Selected Tables"},  
			{"PROBLEM_GETTING_PRIMARY_KEYS_DIALOG.title", "Primary Keys Error"},
			{"PROBLEM_GETTING_PRIMARY_KEYS_DIALOG.message", "There was an error getting the Primary Keys from the database. Most likely, this error means the database driver does not support this command. You must specify the primary key fields for the tables manually."},
			{"TABLE_IMPORTATION_DIALOG.TITLE", "Importing Tables"},
			{"TABLE_IMPORTATION_MESSAGE", "Importing the selected tables from the database...  Please wait..."},
			{"GET_TABLE_NAMES_DIALOG.TITLE", "Getting Table Names"},
			{"GET_TABLE_NAMES_DIALOG_MESSAGE", "Getting list of available tables from the database...  Please wait..."},


		//MWTableNode
			//RenameTableAction
			{"RENAME", "R&ename..."},
			{"RENAME_TABLE_DIALOG.title", "Rename {0}"},
			{"RENAME_TABLE_DIALOG.message", "Enter the new name for the table:"},
	
			//RefreshAction
			{"REFRESH", "&Refresh from Database"},
			{"ERROR_REFRESHING_TABLES_DIALOG.title", "Error Refreshing Tables"},
			{"ERROR_REFRESHING_TABLES_DIALOG.message", "The following tables were not refreshed because they were not found on the database:"},
			{"DUPLICATE_TABLES_DIALOG.title", "Duplicate Tables for {0}"},
			{"DUPLICATE_TABLES_DIALOG.message", "The table {0} has multiple matches on the database, select the table from which you would like to refresh:"},
		
			//RemoveAction
			{"REMOVE_TABLE", "Remo&ve"},
			{"REMOVE_TABLE.toolTipText", "Remove Selected Table"},

			//GenerateCreationScript actions
			{"GENERATE_CREATION_SCRIPT_MENU", "&Generate Creation Script for"},
			{"GENERATE_CREATION_SCRIPT_MENU.toolTipText", "Generate Creation Script for"},
			{"GENERATE_CREATION_SCRIPT_DIALOG_TITLE", "Generate Creation Script"},
			{"SQL_CREATION_SCRIPT_DIALOG.title", "SQL Creation Script"},	

			
			//CreateOnDatabase actions
			{"CREATE_ON_DATABASE_MENU", "Create on &Database"},
			{"CREATE_ON_DATABASE_MENU.toolTipText", "Create on Database"},
			{"CREATE_ON_DATABASE_DIALOG.title", "Create On Database"},
			{"CREATE_ON_DATABASE_DONE_DIALOG.title", "Create On Database"},
			{"CREATE_ON_DATABASE_DONE_DIALOG.message", "{0} table(s) created in the database."},
			{"TABLES_WILL_BE_OVERWRITTEN_DIALOG.title", "Warning: Tables Will Be Overwritten"},
			{"TABLES_WILL_BE_OVERWRITTEN_DIALOG.message","The selected tables on the database will be overwritten with the new table information.  All existing table information will be lost.  Do you wish to continue?"},
			{"EXCEPTION_DURING_TABLE_GEN", "An exception occured during table creation."},
			{"EXCEPTION_DURING_TABLE_GEN.title", "Table creation failed."},
			{"EXCEPTION2270_DURING_TABLE_GEN", "The application encountered and error while creating the table on the database.{0}Database error code 2270 indicates that an attempt to create a reference failed.{0}This is likely due to the reference field on the target table not being unique or a primary key.{0}Assure reference fields are set to be unique or primary key on table to avoid this.{0}The table was not successfully created on the database."},
			{"EXCEPTION2270_DURING_TABLE_GEN.title", "Table Reference Creatation Failure"},
			{"EXCEPTION955_DURING_TABLE_GEN", "The application encountered an error while replacing the table on the database.{0}This is most likely caused by another table referencing the table to be replaced.{0}Try replacing all associated tables at once rather than just one to avoid this issue.{0}Table was not successfully created on the database."},
			{"EXCEPTION955_DURING_TABLE_GEN.title", "Table already exists error."},

				{"NO_LOGIN_DEFINED_DIALOG.message", "No login is currently defined."},		
				{"SELECTED_TABLES_LABEL", "&Selected Tables"},
				{"ALL_TABLES_LABEL", "&All Tables"},
				


		{"GENERATE_DESCRIPTORS_FROM_TABLES_ACTION", "Generate &Classes and Descriptors from"},
		{"GENERATE_DESCRIPTORS_FROM_TABLES_ACTION.toolTipText", "Generate Classes and Descriptors from tables."},
		{"GENERATE_EJB_DESCRIPTORS_FROM_TABLES_ACTION", "Generate &EJB Entities and Descriptors from"},
		{"GENERATE_EJB_DESCRIPTORS_FROM_TABLES_ACTION.toolTipText", "Generate EJB Entities and Descriptors from tables."},
		{"ALL_TABLES", "&All Tables"},
		{"SELECTED_TABLES", "&Selected Tables"},


		//DescriptorGenerationCoordinator		
		{"generateClassesAndDescriptors.title", "Generate Classes and Descriptors"},
		{"generateClassesAndDescriptors.message", "Generation complete."},
		{"generateEJB.title", "Generate EJB"},
		{"generateEJB.message", "Generation complete."},
		{"autoGeneratingClassAndDescriptor.message", "Auto-generating class and descriptor definitions may change existing definitions. Do you wish to save the project first?"},
		{"autoGeneratingTableDefinitions.message", "Auto-generating table definitions may change existing definitions. Do you wish to save the project first?"},
		{"saveProject.title", "Save Projects"},
		{"saveProjectError.message", "There was a problem trying to save the project."},
		{"unableToCreateClassesAndDescriptors.title", "Unable to Create Classes and Descriptors"},

	
		//DescriptorGenerationDialog
		{"DescriptorGenerationDialog_class.title", "Generate Classes and Descriptors"},
		{"DescriptorGenerationDialog_1_1_cmp.title", "Generate EJB 1.1 CMP Entity Classes and Descriptors"},
		{"DescriptorGenerationDialog_2_0_cmp.title", "Generate EJB 2.0 CMP Entity Classes and Descriptors"},
		{"DescriptorGenerationDialog_bmp.title", "Generate EJB BMP Entity Classes and Descriptors"},
		{"DescriptorGenerationDialog_packageName", "Package Name:"},
		{"DescriptorGenerationDialog_generateAccessingMethods", "Generate Accessing Methods"},
		{"DescriptorGenerationDialog_generateLocalInterfaces", "Generate Local Interfaces"},
		{"DescriptorGenerationDialog_generateRemoteInterfaces", "Generate Remote Interfaces"},
		{"DescriptorGenerationDialog_noRelationshipSupport.title", "Warning"},
		{"DescriptorGenerationDialog_noRelationshipSupport.message", "If no local interfaces are generated, then no inter-EJB relationships can be generated, either."},

		//RelationshipGenerationDialog
		{"chooseRelationshipsToGenerate.title", "Choose Relationships to Generate"},
		{"basedOnTheForeignKeysOfThe", "Based on the foreign keys of the tables, some relationship mappings can be generated. Select the appropriate relationships below and indicate their types using the mapping buttons."},
		{"potentialRelationships", "&Potential Relationships:"},
		{"selectedRelationships", "&Selected Relationships:"},
		{"mapAsOneToOne","Map As One to One"},
		{"mapAsOneToMany","Map As One to Many"},			
		{"removeSelectedMappings","Remove Selected Mappings"},
		{"generateBidirectionalRelationships", "&Generate Bi-directional Relationships"},


		//MWDatabasePropertiesPage
		{"DATABASE_PLATFORM_LABEL", "&Database Platform:"},
		{"CHANGE_DATABASE_PLATFORM_BUTTON_TEXT", "Change..."},
		{"DEFINED_LOGINS_LIST_LABEL_TEXT", "D&efined Logins:"},
		{"ADD_LOGIN_BUTTON_TEXT", "&Add..."},
		{"ADD_LOGIN_TOOL_TIP_TEXT", "Add a New Login"},
		{"REMOVE_LOGIN_BUTTON_TEXT", "&Remove"},
		{"REMOVE_LOGIN_TOOL_TIP_TEXT", "Remove the Selected Login"},
		{"RENAME_LOGIN_BUTTON_TEXT", "Rena&me..."},
		{"RENAME_LOGIN_TOOL_TIP_TEXT", "Rename the Selected Login"},
		{"DEVELOPMENT_LOGIN_LABEL", "Development &Login:"},
		{"DEPLOYMENT_LOGIN_LABEL", "Deplo&yment Login:"},
		{"CHANGE_DATABASE_PLATFORM.title", "Save Project"},
		{"CHANGE_DATABASE_PLATFORM.message", "Changing the database platform may change the field types in the table definitions.{0} Do you want to save the project first?"},
		{"CLEAR_LOGIN.title",	"Clear Development Login"},
		{"CLEAR_LOGIN.message",  "Changing the database platform changes the database tables in the project to match the new platform.{0}It is strongly recomended to clear the development login.  Clear development login?"},
		{"CLEAR_LOGIN_AND_LOGOUT.title",	"Logout and Clear Development Login"},
		{"CLEAR_LOGIN_AND_LOGOUT.message",  "Changing the database platform changes the database tables in the project to match the new platform.{0}It is strongly recomended to log out (if logged in) and clear the development login.{0}Log off and clear development login?"},
		{"NEW_LOGIN_DIALOG.title", "Add New Login"},
		{"NEW_LOGIN_DIALOG.message", "&Enter the name of the new login info:"},
		{"NEW_LOGIN_NAME", "NewLogin"},
		{"RENAME_LOGIN_INFO_DIALOG.title", "Rename Login Info"},
		{"RENAME_LOGIN_INFO_DIALOG.message", "&Enter the new name of the login info:"},
		{"REMOVE_LOGIN_INFO_DIALOG.title", "Removing Login Info"},
		{"REMOVE_LOGIN_INFO_DIALOG.message", "Are you sure you want to remove this login info?"},

			
		//LoginInfoPanel
		{"DRIVER_CLASS_COMBO_BOX_LABEL", "Driver &Class:"},
		{"URL_COMBO_BOX_LABEL", "&URL:"},		
		{"USERNAME_TEXT_FIELD_LABEL", "User Nam&e:"},
		{"PASSWORD_TEXT_FIELD_LABEL", "&Password:"},
		{"SAVE_PASSWORD_CHECK_BOX", "Sa&ve Password"},


		//DatabasePlatformChooserDialog
		{"DATABASE_PLATFORMS_DIALOG.title", "Choose a New Database Platform"},


		//MWTableTabbedPropertiesPage
		
		{"COLUMNS_TAB_TEXT", "Columns"},
		{"REFERENCES_TAB_TEXT", "References"},
	
	
		//MWFieldsPropertiesPage
		
		{"NAME_COLUMN_HEADER", "Name"},
		{"ALLOWS_NULL_COLUMN_HEADER", "Allows Null"},
		{"IDENTITY_COLUMN_HEADER", "Identity"},
		{"PRIMARY_KEY_COLUMN_HEADER", "Primary Key"},
		{"SIZE_COLUMN_HEADER", "Size"},
		{"SUB_SIZE_COLUMN_HEADER", "Sub-Size"},
		{"TYPE_COLUMN_HEADER", "Type"},
		{"UNIQUE_COLUMN_HEADER", "Unique"},
		
		{"ADD_DATABASE_FIELD_BUTTON_TEXT", "&Add..."},
		{"NEW_FIELD_DEFAULT_NAME", "NewField"},
		{"ADD_NEW_FIELD_DIALOG.title", "Add Database Field"},
		{"ADD_NEW_FIELD_DIALOG.message", "Enter the database field name:"},
		
		{"REMOVE_DATABASE_FIELD_BUTTON_TEXT", "&Remove"},
		
		{"RENAME_DATABASE_FIELD_BUTTON_TEXT", "R&ename..."},			
		{"RENAME_FIELD_DIALOG.title", "Rename Database Field"},
		{"RENAME_FIELD_DIALOG.message", "Enter a new name for the database field:"},
		{"removeField.title", "Remove Selected Data Source Fields"},
		{"removeField.message", "Are you sure you want to delete the selected data source fields?"},


		//MWReferencesPropertiesPage
		{"REFERENCE_NAME_COLUMN_HEADER", "Name"},
		{"TARGET_TABLE_COLUMN_HEADER", "Target Table"},
		{"ON_DATABASE_COLUMN_HEADER", "On Database"},
		{"ADD_REFERENCE_BUTTON_TEXT", "&Add..."},
		{"REMOVE_REFERENCE_BUTTON_TEXT", "&Remove"},
		{"RENAME_REFERENCE_BUTTON_TEXT", "R&ename..."},

		{"NEW_REFERENCE_DIALOG.title", "New Reference"},
		{"enterNameOfNewReference", "&Enter Name of New Reference:"},
		{"selectTheSourceTable", "&Select the Source Table:"},
		{"selectTheTargetTable", "Select the Tar&get Table:"},
		{"onDatabase", "&On Database"},
		{"REFERENCE_NAME_MUST_BE_UNIQUE_ERROR", "The reference name must be unique."},
		{"chooseATargetTable.title", "Choose a Target Table"},
		{"chooseASourceTable.title", "Choose a Source Table"},

		{"RENAME_REFERENCE_DIALOG.title", "Rename Reference"},
		{"RENAME_REFERENCE_DIALOG.message", "Enter a new name for the reference:"},
		{"REMOVE_REFERENCES_WARNING_DIALOG.title", "Remove Selected References"},
		{"REMOVE_REFERENCES_WARNING_DIALOG.message", "Are you sure you want to delete the selected references?"},


		// ColumnPairsPanel
		{"SOURCE_COLUMN_COLUMN_HEADER", "Source Column"},
		{"TARGET_COLUMN_COLUMN_HEADER", "Target Column"},
		{"ADD_ASSOCIATION_BUTTON_TEXT", "A&dd"},	
		{"REMOVE_ASSOCIATION_BUTTON_TEXT", "Re&move"},
		{"REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.title", "Remove Selected Field Associations"},
		{"REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.message", "Are you sure you want to delete the selected field asociations?"},
		{"TABLE_HAS_NO_FIELDS.message", "Your tables must have fields before adding field associations."},
		{"TABLE_HAS_NO_FIELDS.title", "Tables Have No Fields"},		
			
	};

	/**
	 * Returns the initialized array of keys and values that
	 * represents the strings used by the classes in the descriptor
	 * package.
	 *
	 * @return An table where the first element is the key used to
	 * retrieve the second element, which is the value
	 */
	public Object[][] getContents()
	{
		return contents;
	}
}
