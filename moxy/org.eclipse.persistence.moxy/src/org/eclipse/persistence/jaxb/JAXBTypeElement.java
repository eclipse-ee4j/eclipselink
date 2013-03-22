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
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * <p>Subclass of JAXBElement to allow the use of ParameterizedTypes.</p>
 * <p>Used as a wrapper object just as JAXBElement would be used during
 * JAXB marshal operations.</p>
 */
public class JAXBTypeElement extends JAXBElement {
	private Type type;

	/**
	 * Create a new JAXBTypeElement.
	 * @param name QName representing the xml element tag name
	 * @param value  Object representing the value of an xml element.
	 * @param type ParameterizedType associated with this JAXBTypeElement.
	 */	
	public JAXBTypeElement(QName name, Object value, ParameterizedType type) {
		super(name, ((Class)type.getRawType()), value);
		this.type = type;
	}	

	/**
	 * Create a new JAXBTypeElement.
	 * @param name QName representing the xml element tag name
	 * @param value Object representing the value of an xml element.
	 * @param type Class associated with this JAXBTypeElement.
	 */
	public JAXBTypeElement(QName name, Object value, Class type) {
		super(name, type, value);
		this.type = type;
	}
	
	/**
	 * Get the Type associated with this JAXBTypeElement
	 * @return the Type associated with this JAXBTypeElement.
	 */
	public Type getType() {
		return type;
	}

    /**
     * Set the Type associated with this JAXBTypeElement
     * @param type to associate with this JAXBTypeElement
     */
    public void setType(Type type) {
       this.type = type;
    }
}
