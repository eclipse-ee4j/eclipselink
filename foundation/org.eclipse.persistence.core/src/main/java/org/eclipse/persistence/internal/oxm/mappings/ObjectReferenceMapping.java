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

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;

public interface ObjectReferenceMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    UNMARSHAL_RECORD extends UnmarshalRecord,
    XML_FIELD extends Field,
    XML_RECORD extends XMLRecord
    > extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD> {
    /**
     * Add a source-target xpath pair to the map.
     *
     * @param srcXPath
     * @param tgtXPath
     */
    void addSourceToTargetKeyFieldAssociation(String srcXPath, String tgtXPath);

    Object buildFieldValue(Object targetObject, XML_FIELD xmlField, ABSTRACT_SESSION session);

    void buildReference(UNMARSHAL_RECORD unmarshalRecord, XML_FIELD xmlField, Object realValue, ABSTRACT_SESSION session);

    List<FIELD> getFields();

    InverseReferenceMapping getInverseReferenceMapping();

    Class getReferenceClass();

    /**
     * INTERNAL:
     */
    String getReferenceClassName();

    /**
     * Return a list of source-target xmlfield pairs.
     */
    Map getSourceToTargetKeyFieldAssociations();

    boolean isWriteOnly();

    void setIsWriteOnly(boolean b);

    void setReferenceClassName(String aClassName);
}
