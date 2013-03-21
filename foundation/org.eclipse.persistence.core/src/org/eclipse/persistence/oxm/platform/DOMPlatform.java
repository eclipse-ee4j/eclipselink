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

import org.eclipse.persistence.internal.oxm.record.DOMUnmarshaller;
import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>This class indicates that DOM parsing should be used when appropriate in an
 *  XML project to create XMLRecords.
 *  <b>Responsibilities:</b><ul>
 *  <li>Extend XMLPlatform</li>
 *  <li>Overrides newPlatformUnmarshaller to return an instance of DOMUnmarshaller</li>
 *  </ul>
 *  
 *  @author  mmacivor
 *  @see org.eclipse.persistence.internal.oxm.record.DOMUnmarshaller
 *  @see org.eclipse.persistence.oxm.record.DOMRecord
 */
public class DOMPlatform extends XMLPlatform {

    /**
     * INTERNAL:
     */
    public PlatformUnmarshaller newPlatformUnmarshaller(XMLUnmarshaller xmlUnmarshaller) {
        return new DOMUnmarshaller(xmlUnmarshaller, null);
    }

    /**
     * INTERNAL:
     */
    public PlatformUnmarshaller newPlatformUnmarshaller(XMLUnmarshaller xmlUnmarshaller, Map<String, Boolean> parserFeatures) {
        return new DOMUnmarshaller(xmlUnmarshaller, parserFeatures);
    }

}