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
