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
// bdoughan - May 6/2008 - 1.0M7 - Initial implementation
package org.eclipse.persistence.sdo.types;

import java.util.Collections;
import java.util.List;
import commonj.sdo.Type;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.dataobjects.OpenSequencedType;
import org.eclipse.persistence.sdo.dataobjects.OpenSequencedTypeImpl;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOOpenSequencedType extends SDOType implements Type {

    public SDOOpenSequencedType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType", sdoTypeHelper);
        this.xmlDescriptor.setNamespaceResolver(new NamespaceResolver());
        this.xmlDescriptor.setInstantiationPolicy(new TypeInstantiationPolicy(this));

        setInstanceClass(OpenSequencedType.class);
        javaImplClass = OpenSequencedTypeImpl.class;
        xmlDescriptor.setJavaClass(javaImplClass);

        setMixed(true);
        setSequenced(true);
        setOpen(true);

        setFinalized(true);
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
        return "OpenSequencedType";
    }

    @Override
    public String getURI() {
        return SDOConstants.ORACLE_SDO_URL;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isDataType() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isSequenced() {
        return true;
    }

    @Override
    public boolean isOpenSequencedType() {
        return false;
    }

}
