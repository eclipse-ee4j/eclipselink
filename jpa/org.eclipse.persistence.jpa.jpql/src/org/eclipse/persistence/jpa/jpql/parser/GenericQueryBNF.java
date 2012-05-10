/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * A generic {@link JPQLQueryBNF} can be used to manually create a new BNF without having to create
 * a concrete instance.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class GenericQueryBNF extends JPQLQueryBNF {

	/**
	 * Creates a new <code>GenericQueryBNF</code>.
	 *
	 * @param id The unique identifier of this BNF
	 */
	public GenericQueryBNF(String id) {
		super(id);
	}
}