/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SchemaResolver;

public class CyclicSchemaResolver extends DefaultSchemaResolver {
    Map processedSchemas;

    public CyclicSchemaResolver() {
        processedSchemas = new HashMap();
    }

    public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
        Object value = processedSchemas.get(namespace);
        try {
            if (value == null) {
                //process
                Source processedSource = super.resolveSchema(sourceXSD, namespace, schemaLocation);
                processedSchemas.put(namespace, processedSource);
                return processedSource;
            } else {
                //already been processed
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}