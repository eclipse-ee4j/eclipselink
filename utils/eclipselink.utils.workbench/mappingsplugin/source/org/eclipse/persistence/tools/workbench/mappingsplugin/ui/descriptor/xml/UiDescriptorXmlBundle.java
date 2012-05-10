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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.util.ListResourceBundle;

public class UiDescriptorXmlBundle 
	extends ListResourceBundle 
{
	static final Object[][] contents = 
	{
		// XMLDescriptorInfoPropertiesPage
		{"SCHEMA_CONTEXT_CHOOSER_LABEL", "Schema &Context:"},
		{"ANY_TYPE_DESCRIPTOR_CHECK_BOX", "Descriptor Represents Complex Type \"&anyType\""},
		{"DOCUMENT_ROOT_CHECK_BOX", "&Document Root"},
		{"DEFAULT_ROOT_ELEMENT_LABEL", "&Default Root Element:"},
		{"DEFAULT_ROOT_ELEMENT_TYPE_LABEL", "Default Root Element &Type:"},
		{"PRESERVE_DOCUMENT_CHECK_BOX", "&Preserve Document"},
		
		{"DESCRIPTOR_INFO_TAB.title", "Descriptor Info"},	
		{"EIS_PROPERTIES_TAB.title", "EIS Properties"},	
		{"EIS_DESCRIPTOR_QUERIES_TAB", "Queries"},
		{"XML_DESCRIPTOR_EVENTS_TAB", "Events"},
		{"XML_DESCRIPTOR_LOCKING_TAB", "Locking"},
		{"EIS_ROOT_DESCRIPTOR_CACHING_TAB" , "Caching"},
		{"EIS_ROOT_DESCRIPTOR_LOCKING_TAB" , "Locking"},
		{"EIS_ROOT_DESCRIPTOR_EJB_INFO_TAB", "EJB Info"},
		{"EIS_ROOT_DESCRIPTOR_RETURNING_TAB", "Returning"},

		// *** EisPrimaryKeysPanel ***
		{"PRIMARY_KEYS_PANEL.TITLE", "Primary Keys"},
		{"PRIMARY_KEYS_DIALOG.TITLE", "Select Primary Keys"},
	
		// EisLockingPolicyPropertiesPage
		{"EIS_LOCKING_POLICY_XPATH", "&XPath:"},		
		
		//EISRootDescriptorAction
		{"EIS_ROOT_DESCRIPTOR_ACTION" , "&Root"},
		{"EIS_ROOT_DESCRIPTOR_ACTION.toolTipText" , "Change Descriptor Type to Root EIS Descriptor"},
		
		//EISCompositeDescriptorAction
		{"EIS_COMPOSITE_DESCRIPTOR_ACTION" , "&Composite"},
		{"EIS_COMPOSITE_DESCRIPTOR_ACTION.toolTipText" , "Change Descriptor Type to Composite EIS Descriptor"},
		
		{"TRANSACTIONAL_DESCRIPTOR_EJB_POLICY_TOOLTIP", "Change Descriptor Type to EJB EIS Descriptor"},
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

