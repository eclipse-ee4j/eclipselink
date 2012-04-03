/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

/**
 * The abstract definition of a refactoring
 *
 * @see RefactoringTool
 * @see BasicRefactoringTool
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractRefactoringTool {

	/**
	 * The JPQL query to manipulate or a single JPQL fragment, which is parsed using the JPQL query
	 * specified BNF.
	 */
	private CharSequence jpqlFragment;

	/**
	 * The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF
	 * JPQLQueryBNF} that determines how to parse the JPQL fragment.
	 */
	private String jpqlQueryBNFId;

	/**
	 * The external form of a provider that gives access to the JPA metadata.
	 */
	private IManagedTypeProvider managedTypeProvider;

	/**
	 * Determines whether the parsing system should be tolerant.
	 */
	private boolean tolerant;

	/**
	 * Creates a new <code>BasicRefactoringTool</code>.
	 *
	 * @param jpqlFragment The JPQL query to manipulate or a single JPQL fragment, which is parsed
	 * using the JPQL query BNF identifier by the given ID
	 * @param jpqlGrammar The {@link JPQLGrammar} that was used to parse the JPQL fragment
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF} that determines how to parse the JPQL fragment
	 */
	protected AbstractRefactoringTool(CharSequence jpqlFragment,
	                                  IManagedTypeProvider managedTypeProvider,
	                                  String jpqlQueryBNFId) {

		super();
		this.tolerant            = true;
		this.jpqlFragment        = jpqlFragment;
		this.jpqlQueryBNFId      = jpqlQueryBNFId;
		this.managedTypeProvider = managedTypeProvider;
	}

	/**
	 * Returns the original JPQL query or the JPQL fragment that was passed to this tool so it can
	 * be manipulated.
	 *
	 * @return The string representation of the JPQL query or fragment
	 */
	public CharSequence getJPQLFragment() {
		return jpqlFragment;
	}

	/**
	 * Returns the unique identifier of the JPQL query BNF that determined how the JPQL query or
	 * fragment needs to be parsed.
	 *
	 * @return The ID of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF JPQLQueryBNF}
	 * used to parse the query
	 */
	public String getJPQLQueryBNFId() {
		return jpqlQueryBNFId;
	}

	/**
	 * Returns the provider of managed types.
	 *
	 * @return The provider that gives access to the managed types
	 */
	public IManagedTypeProvider getManagedTypeProvider() {
		return managedTypeProvider;
	}

	/**
	 * Determines whether the parsing system should be tolerant, meaning if it should try to parse
	 * invalid or incomplete queries.
	 *
	 * @return By default, the parsing system uses tolerance
	 */
	public boolean isTolerant() {
		return tolerant;
	}

	/**
	 * Sets whether the parsing system should be tolerant, meaning if it should try to parse invalid
	 * or incomplete queries.
	 *
	 * @param tolerant <code>true</code> if the JPQL query or fragment should be parsed with tolerance;
	 * <code>false</code> otherwise
	 */
	public void setTolerant(boolean tolerant) {
		this.tolerant = tolerant;
	}

	/**
	 * Returns the resulted of the refactoring operations. The list of changes will be removed after
	 * applying the changes.
	 *
	 * @return The string representation of the JPQL query that contains the changes
	 */
	public abstract String toActualText();
}