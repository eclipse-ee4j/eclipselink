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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for SessionLoaderExceptionResource messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: Gordon Yorke
 */
public class SessionLoaderExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "9000", "Several [{0}] SessionLoaderExceptions were thrown:" },
                                           { "9001", "Unknown tag name: [{0}] in XML node: [{1}]." },
                                           { "9002", "Unable to load Project class [{0}]." },
                                           { "9003", "Unable to process XML tag [{0}] with value [{1}]." },
                                           { "9004", "The project-xml file [{0}] was not found on the classpath, nor on the filesystem." },
                                           { "9005", "An exception was thrown while loading the project-xml file [{0}]." },
                                           
    { "9006", "A {0} was thrown while parsing the XML file.  It occurs at line {1} and column {2} in the XML document." },
                                           { "9007", "An exception was thrown while parsing the XML file." },
                                           
    { "9008", "Unexpected value [{0}] of tag [{1}]." },
                                           { "9009", "Tag [{0}] has unknown attribute." },
                                           { "9010", "A {0} was thrown while parsing the XML file against the XML schema." },
                                           { "9011", "Server platform class {0} has been removed and the corresponding application server version is no longer supported" },
                                           { "9012", "Unable to load session-xml file either because it contains invalid format or the format of XML is not supported." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
