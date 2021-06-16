/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - August 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.oxm;

import jakarta.xml.bind.JAXBElement;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.XMLConverterMapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * Allow the unmarshal context to be wrapped.  This is necessary so that choice
 * mappings with a converter can convert the result of the nested mapping.
 */
public class ChoiceUnmarshalContext implements UnmarshalContext {

    private UnmarshalContext unmarshalContext;
    private XMLConverterMapping converter;

    public ChoiceUnmarshalContext(UnmarshalContext unmarshalContext, XMLConverterMapping converter) {
        this.unmarshalContext = unmarshalContext;
        this.converter = converter;
    }

    @Override
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value) {
        this.unmarshalContext.addAttributeValue(unmarshalRecord, containerValue, getValue(value, unmarshalRecord));
    }

    @Override
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value, Object collection) {
        this.unmarshalContext.addAttributeValue(unmarshalRecord, containerValue, getValue(value, unmarshalRecord), collection);
    }

    @Override
    public void characters(UnmarshalRecord unmarshalRecord) {
        unmarshalContext.characters(unmarshalRecord);
    }

    @Override
    public void endElement(UnmarshalRecord unmarshalRecord) {
        unmarshalContext.endElement(unmarshalRecord);
    }

    @Override
    public void reference(Reference reference) {
        unmarshalContext.reference(reference);
    }

    @Override
    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, Mapping mapping) {
        unmarshalContext.setAttributeValue(unmarshalRecord, getValue(value, unmarshalRecord), mapping);
    }

    @Override
    public void startElement(UnmarshalRecord unmarshalRecord) {
        unmarshalContext.startElement(unmarshalRecord);
    }

    @Override
    public void unmappedContent(UnmarshalRecord unmarshalRecord) {
        unmarshalContext.unmappedContent(unmarshalRecord);
    }

    protected Object getValue(Object value, UnmarshalRecord unmarshalRecord) {
        Object converted = converter.convertDataValueToObjectValue(value, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
        if (converted instanceof JAXBElement<?>) {
            ((JAXBElement<?>)converted).setNil(unmarshalRecord.isNil());
        }
        return converted;
    }

}
