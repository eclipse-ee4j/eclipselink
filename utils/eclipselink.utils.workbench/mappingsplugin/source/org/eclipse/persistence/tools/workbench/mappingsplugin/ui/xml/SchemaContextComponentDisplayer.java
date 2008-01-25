/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;


abstract class SchemaContextComponentDisplayer
{
	static String displayString(ResourceRepository resources, MWSchemaContextComponent component) {
		if (component == null) {
			return resources.getString("SCHEMA_CONTEXT_CHOOSER_NONE_SELECTED_TEXT");
		}
		else {
			String displayString = "";
			
			for (Iterator stream = component.namedComponentChain(); stream.hasNext(); ) {
				MWNamedSchemaComponent nextComponent = (MWNamedSchemaComponent) stream.next();
				displayString = 
					"/" + nextComponent.componentTypeName() 
					+ "::" + nextComponent.qName() 
					+ displayString;
			}
			
			return "schema::" + component.getSchema().getName() + "/" + displayString;
		}
	}
}
