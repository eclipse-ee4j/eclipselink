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

package org.eclipse.persistence.internal.xr;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// Javase imports

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.exceptions.DBWSException;

/**
 * <p><b>INTERNAL:</b>Name and type ({@link QName}) for a bound argument.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class Parameter {

    protected String name;
    protected QName type;
    protected boolean optional;

    /**
     * @return  name of bound argument
     */
    public String getName() {
        return name;
    }
    /**
     * Set name of bound argument
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  type ({@link QName}) of a bound argument
     */
    public QName getType() {
        return type;
    }
    /**
     * Set type of bound argument
     * @param type
     */
    public void setType(QName type) {
        this.type = type;
    }

    /**
     * @return  indicates if the argument is optional
     */
    public boolean isOptional() {
        return optional;
    }
    /**
     * Set the optional indicator
     * @param optional
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void validate(XRServiceAdapter xrService, String operationName) {
        if (!type.getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
            if (!xrService.descriptorsByQName.containsKey(type)) {
                throw DBWSException.parameterHasNoMapping(type.toString(), operationName);
            }
        }
    }
}
