/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

/**
 * This object stores the various proposals available for content assist for a certain position
 * within a JPQL query. The proposals are stored in categories (abstract schema types, identifiers,
 * identification variables and mappings).
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface ContentAssistProposals {

	/**
	 * Returns the collection of possible abstract schema types.
	 *
	 * @return The {@link IEntity entities} defined in the persistence context
	 */
	IterableIterator<IEntity> abstractSchemaTypes();

	/**
	 * Creates a new JPQL query by inserting the given proposal at the given position. The resulted
	 * JPQL query and position will be adjusted by converting the escaped characters escaped to
	 * string values, which means '\r' will have been converted to '\\r.
	 * <p>
	 * TODO: TO REWORD: If the proposal is has more than one identifier, for instance, <code>IS NOT
	 * NULL</code>, then the replacement will go further than just replacing the current word. If the
	 * cursor is in "IS NOT N|" and the proposal is "IS NOT NULL", then "IS NOT" will not be added
	 * twice. If the identifier is "IS NULL" and the proposal is "IS NOT NULL", then NOT will be
	 * inserted between IS and NULL.
	 *
	 * @param jpqlQuery The JPQL query to insert the given proposal
	 * @param proposal The proposal to insert into the query
	 * @param position The position of insertion
	 * @param insert Flag that determines if the partial word following the cursor should be left
	 * intact or replaced by the proposal
	 * @return The result of inserting the proposal into the query, including the adjust position, if
	 * it was required
	 */
	ResultQuery buildEscapedQuery(String jpqlQuery, String proposal, int position, boolean insert);

	/**
	 * Creates a new JPQL query by inserting the given proposal at the given position.
	 * <p>
	 * TODO: TO REWORD: If the proposal is has more than one identifier, for instance, <code>IS NOT
	 * NULL</code>, then the replacement will go further than just replacing the current word. If the
	 * cursor is in "IS NOT N|" and the proposal is "IS NOT NULL", then "IS NOT" will not be added
	 * twice. If the identifier is "IS NULL" and the proposal is "IS NOT NULL", then NOT will be
	 * inserted between IS and NULL.
	 *
	 * @param jpqlQuery The JPQL query to insert the given proposal
	 * @param proposal The proposal to insert into the query
	 * @param position The position of insertion
	 * @param insert Flag that determines if the partial word following the cursor should be left
	 * intact or replaced by the proposal
	 * @return The result of inserting the proposal into the query, including the adjust position, if
	 * it was required
	 */
	ResultQuery buildQuery(String jpqlQuery, String proposal, int position, boolean insert);

	/**
	 * Retrieves the abstract schema type that is mapped with the given identification variable.
	 *
	 * @param identificationVariable The identification variable that, if defined as a range variable,
	 * will be mapped to a managed type
	 * @return The abstract schema type mapped with the given identification variable or
	 * <code>null</code> if the given variable is mapped to something else or not mapped to anything
	 */
	IEntity getAbstractSchemaType(String identificationVariable);

	/**
	 * Returns the role of the given JPQL identifier.
	 *
	 * @param identifier The JPQL identifier to retrieve its role
	 * @return The {@link IdentifierRole} for the given JPQL identifier or <code>null</code> if no
	 * role was defined or if the given string is not a valid JPQL identifier
	 * @since 2.4
	 */
	IdentifierRole getIdentifierRole(String identifier);

	/**
	 * Determines whether there is at least one proposals.
	 *
	 * @return <code>true</code> if there is at least one proposal; otherwise <code>false</code>
	 */
	boolean hasProposals();

	/**
	 * Returns the collection of possible identification variables.
	 *
	 * @return The list of possible identification variables
	 */
	IterableIterator<String> identificationVariables();

	/**
	 * Returns the collection of possible JPQL identifiers.
	 *
	 * @return The list of possible JPQL identifiers
	 */
	IterableIterator<String> identifiers();

	/**
	 * Returns the collection of possible {@link IMapping mappings}, which can be state fields,
	 * association fields and/or collection fields depending on the location used to retrieve the
	 * possible proposals.
	 *
	 * @return The list of possible proposals {@link IMapping mappings}
	 */
	IterableIterator<IMapping> mappings();
}