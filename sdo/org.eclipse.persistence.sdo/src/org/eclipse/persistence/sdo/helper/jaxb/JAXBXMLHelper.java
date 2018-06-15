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
//     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.sdo.helper.jaxb;

import commonj.sdo.helper.HelperContext;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegate;

/**
 * This implementation of commonj.sdo.helper.XMLHelper is responsible for
 * ensuring that newly unmarshalled DataObjects are assigned a JAXB aware
 * value store.
 * <pre>
 * XMLHelper xmlHelper = jaxbHelperContext.getXMLHelper();
 * XMLDocument xmlDocument = xmlHelper.load(xml);
 * DataObject customerDO = xmlDocument.getRootObject();
 *
 * Customer customer = jaxbHelperContext.unwrap(customerDO);
 * </pre>
 */
public class JAXBXMLHelper extends SDOXMLHelperDelegate {

    /**
     * Create a new instance of JAXBXMLHelper
     * @param helperContext - The HelperContext used to get this XMLHelper.
     */
    public JAXBXMLHelper(HelperContext helperContext) {
        super(helperContext);
    }

    /**
     * Create a new instance of JAXBXMLHelper
     * @param helperContext - The HelperContext used to get this XMLHelper.
     * @param classLoader - The ClassLoader containing the generated SDO classes/interfaces (if any).
     */
    public JAXBXMLHelper(HelperContext helperContext, ClassLoader classLoader) {
        super(helperContext, classLoader);
    }

    @Override
    public JAXBHelperContext getHelperContext() {
        return (JAXBHelperContext) super.getHelperContext();
    }

}
