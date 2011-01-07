/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import java.util.List;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class JPQLSimpleTestSuite extends TestSuite {
    public JPQLSimpleTestSuite() {
        setDescription("The unit tests for EJBQL");
    }

    public void addTests() {
        addTest(JPQLTestCase.getBaseTestCase());
        addSpecialTest(new SimpleAbsTest());
        addTest(new SimpleBetweenAndTest());
        addTest(new SimpleBetweenTest());
        addTest(new SimpleConcatTest());
        addTest(new SimpleConcatTestWithConstants1());
        addTest(new SimpleDoubleOrTest());
        addTest(new SimpleEqualsBracketsTest());
        addTest(new SimpleEqualsTest());
        addTest(new SimpleEqualsTestWithJoin());
        addTest(new SimpleEqualsTestWithManualJoin());
        addTest(new SimpleEqualsWithAs());
        //JGL Added to support phones = ?1
        addTest(new CollectionMemberIdentifierEqualsTest());
        //JGL Added to support phones <> ?1
        //Test commented for now...Not sure this query makes sense
        addTest(new CollectionMemberIdentifierNotEqualsTest());
        //JGL Added to support emp = ?1
        //BUG 3003399 in Core
        addTest(new AbstractSchemaIdentifierEqualsTest());
        //JGL Added to support emp <> ?1
        //BUG 3003399 in Core
        addTest(new AbstractSchemaIdentifierNotEqualsTest());

        addTest(new SimpleFromFailed());
        addTest(new SimpleInOneDotTest());
        addTest(new SimpleInOneDotTestReversed());
        addTest(new SimpleInTest());
        addTest(new SimpleLengthTest());
        addTest(new SimpleLikeTest());
        addTest(new SimpleLikeTestWithParameter());
        addTest(new SimpleNotBetweenTest());
        addTest(new SimpleNotEqualsTwoVariables());
        addTest(new SimpleNotEqualsVariablesIngeter());
        addTest(new SimpleNotInTest());
        addTest(new SimpleNotLikeTest());
        addTest(new SimpleOrFollowedByAndTest());
        addTest(new SimpleOrFollowedByAndTestWithStaticNames());
        addTest(new SimpleOrTest());
        addTest(new SimpleParameterTest());
        addTest(new SimpleParameterTestChangingParameters());
        addSpecialTest(new SimpleReverseAbsTest());
        addTest(new SimpleReverseConcatTest());
        addTest(new SimpleReverseEqualsTest());
        addTest(new SimpleReverseLengthTest());
        addTest(new SimpleReverseParameterTest());
        addSpecialTest(new SimpleReverseSqrtTest());
        addTest(new SimpleReverseSubstringTest());
        addSpecialTest(new SimpleSqrtTest());
        addTest(new SimpleSubstringTest());

        addTest(new SimpleNullTest());
        addTest(new SimpleNotNullTest());

        // removed until we decide how to do MEMBER OF
        /*
        addTest(new SimpleMemberOfTest());
        addTest(new SimpleNotMemberOfTest());
        addTest(new SimpleMemberTest());
        addTest(new SimpleNotMemberTest());
        addTest(new SimpleMemberWithParameterTest());
        addTest(SimpleNullTest.getSimpleNullTest());
        addTest(SimpleNullTest.getSimpleNotNullTest());
        addTest(new SimpleMemberOfTest());
        addTest(new SimpleNotMemberOfTest());
        */
        addTest(new SimpleOrFollowedByAndTest());
        addTest(new SimpleNotEqualsVariablesIngeter());
        addTest(new SimpleBetweenAndTest());
        addTest(new SimpleInOneDotTest());
        addTest(new SimpleEqualsBracketsTest());
        addTest(new SimpleEqualsWithAs());

        addTest(new DistinctTest());
        addTest(new ConformResultsInUnitOfWorkTest());

        addTest(ModTest.getSimpleModTest());

        //JGL Added to test IS [NOT] EMPTY -- BUG 2775179 
        addSpecialTest(new SimpleIsEmptyTest());
        addSpecialTest(new SimpleIsNotEmptyTest());
        //JGL Added to test ' (Apostrophe) -- BUG 3105560 
        addTest(new SimpleApostropheTest());
        //JGL Added to test WHERE . '..\_' ...ESCAPE '\' -- BUG 3106981 
        addTest(new SimpleEscapeUnderscoreTest());
        //JGL Added to test l MEMBER OF o.lineItems -- BUG 3107061 
        addTest(new SmallProjectMemberOfProjectsTest());
        //JGL Added to test NOT MEMBER OF
        addTest(new SmallProjectNOTMemberOfProjectsTest());
        //JGL Added BUG 3106877
        addTest(new SelectCOUNTOneToOneTest());
        addTest(new SelectOneToOneTest());
        //JGL Added for BUG 3105651
        addTest(new SelectPhoneNumberDeclaredInINClauseTest());

        IdentifierTest.addTestsTo(this);
        addTest(WhitespaceTest.getWhitespaceTests());

        addTest(BinaryOperatorTest.getSimpleBinaryOperatorTests());
        addTest(BinaryOperatorWithParameterTest.getParameterBinaryOperatorTests());

        // JED Added to test ORDER BY - EJBQL 2.1
        addTest(OrderByTest.getOrderByTestSuite());

        // JED Added to test aggregates - EJBQL 2.1
        addTest(AggregateTest.getSimpleAggregateTestSuite());

        //BUG 3175011
        addTest(new ChangeJPQLStringAfterExecutionTest());
        
        addTest(buildMaxRowsTest());

        //217745
        addTest(new CustomQueryStringTranlateValidationTest());
    
    }
    
    /**
     * This tests using query properties with EJBQL.
     * It verifies that the parse cache is not used if properties are used.
     */
    public TestCase buildMaxRowsTest() {
        return new TestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setEJBQLString("Select Object(Emp) from Employee Emp");
                List results = (List) getSession().executeQuery(query);
                if (results.size() != 12) {
                    throwError("Incorrect number of employees:12->" + results.size());
                }
                // Execute twice to ensure prepare works right.
                results = (List) getSession().executeQuery(query);
                if (results.size() != 12) {
                    throwError("Incorrect number of employees:12->" + results.size());
                }
                query = new ReadAllQuery(Employee.class);
                query.setEJBQLString("Select Object(Emp) from Employee Emp");
                query.setMaxRows(5);
                results = (List) getSession().executeQuery(query);
                if (results.size() != 5) {
                    throwError("Incorrect number of employees:5->" + results.size());
                }
            }
        };
    }

    public void addSpecialTest(JPQLTestCase theTest) {
        theTest.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(theTest);
    }
}
