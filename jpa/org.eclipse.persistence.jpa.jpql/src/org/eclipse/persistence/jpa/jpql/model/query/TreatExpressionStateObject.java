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
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * Returns an expression that allows to treat its base as if it were a subclass of the class
 * returned by the base.
 * <p>
 * <p>
 * <b>Note:</b> {@link IEclipseLinkStateObjectVisitor} needs to be used to traverse this state
 * object.
 * <p>
 * <div nowrap><b>BNF:</b> <code>join_treat ::= TREAT(collection_valued_path_expression [AS] entity_type_literal)</code><p>
 *
 * @see TreatExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class TreatExpressionStateObject extends AbstractStateObject {

	/**
	 * Flag used to determine if the <code><b>AS</b></code> identifier is used or not.
	 */
	private boolean as;

	/**
	 * The {@link StateObject} representing the entity type name.
	 */
	private EntityTypeLiteralStateObject entityTypeName;

	/**
	 * Keeps a reference onto the {@link JoinStateObject} since it owns the {@link IStatObject}
	 * representing the collection-valued path expression.
	 */
	private JoinStateObject joinStateObject;

	/**
	 * Notifies the visibility of the <code><b>AS</b></code> identifier has changed.
	 */
	public static final String AS_PROPERTY = "as";

	/**
	 * Notifies the entity type name property has changed.
	 */
	public static final String ENTITY_TYPE_NAME_PROPERTY = "entityTypeName";

	/**
	 * Creates a new <code>TreatExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which is temporary since this state object will
	 * be parented with the state object representing the join's association path expression
	 */
	public TreatExpressionStateObject(JoinStateObject parent) {
		super(parent);
		this.joinStateObject = parent;
	}

	/**
	 * Creates a new <code>TreatExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which is temporary since this state object will
	 * be parented with the state object representing the join's association path expression
	 * @param as Determines whether the <code><b>AS</b></code> identifier is used or not
	 * @param entityTypeName
	 */
	public TreatExpressionStateObject(JoinStateObject parent, boolean as, String entityTypeName) {
		super(parent);
		this.as              = as;
		this.joinStateObject = parent;
		this.entityTypeName.setText(entityTypeName);
	}

	/**
	 * Creates a new <code>TreatExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which is temporary since this state object will
	 * be parented with the state object representing the join's association path expression
	 * @param entityTypeName
	 */
	public TreatExpressionStateObject(JoinStateObject parent, String entityTypeName) {
		this(parent, false, entityTypeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		acceptUnknownVisitor(visitor);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public TreatExpressionStateObject addAs() {
		if (!as) {
			setAs(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(entityTypeName);
	}

	/**
	 * Appends the given text to the existing entity type name property.
	 *
	 * @param text The text to append to the entity type name property or nothing is done if the
	 * given value is <code>null</code>
	 */
	public void appendToEntityTypeName(String text) {
		if (text != null) {
			if (entityTypeName == null) {
				setEntityTypeName(text);
			}
			else {
				setEntityTypeName(entityTypeName + text);
			}
		}
	}

	/**
	 * Resolves the entity type name to the external form of the actual {@link Entity}.
	 *
	 * @return Either the {@link IEntity} with the same entity type name or <code>null</code> if the
	 * managed type provider does not have an entity with that name
	 */
	public IEntity getEntity() {

		for (IEntity entity : getManagedTypeProvider().entities()) {
			if (entity.getName().equals(entityTypeName)) {
				return entity;
			}
		}

		return null;
	}

	/**
	 * Returns the name of the entity that is used to downcast the join association path.
	 *
	 * @return The name of the entity used for down casting
	 */
	public String getEntityTypeName() {
		return entityTypeName.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreatExpression getExpression() {
		return (TreatExpression) super.getExpression();
	}

	/**
	 * Returns the reference of the {@link JoinStateObject}.
	 *
	 * @return The "owning" of this object
	 */
	public JoinStateObject getJoin() {
		return joinStateObject;
	}

	/**
	 * Returns the {@link StateObject} representing the identification variable that starts the path
	 * expression, which can be a sample identification variable, a map value, map key or map entry
	 * expression.
	 *
	 * @return The root of the path expression
	 */
	public StateObject getJoinAssociationIdentificationVariable() {
		return joinStateObject.getJoinAssociationIdentificationVariable();
	}

	/**
	 * Returns the {@link CollectionValuedPathExpressionStateObject} representing the join
	 * association path.
	 *
	 * @return The state object representing the join association path
	 */
	public CollectionValuedPathExpressionStateObject getJoinAssociationPathStateObject() {
		return joinStateObject.getJoinAssociationPathStateObject();
	}

	/**
	 * Determines whether the <code><b>AS</b></code> identifier is used.
	 *
	 * @return <code>true</code> if the <code><b>AS</b></code> identifier is used; <code>false</code>
	 * otherwise
	 */
	public boolean hasAs() {
		return as;
	}

	/**
	 * Determines whether the.
	 *
	 * @return <code>true</code> if the entity type name has been defined; <code>false</code>
	 * otherwise
	 */
	public boolean hasEntityTypeName() {
		return entityTypeName != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		entityTypeName = new EntityTypeLiteralStateObject(this);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is not specified.
	 */
	public void removeAs() {
		if (as) {
			setAs(true);
		}
	}

	/**
	 * Sets whether the <code><b>AS</b></code> identifier should be used.
	 *
	 * @param as <code>true</code> if the <code><b>AS</b></code> identifier should be used part;
	 * <code>false</code> otherwise
	 */
	public void setAs(boolean as) {
		boolean oldAs = this.as;
		this.as = as;
		firePropertyChanged(AS_PROPERTY, oldAs, as);
	}

	/**
	 * Sets the name of the entity that is used to downcast the join association path.
	 *
	 * @param entityType The Java class representing the entity type
	 */
	public void setEntityTypeName(Class<?> entityType) {
		setEntityTypeName(entityType.getName());
	}

	/**
	 * Sets the name of the entity that is used to downcast the join association path.
	 *
	 * @param entityType The external form of the entity type
	 */
	public void setEntityTypeName(IEntity entityType) {
		setEntityTypeName(entityType.getName());
	}

	/**
	 * Sets the name of the entity that is used to downcast the join association path.
	 *
	 * @param entityTypeName The new name of the entity used for down casting
	 */
	public void setEntityTypeName(String entityTypeName) {
		String oldEntityTypeName = getEntityTypeName();
		this.entityTypeName.setText(entityTypeName);
		firePropertyChanged(ENTITY_TYPE_NAME_PROPERTY, oldEntityTypeName, entityTypeName);
	}

	/**
	 * Keeps a reference of the {@link TreatExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link TreatExpression parsed object} representing the <code><b>TREAD</b></code>
	 * expression
	 */
	public void setExpression(TreatExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Toggles the visibility of the <code><b>AS</b></code> identifier; either adds it if it's not
	 * present otherwise removes it if it's present.
	 */
	public void toggleAs() {
		setAs(!as);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		// TREAT
		writer.append(TREAT);

		writer.append(LEFT_PARENTHESIS);

		// Join association path expression
		getJoinAssociationPathStateObject().toTextInternal(writer);

		// AS
		if (as) {
			writer.append(SPACE);
			writer.append(AS);
		}

		// Entity type literal
		if (entityTypeName.hasText()) {
			writer.append(SPACE);
			entityTypeName.toTextInternal(writer);
		}

		writer.append(RIGHT_PARENTHESIS);
	}
}