/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.mappings;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;

public interface AnyObjectMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    SESSION extends CoreSession,
    UNMARSHAL_KEEP_AS_ELEMENT_POLICY extends UnmarshalKeepAsElementPolicy,
    UNMARSHALLER extends Unmarshaller,
    XML_RECORD extends XMLRecord> extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLConverterMapping<MARSHALLER, SESSION, UNMARSHALLER> {

    UNMARSHAL_KEEP_AS_ELEMENT_POLICY getKeepAsElementPolicy();

    boolean isMixedContent();

    void setConverter(CONVERTER converter);

    void setKeepAsElementPolicy(UNMARSHAL_KEEP_AS_ELEMENT_POLICY unmarshalKeepAsElementPolicy);

    void setField(FIELD field);

    void setIsWriteOnly(boolean b);

    void setMixedContent(boolean mixed);

    void setUseXMLRoot(boolean useXMLRoot);

    boolean usesXMLRoot();

}
