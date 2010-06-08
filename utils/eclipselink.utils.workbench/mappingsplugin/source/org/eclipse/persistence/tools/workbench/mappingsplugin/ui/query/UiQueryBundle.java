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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

/**
 * Syntax for the new resource bundle:
 *
 * Sections:
 *  - Dialog title and dialog message
 *  - Unique (single) string
 *  - Text, mnemonic, accelerator, toolTipText for label, buttons
 * 
 * When using a resource bundle for a menu, a button, toolTip or dialog title,
 * each word begins with an upper case letter except for preposition (in, into,
 * from, a, of, etc).
 * When using a resource bundle for a message, only the first letter of the sentence
 * begins with an upper case letter.
 *
 * Important: if using two {xxx} in a bundle, all the ' as to be given as '' (not ")
 * Ex. "Cannot find ''{0}'' because of {1}".
 */
public final class UiQueryBundle extends java.util.ListResourceBundle
{
	static final Object[][] contents = new Object[][] {
		
		//RelationalDescriptorQueryManagerPropertiesPage
		{"NAMES_QUERIES_TAB", "Named Queries"},
		{"CUSTOM_SQL_TAB", "Custom SQL"},
		{"SETTINGS_TAB", "Settings"},
			
			// AddArgumentDialog
			{"ADD_ARGUMENT_DIALOG_TITLE",                      					"New Argument"},
			{"ADD_ARGUMENT_DIALOG_DESCRIPTION",                					"Enter the name of the new argument if named and specify its type."},
			{"ADD_ARGUMENT_DIALOG_DESCRIPTION_TITLE",          					"Create New Argument"},
			{"ENTER_NEW_ARGUMENT_NAME",											"Name:"},
			{"NAMED_IN_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", 					"Named &In"},
			{"NAMED_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG",    				"Named &Out"},
			{"NAMED_IN_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG",      			"N&amed InOut"},
			{"UNNAMED_IN_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", 					"Unnamed I&n"},
			{"UNNAMED_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG",    				"Unnamed O&ut"},
			{"UNNAMED_IN_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG",      			"Unnamed InOu&t"},
			{"TYPE_BORDER_LABEL_ON_ADD_ARGUMENT_DIALOG",                   		"Argument Type"},
			{"PASS_TYPE_BORDER_LABEL_ON_ADD_ARGUMENT_DIALOG",                	"Argument Passing Type"},
			{"PARAMETER_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG",					"&Parameter"},
			{"VALUE_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG",						"&Value"},

			// StoredProcedurePropertiesPage
			{"ARGUMENTS_LIST",													"Arguments"},
			{"ARGUMENT_TYPE_LABEL",												"Argument Type:"},
			{"ARGUMENT_NONE_SELECTED",											"No argument is selected"},
			{"ARGUMENT_NAMED_IN",												"Named In"},
			{"ARGUMENT_NAMED_OUT",												"Named Out"},
			{"ARGUMENT_NAMED_IN_OUT",											"Named In/Out"},
			{"ARGUMENT_UNNAMED_IN",												"Unnamed In"},
			{"ARGUMENT_UNNAMED_OUT",											"Unnamed Out"},
			{"ARGUMENT_UNNAMED_IN_OUT",											"Unnamed In/Out"},
			{"UNNAMED_ARGUMENT", 												"unnamed argument"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_DESCRIPTION",                    "* Indicates optional properties."},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_ADD_ARGUMENT_BUTTON",            "Add Unnamed Output Arguments"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_PROCEDURE_NAME",                 "Procedure Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_DEFAULT_NAME",                   "name"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_OUTPUT_CURSOR_NAME",				"Output Cursor Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_USE_UNNAMED_OUPUT_CURSOR",		"Use Unnamed Output Cursor"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_PROCEDURES_PANEL_NAME",          "Stored Procedures"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_PROCEDURE_DETAILS_LIST",         "Stored Procedure Details"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_GROUP_BOX",                      "Stored Procedures"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_RENAME_BUTTON",                  "&Rename"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NAMED_ARGUMENT_LABEL",           "Named Argument Details"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NAMED_INOUTPUT_ARGUMENT_LABEL",  "Named InOutput Argument Details"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NAMED_OUTPUT_ARGUMENT_LABEL",    "Named Output Argument Details"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NEW_DIALOG_DESCRIPTION",         "Enter the name of the stored procedure to create."},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NEW_DIALOG_DESCRIPTION_TITLE",   "Create New Stored Procedure"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NEW_DIALOG_LABEL",               "&Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NEW_DIALOG_TITLE",               "New Stored Procedure"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_REMOVE_ARGUMENT_BUTTON",         "Remove Unnamed Output Arguments"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_RENAME_PROCEDURE_DIALOG_LABEL",  "&Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_RENAME_PROCEDURE_DIALOG_TITLE",  "Rename Procedure"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_UNAMED_ARGUMENT_LABEL",          "Unnamed Argument Details"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_UNAMED_INOUTPUT_ARGUMENT_LABEL", "Unnamed InOutput Argument Details" },
			{"STORED_PROCEDURE_PROPERTIES_PAGE_UNAMED_OUTPUT_ARGUMENT_LABEL",   "Unnamed Output Argument Details"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_RENAME_PROCEDURE_DIALOG_DESCRIPTION",       "Enter a new name for the stored procedure."},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_RENAME_PROCEDURE_DIALOG_DESCRIPTION_TITLE", "Rename ''{0}''"},

