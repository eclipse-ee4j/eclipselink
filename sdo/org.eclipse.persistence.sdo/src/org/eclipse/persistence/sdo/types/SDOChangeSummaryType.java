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

import commonj.sdo.ChangeSummary;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class SDOChangeSummaryType extends SDOType implements Type {

    public SDOChangeSummaryType(SDOTypeHelper sdoTypeHelper) {
        super(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY, sdoTypeHelper);

        setInstanceClass(ChangeSummary.class);

        xmlDescriptor.setJavaClass(SDOChangeSummary.class);
        xmlDescriptor.setSequencedObject(false);
        
        XMLDirectMapping loggingMapping = new XMLDirectMapping();
        loggingMapping.setAttributeName("loggingMapping");
        loggingMapping.setXPath("@logging");
        loggingMapping.setNullValue(Boolean.TRUE);
        xmlDescriptor.addMapping(loggingMapping);

        XMLCompositeDirectCollectionMapping createdMapping = new XMLCompositeDirectCollectionMapping();
        createdMapping.setAttributeName("createdXPaths");
        createdMapping.setXPath("@create");
        createdMapping.useCollectionClass(ArrayList.class);
        ((XMLField)createdMapping.getField()).setUsesSingleNode(true);
        xmlDescriptor.addMapping(createdMapping);

        XMLCompositeDirectCollectionMapping deletedMapping = new XMLCompositeDirectCollectionMapping();
        deletedMapping.setAttributeName("deletedXPaths");
        deletedMapping.setXPath("@delete");
        deletedMapping.useCollectionClass(ArrayList.class);
        ((XMLField)deletedMapping.getField()).setUsesSingleNode(true);
        xmlDescriptor.addMapping(deletedMapping);

        XMLAnyCollectionMapping aChangeMapping = new XMLAnyCollectionMapping();
        aChangeMapping.setAttributeName("modifiedDoms");
        aChangeMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
        aChangeMapping.useCollectionClass(ArrayList.class);
        xmlDescriptor.addMapping(aChangeMapping);
    }

    public Object get(Property property) {
        return null;
    }

    public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
    }

    public List getDeclaredProperties() {
        return Collections.EMPTY_LIST;
    }

    public List getInstanceProperties() {
        return Collections.EMPTY_LIST;
    }

    public String getName() {
        return SDOConstants.CHANGESUMMARY;
    }

    public List getProperties() {
        return Collections.EMPTY_LIST;
    }

    public SDOProperty getProperty(String propertyName) {
        return null;
    }

    public String getURI() {
        return SDOConstants.SDO_URL;
    }

    public boolean isAbstract() {
        return true;
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

    public boolean isChangeSummaryType() {
        return true;
    }

}
