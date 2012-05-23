/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.utility.diff.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class InheritanceEmployee extends SimpleEmployee {
	private Address address;
	private Collection dependents;
	private List cars;
	private Map phoneNumbers;	// keyed by description
	private Collection underlings;
	private List vacationBackups;
	private Map eatingPartners;	// keyed by meal


	public InheritanceEmployee(int id, String name) {
		super(id, name);
		this.dependents = new ArrayList();
		this.cars = new ArrayList();
		this.phoneNumbers = new HashMap();
		this.underlings = new ArrayList();
		this.vacationBackups = new ArrayList();
		this.eatingPartners = new HashMap();
	}

	public Address getAddress() {
		return this.address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Iterator dependents() {
		return this.dependents.iterator();
	}
	public Dependent addDependent(String depName, String depDescription) {
		Dependent dependent = new Dependent(depName, depDescription);
		this.dependents.add(dependent);
		return dependent;
	}
	public void clearDependents() {
		this.dependents.clear();
	}

	public ListIterator cars() {
		return this.cars.listIterator();
	}
	public Car addCar(String carName, String carDescription) {
		Car car = new Car(carName, carDescription);
		this.cars.add(car);
		return car;
	}
	public void clearCars() {
		this.cars.clear();
	}

	public Iterator phoneNumbers() {
		return this.phoneNumbers.entrySet().iterator();
	}
	public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number, String extension) {
		PhoneNumber phone = new PhoneNumber(areaCode, exchange, number, extension);
		this.phoneNumbers.put(phoneDescription, phone);
		return phone;
	}
	public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number) {
		PhoneNumber phone = new PhoneNumber(areaCode, exchange, number);
		this.phoneNumbers.put(phoneDescription, phone);
		return phone;
	}
	public void clearPhoneNumbers() {
		this.phoneNumbers.clear();
	}
	public PhoneNumber getPhoneNumber(String phoneDescription) {
		return (PhoneNumber) this.phoneNumbers.get(phoneDescription);
	}

	public Iterator underlings() {
		return this.underlings.iterator();
	}
	public void addUnderling(Employee underling) {
		this.underlings.add(underling);
	}
	public void clearUnderlings() {
		this.underlings.clear();
	}

	public Iterator vacationBackups() {
		return this.vacationBackups.iterator();
	}
	public void addVacationBackup(Employee vacationBackup) {
		this.vacationBackups.add(vacationBackup);
	}
	public void clearVacationBackups() {
		this.vacationBackups.clear();
	}

	public Iterator eatingPartners() {
		return this.eatingPartners.entrySet().iterator();
	}
	public void setEatingPartner(String meal, Employee partner) {
		this.eatingPartners.put(meal, partner);
	}
	public void clearEatingPartners() {
		this.eatingPartners.clear();
	}
	public Employee getEatingPartner(String meal) {
		return (Employee) this.eatingPartners.get(meal);
	}

}
