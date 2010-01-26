package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class UnitOfWorkConformNewObjectTest  extends AutoVerifyTestCase {
    public UnitOfWorkConformNewObjectTest() {
        setDescription("Test that the query maintains order for non changed objects.");
    }

    public void test() {
        String lastName = "UOWConformNewObjectTest";
        
        Employee emp1 = new Employee();
        emp1.setFirstName("1");
        emp1.setLastName(lastName);
        
        Employee emp2 = new Employee();
        emp2.setFirstName("2");
        emp2.setLastName(lastName);
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.setShouldNewObjectsBeCached(true);
        
        uow.registerNewObject(emp1);
        uow.registerNewObject(emp2);
        // assign pk only to the first employee
        uow.assignSequenceNumber(emp1);
        
        String errorMsg = "";
        String localErrorMsg;
        ExpressionBuilder builder;
        
        ReadAllQuery queryReadAll = new ReadAllQuery();
        queryReadAll.setReferenceClass(Employee.class);
        queryReadAll.conformResultsInUnitOfWork();
        Vector<Employee> employees = (Vector)uow.executeQuery(queryReadAll);
        localErrorMsg = ""; 
        if(!employees.contains(emp1)) {
            localErrorMsg += "emp1, ";
        }
        if(!employees.contains(emp2)) {
            localErrorMsg += "emp2";
        }
        if(localErrorMsg.length() > 0) {
            errorMsg += "ReadAll: " + errorMsg + "; ";
        }
        
        ReadAllQuery queryReadAllByLastName = new ReadAllQuery();
        queryReadAllByLastName.setReferenceClass(Employee.class);
        queryReadAllByLastName.conformResultsInUnitOfWork();
        builder = queryReadAllByLastName.getExpressionBuilder();
        queryReadAllByLastName.setSelectionCriteria(builder.get("lastName").equal(lastName));
        Vector<Employee> employeesByLastName = (Vector)uow.executeQuery(queryReadAllByLastName);
        localErrorMsg = ""; 
        if(!employeesByLastName.contains(emp1)) {
            localErrorMsg += "emp1, ";
        }
        if(!employeesByLastName.contains(emp2)) {
            localErrorMsg += "emp2";
        }
        if(localErrorMsg.length() > 0) {
            errorMsg += "ReadAllByLastName: " + errorMsg + "; ";
        }
        
        ReadObjectQuery queryReadObject_1 = new ReadObjectQuery();
        queryReadObject_1.setReferenceClass(Employee.class);
        queryReadObject_1.conformResultsInUnitOfWork();
        builder = queryReadObject_1.getExpressionBuilder();
        queryReadObject_1.setSelectionCriteria(builder.get("lastName").equal(lastName).and(builder.get("firstName").equal("1")));
        if(emp1 != uow.executeQuery(queryReadObject_1)) {
            errorMsg += "ReadObject_1: emp1; ";
        }

        ReadObjectQuery queryReadObject_2 = new ReadObjectQuery();
        queryReadObject_2.setReferenceClass(Employee.class);
        queryReadObject_2.conformResultsInUnitOfWork();
        builder = queryReadObject_2.getExpressionBuilder();
        queryReadObject_2.setSelectionCriteria(builder.get("lastName").equal(lastName).and(builder.get("firstName").equal("2")));
        if(emp2 != uow.executeQuery(queryReadObject_2)) {
            errorMsg += "ReadObject_2: emp2; ";
        }

        ReadObjectQuery queryReadObject_1_ByObject = new ReadObjectQuery();
        queryReadObject_1_ByObject.setReferenceClass(Employee.class);
        queryReadObject_1_ByObject.conformResultsInUnitOfWork();
        queryReadObject_1_ByObject.setSelectionObject(emp1);
        Employee result = (Employee)uow.executeQuery(queryReadObject_1_ByObject);
        if (emp1 != result) {
            errorMsg += "queryReadObject_1_ByObject: emp1; ";
        }

        ReadObjectQuery queryReadObject_1_ByKey = new ReadObjectQuery();
        queryReadObject_1_ByKey.setReferenceClass(Employee.class);
        queryReadObject_1_ByKey.conformResultsInUnitOfWork();
        queryReadObject_1_ByKey.setSelectionId(emp1.getId());
        result = (Employee)uow.executeQuery(queryReadObject_1_ByKey);
        if (emp1 != result) {
            errorMsg += "queryReadObject_1_ByKey: emp1; ";
        }

        uow.release();
        
        if (errorMsg.length() > 0) {
            errorMsg = "The following objects were not found: " + errorMsg;
            throw new TestErrorException(errorMsg);
        }
    }
}
