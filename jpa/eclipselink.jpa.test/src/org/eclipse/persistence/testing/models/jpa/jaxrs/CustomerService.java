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

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Remote
@Path("/customer_war")
public class CustomerService {

	@PersistenceContext(unitName = "jaxrs")
	EntityManager entityManager;

	@POST
	@Consumes(MediaType.APPLICATION_XML)
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
	@Consumes(MediaType.APPLICATION_XML)
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
	@Produces(MediaType.APPLICATION_XML)
	@Path("findCustomerByCity/{city}")
	public List<Customer> findCustomerByCity(@PathParam("city") String city) {
		Query query = entityManager.createNamedQuery("findCustomerByCity");
		//query.setParameter("city", "Ottawa");
		query.setParameter("city", city);
		return query.getResultList();
	}

}