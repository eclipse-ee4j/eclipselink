/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import java.io.*;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;

/**
 * INTERNAL:
 * This class is used by the Mapping Workbench Session Configuration to write the session config
 * to XML.
 */
public class XMLSessionConfigWriter {
    public XMLSessionConfigWriter() {
        super();
    }

    /**
     * Given the file name (including path), and a SessionConfigs,
     * this writes out the session XML file.
     *
     * @param fileName file to write to (including path)
     * @param eclipseLinkSessions the SessionConfigs instance to write
     */
    public static void write(SessionConfigs toplinkSessions, String fileName) {
        Writer writer;
        try {
			//Bug#4305370 Needs to be utf-8 encoded.
	    	writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            write(toplinkSessions, writer);
            writer.close();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * Given the writer, and a SessionConfigs,
     * this writes out the session XML file.
     *
     * @param writer writer to writer to
     * @param eclipseLinkSessions the SessionConfigs instance to write
     */
    public static void write(SessionConfigs toplinkSessions, Writer writer) {
        XMLContext context = new XMLContext(new XMLSessionConfigProject_11_1_1());
        XMLMarshaller marshaller = context.createMarshaller();

        // this is throwing a null pointer exception right now, bug entered
        //marshaller.setNoNamespaceSchemaLocation("eclipse_persistence_sessions_1_0.xsd");
        marshaller.marshal(toplinkSessions, writer);

        try {
            writer.flush();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
}
