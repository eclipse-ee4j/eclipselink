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
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.FunctionsReturningStringsBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.PreLiteralExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>TRIM</b></code> function trims the specified character from a string. If the
 * character to be trimmed is not specified, it is assumed to be space (or blank). The optional
 * <code>trim_character</code> is a single-character string literal or a character-valued input
 * parameter (i.e., char or <code>Character</code>). If a trim specification is not provided,
 * <code><b>BOTH</b></code> is assumed. The <code><b>TRIM</b></code> function returns the trimmed
 * string.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= TRIM([[trim_specification] [trim_character] FROM] string_primary)</code><p>
 *
 * @see TrimExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class TrimExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Defines the way the string is trimmed.
	 */
	private Specification specification;

	/**
	 * The trim character, if specified.
	 */
	private StateObject trimCharacter;

	/**
	 * Notifies the visibility of the <code><b>FROM</b></code> identifier has changed.
	 */
	public static final String HAS_FROM_PROPERTY = "hasFrom";

	/**
	 * Notifies the specification property has changed.
	 */
	public static final String SPECIFICATION_PROPERTY = "specification";

	/**
	 * Notify the state object representing the trim character has changed.
	 */
	public static final String TRIM_CHARACTER_PROPERTY = "trimCharacterStateObject";

	/**
	 * Creates a new <code>TrimExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public TrimExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>TrimExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @param specification Defines the way the string is trimmed, or {@link org.eclipse.persistence.
	 * jpa.jpql.parser.TrimExpression.Specification#DEFAULT Specification.DEFAULT} when it is not
	 * present
	 * @param stateObject The {@link StateObject} representing the string primary
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public TrimExpressionStateObject(StateObject parent,
	                                 Specification specification,
	                                 StateObject stateObject) {

		this(parent, specification, null, stateObject);
	}

	/**
	 * Creates a new <code>TrimExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param specification Defines the way the string is trimmed, or {@link org.eclipse.persistence.
	 * jpa.jpql.parser.TrimExpression.Specification#DEFAULT Specification.DEFAULT} when it is not
	 * present
	 * @param trimCharacter The trim character
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public TrimExpressionStateObject(StateObject parent,
	                                 Specification specification,
	                                 StateObject trimCharacter,
	                                 StateObject stateObject) {

		super(parent, stateObject);
		this.specification = specification;
		this.trimCharacter = parent(trimCharacter);
	}

	/**
	 * Creates a new <code>TrimExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public TrimExpressionStateObject(StateObject parent, String jpqlFragment) {
		super(parent, jpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrimExpression getExpression() {
		return (TrimExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return TRIM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return StringPrimaryBNF.ID;
	}

	/**
	 * Returns the new trim specification.
	 *
	 * @return The new trim specification; which is never <code>null</code>
	 */
	public Specification getSpecification() {
		return specification;
	}

	/**
	 * Returns the {@link StateObject} representing the trim character.
	 *
	 * @return The {@link StateObject} representing the trim character or <code>null</code> if it is
	 * not present
	 */
	public StateObject getTrimCharacter() {
		return trimCharacter;
	}

	/**
	 * Determines whether the way the trim is trimmed was parsed.
	 *
	 * @return <code>true</code> if the query contained the way the trim needs to be trimmed;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpecification() {
		return (specification != Specification.DEFAULT);
	}

	/**
	 * Determines whether the character used to trim the string was specified.
	 *
	 * @return <code>true</code> if the character used for trimming was specified; <code>false</code>
	 * otherwise
	 */
	public boolean hasTrimCharacter() {
		return trimCharacter != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(String jpqlFragment) {

		StringBuilder sb = new StringBuilder();
		sb.append(TRIM);
		sb.append(LEFT_PARENTHESIS);
		sb.append(jpqlFragment);
		sb.append(RIGHT_PARENTHESIS);

		JPQLExpression jpqlExpression = new JPQLExpression(
			sb,
			getGrammar(),
			FunctionsReturningStringsBNF.ID,
			true
		);

		TrimExpression trimExpression = (TrimExpression) jpqlExpression.getQueryStatement();
		setSpecification(trimExpression.getSpecification());
		parseTrimCharacter(trimExpression.getTrimCharacter().toParsedText());
		super.parse(trimExpression.getExpression().toParsedText());

		// The trim character is actually the string primary
		if (!hasStateObject() && hasTrimCharacter()) {
			setStateObject(new StringLiteralStateObject(this, trimCharacter.toString()));
			trimCharacter = null;
		}
	}

	/**
	 * Parses the given JPQL fragment, which represents either a single-character string literal or a
	 * character-valued input parameter, the fragment will be parsed and converted into a {@link
	 * StateObject}.
	 *
	 * @param jpqlFragment The portion of the query to parse
	 */
	public void parseTrimCharacter(CharSequence jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, PreLiteralExpressionBNF.ID);
		setTrimCharacter(stateObject);
	}

	/**
	 * Removes the trim specification.
	 */
	public void removeSpecification() {
		setSpecification(Specification.DEFAULT);
	}

	/**
	 * Removes the trim character if it is defined.
	 */
	public void removeTrimCharacter() {
		setTrimCharacter(null);
	}

	/**
	 * Keeps a reference of the {@link TrimExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link TrimExpression parsed object} representing a <code><b>TRIM</b></code>
	 * expression
	 */
	public void setExpression(TrimExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the new trim specification.
	 *
	 * @param specification The new trim specification; <code>null</code> is not valid
	 */
	public void setSpecification(Specification specification) {
		Assert.isNotNull(specification, "The Specification cannot be null");
		Specification oldSpecification = this.specification;
		this.specification = specification;
		firePropertyChanged(SPECIFICATION_PROPERTY, oldSpecification, specification);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}

	/**
	 * Sets the character to trim from the string. If the character to be trimmed is not specified,
	 * it is assumed to be space (or blank). It is a single-character string literal or a character-
	 * valued input parameter (i.e., char or <code>Character</code>).
	 *
	 * @param trimCharacter The trim character or <code>null</code> to remove it
	 */
	public void setTrimCharacter(StateObject trimCharacter) {
		StateObject oldTrimCharacter = this.trimCharacter;
		this.trimCharacter = parent(trimCharacter);
		firePropertyChanged(TRIM_CHARACTER_PROPERTY, oldTrimCharacter, trimCharacter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextEncapsulatedExpression(Appendable writer) throws IOException {

		// Specification
		if (specification != Specification.DEFAULT) {
			writer.append(specification.name());
			writer.append(SPACE);
		}

		// Trim character
		if (hasTrimCharacter()) {
			trimCharacter.toString(writer);
			writer.append(SPACE);
		}

		// FROM
		if ((specification != Specification.DEFAULT) || hasTrimCharacter()) {
			writer.append(FROM);
			writer.append(SPACE);
		}

		// String primary
		super.toTextEncapsulatedExpression(writer);
	}
}