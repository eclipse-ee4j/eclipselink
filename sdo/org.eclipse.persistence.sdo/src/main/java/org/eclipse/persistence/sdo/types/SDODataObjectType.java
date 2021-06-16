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
// bdoughan - May 6/2008 - 1.0M7 - Initial implementation
package org.eclipse.persistence.sdo.types;

import java.util.Collections;
import java.util.List;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDODataObjectType extends SDOType implements Type {

    public SDODataObjectType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT, sdoTypeHelper);
        setInstanceClass(DataObject.class);
        javaImplClass = DataObject.class;
        xmlDescriptor.setJavaClass(javaImplClass);
        setAbstract(true);
    }

    @Override
    public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getName() {
        return SDOConstants.DATAOBJECT;
    }

    @Override
    public String getURI() {
        return SDOConstants.SDO_URL;
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isDataType() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isSequenced() {
        return false;
    }

    @Override
    public boolean isDataObjectType() {
        return true;
    }

}
