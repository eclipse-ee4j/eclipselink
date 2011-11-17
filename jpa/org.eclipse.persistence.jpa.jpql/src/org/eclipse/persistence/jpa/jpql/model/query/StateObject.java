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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IPropertyChangeListener;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

/**
 * A <code>StateObject</code> is an editable representation of a JPQL query.
 * <p>
 * {@link org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder IJPQLQueryBuilder} can be used to
 * create the state model from an existing JPQL query.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface StateObject {

	/**
	 * Visits this {@link StateObject} by the given {@link StateObjectVisitor visitor}.
	 *
	 * @param visitor The {@link StateObjectVisitor visitor} to visit this object
	 */
	void accept(StateObjectVisitor visitor);

	/**
	 * Registers the given {@link IPropertyChangeListener} for the specified property. The listener
	 * will be notified only for changes to the specified property.
	 *
	 * @param propertyName The name of the property for which the listener was registered
	 * @param listener The listener to be notified upon changes
	 * @exception NullPointerException {@link IPropertyChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener is already registered with the property name
	 */
	void addPropertyChangeListener(String propertyName, IPropertyChangeListener<?> listener);

	/**
	 * Returns the ordered children of this {@link StateObject}.
	 *
	 * @return The children of this {@link StateObject} or an empty iterable this state object does
	 * not have children
	 */
	IterableIterator<StateObject> children();

	/**
	 * Decorates this {@link StateObject} with the given decorator. It means the behavior of this
	 * {@link StateObject} is modified by the given one. By default, this {@link StateObject}
	 * becomes the parent of the given one.
	 *
	 * @param decorator The {@link StateObject} decorating this one
	 */
	void decorate(StateObject decorator);

	/**
	 * Returns the {@link IdentificationVariableStateObject} representing the given identification
	 * variable.
	 *
	 * @param variable The name of the identification variable to retrieve its state object
	 * @return The {@link IdentificationVariableStateObject} defining the given identification variable
	 */
	IdentificationVariableStateObject findIdentificationVariable(String identificationVariable);

	/**
	 * Returns the declaration clause which defines the domain of the query by declaring
	 * identification variables.
	 *
	 * @return The declaration clause of which this {@link StateObject} is a child; i.e. either the
	 * top-level declaration if this is part of the top query or the sub-level declaration if this is
	 * part of a subquery
	 */
	DeclarationStateObject getDeclaration();

	/**
	 * Returns the {@link StateObject} decorating this one if one has been set, which means the
	 * behavior of this {@link StateObject} is modified by the decorator.
	 *
	 * @return The {@link StateObject} decorating this one
	 */
	StateObject getDecorator();

	/**
	 * Returns the actual parsed object if this {@link StateObject} representation of the JPQL query
	 * was created by parsing an existing JPQL query.
	 *
	 * @return The parsed object when a JPQL query is parsed and converted into a {@link StateObject}
	 * or <code>null</code> when the JPQL query is manually created (i.e. not from a string)
	 */
	Expression getExpression();

	/**
	 * Returns the grammar that defines how to parse a JPQL query.
	 *
	 * @return The grammar that was used to parse the JPQL query
	 */
	JPQLGrammar getGrammar();

	/**
	 * Returns the provider of managed types.
	 *
	 * @return The provider that gives access to the managed types
	 */
	IManagedTypeProvider getManagedTypeProvider();

	/**
	 * Returns the parent of this {@link StateObject}.
	 *
	 * @return Returns the parent of this {@link StateObject}, which is <code>null</code> only when
	 * this is the root of the hierarchy
	 */
	StateObject getParent();

	/**
	 * Returns the {@link IJPQLQueryBuilder} that is responsible to create various part of the {@link
	 * StateObject} hierarchy.
	 *
	 * @return The builder that created this {@link StateObject} from a JPQL query or that gives
	 * access to various sub-builders
	 */
	IJPQLQueryBuilder getQueryBuilder();

	/**
	 * Returns the root of the {@link StateObject} hierarchy.
	 *
	 * @return The root of the state model representing the JPQL query
	 */
	JPQLQueryStateObject getRoot();

	/**
	 * Determines whether this {@link StateObject} is being decorated by another {@link StateObject},
	 * which means the behavior is modified by the given one.
	 *
	 * @param <code>true</code> if this {@link StateObject} is being decorated; <code>false</code>
	 * otherwise
	 */
	boolean isDecorated();

	/**
	 * Unregisters the given {@link IPropertyChangeListener} that was registered for the specified
	 * property. The listener will no longer be notified when the property changes.
	 *
	 * @param propertyName The name of the property for which the listener was registered
	 * @param listener The listener to unregister
	 * @exception NullPointerException {@link IPropertyChangeListener} cannot be <code>null</code>
	 * @exception IllegalArgumentException The listener was never registered with the property name
	 */
	void removePropertyChangeListener(String propertyName, IPropertyChangeListener<?> listener);

	/**
	 * Sets the given {@link StateObject} to become the parent of this one.
	 *
	 * @param parent The new parent {@link StateObject} of this one, which cannot be <code>null</code>
	 */
	void setParent(StateObject parent);

	/**
	 * Prints out a string representation of this {@link StateObject}, which should not be used to
	 * define a <code>true</code> string representation of a JPQL query but should be used for
	 * debugging purposes.
	 * <p>
	 * <b>Important:</b> If this {@link StateObject} is decorated by another one, then {@link
	 * #toString(Appendable)} from that decorator is invoked, otherwise the information contained in
	 * this one will be printed out.
	 *
	 * @param writer The writer used to print out the string representation
	 * @see #toText(Appendable)
	 */
	void toString(Appendable writer);

	/**
	 * Prints out a string representation of this {@link StateObject}, which should not be used to
	 * define a <code>true</code> string representation of a JPQL query but should be used for
	 * debugging purposes.
	 * <p>
	 * <b>Important:</b> Even if this {@link StateObject} is decorated by another one, the decorator
	 * will not be used to print out a string representation of this one.
	 *
	 * @param writer The writer used to print out the string representation
	 * @see #toString(Appendable)
	 */
	void toText(Appendable writer);
}