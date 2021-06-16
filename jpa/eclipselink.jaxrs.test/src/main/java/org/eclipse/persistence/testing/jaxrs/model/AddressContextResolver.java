/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.4 - added JSON support testing
package org.eclipse.persistence.testing.jaxrs.model;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

@Provider
//@Produces("application/xml")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AddressContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext jc;

    public AddressContextResolver() {
        try {
            Map<String, Object> props = new HashMap<String, Object>(1);
            props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, "META-INF/binding-address.xml");
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
