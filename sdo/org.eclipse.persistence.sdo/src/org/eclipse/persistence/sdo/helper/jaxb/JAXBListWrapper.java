/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Jan 27/2009 - 1.1 - Initial implementation
*     bdoughan - Mar 31/2009 - 2.0 - Majority of behaviour ported to JAXBList
*     bdoughan - May 19/2010 - 2.1 - Added support for inverse references
******************************************************************************/
package org.eclipse.persistence.sdo.helper.jaxb;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.ListWrapper;

import commonj.sdo.DataObject;

public class JAXBListWrapper extends ListWrapper {

    private JAXBValueStore jaxbValueStore;
    private XMLInverseReferenceMapping xmlInverseReferenceMapping;

    public JAXBListWrapper(JAXBValueStore aJAXBValueStore, SDOProperty aProperty) {
        super(aJAXBValueStore.getDataObject(), aProperty);
        jaxbValueStore = aJAXBValueStore;
        currentElements = new JAXBList(aJAXBValueStore, aProperty);
        xmlInverseReferenceMapping = null;
        DatabaseMapping jaxbMapping = jaxbValueStore.getJAXBMappingForProperty(property);
        if(jaxbMapping.isAbstractCompositeCollectionMapping()) {
            XMLCompositeCollectionMapping xmlCompositeCollectionMapping = (XMLCompositeCollectionMapping) jaxbMapping;
            xmlInverseReferenceMapping = xmlCompositeCollectionMapping.getInverseReferenceMapping();
        } else if(jaxbMapping.isReferenceMapping()) {
            XMLCollectionReferenceMapping xmlCollectionReferenceMapping = (XMLCollectionReferenceMapping) jaxbMapping;
            xmlInverseReferenceMapping = xmlCollectionReferenceMapping.getInverseReferenceMapping();
        }
    }

    @Override
    protected void copyElements() {
        if (isLogging() && (!dataObject.getChangeSummary().isDirty(this))) {
            dataObject.getChangeSummary().getOriginalElements().put(this, new ArrayList(currentElements));
        } 
    }

    @Override
    public JAXBList getCurrentElements() {
        return (JAXBList) currentElements;
    }

    @Override
    protected void updateContainment(Object item, boolean updateSequence) {
        super.updateContainment(item, updateSequence);
        if(null != xmlInverseReferenceMapping) {
            xmlInverseReferenceMapping.setAttributeValueInObject(jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) item), jaxbValueStore.getEntity());
        }
    }

    @Override
    protected void updateContainment(Collection items, boolean updateSequence) {
        super.updateContainment(items, updateSequence);
        if(null != xmlInverseReferenceMapping) {
            for(Object item : items) {
                xmlInverseReferenceMapping.setAttributeValueInObject(jaxbValueStore.getJAXBHelperContext().unwrap((DataObject) item), jaxbValueStore.getEntity());
            }
        }
    }

}