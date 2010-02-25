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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;

public class MappingSystem extends TestSystem {
    protected static boolean useFastTableCreatorAfterInitialCreate = Boolean
            .getBoolean("eclipselink.test.toggle-fast-table-creator");
    protected static boolean isFirstCreation = true;

    protected Project legacyProject;
    protected Project multipleTableProject;
    protected Project keyboardProject;
    protected Project bidirectionalProject;

    public MappingSystem() {
        project = new MappingProject();
        legacyProject = new LegacyTestProject();
        multipleTableProject = new MultipleTableTestProject();
        keyboardProject = new KeyboardProject();
        bidirectionalProject = new BiDirectionInserOrderTestProject();
    }

    public void addDescriptors(DatabaseSession session) {
        DatabasePlatform platform = session.getLogin().getPlatform();

        ClassDescriptor empDescriptor = ((ClassDescriptor) project.getDescriptors().get(Employee.class));
        Employee.addToDescriptor(empDescriptor);
        
        ClassDescriptor hardwareDescriptor = ((ClassDescriptor) project.getDescriptors().get(Hardware.class));
        Hardware.addToDescriptor(hardwareDescriptor);
        
        ClassDescriptor monitorDescriptor = ((ClassDescriptor) project.getDescriptors().get(Monitor.class));
        Monitor.addToDescriptor(monitorDescriptor);
        
        // If on Access exclude the jobDescription mapping
        if (platform.isAccess()) {
            empDescriptor.getMappings().removeElement(empDescriptor.getMappingForAttributeName("jobDescription"));
        }
        
        if (platform.getDefaultSequence().shouldAcquireValueAfterInsert()) {
            RelationalDescriptor cubicleDescriptor = ((RelationalDescriptor) project.getDescriptors().get(Cubicle.class));
            cubicleDescriptor.setSequenceNumberField(null);
            cubicleDescriptor.setSequenceNumberName(null);
        }

        (session).addDescriptors(project);

        // Add the Legacy Test Project.
        (session).addDescriptors(legacyProject);

        // Add the MultipleTableTest Test Project.
        (session).addDescriptors(multipleTableProject);

        // Add the keyboard project - tests constraints.
        (session).addDescriptors(keyboardProject);
        ClassDescriptor joystickDescriptor = ((ClassDescriptor) keyboardProject.getDescriptors().get(Joystick.class));
        joystickDescriptor.addConstraintDependencies(Keyboard.class);

        // Add the insert order project.
        (session).addDescriptors(bidirectionalProject);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        (new LegacyTableMaker()).replaceTables(session);
        (new MultipleTableTestTableMaker()).replaceTables(session);
        (new KeyboardTables()).replaceTables(session);

        //insert order test table creation
        new BiDirectionInsertOrderTableMaker().replaceTables(session);

        //TableDefinition emp1Definition = Employee1.tableDefinition();
        TableDefinition empDefinition = Employee.tableDefinition();
        DatabasePlatform platform = session.getLogin().getPlatform();

        // Only add this field if NOT on Access or DB2
        if (!platform.isAccess()) {
            empDefinition.addField("JDESC", Byte[].class);
        }

        boolean orig_FAST_TABLE_CREATOR = SchemaManager.FAST_TABLE_CREATOR;
        // on Symfoware, to avoid table locking issues only the first invocation
        // of an instance of this class (drops & re-)creates the tables.
        if (useFastTableCreatorAfterInitialCreate && !isFirstCreation) {
            SchemaManager.FAST_TABLE_CREATOR = true;
        }
        try {
            schemaManager.replaceObject(empDefinition);
            schemaManager.replaceObject(Employee.joinTableDefinition());
            schemaManager.replaceObject(Employee.employeePhoneJoinTableDefinition());
            schemaManager.replaceObject(Phone.tableDefinition());

            schemaManager.replaceObject(CompanyCard.tableDefinition());
            schemaManager.replaceObject(Computer.tableDefinition());
            schemaManager.replaceObject(Cubicle.tableDefinition());
            schemaManager.replaceObject(EmergencyExit.tableDefinition());
            schemaManager.replaceObject(EmergencyExit.relationTableDefinition());
            schemaManager.replaceObject(Shipment.tableDefinition());
            schemaManager.replaceObject(getPolicyTableDefinition());
            schemaManager.replaceObject(Address.tableDefinition());
            schemaManager.replaceObject(Monitor.tableDefinition());
            schemaManager.replaceObject(Hardware.tableDefinition());
            schemaManager.replaceObject(SecureSystem.tableDefinition());
            schemaManager.replaceObject(Identification.tableDefinition());
            schemaManager.createSequences();
        } finally {
            if (useFastTableCreatorAfterInitialCreate && !isFirstCreation) {
                SchemaManager.FAST_TABLE_CREATOR = orig_FAST_TABLE_CREATOR;
            }
        }
        // next time it deletes the rows instead.
        isFirstCreation = false;
    }

    public static TableDefinition getPolicyTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_POL");

        definition.addField("FNAME", String.class, 20);
        definition.addField("LNAME", String.class, 20);
        definition.addField("POLICY", String.class, 20);

        return definition;
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        Employee1 employee_1 = Employee1.example1();
        Employee1 employee_2 = Employee1.example2();
        Employee1 employee_3 = Employee1.example3();

        unitOfWork.registerObject(employee_1);
        manager.registerObject(employee_1, "example1");

        manager.registerObject(employee_2, "example2");
        unitOfWork.registerObject(employee_3);
        manager.registerObject(employee_3, "example3");

