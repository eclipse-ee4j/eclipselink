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
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

public interface DirectCollectionMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    SESSION extends CoreSession,
    UNMARSHALLER extends Unmarshaller,
    XML_RECORD extends XMLRecord> extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLContainerMapping, XMLConverterMapping<MARSHALLER, SESSION, UNMARSHALLER> {

 	 /**
     * Return the class each element in the object's
     * collection should be converted to, before the collection
     * is inserted into the object.
     * This is optional - if left null, the elements will be added
     * to the object's collection unconverted.
     */
    public Class getAttributeElementClass();
    	
    public AbstractNullPolicy getNullPolicy();
    
    /**
     * Return the converter on the mapping.
     * A converter can be used to convert between the direct collection's object value and database value.
     */
    public CONVERTER getValueConverter();

    public boolean isCDATA();

    public boolean isCollapsingStringValues();

    public boolean isNormalizingStringValues();
    
    /**
     * Set the class each element in the object's
     * collection should be converted to, before the collection
     * is inserted into the object.
     * This is optional - if left null, the elements will be added
     * to the object's collection unconverted.
     */
    public void setAttributeElementClass(Class attributeElementClass);
    
    /**
     * Indicates that this mapping should collapse all string values before adding them
     * to the collection on unmarshal. Collapse removes leading and trailing whitespaces, and replaces
     * any sequence of whitespace characters with a single space.
     * @param normalize
     */
    public void setCollapsingStringValues(boolean collapse);
    
    /**
     * Set the field that holds the nested collection.
     */
    public void setField(FIELD field);

	/**
      * PUBLIC:
      * Set the class each element in the database row's
      * collection should be converted to, before the collection
      * is inserted into the database.
      * This is optional - if left null, the elements will be added
      * to the database row's collection unconverted.
      */
     public void setFieldElementClass(Class fieldElementClass);
	
    public void setIsCDATA(boolean CDATA);
    
    public void setIsWriteOnly(boolean b);
    
    /**
     * Indicates that this mapping should normalize all string values before adding them
     * to the collection on unmarshal. Normalize replaces any CR, LF or Tab characters with a
     * single space character.
     * @param normalize
     */
    public void setNormalizingStringValues(boolean normalize);
    
    public void setNullPolicy(AbstractNullPolicy nullPolicyFromProperty);
    
    /** 
      * Sets whether the mapping uses a single node.
      * @param True if the items in the collection are in a single node or false if each of the items in the collection is in its own node
      */
     public void setUsesSingleNode(boolean usesSingleNode);
    
    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the direct collection's object value and database value.
     */
    public void setValueConverter(CONVERTER valueConverter);
     
     /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    public void setXPath(String xpathString);
     
     public void useCollectionClassName(String concreteContainerClassName);
     
     /**
     * Checks whether the mapping uses a single node.
     *
     * @returns True if the items in the collection are in a single node or false if each of the items in the collection is in its own node.
     */
    public boolean usesSingleNode();

}