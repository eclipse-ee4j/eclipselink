/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm.platform;

import java.util.Map;

import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.internal.oxm.record.SAXUnmarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

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
    public PlatformUnmarshaller newPlatformUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
        return new SAXUnmarshaller(xmlUnmarshaller, null);
    }

    /**
     * INTERNAL:
     */
    public PlatformUnmarshaller newPlatformUnmarshaller(XMLUnmarshaller xmlUnmarshaller, Map<String, Boolean> parserFeatures) {
        return new SAXUnmarshaller(xmlUnmarshaller, parserFeatures);
    }

}
