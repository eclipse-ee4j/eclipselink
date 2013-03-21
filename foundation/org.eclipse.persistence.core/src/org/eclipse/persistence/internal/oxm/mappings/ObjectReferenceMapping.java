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
    public void addSourceToTargetKeyFieldAssociation(String srcXPath, String tgtXPath);

    public Object buildFieldValue(Object targetObject, XML_FIELD xmlField, ABSTRACT_SESSION session);

    public void buildReference(UNMARSHAL_RECORD unmarshalRecord, XML_FIELD xmlField, Object realValue, ABSTRACT_SESSION session);

    public List<FIELD> getFields();

    public InverseReferenceMapping getInverseReferenceMapping();

    public Class getReferenceClass();

    /**
     * INTERNAL:
     */
    public String getReferenceClassName();

    /**
     * Return a list of source-target xmlfield pairs.
     */
    public Map getSourceToTargetKeyFieldAssociations();
    
    public boolean isWriteOnly();
    
    public void setIsWriteOnly(boolean b);
    
    public void setReferenceClassName(String aClassName);
}