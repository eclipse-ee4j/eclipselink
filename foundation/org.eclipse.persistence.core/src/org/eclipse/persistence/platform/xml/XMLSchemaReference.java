/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.xml;


// JDK imports
import java.net.URL;

/**
 * A schema reference is used to access a schema in order to validate a
 * document.
 */
public interface XMLSchemaReference {
    int COMPLEX_TYPE = 1;
    int SIMPLE_TYPE = 2;
    int ELEMENT = 3;
    int GROUP = 5;

    /**
     * Returns the path to be traversed for validation purposes.
     *
     * @return a string represented the path to be traversed
     */
    String getSchemaContext();

    /**
     * Indicates if the schema reference references a simple type definition,
     * complex type definition, element or group
     *
     * @return COMPLEX_TYPE=1, SIMPLE_TYPE=2, ELEMENT=3, GROUP=5
     */
    int getType();

    /**
     * A URL which referenes the Schema.
     *
     * @return the schema URL
     */
    URL getURL();
}
