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
 *     Vikram Bhatia - initial API and implementation. 
 ******************************************************************************/  

package org.eclipse.persistence.testing.tests.writing;

import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.models.ownership.OwnershipSystem;

public class CommitOrderTest extends AutoVerifyTestCase {
    
    private static final String expectedResult = initializeExpectedResult();
    
    private String orderA = null;
    private String orderB = null;
    private String orderC = null;
    private String orderD = null;

    public CommitOrderTest() {
        setDescription("Check that the commit order is consistent.");
    }

    public String getErrorMessage(String resultList) {
        StringBuffer sb = new StringBuffer();
        sb.append("Incorrect Commit Oder Found.\n");
        sb.append("[Found] : ");
        sb.append(resultList);
        sb.append("\n[Expected] : ");
        sb.append(expectedResult);
        return sb.toString();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        orderA = null;
        orderB = null;
        orderC = null;
        orderD = null;
    }

    public void setup() {
//        getSession().setLogLevel(SessionLog.FINE);
    }

    public void test() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new EmployeeSystem().addDescriptors(getDatabaseSession());
        orderA = getResult(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InsuranceSystem().addDescriptors(getDatabaseSession());
        orderB = getResult(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new OwnershipSystem().addDescriptors(getDatabaseSession());
        orderC = getResult(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new HashMap());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InheritanceSystem().addDescriptors(getDatabaseSession());
        orderD = getResult(getAbstractSession().getCommitManager().getCommitOrder());
    }
    
    protected void verify() {
        if (!expectedResult.equals(orderA)) {
            throw new TestErrorException(getErrorMessage(orderA));
        }
        
        if (!expectedResult.equals(orderB)) {
            throw new TestErrorException(getErrorMessage(orderB));
        }
        
        if (!expectedResult.equals(orderC)) {
            throw new TestErrorException(getErrorMessage(orderC));
        }
        
        if (!expectedResult.equals(orderD)) {
            throw new TestErrorException(getErrorMessage(orderD));
        }
    }

    private String getResult(List<Class> resultList) {
        StringBuffer sb = new StringBuffer();
        
        for(Class cls : resultList) {
            sb.append(cls.getName());
            sb.append(", ");
        }
        
        return sb.toString();
    }
    
    private static String initializeExpectedResult() {
        StringBuffer sb = new StringBuffer();
        sb.append("org.eclipse.persistence.testing.models.aggregate.Address, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Address1, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.AddressDescription, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Agent, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Bicycle, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Builder, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Car, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Client, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Company, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Computer, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Customer, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Dependant, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Employee, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Employee1, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.EvaluationClient, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Manufacturer, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.GolfClub, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.GolfClubShaft, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.HomeAddress, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.House, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Job, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Language, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Oid, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Period, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.PeriodDescription, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.ProjectDescription, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Responsibility, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.SellingPoint, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.SingleHouse, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Switch, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchState, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchStateOFF, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.SwitchStateON, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.TownHouse, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Transport, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Vehicle, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Version, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.Worker, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.WorkingAddress, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.Guardian, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.MailingAddress, ");
        sb.append("org.eclipse.persistence.testing.models.aggregate.nested.Student, ");
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadAggregate, ");
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadReferenceData, ");
        sb.append("org.eclipse.persistence.testing.models.bigbad.BigBadObject, ");
        sb.append("org.eclipse.persistence.testing.models.collections.Diner, ");
        sb.append("org.eclipse.persistence.testing.models.collections.Location, ");
        sb.append("org.eclipse.persistence.testing.models.collections.Restaurant, ");
        sb.append("org.eclipse.persistence.testing.models.collections.Menu, ");
        sb.append("org.eclipse.persistence.testing.models.collections.MenuItem, ");
        sb.append("org.eclipse.persistence.testing.models.collections.Person, ");
        sb.append("org.eclipse.persistence.testing.models.collections.Waiter, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Address, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Employee, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Child, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.LargeProject, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.PhoneNumber, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.Project, ");
        sb.append("org.eclipse.persistence.testing.models.employee.domain.SmallProject, ");
        sb.append("org.eclipse.persistence.testing.models.events.AboutToInsertMultiTableObject, ");
        sb.append("org.eclipse.persistence.testing.models.events.AboutToInsertSingleTableObject, ");
        sb.append("org.eclipse.persistence.testing.models.events.Address, ");
        sb.append("org.eclipse.persistence.testing.models.events.CreditCard, ");
        sb.append("org.eclipse.persistence.testing.models.events.EmailAccount, ");
        sb.append("org.eclipse.persistence.testing.models.events.Phone, ");
        sb.append("org.eclipse.persistence.testing.models.events.Customer, ");
        sb.append("org.eclipse.persistence.testing.models.events.Order, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_1_King2, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_2_1_King2, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_2_King2, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.A_King2, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Animal_Matt, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Human, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.JavaProgrammer, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mammal, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Primate, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Programmer, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Alligator, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Animal, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.BudgettedProject, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.ProjectWorker, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.BaseProject, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Company, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Bicycle, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Boat, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Car, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.SportsCar, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.SalesRep, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.SoftwareEngineer, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Engineer, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Person, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Bus, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Cat, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.CompanyWorker, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Computer, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Developer_King, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Dog, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Entomologist, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.FueledVehicle, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.GrassHopper, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.IBMPC, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Insect, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.LabradorRetriever, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.LadyBug, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mac, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Mainframe, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.NonFueledVehicle, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.PC, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Person_King, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.SeniorDeveloper_King, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Vehicle, ");
        sb.append("org.eclipse.persistence.testing.models.inheritance.Worker, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.PolicyHolder, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.Address, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.Policy, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.HealthPolicy, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.HousePolicy, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.VehiclePolicy, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.Claim, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.HealthClaim, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.HouseClaim, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.Phone, ");
        sb.append("org.eclipse.persistence.testing.models.insurance.VehicleClaim, ");
        sb.append("org.eclipse.persistence.testing.models.legacy.Employee, ");
        sb.append("org.eclipse.persistence.testing.models.legacy.Computer, ");
        sb.append("org.eclipse.persistence.testing.models.legacy.GaurenteedShipment, ");
        sb.append("org.eclipse.persistence.testing.models.legacy.InsuredShipment, ");
        sb.append("org.eclipse.persistence.testing.models.legacy.Shipment, ");
        sb.append("org.eclipse.persistence.testing.models.legacy.Order, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Address, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.BabyMonitor, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Crib, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Baby, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.CompanyCard, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Cubicle, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Monitor, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Computer, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.EmergencyExit, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee1, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Employee2, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Hardware, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Identification, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Keyboard, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Joystick, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Key, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Phone, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.SecureSystem, ");
        sb.append("org.eclipse.persistence.testing.models.mapping.Shipment, ");
        sb.append("org.eclipse.persistence.testing.models.multipletable.Budget, ");
        sb.append("org.eclipse.persistence.testing.models.multipletable.BusinessProject, ");
        sb.append("org.eclipse.persistence.testing.models.multipletable.LargeBusinessProject, ");
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectB, ");
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectA, ");
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectD, ");
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectC, ");
        sb.append("org.eclipse.persistence.testing.models.ownership.ObjectE, ");
        sb.append("org.eclipse.persistence.testing.models.sequencing.SeqTestClass1, ");
        sb.append("org.eclipse.persistence.testing.models.sequencing.SeqTestClass2, ");
        sb.append("org.eclipse.persistence.testing.models.vehicle.EngineType, ");
        sb.append("org.eclipse.persistence.testing.models.vehicle.FuelType, ");
        sb.append("org.eclipse.persistence.testing.models.vehicle.SportsCar, ");
        sb.append("org.eclipse.persistence.testing.models.vehicle.CarOwner, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.TestClass1, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.options.QueryOptionHistory, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.options.QueryOptionEmployee, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Brewer, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Beer, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Person, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.report.Bar, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.report.History, ");
        sb.append("org.eclipse.persistence.testing.tests.queries.report.ReportEmployee, ");
        return sb.toString();
    }
}
