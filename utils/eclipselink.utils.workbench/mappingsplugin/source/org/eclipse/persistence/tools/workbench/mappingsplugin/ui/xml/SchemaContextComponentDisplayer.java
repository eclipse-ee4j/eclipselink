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
