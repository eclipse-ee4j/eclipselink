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
package org.eclipse.persistence.internal.jaxb.many;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This abstract class is used to support JAXBContext creation with a Map class.
 * Classes which extend this class will be dynamically generated when a Map class is given to JAXBContext  
 */
@XmlTransient
public abstract class MapValue<T> extends ManyValue<Object, T> {

	@XmlTransient
	public boolean isArray(){
		return false;
	}
}
