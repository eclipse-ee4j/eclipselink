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

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.delegates.SDODataFactoryDelegate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * This implementation of commonj.sdo.helper.DataFactory is responsible for
 * ensuring that newly created DataObjects are assigned a JAXB aware value store.
 * <pre>
 * Type customerType = jaxbHelperContext.getType(Customer.class);
 * DataObject customerDO = jaxbHelperContext.getDataFactory().create(customerType);
 * customerDO.set("first-name", "Jane");
 *
 * Customer customer = jaxbHelperContext.unwrap(customerDO);
 * customer.getFirstName();  // returns "Jane"
 * </pre>
 */
public class JAXBDataFactory extends SDODataFactoryDelegate {

    public JAXBDataFactory(HelperContext helperContext) {
        super(helperContext);
    }

    @Override
    public JAXBHelperContext getHelperContext() {
        return (JAXBHelperContext) super.getHelperContext();
    }

    /**
     * Return a new DataObject of the specified Type.  If a corresponding
     * class (based on XML schema information) exists in the JAXBContext,
     * then the returned DataObject will wrap an instance of that class.
     */
    @Override
    public DataObject create(Type type) {
        if(null == type) {
            return super.create(type);
        }
        SDODataObject dataObject = (SDODataObject) super.create(type);
        try {
            JAXBValueStore jpaValueStore = new JAXBValueStore(getHelperContext(), (SDOType) type);
            jpaValueStore.initialize(dataObject);
            dataObject._setCurrentValueStore(jpaValueStore);
            getHelperContext().putWrapperDataObject(jpaValueStore.getEntity(), dataObject);
        } catch(Exception e) {
            return super.create(type);
        }
        return dataObject;
    }

}
