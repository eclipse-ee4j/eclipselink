/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.4 - added JSON support testing
package org.eclipse.persistence.testing.jaxrs.model;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Stateless
@Remote
@Path("/address_war")
public class AddressService {

    @PersistenceContext(unitName = "jaxrs")
    EntityManager entityManager;

    @POST
    //@Consumes(MediaType.APPLICATION_XML)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void create(Address address) {
        entityManager.persist(address);
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{id}")
    public Address read(@PathParam("id") long id) {
        return entityManager.find(Address.class, id);
    }

    @PUT
    //@Consumes(MediaType.APPLICATION_XML)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void update(Address address) {
        entityManager.merge(address);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        Address address = entityManager.find(Address.class, id);
        if (null != address) {
            entityManager.remove(address);
        }
    }

}