			// Arugment panes
			{"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_NAME_COLUMN",           	"&Name"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_FIELD_SQL_TYPE_NAME_COLUMN",     	"SQ&L Type Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_FIELD_SQL_SUB_TYPE_NAME_COLUMN", 	"S&QL Sub-Type:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_INFIELD_NAME_COLUMN",            	"&InField Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_JAVA_CLASS_TYPE_COLUMN",         	"Java Type Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_JAVA_CLASS_TYPE_COLUMN.tooltip",		"The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure."},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_NESTED_TYPE_FIELD_NAME_COLUMN",		"&Nested Type Field Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_OUTFIELD_NAME_COLUMN",           	"&OutField Name:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_CLASS_COLUMN",		"Argument Value &Type:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_COLUMN",				"Argument Value:"},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_EDIT_BUTTON",			"&Edit Value..."},
			{"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_EDIT_DIALOG_TITLE",	"Edit Argument Value"},

			//RelationalQueriesPropertiesPage.			
			{"QUERIES_PANEL_NAME",                    "Queries"},
			{"DESCRIPTOR_ALIAS_LABEL",                "Descr&iptor Alias:"},
			{"CANNOT_RENAME_QUERY_DIALOG.message",    "Cannot rename EclipseLink reserved finders."},
			{"RENAME_QUERY_DIALOG.title",             "Rename Query"},
			{"RENAME_QUERY_DIALOG.message",           "Enter a new name for the query:"},
			{"NAMED_QUERIES_LIST",                    "Named Queries"},
			{"QUERIES_LIST",                          "Queries:"},
			{"QUERY_VARIETY_LABEL",                   "Query &Variety:"},
			{"QUERY_VARIETY_NONE_SELECTED",           "No query is selected"},
			{"QUERY_VARIETY_EJB_SELECT",              "EJB Select"},
			{"QUERY_VARIETY_EJB_FINDER",              "EJB Finder"},
			{"QUERY_VARIETY_TOPLINK_NAMED_QUERY",     "EclipseLink Named Query"},
			{"QUERY_VARIETY_TOPLINK_RESERVED_FINDER", "EclipseLink Reserved Finder"},

			//RelationalCustomSqlPropertiesPage	
  			{"CUSTOM_QUERY_INSERT_TAB", "Insert"},
  			{"CUSTOM_QUERY_UPDATE_TAB", "Update"},
  			{"CUSTOM_QUERY_DELETE_TAB", "Delete"},
  			{"CUSTOM_QUERY_READ_OBJECT_TAB", "Read Object"},
  			{"CUSTOM_QUERY_READ_ALL_TAB", "Read All"},
			
