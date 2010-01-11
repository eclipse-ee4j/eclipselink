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
package org.eclipse.persistence.sdo.helper;

/**
 * <p><b>Purpose</b>:This interface can be implemented and used in conjunction with SDOClassGenerator
 * to write the generated source as desired.
 * @see org.eclipse.persistence.sdo.helper.SDOClassGenerator
 * @see org.eclipse.persistence.sdo.helper.FileCodeWriter
 */
public interface CodeWriter {

    /**
     * <p>Called from org.eclipse.persistence.sdo.helper.SDOClassGenerator for each generated interface if a
     * CodeWriter was passed into the generate method.
     *
     * @param dir The directory corresponding to the package of the generated source file
     * @param filename The name of the generated source file including the .java extension
     * @param content StringBuffer containing the contents of the generated interface.
     */
    public void writeInterface(String dir, String filename, StringBuffer content);

    /**
      * <p>Called from org.eclipse.persistence.sdo.helper.SDOClassGenerator for each generated interface if a
      * CodeWriter was passed into the generate method.
      *
      * @param dir The directory corresponding to the package of the generated source file
      * @param filename The name of the generated source file including the .java extension
      * @param content StringBuffer containing the contents of the generated implementation class.
      */
    public void writeImpl(String dir, String filename, StringBuffer content);
}
