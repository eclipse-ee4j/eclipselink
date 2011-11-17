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
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.spi;

/**
 * This enumeration lists the mapping types defined in the Java Persistence functional specification
 * and those that are provided by EclipseLink.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IEclipseLinkMappingType extends IMappingType {

	/**
	 * The constant for a basic collection mapping, which is deprecated.
	 */
	int BASIC_COLLECTION = 101;

	/**
	 * The constant for a basic map mapping, which is deprecated.
	 */
	int BASIC_MAP = 102;

	/**
	 * The constant for a transformation mapping.
	 */
	int TRANSFORMATION = 103;

	/**
	 * The constant for a variable one to one mapping.
	 */
	int VARIABLE_ONE_TO_ONE = 104;
}