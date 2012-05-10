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
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;

/**
 * The default implementation of {@link INewValueStateObjectBuilder}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultNewValueStateObjectBuilder extends AbstractNewValueStateObjectBuilder {

	/**
	 * Creates a new <code>DefaultNewValueStateObjectBuilder</code>.
	 *
	 * @param parent The parent of the expression to build, which is only required when a JPQL
	 * fragment needs to be parsed
	 */
	public DefaultNewValueStateObjectBuilder(UpdateItemStateObject parent) {
		super(parent);
	}
}