        /////////////////////////////////////////
        // Populate the MultipleTableTest
        Employee2 employee2_1 = Employee2.example1();
        Employee2 employee2_2 = Employee2.example2();
        unitOfWork.registerObject(employee2_1);
        manager.registerObject(employee2_1, "example1");
        manager.registerObject(employee2_2, "example2");

        /////////////////////////////////////////
        Employee employee1 = Employee.example1();
        Employee employee2 = Employee.example2();
        Employee employee3 = Employee.example3();
        Employee employee4 = Employee.example4();
        Employee employee5 = Employee.example5();
        Employee employee6 = Employee.example6();

        ClassDescriptor cubicleDescriptor = session.getClassDescriptor(Cubicle.class);
        if (!cubicleDescriptor.usesSequenceNumbers()) {
            Employee[] employees = { employee1, employee2, employee3, employee4, employee5, employee6 };
            int id = 100;
            for (int i = 0; i < employees.length; i++) {
                if ((employees[i].cubicle != null) && (employees[i].cubicle.id == null)) {
                    employees[i].cubicle.id = new java.math.BigDecimal(id);
                    id = id + 10;
                }
            }
        }

        employee1.setManager(null);
        employee1.addManagedEmployee(employee2);
        employee1.addManagedEmployee(employee3);

        employee2.setManager(employee1);
        employee2.addManagedEmployee(employee4);

        employee3.setManager(employee1);
        employee3.addManagedEmployee(employee5);

        employee4.setManager(employee2);

        employee5.setManager(employee3);
        employee5.addManagedEmployee(employee6);

        employee6.setManager(employee5);

        unitOfWork.registerObject(employee1);
        manager.registerObject(employee1, "example1");
        manager.registerObject(employee1.computer, "example1");
        manager.registerObject(employee1.shipments.firstElement(), "example1");

        unitOfWork.registerObject(employee2);
        manager.registerObject(employee2, "example2");
        manager.registerObject(employee2.computer, "example2");
        manager.registerObject(employee2.shipments.firstElement(), "example2");

        unitOfWork.registerObject(employee3);
        manager.registerObject(employee3, "example3");
        manager.registerObject(employee3.computer, "example3");

        unitOfWork.registerObject(employee4);
        manager.registerObject(employee4, "example4");
        manager.registerObject(employee4.computer, "example4");
        manager.registerObject(employee4.shipments.firstElement(), "example4");

        unitOfWork.registerObject(employee5);
        manager.registerObject(employee5, "example5");
        manager.registerObject(employee5.computer, "example5");
        manager.registerObject(employee5.shipments.firstElement(), "example5");

        unitOfWork.registerObject(employee6);
        manager.registerObject(employee6, "example6");
        manager.registerObject(employee6.computer, "example6");

        Address address1 = Address.example1();
        Address address2 = Address.example2();
        manager.registerObject(address1, "example1");
        manager.registerObject(address2, "example2");

        unitOfWork.registerObject(address1);
        unitOfWork.registerObject(address2);

        unitOfWork.commit();
    }

    /**
     * CR3922
     * This method tests OneToOneMapping.buildSelectionCriteria(), which would allow customers to
     * get the potential selection criteria for a mapping prior to initialization.
     * This would allow them to more easily create an ammendment method that would ammend the SQL for the join.
     * Add a selection criteria that retrievs all cubicles with location 3rd floor, Section R, Third qubicle on left.
     */
    public static void modifyOneToOneMappingDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor descriptor) {
        OneToOneMapping oneToOneMapping = (OneToOneMapping)descriptor.getMappingForAttributeName("cubicle");
        Expression exp = oneToOneMapping.buildSelectionCriteria();
        ExpressionBuilder builder = exp.getBuilder();
        Expression addedExpression = builder.getField("MAP_CUB.LOCATION").equal("3rd floor, Section R, Third qubicle on left");
        oneToOneMapping.setSelectionCriteria(exp.and(addedExpression));
    }

    /**
     * CR3922
     * This method tests OneToManyMapping.buildSelectionCriteria(), which would allow customers to
     * get the potential selection criteria for a mapping prior to initialization.
     * This would allow them to more easily create an ammendment method that would ammend the SQL for the join.
     * Add a selection criteria that retrievs all phone numbers with area code 613.
     */
    public static void modifyOneToManyMappingDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor descriptor) {
        OneToManyMapping oneToManyMapping = (OneToManyMapping)descriptor.getMappingForAttributeName("managedEmployees");
        Expression exp = oneToManyMapping.buildSelectionCriteria();
        ExpressionBuilder builder = exp.getBuilder();
        Expression addedExpression = builder.getField("MAP_EMP.LNAME").equal("Chapman");
        oneToManyMapping.setSelectionCriteria(exp.and(addedExpression));
    }

    /**
     * CR3922
     * This method tests OneToOneMapping.buildSelectionCriteria() when mapping has target foreign keys, which would allow customers to
     * get the potential selection criteria for a mapping prior to initialization.
     * This would allow them to more easily create an ammendment method that would ammend the SQL for the join.
     * Add a selection criteria that retrievs all hardware (computers) with employee's last name Louis.
     */
    public static void modifyTargetOneToOneMappingDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor descriptor) {
        OneToOneMapping oneToOneMapping = (OneToOneMapping)descriptor.getMappingForAttributeName("computer");
        Expression exp = oneToOneMapping.buildSelectionCriteria();
        ExpressionBuilder builder = exp.getBuilder();
        Expression addedExpression = builder.getField("MAP_HRW.EMP_LNAME").equal("Louis");
        oneToOneMapping.setSelectionCriteria(exp.and(addedExpression));
    }
}
