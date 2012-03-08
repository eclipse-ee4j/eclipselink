/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

/**
 * The interface is used to traverse some of the subclasses of {@link Resolver}. This visitor is
 * meant to traverse state field path expressions and collection-valued path expressions. The
 * hierarchy of resolving a path expression looks like this:
 * <p><pre>
 * EntityResolver
 * or
 * CollectionValuedFieldResolver
 * or
 * TreatResolver
 *  |
 *  -> IdentificationVariableResolver
 *      |
 *      (optional KeyResolver or ValueResolver)
 *      |
 *      -> StateFieldResolver
 *          |
 *        1...n
 *          |
 *          -> StateFieldResolver or CollectionValuedFieldResolver</pre>
 *
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface ResolverVisitor {

	/**
	 * Visits the given {@link CollectionValuedFieldResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(CollectionValuedFieldResolver resolver);

	/**
	 * Visits the given {@link EntityResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(EntityResolver resolver);

	/**
	 * Visits the given {@link EnumLiteralResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(EnumLiteralResolver resolver);

	/**
	 * Visits the given {@link IdentificationVariableResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(IdentificationVariableResolver resolver);

	/**
	 * Visits the given {@link KeyResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(KeyResolver resolver);

	/**
	 * Visits the given {@link StateFieldResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(StateFieldResolver resolver);

	/**
	 * Visits the given {@link TreatResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(TreatResolver resolver);

	/**
	 * Visits the given {@link ValueResolver}.
	 *
	 * @param resolver The {@link Resolver} to visit
	 */
	void visit(ValueResolver resolver);
}