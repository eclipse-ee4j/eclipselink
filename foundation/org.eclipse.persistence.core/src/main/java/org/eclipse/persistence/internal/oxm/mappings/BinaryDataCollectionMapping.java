/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.internal.oxm.mappings.MimeTypePolicy;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

public interface BinaryDataCollectionMapping<
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
    XML_RECORD extends XMLRecord> extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLContainerMapping, XMLConverterMapping<MARSHALLER, SESSION, UNMARSHALLER> {

    Class getAttributeElementClass();

    /**
     * INTERNAL
     */
    String getMimeType();

    String getMimeType(Object object);

    MIME_TYPE_POLICY getMimeTypePolicy();

    AbstractNullPolicy getNullPolicy();

    boolean isSwaRef();

    boolean isWriteOnly();

    /**
     * Set the class each element in the object's
     * collection should be converted to, before the collection
     * is inserted into the object.
     * This is optional - if left null, the elements will be added
     * to the object's collection unconverted.
     */
    void setAttributeElementClass(Class attributeElementClass);

    /**
     * ADVANCED:
     * Set the field in the mapping.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    void setField(FIELD theField);

    void setIsWriteOnly(boolean b);


    /**
     * Allow implementer to set the MimeTypePolicy class FixedMimeTypePolicy or AttributeMimeTypePolicy (dynamic)
     * @param aPolicy MimeTypePolicy
     */
    void setMimeTypePolicy(MIME_TYPE_POLICY aPolicy);

    /**
     * Set the AbstractNullPolicy on the mapping<br>
     * The default policy is NullPolicy.<br>
     *
     * @param aNullPolicy
     */
    void setNullPolicy(AbstractNullPolicy aNullPolicy);

    void setShouldInlineBinaryData(boolean b);

    void setSwaRef(boolean swaRef);
    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the direct collection's object value and database value.
     */
    void setValueConverter(CONVERTER valueConverter);

    boolean shouldInlineBinaryData();

    void useCollectionClassName(String concreteContainerClassName);
}
