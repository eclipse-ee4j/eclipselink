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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * This helper can perform various operations over a JPQL query.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class JPQLQueryHelper {

	/**
	 * The grammar that defines how to parse a JPQL query.
	 */
	private JPQLGrammar jpqlGrammar;

	/**
	 * Creates a new <code>JPQLQueryHelper</code> which uses the latest JPQL grammar.
	 */
	public JPQLQueryHelper() {
		this(DefaultEclipseLinkJPQLGrammar.instance());
	}

	/**
	 * Creates a new <code>JPQLQueryHelper</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} that will determine how to parse JPQL queries
	 */
	public JPQLQueryHelper(JPQLGrammar jpqlGrammar) {
		super();
		Assert.isNotNull(jpqlGrammar, "The JPQLGrammar cannot be null");
		this.jpqlGrammar = jpqlGrammar;
	}

	/**
	 * Retrieves the class names and the attribute names mapped to their types that are used in the
	 * constructor expressions defined in the <code><b>SELECT</b></code> clause.
	 * <p>
	 * For instance, from the following JPQL query:
	 * <p>
	 * <pre><code> SELECT new test.example.Employee(e.name, e.id),
	 *        new test.example.Address(a.zipcode)
	 * FROM Employee e, Address a</code></pre>
	 * <p>
	 * The return object would be
	 * <pre><code> |- test.example.Employee
	 * |-   |- name : String
	 * |-   |- id : int
	 * |- test.example.Address
	 *      |- zipcode : int</code></pre>
	 *
	 * @param session The {@link AbstractSession}
	 */
	public List<ConstructorQueryMappings> getConstructorQueryMappings(AbstractSession session) {

		List<ConstructorQueryMappings> allMappings = new LinkedList<ConstructorQueryMappings>();

		for (DatabaseQuery query : session.getJPAQueries()) {
			ConstructorQueryMappings mappings = getConstructorQueryMappings(session, query);
			allMappings.add(mappings);
		}

		return allMappings;
	}

	/**
	 * Retrieves the class names and the attribute names mapped to their types that are used in the
	 * constructor expressions defined in the <code><b>SELECT</b></code> clause.
	 * <p>
	 * For instance, from the following JPQL query:
	 * <p>
	 * <pre><code> SELECT new test.example.Address(a.streetName, a.zipcode)
	 * FROM Address a</code></pre>
	 * <p>
	 * The return object would be
	 * <pre><code> test.example.Address
	 *    |- BasicMapping(streetName) : String
	 *    |- BasicMapping(zipcode) : int</code></pre>
	 *
	 * @param session The {@link AbstractSession}
	 */
	public ConstructorQueryMappings getConstructorQueryMappings(AbstractSession session,
	                                                            DatabaseQuery query) {

		JPQLQueryContext queryContext = new JPQLQueryContext(query, jpqlGrammar);

		ConstructorQueryMappings mappings = new ConstructorQueryMappings(query);
		mappings.populate(jpqlGrammar);
		return mappings;
	}

	/**
	 * Returns the JPQL grammar that will be used to define how to parse a JPQL query.
	 *
	 * @return The grammar that will determine how to parse a JPQL query
	 */
	public JPQLGrammar getGrammar() {
		return jpqlGrammar;
	}
}