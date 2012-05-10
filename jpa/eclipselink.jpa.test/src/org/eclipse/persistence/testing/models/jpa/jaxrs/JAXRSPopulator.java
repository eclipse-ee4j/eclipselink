/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - inital implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import java.util.Vector;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Address;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Customer;
import org.eclipse.persistence.testing.models.jpa.jaxrs.PhoneNumber;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class JAXRSPopulator {

	protected PopulationManager populationManager;

	public JAXRSPopulator() {
		this.populationManager = PopulationManager.getDefaultManager();
	}

	/**
	 * Call all of the example methods in this system to guarantee that all our
	 * objects are registered in the population manager
	 */
	public void buildExamples() {
		// First ensure that no previous examples are hanging around.
		PopulationManager.getDefaultManager().getRegisteredObjects()
				.remove(Address.class);
		PopulationManager.getDefaultManager().getRegisteredObjects()
				.remove(PhoneNumber.class);
		PopulationManager.getDefaultManager().getRegisteredObjects()
				.remove(Customer.class);

		PopulationManager.getDefaultManager().registerObject(Customer.class,
				JAXRSExamples.customerExample1(), "c1");
		PopulationManager.getDefaultManager().registerObject(Customer.class,
				JAXRSExamples.customerExample2(), "c2");
		PopulationManager.getDefaultManager().registerObject(Customer.class,
				JAXRSExamples.customerExample3(), "c3");

	}

	public void persistExample(Session session) {
		Vector allObjects = new Vector();
		UnitOfWork unitOfWork = session.acquireUnitOfWork();
		PopulationManager.getDefaultManager().addAllObjectsForClass(
				Address.class, allObjects);
		PopulationManager.getDefaultManager().addAllObjectsForClass(
				PhoneNumber.class, allObjects);
		PopulationManager.getDefaultManager().addAllObjectsForClass(
				Customer.class, allObjects);
		unitOfWork.registerAllObjects(allObjects);
		unitOfWork.commit();

	}

	protected boolean containsObject(Class domainClass, String identifier) {
		return populationManager.containsObject(domainClass, identifier);
	}

}
