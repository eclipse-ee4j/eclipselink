/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.mappings.converters;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose: </b> Provides an implementation of XMLConverter to wrap/unwrap objects in an 
 * XMLRoot in order to capture element name information.
 * <p><b>Responsibilities</b>
 * <li>Wrap an object in an XMLRoot on unmarshal. Do any required conversions based on type for 
 * simple mappings.
 * <li>Unwrap an XMLRoot from the object and pass it along to be marshalled.
 * @see XMLConverter
 * @see Converter
 */
public class XMLRootConverter implements XMLConverter {
	private XPathFragment rootFragment;
	private XMLField associatedField;
	private DatabaseMapping mapping;
	
	public XMLRootConverter(XMLField associatedField) {
		this.associatedField = associatedField;
	}
	
	public Object convertDataValueToObjectValue(Object dataValue,
			Session session, XMLUnmarshaller unmarshaller) {
		return convertDataValueToObjectValue(dataValue, session);
	}

	public Object convertObjectValueToDataValue(Object objectValue,
			Session session, XMLMarshaller marshaller) {
		return convertObjectValueToDataValue(objectValue, session);
	}

	public Object convertDataValueToObjectValue(Object dataValue,
			Session session) {
		XMLRoot root = new XMLRoot();
		root.setLocalName(this.rootFragment.getLocalName());
		root.setNamespaceURI(this.rootFragment.getNamespaceURI());
		
		if(mapping.isAbstractDirectMapping()){
			if ((dataValue == null) || (dataValue.getClass() != mapping.getAttributeClassification())) {
				try {
					dataValue = session.getDatasourcePlatform().convertObject(dataValue, mapping.getAttributeClassification());
				} catch (ConversionException e) {
					throw ConversionException.couldNotBeConverted(this, mapping.getDescriptor(), e);
				}
			}
		}
		
		root.setObject(dataValue);
		
		return root;
	}

	public Object convertObjectValueToDataValue(Object objectValue,
			Session session) {
		if(objectValue instanceof XMLRoot) {
			return ((XMLRoot)objectValue).getObject();
		} else {
			return objectValue;
		}
	}

	public void initialize(DatabaseMapping mapping, Session session) {
		XPathFragment fragment = associatedField.getXPathFragment();
		while(fragment.getNextFragment() != null && !(fragment.getNextFragment().nameIsText())) {
			fragment = fragment.getNextFragment();
		}
		if(fragment.hasNamespace() && associatedField.getNamespaceResolver() != null){
			String uri = associatedField.getNamespaceResolver().resolveNamespacePrefix(fragment.getPrefix());
			fragment.setNamespaceURI(uri);
		}
		this.rootFragment = fragment;
		this.mapping = mapping;
	}

	public boolean isMutable() {
		return false;
	}

}