			//RelationalQueriesSettingsPage
			{"SETTINGS_PANEL_NAME", "Settings"},
			{"QUERY_TIMEOUT_BORDER_LABEL", "Query Timeout"},
			{"QUERY_SETTINGS_DEFAULT_TIMEOUT", "&Default Timeout"},
			{"QUERY_SETTINGS_NO_TIMEOUT", "&No Timeout"},
			{"QUERY_SETTINGS_TIMEOUT", "&Timeout:"},
			{"QUERY_SETTINGS_TIMEOUT_POSTFIX_LABEL", "seconds"},
					

			//AddQueryDialog
			{"ADD_QUERY_DIALOG.title", "Add Named Query"},
			{"READ_OBJECT_RADIOBUTTON_ON_ADD_QUERY_DIALOG", "Read Object"},
			{"READ_ALL_RADIOBUTTON_ON_ADD_QUERY_DIALOG", "Read All"},
			{"REPORT_RADIOBUTTON_ON_ADD_QUERY_DIALOG", "Report"},
			
			{"EJB_SELECT_TYPE_LABEL_ON_ADD_QUERY_DIALOG", " EJB Select"},
			{"EJB_FINDER_RADIOBUTTON_ON_ADD_QUERY_DIALOG", " EJB Finder"},
			{"NAME_TEXT_FIELD_LABEL_ON_ADD_QUERY_DIALOG", "Name:"},
			{"TOPLINK_NAMED_QUERY_RADIOBUTTON_ON_ADD_QUERY_DIALOG", " EclipseLink Named Query"},
			{"TOPLINK_RESERVED_FINDER_RADIOBUTTON_ON_ADD_QUERY_DIALOG", " EclipseLink Reserved Finder"},
			{"TYPE_BORDER_LABEL_ON_ADD_QUERY_DIALOG", "Type:"},
			{"VARIETY_BORDER_LABEL_ON_ADD_QUERY_DIALOG", "Variety:"},
			{"EITHER_TEXTFIELD_COMBOBOX_SET_VISIBLE_EXCEPTION", "Developer: either the text field or combo box must be set visible here."},
		
			//QueryPanel
			{"GENERAL_TAB","General"},
			{"OPTIONS_TAB","Options"},
			{"FORMAT_TAB","Selection Criteria"},
			{"ORDER_TAB","Order"},
			{"OPTIMIZATION_TAB","Optimization"},
				
			//QueryGeneralPanel
			{"QUERY_PARAMETERS_TABLE_TITLE",    "Parameters"},
			{"QUERY_TYPE_COMBO_BOX_LABEL",      "T&ype:"},
			{"QUERY_CHANGE_QUERY_TYPE_TITLE",   "Change Query Type"},
			{"QUERY_CHANGE_QUERY_TYPE_MESSAGE", "Settings not shared between query types will be lost. Do you want to continue?"},
			{"QUERY_TYPE_CHANGE_DISSALLOWED", 	"Query type change is dissallowed because of a setting in preferences.  This can be changed in mapping preferences."},
			{"QUERY_FORMAT_CHANGE_DISSALLOWED", "Query format change is dissallowed because of a setting in preferences.  This can be changed in mapping preferences."},
	
			//QueryParametersPanel
			{"ADD_PARAMETER_BUTTON", "A&dd..."},
			{"REMOVE_PARAMETER_BUTTON", "Re&move"},
			{"EDIT_PARAMETER_BUTTON", "&Edit"},
			{"TYPE_COLUMN_HEADER", "Type"},
			{"NAME_COLUMN_HEADER", "Name"},
			{"QUERY_PARAMETER_DIALOG.PARAMETER_TYPE_LABEL", "&Type:"},
			{"QUERY_PARAMETER_DIALOG.PARAMETER_NAME_LABEL", "&Name:"},
			{"QUERY_PARAMETER_DIALOG.title",      "Add Query Parameter"},
			{"QUERY_PARAMETER_DIALOG_EDIT.title", "Edit Query Parameter"},
			{"QUERY_PARAMETER_DIALOG_NO_PARAMETER_NAME_SPECIFIED", "The parameter name cannot be empty."},
			{"QUERY_PARAMETER_DIALOG_CLASS_ALREADY_EXISTS",    "A parameter named ''{0}'' already exists."},						
			
