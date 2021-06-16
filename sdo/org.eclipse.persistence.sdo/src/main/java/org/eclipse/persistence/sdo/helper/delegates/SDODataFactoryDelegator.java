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
//     dmccann - Nov 4/2008 - 1.1 - Initial implementation
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

    @Override
    public DataObject create(String uri, String typeName) {
        return getDataFactoryDelegate().create(uri, typeName);
    }

    @Override
    public DataObject create(Class interfaceClass) {
        return getDataFactoryDelegate().create(interfaceClass);
    }

    @Override
    public DataObject create(Type type) {
        return getDataFactoryDelegate().create(type);
    }

    public SDODataFactoryDelegate getDataFactoryDelegate() {
        return (SDODataFactoryDelegate) SDOHelperContext.getHelperContext().getDataFactory();
    }

}
