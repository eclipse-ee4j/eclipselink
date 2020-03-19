/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxrs.model;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@Path("/phonenumber_war")
public class PhoneNumberService {

    @PersistenceContext(unitName = "jaxrs")
    EntityManager entityManager;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public void create(PhoneNumber phoneNumber) {
        entityManager.persist(phoneNumber);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id}")
    public PhoneNumber read(@PathParam("id") long id) {
        return entityManager.find(PhoneNumber.class, id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void update(PhoneNumber phoneNumber) {
        entityManager.merge(phoneNumber);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        PhoneNumber phoneNumber = entityManager.find(PhoneNumber.class, id);
        if (null != phoneNumber) {
            entityManager.remove(phoneNumber);
        }
    }

}
