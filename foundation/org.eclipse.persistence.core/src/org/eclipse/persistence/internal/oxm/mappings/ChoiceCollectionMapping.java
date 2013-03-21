/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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

    public void addChoiceElement(List<XML_FIELD> srcFields, String elementTypeName, List<XML_FIELD> tgtFields);

    public void addChoiceElement(String xpath, String elementTypeName);

    public void addChoiceElement(String srcXpath, String elementTypeName, String tgtXpath);
    
    public void addChoiceElement(XML_FIELD field, String elementTypeName);

    public void addConverter(XML_FIELD field, CONVERTER converter);
    
    public Map<XML_FIELD, XML_MAPPING> getChoiceElementMappings();

    public Map<Class, XML_MAPPING> getChoiceElementMappingsByClass();

    public List<XMLChoiceFieldToClassAssociation> getChoiceFieldToClassAssociations();
    
    public Map<String, XML_FIELD> getClassNameToFieldMappings();
    
    public Map<Class, XML_FIELD> getClassToFieldMappings();

    public Map<Class, List<XML_FIELD>> getClassToSourceFieldsMappings();
    
    public CONVERTER getConverter();
    
    public CONVERTER getConverter(XML_FIELD field);
    
    public Map<XML_FIELD, Class> getFieldToClassMappings();
    
    public XML_MAPPING getMixedContentMapping();
    
    public boolean isMixedContent();
    
    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(CONVERTER converter);
    
    public void setIsWriteOnly(boolean b);

    /**
     * Allows the user to indicate that this mapping should also allow for mixed content in addition to 
     * any of the elements in the choice. 
     * @since EclipseLink 2.3.1
     */
    public void setMixedContent(boolean mixed);
    
    /**
     * Allows the user to indicate that this mapping should also allow for mixed content in addition to 
     * any of the elements in the choice. The grouping element parameter is used in the case that there is
     * a common grouping element to all the other elements in this choice. If so, that grouping element can
     * be specified here to allow the mixed content to be written/detected inside the wrapper element.
     * @since EclipseLink 2.3.1
     */
    public void setMixedContent(String groupingElement);
    
    public void useCollectionClassName(String concreteContainerClassName);

}