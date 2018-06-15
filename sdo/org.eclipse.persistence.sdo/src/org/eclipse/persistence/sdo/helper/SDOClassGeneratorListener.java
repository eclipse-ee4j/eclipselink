/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
    public void preInterfacePackage(StringBuffer buffer);

    /**
     * This event will be triggered before the package declaration on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    public void preImplPackage(StringBuffer buffer);

    /**
     * This event will be triggered before the import declarations on the interface
     * @param buffer The current StringBuffer that can have content appended to it
     */
    public void preInterfaceImports(StringBuffer buffer);

    /**
     * This event will be triggered before the import declarations on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    public void preImplImports(StringBuffer buffer);

    /**
     * This event will be triggered before the interface declaration on the interface
     * @param buffer The current StringBuffer that can have content appended to it
     */
    public void preInterfaceClass(StringBuffer buffer);

    /**
     * This event will be triggered before the class declaration on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    public void preImplClass(StringBuffer buffer);

    /**
     * This event will be triggered before the attribute declarations on the implementation class
     * @param buffer The current StringBuffer that can have content appended to it
     */
    public void preImplAttributes(StringBuffer buffer);
}
