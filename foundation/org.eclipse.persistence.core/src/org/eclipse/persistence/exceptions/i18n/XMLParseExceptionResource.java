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

public class XMLParseExceptionResource extends ListResourceBundle {
 
	static final Object[][] contents = {
		{ "34000", "An exception occurred while attempting to parse entity-mappings file: {0}. A DocumentBuilder instance could not be created."},
		{ "34001", "An exception occurred while attempting to read entity-mappings file: {0}."},
		{ "34002", "An exception occurred while processing persistence.xml from URL: {0}. A SAXParser instance could not be created."},
		{ "34003", "An exception occurred while processing persistence.xml from URL: {0}. An XMLReader instance could not be created."},
		{ "34004", "An exception occurred while processing persistence.xml from URL: {0}. The schema source at URL: {1} could not be set."},
    };

    /**
      * Return the lookup table.
      */
    protected Object[][] getContents() {
        return contents;
    }
}
