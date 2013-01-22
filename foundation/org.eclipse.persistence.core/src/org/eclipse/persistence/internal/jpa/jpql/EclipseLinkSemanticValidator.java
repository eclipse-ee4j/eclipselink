/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidatorExtension;

/**
 * The EclipseLink runtime version of Hermes' semantic validator.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
final class EclipseLinkSemanticValidator extends AbstractEclipseLinkSemanticValidator {

	/**
	 * Creates a new <code>EclipseLinkSemanticValidator</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 */
	EclipseLinkSemanticValidator(JPQLQueryContext queryContext) {

		super(new EclipseLinkSemanticValidatorHelper(queryContext),
		      EclipseLinkSemanticValidatorExtension.NULL_EXTENSION);
	}
}