            {"SELECTION_CRITERIA_TYPE_LABEL",    "T&ype:"},                        

            //QueryOptionsPanel
			{"REFRESH_IDENTITY_MAP_RESULTS_CHECK_BOX", "Refresh Identity &Map Results"},
			{"CACHE_STATMENT_LABEL","&Cache Statement:"},
			{"BIND_PARAMETERS_LABEL","&Bind Parameters:"},	
			{"CACHE_USAGE_LABEL","Cache &Usage:"},
			{"IN_MEMORY_QUERY_INDIRECTION_LABEL","&In Memory Query Indirection:"},
            {"ADVANCED_BUTTON_TEXT", "Ad&vanced..."},
 
            {"QUERY_DESCRIPTOR_DEFAULT_VALUE",  "Default ({0})"},
            
            //MWQuery - cacheUsage strings			
			{"UNDEFINED_CACHE_USAGE_OPTION","Use Descriptor Setting"},
			{"DO_NOT_CHECK_CACHE_OPTION","Do Not Check Cache"},
			{"CHECK_CACHE_BY_EXACT_PRIMARY_KEY_OPTION","Check Cache by Exact Primary Key"},
			{"CHECK_CACHE_BY_PRIMARY_KEY_OPTION","Check Cache by Primary Key"},
			{"CHECK_CACHE_THEN_DATABASE_OPTION", "Check Cache Then Database"},
			{"CHECK_CACHE_ONLY_OPTION", "Check Cache Only"},
			{"CONFORM_RESULTS_IN_UNIT_OF_WORK_OPTION", "Conform Results in Unit of Work"},
			//MWQuery - inMemoryQueryIndirection strings		  	
			{"THROW_INDIRECTION_EXCEPTION_OPTION","Throw Indirection Exception"},
			{"TRIGGER_INDIRECTION_OPTION","Trigger Indirection"},
			{"IGNORE_EXCEPTION_RETURN_CONFORMED_OPTION","Ignore Exception Return Conformed"},
			{"IGNORE_EXCEPTION_RETURN_NOT_CONFORMED_OPTION","Ignore Exception Return Not Conformed"},

