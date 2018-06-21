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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.mappings.converters;

import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose</b>: Conversion interface to allow conversion between object and data types.
 * This can be used in any mapping to convert between the object and data types without requiring code
 * placed in the object model. This extension of the Converter interface allows for the XMLMarshaller
 * and XMLUnmarshaller to be passed into the conversion methods.
 *
 * @see Converter
 * @see org.eclipse.persistence.oxm.mappings.XMLDirectMapping XMLDirectMapping
 * @see org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping XMLCompositeDirectCollectionMapping
 * @see org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping XMLCompositeObjectMapping
 * @see org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping XMLCompositeCollectionMapping
 *
 */

public interface XMLConverter extends Converter {
    Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller);
    Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller);

}
