/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
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
	 * @exception NullPointerException The {@link JPQLGrammar} cannot be <code>null</code>
	 */
	public JPQLQueryHelper(JPQLGrammar jpqlGrammar) {
		super();
		Assert.isNotNull(jpqlGrammar, "The JPQLGrammar cannot be null");
		this.jpqlGrammar = jpqlGrammar;
	}

	/**
	 * Retrieves the list of {@link ClassDescriptor descriptors} corresponding to the entities used
	 * throughout the given JPQL query.
	 *
	 * @param jpqlQuery The JPQL query used to gather the descriptors defined in the declaration clause
	 * @param session The {@link AbstractSession} is used to retrieve the descriptors
	 * @return The list of {@link ClassDescriptors descriptors} used in the JPQL query or an empty
	 * list if the JPQL query is invalid or incomplete
	 */
	public List<ClassDescriptor> getClassDescriptors(CharSequence jpqlQuery, AbstractSession session) {

		// Parse the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, jpqlGrammar, true);

		// Create the context and cache the information
		JPQLQueryContext queryContext = new JPQLQueryContext(jpqlGrammar);
		queryContext.cache(session, null, jpqlExpression, jpqlQuery);

		// Visit the JPQL query and collect the descriptors defined in the declaration clauses
		DescriptorCollector collector = new DescriptorCollector(queryContext);
		jpqlExpression.accept(collector);

		return new ArrayList<ClassDescriptor>(collector.descriptors);
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
	 * The return object is
	 * <pre><code> |- test.example.Employee
	 * |-   |- name : String
	 * |-   |- id : int
	 * |- test.example.Address
	 *      |- zipcode : int</code></pre>
	 *
	 * @param session The {@link AbstractSession} is used to retrieve the mappings used in the
	 * constructor items
	 * @return The holder of the result
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
	 * The return object is
	 * <pre><code> test.example.Address
	 *    |- BasicMapping(streetName) : String
	 *    |- BasicMapping(zipcode) : int</code></pre>
	 *
	 * @param session The {@link AbstractSession} is used to retrieve the mappings used in the
	 * constructor items
	 * @return The holder of the result
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

	private static class DescriptorCollector extends AbstractEclipseLinkTraverseChildrenVisitor {

		private Set<ClassDescriptor> descriptors;
		private JPQLQueryContext queryContext;
		private DeclarationResolver resolver;

		/**
		 * Creates a new <code>DescriptorCollector</code>.
		 */
		private DescriptorCollector(JPQLQueryContext queryContext) {
			super();
			this.descriptors  = new HashSet<ClassDescriptor>();
			this.queryContext = queryContext;
		}

		private void collectDescriptors(Expression expression, DeclarationResolver resolver) {

			resolver.populate(expression);

			for (Declaration declaration : resolver.getDeclarations()) {
				ClassDescriptor descriptor = declaration.getDescriptor();
				if (descriptor != null) {
					descriptors.add(descriptor);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {
			resolver = new DeclarationResolver(queryContext, null);
			collectDescriptors(expression, resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {
			resolver = new DeclarationResolver(queryContext, null);
			collectDescriptors(expression, resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			resolver = new DeclarationResolver(queryContext, resolver);
			collectDescriptors(expression, resolver);
			resolver = resolver.getParent();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			resolver = new DeclarationResolver(queryContext, null);
			collectDescriptors(expression, resolver);
		}
	}
}