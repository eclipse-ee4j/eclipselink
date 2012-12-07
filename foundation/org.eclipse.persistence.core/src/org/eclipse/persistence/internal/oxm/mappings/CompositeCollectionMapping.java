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

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

public interface CompositeCollectionMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    SESSION extends CoreSession,
    UNMARSHAL_KEEP_AS_ELEMENT_POLICY extends UnmarshalKeepAsElementPolicy,
    XML_RECORD extends XMLRecord> extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLContainerMapping, XMLConverterMapping<SESSION> {

    public InverseReferenceMapping getInverseReferenceMapping();

    public UNMARSHAL_KEEP_AS_ELEMENT_POLICY getKeepAsElementPolicy();

    public AbstractNullPolicy getNullPolicy();

    public Object getReferenceClassName();
    
    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(CONVERTER converter);
    
    public void setIsWriteOnly(boolean b);

    public void setKeepAsElementPolicy(UNMARSHAL_KEEP_AS_ELEMENT_POLICY keepAsElementPolicy);
	
    /**
     * Set the AbstractNullPolicy on the mapping<br>
     * The default policy is NullPolicy.<br>
     *
     * @param aNullPolicy
     */
	public void setNullPolicy(AbstractNullPolicy nullPolicyFromProperty);
    
    /**
     * This is a reference class whose instances this mapping will store in the domain objects.
     */
    public void setReferenceClass(Class aClass);
    
    public void setReferenceClassName(String aClassName);
    
    /**
     * Set the Mapping field name attribute to the given XPath String
     *
     * @param xpathString String
     *
     */
    public void setXPath(String xpathString);
    
    /**
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>jdk1.2.x: The container class must implement (directly or indirectly) the Collection interface.
     * <p>jdk1.1.x: The container class must be a subclass of Vector.
     */
    public void useCollectionClass(Class concreteContainerClass);
    
    public void useCollectionClassName(String concreteContainerClassName);


}