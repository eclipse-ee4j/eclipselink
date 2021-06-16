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
