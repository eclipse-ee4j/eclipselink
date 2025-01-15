package org.eclipse.persistence.testing.models.jpa.advanced.holders;

import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;

import java.math.BigInteger;
import java.util.Objects;

public class EmployeeDetail {
    public String firstName;
    public String lastName;
    public Employee manager;
    public Long count;
    public BigInteger code;
    public EmploymentPeriod period;
    public EmployeeDetail(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public EmployeeDetail(String firstName, String lastName, Employee manager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.manager = manager;
    }
    public EmployeeDetail(EmploymentPeriod e) {
        this.period = e;
    }
    public EmployeeDetail(Employee e) {
        this.firstName = e.getFirstName();
        this.lastName = e.getLastName();
        this.manager = e.getManager();
    }
    public EmployeeDetail(String firstName, String lastName, Long count) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.count = count;
    }
    public EmployeeDetail(String firstName, String lastName, BigInteger code) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.code = code;
    }
    @Override
    public int hashCode() {
        int result = 0;
        result += (firstName != null) ? firstName.hashCode() : 0;
        result += (lastName != null) ? lastName.hashCode() : 0;
        result += (manager != null) ? manager.hashCode() : 0;
        result += (count != null) ? count.hashCode() : 0;
        result += (code != null) ? code.hashCode() : 0;
        result += (period != null) ? period.hashCode() : 0;
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if ((o == null) || (!(o instanceof EmployeeDetail))) {
            return false;
        }
        EmployeeDetail other = (EmployeeDetail) o;
        return Objects.equals(this.firstName, other.firstName) &&
                Objects.equals(this.lastName, other.lastName) &&
                Objects.equals(this.manager, other.manager) &&
                Objects.equals(this.count, other.count) &&
                Objects.equals(this.code, other.code) &&
                Objects.equals(this.period, other.period);
    }
    @Override
    public String toString() {
        return "EmployeeDetail(" + firstName + ", " + lastName + ", " +
                manager + ", " + count + ", " + code + ", "+ period+ ")";
    }
}
