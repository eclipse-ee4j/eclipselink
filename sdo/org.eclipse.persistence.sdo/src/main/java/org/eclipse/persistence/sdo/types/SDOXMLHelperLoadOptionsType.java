/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// bdoughan - May 6/2008 - 1.0M7 - Initial implementation
package org.eclipse.persistence.sdo.types;

import java.util.Collections;
import java.util.List;
import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOXMLHelperLoadOptionsType extends SDOType implements Type {

    public SDOXMLHelperLoadOptionsType(SDOTypeHelper sdoTypeHelper, SDOType typeType) {
        super(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS, sdoTypeHelper);

        this.xmlDescriptor.setInstantiationPolicy(new TypeInstantiationPolicy(this));

        SDOProperty typeOptionProperty = new SDOProperty(aHelperContext);
        typeOptionProperty.setName(SDOConstants.TYPE_LOAD_OPTION);
        typeOptionProperty.setMany(false);
        typeOptionProperty.setType(typeType);
        addDeclaredProperty(typeOptionProperty);

        SDOProperty attachmentMarshallerProperty = new SDOProperty(aHelperContext);
        attachmentMarshallerProperty.setName(SDOConstants.ATTACHMENT_MARSHALLER_OPTION);
        attachmentMarshallerProperty.setMany(false);
        attachmentMarshallerProperty.setType(SDOConstants.SDO_OBJECT);
        addDeclaredProperty(attachmentMarshallerProperty);

        SDOProperty attachmentUnmarshallerProperty = new SDOProperty(aHelperContext);
        attachmentUnmarshallerProperty.setName(SDOConstants.ATTACHMENT_UNMARSHALLER_OPTION);
        attachmentUnmarshallerProperty.setMany(false);
        attachmentUnmarshallerProperty.setType(SDOConstants.SDO_OBJECT);
        addDeclaredProperty(attachmentUnmarshallerProperty);
    }

    @Override
    public List getAliasNames() {
        return Collections.emptyList();
    }

    @Override
    public List getBaseTypes() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return SDOConstants.XMLHELPER_LOAD_OPTIONS;
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
        return false;
    }

    @Override
    public boolean isSequenced() {
        return false;
    }

}
