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
package org.eclipse.persistence.jpa.jpql.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * This is the abstract definition of all the parts used to create the tree hierarchy representing
 * the parsed JPQL query. It has the core of the parsing engine and of the content assist behavior
 * (retrieval of the possible {@link ExpressionFactory expression factories}).
 *
 * @see ExpressionFactory
 * @see StringExpression
 * @see JPQLGrammar
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractExpression extends StringExpression
                                         implements Expression {

	/**
	 * The string representation of this {@link Expression}, which needs to include any characters
	 * that are considered virtual, i.e. that was parsed when the query is incomplete and is needed
	 * for functionality like content assist.
	 *
	 * @see #toActualText()
	 */
	private String actualText;

	/**
	 * The children of this {@link AbstractExpression}.
	 *
	 * @see #children()
	 */
	private List<Expression> children;

	/**
	 * The string representation of this {@link AbstractExpression}.
	 *
	 * @see #orderedChildren()
	 */
	private List<StringExpression> orderedChildren;

	/**
	 * The parent of this expression or <code>null</code> if this is the root of the parsed tree
	 * hierarchy.
	 */
	private AbstractExpression parent;

	/**
	 * The string representation of this {@link AbstractExpression}.
	 *
	 * @see #toParsedText()
	 */
	private String parsedText;

	/**
	 * If this expression has an identifier or a single value, then it's possible it's using this
	 * text to store it.
	 */
	private String text;

	/**
	 * The constant for ','.
	 */
	public static final char COMMA = ',';

	/**
	 * The constant for '.'.
	 */
	public static final char DOT = '.';

	/**
	 * The constant for '"'.
	 */
	public static final char DOUBLE_QUOTE = '\"';

	/**
	 * The constant for '{'.
	 */
	public static final char LEFT_CURLY_BRACKET = '{';

	/**
	 * The constant for '('.
	 */
	public static final char LEFT_PARENTHESIS = '(';

	/**
	 * The constant for a character that is not defined.
	 */
	public static final char NOT_DEFINED = '\0';

	/**
	 * The constant for '}'.
	 */
	public static final char RIGHT_CURLY_BRACKET = '}';

	/**
	 * The constant for ')'.
	 */
	public static final char RIGHT_PARENTHESIS = ')';

	/**
	 * The constant for '''.
	 */
	public static final char SINGLE_QUOTE = '\'';

	/**
	 * The constant for ' '.
	 */
	public static final char SPACE = ' ';

	/**
	 * The constant for '_'.
	 */
	public static final char UNDERSCORE = '_';

	/**
	 * Creates a new <code>AbstractExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	protected AbstractExpression(AbstractExpression parent) {
		this(parent, ExpressionTools.EMPTY_STRING);
	}

	/**
	 * Creates a new <code>AbstractExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param text The text to be stored in this expression, <code>null</code> cannot be passed
	 */
	protected AbstractExpression(AbstractExpression parent, String text) {
		super();
		this.text   = text;
		this.parent = parent;
	}

	/**
	 * The given {@link ExpressionVisitor} needs to visit this class but it is defined by a
	 * third-party provider. This method will programmatically invoke the <b>visit</b> method defined
	 * on the given visitor which signature should be.
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(ThirdPartyExpression expression)</code>
	 * <p>
	 * or
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(Expression expression)</code><p>
	 *
	 * @param visitor The {@link ExpressionVisitor} to visit this {@link Expression} programmatically
	 * @return <code>true</code> if the call was successfully executed; <code>false</code> otherwise
	 * @since 2.4
	 */
	protected boolean acceptUnknownVisitor(ExpressionVisitor visitor) {
		try {
			try {
				acceptUnknownVisitor(visitor, visitor.getClass(), getClass());
			}
			catch (NoSuchMethodException e) {
				// Try with Expression has the parameter type
				acceptUnknownVisitor(visitor, visitor.getClass(), Expression.class);
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
	 * The given {@link ExpressionVisitor} needs to visit this class but it is defined by a
	 * third-party provider. This method will programmatically invoke the <b>visit</b> method defined
	 * on the given visitor which signature should be.
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(ThirdPartyExpression expression)</code>
	 * <p>
	 * or
	 * <p>
	 * <div nowrap><code>{public|protected|private} void visit(Expression expression)</code><p>
	 *
	 * @param visitor The {@link ExpressionVisitor} to visit this {@link Expression} programmatically
	 * @param type The type found in the hierarchy of the given {@link ExpressionVisitor} that will
	 * be used to retrieve the visit method
	 * @param parameterType The parameter type of the visit method
	 * @return <code>true</code> if the call was successfully executed; <code>false</code> otherwise
	 * @see #acceptUnknownVisitor(ExpressionVisitor)
	 * @since 2.4
	 */
	protected void acceptUnknownVisitor(ExpressionVisitor visitor,
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
	 * Adds the children of this {@link Expression} to the given collection.
	 *
	 * @param children The collection used to store the children
	 */
	protected void addChildrenTo(Collection<Expression> children) {
	}

	/**
	 * Adds the {@link StringExpression StringExpressions} representing this {@link Expression}.
	 *
	 * @param children The list used to store the string representation of this {@link Expression}
	 */
	protected void addOrderedChildrenTo(List<StringExpression> children) {
	}

	/**
	 * No factories were found to create an {@link Expression} with the content of {@link WordParser},
	 * this method will retrieve the fallback {@link ExpressionFactory} defined in the given {@link
	 * JPQLQueryBNF BNF}.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param word The word that was retrieved from the given text, which is the first word in the text
	 * @param queryBNF The {@link JPQLQueryBNF} used to determine how to parse from the current
	 * position of the cursor within the JPQL query
	 * @param expression The {@link Expression} that has just been parsed or <code>null</code>
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 * @return The {@link Expression} representing the given sub-query
	 */
	protected final AbstractExpression buildExpressionFromFallingBack(WordParser wordParser,
	                                                                  String word,
	                                                                  JPQLQueryBNF queryBNF,
	                                                                  AbstractExpression expression,
	                                                                  boolean tolerant) {

		ExpressionFactory factory = findFallBackExpressionFactory(queryBNF);

		if (factory == null) {
			return null;
		}

		// When parsing an invalid or incomplete query, it is possible two literals would be parsed
		// but in some cases, a CollectionExpression should not be created and the parsing should
		// actually stop here. Example: BETWEEN 10 20, when parsing 20, it should not be parsed as
		// part of the lower bound expression
		if (tolerant &&
		    (factory.getId() == PreLiteralExpressionFactory.ID) &&
		    shouldSkipLiteral(expression)) {

			return null;
		}

		return factory.buildExpression(this, wordParser, word, queryBNF, expression, tolerant);
	}

	/**
	 * Creates a new <code>null</code>-{@link Expression} parented with this one.
	 *
	 * @return A new <code>null</code> version of an {@link Expression}
	 */
	protected final AbstractExpression buildNullExpression() {
		return new NullExpression(this);
	}

	/**
	 * Creates a new {@link StringExpression} wrapping the given character value.
	 *
	 * @param value The character to wrap as a {@link StringExpression}
	 * @return The {@link StringExpression} representation of the given identifier where the owning
	 * {@link Expression} is this one
	 */
	protected final StringExpression buildStringExpression(char value) {
		return buildStringExpression(String.valueOf(value));
	}

	/**
	 * Creates a new {@link StringExpression} wrapping the given string value.
	 *
	 * @param value The string to wrap as a <code>StringExpression</code>
	 * @return The {@link StringExpression} representation of the given identifier where the owning
	 * {@link Expression} is this one
	 */
	protected final StringExpression buildStringExpression(String value) {
		return new DefaultStringExpression(this, value);
	}

	/**
	 * Creates an {@link Expression} that contains an malformed expression.
	 *
	 * @param text The text causing the expression to be malformed
	 * @return A new {@link Expression} where {@link #toText()} returns the given text
	 */
	protected final AbstractExpression buildUnknownExpression(String text) {
		return new UnknownExpression(this, text);
	}

	/**
	 * {@inheritDoc}
	 */
	public final IterableListIterator<Expression> children() {
		if (children == null) {
			children = new LinkedList<Expression>();
			addChildrenTo(children);
		}
		return new CloneListIterator<Expression>(children);
	}

	/**
	 * Retrieve the {@link ExpressionFactory} from the given {@link JPQLQueryBNF} by following the
	 * path of fallback {@link JPQLQueryBNF JPQLQueryBNFs} and then returns the {@link ExpressionFactory}
	 * from the leaf {@link JPQLQueryBNF}.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} for which its associated fallback {@ink ExpressionFactory}
	 * will be searched
	 * @return Either the fallback {@link ExpressionFactory} linked to the given {@link JPQLQueryBNF}
	 * or <code>null</code> if none was declared
	 */
	protected final ExpressionFactory findFallBackExpressionFactory(JPQLQueryBNF queryBNF) {

		String fallBackBNFId = queryBNF.getFallbackBNFId();

		// No fall back BNF is defined, then nothing can be done
		if (fallBackBNFId == null) {
			return null;
		}

		JPQLQueryBNF fallBackQueryBNF = getQueryBNF(fallBackBNFId);

		// Traverse the fall back BNF because it has its own fall back BNF
		if (fallBackQueryBNF != queryBNF &&
		    fallBackQueryBNF.getFallbackBNFId() != null) {

			return findFallBackExpressionFactory(fallBackQueryBNF);
		}

		// Retrieve the factory associated with the fall back BNF
		return getExpressionRegistry().getExpressionFactory(fallBackQueryBNF.getFallbackExpressionFactoryId());
	}

	/**
	 * Retrieves the {@link JPQLQueryBNF} that was used to parse the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} for which its BNF is needed
	 * @return The {@link JPQLQueryBNF} that was used to parse the given expression
	 */
	public JPQLQueryBNF findQueryBNF(AbstractExpression expression) {
		return getQueryBNF();
	}

	/**
	 * Retrieves the registered {@link ExpressionFactory} that was registered for the given unique
	 * identifier.
	 *
	 * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to retrieve
	 * @return The {@link ExpressionFactory} mapped with the given unique identifier
	 * @see ExpressionRegistry#getExpressionFactory(String)
	 */
	protected final ExpressionFactory getExpressionFactory(String expressionFactoryId) {
		return getExpressionRegistry().getExpressionFactory(expressionFactoryId);
	}

	/**
	 * Returns the registry containing the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link
	 * org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory ExpressionFactories} that are used
	 * to properly parse a JPQL query.
	 *
	 * @return The registry containing the information related to the JPQL grammar
	 */
	protected final ExpressionRegistry getExpressionRegistry() {
		return getGrammar().getExpressionRegistry();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLGrammar getGrammar() {
		return getRoot().getGrammar();
	}

	/**
	 * Retrieves the identifiers that are supported by the given BNF.
	 *
	 * @param queryBNFId The unique identifier of the BNF for which the supported identifiers are
	 * requested
	 * @return The list of JPQL identifiers that can be used with the BNF
	 * @see ExpressionRegistry#getIdentifiers(String)
	 */
	protected Iterable<String> getIdentifiers(String queryBNFId) {
		return getExpressionRegistry().getIdentifiers(queryBNFId);
	}

	/**
	 * Retrieves the JPA version in which the identifier was first introduced.
	 *
	 * @return The version in which the identifier was introduced
	 */
	public JPAVersion getIdentifierVersion(String identifier) {
		return getRoot().getIdentifierVersion(identifier);
	}

	/**
	 * Returns the version of the Java Persistence to support.
	 *
	 * @return The JPA version supported by the grammar
	 * @see JPQLGrammar
	 */
	protected JPAVersion getJPAVersion() {
		return getRoot().getJPAVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	public final AbstractExpression getParent() {
		return parent;
	}

	/**
	 * Returns the BNF of this {@link Expression}.
	 *
	 * @return The {@link JPQLQueryBNF}, which represents the grammar of this {@link Expression}
	 */
	public abstract JPQLQueryBNF getQueryBNF();

	/**
	 * Retrieves the BNF object that was registered for the given unique identifier.
	 *
	 * @param queryBNFID The unique identifier of the {@link JPQLQueryBNF} to retrieve
	 * @return The {@link JPQLQueryBNF} representing a section of the grammar
	 */
	public JPQLQueryBNF getQueryBNF(String queryBNFID) {
		return getExpressionRegistry().getQueryBNF(queryBNFID);
	}

	/**
	 * {@inheritDoc}
	 */
	public final JPQLExpression getRoot() {
		return (parent == null) ? (JPQLExpression) this : parent.getRoot();
	}

	/**
	 * Returns the encapsulated text of this {@link AbstractExpression}, which can be used in various
	 * ways, it can be a keyword, a literal, etc.
	 *
	 * @return The full text of this expression or a keyword, or only what this expression encapsulates
	 */
	protected String getText() {
		return text;
	}

	/**
	 * Determines whether the given {@link JPQLQueryBNF} handles aggregate expressions.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} used to determine if the parsing should handle
	 * aggregate expressions
	 * @return <code>true</code> if the given BNF handles aggregate expressions; <code>false</code>
	 * otherwise
	 */
	protected boolean handleAggregate(JPQLQueryBNF queryBNF) {
		return queryBNF.handleAggregate();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAncestor(Expression expression) {

		if (expression == this) {
			return true;
		}

		if (expression == null) {
			return false;
		}

		return isAncestor(expression.getParent());
	}

	/**
	 * Determines if the given word is a JPQL identifier. The check is case insensitive.
	 *
	 * @param word The word to test if it is a JPQL identifier
	 * @return <code>true</code> if the word is an identifier, <code>false</code> otherwise
	 * @see ExpressionRegistry#isIdentifier(String)
	 */
	protected final boolean isIdentifier(String word) {
		return getExpressionRegistry().isIdentifier(word);
	}

	/**
	 * Determines whether this expression is a <code>null</code> {@link Expression} or any other subclass.
	 *
	 * @return <code>false</code> by default
	 */
	protected boolean isNull() {
		return false;
	}

	/**
	 * Determines whether the parsing is complete based on what is left in the given text. The text
	 * is never empty.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param word The word that was retrieved from the given text, which is the first word in the text
	 * @param expression The {@link Expression} that has already been parsed
	 * @return <code>true</code> if the text no longer can't be parsed by the current expression;
	 * <code>false</code> if more can be parsed
	 */
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(FROM)               ||
		       word.equalsIgnoreCase(WHERE)              ||
		       word.equalsIgnoreCase(HAVING)             ||
		       wordParser.startsWithIdentifier(GROUP_BY) ||
		       wordParser.startsWithIdentifier(ORDER_BY);
	}

	/**
	 * Determines if the parser is in tolerant mode or is in fast mode. When the tolerant is turned
	 * on, it means the parser will attempt to parse incomplete or invalid queries.
	 *
	 * @return <code>true</code> if the parsing system should parse invalid or incomplete queries;
	 * <code>false</code> when the query is well-formed and valid
	 */
	protected boolean isTolerant() {
		return getRoot().isTolerant();
	}

	/**
	 * Determines whether this expression is an unknown {@link Expression} or any other subclass.
	 *
	 * @return <code>false</code> by default
	 */
	protected boolean isUnknown() {
		return false;
	}

	/**
	 * Determines whether this {@link AbstractExpression} is virtual, meaning it's not part of the
	 * query but is required for proper navigability.
	 *
	 * @return <code>true</code> if this {@link AbstractExpression} was virtually created to fully
	 * qualify path expression; <code>false</code> if it was parsed
	 */
	protected boolean isVirtual() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IterableListIterator<StringExpression> orderedChildren() {
		if (orderedChildren == null) {
			orderedChildren = new ArrayList<StringExpression>();
			addOrderedChildrenTo(orderedChildren);
		}
		return new CloneListIterator<StringExpression>(orderedChildren);
	}

	/**
	 * Parses the query by starting at the current position, which is part of the given {@link WordParser}.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 */
	protected abstract void parse(WordParser wordParser, boolean tolerant);

	/**
	 * Parses the given text by using the specified BNF.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param queryBNF The {@link JPQLQueryBNF} used to determine how to parse from the current
	 * position of the cursor within the JPQL query
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 * @return The {@link Expression} representing the given sub-query
	 */
	protected AbstractExpression parse(WordParser wordParser, JPQLQueryBNF queryBNF, boolean tolerant) {

		// Quick check so we don't create some objects for no reasons
		if (wordParser.isTail()) {
			return null;
		}

		List<AbstractExpression> children = new LinkedList<AbstractExpression>();
		List<Boolean> separatedByCommas = new LinkedList<Boolean>();
		List<Boolean> separatedBySpaces = new LinkedList<Boolean>();
		AbstractExpression expression = null;
		AbstractExpression child = null;
		int length = wordParser.position();
		boolean beginning = !tolerant;
		char character = wordParser.character();
		int count = 0;

		// Parse the string until the position of the cursor is at the end of the
		// string or until ParserHelper says the parsing is complete
		while (!wordParser.isTail()) {

			child = null;

			// Right away create a SubExpression and parse the encapsulated expression
			if (wordParser.startsWith(LEFT_PARENTHESIS)) {
				expression = new SubExpression(this, queryBNF.getId());
				expression.parse(wordParser, tolerant);

				beginning = false;

				// Continue to the next character/word
				count     = wordParser.skipLeadingWhitespace();
				character = wordParser.character();

				// Store the SubExpression
				children.add(expression);
				separatedByCommas.add(Boolean.FALSE);
				separatedBySpaces.add(count > 0);
			}

			// Retrieve the next word, including any arithmetic symbols
			String word = wordParser.potentialWord();

			if (word.length() > 0) {

				// Nothing more to parse
				if (!tolerant && !beginning && isParsingComplete(wordParser, word, expression) ||
				     tolerant &&               isParsingComplete(wordParser, word, expression)) {

					break;
				}

				// Parse using the ExpressionFactory that is mapped with a JPQL identifier (word)
				if (shouldParseWithFactoryFirst()) {
					ExpressionFactory factory = queryBNF.getExpressionFactory(word);

					if (factory != null) {
						child = parse(wordParser, word, factory, queryBNF, expression, tolerant);

						if (child != null) {
							expression = updateParsingInfo(expression, child, children, separatedByCommas, separatedBySpaces);

							// Continue with the next character/word
							count     = wordParser.skipLeadingWhitespace();
							character = wordParser.character();
							beginning = false;
						}
					}
				}

				// No factories could be used, use the fall back ExpressionFactory
				if (child == null) {
					child = buildExpressionFromFallingBack(wordParser, word, queryBNF, expression, tolerant);

					if (child != null) {
						expression = updateParsingInfo(expression, child, children, separatedByCommas, separatedBySpaces);

						// Continue to the next character/word
						count     = wordParser.skipLeadingWhitespace();
						character = wordParser.character();
						beginning = false;
					}
				}
			}

			if ((child != null) && (expression != null) && (expression != child)) {
				count = 0;
			}

			// If the previous length and the new length are the same, it means the
			// expression factories could not parse the text, retrieve the factory
			// so it can be parsed even though it's not valid
			if (tolerant &&
			    (child == null) &&
			    (wordParser.position() == length) &&
			    (character != COMMA)) {

				if ((word.length() > 0) && getExpressionRegistry().getIdentifierRole(word) != IdentifierRole.AGGREGATE) {
					ExpressionFactory factory = getExpressionRegistry().expressionFactoryForIdentifier(word);

					if (factory != null) {
						child = parse(wordParser, word, factory, queryBNF, expression, tolerant);

						if (child != null) {
							child = new BadExpression(this, child);
							expression = updateParsingInfo(expression, child, children, separatedByCommas, separatedBySpaces);

							// Continue with the next character/word
							character = wordParser.character();
							beginning = false;
						}
					}
				}
			}

			// No more expression can be parsed here, break here
			// so the caller can continue parsing
			if ((child == null) && (character != COMMA)) {
				break;
			}

			// Store the child but skip a very special case, which happens when parsing
			// two subqueries in a collection expression. Example: (SELECT ... ), (SELECT ... )
			if ((expression == null) || (child != null)) {
				 children.add(child);
				 separatedByCommas.add(Boolean.FALSE);
				 separatedBySpaces.add(count > 1);
			}

			// Nothing else to parse
			if (wordParser.isTail()) {
				break;
			}

			// ','
			if (character == COMMA) {

				// The current expression does not handle collection, then stop the
				// parsing here so the parent can continue to parse
				if (!queryBNF.handleCollection()) {
					break;
				}

				int collectionIndex = separatedByCommas.size() - 1;

				// Skip the comma
				wordParser.moveForward(1);
				separatedByCommas.set(collectionIndex, Boolean.TRUE);

				// Remove leading whitespace
				count = wordParser.skipLeadingWhitespace();
				separatedBySpaces.set(collectionIndex, count > 0);

				character = wordParser.character();
				expression = null;

				// Special case when we have ((), (), ())
				if (character == LEFT_PARENTHESIS) {
					continue;
				}

				// No more text, the query ends with a comma
				word = wordParser.potentialWord();
				boolean stopParsing = tolerant && (word.length() == 0 || isParsingComplete(wordParser, word, null));

				if (wordParser.isTail() || stopParsing) {
					child = null;

					// Make sure the space is not re-added at the end of the query
					count = 0;

					// Add a null Expression since the expression ends with a comma
					children.add(child);
					separatedByCommas.add(Boolean.FALSE);
					separatedBySpaces.add(Boolean.FALSE);

					// Nothing else to parse
					if (stopParsing) {
						break;
					}
				}

				// Nothing more to parse, stop right away otherwise the else
				// of the if statement below will stop the parsing
				if (character == RIGHT_PARENTHESIS) {
					break;
				}
			}
			else {
				// ')'
				if (character == RIGHT_PARENTHESIS) {
					break;
				}
				// Continue parsing the collection expression
				else if (handleAggregate(queryBNF)) {
					separatedBySpaces.set(separatedBySpaces.size() - 1, count > 0);
				}
				// Nothing more to parse
				else {
					break;
				}
			}
		}

		if (count > 0) {

			if (!separatedByCommas.isEmpty() &&
			     separatedByCommas.get(separatedByCommas.size() - 1)) {

				separatedBySpaces.set(separatedBySpaces.size() - 1, Boolean.TRUE);
			}
			else {
				wordParser.moveBackward(count);

				if (!separatedBySpaces.isEmpty()) {
					separatedBySpaces.set(separatedBySpaces.size() - 1, Boolean.FALSE);
				}
			}
		}

		// Simply return the single expression
		if (children.size() == 1 &&
		    separatedByCommas.get(0) == Boolean.FALSE &&
		    separatedBySpaces.get(0) == Boolean.FALSE) {

			expression = children.get(0);
		}
		// Return a collection expression, which wraps the sub-expressions
		else if (!children.isEmpty()) {
			expression = new CollectionExpression(this, children, separatedByCommas, separatedBySpaces);
		}
		// No query could be found, return a null expression
		else {
			expression = null;
		}

		return expression;
	}

	/**
	 * Parses the given text. When the text starts with one of the identifiers returned by the {@link
	 * ExpressionFactory}, then the factory is used to create the expression.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param word The current word to parse
	 * @param factory The factory used to determine if the text matches the criteria for it to create
	 * the expression
	 * @param queryBNF The BNF helping to parse the query
	 * @param expression The {@link Expression} that has just been parsed or <code>null</code>
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 * @return A new expression containing a portion of the text or <code>null</code> if the text
	 * doesn't meet the criteria of the factory
	 */
	protected final AbstractExpression parse(WordParser wordParser,
	                                         String word,
	                                         ExpressionFactory factory,
	                                         JPQLQueryBNF queryBNF,
	                                         AbstractExpression expression,
	                                         boolean tolerant) {

		return factory.buildExpression(this, wordParser, word, queryBNF, expression, tolerant);
	}

	/**
	 * Right away parses the text by retrieving the {@link ExpressionFactory} for the first word that
	 * is extracted from {@link WordParser} at the current location.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param queryBNF The grammar used to retrieve the {@link ExpressionFactory expression factory}
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 * @return The {@link Expression} representing the given sub-query
	 */
	protected AbstractExpression parseSingleExpression(WordParser wordParser,
	                                                   JPQLQueryBNF queryBNF,
	                                                   boolean tolerant) {

		String word = wordParser.potentialWord();
		ExpressionFactory factory = queryBNF.getExpressionFactory(word);
		return (factory == null) ? null : parse(wordParser, word, factory, queryBNF, null, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void populatePosition(QueryPosition queryPosition, int position) {

		queryPosition.addPosition(this, position);

		// The position is at the beginning of this expression
		if (position == 0) {
			queryPosition.setExpression(this);
		}
		else {
			// Traverse the children in order to find where the cursor is located
			for (StringExpression stringExpression : orderedChildren()) {

				String expressionText = stringExpression.toParsedText();

				// The position is in the StringExpression, traverse it
				if (position <= expressionText.length()) {
					stringExpression.populatePosition(queryPosition, position);
					return;
				}

				// Continue with the next child by adjusting the position
				position -= expressionText.length();
			}

			throw new IllegalStateException("The traversal to find the ExpressionFactories didn't work correctly, it shouldn't hit this.");
		}
	}

	/**
	 * Rebuilds the actual parsed text if it has been cached.
	 */
	protected final void rebuildActualText() {
		if (actualText != null) {
			toActualText();
		}
	}

	/**
	 * Rebuilds the parsed parsed text if it has been cached.
	 */
	protected final void rebuildParsedText() {
		if (parsedText != null) {
			toParsedText();
		}
	}

	/**
	 * Re-parents this {@link Expression} to be a child of the given {@link Expression}.
	 *
	 * @param parent The new parent of this object
	 */
	protected final void setParent(AbstractExpression parent) {
		this.parent = parent;
	}

	/**
	 * Sets the text of this {@link Expression}.
	 *
	 * @param text The immutable text wrapped by this {@link Expression}, which cannot be <code>null</code>
	 */
	protected final void setText(String text) {
		this.text = text;
	}

	/**
	 * Determines whether the parsing of the query should be performed using the {@link ExpressionFactory
	 * factories} first or it should automatically fallback to the fallback factory.
	 *
	 * @return <code>true</code> is returned by default so the factories are used before falling back
	 */
	protected boolean shouldParseWithFactoryFirst() {
		return true;
	}

	/**
	 * When parsing an invalid or incomplete query, it is possible two literals would be parsed but
	 * in some cases, a CollectionExpression should not be created and the parsing should actually
	 * stop here. Example: BETWEEN 10 20, when parsing 20, it should not be parsed as part of the
	 * lower bound expression.
	 *
	 * @param expression The {@link Expression} that has just been parsed or <code>null</code>
	 * @return <code>true</code>
	 */
	protected boolean shouldSkipLiteral(AbstractExpression expression) {
		return (expression != null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toActualText() {
		if (actualText == null) {
			StringBuilder writer = new StringBuilder();
			toParsedText(writer, true);
			actualText = writer.toString();
		}
		return actualText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toParsedText() {
		if (parsedText == null) {
			StringBuilder writer = new StringBuilder();
			toParsedText(writer, false);
			parsedText = writer.toString();
		}
		return parsedText;
	}

	/**
	 * Generates a string representation of this {@link Expression}, including its children,
	 * if it has any.
	 *
	 * @param writer The buffer used to append this {@link Expression}'s string representation
	 * @param actual Determines whether the string representation should represent what was parsed,
	 * i.e. include any "virtual" whitespace (such as ending whitespace) and the actual case of the
	 * JPQL identifiers
	 */
	protected abstract void toParsedText(StringBuilder writer, boolean actual);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		// toString() should only be called during debugging, hence the cached parsed text
		// should always be recreated in order to reflect the current state while debugging
		parsedText = null;
		return toParsedText();
	}

	private AbstractExpression updateParsingInfo(AbstractExpression expression,
	                                             AbstractExpression child,
	                                             List<AbstractExpression> children,
	                                             List<Boolean> separatedByCommas,
	                                             List<Boolean> separatedBySpaces) {

		// "child" is a child of the expression, remove it from the
		// collection since it's already parented
		if ((expression != null) && child.isAncestor(expression)) {

			// Also remove the indicator for comma separation
			int expressionLocation = children.indexOf(expression);

			if (expressionLocation > -1) {
				separatedByCommas.remove(expressionLocation);
				separatedBySpaces.remove(expressionLocation);
			}

			children.remove(expression);
		}

		// The child expression becomes the new current root expression
		return child;
	}
}