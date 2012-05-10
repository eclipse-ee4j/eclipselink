/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
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
