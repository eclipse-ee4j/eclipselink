/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.*;
import java.net.URI;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * ORM.xml reader.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappingsWriter {
    public XMLEntityMappingsWriter() {}

    /**
     * INTERNAL:
     */
    public static void write(XMLEntityMappings entityMappings, URI uri) {
        Writer writer;
        try {
        	FileOutputStream outputStream = new FileOutputStream(new File(uri));
	    	writer = new OutputStreamWriter(outputStream, "UTF-8");
            write(entityMappings, writer);
            writer.close();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * INTENAL:
     */
    public static void write(XMLEntityMappings entityMappings, Writer writer) {
        XMLContext context = new XMLContext(new XMLEntityMappingsMappingProject(XMLEntityMappingsReader.ECLIPSELINK_ORM_NAMESPACE, XMLEntityMappingsReader.ECLIPSELINK_ORM_XSD));
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.setSchemaLocation(XMLEntityMappingsReader.ECLIPSELINK_ORM_XSD);
        marshaller.marshal(entityMappings, writer);
        
        try {
            writer.flush();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
}
