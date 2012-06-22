/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

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

    public void validate(XRServiceAdapter xrService, String operationName) {
        if (!type.getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
            if (!xrService.descriptorsByQName.containsKey(type)) {
                throw DBWSException.parameterHasNoMapping(type.toString(), operationName);
            }
        }
    }
}
