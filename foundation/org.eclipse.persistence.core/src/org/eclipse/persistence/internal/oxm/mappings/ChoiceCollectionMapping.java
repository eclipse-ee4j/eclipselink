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

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XMLChoiceFieldToClassAssociation;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;

public interface ChoiceCollectionMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    SESSION extends CoreSession,
    UNMARSHALLER extends Unmarshaller,
    XML_FIELD extends Field,
    XML_MAPPING extends Mapping,
    XML_RECORD extends XMLRecord
    > extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLContainerMapping, XMLConverterMapping<MARSHALLER, SESSION, UNMARSHALLER> {

    void addChoiceElement(List<XML_FIELD> srcFields, String elementTypeName, List<XML_FIELD> tgtFields);

    void addChoiceElement(String xpath, String elementTypeName);

    void addChoiceElement(String srcXpath, String elementTypeName, String tgtXpath);

    void addChoiceElement(XML_FIELD field, String elementTypeName);

    void addConverter(XML_FIELD field, CONVERTER converter);

    Map<XML_FIELD, XML_MAPPING> getChoiceElementMappings();

    Map<Class, XML_MAPPING> getChoiceElementMappingsByClass();

    List<XMLChoiceFieldToClassAssociation> getChoiceFieldToClassAssociations();

    Map<String, XML_FIELD> getClassNameToFieldMappings();

    Map<Class, XML_FIELD> getClassToFieldMappings();

    Map<Class, List<XML_FIELD>> getClassToSourceFieldsMappings();

    CONVERTER getConverter();

    CONVERTER getConverter(XML_FIELD field);

    Map<XML_FIELD, Class> getFieldToClassMappings();

    XML_MAPPING getMixedContentMapping();

    boolean isMixedContent();

    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    void setConverter(CONVERTER converter);

    void setIsWriteOnly(boolean b);

    /**
     * Allows the user to indicate that this mapping should also allow for mixed content in addition to
     * any of the elements in the choice.
     * @since EclipseLink 2.3.1
     */
    void setMixedContent(boolean mixed);

    /**
     * Allows the user to indicate that this mapping should also allow for mixed content in addition to
     * any of the elements in the choice. The grouping element parameter is used in the case that there is
     * a common grouping element to all the other elements in this choice. If so, that grouping element can
     * be specified here to allow the mixed content to be written/detected inside the wrapper element.
     * @since EclipseLink 2.3.1
     */
    void setMixedContent(String groupingElement);

    void useCollectionClassName(String concreteContainerClassName);

    boolean isAny();

    AnyCollectionMapping getAnyMapping();

}
