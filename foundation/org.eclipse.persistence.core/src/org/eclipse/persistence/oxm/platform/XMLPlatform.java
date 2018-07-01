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
package org.eclipse.persistence.oxm.platform;

import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.record.PlatformUnmarshaller;
import org.eclipse.persistence.internal.oxm.XMLUnmarshaller;

public abstract class XMLPlatform<XML_UNMARSHALLER extends XMLUnmarshaller> extends DatasourcePlatform implements org.eclipse.persistence.internal.oxm.record.XMLPlatform<XML_UNMARSHALLER> {
    public ConversionManager getConversionManager() {
        // Lazy init for serialization.
        if (conversionManager == null) {
            //Clone the default to allow customers to easily override the conversion manager
            conversionManager = (XMLConversionManager)XMLConversionManager.getDefaultXMLManager().clone();
        }
        return conversionManager;
    }

    /**
     * INTERNAL:
     */
    public abstract PlatformUnmarshaller newPlatformUnmarshaller(XML_UNMARSHALLER xmlUnmarshaller);

    /**
     * INTERNAL:
     */
    @Override
    public abstract PlatformUnmarshaller newPlatformUnmarshaller(XML_UNMARSHALLER xmlUnmarshaller, Map<String, Boolean> parserFeatures);

}
