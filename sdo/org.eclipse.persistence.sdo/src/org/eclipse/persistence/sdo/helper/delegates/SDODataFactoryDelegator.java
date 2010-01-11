/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     dmccann - Nov 4/2008 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.delegates;

import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * <p><b>Purpose</b>: Helper to provide access to SDO Data Factory.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Finds the appropriate SDODataFactoryDelegate for the classLoader/application name and delegates work to it
 * <li> Create DataObjects for given uri/typename pairs, interface class or type
 * </ul>
 */
public class SDODataFactoryDelegator extends AbstractHelperDelegator implements SDODataFactory {

    public SDODataFactoryDelegator() {
    }

    public SDODataFactoryDelegator(HelperContext aContext) {
        aHelperContext = aContext;
    }

    public DataObject create(String uri, String typeName) {
        return getDataFactoryDelegate().create(uri, typeName);
    }

    public DataObject create(Class interfaceClass) {
        return getDataFactoryDelegate().create(interfaceClass);
    }

    public DataObject create(Type type) {
        return getDataFactoryDelegate().create(type);
    }

    public SDODataFactoryDelegate getDataFactoryDelegate() {
        return (SDODataFactoryDelegate) SDOHelperContext.getHelperContext().getDataFactory();
    }

}
