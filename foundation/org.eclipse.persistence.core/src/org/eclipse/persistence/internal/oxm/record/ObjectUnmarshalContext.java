/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * An implementation of UnmarshalContext for handling plain old java objects that
 * are mapped to XML. 
 */
public class ObjectUnmarshalContext implements UnmarshalContext {

    private static final ObjectUnmarshalContext INSTANCE = new ObjectUnmarshalContext();

    public static ObjectUnmarshalContext getInstance() {
        return INSTANCE;
    }
    
    public void startElement(UnmarshalRecord unmarshalRecord) {
    }

    public void characters(UnmarshalRecord unmarshalRecord) {
    }
    
    public void endElement(UnmarshalRecord unmarshalRecord) {
    }

    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, Mapping mapping) {
        mapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), value);
    }
    
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value) {
        Object collection = unmarshalRecord.getContainerInstance(containerValue);
        addAttributeValue(unmarshalRecord, containerValue, value, collection);
    }

    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value, Object collection) {
        containerValue.getContainerPolicy().addInto(value, collection, unmarshalRecord.getSession());
    }
    
    public void reference(Reference reference) {
    }

    public void unmappedContent(UnmarshalRecord unmarshalRecord) {
    }

}
