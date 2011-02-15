/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.QueryBNFAccessor;
import org.eclipse.persistence.utils.jpa.query.spi.IEntity;
import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This unit-test is responsible to test the JPQL content assist.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ContentAssistTest extends AbstractQueryTest
{
	protected Iterator<String> abstractSchemaNames()
	{
		List<String> names = new ArrayList<String>();
		names.add("Address");
		names.add("Alias");
		names.add("Customer");
		names.add("Dept");
		names.add("Employee");
		names.add("Home");
		names.add("Order");
		names.add("Phone");
		names.add("Product");
		names.add("Project");
		return names.iterator();
	}

	protected List<String> addAll(List<String> items1, Iterator<String> items2)
	{
		while (items2.hasNext())
		{
			items1.add(items2.next());
		}
		return items1;
	}

	private IQuery buildQuery(String query, IJPAVersion version)
	{
		return new Query(query, version);
	}

	protected ContentAssistItems contentAssistItems(IJPAVersion version,
	                                                String actualQuery,
	                                                int position)
	{
		ContentAssistProvider provider = new ContentAssistProvider
		(
			buildQuery(actualQuery, version),
			position,
			false
		);
		return provider.items();
	}

	protected ContentAssistItems contentAssistItems(IQuery query, int position)
	{
		ContentAssistProvider provider = new ContentAssistProvider(query, position);
		return provider.items();
	}

	protected ContentAssistItems contentAssistItems(String actualQuery, int position)
	{
		return contentAssistItems(IJPAVersion.VERSION_2_0, actualQuery, position);
	}

	protected IEntity entity(String entityName) throws Exception
	{
		IEntity entity = (IEntity) persistenceUnit().getManagedType(entityName);
		assertNotNull("The named query count not be found", entity);
		return entity;
	}

	protected String[] enumNames(Enum<?>[] enums)
	{
		String[] names = new String[enums.length];

		for (int index = enums.length; --index >= 0; )
		{
			names[index] = enums[index].toString();
		}

		return names;
	}
	protected Iterator<String> filter(Iterator<String> identifiers, String startsWith)
	{
		List<String> names = new ArrayList<String>();
		startsWith = startsWith.toUpperCase();
		while (identifiers.hasNext())
		{
			String name = identifiers.next();
			if (name.toUpperCase().startsWith(startsWith))
			{
				names.add(name);
			}
		}
		return names.iterator();
	}

	protected Iterator<String> filteredAbstractSchemaNames(String startsWith)
	{
		List<String> names = new ArrayList<String>();

		for (Iterator<String> iter = abstractSchemaNames(); iter.hasNext(); )
		{
			String name = iter.next();

			if (name.startsWith(startsWith))
			{
				names.add(name);
			}
		}

		return names.iterator();
	}

	protected void hasNoIdentifiers(String query, int position)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		assertFalse
		(
			String.format("The list still contains %s", items),
			items.hasItems()
		);
	}

	protected Iterator<String> iterator(String... values)
	{
		return Arrays.asList(values).iterator();
	}

	protected IQuery namedQuery(String entityName, String queryName) throws Exception
	{
		IEntity entity = entity(entityName);
		IQuery namedQuery = entity.getNamedQuery(queryName);
		assertNotNull("The named query count not be found", namedQuery);
		return namedQuery;
	}

	@Test
	public void test_Abs_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_02()
	{
		String query = "SELECT e FROM Employee e WHERE A";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_03()
	{
		String query = "SELECT e FROM Employee e WHERE A";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_04()
	{
		String query = "SELECT e FROM Employee e WHERE AB";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_05()
	{
		String query = "SELECT e FROM Employee e WHERE ABS";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_06()
	{
		String query = "SELECT e FROM Employee e WHERE (A";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_07()
	{
		String query = "SELECT e FROM Employee e WHERE (A";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_08()
	{
		String query = "SELECT e FROM Employee e WHERE (A)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_09()
	{
		String query = "SELECT e FROM Employee e WHERE (AB)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_Abs_10()
	{
		String query = "SELECT e FROM Employee e WHERE (ABS)";
		int position = "SELECT e FROM Employee e WHERE (ABS".length();
		testDoesNotHaveIdentifiers(query, position, Expression.ABS);
	}

	@Test
	public void test_AbstractSchemaName_01() throws Exception
	{
		String query = "SELECT e FROM Employee e";
		int position = "SELECT e FROM ".length();
		testHasOnlyIdentifiers(query, position, abstractSchemaNames());
	}

	@Test
	public void test_AbstractSchemaName_02() throws Exception
	{
		String query = "SELECT e FROM Employee e";
		int position = "SELECT e FROM E".length();
		testHasOnlyIdentifiers(query, position, "Employee");
	}

	@Test
	public void test_AbstractSchemaName_03() throws Exception
	{
		String query = "SELECT AVG(e.age) FROM ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, abstractSchemaNames());
	}

	@Test
	public void test_AbstractSchemaName_04()
	{
		String query = "SELECT e FROM ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, abstractSchemaNames());
	}

	@Test
	public void test_AbstractSchemaName_05()
	{
		String query = "SELECT e FROM E";
		int position = query.length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			filteredAbstractSchemaNames("E")
		);
	}

	@Test
	public void test_AbstractSchemaName_06()
	{
		String query = "SELECT e FROM Employee e, ";
		int position = query.length();

		List<String> items = new ArrayList<String>();
		items.add(Expression.IN);
		addAll(items, abstractSchemaNames());

		testHasOnlyIdentifiers(query, position, items);
	}

	@Test
	public void test_AllOrAnyExpression_All_1()
	{
		String query = "SELECT e FROM Employee e WHERE AL";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ALL);
	}

	@Test
	public void test_AllOrAnyExpression_All_2()
	{
		String query = "SELECT e FROM Employee e WHERE ALL";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.ALL);
	}

	@Test
	public void test_AllOrAnyExpression_All_3()
	{
		String query = "SELECT e FROM Employee e WHERE (AL)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ALL);
	}

	@Test
	public void test_AllOrAnyExpression_All_4()
	{
		String query = "SELECT e FROM Employee e WHERE (ALL)";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.ALL);
	}

	@Test
	public void test_AllOrAnyExpression_Any_1()
	{
		String query = "SELECT e FROM Employee e WHERE AN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_Any_2()
	{
		String query = "SELECT e FROM Employee e WHERE ANY";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_Any_3()
	{
		String query = "SELECT e FROM Employee e WHERE (AN)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_Any_4()
	{
		String query = "SELECT e FROM Employee e WHERE (ANY)";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_AnyOrAny_1()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_AnyOrAny_2()
	{
		String query = "SELECT e FROM Employee e WHERE A";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_AnyOrAny_3()
	{
		String query = "SELECT e FROM Employee e WHERE (";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_AnyOrAny_4()
	{
		String query = "SELECT e FROM Employee e WHERE (A";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_AnyOrAny_5()
	{
		String query = "SELECT e FROM Employee e WHERE (A)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.ANY);
	}

	@Test
	public void test_AllOrAnyExpression_Some_01()
	{
		String query = "SELECT e FROM Employee e WHERE S";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_02()
	{
		String query = "SELECT e FROM Employee e WHERE S";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_03()
	{
		String query = "SELECT e FROM Employee e WHERE SO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_04()
	{
		String query = "SELECT e FROM Employee e WHERE SOM";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_05()
	{
		String query = "SELECT e FROM Employee e WHERE SOME";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_06()
	{
		String query = "SELECT e FROM Employee e WHERE (S";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_07()
	{
		String query = "SELECT e FROM Employee e WHERE (SO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_08()
	{
		String query = "SELECT e FROM Employee e WHERE (SOM";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_09()
	{
		String query = "SELECT e FROM Employee e WHERE (SOME";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_10()
	{
		String query = "SELECT e FROM Employee e WHERE (S)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_11()
	{
		String query = "SELECT e FROM Employee e WHERE (SO)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_12()
	{
		String query = "SELECT e FROM Employee e WHERE (SOM)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_AllOrAnyExpression_Some_13()
	{
		String query = "SELECT e FROM Employee e WHERE (SOME)";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.SOME);
	}

	@Test
	public void test_As_01()
	{
		String query = "SELECT o FROM Countries ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_02()
	{
		String query = "SELECT o FROM Countries o";
		int position = "SELECT o FROM Countries ".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_03()
	{
		String query = "SELECT o FROM Countries a";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_04()
	{
		String query = "SELECT o FROM Countries o ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_05()
	{
		String query = "SELECT o FROM Countries A o";
		int position = "SELECT o FROM Countries A".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_06()
	{
		String query = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries A".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_07()
	{
		String query = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries AS".length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_08()
	{
		String query = "SELECT o FROM Countries AS o";
		int position = "SELECT o FROM Countries AS ".length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_As_09()
	{
		String query = "SELECT ABS(a.city)  FROM Address AS a";
		int position = "SELECT ABS(a.city) ".length();
		testHasIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_AvgFunction_01()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AVG);
	}

	@Test
	public void test_AvgFunction_02()
	{
		String query = "SELECT A";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AVG);
	}

	@Test
	public void test_AvgFunction_03()
	{
		String query = "SELECT AV";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AVG);
	}

	@Test
	public void test_AvgFunction_04()
	{
		String query = "SELECT AVG";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AVG);
	}

	@Test
	public void test_AvgFunction_05()
	{
		String query = "SELECT AVG(";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_06()
	{
		String query = "SELECT AVG() From Employee e";
		int position = "SELECT AVG(".length();
		testHasOnlyIdentifiers(query, position, "e", Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_07()
	{
		String query = "SELECT AVG(DISTINCT ) From Employee e";
		int position = "SELECT AVG(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_AvgFunction_08()
	{
		String query = "SELECT AVG(D ) From Employee e";
		int position = "SELECT AVG(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_09()
	{
		String query = "SELECT AVG(DI ) From Employee e";
		int position = "SELECT AVG(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_10()
	{
		String query = "SELECT AVG(DIS ) From Employee e";
		int position = "SELECT AVG(DIS".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_11()
	{
		String query = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_12()
	{
		String query = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_13()
	{
		String query = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_14()
	{
		String query = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_AvgFunction_15()
	{
		String query = "SELECT AVG(DISTINCT e) From Employee e";
		int position = "SELECT AVG(DISTINCT e".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_AvgFunction_16()
	{
		String query = "SELECT AVG(DISTINCT e) From Employee emp";
		int position = "SELECT AVG(DISTINCT e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_AvgFunction_17()
	{
		String query = "SELECT AVG() From Employee emp";
		int position = "SELECT AVG(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_18()
	{
		String query = "SELECT AVG(e) From Employee emp";
		int position = "SELECT AVG(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_AvgFunction_19()
	{
		String query = "SELECT AVG(em) From Employee emp";
		int position = "SELECT AVG(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_AvgFunction_20()
	{
		String query = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(emp".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_AvgFunction_21()
	{
		String query = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_AvgFunction_22()
	{
		String query = "SELECT AVG(emp) From Employee emp";
		int position = "SELECT AVG(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_AvgFunction_23()
	{
		String query = "SELECT AVG( From Employee emp";
		int position = "SELECT AVG(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_AvgFunction_24()
	{
		String query = "SELECT AVG(e From Employee emp";
		int position = "SELECT AVG(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_BetweenExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.age ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.age B";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BET";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETW";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN);
	}

	@Test
	public void test_BetweenExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.NOT_BETWEEN);
	}

	@Test
	public void test_BetweenExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE e.age NOT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_BETWEEN);
	}

	@Test
	public void test_BetweenExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE e.age NOT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_BETWEEN);
	}

	@Test
	public void test_BetweenExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE e.age NOT B";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.BETWEEN, Expression.NOT_BETWEEN);
	}

	@Test
	public void test_BetweenExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.AND);
	}

	@Test
	public void test_BetweenExpression_14()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 A";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.AND);
	}

	@Test
	public void test_BetweenExpression_15()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AN";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.AND);
	}

	@Test
	public void test_BetweenExpression_16()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AND";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AND);
	}

	@Test
	public void test_BetweenExpression_17()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AND ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AND);
	}

	@Test
	public void test_CollectionMemberDeclaration_01()
	{
		String query = "SELECT e FROM Employee e, I";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_02()
	{
		String query = "SELECT e FROM ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_03()
	{
		String query = "SELECT e FROM Employee e, IN(e.names) AS f";
		int position = query.length() - "e, IN(e.names) AS f".length();
		testDoesNotHaveIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_04()
	{
		String query = "SELECT e FROM Employee e, ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_05()
	{
		String query = "SELECT e FROM Employee e, I";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_06()
	{
		String query = "SELECT e FROM Employee e, IN";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_07()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AS);
		testDoesNotHaveIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_08()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) A";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_09()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_10()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_11()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_12()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, ".length();
		testHasIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_13()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, I".length();
		testHasOnlyIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_14()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN".length();
		testDoesNotHaveIdentifiers(query, position, Expression.IN);
	}

	@Test
	public void test_CollectionMemberDeclaration_15()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(".length();

		List<String> identifiers = new ArrayList<String>();
		identifiers.add("e");
		addAll(identifiers, QueryBNFAccessor.collectionMemberDeclarationParameters());

		testHasOnlyIdentifiers(query, position, identifiers);
	}

	@Test
	public void test_CollectionMemberDeclaration_16()
	{
		String query = "SELECT e FROM Employee e, IN(";
		int position = query.length();

		List<String> identifiers = new ArrayList<String>();
		identifiers.add("e");
		addAll(identifiers, QueryBNFAccessor.collectionMemberDeclarationParameters());

		testHasOnlyIdentifiers(query, position, identifiers);
	}

	@Test
	public void test_CollectionMemberDeclaration_17()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(".length();

		List<String> identifiers = new ArrayList<String>();
		identifiers.add("e");
		addAll(identifiers, QueryBNFAccessor.collectionMemberDeclarationParameters());

		testHasOnlyIdentifiers(query, position, identifiers);
	}

	@Test
	public void test_CollectionMemberDeclaration_18()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) ".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_19()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) A".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_20()
	{
		String query = "SELECT e FROM Employee e, IN(e.name) AS n";
		int position = "SELECT e FROM Employee e, IN(e.name) AS ".length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_CollectionMemberDeclaration_23()
	{
		String query = "SELECT e FROM Employee e, IN(K";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.KEY);
	}

	@Test
	public void test_CollectionMemberDeclaration_24()
	{
		String query = "SELECT e FROM Employee e, IN(KEY(a)) AS a";
		int position = query.length() - "EY(a)) AS a".length();
		testHasOnlyIdentifiers(query, position, Expression.KEY);
	}

	@Test
	public void test_CollectionMemberExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.MEMBER,
			Expression.NOT_MEMBER
		);
	}

	@Test
	public void test_CollectionMemberExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e N";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e NO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT M";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT ME";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT MEM";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE e NOT MEMB";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE e NOT MEMBE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT MEMBER";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.NOT_MEMBER_OF);
	}

	@Test
	public void test_CollectionMemberExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT MEMBER ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.OF, "e");
	}

	@Test
	public void test_CollectionMemberExpression_14()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT MEMBER O";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.OF);
	}

	@Test
	public void test_CollectionMemberExpression_15()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT MEMBER OF";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.MEMBER,
			Expression.MEMBER_OF,
			Expression.NOT_MEMBER_OF
		);
	}

	@Test
	public void test_CollectionMemberExpression_16()
	{
		String query = "SELECT e FROM Employee e WHERE e.name M";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_17()
	{
		String query = "SELECT e FROM Employee e WHERE e.name ME";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_18()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEM";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_19()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMB";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_20()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_21()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBER";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_22()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBER ";
		int position = query.length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			"e",
			Expression.OF
		);
	}

	@Test
	public void test_CollectionMemberExpression_23()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBER O";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.OF);
	}

	@Test
	public void test_CollectionMemberExpression_24()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBER OF";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_25()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBER OF ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_CollectionMemberExpression_26()
	{
		String query = "SELECT e FROM Employee e JOIN e.employees emp WHERE e MEMBER OF ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_CollectionMemberExpression_27()
	{
		String query = "SELECT e FROM Employee e JOIN e.employees emp WHERE e MEMBER ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, "e", Expression.OF);
	}

	@Test
	public void test_CollectionMemberExpression_28()
	{
		String query = "SELECT e FROM Employee e WHERE e.name MEMBER OF e.employees";
		int position = query.length() - "EMBER OF e.employees".length();
		testHasOnlyIdentifiers(query, position, Expression.MEMBER);
	}

	@Test
	public void test_CollectionMemberExpression_29()
	{
		String query = "SELECT e FROM Employee e WHERE e.name ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.MEMBER,
			Expression.NOT_MEMBER
		);
	}

	@Test
	public void test_Comparison_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.age ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.age <";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.LOWER_THAN,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL
		);

		testHasIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.age >";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);

		testHasIdentifiers
		(
			query,
			position,
			Expression.GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.age =";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.age <=";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.age >=";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Comparison_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.age <>";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.age ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.age <";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.age >";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.GREATER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.age =";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.age <=";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.age >=";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_ComparisonExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.age <>";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DIFFERENT,
			Expression.GREATER_THAN,
			Expression.GREATER_THAN_OR_EQUAL,
			Expression.LOWER_THAN,
			Expression.LOWER_THAN_OR_EQUAL
		);
	}

	@Test
	public void test_Concat_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_02()
	{
		String query = "SELECT e FROM Employee e WHERE C";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_03()
	{
		String query = "SELECT e FROM Employee e WHERE C";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_04()
	{
		String query = "SELECT e FROM Employee e WHERE CO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_05()
	{
		String query = "SELECT e FROM Employee e WHERE CON";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_06()
	{
		String query = "SELECT e FROM Employee e WHERE CONC";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_07()
	{
		String query = "SELECT e FROM Employee e WHERE CONCA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_08()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT";
		testDoesNotHaveIdentifiers(query, query.length(), Expression.CONCAT);
	}

	@Test
	public void test_Concat_09()
	{
		String query = "SELECT e FROM Employee e WHERE (";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_10()
	{
		String query = "SELECT e FROM Employee e WHERE (C";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_11()
	{
		String query = "SELECT e FROM Employee e WHERE (CO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_12()
	{
		String query = "SELECT e FROM Employee e WHERE (CON";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_13()
	{
		String query = "SELECT e FROM Employee e WHERE (CONC";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_14()
	{
		String query = "SELECT e FROM Employee e WHERE (CONCA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_15()
	{
		String query = "SELECT e FROM Employee e WHERE (CONCAT";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_16()
	{
		String query = "SELECT e FROM Employee e WHERE ()";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_17()
	{
		String query = "SELECT e FROM Employee e WHERE (C)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_18()
	{
		String query = "SELECT e FROM Employee e WHERE (CO)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_19()
	{
		String query = "SELECT e FROM Employee e WHERE (CON)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_20()
	{
		String query = "SELECT e FROM Employee e WHERE (CONC)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_21()
	{
		String query = "SELECT e FROM Employee e WHERE (CONCA)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_22()
	{
		String query = "SELECT e FROM Employee e WHERE (CONCAT)";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Concat_23()
	{
		String query = "SELECT e FROM Employee e WHERE e.name ";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.CONCAT);
	}

	@Test
	public void test_Constructor_01()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_02()
	{
		String query = "SELECT N";
		int position = "SELECT ".length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_03()
	{
		String query = "SELECT N";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_04() throws Exception
	{
		String query = "SELECT e, NEW (";
		int position = "SELECT e, ".length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_05() throws Exception
	{
		String query = "SELECT e, NEW (";
		int position = "SELECT e, N".length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_06() throws Exception
	{
		String query = "SELECT NEW String() From Employee e";
		int position = "SELECT NEW String(".length();

		List<String> identifiers = new ArrayList<String>();
		identifiers.add("e");
		addAll(identifiers, QueryBNFAccessor.constructorItemFunctions());

		testHasOnlyIdentifiers(query, position, identifiers);
	}

	@Test
	public void test_Constructor_07() throws Exception
	{
		String query = "SELECT NEW String(e) From Employee e";
		int position = "SELECT NEW String(e".length();
		testHasOnlyIdentifiers(query, position, Expression.ENTRY);
	}

	@Test
	public void test_Constructor_08()
	{
		String query = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, N".length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_09()
	{
		String query = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, NE".length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_10()
	{
		String query = "SELECT e, NEW(java.lang.String)";
		int position = "SELECT e, NEW".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Constructor_11() throws Exception
	{
		// "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c"
		IQuery query = namedQuery("Customer", "customer.new");
		int position = "SELECT new com.titan.domain.Name(c.".length();
		testHasIdentifiers(query, position, "firstName", "hasGoodCredit", "id", "lastName", "address", "home");
	}

	@Test
	public void test_Constructor_12()
	{
		String query = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.firstName, ".length();
		testHasIdentifiers(query, position, "c");
	}

	@Test
	public void test_Constructor_13()
	{
		String query = "SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c";
		int position = "SELECT new com.titan.domain.Name(c.firstName, c".length();
		testHasIdentifiers(query, position, filter(QueryBNFAccessor.constructorItemIdentifiers(), "c"));
	}

	@Test
	public void test_Constructor_15() throws Exception
	{
		String query = "SELECT NE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_16() throws Exception
	{
		String query = "SELECT e, NE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_17() throws Exception
	{
		String query = "SELECT e, NEW";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_18()
	{
		String query = "SELECT NE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_Constructor_19()
	{
		String query = "SELECT NEW";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Constructor_20()
	{
		String query = "SELECT e, NEW";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Constructor_21()
	{
		String query = "SELECT e, NEW java.lang.String(e.name)";
		int position = "SELECT e, ".length();
		testHasIdentifiers(query, position, Expression.NEW);
	}

	@Test
	public void test_CountFunction_01()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.COUNT);
	}

	@Test
	public void test_CountFunction_02()
	{
		String query = "SELECT C";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.COUNT);
	}

	@Test
	public void test_CountFunction_03()
	{
		String query = "SELECT CO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.COUNT);
	}

	@Test
	public void test_CountFunction_031()
	{
		String query = "SELECT COU";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.COUNT);
	}

	@Test
	public void test_CountFunction_032()
	{
		String query = "SELECT COUN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.COUNT);
	}

	@Test
	public void test_CountFunction_04()
	{
		String query = "SELECT COUNT";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.COUNT);
	}

	@Test
	public void test_CountFunction_05()
	{
		String query = "SELECT COUNT(";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_06()
	{
		String query = "SELECT COUNT() From Employee e";
		int position = "SELECT COUNT(".length();
		testHasOnlyIdentifiers(query, position, "e", Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_07()
	{
		String query = "SELECT COUNT(DISTINCT ) From Employee e";
		int position = "SELECT COUNT(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_CountFunction_08()
	{
		String query = "SELECT COUNT(D ) From Employee e";
		int position = "SELECT COUNT(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_09()
	{
		String query = "SELECT COUNT(DI ) From Employee e";
		int position = "SELECT COUNT(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_10()
	{
		String query = "SELECT COUNT(DIS ) From Employee e";
		int position = "SELECT COUNT(DIS".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_11()
	{
		String query = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_12()
	{
		String query = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_13()
	{
		String query = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_14()
	{
		String query = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_CountFunction_15()
	{
		String query = "SELECT COUNT(DISTINCT e) From Employee e";
		int position = "SELECT COUNT(DISTINCT e".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_CountFunction_16()
	{
		String query = "SELECT COUNT(DISTINCT e) From Employee emp";
		int position = "SELECT COUNT(DISTINCT e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_CountFunction_17()
	{
		String query = "SELECT COUNT() From Employee emp";
		int position = "SELECT COUNT(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_18()
	{
		String query = "SELECT COUNT(e) From Employee emp";
		int position = "SELECT COUNT(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_CountFunction_19()
	{
		String query = "SELECT COUNT(em) From Employee emp";
		int position = "SELECT COUNT(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_CountFunction_20()
	{
		String query = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(emp".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_CountFunction_21()
	{
		String query = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_CountFunction_22()
	{
		String query = "SELECT COUNT(emp) From Employee emp";
		int position = "SELECT COUNT(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_CountFunction_23()
	{
		String query = "SELECT COUNT( From Employee emp";
		int position = "SELECT COUNT(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_CountFunction_24()
	{
		String query = "SELECT COUNT(e From Employee emp";
		int position = "SELECT COUNT(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_DateTime_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.hiredTime < ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_03()
	{
		String query = "SELECT e FROM Employee e WHERE C";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_04()
	{
		String query = "SELECT e FROM Employee e WHERE CU";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_05()
	{
		String query = "SELECT e FROM Employee e WHERE CUR";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_06()
	{
		String query = "SELECT e FROM Employee e WHERE CURR";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_07()
	{
		String query = "SELECT e FROM Employee e WHERE CURRE";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_08()
	{
		String query = "SELECT e FROM Employee e WHERE CURREN";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_09()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_10()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_11()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_D";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_12()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_DA";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_13()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_DAT";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_14()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_DATE";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_DateTime_15()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_T";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE
		);
	}

	@Test
	public void test_DateTime_16()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TI";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE
		);
	}

	@Test
	public void test_DateTime_17()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIM";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE
		);
	}

	@Test
	public void test_DateTime_18()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIME";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME
		);
	}

	@Test
	public void test_DateTime_19()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIMES";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME
		);
	}

	@Test
	public void test_DateTime_20()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIMEST";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME
		);
	}

	@Test
	public void test_DateTime_21()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIMESTA";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME
		);
	}

	@Test
	public void test_DateTime_22()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIMESTAM";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.CURRENT_TIMESTAMP
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME
		);
	}

	@Test
	public void test_DateTime_23()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIMESTAMP";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.CURRENT_DATE,
			Expression.CURRENT_TIME,
			Expression.CURRENT_TIMESTAMP
		);
	}

	@Test
	public void test_Delete_01()
	{
		String query = "D";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_02()
	{
		String query = "DE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_03()
	{
		String query = "DEL";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_04()
	{
		String query = "DELE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_05()
	{
		String query = "DELET";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_06()
	{
		String query = "DELETE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_07()
	{
		String query = "DELETE ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_08()
	{
		String query = "DELETE F";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_09()
	{
		String query = "DELETE FR";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_10()
	{
		String query = "DELETE FRO";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_11()
	{
		String query = "DELETE FROM";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DELETE,
			Expression.DELETE_FROM,
			Expression.FROM
		);
	}

	@Test
	public void test_Delete_12()
	{
		String query = "DELETE FROM Employee";
		int position = "D".length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE);
	}

	@Test
	public void test_Delete_13()
	{
		String query = "DELETE FROM Employee";
		int position = "DE".length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE);
	}

	@Test
	public void test_Delete_14()
	{
		String query = "DELETE FROM Employee";
		int position = "DEL".length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE);
	}

	@Test
	public void test_Delete_15()
	{
		String query = "DELETE FROM Employee";
		int position = "DELE".length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE);
	}

	@Test
	public void test_Delete_16()
	{
		String query = "DELETE FROM Employee";
		int position = "DELET".length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE);
	}

	@Test
	public void test_Delete_17()
	{
		String query = "DELETE FROM Employee";
		int position = "DELETE".length();
		testHasOnlyIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_18()
	{
		String query = "DELETE FROM Employee";
		int position = "DELETE ".length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_19()
	{
		String query = "DELETE FROM Employee";
		int position = "DELETE F".length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_20()
	{
		String query = "DELETE FROM Employee";
		int position = "DELETE FR".length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_21()
	{
		String query = "DELETE FROM Employee";
		int position = "DELETE FRO".length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Delete_22()
	{
		String query = "DELETE FROM Employee";
		int position = "DELETE FROM".length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.DELETE,
			Expression.DELETE_FROM,
			Expression.FROM
		);
	}

	@Test
	public void test_Delete_23()
	{
		String query = "DELETE FROM Employee e WHERE";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_24()
	{
		String query = "DELETE FROM WHERE";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.DELETE_FROM);
	}

	@Test
	public void test_Delete_25()
	{
		String query = "DELETE FROM ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, abstractSchemaNames());
	}

	@Test
	public void test_Delete_26()
	{
		String query = "DELETE FROM P";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, filteredAbstractSchemaNames("P"));
	}

	@Test
	public void test_Delete_27()
	{
		String query = "DELETE FROM Employee WHERE n";
		int position = query.length() - "WHERE n".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
		testDoesNotHaveIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Delete_28()
	{
		String query = "DELETE FROM Employee A WHERE";
		int position = query.length() - " WHERE".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_Delete_29()
	{
		String query = "DELETE FROM Employee A ";
		int position = query.length() - 1;
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_Delete_30()
	{
		String query = "DELETE FROM Employee AS e ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Delete_31()
	{
		String query = "DELETE FROM Employee AS e W";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Delete_32()
	{
		String query = "DELETE FROM Employee AS e WH";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Delete_33()
	{
		String query = "DELETE FROM Employee AS e WHE";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Delete_34()
	{
		String query = "DELETE FROM Employee AS e WHER";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Delete_35()
	{
		String query = "DELETE FROM Employee AS e WHERE";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.name ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.name I";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS N";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NO";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.NOT.toString(),
			Expression.IS_EMPTY.toString()
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT ";
		int position = query.length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY,
			Expression.IS_NOT_NULL
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.EMPTY,
			Expression.IS_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT E";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT EM";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT EMP";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT EMPT";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY,
			Expression.EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_14()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT EMPTY";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.EMPTY,
			Expression.IS_EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_15()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS E";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_16()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS EM";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.IS_NOT_EMPTY,
			Expression.EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_17()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS EMP";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_18()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS EMPT";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.IS_EMPTY
		);

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_19()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS EMPTY";
		int position = query.length();

		testDoesNotHaveIdentifiers
		(
			query,
			position,
			Expression.EMPTY,
			Expression.IS_EMPTY,
			Expression.IS_NOT_EMPTY
		);
	}

	@Test
	public void test_Exists_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_02()
	{
		String query = "SELECT e FROM Employee e WHERE E";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_03()
	{
		String query = "SELECT e FROM Employee e WHERE EX";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_04()
	{
		String query = "SELECT e FROM Employee e WHERE EXI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_05()
	{
		String query = "SELECT e FROM Employee e WHERE EXIS";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_06()
	{
		String query = "SELECT e FROM Employee e WHERE EXIST";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_07()
	{
		String query = "SELECT e FROM Employee e WHERE EXISTS";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_08()
	{
		String query = "SELECT e FROM Employee e WHERE (";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_09()
	{
		String query = "SELECT e FROM Employee e WHERE (E";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_10()
	{
		String query = "SELECT e FROM Employee e WHERE (EX";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_11()
	{
		String query = "SELECT e FROM Employee e WHERE (EXI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_12()
	{
		String query = "SELECT e FROM Employee e WHERE (EXIS";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_13()
	{
		String query = "SELECT e FROM Employee e WHERE (EXIST";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_14()
	{
		String query = "SELECT e FROM Employee e WHERE (EXISTS";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Exists_15()
	{
		String query = "SELECT e FROM Employee e WHERE (E)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_16()
	{
		String query = "SELECT e FROM Employee e WHERE (EX)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_17()
	{
		String query = "SELECT e FROM Employee e WHERE (EXI)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_18()
	{
		String query = "SELECT e FROM Employee e WHERE (EXIS)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_19()
	{
		String query = "SELECT e FROM Employee e WHERE (EXIST)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.EXISTS);
	}

	@Test
	public void test_Exists_20()
	{
		String query = "SELECT e FROM Employee e WHERE (EXISTS)";
		int position = query.length() - 1;
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_From_01() throws Exception
	{
		String query = "SELECT AVG(e.age) F";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_FromAs_01()
	{
		String query = "SELECT e FROM Employee ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_FromAs_02()
	{
		String query = "SELECT e FROM Employee A";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_FromAs_03()
	{
		String query = "SELECT e FROM Employee AS";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_FromAs_04()
	{
		String query = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee ".length();
		testHasIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_FromAs_05()
	{
		String query = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee A".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_FromAs_06()
	{
		String query = "SELECT e FROM Employee AS e";
		int position = "SELECT e FROM Employee AS".length();
		testDoesNotHaveIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_GroupBy_01()
	{
		String query = "SELECT e FROM Employee e ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_02()
	{
		String query = "SELECT e FROM Employee e GROUP BY e.name";
		int position = "SELECT e FROM Employee e ".length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_03()
	{
		String query = "SELECT e FROM Employee e G";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_04()
	{
		String query = "SELECT e FROM Employee e GR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_05()
	{
		String query = "SELECT e FROM Employee e GRO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_06()
	{
		String query = "SELECT e FROM Employee e GROU";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_07()
	{
		String query = "SELECT e FROM Employee e GROUP";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_08()
	{
		String query = "SELECT e FROM Employee e GROUP ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_09()
	{
		String query = "SELECT e FROM Employee e GROUP B";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_10()
	{
		String query = "SELECT e FROM Employee e GROUP BY";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_11()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_GroupBy_12()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') G";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.GROUP_BY);
	}

	@Test
	public void test_Having_01()
	{
		String query = "SELECT e FROM Employee e ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_02()
	{
		String query = "SELECT e FROM Employee e HAVING COUNT(e) >= 5";
		int position = "SELECT e FROM Employee e ".length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_03()
	{
		String query = "SELECT e FROM Employee e H";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_04()
	{
		String query = "SELECT e FROM Employee e HA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_05()
	{
		String query = "SELECT e FROM Employee e HAV";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_06()
	{
		String query = "SELECT e FROM Employee e HAVI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_07()
	{
		String query = "SELECT e FROM Employee e HAVIN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_08()
	{
		String query = "SELECT e FROM Employee e HAVING";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_09()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_10()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') H";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_11()
	{
		String query = "SELECT e FROM Employee e GROUP BY e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_12()
	{
		String query = "SELECT e FROM Employee e GROUP BY e.name H";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_13()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Having_14()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name H";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.HAVING);
	}

	@Test
	public void test_Join_01()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.INNER_JOIN,
			Expression.INNER_JOIN_FETCH,
			Expression.JOIN,
			Expression.JOIN_FETCH,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_02()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub L".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_03()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LE".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_04()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEF".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_05()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_06()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_07()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT J".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_JOIN);
	}

	@Test
	public void test_Join_08()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT JO".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_JOIN);
	}

	@Test
	public void test_Join_09()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT JOI".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_JOIN);
	}

	@Test
	public void test_Join_10()
	{
		String query = "SELECT pub FROM Publisher pub LEFT JOIN";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Join_11()
	{
		String query = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.INNER_JOIN,
			Expression.INNER_JOIN_FETCH,
			Expression.JOIN,
			Expression.JOIN_FETCH,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_12()
	{
		String query = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub J".length();
		testHasOnlyIdentifiers(query, position, Expression.JOIN);
	}

	@Test
	public void test_Join_13()
	{
		String query = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub JO".length();
		testHasOnlyIdentifiers(query, position, Expression.JOIN);
	}

	@Test
	public void test_Join_14()
	{
		String query = "SELECT pub FROM Publisher pub JOIN";
		int position = "SELECT pub FROM Publisher pub JOI".length();
		testHasOnlyIdentifiers(query, position, Expression.JOIN);
	}

	@Test
	public void test_Join_15()
	{
		String query = "SELECT pub FROM Publisher pub JOIN";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Join_16()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.INNER_JOIN,
			Expression.INNER_JOIN_FETCH,
			Expression.JOIN,
			Expression.JOIN_FETCH,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_17()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub L".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_18()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LE".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_19()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEF".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_20()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_21()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_OUTER_JOIN
		);
	}

	@Test
	public void test_Join_22()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT O".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_23()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OU".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_24()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUT".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_25()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTE".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_26()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_27()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER ".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_28()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER J".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_29()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JO".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_30()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JOI".length();
		testHasOnlyIdentifiers(query, position, Expression.LEFT_OUTER_JOIN);
	}

	@Test
	public void test_Join_31()
	{
		String query = "SELECT pub FROM Publisher pub LEFT OUTER JOIN";
		int position = "SELECT pub FROM Publisher pub LEFT OUTER JOIN".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Join_32()
	{
		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines ".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_Join_33()
	{
		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines A".length();
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_Join_34()
	{
		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag";
		int position = "SELECT pub FROM Publisher pub JOIN pub.magazines AS".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Join_35()
	{
		String query = "SELECT e FROM Employee e INNER JOIN e.magazines mags";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.INNER_JOIN,
			Expression.INNER_JOIN_FETCH,
			Expression.JOIN,
			Expression.JOIN_FETCH,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_36()
	{
		String query = "SELECT e FROM Employee e INNER JOIN e.magazines mags ";
		int position = "SELECT e FROM Employee e INNER JOIN e.magazines mags ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.INNER_JOIN,
			Expression.INNER_JOIN_FETCH,
			Expression.JOIN,
			Expression.JOIN_FETCH,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH,
			Expression.WHERE,
			Expression.HAVING,
			Expression.ORDER_BY,
			Expression.GROUP_BY
		);
	}

	@Test
	public void test_Join_37()
	{
		String query = "SELECT e FROM Employee e INNER JOIN e.mags mags";
		int position = "SELECT e FROM Employee e INNER".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Join_38()
	{
		String query = "SELECT o from Countries o JOIN o.locationsList e LEFT ";
		int position = query.length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH
		);
	}

	@Test
	public void test_Join_39()
	{
		String query = "SELECT o from Countries o JOIN o.locationsList e LEFT OUTER JOIN FETCH  ";
		int position = "SELECT o from Countries o JOIN o.locationsList e LEFT OUTER JOIN FETCH  ".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Keyword_01()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_02()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = T".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_03()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = TR".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_04()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = "UPDATE Employee e SET e.isEnrolled = TRU".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_05()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = TRUE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Keyword_06()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_07()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = F".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_08()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FA".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_09()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FAL".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_10()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = "UPDATE Employee e SET e.isEnrolled = FALS".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_11()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = FALSE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Keyword_12()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = ".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_13()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = N".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_14()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = NU".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_15()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = "UPDATE Employee e SET e.isEnrolled = NUL".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_16()
	{
		String query = "UPDATE Employee e SET e.isEnrolled = NULL";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_17()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_18()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = T".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_19()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = TR".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_20()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = "SELECT e FROM Employee e WHERE e.hired = TRU".length();
		testHasIdentifiers(query, position, Expression.TRUE);
	}

	@Test
	public void test_Keyword_21()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = TRUE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Keyword_22()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_23()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = F".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_24()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FA".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_25()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FAL".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_26()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = "SELECT e FROM Employee e WHERE e.hired = FALS".length();
		testHasIdentifiers(query, position, Expression.FALSE);
	}

	@Test
	public void test_Keyword_27()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = FALSE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Keyword_28()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = ".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_29()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = N".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_30()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = NU".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_31()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = "SELECT e FROM Employee e WHERE e.hired = NUL".length();
		testHasIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_32()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = NULL";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.NULL);
	}

	@Test
	public void test_Keyword_33()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.NULL,
			Expression.TRUE,
			Expression.FALSE
		);
	}

	@Test
	public void test_Keyword_34()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired =";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.NULL,
			Expression.TRUE,
			Expression.FALSE
		);
	}

	@Test
	public void test_Keyword_35()
	{
		String query = "SELECT e FROM Employee e WHERE e.hired = ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.NULL,
			Expression.TRUE,
			Expression.FALSE
		);
	}

	@Test
	public void test_Length_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_02()
	{
		String query = "SELECT e FROM Employee e WHERE L";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_03()
	{
		String query = "SELECT e FROM Employee e WHERE LE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_04()
	{
		String query = "SELECT e FROM Employee e WHERE LEN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_05()
	{
		String query = "SELECT e FROM Employee e WHERE LENG";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_06()
	{
		String query = "SELECT e FROM Employee e WHERE LENGT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_07()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Length_08()
	{
		String query = "SELECT e FROM Employee e WHERE (L";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_09()
	{
		String query = "SELECT e FROM Employee e WHERE (LE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_10()
	{
		String query = "SELECT e FROM Employee e WHERE (LEN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_11()
	{
		String query = "SELECT e FROM Employee e WHERE (LENG";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_12()
	{
		String query = "SELECT e FROM Employee e WHERE (LENGT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_13()
	{
		String query = "SELECT e FROM Employee e WHERE (LENGTH";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Length_14()
	{
		String query = "SELECT e FROM Employee e WHERE ()";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_15()
	{
		String query = "SELECT e FROM Employee e WHERE (L)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_16()
	{
		String query = "SELECT e FROM Employee e WHERE (LE)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_17()
	{
		String query = "SELECT e FROM Employee e WHERE (LEN)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_18()
	{
		String query = "SELECT e FROM Employee e WHERE (LENG)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_19()
	{
		String query = "SELECT e FROM Employee e WHERE (LENGT)";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LENGTH);
	}

	@Test
	public void test_Length_20()
	{
		String query = "SELECT e FROM Employee e WHERE (LENGTH)";
		int position = query.length() - 1;
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Like_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.LIKE);
	}

	@Test
	public void test_Like_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LIKE);
	}

	@Test
	public void test_Like_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.name L";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LIKE);
	}

	@Test
	public void test_Like_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.name LI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LIKE);
	}

	@Test
	public void test_Like_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.name LIKE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Like_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.name N";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name ".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_10()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name N".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_11()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NO".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_12()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT ".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_13()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT L".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_14()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT LI".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_15()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = "SELECT e FROM Employee e WHERE e.name NOT LIK".length();
		testHasIdentifiers(query, position, Expression.NOT_LIKE);
	}

	@Test
	public void test_Like_16()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Locate_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_02()
	{
		String query = "SELECT e FROM Employee e WHERE L";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_03()
	{
		String query = "SELECT e FROM Employee e WHERE LO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_04()
	{
		String query = "SELECT e FROM Employee e WHERE LOC";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_05()
	{
		String query = "SELECT e FROM Employee e WHERE LOCA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_06()
	{
		String query = "SELECT e FROM Employee e WHERE LOCAT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_07()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_08()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_09()
	{
		String query = "SELECT e FROM Employee e WHERE (";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_10()
	{
		String query = "SELECT e FROM Employee e WHERE (L";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_11()
	{
		String query = "SELECT e FROM Employee e WHERE (LO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_12()
	{
		String query = "SELECT e FROM Employee e WHERE (LOC";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_13()
	{
		String query = "SELECT e FROM Employee e WHERE (LOCA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_14()
	{
		String query = "SELECT e FROM Employee e WHERE (LOCAT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_15()
	{
		String query = "SELECT e FROM Employee e WHERE (LOCATE";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_16()
	{
		String query = "SELECT e FROM Employee e WHERE (LOCATE)";
		int position = query.length() - 1;
		testDoesNotHaveIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_Locate_17()
	{
		String query = "SELECT e FROM Employee e WHERE ()";
		int position = query.length() - 1;
		testHasIdentifiers(query, position, Expression.LOCATE);
	}

	@Test
	public void test_LogicalExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 1 AND 3 ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.AND, Expression.OR);
	}

	@Test
	public void test_Lower_12()
	{
		String query = "SELECT e FROM Employee e WHERE LOWER";
		testHasNoIdentifiers(query, query.length());
	}

	@Test
	public void test_MaxFunction_01()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MAX);
	}

	@Test
	public void test_MaxFunction_02()
	{
		String query = "SELECT M";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MAX);
	}

	@Test
	public void test_MaxFunction_03()
	{
		String query = "SELECT MA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MAX);
	}

	@Test
	public void test_MaxFunction_04()
	{
		String query = "SELECT MAX";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.MAX);
	}

	@Test
	public void test_MaxFunction_05()
	{
		String query = "SELECT MAX(";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_06()
	{
		String query = "SELECT MAX() From Employee e";
		int position = "SELECT MAX(".length();
		testHasOnlyIdentifiers(query, position, "e", Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_07()
	{
		String query = "SELECT MAX(DISTINCT ) From Employee e";
		int position = "SELECT MAX(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_MaxFunction_08()
	{
		String query = "SELECT MAX(D ) From Employee e";
		int position = "SELECT MAX(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_09()
	{
		String query = "SELECT MAX(DI ) From Employee e";
		int position = "SELECT MAX(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_10()
	{
		String query = "SELECT MAX(DIS ) From Employee e";
		int position = "SELECT MAX(DIS".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_11()
	{
		String query = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_12()
	{
		String query = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_13()
	{
		String query = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_14()
	{
		String query = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_MaxFunction_15()
	{
		String query = "SELECT MAX(DISTINCT e) From Employee e";
		int position = "SELECT MAX(DISTINCT e".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_MaxFunction_16()
	{
		String query = "SELECT MAX(DISTINCT e) From Employee emp";
		int position = "SELECT MAX(DISTINCT e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MaxFunction_17()
	{
		String query = "SELECT MAX() From Employee emp";
		int position = "SELECT MAX(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_18()
	{
		String query = "SELECT MAX(e) From Employee emp";
		int position = "SELECT MAX(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MaxFunction_19()
	{
		String query = "SELECT MAX(em) From Employee emp";
		int position = "SELECT MAX(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MaxFunction_20()
	{
		String query = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(emp".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_MaxFunction_21()
	{
		String query = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MaxFunction_22()
	{
		String query = "SELECT MAX(emp) From Employee emp";
		int position = "SELECT MAX(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MaxFunction_23()
	{
		String query = "SELECT MAX( From Employee emp";
		int position = "SELECT MAX(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_MaxFunction_24()
	{
		String query = "SELECT MAX(e From Employee emp";
		int position = "SELECT MAX(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MinFunction_01()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MIN);
	}

	@Test
	public void test_MinFunction_02()
	{
		String query = "SELECT M";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MIN);
	}

	@Test
	public void test_MinFunction_03()
	{
		String query = "SELECT MI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.MIN);
	}

	@Test
	public void test_MinFunction_04()
	{
		String query = "SELECT MIN";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.MIN);
	}

	@Test
	public void test_MinFunction_05()
	{
		String query = "SELECT MIN(";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_06()
	{
		String query = "SELECT MIN() From Employee e";
		int position = "SELECT MIN(".length();
		testHasOnlyIdentifiers(query, position, "e", Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_07()
	{
		String query = "SELECT MIN(DISTINCT ) From Employee e";
		int position = "SELECT MIN(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_MinFunction_08()
	{
		String query = "SELECT MIN(D ) From Employee e";
		int position = "SELECT MIN(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_09()
	{
		String query = "SELECT MIN(DI ) From Employee e";
		int position = "SELECT MIN(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_10()
	{
		String query = "SELECT MIN(DIS ) From Employee e";
		int position = "SELECT MIN(DIS".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_11()
	{
		String query = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_12()
	{
		String query = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_13()
	{
		String query = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_14()
	{
		String query = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_MinFunction_15()
	{
		String query = "SELECT MIN(DISTINCT e) From Employee e";
		int position = "SELECT MIN(DISTINCT e".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_MinFunction_16()
	{
		String query = "SELECT MIN(DISTINCT e) From Employee emp";
		int position = "SELECT MIN(DISTINCT e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MinFunction_17()
	{
		String query = "SELECT MIN() From Employee emp";
		int position = "SELECT MIN(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_18()
	{
		String query = "SELECT MIN(e) From Employee emp";
		int position = "SELECT MIN(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MinFunction_19()
	{
		String query = "SELECT MIN(em) From Employee emp";
		int position = "SELECT MIN(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MinFunction_20()
	{
		String query = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(emp".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_MinFunction_21()
	{
		String query = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MinFunction_22()
	{
		String query = "SELECT MIN(emp) From Employee emp";
		int position = "SELECT MIN(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_MinFunction_23()
	{
		String query = "SELECT MIN( From Employee emp";
		int position = "SELECT MIN(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_MinFunction_24()
	{
		String query = "SELECT MIN(e From Employee emp";
		int position = "SELECT MIN(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_NullComparison_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.IS_NULL);
	}

	@Test
	public void test_NullComparison_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NULL);
	}

	@Test
	public void test_NullComparison_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.name I";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NULL);
	}

	@Test
	public void test_NullComparison_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NULL);
	}

	@Test
	public void test_NullComparison_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NULL);
	}

	@Test
	public void test_NullComparison_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS N";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NULL);
	}

	@Test
	public void test_NullComparison_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT N";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_10()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT NU";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_11()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT NUL";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.IS_NOT_NULL);
	}

	@Test
	public void test_NullComparison_12()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.IS_NOT_NULL);
	}

	@Test
	public void test_Object_01()
	{
		String query = "SELECT O FROM Employee e";
		int position = "SELECT O".length() - 1;
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_02()
	{
		String query = "SELECT OB FROM Employee e";
		int position = "SELECT O".length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_03()
	{
		String query = "SELECT OBJ FROM Employee e";
		int position = "SELECT O".length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_04()
	{
		String query = "SELECT OBJE FROM Employee e";
		int position = "SELECT O".length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_05()
	{
		String query = "SELECT OBJEC FROM Employee e";
		int position = "SELECT O".length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_12()
	{
		String query = "SELECT OBJECT";
		testHasNoIdentifiers(query, query.length());
	}

	@Test
	public void test_Object_13()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_14()
	{
		String query = "SELECT O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_15()
	{
		String query = "SELECT OB";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_16()
	{
		String query = "SELECT OBJ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_17()
	{
		String query = "SELECT OBJE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_18()
	{
		String query = "SELECT OBJEC";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_19()
	{
		String query = "SELECT OBJECT";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.OBJECT);
	}

	@Test
	public void test_Object_20()
	{
		String query = "SELECT DISTINCT OBJECT(a) FROM Address a";
		int position = "SELECT DISTINCT OBJECT(a".length();
		testDoesNotHaveIdentifiers(query, position);
	}

	@Test
	public void test_OptionalClauses_01()
	{
		String query = "SELECT e FROM Employee e ";
		int position = query.length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.INNER_JOIN,
			Expression.INNER_JOIN_FETCH,
			Expression.JOIN,
			Expression.JOIN_FETCH,
			Expression.LEFT_JOIN,
			Expression.LEFT_JOIN_FETCH,
			Expression.LEFT_OUTER_JOIN,
			Expression.LEFT_OUTER_JOIN_FETCH,
			Expression.WHERE,
			Expression.GROUP_BY,
			Expression.HAVING,
			Expression.ORDER_BY
		);
	}

	@Test
	public void test_OptionalClauses_02()
	{
		String query = "SELECT e FROM Employee e HAVING e.name = 'Oracle'";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.WHERE,
			Expression.GROUP_BY,
			Expression.HAVING
		);
	}

	@Test
	public void test_OptionalClauses_03()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.WHERE,
			Expression.GROUP_BY,
			Expression.HAVING,
			Expression.ORDER_BY
		);
	}

	@Test
	public void test_OptionalClauses_04()
	{
		String query = "SELECT e FROM Employee e GROUP BY e.name";
		int position = "SELECT e FROM Employee e ".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.WHERE,
			Expression.GROUP_BY
		);
	}

	@Test
	public void test_OptionalClauses_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Oracle' ";
		int position = query.length();

		testHasIdentifiers
		(
			query,
			position,
			Expression.GROUP_BY,
			Expression.HAVING,
			Expression.ORDER_BY
		);
	}

	@Test
	public void test_OrderBy_01()
	{
		String query = "SELECT e FROM Employee e ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_02()
	{
		String query = "SELECT e FROM Employee e O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_03()
	{
		String query = "SELECT e FROM Employee e OR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_04()
	{
		String query = "SELECT e FROM Employee e ORD";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_05()
	{
		String query = "SELECT e FROM Employee e ORDE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_06()
	{
		String query = "SELECT e FROM Employee e ORDER";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_07()
	{
		String query = "SELECT e FROM Employee e ORDER ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_08()
	{
		String query = "SELECT e FROM Employee e ORDER B";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_09()
	{
		String query = "SELECT e FROM Employee e ORDER BY";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_OrderBy_10()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_11()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_12()
	{
		String query = "SELECT e FROM Employee e GROUP BY e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_13()
	{
		String query = "SELECT e FROM Employee e GROUP BY e.name O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_14()
	{
		String query = "SELECT e FROM Employee e HAVING COUNT(e) >= 5 ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_15()
	{
		String query = "SELECT e FROM Employee e HAVING COUNT(e) >= 5 O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_16()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_17()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_18()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name HAVING COUNT(e) >= 5 ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderBy_19()
	{
		String query = "SELECT e FROM Employee e WHERE (e.name = 'Pascal') GROUP BY e.name HAVING COUNT(e) >= 5 O";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_OrderByItem_01()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.ASC);
	}

	@Test
	public void test_OrderByItem_02()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ASC);
	}

	@Test
	public void test_OrderByItem_03()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name A";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ASC);
	}

	@Test
	public void test_OrderByItem_04()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name AS";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.ASC);
	}

	@Test
	public void test_OrderByItem_05()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name ASC";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_OrderByItem_06()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name D";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.DESC);
	}

	@Test
	public void test_OrderByItem_07()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name DE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.DESC);
	}

	@Test
	public void test_OrderByItem_08()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name DE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.DESC);
	}

	@Test
	public void test_OrderByItem_09()
	{
		String query = "SELECT e FROM Employee e ORDER BY e.name DESC";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Query_01() throws Exception
	{
		String query = AbstractExpression.EMPTY_STRING;
		int position = 0;

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.SELECT,
			Expression.UPDATE,
			Expression.DELETE_FROM
		);
	}

	@Test
	public void test_Restriction_01() throws Exception
	{
		String query = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(".length();
		testDoesNotHaveIdentifiers(query, position, QueryBNFAccessor.selectItemIdentifiers());
	}

	@Test
	public void test_Restriction_02() throws Exception
	{
		String query = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(e".length();
		testDoesNotHaveIdentifiers(query, position, QueryBNFAccessor.selectItemIdentifiers());
	}

	@Test
	public void test_Restriction_03() throws Exception
	{
		String query = "SELECT AVG(e.name) FROM Employee e";
		int position = "SELECT AVG(e.".length();
		testDoesNotHaveIdentifiers(query, position, QueryBNFAccessor.selectItemIdentifiers());
	}

	@Test
	public void test_Restriction_04()
	{
		String query = "SELECT o FROM Countries AS o";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.ORDER_BY);
	}

	@Test
	public void test_SelectClause_01() throws Exception
	{
		String query = "SELECT ";
		int position = query.length();

		List<String> identifiers = new ArrayList<String>();
		identifiers.add(Expression.DISTINCT);
		addAll(identifiers, QueryBNFAccessor.selectItemFunctions());

		testHasOnlyIdentifiers(query, position, identifiers);
	}

	@Test
	public void test_SelectClause_02() throws Exception
	{
		String query = "SELECT e";
		int position = query.length();

		testHasOnlyIdentifiers(query, position, Expression.ENTRY);
	}

	@Test
	public void test_SelectClause_03() throws Exception
	{
		String query = "SELECT  FROM Employee e";
		int position = "SELECT ".length();

		List<String> identifiers = new ArrayList<String>();
		identifiers.add(Expression.DISTINCT);
		identifiers.add("e");
		addAll(identifiers, QueryBNFAccessor.selectItemFunctions());

		testHasOnlyIdentifiers(query, position, identifiers);
	}

	@Test
	public void test_SelectClause_04() throws Exception
	{
		String query = "SELECT AV FROM Employee e";
		int position = "SELECT AV".length();
		testHasOnlyIdentifiers(query, position, Expression.AVG);
	}

	@Test
	public void test_SelectClause_05() throws Exception
	{
		String query = "SELECT e,";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, QueryBNFAccessor.selectItemFunctions());
	}

	@Test
	public void test_SelectClause_06() throws Exception
	{
		String query = "SELECT e, ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, QueryBNFAccessor.selectItemFunctions());
	}

	@Test
	public void test_SelectStatement_01() throws Exception
	{
		String query = "SELECT";

		for (int position = 1, count = query.length() - 1; position < count; position++)
		{
			testHasOnlyIdentifiers(query, position, Expression.SELECT);
		}

		hasNoIdentifiers(query, query.length());
	}

	@Test
	public void test_StateFieldPathExpression_01() throws Exception
	{
		// SELECT c. FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_1");
		int position = "SELECT c.".length();
		testHasOnlyIdentifiers(namedQuery, position, "id", "name", "manager");
	}

	@Test
	public void test_StateFieldPathExpression_02() throws Exception
	{
		// SELECT c.name FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_1");
		int position = "SELECT c.n".length();
		testHasOnlyIdentifiers(namedQuery, position, "name");
	}

	@Test
	public void test_StateFieldPathExpression_03() throws Exception
	{
		// SELECT c.name FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_1");
		int position = "SELECT c.name".length();
		testHasNoIdentifiers(namedQuery, position);
	}

	@Test
	public void test_StateFieldPathExpression_04() throws Exception
	{
		// SELECT c.manager.name FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_2");
		int position = "SELECT c.m".length();
		testHasOnlyIdentifiers(namedQuery, position, "manager");
	}

	@Test
	public void test_StateFieldPathExpression_05() throws Exception
	{
		// SELECT c.manager.name FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_2");
		int position = "SELECT c.manager".length();
		testHasNoIdentifiers(namedQuery, position);
	}

	@Test
	public void test_StateFieldPathExpression_06() throws Exception
	{
		// SELECT c.manager.name FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_2");
		int position = "SELECT c.manager.".length();
		testHasIdentifiers(namedQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_StateFieldPathExpression_07() throws Exception
	{
		// SELECT c.manager.name FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_2");
		int position = "SELECT c.manager.name".length();
		testHasNoIdentifiers(namedQuery, position);
	}

	@Test
	public void test_StateFieldPathExpression_08() throws Exception
	{
		// SELECT c.employees. FROM CodeAssist c
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_3");
		int position = "SELECT c.employees.".length();
		testHasNoIdentifiers(namedQuery, position);
	}

	@Test
	public void test_StateFieldPathExpression_09() throws Exception
	{
		// SELECT e. FROM CodeAssist c JOIN c.employees e
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_4");
		int position = "SELECT e.".length();
		testHasIdentifiers(namedQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_StateFieldPathExpression_10() throws Exception
	{
		// SELECT e. FROM CodeAssist c, IN c.employees e
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_5");
		int position = "SELECT e.".length();
		testHasIdentifiers(namedQuery, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_StateFieldPathExpression_11() throws Exception
	{
		// SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_6");
		int position = "SELECT a.".length();
		testHasOnlyIdentifiers(namedQuery, position, "alias");
	}

	@Test
	public void test_StateFieldPathExpression_12() throws Exception
	{
		// SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_6");
		int position = "SELECT a.alias".length();
		testHasNoIdentifiers(namedQuery, position);
	}

	@Test
	public void test_StateFieldPathExpression_13() throws Exception
	{
		// SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_6");
		int position = "SELECT a.alias FROM CodeAssist c, IN c.".length();
		testHasOnlyIdentifiers(namedQuery, position, "employees", "customerMapAddress", "customerMap");
	}

	@Test
	public void test_StateFieldPathExpression_14() throws Exception
	{
		// SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_6");
		int position = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(".length();
		testHasOnlyIdentifiers(namedQuery, position, "cust");
	}

	@Test
	public void test_StateFieldPathExpression_15() throws Exception
	{
		// SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a
		IQuery namedQuery = namedQuery("CodeAssist", "codeAssist.code_6");
		int position = "SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).".length();
		testHasOnlyIdentifiers(namedQuery, position, "phoneList", "aliases");
	}

	@Test
	public void test_SubQuery_01() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (".length();

		List<String> items = new ArrayList<String>();
		addAll(items, QueryBNFAccessor.comparisonExpressionFunctions());
		addAll(items, QueryBNFAccessor.comparisonExpressionClauses());

		testHasOnlyIdentifiers(query, position, items);
	}

	@Test
	public void test_SubQuery_02() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (S".length();
		testHasIdentifiers(query, position, Expression.SELECT);
	}

	@Test
	public void test_SubQuery_03() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SE".length();
		testHasOnlyIdentifiers(query, position, Expression.SELECT);
	}

	@Test
	public void test_SubQuery_04() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SEL".length();
		testHasOnlyIdentifiers(query, position, Expression.SELECT);
	}

	@Test
	public void test_SubQuery_05() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELE".length();
		testHasOnlyIdentifiers(query, position, Expression.SELECT);
	}

	@Test
	public void test_SubQuery_06() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELEC".length();
		testHasOnlyIdentifiers(query, position, Expression.SELECT);
	}

	@Test
	public void test_SubQuery_07() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT".length();
		testDoesNotHaveIdentifiers(query, position, Expression.SELECT);
	}

	@Test
	public void test_SubQuery_08() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT ";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT".length();
		testDoesNotHaveIdentifiers(query, position);
	}

	@Test
	public void test_SubQuery_09() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT A";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT A".length();

		testHasOnlyIdentifiers
		(
			query,
			position,
			Expression.ABS,
			Expression.AVG
		);
	}

	@Test
	public void test_SubQuery_10() throws Exception
	{
		String query = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)";
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) ".length();
		testHasOnlyIdentifiers(query, position, Expression.AS, Expression.FROM);
	}

	@Test
	public void test_SubQuery_StateFieldPathExpression_01() throws Exception
	{
		// SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)
		IQuery namedQuery = namedQuery("Employee", "employee.subquery.code_1");
		int position = "SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.".length();
		testHasOnlyIdentifiers(namedQuery, position, "empId", "salary");
	}

	@Test
	public void test_Substring_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_02()
	{
		String query = "SELECT e FROM Employee e WHERE S";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_03()
	{
		String query = "SELECT e FROM Employee e WHERE SU";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_04()
	{
		String query = "SELECT e FROM Employee e WHERE SUB";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_05()
	{
		String query = "SELECT e FROM Employee e WHERE SUBS";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_06()
	{
		String query = "SELECT e FROM Employee e WHERE SUBST";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_07()
	{
		String query = "SELECT e FROM Employee e WHERE SUBSTR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_08()
	{
		String query = "SELECT e FROM Employee e WHERE SUBSTR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_09()
	{
		String query = "SELECT e FROM Employee e WHERE SUBSTRI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_10()
	{
		String query = "SELECT e FROM Employee e WHERE SUBSTRIN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_Substring_11()
	{
		String query = "SELECT e FROM Employee e WHERE SUBSTRING";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.SUBSTRING);
	}

	@Test
	public void test_SumFunction_01()
	{
		String query = "SELECT ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUM);
	}

	@Test
	public void test_SumFunction_02()
	{
		String query = "SELECT S";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUM);
	}

	@Test
	public void test_SumFunction_03()
	{
		String query = "SELECT SU";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SUM);
	}

	@Test
	public void test_SumFunction_04()
	{
		String query = "SELECT SUM";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.SUM);
	}

	@Test
	public void test_SumFunction_05()
	{
		String query = "SELECT SUM(";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_06()
	{
		String query = "SELECT SUM() From Employee e";
		int position = "SELECT SUM(".length();
		testHasOnlyIdentifiers(query, position, "e", Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_07()
	{
		String query = "SELECT SUM(DISTINCT ) From Employee e";
		int position = "SELECT SUM(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_SumFunction_08()
	{
		String query = "SELECT SUM(D ) From Employee e";
		int position = "SELECT SUM(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_09()
	{
		String query = "SELECT SUM(DI ) From Employee e";
		int position = "SELECT SUM(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_10()
	{
		String query = "SELECT SUM(DIS ) From Employee e";
		int position = "SELECT SUM(DIS".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_11()
	{
		String query = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_12()
	{
		String query = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(D".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_13()
	{
		String query = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DI".length();
		testHasOnlyIdentifiers(query, position, Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_14()
	{
		String query = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DISTINCT ".length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_SumFunction_15()
	{
		String query = "SELECT SUM(DISTINCT e) From Employee e";
		int position = "SELECT SUM(DISTINCT e".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_SumFunction_16()
	{
		String query = "SELECT SUM(DISTINCT e) From Employee emp";
		int position = "SELECT SUM(DISTINCT e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_SumFunction_17()
	{
		String query = "SELECT SUM() From Employee emp";
		int position = "SELECT SUM(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_18()
	{
		String query = "SELECT SUM(e) From Employee emp";
		int position = "SELECT SUM(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_SumFunction_19()
	{
		String query = "SELECT SUM(em) From Employee emp";
		int position = "SELECT SUM(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_SumFunction_20()
	{
		String query = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(emp".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_SumFunction_21()
	{
		String query = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_SumFunction_22()
	{
		String query = "SELECT SUM(emp) From Employee emp";
		int position = "SELECT SUM(em".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_SumFunction_23()
	{
		String query = "SELECT SUM( From Employee emp";
		int position = "SELECT SUM(".length();
		testHasOnlyIdentifiers(query, position, "emp", Expression.DISTINCT);
	}

	@Test
	public void test_SumFunction_24()
	{
		String query = "SELECT SUM(e From Employee emp";
		int position = "SELECT SUM(e".length();
		testHasOnlyIdentifiers(query, position, "emp");
	}

	@Test
	public void test_Trim_01()
	{
		String query = "SELECT e FROM Employee e WHERE ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRIM);
	}

	@Test
	public void test_Trim_02()
	{
		String query = "SELECT e FROM Employee e WHERE T";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRIM);
	}

	@Test
	public void test_Trim_03()
	{
		String query = "SELECT e FROM Employee e WHERE TR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRIM);
	}

	@Test
	public void test_Trim_04()
	{
		String query = "SELECT e FROM Employee e WHERE TRI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRIM);
	}

	@Test
	public void test_Trim_05()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Trim_06()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_07()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(B";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.BOTH);
	}

	@Test
	public void test_Trim_08()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(BO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.BOTH);
	}

	@Test
	public void test_Trim_09()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(BOT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.BOTH);
	}

	@Test
	public void test_Trim_10()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(BOTH";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Trim_11()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(L";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LEADING);
	}

	@Test
	public void test_Trim_12()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(LE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LEADING);
	}

	@Test
	public void test_Trim_13()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(LEA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LEADING);
	}

	@Test
	public void test_Trim_14()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(LEAD";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LEADING);
	}

	@Test
	public void test_Trim_15()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(LEADI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LEADING);
	}

	@Test
	public void test_Trim_16()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(LEADIN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.LEADING);
	}

	@Test
	public void test_Trim_17()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(LEADING";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Trim_18()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(T";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_19()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_20()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_21()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_22()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAIL";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_23()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILI";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_24()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILIN";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.TRAILING);
	}

	@Test
	public void test_Trim_25()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Trim_26()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Trim_27()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' F";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Trim_28()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FR";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Trim_29()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FRO";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.FROM);
	}

	@Test
	public void test_Trim_30()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Trim_31()
	{
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING 'd' FROM ";
		int position = query.length();
		testHasIdentifiers(query, position, "e");
	}

	@Test
	public void test_Update_01()
	{
		String query = "U";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_02()
	{
		String query = "UP";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_03()
	{
		String query = "UPD";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_04()
	{
		String query = "UPDA";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_05()
	{
		String query = "UPDAT";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_06()
	{
		String query = "UPDATE";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_07()
	{
		String query = "UPDATE Employee";
		int position = "U".length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_08()
	{
		String query = "UPDATE Employee";
		int position = "UP".length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_09()
	{
		String query = "UPDATE Employee";
		int position = "UPD".length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_10()
	{
		String query = "UPDATE Employee";
		int position = "UPDA".length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_11()
	{
		String query = "UPDATE Employee";
		int position = "UPDAT".length();
		testHasIdentifiers(query, position, Expression.UPDATE);
	}

	@Test
	public void test_Update_12()
	{
		String query = "UPDATE Employee";
		int position = "UPDATE".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_13()
	{
		String query = "UPDATE Employee e ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SET);
	}

	@Test
	public void test_Update_14()
	{
		String query = "UPDATE Employee e S";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SET);
	}

	@Test
	public void test_Update_15()
	{
		String query = "UPDATE Employee e SE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.SET);
	}

	@Test
	public void test_Update_16()
	{
		String query = "UPDATE Employee e SET";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_17()
	{
		String query = "UPDATE SET";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_18()
	{
		String query = "UPDATE S";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_19()
	{
		String query = "UPDATE Employee S";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_20()
	{
		String query = "UPDATE Employee S SET";
		int position = "UPDATE Employee S".length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_21()
	{
		String query = "UPDATE Z";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_22() throws Exception
	{
		IQuery query = namedQuery("Employee", "employee.update3");
		int position = "UPDATE A".length();
		testHasOnlyIdentifiers(query, position, filteredAbstractSchemaNames("A"));
	}

	@Test
	public void test_Update_23()
	{
		String query = "UPDATE Employee e SET ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, "e");
	}

	@Test
	public void test_Update_24()
	{
		String query = "UPDATE Employee SET e";
		int position = "UPDATE Employee SET ".length();
		testDoesNotHaveIdentifiers(query, position);
	}

	@Test
	public void test_Update_25()
	{
		String query = "UPDATE ";
		int position = query.length();
		testHasOnlyIdentifiers(query, position, abstractSchemaNames());
	}

	@Test
	public void test_Update_26()
	{
		String query = "UPDATE Alias a";
		int position = "UPDATE ".length();
		testHasOnlyIdentifiers(query, position, abstractSchemaNames());
	}

	@Test
	public void test_Update_27() throws Exception
	{
		IQuery query = namedQuery("Employee", "employee.update2");
		int position = "UPDATE Al".length();
		testHasOnlyIdentifiers(query, position, "Alias");
	}

	@Test
	public void test_Update_28()
	{
		String query = "UPDATE Employee A SET";
		int position = "UPDATE Employee A ".length();
		testHasIdentifiers(query, position, Expression.SET);
	}

	@Test
	public void test_Update_30()
	{
		String query = "UPDATE Employee A ";
		int position = query.length() - 1;
		testHasOnlyIdentifiers(query, position, Expression.AS);
	}

	@Test
	public void test_Update_31()
	{
		String query = "UPDATE Employee AS ";
		int position = query.length();
		testHasNoIdentifiers(query, position);
	}

	@Test
	public void test_Update_32() throws Exception
	{
		// UPDATE Employee AS e SET e.
		IQuery query = namedQuery("Employee", "employee.update1");
		int position = "UPDATE Employee AS e SET e.".length();
		testHasIdentifiers(query, position, "department", "empId", "manager", "name", "salary", "dept");
	}

	@Test
	public void test_Upper_12()
	{
		String query = "SELECT e FROM Employee e WHERE UPPER";
		testHasNoIdentifiers(query, query.length());
	}

	@Test
	public void test_Where_01()
	{
		String query = "SELECT e FROM Employee e ";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Where_02()
	{
		String query = "SELECT e FROM Employee e WHERE COUNT(e) >= 5";
		int position = query.indexOf(Expression.WHERE);
		testHasIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Where_03()
	{
		String query = "SELECT e FROM Employee e W";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Where_04()
	{
		String query = "SELECT e FROM Employee e WH";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Where_05()
	{
		String query = "SELECT e FROM Employee e WHE";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Where_06()
	{
		String query = "SELECT e FROM Employee e WHER";
		int position = query.length();
		testHasIdentifiers(query, position, Expression.WHERE);
	}

	@Test
	public void test_Where_07()
	{
		String query = "SELECT e FROM Employee e WHERE";
		int position = query.length();
		testDoesNotHaveIdentifiers(query, position, Expression.WHERE);
	}

	protected void testDoesNotHaveIdentifiers(String query, int position)
	{
		testDoesNotHaveIdentifiers
		(
			query,
			position,
			new String[0]
		);
	}

	protected void testDoesNotHaveIdentifiers(String query, int position, Enum<?>... enums)
	{
		testDoesNotHaveIdentifiers
		(
			query,
			position,
			enumNames(enums)
		);
	}

	protected void testDoesNotHaveIdentifiers(String query,
	                                          int position,
	                                          Iterator<String> identifiers)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		while (identifiers.hasNext())
		{
			String identifier = identifiers.next();

			assertFalse
			(
				identifier +  " should not be a choice",
				items.remove(identifier)
			);
		}
	}

	protected void testDoesNotHaveIdentifiers(String query, int position, String... identifiers)
	{
		testDoesNotHaveIdentifiers(query, position, iterator(identifiers));
	}

	protected void testHasIdentifiers(IQuery query, int position, String... identifiers)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		for (String identifier : identifiers)
		{
			assertTrue
			(
				identifier + " should be a choice",
				items.remove(identifier)
			);
		}
	}

	protected void testHasIdentifiers(String query, int position, Enum<?>... enums)
	{
		testHasIdentifiers
		(
			query,
			position,
			enumNames(enums)
		);
	}

	protected void testHasIdentifiers(String query, int position, Iterator<String> identifiers)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		while (identifiers.hasNext())
		{
			String identifier = identifiers.next();

			assertTrue
			(
				identifier + " should be a choice",
				items.remove(identifier)
			);
		}
	}

	protected void testHasIdentifiers(String query, int position, String... identifiers)
	{
		testHasIdentifiers(query, position, iterator(identifiers));
	}

	protected void testHasNoIdentifiers(IQuery query, int position)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		assertFalse
		(
			items + " should not be choice(s)",
			items.hasItems()
		);
	}

	protected void testHasNoIdentifiers(String query, int position)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		assertFalse
		(
			items + " should not be choice(s)",
			items.hasItems()
		);
	}

	protected void testHasOnlyIdentifiers(IQuery query, int position, Enum<?>... identifiers)
	{
		testHasOnlyIdentifiers
		(
			query,
			position,
			enumNames(identifiers)
		);
	}

	protected void testHasOnlyIdentifiers(IQuery query, int position, Iterator<String> identifiers)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		while (identifiers.hasNext())
		{
			String identifier = identifiers.next();

			assertTrue
			(
				String.format("The item %s is not part of the choices", identifier),
				items.remove(identifier)
			);
		}

		assertFalse
		(
			String.format("The list still contains %s", items),
			items.hasItems()
		);
	}

	protected void testHasOnlyIdentifiers(IQuery query, int position, String... identifiers)
	{
		testHasOnlyIdentifiers(query, position, iterator(identifiers));
	}

	protected void testHasOnlyIdentifiers(String query, int position, Collection<String> identifiers)
	{
		testHasOnlyIdentifiers(query, position, identifiers.iterator());
	}

	protected void testHasOnlyIdentifiers(String query, int position, Enum<?>... identifiers)
	{
		testHasOnlyIdentifiers(query, position, enumNames(identifiers));
	}

	protected void testHasOnlyIdentifiers(String query, int position, Iterator<String> identifiers)
	{
		ContentAssistItems items = contentAssistItems(query, position);

		while (identifiers.hasNext())
		{
			String identifier = identifiers.next();

			assertTrue
			(
				String.format("The item %s is not part of the choices", identifier),
				items.remove(identifier)
			);
		}

		assertFalse
		(
			String.format("The list should not contain %s", items),
			items.hasItems()
		);
	}

	protected void testHasOnlyIdentifiers(String query, int position, String... identifiers)
	{
		testHasOnlyIdentifiers(query, position, iterator(identifiers));
	}

	private class ManagedTypeProvider implements IManagedTypeProvider
	{
		private final IJPAVersion version;

		ManagedTypeProvider(IJPAVersion version)
		{
			super();
			this.version = version;
		}

		@Override
		public Iterator<String> entityNames()
		{
			return ContentAssistTest.this.abstractSchemaNames();
		}

		@Override
		public IManagedType getManagedType(IType type)
		{
			throw new IllegalAccessError("ManagedTypeProvider.getManagedType() shouldn't be called for this test, should use a real named query");
		}

		@Override
		public IManagedType getManagedType(String abstractSchemaName)
		{
			throw new IllegalAccessError("ManagedTypeProvider.getManagedType() shouldn't be called for this test, should use a real named query");
		}

		@Override
		public ITypeRepository getTypeRepository()
		{
			throw new IllegalAccessError("ManagedTypeProvider.getTypeRepository() shouldn't be called for this test, should use a real named query");
		}

		@Override
		public IJPAVersion getVersion()
		{
			return version;
		}

		@Override
		public Iterator<IManagedType> managedTypes()
		{
			throw new IllegalAccessError("ManagedTypeProvider.managedTypes() shouldn't be called for this test, should use a real named query");
		}
	}

	private class Query implements IQuery
	{
		private final IManagedTypeProvider provider;
		private final String query;

		Query(String query, IJPAVersion version)
		{
			super();

			this.query    = query;
			this.provider = new ManagedTypeProvider(version);
		}

		@Override
		public String getExpression()
		{
			return query;
		}

		@Override
		public IManagedTypeProvider getProvider()
		{
			return provider;
		}
	}
}