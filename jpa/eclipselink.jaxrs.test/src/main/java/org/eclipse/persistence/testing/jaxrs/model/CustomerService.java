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

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Stateless
@Remote
@Path("/customer_war")
public class CustomerService {

    @PersistenceContext(unitName = "jaxrs")
    EntityManager entityManager;

    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void create(Customer customer) {
        entityManager.persist(customer);
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{id}")
    public Customer read(@PathParam("id") long id) {
        return entityManager.find(Customer.class, id);
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void update(Customer customer) {
        entityManager.merge(customer);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        Customer customer = entityManager.find(Customer.class, id);
        if (null != customer) {
            entityManager.remove(customer);
        }
    }

    @SuppressWarnings("unchecked")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("findCustomerByCity/{city}")
    public List<Customer> findCustomerByCity(@PathParam("city") String city) {
        Query query = entityManager.createNamedQuery("findCustomerByCity");
        //query.setParameter("city", "Ottawa");
        query.setParameter("city", city);
        return query.getResultList();
    }

}
