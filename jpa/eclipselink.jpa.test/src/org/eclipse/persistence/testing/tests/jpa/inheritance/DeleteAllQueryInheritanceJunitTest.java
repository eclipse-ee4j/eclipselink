/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.Vector;

import junit.framework.*;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.*;
import org.eclipse.persistence.testing.framework.DeleteAllQueryTestHelper;
 
public class DeleteAllQueryInheritanceJunitTest extends JUnitTestCase {
        
    static Vector originalVehicleObjects;
    static Vector originalCompanyObjects;
    static ReportQuery reportQueryVehicles;
    static ReportQuery reportQueryCompanies; 
    {
        reportQueryVehicles = new ReportQuery(Vehicle.class, new ExpressionBuilder());
        reportQueryVehicles.setShouldRetrievePrimaryKeys(true);
        reportQueryCompanies = new ReportQuery(Company.class, new ExpressionBuilder());
        reportQueryCompanies.setShouldRetrievePrimaryKeys(true);
    }
    
    public DeleteAllQueryInheritanceJunitTest() {
        super();
    }
    
    public DeleteAllQueryInheritanceJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        Vector currentVehicleObjects = (Vector)getDbSession().executeQuery(reportQueryVehicles);
        Vector currentCompanyObjects = (Vector)getDbSession().executeQuery(reportQueryCompanies);
        if(!currentVehicleObjects.equals(originalVehicleObjects) || !currentCompanyObjects.equals(originalCompanyObjects)) {
            if(!currentVehicleObjects.isEmpty() || !currentCompanyObjects.isEmpty()) {
                clearVehiclesCompanies();
            }
            populateVehiclesCompanies();
            originalVehicleObjects = (Vector)getDbSession().executeQuery(reportQueryVehicles);
            originalCompanyObjects = (Vector)getDbSession().executeQuery(reportQueryCompanies);
        }
        clearCache();
    }
    
    protected static DatabaseSession getDbSession() {
        return getServerSession();   
    }
    
    protected static UnitOfWork acquireUnitOfWork() {
        return getDbSession().acquireUnitOfWork();   
    }
    
    protected static void clearVehiclesCompanies() {
        UnitOfWork uow = acquireUnitOfWork();
        // delete all Vechicles
        uow.executeQuery(new DeleteAllQuery(Vehicle.class));
        // delete all Companies
        uow.executeQuery(new DeleteAllQuery(Company.class));
        uow.commit();
        clearCache();
    }
    
    protected static void populateVehiclesCompanies() {
        UnitOfWork uow = acquireUnitOfWork();
        uow.registerNewObject(InheritanceModelExamples.companyExample1());
        uow.registerNewObject(InheritanceModelExamples.companyExample2());
        uow.registerNewObject(InheritanceModelExamples.companyExample3());
        uow.commit();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DeleteAllQueryInheritanceJunitTest.class);
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    // JUnit framework will automatically execute all methods starting with test...
    // The test methods' name pattern is a word "test" followed by underscore and the used selectionExpression:
    // test_selectionExpression
    
    // ALL Vehicles
    public static void test_null() {
        deleteAllQueryInternal_Deferred_Children(Vehicle.class, null);
    }
    
    // ALL Vehicles - nondeferred (execute deleteAllQuery immediately as opposed to during uow.commit)
    public static void test_nullNonDeferred() {
        deleteAllQueryInternal_NonDeferred_Children(Vehicle.class, null);
    }
    
    // Vehicles owned by TOP Company
    public static void test_ownerTOP() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("owner").get("name").equal("TOP");
        deleteAllQueryInternal_Deferred_Children(Vehicle.class, exp);
    }
    
    // FueledVehicles running on Petrol
    public static void test_fuelTypePetrol() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("fuelType").equalsIgnoreCase("Petrol");        
        deleteAllQueryInternal_Deferred_Children(FueledVehicle.class, exp);
    }
    
    // shchool buses without drivers
    public static void test_schoolBusNullDriver() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("description").equalsIgnoreCase("School bus").and(builder.get("busDriver").isNull()); 
        deleteAllQueryInternal_Deferred_Children(Bus.class, exp);
    }
    
    // FueledVehicles owned by Companies that also own NonFueledVehicles
    public static void test_ownerOwnsNonFueledVehicle() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ExpressionBuilder subBuilder = new ExpressionBuilder();
        ReportQuery rq = new ReportQuery(NonFueledVehicle.class, subBuilder);
        rq.addAttribute("id");
        Expression subExpression = subBuilder.get("owner").equal(builder.get("owner"));
        rq.setSelectionCriteria(subExpression);

        Expression exp = builder.exists(rq);
        deleteAllQueryInternal_Deferred_Children(FueledVehicle.class, exp);
    }
    
    protected static void deleteAllQueryInternal_Deferred_Children(Class referenceClass, Expression selectionExpression) {
        deleteAllQueryInternal(referenceClass, selectionExpression, true, true);
    }
    
    protected static void deleteAllQueryInternal_NonDeferred_Children(Class referenceClass, Expression selectionExpression) {
        deleteAllQueryInternal(referenceClass, selectionExpression, false, true);
    }
    
    protected static void deleteAllQueryInternal_Deferred_NoChildren(Class referenceClass, Expression selectionExpression) {
        deleteAllQueryInternal(referenceClass, selectionExpression, true, false);
    }
    
    protected static void deleteAllQueryInternal_NonDeferred_NoChildren(Class referenceClass, Expression selectionExpression) {
        deleteAllQueryInternal(referenceClass, selectionExpression, false, false);
    }
    
    // referenceClass - the reference class of DeleteAllQuery to be tested
    // selectionExpression - selection expression of DeleteAllQuery to be tested
    // shouldDeferExecutionInUOW==true causes deferring query execution until uow.commit;
    // shouldDeferExecutionInUOW==false causes immediate query execution;
    // shouldHandleChildren==true means the test will be executed not only with the specified class,
    // but also with all its subclasses.
    // Each test will test DeleteAllQuery with the specified reference class
    // and all its subclasses
    // Example: for Vehicle.class  9 DeleteAllQueries will be tested.
    // shouldHandleChildren==false means the test will be executed with the specified class only.
    protected static void deleteAllQueryInternal(Class referenceClass, Expression selectionExpression, boolean shouldDeferExecutionInUOW, boolean handleChildren) {
        if (getServerSession().getDatasourcePlatform().isSymfoware()) {
            getServerSession().logMessage("DeleteAllQueryInheritanceJunitTest test skipped for this platform, "
                                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        String errorMsg = DeleteAllQueryTestHelper.execute(getDbSession(), referenceClass, selectionExpression, shouldDeferExecutionInUOW, handleChildren);
        if(errorMsg != null) {
            fail(errorMsg);
        }
    }
}
