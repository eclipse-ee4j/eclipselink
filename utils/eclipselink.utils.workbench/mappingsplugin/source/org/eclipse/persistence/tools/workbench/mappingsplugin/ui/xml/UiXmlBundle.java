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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.util.ListResourceBundle;

public class UiXmlBundle 
	extends ListResourceBundle 
{
	static final Object[][] contents = 
	{
		// *** For SchemaContextChooser ***
												
		{"SCHEMA_CONTEXT_CHOOSER_BROWSE_BUTTON","Browse..."},
		{"SCHEMA_CONTEXT_CHOOSER_NONE_SELECTED_TEXT", "<none selected>"},
		
		
		// *** For SchemaContextChooserDialog ***
		
		{"SCHEMA_CONTEXT_CHOOSER_DIALOG.TITLE", "Choose Schema Context"},
		{"SCHEMA_CONTEXT_CHOOSER_DIALOG.PREVIEW_TEXT_FIELD", "Schema &Context:"},
		{"SCHEMA_CONTEXT_CHOOSER_DIALOG.NO_SCHEMAS_LOADED_TITLE", "No Schemas Available"},
		{"SCHEMA_CONTEXT_CHOOSER_DIALOG.NO_SCHEMAS_LOADED_MESSAGE", "You must import a schema before choosing a schema context."},
		
			
		// *** For SchemaRootELementChooser ***
												
		{"SCHEMA_ROOT_ELEMENT_CHOOSER_BROWSE_BUTTON","Browse..."},
		{"SCHEMA_ROOT_ELEMENT_CHOOSER_NONE_SELECTED_TEXT", "<none selected>"},
		
		
		// *** For SchemaRootElementChooserDialog ***
		
		{"SCHEMA_ROOT_ELEMENT_CHOOSER_DIALOG.TITLE", "Choose Root Element"},
		{"SCHEMA_ROOT_ELEMENT_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_TITLE", "No Schema Context Specified"},
		{"SCHEMA_ROOT_ELEMENT_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_MESSAGE", "You must specify a schema context for the descriptor before choosing a root element."},
		
		// *** For SchemaComplexTypeChooser ***
		
		{"SCHEMA_COMPLEX_TYPE_CHOOSER_BROWSE_BUTTON","Browse..."},
		{"SCHEMA_COMPLEX_TYPE_CHOOSER_NONE_SELECTED_TEXT", "<none selected>"},
		
		
		// *** For SchemaCompexTypeChooserDialog ***
		
		{"SCHEMA_COMPLEX_TYPE_CHOOSER_DIALOG.TITLE", "Choose Complex Type"},
		{"SCHEMA_COMPLEX_TYPE_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_TITLE", "No Schema Context Specified"},
		{"SCHEMA_COMPLEX_TYPE_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_MESSAGE", "You must specify a schema context for the descriptor before choosing a root element type."},
		
		
		// *** For XpathChooser ***
		{"XPATH_LABEL", "&XPath:"},		// provided as convenience
		
		
		// *** For XpathChooserDialog ***
		
		{"XPATH_CHOOSER_DIALOG.TITLE", "Choose XPath"},
		{"XPATH_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_TITLE", "No Schema Context Specified"},
		{"XPATH_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_MESSAGE", "You must specify a schema context for the descriptor before choosing an XPath."},
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