			//QueryAdvancedOptionsDialog
			{"USE_DESCRIPTOR_SETTING_OPTION", "Use Descriptor Setting"},
			{"DONT_ACQUIRE_LOCKS_OPTION","Do Not Acquire Locks"},
			{"ACQUIRE_LOCKS_OPTION","Acquire Locks"},
			{"ACQUIRE_LOCKS_NO_WAIT_OPTION","Acquire Locks No Wait"},		
			{"DISTINCT_STATE_CHECK_BOX_LABEL","&Distinct State:"},
			{"UNCOMPUTED_DISTINCT_OPTION","Uncomputed Distinct"},
			{"USE_DISTINCT_OPTION","Use Distinct"},
			{"DO_NOT_USE_DISTINCT_OPTION","Do Not Use Distinct"},
			{"ADVANCED_QUERY_OPTIONS_DIALOG.title", "Advanced Query Options"},
			{"MAINTAIN_CACHE_CHECK_BOX","&Maintain Cache"},
			{"USE_WRAPPER_POLICY_CHECK_BOX", "&Use Wrapper Policy"},
			{"PREPARE_CHECK_BOX", "&Prepare SQL Once"},
			{"CACHE_QUERY_RESULTS_CHECK_BOX", "&Cache Query Results"},	
			{"OUTER_JOIN_ALL_SUBCLASSES_CHECK_BOX", "Outer &Join All Subclasses"},	
			{"REFRESH_REMOTE_IDENTITY_MAP_RESULTS_CHECK_BOX", "&Refresh Remote Identity Map Results"},
			{"MAXIMUM_ROWS_PANEL.title", "Maximum Rows"},
			{"NO_MAXIMUM_CHECK_BOX", "N&o Maximum:"},
			{"FIRST_RESULT_PANEL.title", "First Result"},
			{"SET_FIRST_RESULT_CHECK_BOX", "C&hoose First Result:"},
			{"QUERY_TIMEOUT_PANEL.title", "Query Timeout"},
			{"QUERY_TIMEOUT_SECONDS_LABEL", "seconds"},
			{"QUERY_TIMEOUT_USE_DESCRIPTOR_SETTING", "De&fault Timeout"},
			{"QUERY_TIMEOUT_NO_TIMEOUT", "&No Timeout"},
			{"QUERY_TIMEOUT_TIMEOUT", "&Timeout:"},
			{"PESSIMISTIC_LOCKING_CHECK_BOX_LABEL","Pessimistic &Locking:"},
			{"SET_REFRESH_IDENTITY_MAP_RESULTS.title", "Set Refresh Identity Map Results?"},
			{"SET_LOCKING_DIALOG_MESSAGE","If a query uses pessimistic locking, it must refresh the identity map results.<br>Would you like us to set Refresh Identity Map Results and Refresh Remote Identity Map Results to true?"},
			{"SET_FALSE_MAINTAIN_CACHE_DIALOG_MESSAGE","If a query does not maintain cache, it can not refresh the identity map results.<br>Would you like us to set Refresh Identity Map Results and Refresh Remote Identity Map Results to false?"},
			{"EXCLUSIVE_CONNECTION_CHECK_BOX", "&Exclusive Connection"},

			//QueryFormatPanel
			{"AUTO_GENERATED_QUERY_COMMENT", "The query for this finder will be generated at runtime."},
			{"EXPRESSION_PANEL_LABEL","Expression:"},
			{"EDIT_EXPRESSION_BUTTON","   &Edit...   "},
			{"EXPRESSION_OPTION","Expression"},
			{"SQL_OPTION","SQL"},
			{"EJBQL_OPTION","EJBQL"},
			{"STORED_PROCEDURE_OPTION", "Stored Procedure"},
			{"QUERY_FORMAT_TYPE_CHANGED_DIALOG_TITLE.title","Change Query Format?"},
			{"QUERY_FORMAT_TYPE_CHANGED_FROM_EXPRESSION_FORMAT_DIALOG_MESSAGE", "Changing the query format will delete your expression information.  Are you sure you want to do this?"},
			{"QUERY_FORMAT_TYPE_CHANGED_FROM_STRING_FORMAT_DIALOG_MESSAGE", "Changing the query format will delete your query string information.  Are you sure you want to do this?"},
			{"QUERY_STRING_PANEL_LABEL","Q&uery String:"},

			//BasicExpressionPanel
			{"FIRST_ARGUMENT_PANEL_TITLE.title", "First Argument"},
			{"OPERATOR_LABEL:", "&Operator:"},
			{"SECOND_ARGUMENT_PANEL_TITLE.title", "Second Argument"},			

			//FirstArgumentPanel
			{"QUERY_KEY_LABEL_ON_FIRST_ARGUMENT_PANEL", "Query Key:"},
			{"QUERY_KEY_EDIT_BUTTON_ON_FIRST_ARGUMENT_PANEL","&Edit"},
			
			//SecondArgumentPanel
			{"LITERAL_RADIO_BUTTON_ON_SECOND_ARGUMENT_PANEL", "L&iteral"},
			{"PARAMETER_RADIO_BUTTON_ON_SECOND_ARGUMENT_PANEL", "&Parameter"},
			{"QUERY_KEY_RADIO_BUTTON_ON_SECOND_ARGUMENT_PANEL", "Query &Key"},
			
			//ExpressionBuilderDialog
			{"EXPRESSION_BUILDER_DIALOG.title", "Expression Builder"},
						
