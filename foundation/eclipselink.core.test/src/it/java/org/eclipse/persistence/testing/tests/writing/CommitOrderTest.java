/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Vikram Bhatia - initial API and implementation.
//     David Minsky - tweaks and comments
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.models.ownership.OwnershipSystem;

import java.util.HashMap;
import java.util.List;

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

    @Override
    public void test() {
        getSession().getProject().setDescriptors(new HashMap<>());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new EmployeeSystem().addDescriptors(getDatabaseSession());
        orderAResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getProject().setDescriptors(new HashMap<>());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InsuranceSystem().addDescriptors(getDatabaseSession());
        orderBResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getProject().setDescriptors(new HashMap<>());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new OwnershipSystem().addDescriptors(getDatabaseSession());
        orderCResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getProject().setDescriptors(new HashMap<>());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InheritanceSystem().addDescriptors(getDatabaseSession());
        orderDResults = getCommitOrderListAsString(getAbstractSession().getCommitManager().getCommitOrder());
    }

    @Override
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

    @Override
    public void reset() {
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        orderAResults = null;
        orderBResults = null;
        orderCResults = null;
        orderDResults = null;
    }

    protected String getErrorMessage(String resultList) {
        StringBuilder sb = new StringBuilder();
        String cr = System.lineSeparator();
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

    protected String getCommitOrderListAsString(List<Class<?>> classes) {
        StringBuilder sb = new StringBuilder();
        String cr = System.lineSeparator();
        for (Class<?> clazz : classes) {
            sb.append(clazz.getName());
            sb.append(cr);
        }
        return sb.toString();
    }

    protected static String initializeExpectedCommitOrder() {
        StringBuilder sb = new StringBuilder();
        String cr = System.lineSeparator();
        sb.append("org.eclipse.persistence.testing.models.aggregate.Address").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Address1").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.AddressDescription").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Agent").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Cousin").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Aggregate").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Bicycle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Builder").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Car").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Parent").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Child").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Client").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Company").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Computer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Customer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Dependant").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Employee").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Employee1").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.EvaluationClient").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Manufacturer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.GolfClub").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.GolfClubShaft").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.HomeAddress").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.House").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Job").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Language").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Oid").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Period").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.PeriodDescription").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.ProjectDescription").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Relative").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Responsibility").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SellingPoint").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SingleHouse").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.StepChild").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Switch").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchState").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchStateOFF").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchStateON").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.TownHouse").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Transport").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Vehicle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Version").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.Worker").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.WorkingAddress").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.Guardian").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.MailingAddress").append(cr);
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.Student").append(cr);
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadAggregate").append(cr);
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadReferenceData").append(cr);
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadObject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Diner").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Location").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Restaurant").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Menu").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.MenuItem").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Person").append(cr);
        sb.append("org.eclipse.persistence.testing.models.collections.Waiter").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Address").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Employee").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Child").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.LargeProject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.PhoneNumber").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Project").append(cr);
        sb.append("org.eclipse.persistence.testing.models.employee.domain.SmallProject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.AboutToInsertMultiTableObject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.AboutToInsertSingleTableObject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.Address").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.CreditCard").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.EmailAccount").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.Phone").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.Customer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.events.Order").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_1_King2").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_2_1_King2").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_2_King2").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_King2").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Animal_Matt").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Human").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.JavaProgrammer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mammal").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Primate").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Programmer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Alligator").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Animal").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Teacher").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Apple").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.BudgettedProject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.ProjectWorker").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.BaseProject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Company").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Bicycle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Boat").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Car").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SportsCar").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SalesRep").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SoftwareEngineer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Engineer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Person").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Bus").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Cat").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.CompanyWorker").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Computer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Developer_King").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Dog").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Entomologist").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Fruit").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.FueledVehicle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.GrassHopper").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.IBMPC").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Insect").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.LabradorRetriever").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.LadyBug").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mac").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mainframe").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.NonFueledVehicle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.PC").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Pear").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Person_King").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.SeniorDeveloper_King").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Vehicle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.inheritance.Worker").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.PolicyHolder").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Address").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.BicyclePolicy").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Policy").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HealthPolicy").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HousePolicy").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.VehiclePolicy").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Claim").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HealthClaim").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.HouseClaim").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.Phone").append(cr);
        sb.append("org.eclipse.persistence.testing.models.insurance.VehicleClaim").append(cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Employee").append(cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Computer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.GaurenteedShipment").append(cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.InsuredShipment").append(cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Shipment").append(cr);
        sb.append("org.eclipse.persistence.testing.models.legacy.Order").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Address").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.BabyMonitor").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Crib").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Baby").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.CompanyCard").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Cubicle").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Monitor").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Computer").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.EmergencyExit").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee1").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee2").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Hardware").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Identification").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Keyboard").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Joystick").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Key").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Peripheral").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Phone").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.SecureSystem").append(cr);
        sb.append("org.eclipse.persistence.testing.models.mapping.Shipment").append(cr);
        sb.append("org.eclipse.persistence.testing.models.multipletable.Budget").append(cr);
        sb.append("org.eclipse.persistence.testing.models.multipletable.BusinessProject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.multipletable.LargeBusinessProject").append(cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectB").append(cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectA").append(cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectD").append(cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectC").append(cr);
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectE").append(cr);
        sb.append("org.eclipse.persistence.testing.models.sequencing.SeqTestClass1").append(cr);
        sb.append("org.eclipse.persistence.testing.models.sequencing.SeqTestClass2").append(cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.EngineType").append(cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.FuelType").append(cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.SportsCar").append(cr);
        sb.append("org.eclipse.persistence.testing.models.vehicle.CarOwner").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.TestClass1").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.options.QueryOptionHistory").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.options.QueryOptionEmployee").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Brewer").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Beer").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Person").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Bar").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.History").append(cr);
        sb.append("org.eclipse.persistence.testing.tests.queries.report.ReportEmployee").append(cr);
        return sb.toString();
    }
}
