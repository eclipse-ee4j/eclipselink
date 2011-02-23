/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query;

/**
 * This test tests building a {@link DatabaseQuery} from a JPQL query that is parsed by
 * {@link org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression JPQLExpression}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
//@SuppressWarnings("nls")
public final class HermesQueryParserTest extends AbstractJPQLQueryTest {

//	/**
//	 * The Differentiator that compares two {@link DatabaseQuery queries}.
//	 */
//	private static DiffEngine diffEngine;
//
//	/**
//	 *
//	 */
//	private static HermesParser queryBuilder;
//
//	@AfterClass
//	public static void afterClass() {
//		queryBuilder = null;
//	}
//
//	@BeforeClass
//	public static void beforeClass() {
//		queryBuilder = new HermesParser(true);
//	}
//
//	private Differentiator buildArgumentTypeDifferentiator() {
//		return new Differentiator() {
//			Differentiator differentiator = buildTypeDifferentiator();
//			@Override
//			public boolean comparesValueObjects() {
//				return differentiator.comparesValueObjects();
//			}
//			@Override
//			public Diff diff(Object object1, Object object2) {
//				if (object1 == Object.class ||
//				    object2 == Object.class)
//				{
//					return new NullDiff(object1, object2, this);
//				}
//				return differentiator.diff(object1, object2);
//			}
//			@Override
//			public Diff keyDiff(Object object1, Object object2) {
//				return differentiator.keyDiff(object1, object2);
//			}
//		};
//	}
//
//	private Differentiator buildArgumentTypeNameDifferentiator() {
//		return new Differentiator() {
//			Differentiator differentiator = buildTypeNameDifferentiator();
//			@Override
//			public boolean comparesValueObjects() {
//				return differentiator.comparesValueObjects();
//			}
//			@Override
//			public Diff diff(Object object1, Object object2) {
//				if (Object.class.getName().equals(object1) ||
//				    Object.class.getName().equals(object2))
//				{
//					return new NullDiff(object1, object2, this);
//				}
//				return differentiator.diff(object1, object2);
//			}
//			@Override
//			public Diff keyDiff(Object object1, Object object2) {
//				return differentiator.keyDiff(object1, object2);
//			}
//		};
//	}
//
//	private Differentiator buildReportItemNameDifferentiator(Differentiator differentiator) {
//		return new DifferentiatorWrapper(differentiator) {
//			@Override
//			public Diff diff(Object object1, Object object2) {
//				// NullIfNode create a toString() representation for the attribute name,
//				// which can't be replicated, so we'll ignore that check
//				if (object1 != null && object1.toString().startsWith("NullIf(") &&
//				    object2 != null && object2.toString().equalsIgnoreCase("NULLIF")) {
//
//					return new NullDiff(object1, object2, this);
//				}
//				return super.diff(object1, object2);
//			}
//		};
//	}
//
//	private Differentiator buildTypeDifferentiator() {
//		return new Differentiator() {
//			@Override
//			public boolean comparesValueObjects() {
//				return true;
//			}
//			@Override
//			public Diff diff(Object object1, Object object2) {
//				object1 = wrap(object1);
//				object2 = wrap(object2);
//				return EqualityDifferentiator.instance().diff(object1, object2);
//			}
//			@Override
//			public Diff keyDiff(Object object1, Object object2) {
//				return this.diff(object1, object2);
//			}
//			private Object wrap(Object object) {
//				if (object == boolean.class) {
//					return Boolean.class;
//				}
//				if (object == int.class) {
//					return Integer.class;
//				}
//				if (object == long.class) {
//					return Long.class;
//				}
//				if (object == float.class) {
//					return Float.class;
//				}
//				if (object == double.class) {
//					return Double.class;
//				}
//				if (object == char.class) {
//					return Character.class;
//				}
//				if (object == void.class) {
//					return Void.class;
//				}
//				return object;
//			}
//		};
//	}
//
//	private Differentiator buildTypeNameDifferentiator() {
//		return new Differentiator() {
//			@Override
//			public boolean comparesValueObjects() {
//				return true;
//			}
//			@Override
//			public Diff diff(Object object1, Object object2) {
//				object1 = wrap(object1);
//				object2 = wrap(object2);
//				return EqualityDifferentiator.instance().diff(object1, object2);
//			}
//			@Override
//			public Diff keyDiff(Object object1, Object object2) {
//				return this.diff(object1, object2);
//			}
//			private Object wrap(Object object) {
//				if (object == null) {
//					return object;
//				}
//				if ("boolean".equals(object)) {
//					return Boolean.class.getName();
//				}
//				if ("int".equals(object)) {
//					return Integer.class.getName();
//				}
//				if ("long".equals(object)) {
//					return Long.class.getName();
//				}
//				if ("float".equals(object)) {
//					return Float.class.getName();
//				}
//				if ("double".equals(object)) {
//					return Double.class.getName();
//				}
//				if ("char".equals(object)) {
//					return Character.class.getName();
//				}
//				if ("void".equals(object)) {
//					return Void.class.getName();
//				}
//				return object;
//			}
//		};
//	}
//
//	private DiffEngine diffEngine() {
//		if (diffEngine == null) {
//			diffEngine = new DiffEngine();
//			populateDiffEngine();
//		}
//		return diffEngine;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected JavaManagedTypeProvider persistenceUnit() throws Exception {
//		return (JavaManagedTypeProvider) super.persistenceUnit();
//	}
//
//	private void populateDiffEngine() {
//		ReflectiveDifferentiator rd;
//
//		// Query
//		rd = diffEngine.addReflectiveDifferentiator(DatabaseQuery.class);
//			rd.addMapFieldNamed("properties");
//			rd.addListFieldsNamed("arguments", "argumentFields", "argumentValues", "accessors");
//			rd.setFieldDifferentiator(
//				"argumentTypes",
//				new OrderedContainerDifferentiator(ListAdapter.instance(), buildArgumentTypeDifferentiator())
//			);
//			rd.setFieldDifferentiator(
//				"argumentTypeNames",
//				new OrderedContainerDifferentiator(ListAdapter.instance(), buildArgumentTypeNameDifferentiator())
//			);
//
//		rd = diffEngine.addReflectiveDifferentiator(ReportQuery.class);
//			rd.addListFieldsNamed("names", "items", "groupByExpressions");
//			rd.addCollectionFieldNamed("returnedKeys");
//			rd.addReferenceListFieldsNamed("groupByExpressions");
////			rd.addReferenceFieldNamed("havingExpression");
//
//		rd = diffEngine.addReflectiveDifferentiator(UpdateAllQuery.class);
//			rd.addReferenceMapFieldNamed("m_updateClauses");
//
//		rd = diffEngine.addReflectiveDifferentiator(DeleteAllQuery.class);
//
//		rd = diffEngine.addReflectiveDifferentiator(ModifyAllQuery.class);
//			rd.addReferenceFieldNamed("defaultBuilder");
//
//		rd = diffEngine.addReflectiveDifferentiator(ObjectBuildingQuery.class);
//			rd.ignoreFieldNamed("shouldBuildNullForNullPk"); // Temporary
//
//		rd = diffEngine.addReflectiveDifferentiator(ReadAllQuery.class);
//			rd.addReferenceFieldsNamed("startWithExpression", "connectByExpression");
//			rd.addListFieldNamed("orderSiblingsByExpressions");
//
//		rd = diffEngine.addReflectiveDifferentiator(ObjectLevelReadQuery.class);
//			rd.addListFieldNamed("additionalFields");
//			rd.addListFieldsNamed("nonFetchJoinAttributeExpressions",
//			                      "partialAttributeExpressions",
//			                      "orderByExpressions");
//			rd.addMapFieldNamed("concreteSubclassCalls");
//
//		rd = diffEngine.addReflectiveDifferentiator(JoinedAttributeManager.class);
//			rd.addListFieldsNamed("joinedAggregateMappings",
//			                      "joinedAttributeMappings",
//			                      "joinedAttributeExpressions",
//			                      "joinedAttributes",
//			                      "dataResults",
//			                      "orderByExpressions",
//			                      "additionalFieldExpressions");
//			rd.addMapFieldsNamed("joinedMappingIndexes",
//			                     "joinedMappingQueries",
//			                     "joinedMappingQueryClones",
//			                     "dataResultsByPrimaryKey");
//			rd.addReferenceFieldsNamed("baseExpressionBuilder",
//			                           "baseQuery");
//
//		rd = diffEngine.addReflectiveDifferentiator(DatabaseQueryMechanism.class);
//			rd.addReferenceFieldNamed("query");
//
//		rd = diffEngine.addReflectiveDifferentiator(JPQLCallQueryMechanism.class);
//			rd.ignoreFieldNamed("ejbqlCall"); // ???
//
//		rd = diffEngine.addReflectiveDifferentiator(JPQLCall.class);
//			rd.addReferenceFieldNamed("query");
//
//		rd = diffEngine.addReflectiveDifferentiator(ReportItem.class);
//			rd.setFieldDifferentiator("name", buildReportItemNameDifferentiator(diffEngine.getRecordingDifferentiator()));
//			rd.setFieldDifferentiator("resultType", buildArgumentTypeDifferentiator());
//			rd.addReferenceFieldNamed("attributeExpression");
//
//		// Expression
//		rd = diffEngine.addReflectiveDifferentiator(ExpressionBuilder.class);
//			rd.addKeyFieldNamed("queryClass");
//			rd.ignoreFieldNamed("session");
//		rd = diffEngine.addReflectiveDifferentiator(ObjectExpression.class);
//			rd.addReferenceListFieldNamed("derivedExpressions");
//		rd = diffEngine.addReflectiveDifferentiator(Expression.class);
//		rd = diffEngine.addReflectiveDifferentiator(RelationExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(CompoundExpression.class);
////		rd = (ReflectiveDifferentiator) diffEngine.setUserDifferentiator(CompoundExpression.class, buildCompoundExpressionDifferentiator());
//			rd.addReferenceFieldsNamed("builder", "firstChild", "secondChild");
//		rd = diffEngine.addReflectiveDifferentiator(LogicalExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(DataExpression.class);
//			rd.addListFieldsNamed("derivedTables", "derivedFields");
//		rd = diffEngine.addReflectiveDifferentiator(BaseExpression.class);
//			rd.addReferenceFieldsNamed("baseExpression", "builder");
//		rd = diffEngine.addReflectiveDifferentiator(QueryKeyExpression.class);
//			rd.addKeyFieldNamed("name");
//		rd = diffEngine.addReflectiveDifferentiator(ParameterExpression.class);
//			rd.addKeyFieldNamed("field");
//			rd.addReferenceFieldNamed("localBase");
//			rd.setFieldDifferentiator("type", buildTypeDifferentiator());
//		rd = diffEngine.addReflectiveDifferentiator(FunctionExpression.class);
//			rd.addReferenceListFieldNamed("children");
//			rd.addReferenceFieldNamed("operator");
//			rd.setFieldDifferentiator("resultType", buildArgumentTypeDifferentiator());
//		rd = diffEngine.addReflectiveDifferentiator(ExpressionOperator.class);
//			rd.addArrayFieldsNamed("databaseStrings", "javaStrings");
//			rd.ignoreFieldNamed("argumentIndices"); // TODO: int[]
//		rd = diffEngine.addReflectiveDifferentiator(ConstructorReportItem.class);
//			rd.setFieldDifferentiator("constructorArgTypes", new OrderedContainerDifferentiator(ArrayAdapter.instance(), buildTypeDifferentiator()));
//			rd.addListFieldsNamed("constructorMappings", "reportItems");
//		rd = diffEngine.addReflectiveDifferentiator(ConstantExpression.class);
//			rd.addReferenceFieldNamed("localBase");
////			rd.ignoreFieldNamed("localBase"); // TODO
//		rd = diffEngine.addReflectiveDifferentiator(MapEntryExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(SubSelectExpression.class);
//			rd.addReferenceFieldsNamed("criteriaBase", "subQuery");
//			rd.setFieldDifferentiator("returnType", buildTypeDifferentiator());
//		rd = diffEngine.addReflectiveDifferentiator(ClassTypeExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(IndexExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(FieldExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(AsOfClause.class);
//		rd = diffEngine.addReflectiveDifferentiator(ManualQueryKeyExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(TableExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(ArgumentListFunctionExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(CollectionExpression.class);
//		rd = diffEngine.addReflectiveDifferentiator(LiteralExpression.class);
//			rd.addReferenceFieldNamed("localBase");
//		rd = diffEngine.addReflectiveDifferentiator(TableAliasLookup.class);
//		rd = diffEngine.addReflectiveDifferentiator(DateConstantExpression.class);
//	}
//
//	private DatabaseQuery postInitialize(DatabaseQuery databaseQuery,
//	                                     AbstractSession session) throws Exception {
//
//		// If the query uses fetch joins, need to use JPA default of not
//		// filtering duplicates.
//		if (databaseQuery.isReadAllQuery()) {
//			ReadAllQuery readAllQuery = (ReadAllQuery) databaseQuery;
//
//			if (readAllQuery.hasJoining() &&
//			   (readAllQuery.getDistinctState() == ReadAllQuery.DONT_USE_DISTINCT)) {
//
//				readAllQuery.setShouldFilterDuplicates(false);
//			}
//		}
//
//		((JPQLCallQueryMechanism) databaseQuery.getQueryMechanism()).getJPQLCall().setIsParsed(true);
//
//		// GF#1324 eclipselink.refresh query hint does not cascade
//		// cascade by mapping as default for read query
//		if (databaseQuery.isReadQuery()) {
//			databaseQuery.cascadeByMapping();
//		}
//
//      // If a primary key query, switch to read-object to allow cache hit
//		if (databaseQuery.isReadAllQuery() &&
//		   !databaseQuery.isReportQuery()  &&
//		   ((ReadAllQuery) databaseQuery).shouldCheckCache())
//		{
//			ReadAllQuery readQuery = (ReadAllQuery) databaseQuery;
//
//			if ((readQuery.getContainerPolicy().getContainerClass() == ContainerPolicy.getDefaultContainerClass()) &&
//			    !readQuery.hasHierarchicalExpressions())
//			{
//				databaseQuery.checkDescriptor(session());
//				Expression selectionCriteria = databaseQuery.getSelectionCriteria();
//
//				if ((selectionCriteria) != null && databaseQuery.getDescriptor().getObjectBuilder().isPrimaryKeyExpression(true, selectionCriteria, session())) {
//					ReadObjectQuery newQuery = new ReadObjectQuery();
//					newQuery.copyFromQuery(databaseQuery);
//					databaseQuery = newQuery;
//				}
//			}
//		}
//
//		return databaseQuery;
//	}
//
//	private AbstractSession session() throws Exception {
//		return persistenceUnit().getSession();
//	}
//
//	private void test_buildQuery(String queryName) throws Exception {
//
//		// Retrieve the EclipseLink's DatabaseQuery
//		DatabaseQuery databaseQuery = session().getQuery(queryName);
//		assertNotNull("The query named " + queryName + " was not found on the session", databaseQuery);
//
//		// Manually create the query for debug purpose
//		EJBQueryImpl.buildEJBQLDatabaseQuery(queryName, databaseQuery.getJPQLString(), session(), null, null, persistenceUnit().getClassLoader());
//
//		// Create our version of the DatabaseQuery
//		DatabaseQuery query = queryBuilder.buildQuery(databaseQuery.getJPQLString(), session());
//		DatabaseQuery newDatabaseQuery = query.getClass().newInstance();
//		query = postInitialize(query, session());
//		query.setName(queryName);
//
//		// Compare the two queries
//		DiffEngine engine = diffEngine();
//		Diff diff = engine.diff(databaseQuery, query);
//		assertTrue(diff.getDescription(), diff.identical());
//
//		// Create our version of the DatabaseQuery
//		queryBuilder.populateQuery(databaseQuery.getJPQLString(), newDatabaseQuery, session());
//		newDatabaseQuery = postInitialize(newDatabaseQuery, session());
//		newDatabaseQuery.setName(queryName);
//
//		// Compare the two queries
//		diff = engine.diff(databaseQuery, newDatabaseQuery);
//		assertTrue(diff.getDescription(), diff.identical());
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_01() throws Exception {
//		// SELECT LENGTH(a.street) FROM Address a
//		test_buildQuery("address.length");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_02() throws Exception {
//		// SELECT c FROM Address a JOIN a.customerList c
//		test_buildQuery("address.collection");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_03() throws Exception {
//		// SELECT INDEX(c) FROM Address a JOIN a.customerList c
//		test_buildQuery("address.index");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_04() throws Exception {
//		// SELECT CONCAT(a.street, a.city) FROM Address a
//		test_buildQuery("address.concat");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_05() throws Exception {
//		// SELECT LOCATE(a.street, 'Arco Drive') FROM Address a
//		test_buildQuery("address.locate");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_06() throws Exception {
//		// SELECT SIZE(a.customerList) FROM Address a JOIN a.customerList c
//		test_buildQuery("address.size");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_07() throws Exception {
//		// SELECT c.lastName FROM Address a JOIN a.customerList AS c
//		test_buildQuery("address.stateField");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_08() throws Exception {
//		// SELECT SUBSTRING(a.state, 0, 1) FROM Address a
//		test_buildQuery("address.substring");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_09() throws Exception {
//		// SELECT a.id + 2 FROM Address a
//		test_buildQuery("address.addition");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_10() throws Exception {
//		// SELECT a FROM Address a, Employee e JOIN FETCH a.customerList, Alias s JOIN FETCH s.addresses
//		test_buildQuery("address.join");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_11() throws Exception {
//		// SELECT MOD(a.id, 2) FROM Address a JOIN FETCH a.customerList
//		// should be
//		// SELECT MOD(a.id, 2) AS m FROM Address a JOIN FETCH a.customerList ORDER BY m
//		test_buildQuery("address.join.fetch");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_12() throws Exception {
//		// SELECT DISTINCT COUNT(a) FROM Address a
//		test_buildQuery("address.count");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_13() throws Exception {
//		// SELECT e FROM Address a LEFT JOIN a.customerList e
//		test_buildQuery("address.join.left");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_14() throws Exception {
//		// SELECT e FROM Address a INNER JOIN a.customerList e
//		test_buildQuery("address.join.inner");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_15() throws Exception {
//		// SELECT e FROM Address a LEFT OUTER JOIN a.customerList e
//		test_buildQuery("address.join.left.outer");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_16() throws Exception {
//		// SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList
//		test_buildQuery("address.member");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_17() throws Exception {
//		// SELECT a FROM Address a, Customer c WHERE c NOT MEMBER OF a.customerList
//		test_buildQuery("address.member.not");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_18() throws Exception {
//		// SELECT c.dept FROM Address a LEFT OUTER JOIN a.customerList c
//		test_buildQuery("address.relationship1");
//	}
//
//	@Test
//	public void test_buildQuery_Address_Select_19() throws Exception {
//		// SELECT c.lastName FROM Address a LEFT OUTER JOIN a.customerList c
//		test_buildQuery("address.relationship2");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_01() throws Exception {
//		// SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1
//		test_buildQuery("alias.param1");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_02() throws Exception {
//		// SELECT KEY(k) FROM Alias a JOIN a.ids k
//		test_buildQuery("alias.key1");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_03() throws Exception {
//		// SELECT VALUE(v) FROM Alias a JOIN a.ids v
//		test_buildQuery("alias.value1");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_04() throws Exception {
//		// SELECT v FROM Alias a JOIN a.ids v
//		test_buildQuery("alias.value2");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_05() throws Exception {
//		// SELECT ENTRY(e) FROM Alias a JOIN a.ids e
//		test_buildQuery("alias.entry");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_06() throws Exception {
//		// SELECT KEY(e).firstName FROM Alias a JOIN a.addresses e
//		test_buildQuery("alias.key2");
//	}
//
//	@Test
//	public void test_buildQuery_Alias_Select_07() throws Exception {
//		// SELECT VALUE(e).zip.code FROM Alias a JOIN a.employees e
//		test_buildQuery("alias.value3");
//	}
//
//	@Test
//	public void test_buildQuery_Customer_Select_01() throws Exception {
//		// SELECT c FROM Customer c
//		test_buildQuery("customer.findAll");
//	}
//
//	@Test
//	public void test_buildQuery_Customer_Select_02() throws Exception {
//		// select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)
//		test_buildQuery("customer.name");
//	}
//
//	@Test
//	public void test_buildQuery_Customer_Select_03() throws Exception {
//		// select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)
//		test_buildQuery("customer.substring");
//	}
//
//	@Test
//	public void test_buildQuery_Customer_Select_04() throws Exception {
//		// SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area
//		test_buildQuery("customer.area");
//	}
//
//	@Test
//	public void test_buildQuery_Customer_Select_05() throws Exception {
//		// SELECT c from Customer c where c.home.city IN :city
//		test_buildQuery("customer.city");
//	}
//
//	@Test
//	public void test_buildQuery_Customer_Select_06() throws Exception {
//		// SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c
//		test_buildQuery("customer.new");
//	}
//
//	@Test
//	public void test_buildQuery_Dept_Delete_01() throws Exception {
//		// DELETE FROM Department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'
//		test_buildQuery("dept.and.multiple");
//	}
//
//	@Test
//	public void test_buildQuery_Dept_Select_01() throws Exception {
//		// select o from Dept o
//		test_buildQuery("dept.findAll");
//	}
//
//	@Test
//	public void test_buildQuery_Dept_Select_02() throws Exception {
//		// select o from Dept o where o.dname in (:dname1, :dname2, :dname3)
//		test_buildQuery("dept.dname");
//	}
//
//	@Test
//	public void test_buildQuery_Dept_Select_03() throws Exception {
//		// select d.floorNumber from Dept d
//		test_buildQuery("dept.floorNumber");
//	}
//
//	@Test
//	public void test_buildQuery_Dept_Select_04() throws Exception {
//		// SELECT NEW java.util.Vector(d.dname) FROM Dept d
//		test_buildQuery("dept.new1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Delete_01() throws Exception {
//		// DELETE FROM Employee e
//		test_buildQuery("employee.delete");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Delete_02() throws Exception {
//		// DELETE FROM Employee e WHERE e.department = :dept
//		test_buildQuery("employee.delete.dept");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_01() throws Exception {
//		// SELECT e FROM Employee e
//		test_buildQuery("employee.select");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_02() throws Exception {
//		// SELECT e.name, d.dname FROM Employee e, Dept d
//		test_buildQuery("employee.collection");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_03() throws Exception {
//		// SELECT e FROM Employee e WHERE e.name = ?1 ORDER BY e.name
//		test_buildQuery("employee.?1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_04() throws Exception {
//		// SELECT 2 + 2.2F FROM Employee e
//		test_buildQuery("employee.addition1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_05() throws Exception {
//		// SELECT AVG(e.salary) + 2E2 FROM Employee e
//		test_buildQuery("employee.addition2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_06() throws Exception {
//		// SELECT e.salary + 2 FROM Employee e
//		test_buildQuery("employee.addition3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_07() throws Exception {
//		// SELECT CASE WHEN e.name = 'Java Persistence Query Language' THEN 'Java Persistence Query Language' WHEN e.salary BETWEEN 1 and 2 THEN SUBSTRING(e.name, 0, 2) ELSE e.name END FROM Employee e
//		test_buildQuery("employee.case1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_08() throws Exception {
//		// SELECT CASE WHEN e.name = 'JPQL' THEN e.working WHEN e.salary BETWEEN 1 and 2 THEN TRUE ELSE p.completed END FROM Employee e, Project p
//		test_buildQuery("employee.case2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_09() throws Exception {
//		// SELECT CASE WHEN e.name = 'JPQL' THEN e.working WHEN e.salary BETWEEN 1 and 2 THEN SUBSTRING(e.name, 0, 2) ELSE e.dept END FROM Employee e
//		test_buildQuery("employee.case3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_10() throws Exception {
//		// SELECT e FROM Employee E
//		test_buildQuery("employee.caseInsensitive");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_11() throws Exception {
//		// SELECT CURRENT_DATE FROM Employee e
//		test_buildQuery("employee.date1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_12() throws Exception {
//		// SELECT {d '2008-12-31'} FROM Employee e
//		test_buildQuery("employee.date2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_13() throws Exception {
//		// SELECT e FROM Employee e WHERE e.salary = (SELECT MAX(e.salary) FROM Employee a WHERE a.department = :dept)
//		test_buildQuery("employee.dept");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_14() throws Exception {
//		// SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base
//		test_buildQuery("employee.deptBase");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_15() throws Exception {
//		// select e from Employee e where e.dept.deptno in :deptno
//		test_buildQuery("employee.deptno");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_16() throws Exception {
//		// SELECT 2 / 2.2F FROM Employee e
//		test_buildQuery("employee.division1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_17() throws Exception {
//		// SELECT AVG(e.salary) / 2E2 FROM Employee e
//		test_buildQuery("employee.division2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_18() throws Exception {
//		// SELECT e.salary / 2 FROM Employee e
//		test_buildQuery("employee.division3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_19() throws Exception {
//		// SELECT CASE WHEN e.name = 'Pascal' THEN com.titan.domain.EnumType.FIRST_NAME WHEN e.name = 'JPQL' THEN com.titan.domain.EnumType.LAST_NAME ELSE com.titan.domain.EnumType.NAME END FROM Employee e
//		test_buildQuery("employee.enum");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_20() throws Exception {
//		// SELECT LOWER(e.name) FROM Employee e
//		test_buildQuery("employee.lower");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_21() throws Exception {
//		// SELECT FALSE FROM Employee e
//		test_buildQuery("employee.false");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_22() throws Exception {
//		// SELECT e FROM Employee e
//		test_buildQuery("employee.findAll");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_23() throws Exception {
//		// SELECT FUNC('toString', e.name) FROM Employee e
//		test_buildQuery("employee.func1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_24() throws Exception {
//		// SELECT FUNC('age', e.empId, e.salary) FROM Employee e
//		test_buildQuery("employee.func2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_25() throws Exception {
//		// SELECT FUNC('age', e.empId, e.name) FROM Employee e
//		test_buildQuery("employee.func3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_26() throws Exception {
//		// SELECT FUNC('age', e.empId, :name) FROM Employee e
//		test_buildQuery("employee.func4");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_27() throws Exception {
//		// SELECT MAX(e.salary) FROM Employee e
//		test_buildQuery("employee.max");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_28() throws Exception {
//		// SELECT MIN(e.salary) FROM Employee e
//		test_buildQuery("employee.min");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_29() throws Exception {
//		// SELECT MOD(e.salary, e.empId) FROM Employee e
//		test_buildQuery("employee.mod");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_30() throws Exception {
//		// SELECT 2 * 2.2F FROM Employee e
//		test_buildQuery("employee.multiplication1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_31() throws Exception {
//		// SELECT AVG(e.salary) * 2E2 FROM Employee e
//		test_buildQuery("employee.multiplication2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_32() throws Exception {
//		// SELECT e.name * 2 FROM Employee e
//		test_buildQuery("employee.multiplication3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_33() throws Exception {
//		// SELECT NULLIF(e.name, 'JPQL') FROM Employee e
//		test_buildQuery("employee.nullif1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_34() throws Exception {
//		// SELECT NULLIF(2 + 2, 'JPQL') FROM Employee e
//		test_buildQuery("employee.nullif2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_35() throws Exception {
//		// SELECT e.name AS n From Employee e
//		test_buildQuery("employee.resultVariable1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_36() throws Exception {
//		// SELECT e.name n From Employee e
//		test_buildQuery("employee.resultVariable2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_37() throws Exception {
//		// SELECT e.salary / 1000D n From Employee e
//		test_buildQuery("employee.resultVariable3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_38() throws Exception {
//		// SELECT SUM(e.salary) FROM Employee e
//		test_buildQuery("employee.sum");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_39() throws Exception {
//		// SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p JOIN p.employees emp WHERE emp = e AND p.name = :name)
//		test_buildQuery("employee.subquery1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_40() throws Exception {
//		// SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)
//		test_buildQuery("employee.subquery.code_1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_41() throws Exception {
//		// SELECT 2 - 2.2F FROM Employee e
//		test_buildQuery("employee.substraction1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_42() throws Exception {
//		// SELECT AVG(e.salary) - 2E2 FROM Employee e
//		test_buildQuery("employee.substraction2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_43() throws Exception {
//		// SELECT e.name - 2 FROM Employee e
//		test_buildQuery("employee.substraction3");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_44() throws Exception {
//		// SELECT TRIM(e.name) FROM Employee e
//		test_buildQuery("employee.trim");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_45() throws Exception {
//		// SELECT TRUE FROM Employee e
//		test_buildQuery("employee.true");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_46() throws Exception {
//		// UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2
//		test_buildQuery("employee.update.positional");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_47() throws Exception {
//		// SELECT UPPER(e.name) FROM Employee e
//		test_buildQuery("employee.upper");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_48() throws Exception {
//		// SELECT OBJECT(e) FROM Employee e WHERE e.phoneNumbers IS EMPTY and e.name like 'testFlushModeOnUpdateQuery'
//		test_buildQuery("employee.and");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_49() throws Exception {
//		// SELECT a FROM Employee e LEFT JOIN e.address a
//		test_buildQuery("employee.join.left1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_50() throws Exception {
//		// SELECT m, e FROM Employee e LEFT JOIN e.managerEmployee m
//		test_buildQuery("employee.join.left2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_51() throws Exception {
//		// SELECT e FROM Employee e WHERE e.empId in (SELECT MIN(ee.empId) FROM Employee ee)
//		test_buildQuery("employee.subquery2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_52() throws Exception {
//		// SELECT e FROM Employee e JOIN FETCH e.address WHERE e.empId = :ID
//		test_buildQuery("employee.join.fetch1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_53() throws Exception {
//		// SELECT e, e.name FROM Employee e JOIN FETCH e.address WHERE e.empId = :ID
//		test_buildQuery("employee.join.fetch2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_54() throws Exception {
//		// SELECT OBJECT(e) FROM Employee e
//		test_buildQuery("employee.object1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_55() throws Exception {
//		// SELECT OBJECT(e) FROM Employee e WHERE e.name = ?1
//		test_buildQuery("employee.object2");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Select_56() throws Exception {
//		// DELETE FROM Dept d WHERE d.dname = 'DEPT A' AND d.role = 'ROLE A' AND d.loc = 'LOCATION A'
//		test_buildQuery("dept.and.multiple");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Update_01() throws Exception {
//		// UPDATE Employee e SET e.name = ?1
//		test_buildQuery("employee.update1");
//	}
//
//	@Test
//	public void test_buildQuery_Employee_Update_02() throws Exception {
//		// UPDATE Employee e set e.salary = e.roomNumber, e.roomNumber = e.salary, e.address = null where e.name = 'testUpdateUsingTempStorage'
//		test_buildQuery("employee.update2");
//	}
//
//	@Test
//	public void test_buildQuery_Order_Select_01() throws Exception {
//		// select object(o) FROM Order o Where SQRT(o.totalPrice) > :doubleValue
//		test_buildQuery("order.doubleValue");
//	}
//
//	@Test
//	public void test_buildQuery_Phone_Select_01() throws Exception {
//		// SELECT p FROM Phone p
//		test_buildQuery("phone.findAll");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Select_01() throws Exception {
//		// SELECT ABS(p.id) FROM Project p
//		test_buildQuery("project.abs");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Select_02() throws Exception {
//		// SELECT DISTINCT p From Product p where p.shelfLife.soldDate NOT BETWEEN :date1 AND :newdate
//		test_buildQuery("product.date");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Select_03() throws Exception {
//		// Select Distinct Object(p) from Product p where (p.quantity > (500 + :int1)) AND (p.partNumber IS NULL)
//		test_buildQuery("product.int1");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Select_04() throws Exception {
//		// SELECT AVG(p.quantity) FROM Product p
//		test_buildQuery("product.quantity");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Select_05() throws Exception {
//		// SELECT MAX(p.quantity) FROM Product p
//		test_buildQuery("product.max");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Select_06() throws Exception {
//		// SELECT MIN(p.quantity) FROM Product p
//		test_buildQuery("product.min");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Update_01() throws Exception {
//		// UPDATE Product AS p SET p.partNumber = NULL
//		test_buildQuery("product.null");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Update_02() throws Exception {
//		// UPDATE Product SET shelfLife.soldDate = CURRENT_DATE WHERE shelfLife IS NOT NULL AND shelfLife.soldDate <> CURRENT_DATE
//		test_buildQuery("product.update1");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Update_03() throws Exception {
//		// UPDATE Product SET partNumber = CASE type WHEN com.titan.domain.EnumType.FIRST_NAME THEN '1' WHEN com.titan.domain.EnumType.LAST_NAME THEN '2' ELSE '3' END
//		test_buildQuery("product.update2");
//	}
//
//	@Test
//	public void test_buildQuery_Product_Update_04() throws Exception {
//		// UPDATE Product SET partNumber = CASE TYPE(project) WHEN LargeProject THEN '2' WHEN SmallProject THEN '3' ELSE '4' END
//		test_buildQuery("product.update3");
//	}
//
//	@Test
//	public void test_buildQuery_Project_Select_01() throws Exception {
//		// SELECT COUNT(p) FROM Project p WHERE TYPE(p) = AbstractProduct
//		test_buildQuery("product.type1");
//	}
//
//	@Test
//	public void test_buildQuery_Project_Select_02() throws Exception {
//		// SELECT p FROM Project p WHERE TYPE(p) IN(LargeProject, SmallProject)
//		test_buildQuery("product.type2");
//	}
//
//	@Test
//	public void test_buildQuery_Project_Update_01() throws Exception {
//		// UPDATE Project SET name = 'JPQL'
//		test_buildQuery("project.update1");
//	}
//
//	@Test
//	public void test_buildQuery_Project_Update_02() throws Exception {
//		// UPDATE Project AS p SET p.name = 'JPQL' WHERE p.completed = TRUE
//		test_buildQuery("project.update2");
//	}
//
//	@Test
//	public void test_buildQuery_Project_Update_03() throws Exception {
//		// UPDATE Project AS p SET p.name = 'JPQL' WHERE p.completed = FALSE
//		test_buildQuery("project.update3");
//	}
//
//	@Test
//	public void test_buildQuery_Project_Update_04() throws Exception {
//		// UPDATE Project AS p SET p.name = null
//		test_buildQuery("project.update4");
//	}
//
//	@Test
//	public void test_buildSelectionCriteria_01() throws Exception {
//
//		String selectionCriteria = "this.department = 'JPQL'";
//
//		Map<String, Class<?>> arguments = new HashMap<String, Class<?>>();
//		Expression expression = queryBuilder.buildSelectionCriteria("Employee", selectionCriteria, session(), arguments);
//
//		// This is required because EclipseLink does it to check something
//		expression.getBuilder();
//
//		// Manually create the query for debug purpose
//		String jpqlQuery = "SELECT this FROM Employee this WHERE " + selectionCriteria;
//		DatabaseQuery query = EJBQueryImpl.buildEJBQLDatabaseQuery(null, jpqlQuery, session(), null, null, persistenceUnit().getClassLoader());
//
//		// Compare the two selection criteria
//		DiffEngine engine = diffEngine();
//		Diff diff = engine.diff(query.getSelectionCriteria(), expression);
//		assertTrue(diff.getDescription(), diff.identical());
//	}
//
//	@Test
//	public void test_buildSelectionCriteria_02() throws Exception {
//
//		String selectionCriteria = "this.name LIKE 'Ottawa%'";
//
//		Map<String, Class<?>> arguments = new HashMap<String, Class<?>>();
//		Expression expression = queryBuilder.buildSelectionCriteria("Employee", selectionCriteria, session(), arguments);
//
//		// This is required because EclipseLink does it to check something
//		expression.getBuilder();
//
//		// Manually create the query for debug purpose
//		String jpqlQuery = "SELECT this FROM Employee this WHERE " + selectionCriteria;
//		DatabaseQuery query = EJBQueryImpl.buildEJBQLDatabaseQuery(null, jpqlQuery, session(), null, null, persistenceUnit().getClassLoader());
//
//		// Compare the two selection criteria
//		DiffEngine engine = diffEngine();
//		Diff diff = engine.diff(query.getSelectionCriteria(), expression);
//		assertTrue(diff.getDescription(), diff.identical());
//	}
}