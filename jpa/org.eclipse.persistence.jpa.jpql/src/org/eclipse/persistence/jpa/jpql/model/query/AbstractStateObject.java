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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.model.DefaultProblem;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IPropertyChangeListener;
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.VirtualJPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * The abstract definition of a {@link StateObject}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractStateObject implements StateObject {

	/**
	 * The object responsible to actually register the listeners and to notify them upon changes made
	 * to this {@link StateObject}.
	 */
	private ChangeSupport changeSupport;

	/**
	 * The {@link StateObject} that is decorating this one by changing its behavior or <code>null</code>
	 * if none was set.
	 */
	private StateObject decorator;

	/**
	 * The parsed object when a JPQL query is parsed and converted into a {@link StateObject} or
	 * <code>null</code> when the JPQL query is manually created (i.e. not from a string).
	 */
	private Expression expression;

	/**
	 * The parent of this state object.
	 */
	private StateObject parent;

	/**
	 * Creates a new <code>AbstractStateObject</code>.
	 *
	 * @param The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>, unless {@link
	 * #changeSupport} is overridden and does not throw the exception
	 */
	protected AbstractStateObject(StateObject parent) {
		super();
		this.parent = checkParent(parent);
		initialize();
	}

	/**
	 * The given {@link StateObjectVisitor} needs to visit this class but it is defined by a
	 * third-party provider. This method will programmatically invoke the <b>visit</b> method defined
	 * on the given visitor which signature should be.
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(ThirdPartyStateObject stateObject)</code>
	 * <p>
	 * or
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(StateObject stateObject)</code><p>
	 *
	 * @param visitor The {@link StateObjectVisitor} to visit this {@link StateObject} programmatically
	 * @return <code>true</code> if the call was successfully executed; <code>false</code> otherwise
	 * @since 2.4
	 */
	protected boolean acceptUnknownVisitor(StateObjectVisitor visitor) {
		try {
			try {
				acceptUnknownVisitor(visitor, visitor.getClass(), getClass());
			}
			catch (NoSuchMethodException e) {
				// Try with Expression has the parameter type
				acceptUnknownVisitor(visitor, visitor.getClass(), StateObject.class);
			}
			return true;
		}
		catch (NoSuchMethodException e) {
			// Ignore, just do nothing
			return false;
		}
		catch (IllegalAccessException e) {
			// Ignore, just do nothing
			return false;
		}
		catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			RuntimeException actual;
			if (cause instanceof RuntimeException) {
				actual = (RuntimeException) cause;
			}
			else {
				actual = new RuntimeException(cause);
			}
			throw actual;
		}
	}

	/**
	 * The given {@link StateObjectVisitor} needs to visit this class but it is defined by a
	 * third-party provider. This method will programmatically invoke the <b>visit</b> method defined
	 * on the given visitor which signature should be.
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(ThirdPartyStateObject stateObject)</code>
	 * <p>
	 * or
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(StateObject stateObject)</code><p>
	 *
	 * @param visitor The {@link StateObjectVisitor} to visit this {@link StateObject} programmatically
	 * @param type The type found in the hierarchy of the given {@link StateObjectVisitor} that will
	 * be used to retrieve the visit method
	 * @param parameterType The parameter type of the visit method
	 * @see #acceptUnknownVisitor(ExpressionVisitor)
	 * @since 2.4
	 */
	protected void acceptUnknownVisitor(StateObjectVisitor visitor,
	                                    Class<?> type,
	                                    Class<?> parameterType) throws NoSuchMethodException,
	                                                                   IllegalAccessException,
	                                                                   InvocationTargetException{

		try {
			Method visitMethod = type.getDeclaredMethod("visit", parameterType);
			visitMethod.setAccessible(true);
			visitMethod.invoke(visitor, this);
		}
		catch (NoSuchMethodException e) {
			type = type.getSuperclass();
			if (type == Object.class) {
				throw e;
			}
			else {
				acceptUnknownVisitor(visitor, type, parameterType);
			}
		}
	}

	/**
	 * Adds the children of this {@link StateObject} to the given list.
	 *
	 * @param children The list used to store the children
	 */
	protected void addChildren(List<StateObject> children) {
	}

	/**
	 * Adds to the given list the problems that were found with the current state of this {@link
	 * StateObject}, which means there are validation issues.
	 *
	 * @param problems The list to which the problems are added
	 */
	protected void addProblems(List<Problem> problems) {
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addPropertyChangeListener(String propertyName, IPropertyChangeListener<?> listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Determines whether the given two {@link StateObject} are equivalent, i.e. the information of
	 * both {@link StateObject} is the same.
	 *
	 * @param stateObject1 The first {@link StateObject} to compare its content with the other one
	 * @param stateObject2 The second {@link StateObject} to compare its content with the other one
	 * @return <code>true</code> if both objects are equivalent; <code>false</code> otherwise
	 */
	protected final boolean areEquivalent(StateObject stateObject1, StateObject stateObject2) {

		// Both are equal or both are null
		if ((stateObject1 == stateObject2) || (stateObject1 == null) && (stateObject2 == null)) {
			return true;
		}

		// One is null but the other is not
		if ((stateObject1 == null) || (stateObject2 == null)) {
			return false;
		}

		return stateObject1.isEquivalent(stateObject2);
	}

	/**
	 * Creates a new {@link Problem} describing a single issue found with the information contained
	 * in this {@link StateObject}.
	 *
	 * @param messageKey The key used to retrieve the localized message describing the problem found
	 * with the current state of this {@link StateObject}
	 * @return The new {@link Problem}
	 */
	protected final Problem buildProblem(String messageKey) {
		return buildProblem(messageKey, ExpressionTools.EMPTY_STRING_ARRAY);
	}

	/**
	 * Creates a new {@link Problem} describing a single issue found with the information contained
	 * in this {@link StateObject}.
	 *
	 * @param messageKey The key used to retrieve the localized message describing the problem found
	 * with the current state of this {@link StateObject}
	 * @param arguments A list of arguments that can be used to complete the message or an empty list
	 * if no additional information is necessary
	 * @return The new {@link Problem}
	 */
	protected final Problem buildProblem(String messageKey , String... arguments) {
		return new DefaultProblem(this, messageKey, arguments);
	}

	/**
	 * Parses the given JPQL fragment using the given JPQL query BNF.
	 *
	 * @param jpqlFragment A portion of a JPQL query that will be parsed and converted into a {@link
	 * StateObject}
	 * @param queryBNFId The unique identifier of the BNF that determines how to parse the fragment
	 * @return A {@link StateObject} representation of the given JPQL fragment
	 */
	@SuppressWarnings("unchecked")
	protected <T extends StateObject> T buildStateObject(CharSequence jpqlFragment, String queryBNFId) {
		return (T) getQueryBuilder().buildStateObject(this, jpqlFragment, queryBNFId);
	}

	/**
	 * Parses the given JPQL fragment using the given JPQL query BNF.
	 *
	 * @param jpqlFragment A portion of a JPQL query that will be parsed and converted into either a
	 * single {@link StateObject} or a list of {@link StateObject}, which happens when the fragment
	 * contains a collection of items separated by either a comma or a space
	 * @param queryBNFId The unique identifier of the BNF that will be used to parse the fragment
	 * @return A list of {@link StateObject StateObjects} representing the given JPQL fragment, which
	 * means the list may contain a single {@link StateObject} or a multiple {@link StateObject
	 * StateObjects} if the fragment contains more than one expression of the same type. Example:
	 * "JOIN e.employees e LEFT JOIN e.address a", this would be parsed in two state objects
	 */
	@SuppressWarnings("unchecked")
	protected <T extends StateObject> List<T> buildStateObjects(CharSequence jpqlFragment,
	                                                            String queryBNFId) {

		VirtualJPQLQueryBNF queryBNF = new VirtualJPQLQueryBNF(getGrammar());
		queryBNF.setHandleCollection(true);
		queryBNF.setFallbackBNFId(queryBNFId);
		queryBNF.registerQueryBNF(queryBNFId);

		final List<StateObject> items = new ArrayList<StateObject>();

		try {
			StateObject stateObject = buildStateObject(jpqlFragment, queryBNF.getId());

			StateObjectVisitor visitor = new AnonymousStateObjectVisitor() {
				@SuppressWarnings("unused")
				public void visit(CollectionExpressionStateObject stateObject) {
					CollectionTools.addAll(items, stateObject.children());
				}
				@Override
				protected void visit(StateObject stateObject) {
					items.add(stateObject);
				}
			};

			stateObject.accept(visitor);
		}
		finally {
			queryBNF.dispose();
		}

		return (List<T>) items;
	}

	/**
	 * Checks whether the given parent is <code>null</code> or not. If it's <code>null</code> then
	 * throw a {@link NullPointerException}.
	 *
	 * @param parent The parent of this state object
	 * @return The given object
	 */
	protected StateObject checkParent(StateObject parent) {
		if (parent == null) {
			Assert.isNotNull(parent, "The parent cannot be null");
		}
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IterableIterator<StateObject> children() {
		List<StateObject> children = new ArrayList<StateObject>();
		addChildren(children);
		return new CloneIterator<StateObject>(children);
	}

	/**
	 * {@inheritDoc}
	 */
	public void decorate(StateObject decorator) {
		this.decorator = parent(decorator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object object) {
		return super.equals(object);
	}

	/**
	 * {@inheritDoc}
	 */
	public IdentificationVariableStateObject findIdentificationVariable(String identificationVariable) {
		return parent.findIdentificationVariable(identificationVariable);
	}

	/**
	 * Notifies the {@link IPropertyChangeListener IPropertyChangeListeners} that have been registered
	 * with the given property name that the property has changed.
	 *
	 * @param propertyName The name of the property associated with the property change
	 * @param oldValue The old value of the property that changed
	 * @param newValue The new value of the property that changed
	 */
	protected final void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChanged(propertyName, oldValue, newValue);
	}

	/**
	 * Returns the object responsible to actually register the listeners and to notify them upon
	 * changes made to this {@link StateObject}.
	 *
	 * @return The manager of listeners and notification
	 */
	protected final ChangeSupport getChangeSupport() {
		return changeSupport;
	}

	/**
	 * {@inheritDoc}
	 */
	public DeclarationStateObject getDeclaration() {
		return parent.getDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject getDecorator() {
		return decorator;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLGrammar getGrammar() {
		return getRoot().getGrammar();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedTypeProvider getManagedTypeProvider() {
		return getRoot().getManagedTypeProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public IJPQLQueryBuilder getQueryBuilder() {
		return getRoot().getQueryBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryStateObject getRoot() {
		return parent.getRoot();
	}

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	public IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	public IType getType(String typeName) {
		return getTypeRepository().getType(typeName);
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	public TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	public ITypeRepository getTypeRepository() {
		return getManagedTypeProvider().getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	/**
	 * Initializes this state object.
	 */
	protected void initialize() {
		changeSupport = new ChangeSupport(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDecorated() {
		return decorator != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEquivalent(StateObject stateObject) {
		return (this == stateObject) ||
		       ((stateObject != null) && (stateObject.getClass() == getClass()));
	}

	/**
	 * Makes sure the given list of {@link StateObject} has this one as its parent.
	 *
	 * @param stateObjects The list of {@link StateObject} to have this one as its parent
	 * @return The given list of {@link StateObject}
	 */
	protected <T extends StateObject> List<T> parent(List<T> stateObjects) {
		for (StateObject stateObject : stateObjects) {
			parent(stateObject);
		}
		return stateObjects;
	}

	/**
	 * Makes sure the given list of {@link StateObject} has this one as its parent.
	 *
	 * @param stateObjects The list of {@link StateObject} to have this one as its parent
	 * @return The given list of {@link StateObject}
	 */
	protected <T extends StateObject> T[] parent(T... stateObjects) {
		for (StateObject stateObject : stateObjects) {
			parent(stateObject);
		}
		return stateObjects;
	}

	/**
	 * Makes sure the given {@link StateObject} has this one as its parent.
	 *
	 * @param stateObject The {@link StateObject} to have this one as its parent
	 * @return The given {@link StateObject}
	 */
	protected <T extends StateObject> T parent(T stateObject) {
		if (stateObject != null) {
			stateObject.setParent(this);
		}
		return stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removePropertyChangeListener(String propertyName, IPropertyChangeListener<?> listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Sets the actual parsed object if this {@link StateObject} representation of the JPQL query
	 * is created by converting the parsed representation of the JPQL query.
	 *
	 * @param expression The parsed object when a JPQL query is parsed
	 */
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setParent(StateObject parent) {
		Assert.isNotNull(parent, "The parent cannot be null");
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void toString(Appendable writer) {
		try {
			toStringInternal(writer);
		}
		catch (IOException e) {
			// Never happens because the Appendable should be an AbstractStringBuilder
		}
	}

	/**
	 * Prints out a string representation of this {@link StateObject}.
	 * <p>
	 * <b>Important:</b> If this {@link StateObject} is decorated by another one, then {@link
	 * #toString(Appendable)} from that decorator is invoked, otherwise {@link #toTextInternal(Appendable)}
	 * from this one is invoked.
	 *
	 * @param writer The writer used to print out the string representation
	 * @throws IOException This should never happens, it is only required because
	 * {@link Appendable#append(CharSequence)} throws an {@link IOException}
	 */
	protected final void toStringInternal(Appendable writer) throws IOException {
		if (isDecorated()) {
			getDecorator().toString(writer);
		}
		else {
			toTextInternal(writer);
		}
	}

	protected void toStringItems(Appendable writer,
	                             List<? extends StateObject> items,
	                             boolean useComma) throws IOException {

		int count = items.size();
		int index = -1;

		for (StateObject stateObject : items) {
			stateObject.toString(writer);

			if (++index + 1 < count) {
				if (useComma) {
					writer.append(COMMA);
				}
				writer.append(SPACE);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void toText(Appendable writer) {
		try {
			toTextInternal(writer);
		}
		catch (IOException e) {
			// Never happens because the Appendable should be an AbstractStringBuilder
		}
	}

	/**
	 * Prints out a string representation of this {@link StateObject}, which should not be used to
	 * define a <code>true</code> string representation of a JPQL query but should be used for
	 * debugging purposes.
	 *
	 * @param writer The writer used to print out the string representation
	 * @throws IOException This should never happens, it is only required because {@link Appendable}
	 * is used instead of any concrete class
	 */
	protected abstract void toTextInternal(Appendable writer) throws IOException;
}