/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;

/**
 * This registry initializes the singleton instances of various API required for
 * parsing a Java Persistence query. It supports version 1.0 and 2.0 of the Java
 * Persistence query language as well as the EclipseLink's extension over the
 * language.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class ExpressionRegistry
{
	/**
	 * The map of {@link ExpressionFactory ExpressionFactories} that have been
	 * registered and required for parsing a JPQL query, they are mapped with
	 * their unique identifier.
	 */
	private Map<String, ExpressionFactory> expressionFactories;

	/**
	 * The set of the JPQL identifiers defined by the grammar.
	 */
	private Map<String, IdentifierRole> identifiers;

	/**
	 * This table specify in which JPA version the identifiers was introduced.
	 */
	private Map<String, IJPAVersion> identifiersVersions;

	/**
	 * The {@link JPQLQueryBNF} unique identifiers mapped to the only instance
	 * of the BNF rule.
	 */
	private Map<String, JPQLQueryBNF> queryBNFs;

	/**
	 * Creates the only instance of <code>ExpressionRegistry</code>.
	 */
	ExpressionRegistry()
	{
		super();
		initialize();
	}

	/**
	 * Retrieves the registered {@link ExpressionFactory} that was registered for
	 * the given unique identifier.
	 *
	 * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory}
	 * to retrieve
	 * @return The {@link ExpressionFactory} mapped with the given unique identifier
	 */
	ExpressionFactory expressionFactory(String expressionFactoryId)
	{
		return expressionFactories.get(expressionFactoryId);
	}

	/**
	 * Retrieves the {@link ExpressionFactory} that is responsible for creating
	 * the {@link Expression} object that represents the given JPQL identifier.
	 *
	 * @param identifier The JPQL identifier for which its factory is searched
	 * @return Either the {@link ExpressionFactory} that creates the {@link
	 * Expression} or <code>null</code> if none was found
	 */
	ExpressionFactory expressionFactoryForIdentifier(String identifier)
	{
		if (Expression.SELECT.equalsIgnoreCase(identifier))
		{
			return expressionFactory(SimpleSelectStatementFactory.ID);
		}

		for (ExpressionFactory expressionFactory : expressionFactories.values())
		{
			boolean found = Arrays.binarySearch(expressionFactory.identifiers(), identifier) > -1;

			if (found)
			{
				return expressionFactory;
			}
		}

		return null;
	}

	/**
	 * Retrieves the role of the given identifier. A role helps to describe the
	 * purpose of the identifier in a query.
	 *
	 * @param identifier The identifier for which its role is requested
	 * @return The role of the given identifier
	 */
	IdentifierRole identifierRole(String identifier)
	{
		return identifiers.get(identifier);
	}

	/**
	 * Returns the JPQL identifiers defined for Java Persistence Query version
	 * 1.0 and 2.0.
	 *
	 * @return The set of identifiers
	 */
	Collection<String> identifiers()
	{
		return identifiers.keySet();
	}

	/**
	 * Retrieves the identifiers that are supported by the given BNF.
	 *
	 * @param queryBNFId The unique identifier of the BNF for which the supported
	 * identifiers are requested
	 * @return The list of JPQL identifiers that can be used with the BNF
	 */
	Iterator<String> identifiers(String queryBNFId)
	{
		return queryBNF(queryBNFId).identifiers();
	}

	/**
	 * Retrieves the JPA version in which the identifier was first introduced.
	 *
	 * @return The version in which the identifier was introduced
	 */
	IJPAVersion identifierVersion(String identifier)
	{
		IJPAVersion version = identifiersVersions.get(identifier);
		return (version != null) ? version : IJPAVersion.VERSION_1_0;
	}

	/**
	 * Instantiates the only instance of various API used by the parser.
	 */
	private void initialize()
	{
		queryBNFs           = new HashMap<String, JPQLQueryBNF>();
		identifiers         = new HashMap<String, IdentifierRole>();
		expressionFactories = new HashMap<String, ExpressionFactory>();
		identifiersVersions = new HashMap<String, IJPAVersion>();

		initializeIdentifiers();
		initializeExpressionFactories();
		initializeBNFs();

		identifiers = Collections.unmodifiableMap(identifiers);
	}

	/**
	 * Creates a map where the key is a unique ID and the value is an
	 * {@link JPQLQueryBNF} representing a portion of the JPQL grammar.
	 */
	private void initializeBNFs()
	{
		registerBNF(new AbstractSchemaNameBNF());
		registerBNF(new AggregateExpressionBNF());
		registerBNF(new AllOrAnyExpressionBNF());
		registerBNF(new ArithmeticExpressionBNF());
		registerBNF(new ArithmeticFactorBNF());
		registerBNF(new ArithmeticPrimaryBNF());
		registerBNF(new ArithmeticTermBNF());
		registerBNF(new BadExpressionBNF());
		registerBNF(new BetweenExpressionBNF());
		registerBNF(new BooleanExpressionBNF());
		registerBNF(new BooleanLiteralBNF());
		registerBNF(new BooleanPrimaryBNF());
		registerBNF(new CaseExpressionBNF());
		registerBNF(new CaseOperandBNF());
		registerBNF(new CoalesceExpressionBNF());
		registerBNF(new CollectionMemberDeclarationBNF());
		registerBNF(new CollectionMemberExpressionBNF());
		registerBNF(new CollectionValuedPathExpressionBNF());
		registerBNF(new ComparisonExpressionBNF());
		registerBNF(new ConditionalExpressionBNF());
		registerBNF(new ConditionalFactorBNF());
		registerBNF(new ConditionalPrimaryBNF());
		registerBNF(new ConditionalTermBNF());
		registerBNF(new ConstructorExpressionBNF());
		registerBNF(new ConstructorItemBNF());
		registerBNF(new DatetimeExpressionBNF());
		registerBNF(new DateTimePrimaryBNF());
		registerBNF(new DateTimeTimestampLiteralBNF());
		registerBNF(new DeleteClauseBNF());
		registerBNF(new DeleteClauseRangeVariableDeclarationBNF());
		registerBNF(new DeleteStatementBNF());
		registerBNF(new DerivedCollectionMemberDeclarationBNF());
		registerBNF(new ElseExpressionBNF());
		registerBNF(new EmptyCollectionComparisonExpressionBNF());
		registerBNF(new EntityExpressionBNF());
		registerBNF(new EntityOrValueExpressionBNF());
		registerBNF(new EntityTypeExpressionBNF());
		registerBNF(new EnumExpressionBNF());
		registerBNF(new EnumLiteralBNF());
		registerBNF(new EnumPrimaryBNF());
		registerBNF(new ExistsExpressionBNF());
		registerBNF(new FromClauseBNF());
		registerBNF(new FunctionsReturningDatetimeBNF());
		registerBNF(new FunctionsReturningNumericsBNF());
		registerBNF(new FunctionsReturningStringsBNF());
		registerBNF(new GeneralCaseExpressionBNF());
		registerBNF(new GeneralIdentificationVariableBNF());
		registerBNF(new GroupByClauseBNF());
		registerBNF(new GroupByItemBNF());
		registerBNF(new HavingClauseBNF());
		registerBNF(new IdentificationVariableBNF());
		registerBNF(new IdentificationVariableDeclarationBNF());
		registerBNF(new InExpressionBNF());
		registerBNF(new InItemBNF());
		registerBNF(new InputParameterBNF());
		registerBNF(new InternalBetweenExpressionBNF());
		registerBNF(new InternalCoalesceExpressionBNF());
		registerBNF(new InternalCountBNF());
		registerBNF(new InternalEntityTypeExpressionBNF());
		registerBNF(new InternalFromClauseBNF());
		registerBNF(new InternalJoinBNF());
		registerBNF(new InternalOrderByClauseBNF());
		registerBNF(new InternalOrderByItemBNF());
		registerBNF(new InternalSimpleFromClauseBNF());
		registerBNF(new InternalUpdateClauseBNF());
		registerBNF(new InternalWhenClauseBNF());
		registerBNF(new JoinAssociationPathExpressionBNF());
		registerBNF(new JoinBNF());
		registerBNF(new JoinFetchBNF());
		registerBNF(new JPQLStatementBNF());
		registerBNF(new LikeExpressionBNF());
		registerBNF(new NewValueBNF());
		registerBNF(new NullComparisonExpressionBNF());
		registerBNF(new NullIfExpressionBNF());
		registerBNF(new NumericLiteralBNF());
		registerBNF(new ObjectExpressionBNF());
		registerBNF(new OrderByClauseBNF());
		registerBNF(new OrderByItemBNF());
		registerBNF(new PatternValueBNF());
		registerBNF(new PreLiteralExpressionBNF());
		registerBNF(new QualifiedIdentificationVariableBNF());
		registerBNF(new RangeVariableDeclarationBNF());
		registerBNF(new ResultVariableBNF());
		registerBNF(new ScalarExpressionBNF());
		registerBNF(new SelectClauseBNF());
		registerBNF(new SelectExpressionBNF());
		registerBNF(new SelectItemBNF());
		registerBNF(new SelectStatementBNF());
		registerBNF(new SimpleArithmeticExpressionBNF());
		registerBNF(new SimpleCaseExpressionBNF());
		registerBNF(new SimpleConditionalExpressionBNF());
		registerBNF(new SimpleEntityExpressionBNF());
		registerBNF(new SimpleEntityOrValueExpressionBNF());
		registerBNF(new SimpleSelectClauseBNF());
		registerBNF(new SimpleSelectExpressionBNF());
		registerBNF(new SingleValuedObjectPathExpressionBNF());
		registerBNF(new SingleValuedPathExpressionBNF());
		registerBNF(new StateFieldPathExpressionBNF());
		registerBNF(new StringExpressionBNF());
		registerBNF(new StringLiteralBNF());
		registerBNF(new StringPrimaryBNF());
		registerBNF(new SubConditionalExpressionBNF());
		registerBNF(new SubQueryBNF());
		registerBNF(new SubQueryFromClauseBNF());
		registerBNF(new SubSelectIdentificationVariableDeclarationBNF());
		registerBNF(new SubSimpleArithmeticExpressionBNF());
		registerBNF(new TypeExpressionBNF());
		registerBNF(new UpdateClauseBNF());
		registerBNF(new UpdateItemBNF());
		registerBNF(new UpdateItemStateFieldPathExpressionBNF());
		registerBNF(new UpdateStatementBNF());
		registerBNF(new WhenClauseBNF());
		registerBNF(new WhereClauseBNF());

		// EclipseLink's extension
		registerBNF(new FuncExpressionBNF());
		registerBNF(new InternalFuncExpressionBNF());
	}

	/**
	 * Creates a map where the key is an identifier and the value is an
	 * {@link ExpressionFactory} responsible to create the actual
	 * {@link Expression}.
	 */
	private void initializeExpressionFactories()
	{
		// JPA version 1.0
		registerFactory(new AbsExpressionFactory());
		registerFactory(new AbstractSchemaNameFactory());
		registerFactory(new AllOrAnyExpressionFactory());
		registerFactory(new AndExpressionFactory());
		registerFactory(new ArithmeticExpressionFactory());
		registerFactory(new AvgFunctionFactory());
		registerFactory(new BadExpressionFactory());
		registerFactory(new BasicLiteralExpressionFactory());
		registerFactory(new BetweenExpressionFactory());
		registerFactory(new CollectionMemberDeclarationFactory());
		registerFactory(new CollectionMemberExpressionFactory());
		registerFactory(new CollectionValuedPathExpressionFactory());
		registerFactory(new ComparisonExpressionFactory());
		registerFactory(new ConcatExpressionFactory());
		registerFactory(new ConstructorExpressionFactory());
		registerFactory(new CountFunctionFactory());
		registerFactory(new DateTimeFactory());
		registerFactory(new DeleteClauseFactory());
		registerFactory(new DeleteStatementFactory());
		registerFactory(new EmptyCollectionComparisonExpressionFactory());
		registerFactory(new ExistsExpressionFactory());
		registerFactory(new FromClauseFactory());
		registerFactory(new GroupByClauseFactory());
		registerFactory(new GroupByItemFactory());
		registerFactory(new HavingClauseFactory());
		registerFactory(new IdentificationVariableDeclarationFactory());
		registerFactory(new IdentificationVariableFactory());
		registerFactory(new InExpressionFactory());
		registerFactory(new InternalOrderByItemFactory());
		registerFactory(new IsExpressionFactory());
		registerFactory(new JoinFactory());
		registerFactory(new KeywordExpressionFactory());
		registerFactory(new LengthExpressionFactory());
		registerFactory(new LikeExpressionFactory());
		registerFactory(new LiteralExpressionFactory());
		registerFactory(new LocateExpressionFactory());
		registerFactory(new LowerExpressionFactory());
		registerFactory(new MaxFunctionFactory());
		registerFactory(new MinFunctionFactory());
		registerFactory(new ModExpressionFactory());
		registerFactory(new NotExpressionFactory());
		registerFactory(new NullComparisonExpressionFactory());
		registerFactory(new ObjectExpressionFactory());
		registerFactory(new OrderByClauseFactory());
		registerFactory(new OrderByItemFactory());
		registerFactory(new OrExpressionFactory());
		registerFactory(new PreLiteralExpressionFactory());
		registerFactory(new RangeVariableDeclarationFactory());
		registerFactory(new SelectClauseFactory());
		registerFactory(new SelectStatementFactory());
		registerFactory(new SimpleSelectStatementFactory());
		registerFactory(new SizeExpressionFactory());
		registerFactory(new SqrtExpressionFactory());
		registerFactory(new StateFieldPathExpressionFactory());
		registerFactory(new StringLiteralFactory());
		registerFactory(new SubstringExpressionFactory());
		registerFactory(new SumFunctionFactory());
		registerFactory(new TrimExpressionFactory());
		registerFactory(new UnknownExpressionFactory());
		registerFactory(new UpdateClauseFactory());
		registerFactory(new UpdateItemFactory());
		registerFactory(new UpdateItemStateFieldPathExpressionFactory());
		registerFactory(new UpdateStatementFactory());
		registerFactory(new UpperExpressionFactory());
		registerFactory(new WhereClauseFactory());

		// JPA version 2.0
		registerFactory(new CaseExpressionFactory());
		registerFactory(new CoalesceExpressionFactory());
		registerFactory(new EntityTypeLiteralFactory());
		registerFactory(new EntryExpressionFactory());
		registerFactory(new KeyExpressionFactory());
		registerFactory(new IndexExpressionFactory());
		registerFactory(new NullIfExpressionFactory());
		registerFactory(new ResultVariableFactory());
		registerFactory(new TypeExpressionFactory());
		registerFactory(new ValueExpressionFactory());
		registerFactory(new WhenClauseFactory());

		// EclipseLink's extension
		registerFactory(new FuncExpressionFactory());
	}

	/**
	 * Creates the list of JPQL identifiers, which cannot be used as
	 * identification variables.
	 */
	private void initializeIdentifiers()
	{
		// JPA version 1.0
		identifiers.put(Expression.ABS,                   IdentifierRole.FUNCTION);           // ABS(x)
		identifiers.put(Expression.ALL,                   IdentifierRole.FUNCTION);           // ALL(x)
		identifiers.put(Expression.AND,                   IdentifierRole.AGGREGATE);
		identifiers.put(Expression.ANY,                   IdentifierRole.FUNCTION);           // ANY(x)
		identifiers.put(Expression.AS,                    IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.ASC,                   IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.AVG,                   IdentifierRole.FUNCTION);           // AVG(x)
		identifiers.put(Expression.BETWEEN,               IdentifierRole.COMPOUND_FUNCTION);  // x BETWEEN y AND z
		identifiers.put(Expression.BIT_LENGTH,            IdentifierRole.UNUSED);
		identifiers.put(Expression.BOTH,                  IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.CHAR_LENGTH,           IdentifierRole.UNUSED);
		identifiers.put(Expression.CHARACTER_LENGTH,      IdentifierRole.UNUSED);
		identifiers.put(Expression.CLASS,                 IdentifierRole.UNUSED);
		identifiers.put(Expression.CONCAT,                IdentifierRole.FUNCTION);           // CONCAT(x, y)
		identifiers.put(Expression.COUNT,                 IdentifierRole.FUNCTION);           // COUNT(x)
		identifiers.put(Expression.CURRENT_DATE,          IdentifierRole.FUNCTION);
		identifiers.put(Expression.CURRENT_TIME,          IdentifierRole.FUNCTION);
		identifiers.put(Expression.CURRENT_TIMESTAMP,     IdentifierRole.FUNCTION);
		identifiers.put(Expression.DELETE,                IdentifierRole.CLAUSE);
		identifiers.put(Expression.DELETE_FROM,           IdentifierRole.CLAUSE);
		identifiers.put(Expression.DESC,                  IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.DISTINCT,              IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.EMPTY,                 IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.ESCAPE,                IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.EXISTS,                IdentifierRole.FUNCTION);           // EXISTS(x)
		identifiers.put(Expression.FALSE,                 IdentifierRole.FUNCTION);
		identifiers.put(Expression.FETCH,                 IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.FROM,                  IdentifierRole.CLAUSE);
		identifiers.put(Expression.HAVING,                IdentifierRole.AGGREGATE);
		identifiers.put(Expression.IN,                    IdentifierRole.COMPOUND_FUNCTION);  // x IN { (y {, z}* | (s) | t }
		identifiers.put(Expression.INNER,                 IdentifierRole.COMPOUND_FUNCTION);  // Part of JOIN
		identifiers.put(Expression.IS,                    IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.JOIN,                  IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.LEADING,               IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.LEFT,                  IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.LENGTH,                IdentifierRole.FUNCTION);           // LENGTH(x)
		identifiers.put(Expression.LIKE,                  IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.LOCATE,                IdentifierRole.FUNCTION);           // LOCATE(x, y [, z]))
		identifiers.put(Expression.LOWER,                 IdentifierRole.FUNCTION);           // LOWER(x)
		identifiers.put(Expression.MAX,                   IdentifierRole.FUNCTION);           // MAX(x)
		identifiers.put(Expression.MEMBER,                IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.MIN,                   IdentifierRole.FUNCTION);           // MIN(x)
		identifiers.put(Expression.MOD,                   IdentifierRole.FUNCTION);           // MOD(x, y)
		identifiers.put(Expression.NEW,                   IdentifierRole.FUNCTION);           // NEW x (y {, z}*)
		identifiers.put(Expression.NOT,                   IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.NULL,                  IdentifierRole.FUNCTION);
		identifiers.put(Expression.OBJECT,                IdentifierRole.FUNCTION);           // OBJECT(x)
		identifiers.put(Expression.OF,                    IdentifierRole.COMPOUND_FUNCTION);  // Part of MEMBER [OF]
		identifiers.put(Expression.OR,                    IdentifierRole.AGGREGATE);
		identifiers.put(Expression.OUTER,                 IdentifierRole.COMPLETEMENT);       // Part of JOIN
		identifiers.put(Expression.POSITION,              IdentifierRole.UNUSED);
		identifiers.put(Expression.SELECT,                IdentifierRole.CLAUSE);
		identifiers.put(Expression.SET,                   IdentifierRole.CLAUSE);
		identifiers.put(Expression.SIZE,                  IdentifierRole.FUNCTION);           // SIZE(x)
		identifiers.put(Expression.SOME,                  IdentifierRole.FUNCTION);           // SOME(x)
		identifiers.put(Expression.SQRT,                  IdentifierRole.FUNCTION);           // SQRT(x)
		identifiers.put(Expression.SUBSTRING,             IdentifierRole.FUNCTION);           // SUBSTRING(x, y {, z})
		identifiers.put(Expression.SUM,                   IdentifierRole.FUNCTION);
		identifiers.put(Expression.TRAILING,              IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.TRIM,                  IdentifierRole.FUNCTION);           // TRIM([[x [c] FROM] y)
		identifiers.put(Expression.TRUE,                  IdentifierRole.FUNCTION);
		identifiers.put(Expression.UNKNOWN,               IdentifierRole.UNUSED);
		identifiers.put(Expression.UPDATE,                IdentifierRole.CLAUSE);
		identifiers.put(Expression.UPPER,                 IdentifierRole.FUNCTION);           // UPPER(x)
		identifiers.put(Expression.WHEN,                  IdentifierRole.COMPOUND_FUNCTION);  // Part of CASE WHEN ELSE END
		identifiers.put(Expression.WHERE,                 IdentifierRole.CLAUSE);
		identifiers.put(Expression.PLUS,                  IdentifierRole.AGGREGATE);
		identifiers.put(Expression.MINUS,                 IdentifierRole.AGGREGATE);
		identifiers.put(Expression.MULTIPLICATION,        IdentifierRole.AGGREGATE);
		identifiers.put(Expression.DIVISION,              IdentifierRole.AGGREGATE);
		identifiers.put(Expression.LOWER_THAN,            IdentifierRole.AGGREGATE);
		identifiers.put(Expression.LOWER_THAN_OR_EQUAL,   IdentifierRole.AGGREGATE);
		identifiers.put(Expression.GREATER_THAN,          IdentifierRole.AGGREGATE);
		identifiers.put(Expression.GREATER_THAN_OR_EQUAL, IdentifierRole.AGGREGATE);
		identifiers.put(Expression.DIFFERENT,             IdentifierRole.AGGREGATE);
		identifiers.put(Expression.EQUAL,                 IdentifierRole.AGGREGATE);

		// Composite Identifiers
		identifiers.put(Expression.GROUP_BY,              IdentifierRole.CLAUSE);
		identifiers.put(Expression.LEFT_JOIN,             IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.LEFT_JOIN_FETCH,       IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.LEFT_OUTER_JOIN,       IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.LEFT_OUTER_JOIN_FETCH, IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.INNER_JOIN,            IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.INNER_JOIN_FETCH,      IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.IS_EMPTY,              IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.IS_NOT_EMPTY,          IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.IS_NOT_NULL,           IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.IS_NULL,               IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.JOIN_FETCH,            IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.MEMBER_OF,             IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER OF y
		identifiers.put(Expression.NOT_BETWEEN,           IdentifierRole.COMPOUND_FUNCTION);  // x NOT BETWEEN y AND z
		identifiers.put(Expression.NOT_EXISTS,            IdentifierRole.FUNCTION);           // NOT EXISTS(x)
		identifiers.put(Expression.NOT_IN,                IdentifierRole.COMPOUND_FUNCTION);  // x NOT IN { (y {, z}* | (s) | t }
		identifiers.put(Expression.NOT_LIKE,              IdentifierRole.COMPOUND_FUNCTION);  // x NOT LIKE y
		identifiers.put(Expression.NOT_MEMBER,            IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER y
		identifiers.put(Expression.NOT_MEMBER_OF,         IdentifierRole.COMPOUND_FUNCTION);  // x NOT MEMBER OF y
		identifiers.put(Expression.ORDER_BY,              IdentifierRole.CLAUSE);

		// Partial Identifiers
		identifiers.put("BY",                             IdentifierRole.CLAUSE);             // Part of GROUP BY, ORDER BY
		identifiers.put("DELETE",                         IdentifierRole.CLAUSE);
		identifiers.put("GROUP",                          IdentifierRole.CLAUSE);
		identifiers.put("ORDER",                          IdentifierRole.CLAUSE);

		// JPA version 2.0
		identifiers.put(Expression.CASE,                  IdentifierRole.FUNCTION);           // ???
		identifiers.put(Expression.COALESCE,              IdentifierRole.FUNCTION);           // COALLESCE(x {, y}+)
		identifiers.put(Expression.ELSE,                  IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.END,                   IdentifierRole.COMPLETEMENT);
		identifiers.put(Expression.ENTRY,                 IdentifierRole.FUNCTION);           // ENTRY(x)
		identifiers.put(Expression.INDEX,                 IdentifierRole.FUNCTION);           // INDEX(x)
		identifiers.put(Expression.KEY,                   IdentifierRole.FUNCTION);           // KEY(x)
		identifiers.put(Expression.NULLIF,                IdentifierRole.FUNCTION);           // NULLIF(x, y)
		identifiers.put(Expression.THEN,                  IdentifierRole.COMPOUND_FUNCTION);
		identifiers.put(Expression.TYPE,                  IdentifierRole.FUNCTION);           // TYPE(x)
		identifiers.put(Expression.VALUE,                 IdentifierRole.FUNCTION);           // VALUE(x)

		identifiersVersions.put(Expression.CASE,          IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.COALESCE,      IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.ELSE,          IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.END,           IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.ENTRY,         IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.INDEX,         IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.KEY,           IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.NULLIF,        IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.THEN,          IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.TYPE,          IJPAVersion.VERSION_2_0);
		identifiersVersions.put(Expression.VALUE,         IJPAVersion.VERSION_2_0);

		// EclipseLink's extension
		identifiers.put(Expression.FUNC,                  IdentifierRole.FUNCTION);

		identifiersVersions.put(Expression.FUNC,          IJPAVersion.VERSION_2_0);
	}

	/**
	 * Determines if the given word is a JPQL identifier. The check is case
	 * insensitive.
	 *
	 * @param word The word to test if it's a JPQL identifier
	 * @return <code>true</code> if the word is an identifier, <code>false</code>
	 * otherwise
	 */
	boolean isIdentifier(String word)
	{
		return identifiers.containsKey(word.toUpperCase());
	}

	/**
	 * Retrieves the BNF object that was registered for the given unique identifier.
	 *
	 * @param queryBNFID The unique identifier of the {@link JPQLQueryBNF} to
	 * retrieve
	 * @return The {@link JPQLQueryBNF} representing a section of the grammar
	 */
	@SuppressWarnings("unchecked")
	<T extends JPQLQueryBNF> T queryBNF(String queryBNFID)
	{
		return (T) queryBNFs.get(queryBNFID);
	}

	/**
	 * Registers the given {@link ExpressionFactory} by storing it for all its
	 * identifiers.
	 *
	 * @param expressionFactory The {@link ExpressionFactory} to store
	 */
	private void registerBNF(JPQLQueryBNF queryBNF)
	{
		String id = queryBNF.getId();
		queryBNF = queryBNFs.put(id, queryBNF);

		if (queryBNF != null)
		{
			throw new IllegalArgumentException("A JPQLQueryBNF is already registered with the id " + id);
		}
	}

	/**
	 * Registers the given {@link ExpressionFactory} by storing it for all its
	 * identifiers.
	 *
	 * @param expressionFactory The {@link ExpressionFactory} to store
	 */
	private void registerFactory(ExpressionFactory expressionFactory)
	{
		String id = expressionFactory.getId();
		expressionFactory = expressionFactories.put(id, expressionFactory);

		if (expressionFactory != null)
		{
			throw new IllegalArgumentException("An ExpressionFactory is already registered with the id " + id);
		}
	}
}