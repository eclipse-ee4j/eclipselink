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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.util.ListResourceBundle;

public final class UiSchemaResourceBundle 
	extends ListResourceBundle
{
	/** 
	 * The contents of the resource bundle
	 */
	static final Object[][] contents = {
		
		// ***  For MWXmlSchemaRepositoryNode ***
		
		{"SCHEMA_REPOSITORY_NODE_DISPLAY_STRING", "Schemas"},
		
		{"IMPORT_SCHEMA_ACTION", "&Import Schema..."},
		{"IMPORT_SCHEMA_ACTION.TOOL_TIP", "Import Schema"},
		
		{"REIMPORT_ALL_SCHEMAS_ACTION", "Rei&mport All Schemas"},
		{"REIMPORT_ALL_SCHEMAS_ACTION.TOOL_TIP", "Reimport All Schemas"},
		
		
		// ***  For MWXmlSchemaNode ***
		
		{"REIMPORT_SCHEMA_ACTION", "Rei&mport Schema"},
		{"REIMPORT_SCHEMA_ACTION.TOOL_TIP", "Reimport Schema"},
		
		{"SCHEMA_PROPERTIES_ACTION", "&Properties"},
		{"SCHEMA_PROPERTIES_ACTION.TOOL_TIP", "Edit Schema Properties"},
		
		
		// *** For MWXmlSchemaPanel ***
		
		{"SCHEMA_DOCUMENT_INFO_PANEL_TAB", "Schema Document Info"},
		{"SCHEMA_STRUCTURE_PANEL_TAB", "Schema Structure"},
		{"DEFAULT_NAMESPACE_CHECK_BOX", "Use &Default Schema Url:"},
		
		
		// *** For SchemaDialogUtilities ***
		
		{"URL_LOAD_ERROR.TITLE", "Error Specifying Source"},
		{"URL_LOAD_ERROR.MESSAGE", "Could not find the source for the schema \"{0}\"."},
		{"URL_LOAD_ERROR.FILE_RESOURCE.UNSPECIFIED_RESOURCE", "The file has not been specified."},
		{"URL_LOAD_ERROR.FILE_RESOURCE.INCORRECTLY_SPECIFIED_RESOURCE", "Could not locate the file named \"{0}\"."},
		{"URL_LOAD_ERROR.FILE_RESOURCE.NONEXISTENT_RESOURCE", "The file named \"{0}\" does not exist."},
		{"URL_LOAD_ERROR.FILE_RESOURCE.INACCESSIBLE_RESOURCE", "Could not access the file named \"{0}\"."},
		{"URL_LOAD_ERROR.URL_RESOURCE.UNSPECIFIED_RESOURCE", "The URL has not been specified."},
		{"URL_LOAD_ERROR.URL_RESOURCE.INCORRECTLY_SPECIFIED_RESOURCE", "The URL \"{0}\" has not been specified correctly."},
		{"URL_LOAD_ERROR.URL_RESOURCE.NONEXISTENT_RESOURCE", "The URL \"{0}\" does not exist."},
		{"URL_LOAD_ERROR.URL_RESOURCE.INACCESSIBLE_RESOURCE", "Could not access the URL \"{0}\"."},
		{"URL_LOAD_ERROR.CLASSPATH_RESOURCE.UNSPECIFIED_RESOURCE", "The classpath resource has not been specified."},
		{"URL_LOAD_ERROR.CLASSPATH_RESOURCE.INCORRECTLY_SPECIFIED_RESOURCE", "The classpath resource \"{0}\" has not been specified correctly."},
		{"URL_LOAD_ERROR.CLASSPATH_RESOURCE.NONEXISTENT_RESOURCE", "Could not locate the classpath resource \"{0}\"."},
		{"URL_LOAD_ERROR.CLASSPATH_RESOURCE.INACCESSIBLE_RESOURCE", "Could not access the classpath resource \"{0}\"."},
		
		{"SCHEMA_LOAD_ERROR.TITLE", "Error Importing Schema"},
		{"SCHEMA_LOAD_ERROR.MESSAGE", "The following error was encountered while loading the schema{0}\"{1}\".{0}{0}ERROR:  {2}{0}{0}Please check to ensure the document conforms to the {0}XML Schema specification and try again."},
		
		
		// *** For SchemaDocumentInfoPanel ***
		
		{"SCHEMA_SOURCE_LABEL", "Source"},
		
		{"FILE_RESOURCE_LABEL", "File:  \"{0}\""},
		{"URL_RESOURCE_LABEL", "URL:  \"{0}\""},
		{"CLASSPATH_RESOURCE_LABEL", "Classpath resource:  \"{0}\""},
		
		{"EDIT_SCHEMA_SOURCE_BUTTON_TEXT", "Edit..."},
		
		
		// *** For SchemaNamespacesPanel ***
		
		{"NAMESPACES_PANEL_TITLE", "Namespaces"},
		
		{"BUILT_IN_NAMESPACES_TABLE_LABEL", "&Built-in Namespaces:"},
		{"TARGET_NAMESPACE_TABLE_LABEL", "&Target Namespace:"},
		{"IMPORTED_NAMESPACES_TABLE_LABEL", "&Imported Namespaces:"},
		
		{"URL_COLUMN_LABEL", "Namespace URL"},
		{"PREFIX_COLUMN_LABEL", "Prefix"},
		{"DECLARED_COLUMN_LABEL", "Declare"},
		
		
		// *** For SchemaComponentDetailsPanel
		
		{"DETAILS_TABLE_LABEL", "&Details"},
		
		
		// *** For ImportSchemaDialog ***
		
		{"IMPORT_SCHEMA_DIALOG.TITLE", "Import Schema"},
		{"IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.TITLE", "Importing Schema"},
		{"IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.DESCRIPTION", "Importing {0}. Please Wait..."},
		
		
		// *** For EditSchemaDialog ***
		
		{"EDIT_SCHEMA_DIALOG.TITLE", "Schema Properties"},
		{"EDIT_SCHEMA_DIALOG.REIMPORT_TEXT", "Reimport"},
		
		// *** Common to ImportSchemaDialog and EditSchemaDialog ***
		
		{"SCHEMA_NAME_NOT_SPECIFIED_ERROR_MESSAGE", "You must specify a schema name."},
		{"SCHEMA_NAME_NOT_UNIQUE_ERROR_MESSAGE", "The schema name must be unique within the project."},
		{"FILE_NOT_SPECIFIED_ERROR_MESSAGE", "You must specify a file."},
		{"URL_NOT_SPECIFIED_ERROR_MESSAGE", "You must specify a URL."},
		{"CLASSPATH_RESOURCE_NOT_SPECIFIED_ERROR_MESSAGE", "You must specify a classpath resource."},
		
		
		// *** For EditableSchemaPropertiesPanel ***
		
		{"SCHEMA_NAME_LABEL", "&Name: "},
		{"SOURCE_PANEL_LABEL", "Source:"}, 
		{"FILE_RADIO_BUTTON_LABEL", "&File: "},
		{"URL_RADIO_BUTTON_LABEL", "&URL: "},
		{"CLASSPATH_RADIO_BUTTON_LABEL", "&Classpath: "},
		{"RESOURCE_NAME_LABEL", "&Resource Name: "},
		
		{"XML_SCHEMA_DEFINITION_DESCRIPTION", "XML Schema Definitions"},
	};
		
	public Object[][] getContents() {
		return contents;
	}
}
