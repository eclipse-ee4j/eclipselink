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
package org.eclipse.persistence.tools.workbench.scplugin;

import java.util.ListResourceBundle;

public final class SCPluginResourceBundle extends ListResourceBundle {

	private static final Object[][] contents = { 
		
		{"PROJECT_NAME_LABEL", "Name:"},
		{"BROWSE_BUTTON_TEXT", "Browse..."},

		// ProjectNode
		{ "PROJECT_NODE_DISPLAY_STRING_READ_ONLY", "{0} (Read-Only)" },

		// Project
		{ "SESSIONS_CONFIGURATION" , "{0} located at {1}" },

		// Dialogs shown during save
		{ "SAVE_AS_DIALOG_TITLE",             "Save As" },
		{ "SAVE_AS_DIALOG_CANT_SAVE",         "Cannot save the file ''{0}''. The file exists and is marked Read-Only. Save the file with another file name or to another location." },
		{ "SAVE_AS_DIALOG_REPLACE",           "The file {0} already exists. Do you want to replace the existing file?" },
		{ "SAVE_AS_DIALOG_ALREADY_OPENED",    "{0} cannot give a document the same name as an open document.{2}Type a different name for the document you want to save.{2}({1})" },
		{ "SAVE_ERROR_MESSAGE",               "Cannot save ''{0}'' due to an error. Refer to the following message:" },
		{ "SAVE_READ_ONLY_ERROR_MESSAGE",     "{0} cannot complete the save due to a file permission error.{2}({1})" },
		{ "SAVE_RETRY_TO_SAVE_MESSAGE",       "Do you want to retry to save?" },

		// Session title bar
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_DATABASE_EIS",   "{0}  -  {1}" },
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_DATABASE_RDBMS", "{0}  -  {1}" },
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_DATABASE_XML",   "{0}  {1}" },
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_BROKER",         "{0}  {1}" },
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_SERVER_EIS",     "{0}  -  {1}" },
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_SERVER_RDBMS",   "{0}  -  {1}" },
		{ "SESSION_DISPLAY_STRING_TITLE_BAR_SERVER_XML",     "{0}  {1}" },

		{ "NEW_SESSIONS_CONFIGURATION", "&Sessions Configuration" },
		{ "NEW_SESSIONS_CONFIGURATION.TOOL_TIP", "Create New Sessions Configuration" },

		// SCPlugin
		{ "SESSIONS_XML_CANNOT_OPEN_FILE" , "''{0}'' could not be opened. Refer to the following message:" },

		// ui.project.SessionCreationDialog
		{ "SESSION_CREATION_DIALOG_TITLE",            "Create New Session" },
		{ "SESSION_TYPE_LABEL",                       "Select Session" },
		{ "SERVER_SESSION_RADIO_BUTTON",              "&Server Session" },
		{ "DATABASE_SESSION_RADIO_BUTTON",            "&Database Session" },
		{ "DATASOURCE_LABEL",                         "Select Data Source" },
		{ "RELATIONAL_RADIO_BUTTON",                  "D&atabase" },
		{ "XML_RADIO_BUTTON",                         "&XML" },
		{ "EIS_RADIO_BUTTON",                         "&EIS" },
		{ "DATABASE_PLATFORM_LABEL",                  "&Platform:" },
		{ "SAVE_LOCATION_LABEL",                      "Location:" },
		{ "SESSION_NAME_LABEL",                       "&Name:" },
		{ "EIS_PLATFORM_LABEL",                       "&Platform:" },
		{ "SESSION_CREATION_DIALOG_NEW_SESSION_NAME", "Session" },
		{ "SESSION_CREATION_DIALOG_INVALID_NAME",     "The name entered is already defined." },
		{ "USE_SERVER_PLATFORM_CHECK_BOX",            "&Use Server Platform" },

		// Popup Menu, Selected menu
		{ "ADD_BROKER",                            "&Broker..."},
		{ "ADD_BROKER.TOOL_TIP",                   "Create a New Session Broker"},
		{ "ADD_NAMED_CONNECTION_POOL",             "&Named Connection Pool..."},
		{ "ADD_NAMED_CONNECTION_POOL.TOOL_TIP",    "Create a New Named Connection Pool"},
		{ "ADD_SEQUENCE_CONNECTION_POOL",          "&Sequence Connection Pool"},
		{ "ADD_SEQUENCE_CONNECTION_POOL.TOOL_TIP", "Add the Sequence Connection Pool"},
		{ "ADD_SESSION",                           "&Session..." },
		{ "ADD_SESSION.TOOL_TIP",                  "Create a New Database or Server Session" },
		{ "ADD_WRITE_CONNECTION_POOL",             "&Write Connection Pool"},
		{ "ADD_WRITE_CONNECTION_POOL.TOOL_TIP",    "Add the Write Connection Pool"},
		{ "ADD_READ_CONNECTION_POOL",             "&Read Connection Pool"},
		{ "ADD_READ_CONNECTION_POOL.TOOL_TIP",    "Add the Read Connection Pool"},
		{ "DELETE_BROKER_TITLE",                   "Remove Broker"},
		{ "DELETE_BROKER_MESSAGE",                 "Only empty session broker can be deleted. Remove all managed sessions from ''{0}'' prior to delete."},
		{ "DELETE_BROKERS_MESSAGE",                "The selection contains session brokers with managed sessions. Remove all managed sessions from them prior to delete."},
		{ "DELETE_CONNECTION_POOL",                "&Delete"},
		{ "DELETE_CONNECTION_POOL.TOOL_TIP",       "Delete the Selected Connection Pools"},
		{ "DELETE_SESSION",                        "&Delete" },
		{ "DELETE_SESSION.TOOL_TIP",               "Delete the Selected Session" },
		{ "DELETE_DIALOG_TITLE",                   "Remove" },
		{ "NEW_CONNECTION_POOL.TOOL_TIP",          "Add a Connection Pool" },
		{ "NEW_MENU",                              "&New" },
		{ "RENAME_SESSION",                        "&Rename..." },
		{ "RENAME_SESSION.TOOL_TIP",               "Rename the Selected Session" },
		{ "RENAME_DIALOG_TITLE",                   "Rename" },
		{ "RENAME_POOL",                           "&Rename..." },
		{ "RENAME_POOL.TOOL_TIP",                  "Rename the Selected Connection Pool" },
		{ "UNMANAGED_SESSION",                     "&Unmanage"},
		{ "UNMANAGED_SESSION.TOOL_TIP",            "Remove the Selected Session from Its Session Broker"},

		// ui.broker.SessionListDialog
		{ "SESSIONS_LIST_DIALOG_TITLE",            "Sessions" },
		{ "SESSIONS_LIST_DIALOG_SESSIONS_LIST",    "&Select the sessions to manage:" },

		// ui.project.BrokerCreationDialog
		{ "BROKER_CREATION_DIALOG_INVALID_NAME",   "The name entered is already defined." },
		{ "BROKER_CREATION_DIALOG_TITLE",          "Create New Session Broker" },
		{ "BROKER_CREATION_DIALOG_NAME_LABEL",     "&Name:" },
		{ "BROKER_CREATION_DIALOG_SESSIONS_LIST",  "&Select the sessions to manage:" },
		{ "BROKER_CREATION_DIALOG_NEW_NAME",       "SessionBroker" },

		// ui.pool.basic.ConnectionCountPane
		{ "CONNECTION_POOL_CONNECTION_COUNT_TITLE",         "Connection Count" },
		{ "CONNECTION_POOL_EXCLUSIVE_CONNECTION_CHECK_BOX", "E&xclusive Connections" },
		{ "CONNECTION_POOL_MAXIMUM_CONNECTIONS_SPINNER",    "M&aximum:" },
		{ "CONNECTION_POOL_MINIMUM_CONNECTIONS_SPINNER",    "&Minimum:" },
		{ "CONNECTION_POOL_NON_JTS_DATASOURCE_FIELD",       "Non-JTS &Data Source:" },
		{ "CONNECTION_POOL_NON_JTS_CONNECTION_URL_FIELD",   "Non-JTS Connection &URL:" },

		// ui.session.basic.SessionLoggingPropertiesPage
		{ "DATABASE_SESSION_DESTINATION",             "&Destination:" },
		{ "DATABASE_SESSION_DESTINATION_CONSOLE",     "&Console" },
		{ "DATABASE_SESSION_DESTINATION_FILE",        "F&ile"},
		{ "DATABASE_SESSION_LOG_FILE_LOCATION_FIELD", "Lo&g Location:" },
		{ "DATABASE_SESSION_LOGGING_LEVEL_COMBO_BOX", "&Logging Level:" },
		{ "DATABASE_SESSION_LOGGING_TYPE_NO_LOGGING", "No &Logging" },
		{ "DATABASE_SESSION_LOGGING_TYPE_JAVA",       "&Java" },
		{ "DATABASE_SESSION_LOGGING_TYPE_SERVER",     "Ser&ver" },
		{ "DATABASE_SESSION_LOGGING_TYPE_STANDARD",   "St&andard" },
		{ "SESSIONS_LOGGING_PROPERTIES_PAGE_DEFAULT_LOG_FILENAME", "standard output"},

		// ui.project.ProjectPropertiesPage
		{ "PROJECT_LOCATION_BROWSE_BUTTON", "Chan&ge..."},
		{ "PROJECT_LOCATION_FIELD",         "&Project Save Location:"},

		// ui.project.SessionsList
		{ "PROJECT_SESSIONS_BROKER_ADD_BUTTON", "Add &Broker..." },
		{ "PROJECT_SESSIONS_RENAME_BUTTON",     "Rena&me..." },

		// ui.tools.AbstractSessionsListPane
		{ "PROJECT_SESSIONS_LIST",                        "Sessions for ''{0}''"},
		{ "PROJECT_SESSIONS_ADD_BUTTON",                  "Add Sessi&on..."},
		{ "PROJECT_SESSIONS_PROMPT_REMOVE_SINGLE",        "Are you sure to remove ''{0}'' from {1}?" },
		{ "PROJECT_SESSIONS_PROMPT_REMOVE_SINGLE_TITLE",  "Remove Session" },
		{ "PROJECT_SESSIONS_PROMPT_REMOVE_MULTI",         "Are you sure to remove the selected sessions from {0}?" },
		{ "PROJECT_SESSIONS_PROMPT_REMOVE_MULTI_TITLE",   "Remove Sessions" },
		{ "PROJECT_SESSIONS_REMOVE_BUTTON",               "&Remove..."},
		{ "PROJECT_SESSIONS_SESSION_TYPE_BROKER",         "{0} {1}"},
		{ "PROJECT_SESSIONS_SESSION_TYPE_DATABASE_EIS",   "{0} ({1})"},
		{ "PROJECT_SESSIONS_SESSION_TYPE_DATABASE_RDBMS", "{0} ({1})"},
		{ "PROJECT_SESSIONS_SESSION_TYPE_DATABASE_XML",   "{0} {1}"},
		{ "PROJECT_SESSIONS_SESSION_TYPE_SERVER_EIS",     "{0} ({1})"},
		{ "PROJECT_SESSIONS_SESSION_TYPE_SERVER_RDBMS",   "{0} ({1})"},
		{ "PROJECT_SESSIONS_SESSION_TYPE_SERVER_XML",     "{0} {1}"},
		
		// ui.pool.basic.RdbmsReadPoolLoginPropertiesPage
		{ "CONNECTION_READ_USE_NON_TRANSACTIONAL_READ_LOGIN_CHECK_BOX", "N&on-Transactional Read Login" },
		{ "CONNECTION_READ_EXCLUSIVE_CONNECTIONS_CHECK_BOX", "&Exclusive Connections" },

		// ui.session.basic.SessionServerPlatformPropertiesPage
		{ "SERVER_PLATFORM_CHECK_BOX", "Ser&ver Platform" },
		{ "SERVER_PLATFORM_ENABLE_RUNTIME_SERVICES_CHECK_BOX",                "Enable &Runtime Services" },
		{ "SERVER_PLATFORM_ENABLE_EXTERNAL_TRANSACTION_CONTROLLER_CHECK_BOX", "Enable E&xternal Transaction Controller (JTA)" },

		// ui.session.basic.SessionServerPlatformPropertiesPage.CustomServerPlatformSubPane
		{ "SERVER_PLATFORM_CUSTOM_EXTERNAL_TRANSACTION_CONTROLLER_BROWSE_BUTTON", "Bro&wse..." },
		{ "SERVER_PLATFORM_CUSTOM_EXTERNAL_TRANSACTION_CONTROLLER_CHOOSER",       "Transaction &Controller Class (JTA):" },
		{ "SERVER_PLATFORM_CUSTOM_SERVER_CLASS_BROWSE_BUTTON",                    "&Browse..." },
		{ "SERVER_PLATFORM_CUSTOM_SERVER_CLASS_CHOOSER",                          "Server &Platform Class:" },

		// ui.project.SessionCreationDialog
		// ui.session.basic.SessionServerPlatformPropertiesPage
		{ "SERVER_PLATFORM_COMBO_BOX", "P&latform:" },

		// ui.login.AbstractLoginPane
		{ "CONNECTION_PASSWORD_FIELD",  "&Password:" },
		{ "CONNECTION_USER_NAME_FIELD", "User Na&me:" },
		{ "SAVE_PASSWORD_CHECK_BOX",	"&Save Password" },
		{ "SAVE_USERNAME_CHECK_BOX",	"Sa&ve Username" },

		// AbstractRdbmsLoginPane
		{ "CONNECTION_RDBMS_DATA_SOURCE_FIELD",          "Data Sour&ce Name:" },
		{ "CONNECTION_RDBMS_DATABASE_DRIVER_COMBO_BOX",  "D&atabase Driver:" },
		{ "CONNECTION_RDBMS_DATABASE_PLATFORM_FIELD",    "&Database Platform:" },
		{ "CONNECTION_RDBMS_DRIVER_CLASS_BROWSE_BUTTON", "&Browse..." },
		{ "CONNECTION_RDBMS_DRIVER_CLASS_COMBO_BOX",     "Driver &Class:" },
		{ "CONNECTION_RDBMS_DRIVER_URL_COMBO_BOX",       "&URL:" },
		{ "CONNECTION_RDBMS_LOOKUP_TYPE_FIELD",          "Loo&kup Type:"},
		{ "CONNECTION_RDBMS_LOOKUP_TYPE_STRING_CHOICE",  "String"},
		{ "CONNECTION_RDBMS_LOOKUP_TYPE_COMPOSITE_NAME_CHOICE", "Composite Name"},
		{ "CONNECTION_RDBMS_LOOKUP_TYPE_COMPOUND_NAME_CHOICE",  "Compound Name"},

		// EisConnectionPropertiesPane
		{ "CONNECTION_EIS_PLATFORM_FIELD",                   "P&latform:" },
		{ "CONNECTION_EIS_DRIVER_CLASS_BROWSE_BUTTON",       "&Browse..." },
		{ "CONNECTION_EIS_CONNECTION_FACTORY_URL_FIELD",     "Connection Factory &URL:" },
		{ "CONNECTION_EIS_CONNECTION_SPEC_CLASS_NAME_FIELD", "&Connection Specification Class:" },

		// ui.pool.basic.LoginPropertiesPropertiesPage
		{ "LOGIN_PROPERTY_ADD_BUTTON",          "&Add..." },
		{ "LOGIN_PROPERTY_EDIT_BUTTON",         "&Edit..." },
		{ "LOGIN_PROPERTY_EDITOR_ADD_TITLE",    "Add New Property" },
		{ "LOGIN_PROPERTY_EDITOR_EDIT_TITLE",   "Edit Property" },
		{ "LOGIN_PROPERTY_EDITOR_EMPTY_NAME",   "A name is required." },
		{ "LOGIN_PROPERTY_EDITOR_EMPTY_VALUE",  "A value is required." },
		{ "LOGIN_PROPERTY_EDITOR_NAME_FIELD",   "&Name:" },
		{ "LOGIN_PROPERTY_EDITOR_VALUE_FIELD",  "&Value:" },
		{ "LOGIN_PROPERTY_NAME_COLUMN",         "Name" },
		{ "LOGIN_PROPERTY_REMOVE_BUTTON",       "&Remove" },
		{ "LOGIN_PROPERTY_VALUE_COLUMN",        "Value" },
		{ "LOGIN_PROPERTY_EDITOR_EMPTY_NAME_EMPTY_VALUE",   "A name and a value are required." },
		{ "LOGIN_PROPERTY_EDITOR_INVALID_NAME",             "The name entered is already defined." },
		{ "LOGIN_PROPERTY_EDITOR_INVALID_NAME_EMPTY_VALUE", "The name entered is already defined and a value is required." },

		// ui.login.LoginExternalOptionsPane
		{ "CONNECTION_EXTERNAL_CONNECTION_POOLING_CHECK_BOX", "E&xternal Connection Pooling" },

		// ui.session.login.RdbmsConnectionPropertiesPage - Database Driver choices
		{ "CONNECTION_RDBMS_DATA_SOURCE_CHOICE",    "Managed Data Source" },
		{ "CONNECTION_RDBMS_DRIVER_MANAGER_CHOICE", "Driver Manager" },

		// ui.session.login.RdbmsSequencingPropertiesPage
		{ "LOGIN_COUNTER_FIELD_FIELD",                 "C&ounter Field:" },
		{ "LOGIN_CUSTOM_SEQUENCE_TABLE_RADIO_BUTTON",  "&Custom Sequence Table" },
		{ "LOGIN_DEFAULT_SEQUENCE_TABLE_RADIO_BUTTON", "&Default Sequence Table" },
		{ "LOGIN_NAME_FIELD_FIELD",                    "Nam&e Field:" },
		{ "LOGIN_NATIVE_SEQUENCING_RADIO_BUTTON",      "N&ative Sequencing" },
		{ "LOGIN_PREALLOCATION_SIZE_SPINNER",          "&Preallocation Size:" },
		{ "LOGIN_TABLE_NAME_FIELD",                    "Na&me:" },

		// ui.session.login.RdbmsOptionsPropertiesPage
		{ "LOGIN_ADVANCED_OPTIONS_TITLE",         "Advanced Options" },
		{ "LOGIN_BATCH_WRITING_COMBO_BOX",        "Batch Writin&g:" },
		{ "LOGIN_MAX_BATCH_WRITING_SIZE_SPINNER", "Max Si&ze:" },
		{ "LOGIN_BYTE_ARRAY_BINDING_CHECK_BOX" ,  "&Byte Array Binding" },
		{ "LOGIN_CACHE_ALL_STATEMENTS_CHECK_BOX", "&Cache All Statements" },
		{ "LOGIN_JDBC_OPTIONS_TITLE",             "JDBC Options" },
		{ "LOGIN_NATIVE_SQL_CHECK_BOX",           "Nati&ve SQL"},
		{ "LOGIN_STREAMS_FOR_BINDING_CHECK_BOX",  "Streams For Bin&ding" },
		{ "LOGIN_STRING_BINDING_CHECK_BOX",       "St&ring Binding" },
		{ "LOGIN_TABLE_QUALIFIER_FIELD",          "Table Q&ualifier:" },
		{ "LOGIN_TRIM_STRING_CHECK_BOX",          "Tri&m String" },
		{ "LOGIN_USE_PROPERTIES_CHECK_BOX",       "&Properties" },
		{ "LOGIN_FORCE_FIELD_NAMES_TO_UPPERCASE_CHECK_BOX",     "Force F&ield Names to Uppercase" },
		{ "LOGIN_OPTIMIZE_DATA_CONVERSION_CHECK_BOX",           "&Optimize Data Conversion" },
		{ "LOGIN_QUERIES_SHOULD_BIND_ALL_PARAMETERS_CHECK_BOX", "&Queries Should Bind All Parameters" },
		{ "OPTIONS_STRUCT_CONVERTER_LIST",		     "Struct Converters:" },
		{ "OPTIONS_STRUCT_CONVERTERS_ADD_BUTTON",    "&Add..." },
		{ "OPTIONS_STRUCT_CONVERTERS_REMOVE_BUTTON", "Re&move" }, 
		{ "JDBC_OPTIONS_PANE_PING_SQL_LABEL",        "Ping SQL:" },
		{ "JDBC_OPTIONS_PANE_QUERY_RETRY_ATTEMPTS_LABEL", "Query Retry Attempts:"},
		{ "JDBC_OPTIONS_PANE_DELAY_BETWEEN_CONNECTION_ATTEMPTS_LABEL", "Delay Between Connection Attempts(ms):"},
		{ "JDBC_OPTIONS_PANE_CONNECTION_HEALTH_VALIDATED_ON_ERROR_CHECK_BOX", "Connection Health Validated On Error"},
		{ "JDBC_OPTIONS_PANE_NATIVE_SEQUENCING_CHECK_BOX", "Native Sequencing"},

		// ui.session.login.RdbmsConnectionPropertiesPage
		{ "UNKNOWN_PLATFORM_CLASS", "<unkown platform: {0}>" },
		{ "CONNECTION_PANE_PROMPT_TO_REMOVE_CONNECTION_POOLS_MESSAGE", "A Session cannot be configured with internal connection pools and set to use external connection pooling.  This will cause data coruption at runtime.  Do you want to remove all the connection pools in this configuration?" },
		{ "CONNECTION_PANE_PROMPT_TO_REMOVE_CONNECTION_POOLS_TITLE", "Connection Pools" },

		// RdbmsOptionsPropertiesPage - Batch Writing choice
		{ "LOGIN_BATCH_WRITING_NONE_CHOICE",     "None" },
		{ "LOGIN_BATCH_WRITING_JDBC_CHOICE",     "JDBC" },
		{ "LOGIN_BATCH_WRITING_BUFFERED_CHOICE", "Buffered" },
		{ "LOGIN_BATCH_WRITING_NATIVE_CHOICE",   "Native(Oracle)"},	

		// NewSequenceDialog
		{ "NEW_SEQUENCE_DIALOG_DEFAULT_RADIO_BUTTON",       "&Default"},
		{ "NEW_SEQUENCE_DIALOG_DEFAULT_SEQUENCE_CHECK_BOX", "Default &Sequence"},
		{ "NEW_SEQUENCE_DIALOG_DESCRIPTION",                "Specify the name and the type of the sequence to create."},
		{ "NEW_SEQUENCE_DIALOG_NAME_LABEL",                 "&Name:"},
		{ "NEW_SEQUENCE_DIALOG_NATIVE_RADIO_BUTTON",        "N&ative"},
		{ "NEW_SEQUENCE_DIALOG_SEQUENCE_TYPE_GROUP_BOX",    "Type"},
		{ "NEW_SEQUENCE_DIALOG_TABLE_RADIO_BUTTON",         "&Table"},
		{ "NEW_SEQUENCE_DIALOG_TITLE",                      "Create New Sequence"},
		{ "NEW_SEQUENCE_DIALOG_UNARY_TABLE_RADIO_BUTTON",   "&Unary Table"},
		{ "NEW_SEQUENCE_DIALOG_XML_FILE_RADIO_BUTTON",      "&XML File"},

		// SequencePane
		{"SEQUENCE_PANE_PREALLOCATION_SIZE_LABEL", "Preallocation Size:"},

		// SequencingPane
		{"SEQUENCING_PANE_ADD_BUTTON",                             "Add New Sequence"},
		{"SEQUENCING_PANE_DEFAULT_SEQUENCE_TEXT",                  "(Default Sequence)"},
		{"SEQUENCING_PANE_DESCRIPTION_1",                          "You configure TopLink {0} at the session or project level to tell TopLink how to obtain sequence values: that is, what type of sequences to use."},
		{"SEQUENCING_PANE_DESCRIPTION_2",                          "sequencing"},
		{"SEQUENCING_PANE_EDIT_BUTTON",                            "Rename"},
		{"SEQUENCING_PANE_EDIT_SEQUENCE_DIALOG_DESCRIPTION_TITLE", "Rename Sequence"},
		{"SEQUENCING_PANE_EDIT_SEQUENCE_DIALOG_DESCRIPTION",       "Specify a new name for the sequence."},
		{"SEQUENCING_PANE_EDIT_SEQUENCE_DIALOG_LABEL",             "&Name:"},
		{"SEQUENCING_PANE_EDIT_SEQUENCE_DIALOG_TITLE",             "Rename Sequence"},
		{"SEQUENCING_PANE_REMOVE_BUTTON",                          "Remove"},
		{"SEQUENCING_PANE_SEQUENCE_NO_NAME",                       "<name not set ({0})>"},
		{"SEQUENCING_PANE_SEQUENCE_TYPE_DEFAULT",                  "Default"},
		{"SEQUENCING_PANE_SEQUENCE_TYPE_NATIVE",                   "Native"},
		{"SEQUENCING_PANE_SEQUENCE_TYPE_TABLE",                    "Table"},
		{"SEQUENCING_PANE_SEQUENCE_TYPE_UNARY_TABLE",              "Unary Table"},
		{"SEQUENCING_PANE_SEQUENCE_TYPE_XML_FILE",                 "XML File"},

		// TableSequencePane
		{"TABLE_SEQUENCE_PANE_COUNTER_FIELD_LABEL", "Counter Field:"},
		{"TABLE_SEQUENCE_PANE_NAME_FIELD_LABEL",    "Name Field:"},
		{"TABLE_SEQUENCE_PANE_TABLE_LABEL",         "Table:"},

		// UnaryTableSequencePane
		{"UNARY_TABLE_SEQUENCE_PANE_COUNTER_FIELD_LABEL", "Counter Field:"},

		// Session pages
		{ "SESSION_CLUSTERING_TAB_TITLE",        "Cache Coordination" },
		{ "SESSION_GENERAL_TAB_TITLE",           "General" },
		{ "SESSION_LOGGING_TAB_TITLE",           "Logging" },
		{ "SESSION_LOGIN_TAB_TITLE",             "Login" },
		{ "SESSION_MULTIPLE_PROJECTS_TAB_TITLE", "Multiple Projects" },
		{ "SESSION_OPTIONS_TAB_TITLE",           "Options" },
		{ "SESSION_PROJECT_TAB_TITLE",           "Project" },
		{ "SESSION_SERVER_PLATFORM_TAB_TITLE",   "Server Platform" },
		{ "SESSION_SESSIONS_TAB_TITLE",          "Sessions" },
		{ "SESSION_CONNECTION_POLICY_TITLE",     "Connection Policy" },

		{ "SESSION_SERVER_PLATFORM_TAB_MESSAGE", "The Server Platform settings must be configured through the session broker when this session is managed." },
		{ "SESSION_LOGGING_TAB_MESSAGE",         "The Logging settings must be configured through the session broker when this session is managed." },
		{ "SESSION_CLUSTERING_TAB_MESSAGE",      "The Cache Coordination settings must be configured through the session broker when this session is managed." },
		{ "SESSION_OPTIONS_TAB_MESSAGE",         "The Options settings must be configured through the session broker when this session is managed." },

		// LoggingOptionsPane
		{ "LOGGING_OPTIONS_CHECK_BOX",                          "&Options" },
		{ "LOGGING_OPTIONS_LOG_EXCEPTION_STACK_TRACE_CHECKBOX", "Log E&xception Stack Trace" },
		{ "LOGGING_OPTIONS_PRINT_CONNECTION_CHECKBOX",          "Print &Connection" },
		{ "LOGGING_OPTIONS_PRINT_DATE_CHECKBOX",                "Print &Date" },
		{ "LOGGING_OPTIONS_PRINT_SESSION_CHECKBOX",             "P&rint Session" },
		{ "LOGGING_OPTIONS_PRINT_THREAD_CHECKBOX",              "Pri&nt Thread" },

		// ui.project.BrokerCreationDialog
		// ui.project.SessionCreationDialog
		{ "CustomServerPlatform",   "Custom" },
		{ "NoServerPlatform",       ""}, // Needs to be empty
		{ "Oc4jPlatform",			"OC4J" },
		{ "Oc4j_11_1_1_Platform",	"OC4J 11.1.1" },
		{ "Oc4j_10_1_2_Platform",   "OC4J 10.1.2" },
		{ "Oc4j_10_1_3_Platform",   "OC4J 10.1.3" },
		{ "Oc4j_9_0_3_Platform",    "OC4J 9.0.3" },
		{ "Oc4j_9_0_4_Platform",    "OC4J 9.0.4" },
		{ "WebLogic_10_Platform", 	"WebLogic 10.0"	},
		{ "WebLogic_9_Platform",	"WebLogic 9.0"},
		{ "WebLogic_8_1_Platform",  "WebLogic 8.1" },
		{ "WebLogic_7_0_Platform",  "WebLogic 7.0" },
		{ "WebLogic_6_1_Platform",  "WebLogic 6.1" },
		{ "WebSphere_4_0_Platform", "WebSphere 4.0" },
		{ "WebSphere_5_0_Platform", "WebSphere 5.0" },
		{ "WebSphere_5_1_Platform", "WebSphere 5.1" },
		{ "WebSphere_6_0_Platform", "WebSphere 6.0" },
		{ "WebSphere_6_1_Platform", "WebSphere 6.1" },
		{ "WebSphere_7_Platform", 	"WebSphere 7.0" },
		{ "JBossPlatform", 			"JBoss" },
		{ "SunAS9Platform", 		"SunAS 9.0" },
		{ "SunAS9ServerPlatform", 	"SunAS 9.0" },

		// ui.session.general.AbstractSessionProjectPane
		{ "SESSION_PROJECT_PRIMARY_PROJECT_FIELD",        "&Primary Project:" },
		{ "SESSION_PROJECT_USE_MULTIPLE_PROJECTS_BUTTON", "&Multiple Projects" },

		// ui.session.general.SessionProjectPane
		{ "SESSION_PROJECT_EDIT_BUTTON", "E&dit..." },

		// ui.session.general.XmlSessionProjectPane
		{ "SESSION_PROJECT_BROWSE_BUTTON", "&Browse..." },

		// ui.session.general.ProjectTypeEditDialog
		{ "PROJECT_TYPE_EDIT_DIALOG_CLASS_BROWSE_BUTTON", "&Browse..." },
		{ "PROJECT_TYPE_EDIT_DIALOG_CLASS_FIELD",         "&Name:" },
		{ "PROJECT_TYPE_EDIT_DIALOG_CLASS_RADIO_BUTTON",  "&Class" },
		{ "PROJECT_TYPE_EDIT_DIALOG_TITLE_ADD",           "Add Additional Project" },
		{ "PROJECT_TYPE_EDIT_DIALOG_TITLE_EDIT",          "Edit Primary Project" },
		{ "PROJECT_TYPE_EDIT_DIALOG_XML_BROWSE_BUTTON",   "&Browse..." },
		{ "PROJECT_TYPE_EDIT_DIALOG_XML_FIELD",           "&Location:" },
		{ "PROJECT_TYPE_EDIT_DIALOG_XML_RADIO_BUTTON",    "&Xml" },

		// ui.session.basic
		{ "PROJECT_TYPE_EDIT_DIALOG_ERROR_MESSAGE", "The XML location is not valid." },

		// ui.session.general.AbstractSessionMultipleProjectsPane
		{ "SESSION_MULTIPLE_PROJECTS_ADD_BUTTON",    "A&dd..." },
		{ "SESSION_MULTIPLE_PROJECTS_LIST",          "&Projects:"},
		{ "SESSION_MULTIPLE_PROJECTS_REMOVE_BUTTON", "&Remove" },

		// ui.session.general.DatabaseProjectAdvancedPane
		{ "SESSION_PROJECT_ADVANCED_FILE_CHOOSER_DESCRIPTION", "XML Files (*.xml)" },

		// SessionLoggingPropertiesPage
		{ "SESSION_LOGGING_BROWSE_BUTTON", "&Browse..." },

		// Login panels
		{ "LOGIN_CONNECTION_TAB_TITLE",       "Connection" },
		{ "LOGIN_PROPERTIES_TAB_TITLE",       "Properties" },
		{ "LOGIN_SEQUENCING_TAB_TITLE", "Sequencing" },
		{ "LOGIN_OPTIONS_TAB_TITLE",    	"Options" },
		{ "LOGIN_DATASOURCE_PLATFORM",		"&Datasource Platform Class:" },
		{ "LOGIN_EQUAL_NAMESPACE_RESOLVERS",	"&Equal Namespace Resolvers" },
		{ "LOGIN_DOCUMENT_PRESERVATION_POLICY",		"Document &Preservation Policy:" },
		{ "LOGIN_NODE_ORDERING_POLICY",		"&Login Node Ordering Policy:"},
		
		// ui.session.clustering.CacheSynchronizationManagerPane
		{ "CSM_ASYNCHRONOUS_CHECK_BOX",               "&Asynchronous" },
		{ "CSM_CLUSTERING_SERVICE_COMBO_BOX",         "Clustering Ser&vice:" },
		{ "CSM_REMOVE_CONNECTION_ON_ERROR_CHECK_BOX", "&Remove Connection On Error" },
		{ "CSM_JMS_CLUSTERING_CHOICE",                "JMS" },
		{ "CSM_RMI_CLUSTERING_CHOICE",                "RMI" },
		{ "CSM_RMI_JNDI_CLUSTERING_CHOICE",           "RMI JNDI" },
		{ "CSM_RMI_IIOP_JNDI_CLUSTERING_CHOICE",      "RMI/IIOP JNDI" },
		{ "CSM_SUN_CORBA_JNDI_CLUSTERING_CHOICE",     "CORBA JNDI" },

		// ui.session.clustering.JMSClusteringPane
		{ "JMS_CLUSTERING_TOPIC_NAME_FIELD",                    "T&opic Name:" },
		{ "JMS_CLUSTERING_TOPIC_CONNECTION_FACTORY_NAME_FIELD", "Topic Connection Factor&y:" },

		// ui.session.clustering.JNDIAndNamingServicePane
		{ "CSM_JNDI_USERNAME",                               "JNDI User Na&me:" },
		{ "CSM_JNDI_PASSWORD",                               "JNDI &Password:" },
		{ "CSM_NAMING_SERVICE_INITIAL_CONTEXT_FACTORY_NAME", "Naming Service Initial Conte&xt Factory:" },
		{ "CSM_NAMING_SERVICE_URL",                          "Naming Service &URL" },

		// ui.session.clustering.JNDINamingServerPane
		{ "JNDI_INITIAL_CONTEXT_FACTORY_BROWSE_BUTTON", "&Browse..." },
		{ "JNDI_INITIAL_CONTEXT_FACTORY_FIELD",         "Initial Conte&xt Factory:" },
		{ "JNDI_NAMING_SERVICE_TITLE",                  "JNDI Naming Service" },
		{ "JNDI_PASSWORD_FIELD",                        "&Password:" },
		{ "JNDI_SHOW_PROPERTIES_BUTTON",                "Properties&..." },
		{ "JNDI_SHOW_PROPERTIES_TITLE",                 "Edit Properties" },
		{ "JNDI_URL_FIELD",                             "&URL:" },
		{ "JNDI_USER_NAME_FIELD",                       "User Na&me:" },

		// ui.session.clustering.RCMJMSPane
		{ "RMI_JMS_TOPIC_CONNECTION_FACTORY_NAME_FIELD",      "Topi&c Connection Factory Name:" },
		{ "RMI_JMS_TOPIC_NAME_FIELD",                         "Top&ic Name:" },
		{ "RMI_JMS_TOPIC_HOST_URL_FIELD",                         "Topic &Host URL:" },
		{ "JNDI_URL_FIELD_OPTIONAL",                          "&URL:" },
		{ "JNDI_USER_NAME_FIELD_OPTIONAL",                    "User Na&me:" },
		{ "JNDI_PASSWORD_FIELD_OPTIONAL",                     "&Password:" },
		{ "JNDI_INITIAL_CONTEXT_FACTORY_NAME_FIELD_OPTIONAL", "Initial Conte&xt Factory:" },

		// ui.session.clustering.RCMRMIPane
		{ "RMI_MULTICAST_GROUP_ADDRESS_FIELD",        "Multicast Group &Address:" },
		{ "RMI_MULTICAST_PORT_SPINNER",               "Multicast P&ort:" },
		{ "RMI_ANNOUNCEMENT_DELAY_SPINNER",           "Announcement &Delay:" },
		{ "RMI_JNDI_NAMING_SERVICE_RADIO_BUTTON",     "&JNDI Naming Service" },
		{ "RMI_PACKET_TIME_TO_LIVE_SPINNER",          "Pac&ket Time to Live:" },
		{ "RMI_REGISTRY_NAMING_SERVICE_RADIO_BUTTON", "Registry Naming Ser&vice" },
		{ "RMI_REGISTRY_NAMING_SERVICE_URL_FIELD",    "&URL:" },
		{ "RMI_SYNCHRONOUS_CHECK_BOX",                "Syn&chronous" },
		
		// ui.sessions.clustering.RCMOc4jJGroupsPane
		{ "OC4J_USE_SINGLE_THREADED_CHECK_BOX", 		"Use Single Thread &Notification" },
		{ "OC4J_TOPIC_NAME_FIELD",						"T&opic Name:" },

		// ui.session.clustering.RemoteCommandManagerPane - Type choices
		{ "JMS",          "JMS" },
		{ "RMI",          "RMI" },
		{ "RMI/IIOP",     "RMI/IIOP" },
		{ "CORBA",        "CORBA" },
		{ "OC4J",		  "OC4J/JGroups" },
		{ "USER_DEFINED", "User Defined" },

		// ui.session.clustering.RCMUserDefinedPane
		{ "RCM_USER_DEFINED_TRANSPORT_CLASS_FIELD",  "Trans&port Class:" },
		{ "RCM_USER_DEFINED_BROWSE_BUTTON",          "&Browse..." },

		// ui.session.clustering.SessionClusteringPropertiesPage
		{ "CLUSTERING_CLUSTERING_CHECK_BOX",                 "Enable C&ache Coordination" },
		{ "CLUSTERING_CLUSTERING_COMBO_BOX",                 "Cache C&oordination:" },
		{ "CLUSTERING_TYPE_COMBO_BOX",                       "T&ype:" },
		{ "CLUSTERING_CHANNEL_FIELD",                        "Channe&l:" },
		{ "CLUSTERING_REMOVE_CONNECTION_ON_ERROR_CHECK_BOX", "&Remove Connection On Error" },

		// ui.session.clustering.SessionClusteringPropertiesPage - Clustering choices
		{ "CACHE_SYNCHRONIZATION", "Cache Synchronization" },
		{ "REMOTE_COMMAND",        "Remote Command" },
		{ "DEFAULT_CLUSTERING_TYPE", "Default(RMI)"},

		// ui.session.general.EventListenersPane
		{ "OPTIONS_EVENT_LISTENERS_ADD_BUTTON",    "&Add..." },
		{ "OPTIONS_EVENT_LISTENERS_LIST",          "Event Listeners" },
		{ "OPTIONS_EVENT_LISTENERS_REMOVE_BUTTON", "Re&move" },

		// ui.session.general.SessionOptionsPropertiesPage
		{ "OPTIONS_PROFILER_COMBO_BOX",                            "&Profiler:" },
		{ "OPTIONS_EXCEPTION_HANDLER_BROWSE_BUTTON",               "&Browse..." },
		{ "OPTIONS_EXCEPTION_HANDLER_FIELD",                       "E&xception Handler:" },
		{ "OPTIONS_SESSION_CUSTOMIZER_CLASS_BROWSE_BUTTON",        "B&rowse..." },
		{ "OPTIONS_SESSION_CUSTOMIZER_CLASS_FIELD",                "Session &Customizer Class:" },

		// ui.session.general.SessionOptionsPropertiesPage - Profiler choices
		{ "OPTIONS_PROFILER_DMS_CHOICE",         "DMS" },
		{ "OPTIONS_PROFILER_NO_PROFILER_CHOICE", "No Profiler" },
		{ "OPTIONS_PROFILER_TOPLINK_CHOICE",     "Standard" },
		
		// Connection Pool
		{ "DELETE_CONNECTION_POOL_WARNING",                     "Are you sure you want to delete ''{0}''?" },
		{ "DELETE_CONNECTION_POOL_WARNING_TITLE",               "Delete Connection Pool" },
		{ "NAMED_CONNECTION_POOL_CREATION_DIALOG_INVALID_NAME", "The name entered is already defined." },
		{ "NAMED_CONNECTION_POOL_CREATION_DIALOG_TITLE",        "Create New Named Connection Pool" },
		{ "POOL_CREATION_DIALOG_NEW_POOL_NAME",                 "ConnectionPool" },
		{ "POOL_NAME_LABEL",                                    "&Name:" },
		{ "EXTERNAL_CONNECTION_POOLING_ENABLED_WARNING_TITLE", 	"External Connection Pooling" },
		{ "EXTERNAL_CONNECTION_POOLING_ENABLED_WARNING_MESSAGE","External Connection Pooling is enabled on this session configuration, this must be turned off to add a connection pool." },

		// PoolCellRendererAdapter
		{ "CONNECTION_POOL",          "Named Connection Pool: {0}" },
		{ "CONNECTION_POOL_READ",     "Read Connection Pool: {0}" },
		{ "CONNECTION_POOL_SEQUENCE", "Sequence Connection Pool: {0}" },
		{ "CONNECTION_POOL_WRITE",    "Write Connection Pool: {0}" },

		// Connection Pool pages
		{ "POOL_GENERAL_TAB_TITLE", "&General"},
		{ "POOL_LOGIN_TAB_TITLE",   "&Login"},

		// ui.preferences
		{ "PREFERENCES.SC",           "Sessions Configuration"},
		{ "PREFERENCES.SC.NEW_NAMES", "New Names" },
		{ "PREFERENCES.SC.PLATFORM",  "Platform" },

		// ui.preferences.SCPreferencesPage
		{ "PREFERENCES_DEFAULT_CLASSPATH_GROUP_BOX",                  "Default Classpath for New Sessions Configuration" },
		{ "PREFERENCES_DEFAULT_DATA_SOURCE_TYPE_GROUP_BOX",           "Default Data Source Type" },
		{ "PREFERENCES_DEFAULT_NEW_NAME_BROKER_FIELD",                "&Broker:" },
		{ "PREFERENCES_DEFAULT_NEW_NAME_GROUP_BOX",                   "Default New Names" },
		{ "PREFERENCES_DEFAULT_NEW_NAME_POOL_FIELD",                  "&Connection Pool:" },
		{ "PREFERENCES_DEFAULT_NEW_NAME_SESSION_FIELD",               "S&ession:" },
		{ "PREFERENCES_DEFAULT_NEW_NAME_TOPLINK_CONFIGURATION_FIELD", "&Sessions Configuration:" },
		{ "PREFERENCES_DEFAULT_SERVER_PLATFORM_COMBO_BOX",            "&Default Platform:" },

		// ui.session.basic.SessionConnectionPolicyPropertiesPage
		{ "SESSION_CONNECTION_POLICY_ACQUIRE_EXCLUSIVE_CONNECTION_CHECKBOX", "&Acquire Exclusive Connection"},
		{ "SESSION_CONNECTION_POLICY_ACQUIRE_CONNECTIONS_LAZILY_CHECKBOX",   "Acquire &Connections Lazily"},

		{"RDBMS_POOL_LOGIN_PANE_EXTERNAL_CONNECTION_POOLING_CHECKBOX",     "E&xternal Connection Pooling"},
		{"RDBMS_POOL_LOGIN_PANE_EXTERNAL_TRANSACTION_CONTROLLER_CHECKBOX", "External Transaction Controller (&JTA)"},

		// Help Action
		{ "HELP",         "&Help" },
		{ "HELP.tooltip", "Help for the selected item(s)" },
	};

	protected Object[][] getContents() {
		return contents;
	}
}
