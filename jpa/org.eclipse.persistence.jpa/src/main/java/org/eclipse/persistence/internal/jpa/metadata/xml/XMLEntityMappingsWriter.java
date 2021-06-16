/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping file
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.*;

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
    public static void write(XMLEntityMappings entityMappings, OutputStream outputStream) {
        Writer writer;
        try {
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
        XMLContext context = XMLEntityMappingsReader.getEclipseLinkOrmProject();
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.setSchemaLocation(XMLEntityMappingsReader.ECLIPSELINK_ORM_NAMESPACE + " " + XMLEntityMappingsReader.ECLIPSELINK_ORM_XSD);
        marshaller.marshal(entityMappings, writer);

        try {
            writer.flush();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
}
