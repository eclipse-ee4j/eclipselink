/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;

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
    public static void write(SessionConfigs eclipseLinkSessions, String fileName) {
        //Bug#4305370 Needs to be utf-8 encoded.
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8")) {
            write(eclipseLinkSessions, writer);
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
    public static void write(SessionConfigs eclipseLinkSessions, Writer writer) {
        XMLContext context = new XMLContext(new XMLSessionConfigProject_11_1_1());
        XMLMarshaller marshaller = context.createMarshaller();

        // this is throwing a null pointer exception right now, bug entered
        //marshaller.setNoNamespaceSchemaLocation("eclipse_persistence_sessions_1_0.xsd");
        marshaller.marshal(eclipseLinkSessions, writer);

        try {
            writer.flush();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
}
