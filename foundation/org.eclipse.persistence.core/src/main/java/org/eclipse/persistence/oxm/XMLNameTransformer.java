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
//     Denise Smith - EclipseLink 2.3 - Initial Implementation
package org.eclipse.persistence.oxm;

/**
 * Interface used when converting from XML to Java names.
 */
public interface XMLNameTransformer {
    /**
     * Method called when creating a simpletype or complextype from a class
     * @param name - The fully qualified class name as taken from theClass.getName()
     */
    String transformTypeName(String name);

    /**
     * Method called when creating an element from a Java field or method
     * @param name - unmodified field name or if this was from a getter or setter method
     * the "get" or "set" will be automatically removed and the first letter will be made lowercase
     *
     * Example: if the method getFirstName was annotated with @XmlElement the name passed in to this method would be "firstName"
     */
    String transformElementName(String name);

    /**
     * Method called when creating an attribute from a Java field
     * @param name - attribute name from the class
     */
    String transformAttributeName(String name);

    /**
     * Method called when creating a simpletype or complextype from a class
     * @param name - The fully qualified class name as taken from theClass.getName()
     */
    String transformRootElementName(String name);

}
