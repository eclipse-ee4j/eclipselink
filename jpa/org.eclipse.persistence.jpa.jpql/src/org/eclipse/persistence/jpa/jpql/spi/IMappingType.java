/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.spi;

/**
 * This enumeration lists the mapping types defined in the Java Persistence functional specification.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface IMappingType {

	/**
	 * The constant for a basic mapping.
	 */
	int BASIC = 1;

	/**
	 * The constant for an element collection mapping.
	 */
	int ELEMENT_COLLECTION = 2;

	/**
	 * The constant for an embedded mapping.
	 */
	int EMBEDDED = 3;

	/**
	 * The constant for an embedded ID mapping.
	 */
	int EMBEDDED_ID = 4;

	/**
	 * The constant for an ID mapping.
	 */
	int ID = 5;

	/**
	 * The constant for a many to many mapping.
	 */
	int MANY_TO_MANY = 6;

	/**
	 * The constant for a many to one mapping.
	 */
	int MANY_TO_ONE = 7;

	/**
	 * The constant for a one to many mapping.
	 */
	int ONE_TO_MANY = 8;

	/**
	 * The constant for a one to one mapping.
	 */
	int ONE_TO_ONE = 9;

	/**
	 * The constant for an attribute that is not persistent.
	 */
	int TRANSIENT = 10;

	/**
	 * The constant for a version mapping.
	 */
	int VERSION = 11;
}