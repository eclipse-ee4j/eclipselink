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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common;

import java.util.ListResourceBundle;

public class UiCommonBundle 
	extends ListResourceBundle 
{
	static final Object[][] contents = 
	{	
		// *** ClassChooserTools ***
		{"CLASS_CHOOSER_INTERFACE_FILTER_WARNING", "The type you choose must be an interface."},
		{"CLASS_CHOOSER_INTERFACE_FILTER_WARNING.TITLE", "Warning: Not an interface"},
		{"CLASS_CHOOSER_NONINTERFACE_FILTER_WARNING", "The type you choose must not be an interface."},
		{"CLASS_CHOOSER_NONINTERFACE_FILTER_WARNING.TITLE", "Warning: Not a class"},
		{"CLASS_CHOOSER_MAP_FILTER_WARNING", "The type you choose must implement java.util.Map"},
		{"CLASS_CHOOSER_MAP_FILTER_WARNING.TITLE", "Warning: Does not implement Map"},
		{"CLASS_CHOOSER_COLLECTION_FILTER_WARNING", "The type you choose must implement java.util.Collection"},
		{"CLASS_CHOOSER_COLLECTION_FILTER_WARNING.TITLE", "Warning: Does not implement Collection"},
		
	
		// DescriptorCellRendererAdapter
		{"DESCRIPTOR_TYPE_AGGREGATE",            "Aggregate Descriptor - "},
		{"DESCRIPTOR_TYPE_EJB",                  "EJB Descriptor - "},
		{"DESCRIPTOR_TYPE_INTERFACE",            "Interface Descriptor - "},
		{"DESCRIPTOR_TYPE_RELATIONAL",           "Relational Descriptor - "},
		{"DESCRIPTOR_TYPE_ROOT_EIS",           	 "Root EIS Descriptor - "},
		{"DESCRIPTOR_TYPE_COMPOSITE_EIS",        "Composite EIS Descriptor - "},
		{"DESCRIPTOR_TYPE_OX",           		 "OX Descriptor - "},

		// ProjectCellRendererAdapter
		{"ACCESSIBLE_EIS_PROJECT_NODE",        "EIS Project {0}"},
		{"ACCESSIBLE_RELATIONAL_PROJECT_NODE", "Relational Project {0}"},
		{"ACCESSIBLE_OX_PROJECT_NODE",         "XML Project {0}"},

		// ReferenceCellRendererAdapter
		{"REFERENCE_SIGNATURE", "{0} ({1} -> {2})"},
				

		// MappingCellRendererAdapter - based on the icon key
		{"mapping.directCollection",  "Direct Collection Mapping"},
		{"mapping.oneToOne",          "One To One Mapping"},
		{"mapping.oneToMany",         "One To Many Mapping"},
		{"mapping.manyToMany",        "Many To Many Mapping"},
		{"mapping.aggregate",         "Aggregate Mapping"},
		{"mapping.transformation",    "Transformation Mapping"},
		{"mapping.variableOneToOne",  "Variable One To One Mapping"},
		{"mapping.objectType",        "Object Type Mapping"},
		{"mapping.serialized",        "Serialized Mapping"},
		{"mapping.typeConversion",    "Type Conversion Mapping"},

		// StatusDialog
		{"STATUS_DIALOG_MESSAGE",     "&Status:"},
		
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

	};
	
	public Object[][] getContents() 
	{
		return contents;
	}  
}
