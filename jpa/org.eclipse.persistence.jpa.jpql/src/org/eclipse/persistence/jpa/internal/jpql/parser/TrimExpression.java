/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * The <b>TRIM</b> function trims the specified character from a string. If the character to be
 * trimmed is not specified, it is assumed to be space (or blank). The optional <code>trim_character</code>
 * is a single-character string literal or a character-valued input parameter (i.e., char or
 * Character). If a trim specification is not provided, <b>BOTH</b> is assumed. The <b>TRIM</b>
 * function returns the trimmed string.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= TRIM([[trim_specification] [trim_character] FROM] string_primary)</code><p>
 * <p>
 * <div nowrap><b>BNF:</b> <code>trim_character ::= string_literal | input_parameter</code><p>
 * <p>
 * <div norwrap>Example: <code><b>UPDATE</b> Student st <b>SET</b> st.sname=TRIM(st.sname)</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class TrimExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Determines whether the identifier <b>FROM</b> was part of the query.
	 */
	private boolean hasFrom;

	/**
	 * Determines whether a space was parsed after the identifier <b>FROM</b>.
	 */
	private boolean hasSpaceAfterFrom;

	/**
	 * Determines whether a space was parsed after the trim specification.
	 */
	private boolean hasSpaceAfterSpecification;

	/**
	 * Determines whether a space was parsed after the trim character.
	 */
	private boolean hasSpaceAfterTrimCharacter;

	/**
	 * The specification specifies how to trim the string.
	 */
	private Specification specification;

	/**
	 * The character used for trimming the string.
	 */
	private AbstractExpression trimCharacter;

	/**
	 * Creates a new <code>TrimExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	TrimExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void acceptChildren(ExpressionVisitor visitor) {
		getTrimCharacter().accept(visitor);
		super.acceptChildren(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedEncapsulatedExpressionTo(List<StringExpression> children) {

		// Trim specification
		if (hasSpecification()) {
			children.add(buildStringExpression(specification.name()));
		}

		if (hasSpaceAfterSpecification) {
			children.add(buildStringExpression(SPACE));
		}

		// Trim character
		if (hasTrimCharacter()) {
			children.add(trimCharacter);
		}

		if (hasSpaceAfterTrimCharacter) {
			children.add(buildStringExpression(SPACE));
		}

		// 'FROM'
		if (hasFrom) {
			children.add(buildStringExpression(FROM));
		}

		if (hasSpaceAfterFrom) {
			children.add(buildStringExpression(SPACE));
		}

		// String primary
		super.addOrderedEncapsulatedExpressionTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return StringPrimaryBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(FunctionsReturningStringsBNF.ID);
	}

	/**
	 * Returns the specification which specifies how to trim the string.
	 *
	 * @return One of the available choices for trimming the string
	 */
	public Specification getSpecification() {
		return specification;
	}

	/**
	 * Returns the character used for trimming the string.
	 *
	 * @return The character, if one was parsed, that will be used to trim the string. If the
	 * character was not specified, then '\0' is the character
	 */
	public Expression getTrimCharacter() {
		if (trimCharacter == null) {
			trimCharacter = buildNullExpression();
		}
		return trimCharacter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasSpecification() || hasTrimCharacter() || hasFrom || hasExpression();
	}

	/**
	 * Determines whether the identifier <b>FROM</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>FROM</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasFrom() {
		return hasFrom;
	}

	/**
	 * Determines whether a whitespace was found after <b>FROM</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>FROM</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterFrom() {
		return hasSpaceAfterFrom;
	}

	/**
	 * Determines whether a whitespace was found after the way the string is trimmed.
	 *
	 * @return <code>true</code> if there was a whitespace after the trim specification;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterSpecification() {
		return hasSpaceAfterSpecification;
	}

	/**
	 * Determines whether a whitespace was found after the character used to trim the string.
	 *
	 * @return <code>true</code> if there was a whitespace after the trim character;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterTrimCharacter() {
		return hasSpaceAfterTrimCharacter;
	}

	/**
	 * Determines whether the way the trim is trimmed was parsed.
	 *
	 * @return <code>true</code> if the query contained the way the trim needs to
	 * be trimmed; <code>false</code> otherwise
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
		return trimCharacter != null &&
		      !trimCharacter.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		// Parse the trim specification
		specification = parseTrimSpecification(wordParser);

		if (specification != Specification.DEFAULT) {
			wordParser.moveForward(specification.name().length());
			hasSpaceAfterSpecification = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the trim character
		if (!wordParser.startsWithIdentifier(FROM)) {
			trimCharacter = parse(wordParser, queryBNF(PreLiteralExpressionBNF.ID), tolerant);
		}

		if (hasTrimCharacter()) {
			hasSpaceAfterTrimCharacter = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse 'FROM'
		hasFrom = wordParser.startsWithIdentifier(FROM);

		if (hasFrom) {
			wordParser.moveForward(FROM);
			hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the string primary
		super.parseEncapsulatedExpression(wordParser, tolerant);

		// The trim character is actually the string primary
		if (!hasFrom         &&
		    !hasExpression() &&
		     hasTrimCharacter()) {

			setExpression(trimCharacter);
			trimCharacter = null;

			if (hasSpaceAfterTrimCharacter) {
				hasSpaceAfterTrimCharacter = false;
				wordParser.moveBackward(1);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return TRIM;
	}

	private Specification parseTrimSpecification(WordParser wordParser) {

		if (wordParser.startsWithIdentifier(BOTH)) {
			return Specification.BOTH;
		}

		if (wordParser.startsWithIdentifier(LEADING)) {
			return Specification.LEADING;
		}

		if (wordParser.startsWithIdentifier(TRAILING)) {
			return Specification.TRAILING;
		}

		return Specification.DEFAULT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean includeVirtual) {

		// Trim specification
		if (hasSpecification()) {
			writer.append(specification);
		}

		if (hasSpaceAfterSpecification) {
			writer.append(SPACE);
		}

		// Trim character
		if (hasTrimCharacter()) {
			trimCharacter.toParsedText(writer, includeVirtual);
		}

		if (hasSpaceAfterTrimCharacter) {
			writer.append(SPACE);
		}

		// 'FROM'
		if (hasFrom) {
			writer.append(FROM);
		}

		if (hasSpaceAfterFrom) {
			writer.append(SPACE);
		}

		// String primary
		super.toParsedTextEncapsulatedExpression(writer, includeVirtual);
	}

	/**
	 * The possible ways to trim the string.
	 */
	public enum Specification {

		/**
		 * The leading and trailing parts of the string will be trimmed.
		 */
		BOTH,

		/**
		 * Used when the trim specification is not specified, by default it means the leading and
		 * trailing parts of the string will be trimmed.
		 */
		DEFAULT,

		/**
		 * Only the leading part of the string will be trimmed.
		 */
		LEADING,

		/**
		 * Only the trailing part of the string will be trimmed.
		 */
		TRAILING
	}
}