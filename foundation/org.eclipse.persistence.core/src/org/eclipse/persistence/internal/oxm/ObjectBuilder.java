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
package org.eclipse.persistence.internal.oxm;

import java.util.List;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.TransformationMapping;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;

public interface ObjectBuilder<
    ABSTRACT_RECORD extends CoreAbstractRecord,
    ABSTRACT_SESSION extends CoreAbstractSession,
    DESCRIPTOR extends CoreDescriptor,
    MARSHALLER extends Marshaller> {

    boolean addClassIndicatorFieldToRow(AbstractMarshalRecord record);

    List addExtraNamespacesToNamespaceResolver(Descriptor desc, AbstractMarshalRecord marshalRecord, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers);

    Object buildNewInstance();

    XMLRecord buildRow(XMLRecord record, Object object, CoreAbstractSession session, MARSHALLER marshaller, XPathFragment rootFragment);

    Class classFromRow(UnmarshalRecord record, ABSTRACT_SESSION session);

    ABSTRACT_RECORD createRecord(ABSTRACT_SESSION session);

    Object extractPrimaryKeyFromObject(Object currentObject, ABSTRACT_SESSION session);

    List<ContainerValue> getContainerValues();

    List<ContainerValue> getDefaultEmptyContainerValues();

    DESCRIPTOR getDescriptor();

    List<NullCapableValue> getNullCapableValues();

    XPathNode getRootXPathNode();

    List<TransformationMapping> getTransformationMappings();

    boolean isXsiTypeIndicatorField();

    boolean marshalAttributes(MarshalRecord marshalRecord, Object object, CoreAbstractSession session);

}
