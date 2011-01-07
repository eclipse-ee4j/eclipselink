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
import java.util.Iterator;
import java.util.List;
import commonj.sdo.Type;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.metadata.TypeStringConverter;

public class SDOPropertyType extends SDOType implements Type {

    private static final String SDO_DO_URL = "org.eclipse.persistence.sdo.dataobjects";

    private boolean initialized = false;
    private SDOTypeHelper typeHelper;

    public SDOPropertyType(SDOTypeHelper sdoTypeHelper, SDOType typeType) {
        super(SDOConstants.SDO_URL, SDOConstants.PROPERTY, sdoTypeHelper);
        this.typeHelper = sdoTypeHelper;

        setImplClassName(SDO_DO_URL + ".PropertyImpl");
        Class implClass = getImplClass();

        xmlDescriptor.setJavaClass(implClass);
        xmlDescriptor.setInstantiationPolicy(new TypeInstantiationPolicy(this));

        XMLSchemaReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/sdo:Property");
        xmlDescriptor.setSchemaReference(schemaReference);
        // these properties are ordered as listed page 74 sect. 8.3 of the spec in "SDO Model for Types and Properties"
        SDOProperty aliasNameProperty = new SDOProperty(aHelperContext);
        aliasNameProperty.setName("aliasName");
        aliasNameProperty.setMany(true);
        aliasNameProperty.setType(SDOConstants.SDO_STRING);
        addDeclaredProperty(aliasNameProperty);

        SDOProperty propNameProperty = new SDOProperty(aHelperContext);
        propNameProperty.setName("name");
        propNameProperty.setType(SDOConstants.SDO_STRING);
        addDeclaredProperty(propNameProperty);

        SDOProperty manyProperty = new SDOProperty(aHelperContext);
        manyProperty.setName("many");
        manyProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(manyProperty);

        SDOProperty containmentProperty = new SDOProperty(aHelperContext);
        containmentProperty.setName("containment");
        containmentProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(containmentProperty);

        SDOProperty defaultProperty = new SDOProperty(aHelperContext);
        defaultProperty.setName("default");
        defaultProperty.setType(SDOConstants.SDO_OBJECT);
        addDeclaredProperty(defaultProperty);

        SDOProperty readOnlyProperty = new SDOProperty(aHelperContext);
        readOnlyProperty.setName("readOnly");
        readOnlyProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(readOnlyProperty);

        SDOProperty typeProperty = new SDOProperty(aHelperContext);
        typeProperty.setName("type");
        typeProperty.setType(typeType);
        typeProperty.setContainment(false);
        addDeclaredProperty(typeProperty);

        SDOProperty oppositeProperty = new SDOProperty(aHelperContext);
        oppositeProperty.setName("opposite");
        oppositeProperty.setType(this);
        addDeclaredProperty(oppositeProperty);

        SDOProperty nullableProperty = new SDOProperty(aHelperContext);
        nullableProperty.setName("nullable");
        nullableProperty.setType(SDOConstants.SDO_BOOLEAN);
        addDeclaredProperty(nullableProperty);

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
        return SDOConstants.PROPERTY;
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

    public void initializeMappings() {
        Iterator propIterator = this.getDeclaredProperties().iterator();
        while(propIterator.hasNext()) {
            SDOProperty nextProp = (SDOProperty)propIterator.next();
            if(nextProp.getName().equals("type")) {
                XMLDirectMapping mapping = new XMLDirectMapping();
                mapping.setAttributeName(nextProp.getName());
                String xpath = nextProp.getQualifiedXPath(SDOConstants.SDO_URL, true);
                mapping.setXPath(xpath);
                mapping.setConverter(new TypeStringConverter(this.typeHelper));
                if (getXsdType() != null) {
                    ((XMLField)mapping.getField()).setSchemaType(getXsdType());
                } 
                nextProp.setXmlMapping(mapping);
                nextProp.addMappingToOwner(true, -1);
                
            } else if(nextProp.getName().equals("opposite")) {
            } else {
                nextProp.buildMapping(SDOConstants.SDO_URL);
            }
        }
        initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

}