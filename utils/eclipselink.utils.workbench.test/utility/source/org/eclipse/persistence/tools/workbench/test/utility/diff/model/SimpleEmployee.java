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

import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public class SimpleEmployee implements Employee {
	private int id;
	private String name;
	private float salary;
	private String position;
	private Set comments;

	public SimpleEmployee(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.salary = 0;
		this.position = "";
		this.comments = new HashSet();
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

	public String toString() {
		return StringTools.buildToStringFor(this, this.name);
	}


	// ********** unsupported stuff **********

	public Address getAddress() {
		throw new UnsupportedOperationException();
	}
	public void setAddress(Address address) {
		throw new UnsupportedOperationException();
	}

	public Iterator dependents() {
		throw new UnsupportedOperationException();
	}
	public Dependent addDependent(String depName, String depDescription) {
		throw new UnsupportedOperationException();
	}
	public void clearDependents() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}
	public Car addCar(String carName, String carDescription) {
		throw new UnsupportedOperationException();
	}
	public void clearCars() {
		throw new UnsupportedOperationException();
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

	public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number, String extension) {
		throw new UnsupportedOperationException();
	}
	public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number) {
		throw new UnsupportedOperationException();
	}
	public void clearPhoneNumbers() {
		throw new UnsupportedOperationException();
	}
	public PhoneNumber getPhoneNumber(String phoneDescription) {
		throw new UnsupportedOperationException();
	}
	public Iterator phoneNumbers() {
		throw new UnsupportedOperationException();
	}

	public Iterator underlings() {
		throw new UnsupportedOperationException();
	}
	public void addUnderling(Employee underling) {
		throw new UnsupportedOperationException();
	}
	public void clearUnderlings() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}
	public void addVacationBackup(Employee vacationBackup) {
		throw new UnsupportedOperationException();
	}
	public void clearVacationBackups() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}
	public void setEatingPartner(String meal, Employee partner) {
		throw new UnsupportedOperationException();
	}
	public void clearEatingPartners() {
		throw new UnsupportedOperationException();
	}
	public Employee getEatingPartner(String meal) {
		throw new UnsupportedOperationException();
	}

}
