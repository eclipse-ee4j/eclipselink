/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings.converters;

import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Session;


/**
 *  @version $Header: XMLConverter.java 09-aug-2007.15:24:25 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public interface XMLConverter extends Converter {
    Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller);
    Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller);

}