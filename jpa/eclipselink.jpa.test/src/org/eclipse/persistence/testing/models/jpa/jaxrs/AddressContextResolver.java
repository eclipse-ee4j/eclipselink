/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

@Provider
@Produces("application/xml")
public class AddressContextResolver implements ContextResolver<JAXBContext> {

	private JAXBContext jc;

	public AddressContextResolver() {
		try {
			Map<String, Object> props = new HashMap<String, Object>(1);
			props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
					"META-INF/binding-address.xml");
			jc = JAXBContext.newInstance(new Class[] { Address.class }, props);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public JAXBContext getContext(Class<?> clazz) {
		if (Address.class == clazz) {
			return jc;
		}
		return null;
	}

}