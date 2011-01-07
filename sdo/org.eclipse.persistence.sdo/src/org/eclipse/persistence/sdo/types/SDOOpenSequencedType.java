/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* bdoughan - May 6/2008 - 1.0M7 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.types;

import java.util.Collections;
import java.util.List;
import commonj.sdo.Type;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.dataobjects.OpenSequencedType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOOpenSequencedType extends SDOType implements Type {

    private static final String ORACLE_SDO_DO_URL = "org.eclipse.persistence.sdo.dataobjects";

    public SDOOpenSequencedType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.ORACLE_SDO_URL, "OpenSequencedType", sdoTypeHelper);
        this.xmlDescriptor.setNamespaceResolver(new NamespaceResolver());
        this.xmlDescriptor.setInstantiationPolicy(new TypeInstantiationPolicy(this));

        setInstanceClass(OpenSequencedType.class);
        setImplClassName(ORACLE_SDO_DO_URL + ".OpenSequencedTypeImpl");
        Class implClass = getImplClass();
        xmlDescriptor.setJavaClass(implClass);

        setMixed(true);
        setSequenced(true);
        setOpen(true);

        setFinalized(true);
    }

    public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
    }

    public String getName() {
        return "OpenSequencedType";
    }

    public String getURI() {
        return SDOConstants.ORACLE_SDO_URL;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isDataType() {
        return false;
    }

    public boolean isOpen() {
        return true;
    }

    public boolean isSequenced() {
        return true;
    }

    public boolean isOpenSequencedType() {
        return false;
    }

}