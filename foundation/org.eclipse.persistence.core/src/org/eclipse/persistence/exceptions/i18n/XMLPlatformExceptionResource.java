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
 */
public class XMLPlatformExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "27001", "The XML Platform class was not found: {0}" },
                                           { "27002", "The XML Platform could not be instantiated: {0}" },
                                           { "27003", "A new XML document could not be created." },
                                           { "27004", "The XPath was invalid." },
                                           { "27005", "An error occurred while validating the document." },
                                           { "27006", "Could not resolve XML Schema:  {0}" },
                                           { "27101", "An error occurred while parsing the document." },
                                           { "27102", "File not found: [{0}]" },
                                           { "27103", "** Parsing error, line [{0}], uri [{1}] [{2}]" },
                                           { "27201", "An error occurred while transforming the document." },
                                           { "27202", "Unknown type encountered: {0}" },
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
