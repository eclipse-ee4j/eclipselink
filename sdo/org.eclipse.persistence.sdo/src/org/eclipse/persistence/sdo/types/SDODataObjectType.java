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
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDODataObjectType extends SDOType implements Type {

    public SDODataObjectType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT, sdoTypeHelper);
        setInstanceClass(DataObject.class);
        setAbstract(true);
    }

    public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
    }

    public String getName() {
        return SDOConstants.DATAOBJECT;
    }

    public String getURI() {
        return SDOConstants.SDO_URL;
    }

    public boolean isAbstract() {
        return true;
    }

    public boolean isDataType() {
        return false;
    }

    public boolean isOpen() {
        return false;
    }

    public boolean isSequenced() {
        return false;
    }

    public boolean isDataObjectType() {
        return true;
    }

}