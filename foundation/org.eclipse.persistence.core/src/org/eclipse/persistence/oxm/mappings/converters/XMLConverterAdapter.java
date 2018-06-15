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
package org.eclipse.persistence.oxm.mappings.converters;

import org.eclipse.persistence.sessions.Session;
/**
 *  @version $Header: XMLConverterAdapter.java 09-aug-2007.15:24:27 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public abstract class XMLConverterAdapter implements XMLConverter {
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return this.convertObjectValueToDataValue(objectValue, session, null);
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return this.convertDataValueToObjectValue(dataValue, session, null);
    }
}
