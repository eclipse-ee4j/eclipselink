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
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOTypeType extends SDOType implements Type {

    private static final String SDO_DO_URL = "org.eclipse.persistence.sdo.dataobjects";

    private boolean initialized = false;

    public SDOTypeType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.SDO_URL, SDOConstants.TYPE, sdoTypeHelper);

        setImplClassName(SDO_DO_URL + ".TypeImpl");
        Class implClass = getImplClass();

        xmlDescriptor.setJavaClass(implClass);
        xmlDescriptor.setInstantiationPolicy(new TypeInstantiationPolicy(this));
        xmlDescriptor.setDefaultRootElement("sdo:type");

        XMLSchemaReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/sdo:Type");
        xmlDescriptor.setSchemaReference(schemaReference);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(SDOConstants.SDO_PREFIX, SDOConstants.SDO_URL);
        SDOType propertyType = new SDOPropertyType(sdoTypeHelper, this);
        sdoTypeHelper.addType(propertyType);

        // these properties are ordered as listed page 74 sect. 8.3 of the spec in "SDO Model for Types and Properties"

        SDOProperty baseTypeProperty = new SDOProperty(aHelperContext);
        baseTypeProperty.setName("baseType");
        baseTypeProperty.setMany(true);
        baseTypeProperty.setType(this);
        baseTypeProperty.setContainment(true);
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
        setFinalized(true);
    }

    public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
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

    public void initializeMappings() {
        Iterator propIterator = this.getDeclaredProperties().iterator();
        while(propIterator.hasNext()) {
            SDOProperty nextProp = (SDOProperty)propIterator.next();
            nextProp.buildMapping(SDOConstants.SDO_URL);
        }
        initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

}