			//ExpresisonTreePanel
			{"ADD_EXPRESSION_BUTTON", "&Add..."},		
			{"ADD_NESTED_EXPRESSION_BUTTON", "Add &Nested..."},		
			{"REMOVE_EXPRESSION_BUTTON", "&Remove"},
			{"OPERATOR_TYPE_COMBO_BOX_LABEL", "&Logical Operator:"},
			
			//LiteralArgumentPanel
			{"EDIT_LITERAL_TYPE_AND_VALUE.title","Edit the Literal Type and Value"},
			{"CHOOSE_TYPE_LIST_CHOOSER_TITLE.title", "Choose Type"},					
			{"EDIT_LITERAL_BUTTON", "E&dit"},
			{"TYPE_LABEL:", "&Type:"},
			{"TYPE_NAME_LIST_CHOOSER_INPUT_LABEL:", "Type &Name:"},
			{"TYPES_LIST_CHOOSER_LIST_LABEL:", "&Types:"},
			{"VALUE_LABEL:", "&Value:"},	
			{"beASingleCharacter", "The format for {0} must be a single character."},
			{"beAString", "The format for {0} must be a string."},
			{"beBetweenDouble", "The format for {0} must be between {1} and {2}."},
			{"beBetweenFloat", "The format for {0} must be between {1} and {2}."},
			{"beBetweenInteger", "The format for {0} must be between {1} and {2}."},
			{"beBetweenLong", "The format for {0} must be between {1} and {2}."},
			{"beBetween0And127Inclusive", "The format for {0} must be between 0 and 127 inclusive."},
			{"beBetweenShort", "The format for {0} must be between {1} and {2}."},
			{"beEitherTrueOrFalse", "The format for {0} must be either 'true' or 'false'."},
			{"beInTheFormat1", "The format for {0} must be in the format YYYY/MM/DD or YYYY-MM-DD."},
			{"beInTheFormat2", "The format for {0} must be in the format HH-MM-SS or HH:MM:SS."},
			{"beInTheFormat3", "The format for {0} must be in the format YYYY/MM/DD HH:MM:SS or YYYY-MM-DD HH:MM:SS."},
			{"beInTheFormat4", "The format for {0} must be in the format YYYY/MM/DD or YYYY-MM-DD."},
			{"beInTheFormat5", "The format for {0} must be in the format YYYY/MM/DD HH:MM:SS, YYYY/MM/DD, or YYYY-MM-DD."},
			{"containsOnlyDigits", "The format for {0} must contain only digits, '-', and '.'."},
			{"containsOnlyDigitsAnd", "The format for {0} must contain only digits and '-'."},
			{"illegalFormat", "Illegal format"},
						
			//QueryableArgumentPanel
			{"QUERY_KEY_EDIT_BUTTON_ON_SECOND_ARGUMENT_PANEL","E&dit"},
			
			//QueryableEditDialog
			{"CHOOSE_QUERY_KEY_DIALOG_TITLE.title", "Choose Query Key"},
 			
 			//QueryableTree
 			{"ALLOWS_NULL_CHECK_BOX_LABEL_IN_QUERYABLE_TREE", "&Allows Null"},
 			{"ALLOWS_NONE_CHECK_BOX_LABEL_IN_QUERYABLE_TREE", "&Allows None"},

 			// QuerySelectionCriteriaPanel
 			{"QUERY_QUERY_FORMAT_TITLE",   "Change Query Format"},
 			{"QUERY_QUERY_FORMAT_MESSAGE", "If you change the query format, EclipseLink will automatically update the query, as needed. Do you want to continue?"},
		
		{"READ_ALL_QUERY", "Read All"},
 		{"READ_OBJECT_QUERY", "Read Object"},
 		{"REPORT_QUERY", "Report"},

