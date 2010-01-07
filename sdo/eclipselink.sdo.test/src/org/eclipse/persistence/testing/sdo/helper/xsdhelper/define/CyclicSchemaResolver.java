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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;


import java.net.URI;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;

public class CyclicSchemaResolver extends DefaultSchemaResolver {
	public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
		if (schemaLocation != null && !schemaLocation.equals("")) {
			return super.resolveSchema(sourceXSD, namespace, schemaLocation);
		}
    	schemaLocation = namespace.equals("uri") ? "Cyclic1.xsd" : "Cyclic2.xsd"; 
        URL schemaUrl = null;
        try {
	        if (getBaseSchemaLocation() != null) {
            	// Attempt to resolve the schema location against the base location
                URI baseUri = new URI(getBaseSchemaLocation());
                URI resolvedUri = baseUri.resolve(schemaLocation);
                schemaUrl = resolvedUri.toURL();
	        } else {
            	schemaUrl = new URL(schemaLocation);
	        }
        } catch (Exception e) {        
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "sdo_error_processing_referenced_schema", new Object[] {e.getClass().getName(), namespace, schemaLocation });
            AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.FINEST, e);
            return null;
        }
        return new StreamSource(schemaUrl.toExternalForm());
    }
}
