/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

/**
 * <p><b>Purpose</b>: Optionally user can implement this interface to provide their own extra content
 * to the generated source files.
 */
public interface SDOClassGeneratorListener {

    /**
    * This event will be triggered before the package declaration on the interface
    * @param buffer The current StringBuffer that can have content appended to it
    */
    void preInterfacePackage(StringBuffer buffer);

    /**
     * This event will be triggered before the package declaration on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    void preImplPackage(StringBuffer buffer);

    /**
     * This event will be triggered before the import declarations on the interface
     * @param buffer The current StringBuffer that can have content appended to it
     */
    void preInterfaceImports(StringBuffer buffer);

    /**
     * This event will be triggered before the import declarations on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    void preImplImports(StringBuffer buffer);

    /**
     * This event will be triggered before the interface declaration on the interface
     * @param buffer The current StringBuffer that can have content appended to it
     */
    void preInterfaceClass(StringBuffer buffer);

    /**
     * This event will be triggered before the class declaration on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    void preImplClass(StringBuffer buffer);

    /**
     * This event will be triggered before the attribute declarations on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    void preImplAttributes(StringBuffer buffer);
}