		{"BATCH_READ_ATTRIBUTES_LIST", "Batch Read Attributes:"},
		{"BATCH_READ_ATTRIBUTES_LIST_EDIT_BUTTON", "Edit"},
		{"JOINED_ATTRIBUTES_LIST", "Joined Attributes:"},
 		{"JOINED_ATTRIBUTES_LIST_EDIT_BUTTON", "&Edit"},
		{"ORDERING_ATTRIBUTES_LIST", "Ordering Attributes:"},
 		{"ORDERING_ATTRIBUTES_LIST_EDIT_BUTTON", "&Edit"},
        {"ORDERING_ATTRIBUTES_DIALOG_TITLE", "Add Ordering Attribute"},
        {"ORDERING_ATTRIBUTES_EDIT_DIALOG_TITLE", "Edit Ordering Attribute"},
		{"ASCENDING_BUTTON", "&Ascending"},
		{"DESCENDING_BUTTON", "&Descending"},
		{"ASCENDING_CHOICE", "Ascending"},
		{"DESCENDING_CHOICE", "Descending"},
		{"ORDERING_ATTRIBUTES_DIALOG_SELECTED_ATTRIBUTE_BUTTON", "Selected Attribute:"},
		{"ORDERING_ATTRIBUTES_DIALOG_NEW_ATTRIBUTE_BUTTON", "New Attribute:"},
		{"ORDERING_ATTRIBUTES_DIALOG_ORDER_LABEL", "Order:"},
				
		{"REPORT_QUERY_ATTRIBUTES_LIST", "Attributes"},
		{"REPORT_QUERY_ATTRIBUTES_LIST_EDIT_BUTTON", "&Edit"},
        {"REPORT_QUERY_ATTRIBUTES_DIALOG_TITLE", "Add Report Query Attribute"},
        {"REPORT_QUERY_ATTRIBUTES_EDIT_DIALOG_TITLE", "Edit Report Query Attribute"},
		{"REPORT_QUERY_GROUPING_ATTRIBUTES_LIST", "Grouping Attributes:"},		
		{"REPORT_QUERY_GROUPING_ATTRIBUTES_LIST_EDIT_BUTTON", "&Edit"},
		{"ATTRIBUTES_TAB", "Attributes"},
		{"GROUP_ORDER_TAB", "Group/Order"},
		
		{"FUNCTION_LABEL", "&Function:"},
		{"ATTRIBUTE_ITEM_NAME_LABEL", "&Name:"},
		{"ITEM_NAME_COLUMN_HEADER", "Name"},
		{"ATTRIBUTE_COLUMN_HEADER", "Attribute"},
		{"FUNCTION_COLUMN_HEADER", "Function"},
		{"ORDER_COLUMN_HEADER", "Order"},
					
		{"RETURN_CHOICE_LABEL", "&Return Choice:"},
		{"RESULT_COLLECTION_RETURN_OPTION", "Result Collection"},
		{"SINGLE_RESULT_RETURN_OPTION", "Single Result"},
		{"SINGLE_VALUE_RETURN_OPTION", "Single Value"},
		{"SINGLE_ATTRIBUTE_RETURN_OPTION", "Single Attribute"},

		{"RETRIEVE_PRIMARY_KEYS_LABEL", "Retrieve &Primary Keys:"},
		{"NO_PRIMARY_KEY_OPTION", "None"},
		{"FULL_PRIMARY_KEY_OPTION", "All"},
		{"FIRST_PRIMARY_KEY_OPTION", "First"},
        {"GROUPING_ATTRIBUTES_DIALOG_TITLE", "Add Grouping Attribute"},
        {"GROUPING_ATTRIBUTES_EDIT_DIALOG_TITLE", "Edit Grouping Attribute"},
        {"JOINED_ATTRIBUTES_DIALOG_TITLE", "Add Joined Attribute"},
        {"JOINED_ATTRIBUTES_EDIT_DIALOG_TITLE", "Edit Joined Attribute"},
        {"BATCH_READ_ATTRIBUTE_DIALOG_TITLE", "Add Batch Read Attribute"},
        {"BATCH_READ_ATTRIBUTE_EDIT_DIALOG_TITLE", "Edit Batch Read Attribute"},
			
