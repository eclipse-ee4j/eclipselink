/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.mappings;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.record.XMLRecord;

public interface ChoiceObjectMapping<
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    ABSTRACT_SESSION extends CoreAbstractSession,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    SESSION extends CoreSession> extends Mapping<ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD>, XMLConverterMapping<SESSION> {

    public Map<XMLField, XMLMapping> getChoiceElementMappings();

    public Map<Class, XMLMapping> getChoiceElementMappingsByClass();

    public Map<Class, XMLField> getClassToFieldMappings();

    public Map<Class, List<XMLField>> getClassToSourceFieldsMappings();

    public CONVERTER getConverter();

    public Object getFieldValue(Object object, ABSTRACT_SESSION session, XMLRecord marshalRecord);

    public List<FIELD> getFields();

}