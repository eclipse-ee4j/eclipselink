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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.util.ListResourceBundle;


public class UiProjectBundle extends ListResourceBundle {

	
	/** 
	 * The contents of the resource bundles
	 */
	static final Object[][] contents = {

		{"GENERAL_TAB_TITLE", 		"General"},
		{"SEQUENCING_TAB_TITLE", 	"Sequencing"},
		{"DEFAULTS_TAB_TITLE", 		"Defaults"},
		{"OPTIONS_TAB_TITLE", 		"Options"},
		{"CONNECTION_TAB_TITLE",	"Connection Specification"},
		
		// EIS Login Spec
		{"LOGIN_CONNECTION_TAB_TITLE", "Connection"},
		{"CONNECTION_EIS_PLATFORM_FIELD", "EIS &Platform:"},
		{"CONNECTION_EIS_CONNECTION_FACTORY_URL_FIELD", "Connection Factory &URL:"},
		{"CONNECTION_USER_NAME_FIELD", "User Na&me (Optional):"},
		{"CONNECTION_PASSWORD_FIELD", "P&assword (Optional):"},
		{ "SAVE_PASSWORD_CHECK_BOX",	"&Save Password" },
		{"CONNECTION_EIS_DRIVER_CLASS_BROWSE_BUTTON", "&Browse..."},
		{"CONNECTION_EIS_CONNECTION_SPEC_CLASS_NAME_FIELD", "Co&nnection Specification Class:"},
		{"CONNECTION_EXTERNAL_CONNECTION_POOLING_CHECK_BOX", "E&xternal Connection Pooling"},
		
		// EIS Login Properties
		{"LOGIN_PROPERTIES_TAB_TITLE", 			"Properties"},
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

		//Accessibility
		{"ACCESSIBLE_EIS_PROJECT_NODE",        "EIS Project {0}"},
		{"ACCESSIBLE_RELATIONAL_PROJECT_NODE", "Relational Project {0}"},
		{"ACCESSIBLE_OX_PROJECT_NODE",         "XML Project {0}"},

		//MWProjectNode actions
		{"NEED_EMPTY_DIRECTORY_TO_SAVE_DIALOG.message", "To preserve project identity, save your project in an empty directory."},
		{"NEED_EMPTY_DIRECTORY_TO_SAVE_DIALOG.title",   "Project Save Directory"},
		{"PROJECT_SAVED_IN_NEW_FILE_FORMAT_DIALOG",     "This project must be saved in the new file format.  You will be prompted to choose a save location."},
		{"WRITE_PROJECT_EJB_JAR_ON_SAVE_DIALOG",        "Would you like to write the ejb-jar.xml file?"},
		{"WRITE_PROJECT_EJB_JAR_ON_SAVE_DIALOG.title",  "Write ejb-jar.xml File"},
		{"PROJECT_NODE_DISPLAY_STRING_READ_ONLY",       "{0} (Read-Only)"},
					
		// ProjectNode - Dialogs shown during save
		{"SAVE_AS_DIALOG_TITLE",           "Save As"},
		{"SAVE_AS_DIALOG_CANT_SAVE",       "Cannot save the file ''{0}''. The file exists and is marked Read-Only. {1}Save the file with another file name or to another location."},
		{"SAVE_AS_DIALOG_REPLACE",         "The file {0} already exists. Do you want to replace the existing file?"},
		{"SAVE_AS_DIALOG_ALREADY_OPENED",  "{0} cannot give a document the same name as an open document.{2}Type a different name for the document you want to save.{2}({1})"},
		{"SAVE_ERROR_MESSAGE",             "Cannot save ''{0}'' due to an error. Refer to the following message:"},
		{"SAVE_READ_ONLY_ERROR_MESSAGE",   "{0} cannot complete the save due to a file permission error.{2}({1})"},
		{"SAVE_RETRY_TO_SAVE_MESSAGE",     "Do you want to retry to save?"},
		{"SAVE_AS_DIALOG_MWP_FILE_FILTER", "EclipseLink Workbench Project (*.mwp)"},

			//DeleteProjectAction
			{"DELETE_PROJECT_ACTION", "&Delete Project..."},
			{"DELETE_PROJECT_ACTION.toolTipText", "Delete a Project"},
			{"PROJECT_CURRENTLY_LOADED_DIALOG.title" ,"Project Currently Loaded"},
			{"PROJECT_CURRENTLY_LOADED_DIALOG.message" ,"A project named \"{0}\" is currently loaded in the EclipseLink Workbench. Please close this project before deletion."},
			{"DELETE_PROJECT_FILE_CHOOSER.title", "Delete a Project and Associated Resources"},
			{"DELETE_PROJECT_FILE_CHOOSER.buttonText", "&Delete"},
			{"UNABLE_TO_DELETE_PROJECT_DIALOG.title", "Unable to Delete Project"},
			{"UNABLE_TO_DELETE_PROJECT_DIALOG.message", "More than one project file exists in the directory.  Unable to delete {0}"},
			{"DELETE_PROJECT_WARNING.title", "Delete Project and Associated Files"},
			{"DELETE_PROJECT_WARNING.message", "This will delete the project and associated files from the file system.  {0}Are you sure you want to delete the selected project permanently?"},
			{"PROJECT_FILE_DOES_NOT_EXIST.message","No project file exists at this location.  {0}This is probably because the project was never saved. {0}The project will be closed."},				
			//{"deletingALargeProjectMayTake.message", "Deleting a large project may take a few moments. Please wait..."},
	
			//Export Menu		
			{"EXPORT_MENU", "&Export"},

			// ReadOnlyFileDialog
			{"versionControlAssistance.title", "Version Control Assistance"},
			{"versionControlAssistance.message", "The following files are marked read-only.  Please check-out or unlock these files from your version control system to continue.  Press the Save button once the files have been unlocked."},
			{"readOnlyFiles", "Read-only files:"},
			{"saveAs", "Save &As..."},
			{"save", "&Save"},

			// UpdateProjectFromEjbJarXmlAction
			{"PROJECT_EJB_UPDATE_STATUS_DIALOG_TITLE",   "Update Status"},
			{"PROJECT_EJB_UPDATE_STATUS_DIALOG_MESSAGE", "&Status of the project update:"},
			{"PROJECT_EJB_WRITE_STATUS_DIALOG_TITLE",    "Write Status"},
			{"PROJECT_EJB_WRITE_STATUS_DIALOG_MESSAGE",  "&Status of the persistence:"},
			{"PROJECT_UPDATE_STATUS_DIALOG_MESSAGE",     "The project contains the following errors. Continue anyway?"},
			{"PROJECT_UPDATE_STATUS_DIALOG_NO_BUTTON",   "   &No   "},
			{"PROJECT_UPDATE_STATUS_DIALOG_YES_BUTTON",  "   &Yes   "},
			{"PROJECT_UPDATE_SUCCESSFUL",                "Project updated"},
			{"PROJECT_UPDATE_WITH_ERRORS",               "Project updated with incomplete information"},

			// WriteProjectToEjbJarXmlAction
			{"EJB_JAR_XML_VALIDATOR_CHOOSE_EJB_JAR_XML_TITLE",       "Location of ejb-jar.xml"},
			{"EJB_JAR_XML_VALIDATOR_CHOOSE_NEW_EJB_JAR_XML_MESSAGE", "{0} Would you like to choose a location?"},
			{"EJB_JAR_XML_VALIDATOR_CREATE_EJB_JAR_XML_MESSAGE",     "The file \"{0}\" for the project \"{1}\" does not exist. Would you like to create it?"},
			{"EJB_JAR_XML_VALIDATOR_FILE_IS_NOT_READABLE",           "The ejb-jar.xml file \"{0}\" for the project \"{1}\" could not be read."},
			{"EJB_JAR_XML_VALIDATOR_FILE_IS_NOT_WRITABLE",           "The ejb-jar.xml file \"{0}\" for the project \"{1}\" is not writable."},
			{"EJB_JAR_XML_VALIDATOR_INVALID_FILE_CHOSEN",            "The ejb-jar.xml location for the project \"{0}\" is invalid. It must either be an .xml or a .jar file."},
			{"EJB_JAR_XML_VALIDATOR_JAR_DESCRIPTION",                "Jar Files (*.jar)"},
			{"EJB_JAR_XML_VALIDATOR_NO_LOCATION_CHOOSEN",            "The ejb-jar.xml location has not been specified for the project \"{0}\"."},
			{"EJB_JAR_XML_VALIDATOR_OVERWRITE_FILE_MESSAGE",         "{0} Do you want to replace the existing file?"},
			{"EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_MESSAGE",          "The project contains the following errors. Continue anyway?"},
			{"EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_NO_BUTTON",        "   &No   "},
			{"EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_YES_BUTTON",       "   &Yes   "},
			{"EJB_JAR_XML_VALIDATOR_UPDATE_OVERWRITE_MESSAGE",       "The project \"{0}\" is out of synch with the ejb-jar.xml file. Would you like to update or overwrite the file?"},
			{"EJB_JAR_XML_VALIDATOR_UPDATE_SUCCESSFUL",              "{0} has been written successfully"},
			{"EJB_JAR_XML_VALIDATOR_XML_DESCRIPTION",                "XML Files (*.xml)"},
			{"EJB_JAR_XML_VALIDATOR_UPDATE_BUTTON",                  "&Update"},
			{"EJB_JAR_XML_VALIDATOR_OVERWRITE_BUTTON",               "&Overwrite"},

			// ProjectGeneralPropertiesPage
		{"SAVE_LOCATION_TEXT_FIELD_LABEL", "&Project Save Location:"},

		// PersistenceTypePanel
		{"PROJECT_PERSISTENCE_TYPE_EJB_JAR_XML_LOCATION_FIELD", "Location of ejb-jar.&xml:"},
		{"PROJECT_PERSISTENCE_TYPE_BMP_CHECK_BOX",              "B&MP"},
		{"PROJECT_PERSISTENCE_TYPE_BROWSE_BUTTON",              "Bro&wse..."},
		{"PROJECT_PERSISTENCE_TYPE_CMP_1_1_CHECK_BOX",          "CMP &1.1"},
		{"PROJECT_PERSISTENCE_TYPE_CMP_2_x_CHECK_BOX",          "CMP &2.x"},
		{"PROJECT_PERSISTENCE_TYPE_JAVA_OBJECTS_CHECK_BOX",     "&Java Objects"},
		{"PROJECT_PERSISTENCE_TYPE_REMOVE_EJB_INFO_TITLE",      "Remove EJB Information"},
		{"PROJECT_PERSISTENCE_TYPE_REMOVE_EJB_INFO_MESSAGE",    "Are you sure you want to change all descriptors in the project to normal descriptors? You will lose all EJB-specific information."},
		{"PROJECT_PERSISTENCE_TYPE_REMOVE_EJB_2X_INFO_TITLE",   "Remove EJB 2.x Information"},
		{"PROJECT_PERSISTENCE_TYPE_REMOVE_EJB_2X_INFO_MESSAGE", "You are changing the persistence type of the project. This will remove all EJB 2.x CMP information from the descriptors. Are you sure you want to do this?"},
		{"PROJECT_PERSISTENCE_TYPE_TITLE",                      "Persistence Type"},

		// Ejb-jar.xml validation
		{"CONCRETE_INSTANCE_VARIABLE_EXISTS_ERROR",       "There was a concrete instance variable named \"{0}\" already defined on the class \"{1}\".  Please either 1) remove this instance variable from the class, refresh in the MW, and update from the ejb-jar.xml again, or 2) remove the field from the ejb-jar.xml file and update again."},
		{"EJB_2X_ATTRIBUTE_DOES_NOT_EXIST_ERROR",         "A mapping for the field named \"{0}\" could not be created on the descriptor \"{1}\". Check to see that there are abstract getters and setters accessible for this field."},
		{"EJB_CLASS_NOT_FOUND_ERROR",                     "The class \"{0}\" could not be loaded because it (or a class it references) was not on the classpath."},
		{"EJB_JAR_XML_EXCEPTION_ERROR",                   "An exception occurred while attempting to read the ejb-jar.xml file. Check your file to ensure that it is well-formed, valid, and complies to the EJB 2.x DTD or schema.{0}{1}"},
		{"EJB_JAR_FOR_PROJECT_NOT_SPECIFIED_ERROR",       "The ejb-jar.xml location has not been specified for the project \"{0}\"."},
		{"EJB_JAR_FOR_PROJECT_SPECIFIED_INCORRECTLY_ERROR", "The ejb-jar.xml location is relative yet the project save location is not specified."},
		{"EJB_JAR_XML_PROJECT_NOT_UPDATED_ERROR",         "The projects could not be updated.{0}{1}"},
		{"NEITHER_HOME_NOR_REMOTE_INTERFACE_FOUND_ERROR", "Neither local home nor remote home interface for the class \"{0}\" was found on the classpath. The finders for this class were not updated."},
		{"NO_PERSISTENCE_TYPE_SPECIFIED_ERROR",           "EclipseLink Workbench could not determine a project persistence type from the xml file."},
		{"NO_SINGLE_PERSISTENCE_TYPE_ERROR",              "EclipseLink Workbench requires that all entities in the xml file for this project have the same persistence-type and/or cmp-version. The project will be set according to the persistence-type and/or cmp-version of the first entity in the file."},
		{"SELECTOR_NOT_DEFINED_IN_BEAN_CLASS_ERROR",      "Could not find a corresponding ejbSelect method in the bean class: \"{0}\".  No information was updated for this query."},
		{"FINDER_DOES_NOT_EXIST_ON_REMOTE_HOME_AND_LOCAL_HOME_ERROR", "The finder method named \"{0}\" was not defined on a home interface for the entity \"{1}\". The finder was not updated."},
		{"INVALID_DOC_TYPE_ERROR",                        "The doctype for the specified file is not supported. The Workbench only supports the EJB 2.x DTD, or doctype \"{0}\"."},
		{"ERROR_WHILE_WRITING_EJB_JAR_XML",					"An error occured while writing project to ejb-jar.xml.  Message:  {0}"},

		// Ejb-jar.xml update
		{"EJB_DESCRIPTOR_MUST_HAVE_EJB_NAME_WARNING",             "The EJB descriptor, \"{0}\", must have an EJB name specified."},
		{"EJB_DESCRIPTOR_MUST_HAVE_PRIMARY_KEY_CLASS_WARNING",    "The EJB descriptor, \"{0}\", must have a primary key class specified."},
		{"EJB_NAME_MUST_BE_UNIQUE_WARNING",                       "The EJB name, \"{0}\", was used by more than one descriptor. EJB names must be unique."},
		{"EMPTY_TEXT_ATTRIBUTE_WARNING",                          "The element \"{1}\" cannot have an empty text attribute \"{0}\""},
		{"MULTIPLE_ENTITIES_FOUND_FOR_EJB_NAME_WARNING",          "There were multiple entities with the ejb-name \"{0}\" in the XML file."},
		{"NON_VALID_EJB_NAME_FOR_RELATIONSHIP_ROLE_WARNING",      "The ejb-name \"{0}\" in the ejb-relationship-role \"{1}\" is not found in any entity of the XML file"},
		{"NON_VALID_CMP_VERSION_WARNING",                         "The entity \"{0}\" did not have a valid cmp-version. The version must be \"1.x\" or \"2.x\"."},
		{"NON_VALID_MULTIPLICITY_WARNING",                        "There was an invalid multiplicity in a relationship involving the mapping \"{0}\" in the descriptor \"{1}\". The multiplicity must be \"One\" or \"Many\"."},
		{"NON_VALID_PERSISTENCE_TYPE_WARNING",                    "The entity \"{0}\" did not have a valid persistence-type. The type must be \"Bean\" or \"Container\"."},
		{"NON_VALID_QUERY_METHOD_NAME_WARNING",                   "The query method-name \"{0}\" does not start with \"find\" or \"ejbSelect\". No information was updated for this query."},
		{"NOT_SINGLE_PERSISTENCE_TYPE_WARNING",                   "EclipseLink Workbench requires that all entities in the xml file for this project have the same persistence-type and/or cmp-version. The project will be set according to the persistence-type and/or cmp-version of the first entity in the file."},
		{"PROJECT_MUST_HAVE_AT_LEAST_ONE_EJB_DESCRIPTOR_WARNING", "The project, \"{0}\", must have at least one EJB descriptor."},
		{"REQUIRED_ATTRIBUTE_DOES_NOT_EXIST_WARNING",             "There is an element, \"{1}\", that does not have a required attribute, \"{0}\"."},

		//RelationalProjectSequencingPropertiesPage
		{"SEQUENCING_PREALLOCATION_SIZE_SPINNER_LABEL", "&Preallocation Size:"},
		{"DEFAULT_SEQUENCING_RADIO_BUTTON_TEXT", "&Default Sequence Table"},
		{"NATIVE_SEQUENCING_RADIO_BUTTON_TEXT", "N&ative Sequencing"},
		{"CUSTOM_SEQUENCE_TABLE_RADIO_BUTTON_TEXT", "&Custom Sequence Table:"},
		{"SEQUENCING_TABLE_NAME_LIST_CHOOSER_LABEL", "Na&me:"},
		{"SEQUENCE_TABLE_LIST_BROWSER_DIALOG.title", "Select a Sequence Table"},
		{"SEQUENCE_TABLE_LIST_BROWSER_DIALOG.listLabel", "&Tables:"},
		{"SEQUENCING_NAME_FIELD_COMBO_BOX_LABEL", "Nam&e Field:"},
		{"SEQUENCE_NAME_FIELD_LIST_BROWSER_DIALOG.title", "Select a Sequence Name Field"},
		{"SEQUENCE_NAME_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},
		{"SEQUENCING_COUNTER_FIELD_COMBO_BOX_LABEL", "C&ounter Field:"},
		{"SEQUENCE_COUNTER_FIELD_LIST_BROWSER_DIALOG.title", "Select a Sequence Counter Field"},
		{"SEQUENCE_COUNTER_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},

		
		//FieldAccessingPanel
		{"FIELD_ACCESSING_PANEL_BORDER_TEXT", "Mapped Field Accessing"},
		{"METHOD_ACCESSING_RADIO_BUTTON_TEXT", "&Method Accessing"},
		{"FIELD_ACCESSING_RADIO_BUTTON_TEXT", "&Direct Field Accessing"},
			
		
		// Project Defaults Policy
		{"PROJECT_DEFAULTS_POLICY_CREATION", "Creation"},
		{"PROJECT_DEFAULTS_POLICY_AFTER_LOADING_POLICY", "After Loading"},
		{"PROJECT_DEFAULTS_POLICY_COPYING_POLICY", "Copying"},
		{"PROJECT_DEFAULTS_POLICY_INHERITANCE_POLICY", "Inheritance"},
		{"PROJECT_DEFAULTS_POLICY_INSTANTIATION_POLICY", "Instantiation"},
		{"PROJECT_DEFAULTS_POLICY_NAMED_QUERIES", "Named Queries"},
		{"PROJECT_DEFAULTS_POLICY_DESCRIPTOR_ADVANCED_PROPERTIES", "Descriptor Advanced Properties"},
		{"PROJECT_DEFAULTS_POLICY_CACHE_ALL_STATEMENTS", "C&ache All Statements"},
		{"PROJECT_DEFAULTS_POLICY_BIND_ALL_PARAMETERS", "&Bind All Parameters"},
		
		// Transactional Project Defaults Policy
		{"TRANSACTIONAL_PROJECT_DEFAULTS_POLICY_EVENTS_POLICY",    "Events"},
		{"TRANSACTIONAL_PROJECT_DEFAULTS_PROJECT_CACHING",         "Caching"},
		{"TRANSACTIONAL_PROJECT_DEFAULTS_POLICY_RETURNING_POLICY", "Returning"},
		// Relational Project Defaults Policy
		{"RELATIONAL_PROJECT_DEFAULTS_POLICY_INTERFACE_ALIAS_POLICY", "Interface Alias"},
		{"RELATIONAL_PROJECT_DEFAULTS_POLICY_MULTI_TABLE_INFO_POLICY", "Multi Table Info"},
		
		//RelationalProjectOptionsPropertiesPage
		{"DEPLOYMENT_AND_CODE_GENERATION_PANEL_TITLE", "Deployment and Java Source Code Generation"},	
		{"PROJECT_JAVA_SOURCE_PANEL_TITLE", "Project Java Source"},	
		{"PROJECT_CLASS_LABEL", "&Class Name:"},	
		{"PROJECT_SOURCE_ROOT_DIRECTORY_LABEL", "&Root Directory:"},	
		{"PROJECT_DEPLOYMENT_XML_PANEL_TITLE", "Project Deployment XML"},	
		{"PROJECT_DEPLOYMENT_XML_FILE_LABEL", "F&ile Name:"},
		{"TABLE_CREATOR_JAVA_SOURCE_PANEL_TITLE", "Table Creator Java Source"},	
		{"TABLE_CREATOR_CLASS_LABEL", "C&lass Name:"},
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_LABEL", "Root Dir&ectory:"},
		{"MODEL_JAVA_SOURCE_PANEL_TITLE", "Model Java Source"},
		{"MODEL_SOURCE_ROOT_DIRECTORY_LABEL", "Root Director&y:"},	
		{"TABLE_GENERATION_PANEL_TITLE", "Table Generation"},
		{"DEFAULT_PRIMARY_KEY", "Default &Primary Key:"},
		{"PRIMARY_KEY_SEARCH_PATTERN", "Primary &Key Search Pattern:"},
		{"GENERATE_DEPRECATED_DIRECT_MAPPINGS_CHECK_BOX", "&Generate Deprecated Direct Mappings"},
		{"USES_WEAVING_CHECK_BOX", "Use &Weaving"},
		
		// TransactionalProjectDefaultsPropertiesPage
		{"CACHING_POLICY_CACHE_TYPE_CHOOSER",          "T&ype:"},
		{"CACHING_POLICY_CACHE_SIZE_SPINNER",          "Si&ze:"},
		{"CACHING_POLICY_EXISTENCE_CHECKING_CHOOSER",  "Existence &Checking:"},
		{"CACHING_POLICY_CACHE_COORDINATION_CHOOSER",  "C&oordination:"},
		{"CACHING_POLICY_CACHE_ISOLATION_CHOOSER",     "&Isolation:"},

		// TransactionalProjectDefaultsPropertiesPage - Cache Coordination choices
		{"CACHING_POLICY_CACHE_COORDINATION_NONE", "None"},
		{"CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES", "Synchronize Changes"},
		{"CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS", "Synchronize Changes and New Objects"},
		{"CACHING_POLICY_CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS", "Invalidate Changed Objects"},

		// TransactionalProjectDefaultsPropertiesPage - Cache Isolation choices
		{"CACHING_POLICY_CACHE_ISOLATION_ISOLATED", "Isolated"},
		{"CACHING_POLICY_CACHE_ISOLATION_SHARED",   "Shared"},

		// CachingPolicyPropertiesPage - Cache Type choices
		{"ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_FULLIDENTITYMAP",          "Full"},
		{"ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_HARDCACHEWEAKIDENTITYMAP", "Weak with Hard Subcache"},
		{"ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_NOIDENTITYMAP",            "None"},
		{"ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_SOFTCACHEWEAKIDENTITYMAP", "Weak with Soft Subcache"},
		{"ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_WEAKIDENTITYMAP",          "Weak"},
		{"ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_SOFTIDENTITYMAP",		  "Soft"},

		// CachingPolicyPropertiesPage - Existence Checking choices
		{"CACHING_POLICY_EXISTENCE_CHECKING_CHECK_CACHE",          "Check Cache"},
		{"CACHING_POLICY_EXISTENCE_CHECKING_CHECK_DATABASE",       "Check Cache then Database"},
		{"CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_EXISTENCE",     "Assume Existence"},
		{"CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE", "Assume Non-Existence"},
	};
		
	public Object[][] getContents() {
		return contents;
	}
}
