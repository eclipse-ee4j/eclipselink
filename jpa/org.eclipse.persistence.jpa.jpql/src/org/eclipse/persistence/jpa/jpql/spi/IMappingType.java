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
 * This enumeration lists the supported mapping types.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public enum IMappingType {

	/**
	 * The constant for a basic mapping.
	 */
	BASIC,

	/**
	 * The constant for a basic collection mapping, which is deprecated.
	 */
	BASIC_COLLECTION,

	/**
	 * The constant for a basic map mapping, which is deprecated.
	 */
	BASIC_MAP,

	/**
	 * The constant for a element collection mapping.
	 */
	ELEMENT_COLLECTION,

	/**
	 * The constant for an embedded mapping.
	 */
	EMBEDDED,

	/**
	 * The constant for an embedded ID mapping.
	 */
	EMBEDDED_ID,

	/**
	 * The constant for an ID mapping.
	 */
	ID,

	/**
	 * The constant for a many to many mapping.
	 */
	MANY_TO_MANY,

	/**
	 * The constant for a many to one mapping.
	 */
	MANY_TO_ONE,

	/**
	 * The constant for a one to many mapping.
	 */
	ONE_TO_MANY,

	/**
	 * The constant for a one to one mapping.
	 */
	ONE_TO_ONE,

	/**
	 * The constant for a transformation mapping.
	 */
	TRANSFORMATION,

	/**
	 * The constant for an attribute that is not persistent.
	 */
	TRANSIENT,

	/**
	 * The constant for a variable one to one mapping.
	 */
	VARIABLE_ONE_TO_ONE,

	/**
	 * The constant for a version mapping.
	 */
	VERSION
}