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
 *     Vikram Bhatia - initial API and implementation.
 *     David Minsky - tweaks and comments
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.writing;

import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseSession;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

// MODEL SYSTEMS USED IN THIS TESTCASE FOR COMMIT ORDER TESTING 
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.models.ownership.OwnershipSystem;

/**
 * This test compares a hard-coded commit order to a generated commit order from CommitManager.
 * If mapped classes are added into the models above, the hard-coded commit order is no longer
 * valid, and they need to be added into the expected commit order.
 */
public class CommitOrderTest extends TestCase {
    
    private static final String expectedCommitOrder = initializeExpectedCommitOrder();
    
    private String orderAResults = null;
    private String orderBResults = null;
    private String orderCResults = null;
    private String orderDResults = null;

    public CommitOrderTest() {
        super();
        setDescription("Test to verify that the generated commit order is consistent with an expected commit order.");
    }

    public void test() {
        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new EmployeeSystem().addDescriptors(getDatabaseSession());
        orderAResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InsuranceSystem().addDescriptors(getDatabaseSession());
        orderBResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new OwnershipSystem().addDescriptors(getDatabaseSession());
        orderCResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InheritanceSystem().addDescriptors(getDatabaseSession());
        orderDResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());
    }
    
    public void verify() {
        if (!expectedCommitOrder.equals(orderAResults)) {
            throw new TestErrorException(getErrorMessage(orderAResults));
        }
        if (!expectedCommitOrder.equals(orderBResults)) {
            throw new TestErrorException(getErrorMessage(orderBResults));
        }
        if (!expectedCommitOrder.equals(orderCResults)) {
            throw new TestErrorException(getErrorMessage(orderCResults));
        }
        if (!expectedCommitOrder.equals(orderDResults)) {
            throw new TestErrorException(getErrorMessage(orderDResults));
        }
    }

    public void reset() {
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        orderAResults = null;
        orderBResults = null;
        orderCResults = null;
        orderDResults = null;
    }

    protected String getErrorMessage(String resultList) {
        StringBuffer sb = new StringBuffer();
        String cr = Helper.cr();
        sb.append("Error: An unexpected commit order was found.");
        sb.append(cr);
        sb.append("A possible cause of this problem may be additional mapped classes were added to test models used by the testcase ");
        sb.append(getClass().getName());
        sb.append(cr);
        sb.append("Mapped classes added to these model(s) should be added into the expected commit order within this testcase.");
        sb.append(cr);
        sb.append("[Found commit order] : ");
        sb.append(cr);
        sb.append(cr);
        sb.append(resultList);
        sb.append(cr);
        sb.append("[Expected commit order] : ");
        sb.append(cr);
        sb.append(cr);
        sb.append(expectedCommitOrder);
        return sb.toString();
    }

    protected String getCommitOrderListAsString(List<Class> classes) {
        StringBuffer sb = new StringBuffer();
        String cr = Helper.cr();
        for (Class clazz : classes) {
            sb.append(clazz.getName());
            sb.append(cr);
        }
        return sb.toString();
    }
    
    protected static String initializeExpectedCommitOrder() {
        StringBuffer sb = new StringBuffer();
        String cr = Helper.cr();
        sb.append("org.eclipse.persistence.testing.models.aggregate.Address" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Address1" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.AddressDescription" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Agent" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Cousin" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Aggregate" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Bicycle" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Builder" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Car" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Parent" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Child" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Client" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Company" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Computer" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Customer" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Dependant" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Employee" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Employee1" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.EvaluationClient" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Manufacturer" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.GolfClub" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.GolfClubShaft" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.HomeAddress" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.House" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Job" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Language" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Oid" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Period" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.PeriodDescription" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.ProjectDescription" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Relative" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Responsibility" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SellingPoint" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SingleHouse" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.StepChild" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Switch" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchState" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchStateOFF" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchStateON" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.TownHouse" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Transport" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Vehicle" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Version" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Worker" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.WorkingAddress" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.Guardian" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.MailingAddress" + cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.Student" + cr);
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadAggregate" + cr);
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadReferenceData" + cr);
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadObject" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Diner" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Location" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Restaurant" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Menu" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.MenuItem" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Person" + cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Waiter" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Address" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Employee" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Child" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.LargeProject" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.PhoneNumber" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Project" + cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.SmallProject" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.AboutToInsertMultiTableObject" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.AboutToInsertSingleTableObject" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.Address" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.CreditCard" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.EmailAccount" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.Phone" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.Customer" + cr);
        sb.append("org.eclipse.persistence.testing.models.events.Order" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_1_King2" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_2_1_King2" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_2_King2" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_King2" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Animal_Matt" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Human" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.JavaProgrammer" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mammal" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Primate" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Programmer" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Alligator" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Animal" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.BudgettedProject" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.ProjectWorker" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.BaseProject" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Company" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Bicycle" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Boat" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Car" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SportsCar" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SalesRep" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SoftwareEngineer" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Engineer" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Person" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Bus" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Cat" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.CompanyWorker" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Computer" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Developer_King" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Dog" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Entomologist" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.FueledVehicle" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.GrassHopper" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.IBMPC" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Insect" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.LabradorRetriever" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.LadyBug" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mac" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mainframe" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.NonFueledVehicle" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.PC" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Person_King" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SeniorDeveloper_King" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Vehicle" + cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Worker" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.PolicyHolder" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Address" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.BicyclePolicy" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Policy" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HealthPolicy" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HousePolicy" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.VehiclePolicy" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Claim" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HealthClaim" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HouseClaim" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Phone" + cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.VehicleClaim" + cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Employee" + cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Computer" + cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.GaurenteedShipment" + cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.InsuredShipment" + cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Shipment" + cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Order" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Address" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.BabyMonitor" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Crib" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Baby" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.CompanyCard" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Cubicle" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Monitor" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Computer" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.EmergencyExit" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee1" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee2" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Hardware" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Identification" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Keyboard" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Joystick" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Key" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Peripheral" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Phone" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.SecureSystem" + cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Shipment" + cr);
        sb.append("org.eclipse.persistence.testing.models.multipletable.Budget" + cr);
        sb.append("org.eclipse.persistence.testing.models.multipletable.BusinessProject" + cr);
        sb.append("org.eclipse.persistence.testing.models.multipletable.LargeBusinessProject" + cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectB" + cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectA" + cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectD" + cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectC" + cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectE" + cr);
        sb.append("org.eclipse.persistence.testing.models.sequencing.SeqTestClass1" + cr);
        sb.append("org.eclipse.persistence.testing.models.sequencing.SeqTestClass2" + cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.EngineType" + cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.FuelType" + cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.SportsCar" + cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.CarOwner" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.TestClass1" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.options.QueryOptionHistory" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.options.QueryOptionEmployee" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Brewer" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Beer" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Person" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Bar" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.History" + cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.ReportEmployee" + cr);
        return sb.toString();
    }
}
