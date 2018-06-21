/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
    public String transformTypeName(String name);

    /**
     * Method called when creating an element from a Java field or method
     * @param name - unmodified field name or if this was from a getter or setter method
     * the "get" or "set" will be automatically removed and the first letter will be made lowercase
     *
     * Example: if the method getFirstName was annotated with @XmlElement the name passed in to this method would be "firstName"
     */
    public String transformElementName(String name);

    /**
     * Method called when creating an attribute from a Java field
     * @param name - attribute name from the class
     */
    public String transformAttributeName(String name);

    /**
     * Method called when creating a simpletype or complextype from a class
     * @param name - The fully qualified class name as taken from theClass.getName()
     */
    public String transformRootElementName(String name);

}
