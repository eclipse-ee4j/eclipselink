/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//      tware - initial
package org.eclipse.persistence.jpars.test.util;

public class XMLFilePathBuilder {

    public static final String DEFAULT_DIRECTORY = "classes/META-INF/xmldocs/";
    public static final String DIRECTORY_NAME_PROPERTY = "jpars.xmlfile.directory";

    public static String getXMLFileName(String documentName){
        String directory = System.getProperty(DIRECTORY_NAME_PROPERTY);
        if (directory == null){
            directory = DEFAULT_DIRECTORY;
        }
        return directory + documentName;
    }
}
