/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOTypeType extends SDOType implements Type {

    private static final List EMPTY_LIST = new ArrayList(0);
    
    public SDOTypeType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.SDO_URL, SDOConstants.TYPE, sdoTypeHelper);

        SDOType propertyType = new SDOPropertyType(sdoTypeHelper, this);
        sdoTypeHelper.addType(propertyType);
        
        // these properties are ordered as listed page 74 sect. 8.3 of the spec in "SDO Model for Types and Properties"
        
        SDOProperty baseTypeProperty = new SDOProperty(aHelperContext);
        baseTypeProperty.setName("baseType");
        baseTypeProperty.setMany(true);
        baseTypeProperty.setType(this);
        addDeclaredProperty(baseTypeProperty);

        SDOProperty propertiesProperty = new SDOProperty(aHelperContext);
        propertiesProperty.setName("property");
        propertiesProperty.setMany(true);
        propertiesProperty.setContainment(true);
        propertiesProperty.setType(propertyType);
        addDeclaredProperty(propertiesProperty);

        SDOProperty typeAliasNameProperty = new SDOProperty(aHelperContext);
        typeAliasNameProperty.setName("aliasName");
        typeAliasNameProperty.setMany(true);
        typeAliasNameProperty.setType(SDOConstants.SDO_STRING);
        addDeclaredProperty(typeAliasNameProperty);

        SDOProperty nameProperty = new SDOProperty(aHelperContext);
        nameProperty.setName("name");
        nameProperty.setType(SDOConstants.SDO_STRING);
        addDeclaredProperty(nameProperty);

        SDOProperty uriProperty = new SDOProperty(aHelperContext);
        uriProperty.setName("uri");
        uriProperty.setType(SDOConstants.SDO_STRING);
        addDeclaredProperty(uriProperty);

        SDOProperty dataTypeProperty = new SDOProperty(aHelperContext);
        dataTypeProperty.setName("dataType");
        dataTypeProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(dataTypeProperty);

        SDOProperty openProperty = new SDOProperty(aHelperContext);
        openProperty.setName("open");
        openProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(openProperty);

        SDOProperty sequencedProperty = new SDOProperty(aHelperContext);
        sequencedProperty.setName("sequenced");
        sequencedProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(sequencedProperty);

        SDOProperty abstractProperty = new SDOProperty(aHelperContext);
        abstractProperty.setName("abstract");
        abstractProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(abstractProperty);
        
        // set the XMLAnyCollectionMapping on the descriptor on SDO_TYPE
        setOpen(true);        
    }
    
    public List getAliasNames() {
        return EMPTY_LIST;
    }

    public List getBaseTypes() {
        return EMPTY_LIST;
    }

    public String getName() {
        return SDOConstants.TYPE;
    }

    public String getURI() {
        return SDOConstants.SDO_URL;
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
        return false;
    }
    
    public boolean isTypeType() {
        return true;
    }

}