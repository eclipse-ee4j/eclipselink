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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public class MultiClassEmployee implements Employee {
	private int id;
	private String name;
	private float salary;
	private String position;
	private Set comments;
	private Address address;
	private Collection dependents;
	private List cars;
	private Map phoneNumbers;	// keyed by description
	private Collection underlings;
	private List vacationBackups;
	private Map eatingPartners;	// keyed by meal


	public MultiClassEmployee(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.salary = 0;
		this.position = "";
		this.comments = new HashSet();
		this.dependents = new ArrayList();
		this.cars = new ArrayList();
		this.phoneNumbers = new HashMap();
		this.underlings = new ArrayList();
		this.vacationBackups = new ArrayList();
		this.eatingPartners = new HashMap();
	}

	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String string) {
		this.name = string;
	}

	public float getSalary() {
		return this.salary;
	}
	public void setSalary(float f) {
		this.salary = f;
	}

	public String getPosition() {
		return this.position;
	}
	public void setPosition(String string) {
		this.position = string;
	}

	public Iterator comments() {
		return this.comments.iterator();
	}
	public void addComment(String comment) {
		this.comments.add(comment);
	}
	public void clearComments() {
		this.comments.clear();
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
	public Dependent dependentNamed(String depName) {
		for (Iterator stream = this.dependents(); stream.hasNext(); ) {
			Dependent dependent = (Dependent) stream.next();
			if (dependent.getName().equals(depName)) {
				return dependent;
			}
		}
		throw new IllegalArgumentException("dependent not found: " + depName);
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
	public Car carNamed(String carName) {
		for (Iterator stream = this.cars(); stream.hasNext(); ) {
			Car car = (Car) stream.next();
			if (car.getName().equals(carName)) {
				return car;
			}
		}
		throw new IllegalArgumentException("car not found: " + carName);
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
	public Employee underlingNamed(String underlingName) {
		for (Iterator stream = this.underlings(); stream.hasNext(); ) {
			Employee underling = (Employee) stream.next();
			if (underling.getName().equals(underlingName)) {
				return underling;
			}
		}
		throw new IllegalArgumentException("underling not found: " + underlingName);
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
	public Employee vacationBackupNamed(String vacationBackupName) {
		for (Iterator stream = this.vacationBackups(); stream.hasNext(); ) {
			Employee vacationBackup = (Employee) stream.next();
			if (vacationBackup.getName().equals(vacationBackupName)) {
				return vacationBackup;
			}
		}
		throw new IllegalArgumentException("vacation backup not found: " + vacationBackupName);
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

	public String toString() {
		return StringTools.buildToStringFor(this, this.name);
	}

}
