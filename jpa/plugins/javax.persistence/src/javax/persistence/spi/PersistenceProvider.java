/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence API 2.0 Public Draft
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/

package javax.persistence.spi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * Interface implemented by a persistence provider. The implementation of this
 * interface that is to be used for a given
 * {@link javax.persistence.EntityManager} is specified in persistence.xml file
 * in the persistence archive. This interface is invoked by the Container when
 * it needs to create an {@link javax.persistence.EntityManagerFactory}, or by
 * the Persistence class when running outside the Container.
 * 
 * @since Java Persistence API 1.0
 */
public interface PersistenceProvider {

	/**
	 * Called by Persistence class when an EntityManagerFactory is to be
	 * created.
	 * 
	 * @param emName
	 *            The name of the persistence unit
	 * @param map
	 *            A Map of properties for use by the persistence provider. These
	 *            properties may be used to override the values of the
	 *            corresponding elements in the persistence.xml file or specify
	 *            values for properties not specified in the persistence.xml
	 *            (and may be null if no properties are specified).
	 * @return EntityManagerFactory for the persistence unit, or null if the
	 *         provider is not the right provider
	 */
	public EntityManagerFactory createEntityManagerFactory(String emName, Map map);

	/**
	 * Called by the container when an EntityManagerFactory is to be created.
	 * 
	 * @param info
	 *            Metadata for use by the persistence provider
	 * @return EntityManagerFactory for the persistence unit specified by the
	 *         metadata
	 * @param map
	 *            A Map of integration-level properties for use by the
	 *            persistence provider (may be null if no properties are
	 *            specified).
	 */
	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map);
}