		//EisDescriptorQueryManagerPropertiesPage
		{"CUSTOM_CALL_TAB", "Custom &Calls"},
		
		// *** EisCustomCallsPropertiesPage ***
		{"CUSTOM_CALLS_LIST", "Custom Calls"},
		
		{"INSERT_INTERACTION_NAME", "Insert"},
		{"UPDATE_INTERACTION_NAME", "Update"},
		{"DELETE_INTERACTION_NAME", "Delete"},
		{"READ_OBJECT_INTERACTION_NAME", "Read Object"},
		{"READ_ALL_INTERACTION_NAME", "Read All"},
		{"DOES_EXIST_INTERACTION_NAME", "Does Exist"},
		
			//InteractionPanel
			{"ADD_ARGUMENT_BUTTON", "&Add..."},
			{"FUNCTION_NAME", "Function &Name:  "},
			{"INPUT_ARGUMENTS", "Input A&rguments:  "},
			{"INPUT_RECORD_NAME", "&Input Record Name:  "},
			{"INPUT_RESULT_PATH", "Input Result &Path:  "},
			{"INPUT_ROOT_ELEMENT_NAME", "Input Root &Element Name:  "},
			{"ARGUMENT_NAME_COLUMN_HEADER", "Arg&ument Name"},
			{"ARGUMENT_FIELD_NAME_COLUMN_HEADER", "Fie&ld Name"},
			{"INTERACTION_TYPE", "Interaction &Type:  "},
			{"OUTPUT_ARGUMENTS", "&Output Arguments:  "},
			{"OUTPUT_RESULTS_PATH", "Output Result Path:  "},
			{"REMOVE_ARGUMENT_BUTTON", "Re&move"},
			{"XML_INTERACTION", "&XML Interaction"},
			{"PROPERTIES", "Propertie&s:"},
			{"PROPERTY_NAME", "Property Name"},
			{"PROPERTY_VALUE", "Property Value"},
			
			//EisQueryPanel
			{"CALL_TAB", "C&all"},

		// QuickViewPanel
		{"QUICK_VIEW_LABEL",                 "&Quick View:"},
		{"QUICK_VIEW_REMOVE_BUTTON",         "Re&move"},
		{"QUICK_VIEW_EMPTY_ITEM_LABEL",      "No items"},
		{"QUICK_VIEW_PARAMETERS_LABEL",      "Parameters"},
		{"QUICK_VIEW_PARAMETER_LABEL",       "{0} : {1}"},
		{"QUICK_VIEW_PARAMETERS_ACCESSIBLE", "Shows the list of parameters for the selected query"},
		{"QUICK_VIEW_ATTRIBUTES_LABEL",                       "Attributes"},
		{"QUICK_VIEW_ATTRIBUTES_LABEL_ACCESSIBLE",            "Shows the list of attributes for the selected query"},
		{"QUICK_VIEW_JOINED_ATTRIBUTES_LABEL",                "Joined Attributes"},
		{"QUICK_VIEW_JOINED_ATTRIBUTES_LABEL_ACCESSIBLE",     "Shows the list of joined attributes for the selected query"},
		{"QUICK_VIEW_GROUPING_ATTRIBUTES_LABEL",              "Grouping Attributes"},
		{"QUICK_VIEW_GROUPING_ATTRIBUTES_LABEL_ACCESSIBLE",   "Shows the list of grouping attributes for the selected query"},
		{"QUICK_VIEW_BATCH_READ_ATTRIBUTES_LABEL",            "Batch Reading Attributes"},
		{"QUICK_VIEW_BATCH_READ_ATTRIBUTES_LABEL_ACCESSIBLE", "Shows the list of batch read attributes for the selected query"},
		{"QUICK_VIEW_ORDERING_ATTRIBUTES_LABEL",              "Ordering Attributes"},
		{"QUICK_VIEW_ORDERING_ATTRIBUTES_LABEL_ACCESSIBLE",   "Shows the list of ordering attributes for the selected query"},
	}; 

	public Object[][] getContents()
	{
		return contents;
	}
}
