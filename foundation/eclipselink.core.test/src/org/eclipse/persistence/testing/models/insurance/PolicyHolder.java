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
package org.eclipse.persistence.testing.models.insurance;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * <p><b>Purpose</b>: Represents an insurance policy holder.
 * <p><b>Description</b>: Root object that holder an Address and has a 1-M to Policy.
 * @see Policy
 * @since TOPLink/Java 1.0
 */
public class PolicyHolder implements Serializable {
    private String firstName;
    private String lastName;
    private String sex;
    private long ssn;
    private java.sql.Date birthDate;
    private String occupation;
    private Address address;
    private Vector policies;
    private Vector childrenNames;

    // a collection of phones being stored at Oracle8i as VArray type
    private Vector phones;

    public PolicyHolder() {
        this.firstName = "";
        this.lastName = "";
        this.occupation = "";
        this.sex = "Male";
        this.policies = new Vector(3);
        this.childrenNames = new Vector(2);
        this.phones = new Vector(4);
    }

    public void addChildName(String name) {
        getChildrenNames().addElement(name);
    }

    public void addPhone(Phone phone) {
        getPhones().addElement(phone);
    }

    /**
     * Add the policy.
     * Note that it is important to maintain bi-directional relationships both ways when adding.
     */
    public Policy addPolicy(Policy policy) {
        getPolicies().addElement(policy);
        policy.setPolicyHolder(this);
        return policy;
    }

    /**
     * Return an example policy holder instance.
     */
    public static PolicyHolder example1() {
        PolicyHolder holder = new PolicyHolder();

        holder.setFirstName("Bob");
        holder.setLastName("Smith");
        holder.addChildName("Bobby");
        holder.addChildName("Bessy-Sue");
        holder.addChildName("Bessy-Ray");
        holder.setMale();
        holder.setSsn(1111);
        holder.setBirthDate(Helper.dateFromString("1950/02/30"));
        holder.setOccupation("Engineer");

        holder.setAddress(Address.example1());
        holder.addPolicy(HealthPolicy.example1());

        holder.addPhone(Phone.example1());
        holder.addPhone(Phone.example2());

        return holder;
    }

    /**
     * Return an example employee instance.
     */
    public static PolicyHolder example2() {
        PolicyHolder holder = new PolicyHolder();

        holder.setFirstName("Jill");
        holder.setLastName("May");
        holder.setFemale();
        holder.setSsn(2222);
        holder.setBirthDate(Helper.dateFromString("1960/02/15"));
        holder.setOccupation("Diving");

        holder.setAddress(Address.example2());
        holder.addPolicy(HousePolicy.example1());
        holder.addPolicy(VehiclePolicy.example2());

        holder.addPhone(Phone.example3());
        holder.addPhone(Phone.example4());

        return holder;
    }

    /**
     * Return an example employee instance.
     */
    public static PolicyHolder example3() {
        PolicyHolder holder = new PolicyHolder();

        holder.setFirstName("Sarah");
        holder.setLastName("Way");
        holder.setFemale();
        holder.setSsn(3333);
        holder.setBirthDate(Helper.dateFromString("1977/03/03"));
        holder.setOccupation("Student");

        holder.setAddress(Address.example3());
        holder.addPolicy(HousePolicy.example2());

        return holder;
    }

    /**
     * Return an example employee instance.
     */
    public static PolicyHolder example4() {
        PolicyHolder holder = new PolicyHolder();

        holder.setFirstName("Sarah-loo");
        holder.setLastName("Smitty");
        holder.addChildName("Gene");
        holder.addChildName("Jen");
        holder.addChildName("Jess");
        holder.addChildName("Jean");
        holder.setFemale();
        holder.setSsn(4444);
        holder.setBirthDate(Helper.dateFromString("1919/09/09"));
        holder.setOccupation("Unemployed");

        holder.setAddress(Address.example1());
        holder.addPolicy(VehiclePolicy.example1());

        return holder;
    }

    /**
     * Return an example employee instance.
     */
    public static PolicyHolder example5() {
        PolicyHolder holder = new PolicyHolder();

        holder.setFirstName("Shi");
        holder.setLastName("Shu");
        holder.addChildName("tai");
        holder.addChildName("lin");
        holder.addChildName("ching");
        holder.setFemale();
        holder.setSsn(5555);
        holder.setBirthDate(Helper.dateFromString("1910/09/09"));
        holder.setOccupation("Unemployed");

        holder.setAddress(Address.example1());
        holder.addPolicy(VehiclePolicy.example3());

        return holder;
    }

    public Address getAddress() {
        return address;
    }

    public java.sql.Date getBirthDate() {
        return birthDate;
    }

    public Vector getChildrenNames() {
        return childrenNames;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public Vector getPhones() {
        return phones;
    }

    public Vector getPolicies() {
        return policies;
    }

    public String getSex() {
        return sex;
    }

    public long getSsn() {
        return ssn;
    }

    /**
     * This is required for TopLink only to primitively set the address without side effects.
     * If method access is used without indirection the set method given to TopLink must not have any side-effects,
     * such as setting the address's policy holder.  This could cause the unit of work problems.
     */
    public void internalSetAddress(Address address) {
        this.address = address;
    }

    public boolean isFemale() {
        return getSex().equals("Female");
    }

    public boolean isMale() {
        return getSex().equals("Male");
    }

    public void removePhone(Phone device) {
        getPhones().removeElement(device);
    }

    /**
     * Remove the policy.
     * Note that it is important to maintain bi-directional relationships both ways when removing.
     */
    public Policy removePolicy(Policy policy) {
        getPolicies().removeElement(policy);
        policy.setPolicyHolder(null);
        return policy;
    }

    /**
     * Set the address.
     * Note that it is important to maintain bi-directional relationships both ways.
     */
    public void setAddress(Address address) {
        this.address = address;
        if (address != null) {
            address.setPolicyHolder(this);
        }
    }

    public void setBirthDate(java.sql.Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setChildrenNames(Vector childrenNames) {
        this.childrenNames = childrenNames;
    }

    public void setFemale() {
        setSex("Female");
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMale() {
        setSex("Male");
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setPhones(Vector phones) {
        this.phones = phones;
    }

    public void setPolicies(Vector policies) {
        this.policies = policies;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSsn(long ssn) {
        this.ssn = ssn;
    }

    public String toString() {
        return "PolicyHolder: " + getFirstName() + " " + getLastName();
    }
}
