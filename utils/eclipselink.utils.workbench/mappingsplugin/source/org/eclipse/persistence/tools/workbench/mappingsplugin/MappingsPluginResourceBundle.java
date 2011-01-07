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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.util.ListResourceBundle;


public class MappingsPluginResourceBundle extends ListResourceBundle {

	
	/** 
	 * The contents of the resource bundles
	 */
	static final Object[][] contents = {

		//General
		{"DEFAULT_PACKAGE", "(default package)"},

		//Export Menu		
		{"EXPORT_MENU", "&Export"},

		//NewProjectAction
		{"NEW_PROJECT_ACTION", "&Project..."},
		{"NEW_PROJECT_ACTION.toolTipText", "Creates a New Project."},
		{"NEW_PROJECT_ACTION.accelerator", "control E"},
		
		// GenerateProblemReportAction
		{"GENERATE_PROBLEM_REPORT_ACTION", "&Generate Problem Report"},
		{"GENERATE_PROBLEM_REPORT_ACTION.toolTipText", "Generate Problem Report"},
		{"GENERATE_PROBLEM_REPORT_ACTION.accelerator", "control P"},
		
		// For ProjectStatusReportDialog
		{"PROJECT_STATUS_REPORT_DIALOG.COPY_BUTTON", "&Copy"},
		{"endReport", "End Report"},
		{"projectStatusReport", "Project Status Report"},
		
		// ManageNonDescriptorClassesAction
		{"MANAGE_NON_DESCRIPTOR_CLASSES", "&Manage Non-Descriptor Classes ..."},
		{"MANAGE_NON_DESCRIPTOR_CLASSES.toolTipText", "Manage Non-Descriptor Classes"},
		
		// NonDescriptorClassManagementDialog
		{"NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.TITLE",
			"Manage {0}Non-Descriptor Classes"},
		{"NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.EXPLANATION", 
			"These are the non-descriptor classes for whom the full class definitions have been imported into the Workbench.  Other classes may be referenced by name, but their names are all the information that is kept.{0}{0}Add/Refresh:  Entire class definition is updated from class file and added to project.{0}Remove:  Class definition is cleared.  Class may still be referred to by name only."},
		{"NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.ADD_ACTION",
			"Add..."},
		{"NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.REMOVE_ACTION",
			"Remove"},
		{"NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.REFRESH_ACTION",
			"Refresh"},
			
		//UsingJAXBAction
		{"USING_JAXB_ACTION", "Project From &XML Schema (JAXB)..."},
		{"USING_JAXB_ACTION.toolTipText", "Create a EclipseLink Workbench Project using a XML schema input."},
		{"USING_JAXB_ACTION.accelerator", "control X"},
		{"JAXB_GENERATION_EXCEPTION.MESSAGE", "The following error was encountered while generating a project using JAXB:"},
		{"SCHEMA_LOAD_ERROR.MESSAGE", "Error Importing Schema During JAXB Project Generation, The following error was encountered while loading the schema: "},
		{"PROJECT_GENERATING_DIALOG.TITLE", "Generating Project"},
		{"PROJECT_GENERATING_MESSAGE", "Generating Project.  Please wait..."},
		{"GENERATION_EXCEPTION", "Exception Occured During Migration"},
		{"GENERATION_FAILURE", "Generation completed unsuccessfully, double check the input schema for validity."},
		
		//AbstractMigrationAction
		{"PROJECT_MIGRATING_DIALOG.TITLE", "Migrating Project"},
		{"MIGRATING_PROJECT_MESSAGE",      "Migrating Project. Please wait..."},
		{"PROJECT_OPENING_DIALOG.TITLE", "Opening Project"},
		{"OPENING_PROJECT_MESSAGE",      "Opening ''{0}''. Please wait..."},
		{"MIGRATION_LOG", "Migration Log"},
	
		//MigrateFromOC4JAction
		{"MIGRATE_FROM_OC4J_ACTION", "From &OC4J 9.0.x..."},
		{"MIGRATE_FROM_OC4J_ACTION.toolTipText", "Creates one or more EclipseLink Workbench Projects migrated from OC4J."},
		{"MIGRATE_FROM_OC4J_ACTION.accelerator", "control J"},
		{"MIGRATION_EXCEPTION", "Exception Occured During Migration"},
		{"MIGRATION_FAILURE", "The migration completed unsuccessfully.  Please double check your selected input resources."},
		{"MALFORMED_URL", "One of the files selected for the classpath could not be resolved."},
		{"OPEN_EXCEPTION", "Unable to open generated EclipseLink Workbench project file."},
		{"UNSUPPORTED_FILE_EXCEPTION", "Generated EclipseLink Workbench project file is of an unsupported type."},
		{"CANCEL_EXCEPTION", "Operation Cancelled."},
		{"INCOMPLETE_CLASSPATH", "Class not found.  Please assure all necessary files are on the classpath."},
		{"MISSING_CLASSPATH_ENTRY", "A selected classpath entry is missing."},
		{"FILE_NOT_FOUND", "Generated EclipseLink Workbench project missing EJB JAR file."},
		{"EJB_JAR_FILE_NOT_SPECIFIED", "Generated EclipseLink Workbench project EJB JAR file un-specified."},
		
		// *** JaxbProjectCreationDialog ***
		{"JAXB_PROJECT_DIALOG_TITLE", "Create EclipseLink Workbench Project using JAXB"},
		{"INPUT_SCHEMA_FILE_LABEL", "&Schema File:  "},
		{"INPUT_CUSTOMIZATION_FILE_LABEL", "&JAXB Customization File (Optional):"},
		{"OUTPUT_MASTER_DIRECTORY_LABEL", "O&utput Directory:"},
		{"OUTPUT_SOURCE_DIRECTORY_LABEL", "Output Source &Directory:"},
		{"OUTPUT_WORKBENCH_PROJECT_DIRECTORY_LABEL", "Output Workbench &Project Directory:"},
		{"OUTPUT_INTERFACE_PACKAGE_NAME_LABEL", "Package Name for Generated &Interfaces:"},
		{"OUTPUT_IMPL_CLASS_PACKAGE_NAME_LABEL", "Package Name for Generated Implementation &Classes:"},
		{"XSD_FILE_FILTER", "XML Schema Files (*.xsd)"},
		{"XML_FILE_FILTER", "XML Files (*.xml)"},
		{"JAXB_FILE_CHOOSER_TITLE", "Choose File"},
		{"JAXB_DIRECTORY_CHOOSER_TITLE", "Choose Directory"},
		{"SCHEMA_FILE_DOES_NOT_EXIST", "The selected schema file does not exist.  Please select a valid schema file."},
		{"CUSTOMIZATION_FILE_DOES_NOT_EXIST", "The selected customization file does not exist.  Please select a valid file."},
		{"OUTPUT_SOURCE_DIRECTORY_COULD_NOT_BE_RESOLVED", "The output source directory could not be resolved."},
		{"OUTPUT_SOURCE_DIRECTORY_COULD_NOT_BE_CREATED", "The output source directory \"{0}\" could not be created."},
		{"OUTPUT_RESOURCE_DIRECTORY_COULD_NOT_BE_RESOLVED", "The output resource directory could not be resolved."},
		{"OUTPUT_RESOURCE_DIRECTORY_COULD_NOT_BE_CREATED", "The output resource directory \"{0}\" could not be created."},
		{"OUTPUT_WORKBENCH_PROJECT_DIRECTORY_COULD_NOT_BE_RESOLVED", "The output Workbench project directory could not be resolved."},
		{"OUTPUT_WORKBENCH_PROJECT_DIRECTORY_INVALID", "The output Workbench project directory \"{0}\" already contains an existing Workbench project."},
		{"OUTPUT_WORKBENCH_PROJECT_DIRECTORY_COULD_NOT_BE_CREATED", "The output Workbench project directory \"{0}\" could not be created."},
		
		
		{"INTERFACES", "Generate Interfaces"},
		
		//ProjectOC4JMigrationDialog
		{"PROJECT_OC4J_MIGRATION_DIALOG_TITLE", "Create EclipseLink Workbench Projects from OC4J Migration"},
		{"FROM", "From:  "},
		{"TO", "To:  "},
		{"OUTPUT_DIRECTORY", "&Output Directory:  "},
		{"INPUT_DIRECTORY", "I&nput Directory:  "},
		{"CHOOSE_DIRECTORY", "C&hoose Directory"},
		{"CHOOSE_FILE", "&Choose File"},
		{"SELECT", "&Select"},
		{"IS_INDIVIDUAL_FILES", "&Individual Files"},
		{"IS_ARCHIVE_FILE", "Archive &File"},
		{"ARCHIVE_FILE_NAME", "Na&me:  "},
		{"LOG_MESSAGES", "&Show Migration Log"},
		{"INPUT_DIRECTORY_INVALID", "The selected input directory either does not exist, is not a directory, or does not contain the necessary deployment descriptor files. {0}Please select a valid input directory"},
		{"OUTPUT_DIRECTORY_INVALID", "The selected output directory either does not exist, is not a directory, or already contains an existing EclipseLink Workbench project. {0}Please select a valid output directory"},
		{"ARCHIVE_FILE_DOES_NOT_EXIST", "The selected archive file does not exist.  Please select a valid archive file."},
		{"ARCHIVE_FILE_ALREADY_TOPLINK_CMP", "The selected archive file already contains \"{0}\" indicating that it is already a TopLink CMP deployment.{1}Please select an archive file that does not already contain a TopLink deployment descriptor."},
		{"WARNING", "Invalid Selection"},
		{"OUTPUT_DIR_CONTAINS_MW_PROJ", "Choosing the selected output directory will cause the EclipseLink Workbench project to be output to: {0} {1}This directory already contains a EclipseLink Workbench project.  Please remove the existing project files or choose another directory."},
		{"INPUT_DIR_DOESNT_CONTAIN_INPUT_DD", "The selected input directory does not contain the necessary deployment descriptor files. {0}Please select a directory that contains the necessary input files."},
		{"JAR_EAR_FILE_FILTER", "Jar and Ear file filter"},
		{"INPUT_FILE_INCORRECT_TYPE", "Selected input file is not of the correct type.  Please select an archive file of type JAR or EAR"},
		{"SELECTED_ARCHIVE_FILE_APPEARS_INVALID", "Selected input file does not seem to be a valid archive file.  Please select a valid archive file."},
		{"MIGRATION_FAILED", "The migration has failed.  This is probably due to invalid input selections.  Please assure you have selected valid input files."},
		
		//MigrateFromWLSAction
		{"MIGRATE_FROM_WLS_ACTION", "From &Weblogic Server..."},
		{"MIGRATE_FROM_WLS_ACTION.toolTipText", "Creates one or more EclipseLink Workbench Project migrated from Weblogic server"},
		{"MIGRATE_FROM_WLS_ACTION.accelerator", "control W"},

		//ProjectWLSMigrationDialog
		{"PROJECT_WLS_MIGRATION_DIALOG_TITLE", "Create EclipseLink Workbench Projects from WLS Migration"},
		{"OPTIONAL", "*optional*"},
		{"WLS_JAR_XML_LOCATION", "Input weblogic-ejb-jar.xml (file, JAR, EAR):  "},
		
		//RefreshClassesAction
		{"REFRESH_CLASSES_ACTION", "&Refresh Classes"},
		{"REFRESH_CLASSES_ACTION.accelerator", "control R"},
		{"REFRESH_CLASSES_ACTION.toolTipText", "Refreshes Selected Classes"},
		{"EJBJAR_EXCEPTION_MESSAGE", "An exception occurred while attempting to read the ejb-jar.xml file.{0}Check your file to ensure that it is well-formed, valid, and complies to the EJB 2.0 DTD or schema."},
		{"EJBJARXML_PROJECT_NOT_UPDATED_MESSAGE", "{0}The projects could not be updated."},
		{"EJB_JAR_XML_EXCEPTION_ERROR", "An exception occurred while attempting to read the ejb-jar.xml file. Check your file to ensure that it is well-formed, valid, and complies to the EJB 2.x DTD or schema.{0}{1}"},
		{"EJB_JAR_XML_PROJECT_NOT_UPDATED_ERROR", "The projects could not be updated.{0}{1}"},
		{"INVALID_DOC_TYPE_ERROR", "The doctype for the specified file is not supported. The Workbench only supports the EJB 2.x DTD, or doctype \"{0}\"."},
		{"PROJECT_UPDATE_SUCCESSFUL", "Project updated"},
		{"PROJECT_UPDATE_WITH_ERRORS", "Project updated with incomplete information"},
		{"PROJECT_EJB_UPDATE_STATUS_DIALOG_TITLE",   "Update Status"},
		{"PROJECT_EJB_UPDATE_STATUS_DIALOG_MESSAGE", "&Status of the project update:"},
		{"PROJECT_UPDATE_STATUS_DIALOG_MESSAGE",     "The project contains the following errors. Continue anyway?"},
		{"EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_NO_BUTTON", "   &No   "},
		{"EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_YES_BUTTON", "   &Yes   "},
		
		//AddOrRefreshClassesAction
		{"ADD_OR_REFRESH_CLASSES_ACTION", "Add or Refresh &Classes..."},
		{"ADD_OR_REFRESH_CLASSES_ACTION.accelerator", "control shift R"},
		{"ADD_OR_REFRESH_CLASSES_ACTION.toolTipText", "Add or Refresh Classes"},
		{"CLASS_IMPORT_WAIT_DIALOG.TITLE", "Importing Classes"},
		{"CLASS_IMPORT_WAIT_MESSAGE", "Importing the selected classes...  Please wait..."},
		{"UPDATE_PROJECT_FROM_EJB_JAR", "Would you like to update your project from the ejb-jar.xml file?"},
		{"UPDATE_PROJECT_FROM_EJB_JAR.title", "Update from ejb-jar.xml File"},
		{"REFRESH_DESCRIPTORS_WITH_INHERITED_ATTRIBUTES_WARNING", "Refreshing descriptor classes with inherited attributes may cause the loss of the inherited attributes if descriptor class hierarchy has changed.{0}This can be avoided by adding any new classes in the class desriptor hierarchy to the project as either managed or unmangaged.{0}Would you like to continue with the refresh?"},
		{"REFRESH_DESCRIPTORS_WITH_INHERITED_ATTRIBUTES_TITLE", "One or More Descriptors With Inherited Attributes"},
			
		//AutomapAction
		{"AUTOMAP_ACTION", "Auto&map"},
		{"AUTOMAP_ACTION.TOOLTIP", "Attemps to automap the selected nodes"},
		{"AUTOMAP_DESCRIPTOR_SUCCESSFUL", "Automap completed"},
		{"AUTOMAP_PACKAGE_SUCCESSFUL", "All descriptors in ''{0}'' have been automapped"},
		{"AUTOMAP_PROJECT_SUCCESSFUL", "All descriptors in ''{0}'' have been automapped"},
		{"AUTOMAP_STATUS_DIALOG_TITLE", "Automap Status"},
		{"RELATIONAL_PROJECT_UNAUTOMAPPABLE", "For automapping to proceed, the project needs tables to which the descriptors can then be mapped."},
		{"RELATIONAL_PROJECT_UNAUTOMAPPABLE.title", "The project contains no tables."},
		{"XML_PROJECT_UNAUTOMAPPABLE", "Project is XML Project."},
		{"XML_PROJECT_UNAUTOMAPPABLE.title", "Currently automapping is not supported in XML projects."},

		//CreateNewClassAction
		{"CREATE_NEW_CLASS_ACTION", "Create &New Class..."},
		{"CREATE_NEW_CLASS_ACTION.accelerator", "control B"},
		{"CREATE_NEW_CLASS_ACTION.toolTipText", "Create a New Class"},

		// NewClassNameDialog
		{"NEW_CLASS_NAME_DIALOG_DESCRIPTOR_ALREADY_EXISTS",    "A descriptor for the class named ''{0}'' already exists."},
		{"NEW_CLASS_NAME_DIALOG_DESCRIPTOR_ALREADY_EXISTS_DIFFERENT_CASE", "A descriptor for a class with the same name but different case already exists."},
		{"NEW_CLASS_NAME_DIALOG_CLASS_ALREADY_EXISTS",    "A class named ''{0}'' already exists."},
		{"NEW_CLASS_NAME_DIALOG_CLASS_NAME_LABEL",        "&New Class Name:"},
		{"NEW_CLASS_NAME_DIALOG_CLASS_NAME_INVALID",      "The class name is not valid. The class name ''{0}'' is not a valid identifier."},
		{"NEW_CLASS_NAME_DIALOG_CLASS_NAME_RESERVED",      "The class name ''{0}'' is reserved."},
		{"NEW_CLASS_NAME_DIALOG_CLASS_NAME_RESERVED_DIFFERENT_CASE",      "The class name collides with a reserved class name: ''{0}''."},
		{"NEW_CLASS_NAME_DIALOG_INITIAL_CLASS_NAME",      "NewClass"},
		{"NEW_CLASS_NAME_DIALOG_NO_CLASS_NAME_SPECIFIED", "The class name cannot be empty."},
		{"NEW_CLASS_NAME_DIALOG_PACKAGE_NAME_LABEL",      "&Package Name:"},
		{"NEW_CLASS_NAME_DIALOG_TITLE",                   "New Class Name"},
		{"ADD_CLASS_DIALOG_TITLE",                   "Add Class"},
		{"RENAME_CLASS_DIALOG_TITLE",                   "Rename Class"},
		{"NEW_CLASS_NAME_DIALOG_CLASS_ALREADY_EXISTS_DIFFERENT_CASE", "A class with the same name but different case already exists."},
 
		//ExportModelJavaSourceAction
		{"EXPORT_MODEL_JAVA_SOURCE_PROJECT", "&Model Java Source..."},
		{"EXPORT_MODEL_JAVA_SOURCE_PROJECT.toolTipText", "Export Model Java Source for the Selected Projects"},
		{"EXPORT_MODEL_JAVA_SOURCE_PROJECT.accelerator", "control alt E"},

		//ExportSpecificDescriptorModelJavaSourceAction
		{"EXPORT_MODEL_JAVA_SOURCE_DESCRIPTOR", "&Export Model Java Source..."},
		{"EXPORT_MODEL_JAVA_SOURCE_DESCRIPTOR.toolTipText", "Export Model Java Source for the Selected Descriptors"},
		{"EXPORT_MODEL_JAVA_SOURCE_DESCRIPTOR.accelerator", "control alt M"},

		//ExportDeploymentXmlAction
		{"EXPORT_DEPLOYMENT_XML_ACTION", "Project &Deployment XML..."},
		{"EXPORT_DEPLOYMENT_XML_ACTION.accelerator", "control D"},
		{"EXPORT_DEPLOYMENT_XML_ACTION.toolTipText", "Export Deployment XML for the Selected Projects"},
		
        //ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction
        {"EXPORT_DEPLOYMENT_XML_AND_INITIALIZE_RUNTIME_DESCRIPTORS_ACTION", "Project Deployment XML Initialize Descriptors..."},
        {"EXPORT_DEPLOYMENT_XML_AND_INITIALIZE_RUNTIME_DESCRIPTORS_ACTION.toolTipText", "Export Deployment XML for the Selected Projects and Initialize Descriptors at Runtime"},
        {"EXPORT_DEPLOYMENT_XML_AND_INITIALIZE_RUNTIME_DESCRIPTORS_STATUS_BAR", "Runtime Descriptors Initialized Without Error"},
        
        //ExportProjectJavaSourceAction
		{"EXPORT_PROJECT_JAVA_SOURCE_ACTION", "Project &Java Source..."},
		{"EXPORT_PROJECT_JAVA_SOURCE_ACTION.toolTipText", "Export Project Java Source for the Selected Projects"},
		{"EXPORT_PROJECT_JAVA_SOURCE_ACTION.accelerator", "control shift J"},

		//ExportTableCreatorJavaSourceAction
		{"exportTableCreatorJavaSource", "Table &Creator Java Source..."},
		{"exportTableCreatorJavaSource.toolTipText", "Export Table Creator Java Source for the Selected Projects"},
		{"exportTableCreatorJavaSource.accelerator", "control shift E"},

		
		// Help Action
		{"HELP", "&Help"},
		{"HELP.tooltip", "Help for the selected item(s)"},
		
		//RemoveAction 
		{"REMOVE_ACTION", "Remo&ve"},
		{"REMOVE_ACTION.toolTipText", "Remove Selected Item"},
		{"CONFIRM_REMOVE.message", "Are you sure you want to remove ''{0}''?"},
		{"CONFIRM_REMOVE.title", "Confirm Remove"},
		{"CONFIRM_MULTIPLE_REMOVE.message", "Are you sure you want to remove the selected items?"},	
	
		{"PROJECT_UPDATE_PROJECT_FROM_EJB_JAR_XML_ACTION",           "&Update Project from ejb-jar.xml"},
		{"PROJECT_UPDATE_PROJECT_FROM_EJB_JAR_XML_ACTION_TOOLTIP",   "Update EJB Information of the Selected Projects"},
		{"PROJECT_WRITE_PROJECT_TO_EJB_JAR_XML_ACTION",              "&Write Project to ejb-jar.xml"},
		{"PROJECT_WRITE_PROJECT_TO_EJB_JAR_XML_ACTION_TOOLTIP",      "Write the Project Information to ejb-jar.xml"},

		{".mwp", "EclipseLink Workbench Project Files (*{0})"},
		
		{"saveAs.title", "Save {0} As"},
		
		{"save", "Save"},
		
		
		
		
		//ProjectCreationDialog

		{"PROJECT_CREATION_DIALOG_TITLE",             "Create New EclipseLink Workbench Project"},
		{"PROJECT_CREATION_DIALOG_INVALID_FILE_NAME", "The project name is invalid."},
		{"PROJECT_CREATION_DIALOG_NO_FILE_NAME",      "The project name cannot be empty."},
		{"DATASOURCE_LABEL", "Data Source:"},
		{"RELATIONAL_RADIO_BUTTON", "&Database"},
		{"XML_RADIO_BUTTON","&XML"},
		{"EIS_RADIO_BUTTON","&EIS"},
		{"DATABASE_PLATFORM_LABEL", "&Platform:"},
		{"SAVE_LOCATION_LABEL", "Location:"},
		{"BROWSE_BUTTON_TEXT", "Browse..."},
		{"PROJECT_NAME_LABEL", "&Name:"},
		{"EIS_PLATFORM_LABEL", "&Platform:"},
		
		//ProjectLegacyMigrationDialog
		
		{"PROJECT_LEGACY_MIGRATION_DIALOG_TITLE", 		"Create New EclipseLink Workbench Project From Previous Version"},
		{"PROJECT_LEGACY_MIGRATION_DIALOG_SAVE_NOW", 	"Save Now"},
		{"PROJECT_LEGACY_MIGRATION_DIALOG_SAVE_LATER",	"Save Later"},
		{"PROJECT_LEGACY_MIGRATION_DIALOG_WARNING", 	"You have selected a project file that was created by a previous version of the TopLink Workbench.  A new project will be created based on the older project. This will not affect the previous project.  If you choose to save now, you will be prompted to select a save location for the new project."},
		{"LEGACY_MIGRATION_COMPLETE.TITLE", 			"Migration Complete"},
		{"LEGACY_MIGRATION_COMPLETE.MESSAGE",			"The migration is complete and any relative paths have been preserved.  These may not resolve properly in the new project save location and should be verified."},
		{"SAVE_DIRECTORY_FOR_LEGACY_PROJECT_TITLE", 			"Select Project Save Directory"},

		// For ProjectSourceGenerationCoordinator
		{"PROJECT_CLASS_NAME_DIALOG.title", "{0} - Project Class Name"},
		{"PROJECT_CLASS_NAME_DIALOG.message", "&Enter valid project class name:"},
	
		{"PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG.title", "{0} - Project Source Root Directory"},
		{"PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_NO_DIRECTORY_CHOSEN.message", "Choose a valid directory.<p>"},
		{"PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_A_FILE.message", "The path \"{0}\" does not represent a directory.<br>Choose a valid directory.<p>"},
		{"PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_INVALID.message", "The path \"{0}\" is invalid.<br>Choose a valid directory.<p>"},
		{"PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_COULD_NOT_BE_CREATED.message", "The path \"{0}\" could not be created.<br>Choose a valid directory.<p>"},
		{"PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON", "&Select"},
	
		{"CREATE_PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG.title", "Directory Does Not Exist"},
		{"CREATE_PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG.message", "The directory \"{0}\" does not exist.<br>Would you like to create it?"},
	
		{"PROJECT_FILE_EXISTS_DIALOG.title", "Project File Exists"},
		{"PROJECT_FILE_EXISTS_DIALOG.message", "A file named \"{0}\" already exists.<br>Do you wish to overwrite it?"},
	
		{"exportProjectJavaSource", "{0} - Export Project Java Source"},
		{"exportingProjectJavaSource", "Your project java source might not work correctly at runtime because one or more of your active descriptors or tables is incomplete.  Look through your project for descriptors or tables with the yellow caution sign.{0}{0}Continue?"},
	
		{"EXPORT_PROJECT_SOURCE_SUCCESS_DIALOG.title", "{0} - Export Complete"},
		{"EXPORT_PROJECT_SOURCE_SUCCESS_DIALOG.message", "The project java source was exported successfully."},
	
		{"EXPORT_PROJECT_SOURCE_ERROR_DIALOG.message", "An error occurred while attempting to write the file \"{0}\".<br>The file was not written."},

		//TableCreatorSourceGenerationCoordinator
		{"TABLE_CREATOR_CLASS_NAME_DIALOG.title", "{0} - Project Table Creator Class Name"},
		{"TABLE_CREATOR_CLASS_NAME_DIALOG.message", "&Enter valid class name:"},
			
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG.title", "{0} - Project Table Creator Source Root Directory"},
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_NO_DIRECTORY_CHOSEN.message", "Choose a valid directory.<p>"},
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_A_FILE.message", "The path \"{0}\" does not represent a directory.<br>Choose a valid directory.<p>"},
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_INVALID.message", "The path \"{0}\" is invalid.<br>Choose a valid directory.<p>"},
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_COULD_NOT_BE_CREATED.message", "The path \"{0}\" could not be created.<br>Choose a valid directory.<p>"},
		{"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON", "&Select"},
			
		{"CREATE_TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG.title", "Directory Does Not Exist"},
		{"CREATE_TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG.message", "The directory \"{0}\" does not exist.<br>Would you like to create it?"},
			
		{"TABLE_CREATOR_FILE_EXISTS_DIALOG.title", "File Exists"},
		{"TABLE_CREATOR_FILE_EXISTS_DIALOG.message", "A file named \"{0}\" already exists.<br>Do you wish to overwrite it?"},
			
		{"GENERATE_TABLE_CREATOR_SOURCE_SUCCESS_DIALOG.title", "{0} - Export Complete"},
		{"GENERATE_TABLE_CREATOR_SOURCE_SUCCESS_DIALOG.message", "The table creator source was exported successfully."},
			
		{"GENERATE_TABLE_CREATOR_SOURCE_ERROR_DIALOG.message", "An error occurred while attempting to write the file \"{0}\".<br>The file was not written."},

		// ProjectDeploymentXmlGenerationCoordinator
		{"PROJECT_XML_SAVE_AS_DIALOG_CANT_SAVE",        "Cannot save the file ''{0}''. The file exists and is marked Read-Only. Save the file with another file name or to another location."},
		{"PROJECT_XML_SAVE_AS_DIALOG_CANT_SAVE_TITLE",        "Read-Only File"},
		{"PROJECT_XML_SAVE_AS_DIALOG_REPLACE",          "The file {0} already exists. Do you want to replace the existing file?"},
		{"PROJECT_XML_SAVE_AS_DIALOG_REPLACE_TITLE",          "File Already Exists"},
		{"PROJECT_XML_SAVE_AS_DIALOG_TITLE",            "Export Project Deployment XML"},
		{"PROJECT_XML_SAVE_AS_DIALOG_XML_DESCRIPTION",  "XML Files (*.xml)"},
		{"PROJECT_XML_PROJECT_PROBLEMS_DIALOG.title",   "{0} - Generate Project Deployment XML"},
		{"PROJECT_XML_PROJECT_PROBLEMS_DIALOG.message", "Your project might not work correctly at runtime because one or more of your active descriptors or tables is incomplete. Review your project for descriptors or tables with the yellow caution sign.{0}{0}Continue?"},
		{"PROJECT_XML_OVERWRITE_DIALOG_TITLE",          "Overwrite Project Deployment XML"},
		{"GENERATE_PROJECT_XML_INVALID_FILE_DIALOG.title",   "{0} - Invalid Deployment XML File"},
		{"GENERATE_PROJECT_XML_INVALID_FILE_DIALOG.message", "Invalid Deployment XML File: {0}"},
		{"GENERATE_PROJECT_XML_MISSING_XDB_JAR.title",	"NoClassDefFoundError oracle.xdb.dom.XDBDocument"},
		{"GENERATE_PROJECT_XML_MISSING_XDB_JAR.message", "The application has encountered a NoClassDefFoundError for oracle.xdb.dom.XDBDocument.{0}" +
					"The most likely cause of this error is the use of a Direct-to-XMLType Mapping when 'xdb.jar' is missing from the Workbench classpath.{0}" +
					"Please add 'xdb.jar' to the Workbench classpath and re-generate project deployment XML for proper runtime functionality."},
		{"GENERATE_PROJECT_XML_SUCCESS_DIALOG.title",   "{0} - Export Complete"},
		{"GENERATE_PROJECT_XML_SUCCESS_DIALOG.message", "The project deployment XML was exported successfully."},
		{"GENERATE_PROJECT_XML_ERROR_DIALOG.message",   "An error occurred while attempting to write the file \"{0}\".<br>The file was not written."},

		// ModelSourceGenerationCoordinator
		{"MODEL_SOURCE_ROOT_DIRECTORY_DIALOG.title", "{0} - Model Source Root Directory"},
		{"MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_NO_DIRECTORY_CHOSEN.message", "Choose a valid directory.<p>"},
		{"MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_A_FILE.message", "The path \"{0}\" does not represent a directory.<br>Choose a valid directory.<p>"},
		{"MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_INVALID.message", "The path \"{0}\" is invalid.<br>Choose a valid directory.<p>"},
		{"MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_COULD_NOT_BE_CREATED.message", "The path \"{0}\" could not be created.<br>Choose a valid directory.<p>"},
		{"MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON", "&Select"},
		
		{"CREATE_MODEL_SOURCE_ROOT_DIRECTORY_DIALOG.title", "Directory Does Not Exist"},
		{"CREATE_MODEL_SOURCE_ROOT_DIRECTORY_DIALOG.message", "The directory \"{0}\" does not exist.<br>Would you like to create it?"},
		
		{"no",       "     No     "},
		{"noToAll",  "No to All"},
		{"yesToAll", "Yes to All"},
		{"yes",      "    Yes    "},
		{"cancel",   "Cancel"},

		{"EXPORT_MODEL_SOURCE_SUCCESS_DIALOG.title", "{0} - Export Complete"},
		{"EXPORT_MODEL_SOURCE_SUCCESS_DIALOG.message", "The model source files were generated successfully."},
		
		{"EXPORT_MODEL_SOURCE_COMPLETION_DIALOG.title", "{0} - Export Complete"},
		{"EXPORT_MODEL_SOURCE_COMPLETION_DIALOG.message", "Model source files finished exporting."},
		
		{"EXPORT_MODEL_SOURCE_CONTINUABLE_ERROR_DIALOG.message", "An exception occurred while exporting model source files.  Continue?"},
		{"EXPORT_MODEL_SOURCE_CONTINUABLE_ERROR_DIALOG.title", "Exception Encountered"},
		
		{"EXPORT_MODEL_SOURCE_ERROR_DIALOG.message", "An error occurred while exporting model source files."},
		{"EXPORT_MODEL_SOURCE_YES_BUTTON", "    &Yes    "},
		{"EXPORT_MODEL_SOURCE_NO_BUTTON",  "     &No     "},
		
		{"sourceCodeGeneration_fileExists.title", "Target Files Already Exist"},
		{"sourceCodeGeneration_fileExists.message", "Target files already exist. Would you like to overwrite it?"},
		{"sourceCodeGeneration_generateAssociatedClasses.title", "Generate Associated Classes?"},
		{"sourceCodeGeneration_generateAssociatedClasses.message", "Some associated classes are not defined.  Would you like to generate them?"},
		{"sourceCodeGeneration_specCompliance.title", "Comply To EJB Spec?"},
		{"sourceCodeGeneration_specCompliance.message", "Add or change methods to make your entity bean classes EJB specification compliant?"},

	  		
			// Preferences
			{"PREFERENCES.MAPPINGS", "Mappings"},
				{"PREFERENCES.MAPPINGS.MODIFY_ROOT_DESCRIPTOR_CACHING_POLICY", "Allow modification of root descriptor Caching settings"},
				{"PREFERENCES.MAPPINGS.MODIFY_ROOT_DESCRIPTOR_CACHING_POLICY_EXPLANATION", "If user tries to edit caching settings of a child descriptor, the root descriptor settings will be modified."},
        
				{"PREFERENCES.MAPPINGS.MODIFY_ROOT_DESCRIPTOR_CACHING_POLICY_YES", "&Yes"},  
				{"PREFERENCES.MAPPINGS.MODIFY_ROOT_DESCRIPTOR_CACHING_POLICY_NO", "&No"},        
				{"PREFERENCES.MAPPINGS.MODIFY_ROOT_DESCRIPTOR_CACHING_POLICY_PROMPT", "&Prompt"},
				{"ROOT_DESCRIPTOR_CACHING_POLICY_WARNING", "Caching settings are inherited from the root descriptor in an inheritance hierarchy.  Modifying this setting will modify the Caching policy of {0}.  Would you like to continue?"},
				{"ROOT_DESCRIPTOR_CACHING_POLICY_WARNING.title", "Modifying Root Descriptor Caching Policy"},

			//****** Mappings.ChangeQueryType
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE",        "Allow changing query type"},
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE.YES",    "&Yes"},
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE.NO",     "&No"},
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE.PROMPT", "&Prompt"},

			//****** Mappings.ChangeQueryFormat
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT",        "Allow changing query format"},
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT.YES",    "Y&es"},
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT.NO",     "N&o"},
			{"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT.PROMPT", "P&rompt"},

			//****** Mappings.Class node
			{"PREFERENCES.MAPPINGS.CLASS", "Class"},	
				
			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR", "Maintain a zero-argument constructor while editing classes"},
			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_EXPLANATION", "If an existing zero-argument constructor is renamed or a parameter is added to it, the Workbench will add another zero-argument constructor to the class."},
		
			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_YES", "&Yes"},	
			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_NO", "&No"},		
			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_PROMPT", "&Prompt"},
			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_DIALOG.title", "Maintain Zero Argument Constructor"},
 			{"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_DIALOG.message", "This action will result in the class ''{0}'' having no zero-argument constructor, something which TopLink requires for most persisted classes.{1}{1}Would you like to maintain a zero-argument constructor in this class?"},
 			{"PREFERENCES.MAPPINGS.CLASS.PERSIST_LAST_REFRESH_TIMESTAMP", "Persist Last Refresh Timestamp When Saving"},
			
			//****** Mappings.Ejb node
			{"PREFERENCES.MAPPINGS.EJB",                                 "EJB"},	
			{"PREFERENCES.MAPPINGS.EJB.WRITE_EJB_JAR_XML",               "Write ejb-jar.xml on project save"},	
			{"PREFERENCES.MAPPINGS.EJB.ALWAYS_WRITE_EJB_JAR_XML",        "&Always"},
			{"PREFERENCES.MAPPINGS.EJB.NEVER_WRITE_EJB_JAR_XML",         "Ne&ver"},
			{"PREFERENCES.MAPPINGS.EJB.ALWAYS_PROMPT_WRITE_EJB_JAR_XML", "&Prompt"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO",                 "Allow removing EJB info"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO.YES",             "&Yes"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO.NO",              "&No"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO.PROMPT",          "P&rompt"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO",              "Allow removing EJB 2.x info"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO.YES",          "Y&es"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO.NO",           "N&o"},
			{"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO.PROMPT",       "Pro&mpt"},
	
	
			//****** Mappings.Database node
			{"PREFERENCES.MAPPINGS.DATABASE", "Database"},
			{"PREFERENCES.MAPPINGS.DATABASE.DBDRIVER", "Da&tabase Driver:"},
			{"PREFERENCES.MAPPINGS.DATABASE.URL", "&Connection URL:"},
			
	
		//****** MWAbstractClassCodeGenPolicy
		{"CLASS_COMMENT_FOR_CODE_GEN", "Generated by {0} - {1}."},
		{"CLASS_COMMENT_FOR_CODE_GEN_NO_ZERO_ARG_CONSTRUCTOR", "This class has no zero argument constructor.{0}This source code generation mechanism uses the {0} zero argument constructor to initialize instance variables.{0}In order for this class to be used by TopLink,{0} make sure that its instance variables are initialized properly."},		
		{"EMPTY_METHOD_BODY_COMMENT", "// Fill in method body here."},
		{"CODE_GEN_COMMENT_FOR_ONE_TO_ONE_MAPPING_THAT_CONTROLS_WRITING_OF_PRIMARY_KEY", "/* The instance variable \"{0}\" is mapped as a 1-1 that controls writing of a primary key.  A value must be provided before this object is committed. */"},
		{"CODE_GEN_COMMENT_FOR_AGGREGATE_MAPPING_THAT_DOES_NOT_ALLOW_NULL", "/* The instance variable \"{0}\" is mapped as an aggregate that does not allow null.  It must be initialized here. */"},
		{"CODE_GEN_COMMENT_FOR_AGGREGATE_MAPPING_THAT_DOES_NOT_ALLOW_NULL_IMPLEMENTATION_CLASS_NOT_DETERMINED", "/* Implementation class could not be determined. */"},
		{"CODE_GEN_COMMENT_FOR_COLLECTION_IMPLEMENTATION_CLASS_NOT_DETERMINED", "/* The instance variable \"{0}\" uses the container type \"{1}\".  Could not determine implementation class for this type. */"},

	};
		
	public Object[][] getContents() {
		return contents;
	}
}
