/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.platform;

import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.internal.oxm.record.SAXUnmarshaller;
import org.eclipse.persistence.oxm.XMLContext;

/**
 *  @version 1.0
 *  @author  mmacivor
 *  @since   10.1.3
 *  This class is used to indicate that SAX parsing should be used to create an XML
 *  Record when appropriate.
 */
public class SAXPlatform extends XMLPlatform {

    /**
     * INTERNAL:
     */
    public PlatformUnmarshaller newPlatformUnmarshaller(XMLContext xmlContext) {
        return new SAXUnmarshaller(xmlContext);
    }
}