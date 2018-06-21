/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
