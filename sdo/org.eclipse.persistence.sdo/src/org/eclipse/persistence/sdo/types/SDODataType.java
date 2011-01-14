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

import commonj.sdo.Type;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;;

public class SDODataType extends SDOType implements Type {

    private Object pseudoDefault;

    public SDODataType(String aUri, String aName, SDOTypeHelper sdoTypeHelper) {
        super(aUri, aName, sdoTypeHelper, null);
        isDataType = true;
    }

    public SDODataType(String aUri, String aName, Class aClass, SDOTypeHelper sdoTypeHelper) {
        this(aUri, aName, sdoTypeHelper);
        setInstanceClass(aClass);
    }

    public SDODataType(String aUri, String aName, Class aClass, SDOTypeHelper sdoTypeHelper, Object aPseudoDefault) {
        this(aUri, aName, aClass, sdoTypeHelper);
        this.pseudoDefault = aPseudoDefault;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isDataType() {
        return true;
    }

    public boolean isInstance(Object object) {
        return getInstanceClass().isInstance(object);
    }

    public boolean isOpen() {
        return false;
    }

    public boolean isSequenced() {
        return false;
    }

    public Object getPseudoDefault() {
        return pseudoDefault;
    }

    protected void addOpenMappings() {
    }

    public void setOpen(boolean bOpen) {
        if(bOpen) {
            throw SDOException.typeCannotBeOpenAndDataType(getURI(), getName());
        }
    }

}