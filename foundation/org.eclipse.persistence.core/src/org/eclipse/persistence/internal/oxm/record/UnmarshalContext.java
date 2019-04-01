/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * The UnmarshalContext allows mappings to be unmarshalled differently depending
 * on the type of object.  An UnmarshalRecord maintains a reference to a single
 * UnmarshalContext.
 */
public interface UnmarshalContext {

    /**
     * An event indicating that startElement has been called on the unmarshalRecord.
     * @param unmarshalRecord The UnmarshalRecord that received the startElement call.
     */
    void startElement(UnmarshalRecord unmarshalRecord);

    /**
     * An event indicating that characters has been called on the unmarshalRecord.
     * @param unmarshalRecord The UnmarshalRecord that received the characters call.
     */
    void characters(UnmarshalRecord unmarshalRecord);

    /**
     * An event indicating that endElement has been called on the unmarshalRecord.
     * @param unmarshalRecord The UnmarshalRecord that received the endElement call.
     */
    void endElement(UnmarshalRecord unmarshalRecord);

    /**
     * The UnmarshalContext is responsible for assigning values to the object being
     * built.
     * @param unmarshalRecord
     * @param value
     * @param mapping
     */
    void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, Mapping mapping);

    /**
     * When a collection mapping is processed the UnmarshalContext is responsible for
     * handling the values one at a time.
     * @param unmarshalRecord
     * @param containerValue A container object such as a java.util.ArrayList, to which
     * the value will be added.
     * @param value The value to be added to the container,
     */
    void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value);

    /**
     * When a collection mapping is processed the UnmarshalContext is responsible for
     * handling the values one at a time.
     * @param unmarshalRecord
     * @param containerValue A container object such as a java.util.ArrayList, to which
     * the value will be added.
     * @param value The value to be added to the container,
     * @param collection
     */
    void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value, Object collection);

    /*
     * When a Reference is built the UnmarshalContext is given the ability to perform
     * further processing on it.
     * @param reference
     */
    void reference(Reference reference);

    /**
     * This method is called when unmapped content (XML content that does not correspond to
     * any specified mapping, policy, etc.) is encountered during the unmarshal process.
     * @param unmarshalRecord The UnmarshalRecord that encountered the unmapped content .
     */
    void unmappedContent(UnmarshalRecord unmarshalRecord);

}
