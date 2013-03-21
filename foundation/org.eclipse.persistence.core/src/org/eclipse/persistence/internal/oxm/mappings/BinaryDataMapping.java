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

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.mappings.MimeTypePolicy;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

public interface BinaryDataMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    MIME_TYPE_POLICY extends MimeTypePolicy,
    SESSION extends CoreSession,
    UNMARSHALLER extends Unmarshaller,
    XML_RECORD extends XMLRecord> extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLConverterMapping<MARSHALLER, SESSION, UNMARSHALLER> {

    /**
     * INTERNAL
     */
    public String getMimeType();

    public String getMimeType(Object object);

    public AbstractNullPolicy getNullPolicy();

    public Object getObjectValue(Object object, SESSION session);

    /**
     * Get the XPath String
     * @return String the XPath String associated with this Mapping
     */
    public String getXPath();
    
    public boolean isSwaRef();
    
    /**
     * Some databases do not properly support all of the base data types. For these databases,
     * the base data type must be explicitly specified in the mapping to tell EclipseLink to force
     * the instance variable value to that data type
     */
    public void setAttributeClassification(Class attributeClassification);
    
    /**
     * INTERNAL:
     * Set the name of the class for MW usage.
     */
    public void setAttributeClassificationName(String attributeClassificationName);
    
    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(CONVERTER converter);
    
    /**
     * ADVANCED:
     * Set the field in the mapping.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    public void setField(FIELD theField);
    
    public void setIsWriteOnly(boolean b);
    
    /**
     * Force mapping to set default FixedMimeTypePolicy using the MimeType string as argument
     * @param mimeTypeString
     */
    public void setMimeType(String mimeTypeString);
    
    /**
     * Allow implementer to set the MimeTypePolicy class FixedMimeTypePolicy or AttributeMimeTypePolicy (dynamic)
     * @param aPolicy MimeTypePolicy
     */
    public void setMimeTypePolicy(MIME_TYPE_POLICY aPolicy);
    
    /**
     * Set the AbstractNullPolicy on the mapping<br>
     * The default policy is NullPolicy.<br>
     *
     * @param aNullPolicy
     */
    public void setNullPolicy(AbstractNullPolicy aNullPolicy);
    
    
    public void setShouldInlineBinaryData(boolean b);
    

    public void setSwaRef(boolean swaRef);
    
    /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    public void setXPath(String xpathString);
    
    public boolean shouldInlineBinaryData();
}