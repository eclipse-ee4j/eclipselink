package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.testing.models.aggregate.Employee1;
import org.eclipse.persistence.testing.models.aggregate.HomeAddress;
import org.eclipse.persistence.testing.models.aggregate.WorkingAddress;

/** 
 * Test changing private parts of an object.
 * 
 */
public class UpdateChangeObjectTestEmployee1 extends ComplexUpdateTest {

    public UpdateChangeObjectTestEmployee1() {
        super();
    }

    public UpdateChangeObjectTestEmployee1(Employee1 originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee1 employee = (Employee1)this.workingCopy;
        // Transformation
        if (employee.getAddress() instanceof HomeAddress) {
            employee.setAddress(WorkingAddress.example1());
        }else{
            employee.setAddress(HomeAddress.example1());
        }
    }
}
