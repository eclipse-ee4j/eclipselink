/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.testing.framework.wdf.customizer.AdjustArrayTypeCustomizer;

@Cacheable(true)
@Entity
@Table(name = "TMP_EMP", uniqueConstraints = @UniqueConstraint(columnNames = { "ID", "DEPARTMENT" }))
@NamedQuery(name = "Employee.getEmployeesAndHobbies", query = "select e, h from Employee e left join e.hobbies h")
@NamedNativeQueries( {
        @NamedNativeQuery(name = "Employee.schlonz", query = "select \"TMP_EMP\".*  from \"TMP_EMP\"", resultClass = Employee.class),
        @NamedNativeQuery(name = "Employee.schlonzHint", query = "select \"TMP_EMP\".*  from \"TMP_EMP\"", resultClass = Employee.class) })
@Customizer(AdjustArrayTypeCustomizer.class)        
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    protected int id;

    @Basic
    protected String firstname;

    @Basic
    protected String lastname;

    // <attribute name="department">
    // <many-to-one
    // target-entity="com.sap.jpa.example.Department"/>
    // <join-column name="DEPARTMENT"/>
    // </attribute>
    @ManyToOne
    @JoinColumn(name = "DEPARTMENT")
    protected Department department;

    @Basic
    protected BigDecimal salary;

    // <attribute name="reviews">
    // <one-to-many
    // target-entity="com.sap.jpa.example.Review"/>
    // <join-table name="TMP_EMP_REVIEW">
    // <join-column name="EMP_ID"/>
    // <inverse-join-column name="REVIEW_ID"/>
    // </join-table>
    // </attribute>
    @OneToMany
    @JoinTable(name = "TMP_EMP_REVIEW", joinColumns = { @JoinColumn(name = "EMP_ID") }, inverseJoinColumns = { @JoinColumn(name = "REVIEW_ID") })
    protected Set<Review> reviews;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "riders", fetch = FetchType.EAGER)
    protected Set<Bicycle> bicycles;

    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "startDate", column = @Column(name = "EMP_START")), @AttributeOverride(name = "endDate", column = @Column(name = "EMP_END")) })
    protected EmploymentPeriod period;

    // <attribute name="cubicle">
    // <one-to-one
    // target-entity="com.sap.jpa.example.Cubicle">
    // </one-to-one>
    // <join-column name="CUBICLE_FLOOR" referenced-column-name="FLOOR"/>
    // <join-column name="CUBICLE_PLACE" referenced-column-name="PLACE"/>
    // </attribute>
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns( { @JoinColumn(name = "CUBICLE_FLOOR", referencedColumnName = "FLOOR"),
            @JoinColumn(name = "CUBICLE_PLACE", referencedColumnName = "PLACE") })
    protected Cubicle cubicle;

    @Transient
    private boolean postUpdateCalled;

    @Transient
    private boolean postPersistCalled;

    // <attribute name="patents">
    // <many-to-many
    // target-entity="com.sap.jpa.example.Patent"/>
    // <join-table name="TMP_EMP_PATENT">
    // <join-column name="EMP_ID"/>
    // <inverse-join-column name="PATENT_NAME"
    // referenced-column-name="PAT_NAME"/>
    // <!-- inverse-join-column name="PATENT_YEAR"
    // referenced-column-name="PAT_YEAR"/ -->
    // </join-table>
    // </attribute>
    @ManyToMany
    @JoinTable(name = "TMP_EMP_PATENT", joinColumns = { @JoinColumn(name = "EMP_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "PATENT_NAME", referencedColumnName = "PAT_NAME"),
            @JoinColumn(name = "PATENT_YEAR", referencedColumnName = "PAT_YEAR") })
    protected Set<Patent> patents;

    // <attribute name="projects">
    // <many-to-many
    // target-entity="com.sap.jpa.example.Project"/>
    // <join-table name="TMP_EMP_PROJECT">
    // <join-column name="EMP_ID"/>
    // <inverse-join-column name="PROJECT_ID"/>
    // </join-table>
    // </attribute>
    @ManyToMany
    @JoinTable(name = "TMP_EMP_PROJECT", joinColumns = { @JoinColumn(name = "EMP_ID") }, inverseJoinColumns = { @JoinColumn(name = "PROJECT_ID") })
    protected Set<Project> projects;

    // <attribute name="travelProfile">
    // <one-to-one
    // target-entity="com.sap.jpa.example.TravelProfile"/>
    // <join-column name="PROFILE_GUID"/>
    // </attribute>
    @OneToOne
    @JoinColumn(name = "PROFILE_GUID", columnDefinition=TravelProfile.BINARY_16_COLUMN)
    protected TravelProfile travelProfile;

    // <attribute name="hobbies">
    // <many-to-many
    // target-entity="com.sap.jpa.example.Hobby"/>
    // <join-table name="TMP_EMP_HOBBY">
    // <join-column name="EMP_ID"/>
    // <inverse-join-column name="HOBBY_ID"/>
    // </join-table>
    // </attribute>
    @ManyToMany
    @JoinTable(name = "TMP_EMP_HOBBY", joinColumns = { @JoinColumn(name = "EMP_ID") }, inverseJoinColumns = { @JoinColumn(name = "HOBBY_ID") })
    @OrderBy("category ASC, description DESC")
    protected List<Hobby> hobbies;

    @OneToOne(cascade = { CascadeType.PERSIST }, mappedBy = "driver")
    protected MotorVehicle motorVehicle;

    @OneToOne
    @JoinColumn(name = "SAMPLE_ACCOUNT")
    protected Account sampleAccount;

    @OneToOne
    @JoinColumn(name = "BROKERAGE_ACCOUNT")
    protected BrokerageAccount brokerageAccount;

    @OneToOne(mappedBy = "client")
    protected CheckingAccount checkingAccount;

    @OneToMany
    @JoinTable(name = "TMP_EMP_CREDIT", joinColumns = { @JoinColumn(name = "CLIENT_ID") }, inverseJoinColumns = { @JoinColumn(name = "CREDIT_ID") })
    protected Set<CreditCardAccount> creditCardAccounts;

//    @OneToOne
//    @JoinColumn(name = "AUTOMOBILE")
    @Transient // EclipseLink has issue with cyclic FKs FIXME: file bug and add id here
    protected MotorVehicle automobile;

    @ManyToOne
    @JoinColumn(name = "COSTCENTER")
    protected CostCenter costCenter;

    public Employee() {
    }

    public Employee(int aId, String aFirst, String aLast, Department dep) {
        this(aId, aFirst, aLast, dep, null);
    }

    public Employee(int aId, String aFirst, String aLast, Department dep, BigDecimal aSalary) {
        id = aId;
        firstname = aFirst;
        lastname = aLast;
        department = dep;
        salary = aSalary;
    }

    public int getId() {
        return id;
    }

    public void setFirstName(String aFirst) {
        firstname = aFirst;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setLastName(String aLast) {
        lastname = aLast;
    }

    public String getLastName() {
        return lastname;
    }

    public void setDepartment(Department dep) {
        department = dep;
    }

    public Department getDepartment() {
        return department;
    }

    public Cubicle getCubicle() {
        return cubicle;
    }

    public void setCubicle(Cubicle aCubicle) {
        cubicle = aCubicle;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review rev) {
        if (reviews == null) {
            reviews = new HashSet<Review>();
        }
        reviews.add(rev);
    }

    public EmploymentPeriod getEmploymentPeriod() {
        return period;
    }

    public void setEmploymentPeriod(EmploymentPeriod aPeriod) {
        period = aPeriod;
    }

    public void clearPostUpdate() {
        postUpdateCalled = false;
    }

    public void clearPostPersist() {
        postPersistCalled = false;
    }

    @PostUpdate
    public void postUpdate() {
        postUpdateCalled = true;
    }

    @PostPersist
    public void postPersist() {
        postPersistCalled = true;
    }

    public boolean postUpdateWasCalled() {
        return postUpdateCalled;
    }

    public boolean postPersistWasCalled() {
        return postPersistCalled;
    }

    /**
     * @param reviews
     *            The reviews to set.
     */
    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    // @ManyToMany
    public Collection<Patent> getPatents() {
        return patents;
    }

    public void setPatents(Collection<Patent> patents) {
        this.patents = new HashSet<Patent>(patents);
    }

    public void addPatent(Patent patent) {
        if (patents == null) {
            patents = new HashSet<Patent>();
        }
        patents.add(patent);
    }

    /**
     * @return Returns the projects.
     */
    public Set<Project> getProjects() {
        return projects;
    }

    /**
     * @param projects
     *            The projects to set.
     */
    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return Returns the travelProfile.
     */
    public TravelProfile getTravelProfile() {
        return travelProfile;
    }

    /**
     * @param travelProfile
     *            The travelProfile to set.
     */
    public void setTravelProfile(TravelProfile travelProfile) {
        this.travelProfile = travelProfile;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public void addHobby(Hobby hobby) {
        if (hobbies == null) {
            hobbies = new ArrayList<Hobby>();
        }
        hobbies.add(hobby);
    }

    public MotorVehicle getMotorVehicle() {
        return motorVehicle;
    }

    public void setMotorVehicle(MotorVehicle motorVehicle) {
        this.motorVehicle = motorVehicle;
    }

    public Set<Bicycle> getBicycles() {
        return bicycles;
    }

    public void setBicycles(Set<Bicycle> bicycles) {
        this.bicycles = bicycles;
    }

    public void addBicycle(Bicycle bike) {
        if (bicycles == null) {
            bicycles = new HashSet<Bicycle>();
        }
        bicycles.add(bike);

        if (bike.getRiders() == null) {
            bike.setRiders(new HashSet<Employee>());
        }
        bike.getRiders().add(this);
    }

    public Account getSampleAccount() {
        return this.sampleAccount;
    }

    public void setSampleAccount(Account aAccount) {
        this.sampleAccount = aAccount;
    }

    public BrokerageAccount getBrokerageAccount() {
        return this.brokerageAccount;
    }

    public void setBrokerageAccount(BrokerageAccount aBrokerageAccount) {
        this.brokerageAccount = aBrokerageAccount;
    }

    public CheckingAccount getCheckingAccount() {
        return this.checkingAccount;
    }

    public void setCheckingAccount(CheckingAccount aCheckingAccount) {
        this.checkingAccount = aCheckingAccount;
    }

    public Set<CreditCardAccount> getCreditCardAccounts() {
        return this.creditCardAccounts;
    }

    public void addCreditCardAccount(CreditCardAccount cca) {
        if (this.creditCardAccounts == null) {
            this.creditCardAccounts = new HashSet<CreditCardAccount>();
        }
        this.creditCardAccounts.add(cca);
    }

    public MotorVehicle getAutomobile() {
        return automobile;
    }

    public void setAutomobile(MotorVehicle automobile) {
        this.automobile = automobile;
    }

    public CostCenter getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(CostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public static class KrassEmp extends Employee {
        private static final long serialVersionUID = 1L;
        private final String orgelKnopf;

        public String getOrgelKnopf() {
            return orgelKnopf;
        }

        public KrassEmp(String orgelKnopf) {
            super();
            this.orgelKnopf = orgelKnopf;
        }
    }
}
