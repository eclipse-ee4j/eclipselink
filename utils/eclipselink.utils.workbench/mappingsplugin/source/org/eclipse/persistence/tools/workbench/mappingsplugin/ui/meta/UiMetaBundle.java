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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import java.util.ListResourceBundle;

public class UiMetaBundle 
	extends ListResourceBundle 
{
	static final Object[][] contents = {
		
		// ExternalClassLoadFailuresDialog
		{"ERROR_IMPORTING_CLASSES.TITLE",                         "Error Loading Classes"},
		{"CLASSES_COMPILED_AND_ON_CLASSPATH_ERROR_MESSAGE",       "The following classes could not be loaded because they could not be found on the project classpath. Please ensure that all classes are compiled and on the project classpath."},
		{"ERROR_IMPORTING_CLASSES_ERROR_MESSAGE_CLASS_NOT_FOUND", "The class could not be refreshed. Either it is not on the classpath or there was a problem reading the class file."},
		{"ERROR_IMPORTING_CLASSES_ERROR_MESSAGE_INTERFACE",       "The class was imported as an interface."},
		{"ERROR_IMPORTING_CLASSES_ERROR_MESSAGE_IO",              "The class file could not be read: {0}"},

		{"<noneSelected>", "<none selected>"},
		{"addSelectedItems", "Add Selected Items"},  
		{"allClasses", "All Project Classes"},
		{"alreadyExists","Already Exists"},
		{"always", "Always"},
		{"availableItems", "Available Items"},
		{"availableItems*", "Available Items:"},
		{"cancel", "Cancel"},
		{"cannotBeAnInterface.", "The type you choose must not be an interface."},
		{"changedClasses", "Changed Classes Only"},	
		{"chooseDirectory", "Choose a Directory:"},
		{"chooseAnItem", "Choose an Item"},
		{"delete", "Delete"},
		{"directory","Directory: "},
		{"homeDirectory","Home Directory"},
		{"inputText", "Input Text:"},
		{"inputText*", "Input Text"},
		{"javaException", "Java Exception"},
		{"list", "List:"},
		{"lookIn","Look in:   "},
		{"mapping","EclipseLink Workbench"},
		{"message", "Message:"},
		{"mustBeAnInterface.", "The type you choose must be an interface."},
		{"name", "Name:"},
		{"never", "Never"},
		{"newFolder", "New Folder"},
		{"no", "No"},
		{"onlyTheCheckListItemsAreEditable.", "Only the check list items are editable."},
		{"prompt", "Prompt First"},
		{"removeSelectedItems", "Remove Selected Items"}, 
		{"selectAll", "Select All"},
		{"selectedItem", "Selected Item:"},
		{"selectedItems", "Selected Items"},
		{"save", "Save"},
		{"save_dir", "Save Directory"},
		{"saveAbort","Save Aborted"},
		{"saveIn", "Save In:"},
		{"stackTrace", "Stack Trace:"},
		{"tableGenerationCompleted", "Table Generation Completed"},
		{"temporary", "Temporary"},
		{"throwableClassName", "Throwable Class Name:"},
		{"unableToCreate","Cannot create folder here."},
		{"warningNotAnInterface", "Warning: Not an interface."},
		{"warningNotAClass", "Warning: Not a class."},
		{"yes", "Yes"},
		{"youAreAttempting","You are attempting to save in an invalid location!"},
		

	};
	
	public Object[][] getContents() {
		return contents;
	}  
}
