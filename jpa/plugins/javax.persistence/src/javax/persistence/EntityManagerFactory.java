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

package javax.persistence;

import java.util.Map;
import java.util.Set;

/**
 * The <code>EntityManagerFactory</code> interface is used to interact with the
 * entity manager factory for a persistence unit.
 * <p>
 * When the application has finished using the entity manager factory, and/or at
 * application shutdown, the application should close the entity manager
 * factory. Once an <code>EntityManagerFactory</code> has been closed, all its
 * entity managers are considered to be in the closed state.
 * 
 * @since Java Persistence API 1.0
 */
public interface EntityManagerFactory {

	/**
	 * Create a new EntityManager. This method returns a new EntityManager
	 * instance each time it is invoked. The isOpen method will return true on
	 * the returned instance.
	 */
	EntityManager createEntityManager();

	/**
	 * Create a new EntityManager with the specified Map of properties. This
	 * method returns a new EntityManager instance each time it is invoked. The
	 * isOpen method will return true on the returned instance.
	 */
	EntityManager createEntityManager(Map map);

	/**
	 * Return an instance of QueryBuilder for the creation of Criteria API
	 * QueryDefinition objects.
	 * 
	 * @return QueryBuilder instance
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed.
	 * @since Java Persistence API 2.0
	 */
	public QueryBuilder getQueryBuilder();

	/**
	 * Close the factory, releasing any resources that it holds. After a factory
	 * instance is closed, all methods invoked on it will throw an
	 * IllegalStateException, except for isOpen, which will return false. Once
	 * an EntityManagerFactory has been closed, all its entity managers are
	 * considered to be in the closed state.
	 */
	void close();

	/**
	 * Indicates whether or not this factory is open. Returns true until a call
	 * to close has been made.
	 */
	public boolean isOpen();

	/**
	 * Get the properties and associated values that are in effect for the
	 * entity manager factory. Changing the contents of the map does not change
	 * the configuration in effect.
	 * 
	 * @return properties
	 * @since Java Persistence API 2.0
	 */
	public Map getProperties();

	/**
	 * Get the names of the properties that are supported for use with the
	 * entity manager factory. These correspond to properties that may be passed
	 * to the methods of the EntityManagerFactory interface that take a
	 * properties argument. These include all standard properties as well as
	 * vendor-specific properties supported by the provider. These properties
	 * may or may not currently be in effect.
	 * 
	 * @return properties and hints
	 * @since Java Persistence API 2.0
	 */
	public Set<String> getSupportedProperties();

	/**
	 * Access the cache that is associated with the entity manager factory(the
	 * "second level cache").
	 * 
	 * @return instance of the Cache interface
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed.
	 * @since Java Persistence API 2.0
	 */
	public Cache getCache();
}
