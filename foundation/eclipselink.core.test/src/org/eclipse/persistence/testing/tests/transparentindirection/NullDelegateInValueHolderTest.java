package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.transparentindirection.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Test indirection functionality when setting an indirect container's 
 * valueholder to a new ValueHolder.
 * Bug 345495
 */
public class NullDelegateInValueHolderTest extends TestCase {

    protected Class indirectCollectionClass;
    protected AbstractOrder testOrder;
    
    public NullDelegateInValueHolderTest(Class indirectCollectionClass) {
        super();
        this.indirectCollectionClass = indirectCollectionClass;
        setDescription("NullDelegateInValueHolderTest: " + Helper.getShortClassName(this.indirectCollectionClass));
    }
    
    public void setup() {
        String customerName = "ACME, Inc.";
        if (indirectCollectionClass.equals(IndirectList.class)) {
            testOrder = new Order(customerName);
        } else if (indirectCollectionClass.equals(IndirectMap.class)) {
            testOrder = new MappedOrder(customerName);
        } else if (indirectCollectionClass.equals(IndirectSet.class)) {
            testOrder = new SetOrder(customerName);
        }

        ClassDescriptor descriptor = getSession().getDescriptor(testOrder);
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) descriptor.getMappingForAttributeName("salesReps");
        IndirectionPolicy policy = mapping.getIndirectionPolicy();
        
        // replace indirect container's valueholder with a new ValueHolder instance
        mapping.setAttributeValueInObject(testOrder, policy.buildIndirectObject(new ValueHolder()));
    }

    public void test() {
        int numberOfObjects = -1;
        try {
            numberOfObjects = testOrder.getNumberOfSalesReps();
        } catch (Exception e) {
            throw new TestErrorException("Error retrieving number of sales reps from order " + testOrder, e);
        }
        if (numberOfObjects != 0) {
            throwError("Number of sales reps != 0 :" + numberOfObjects);
        }
    }

}
