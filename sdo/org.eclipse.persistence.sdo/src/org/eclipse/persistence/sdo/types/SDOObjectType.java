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

import commonj.sdo.Type;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOObjectType extends SDOType implements Type {

    private static final String ANY_MAPPING_ATTRIBUTE_NAME = "openContentProperties";
    private static final String ANY_MAPPING_GET_METHOD_NAME = "_getOpenContentPropertiesWithXMLRoots";
    private static final String ANY_MAPPING_SET_METHOD_NAME = "_setOpenContentPropertiesWithXMLRoots";

    public SDOObjectType(String aUri, String aName, SDOTypeHelper sdoTypeHelper) {
        super(aUri, aName, sdoTypeHelper);
    }

    @Override
    public boolean isDataType() {
        return false;
    }

    @Override
    public boolean isSequenced() {
        return xmlDescriptor.isSequencedObject();
    }

    @Override
    public void setSequenced(boolean isSequenced) {
        xmlDescriptor.setSequencedObject(isSequenced);
    }

    @Override
    public Object getPseudoDefault() {
        return null;
    }

    protected void addOpenMappings() {
        XMLAnyCollectionMapping anyMapping = new XMLAnyCollectionMapping();
        anyMapping.setAttributeName(ANY_MAPPING_ATTRIBUTE_NAME);
        anyMapping.setGetMethodName(ANY_MAPPING_GET_METHOD_NAME);
        anyMapping.setSetMethodName(ANY_MAPPING_SET_METHOD_NAME);
        anyMapping.setUseXMLRoot(true);
        getXmlDescriptor().addMapping(anyMapping);

        XMLAnyAttributeMapping anyAttrMapping = new XMLAnyAttributeMapping();
        anyAttrMapping.setAttributeName("openContentPropertiesAttributes");
        anyAttrMapping.setGetMethodName("_getOpenContentPropertiesAttributesMap");
        anyAttrMapping.setSetMethodName("_setOpenContentPropertiesAttributesMap");
        getXmlDescriptor().addMapping(anyAttrMapping);
    }

}
