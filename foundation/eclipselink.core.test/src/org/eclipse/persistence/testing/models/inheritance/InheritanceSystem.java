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
package org.eclipse.persistence.testing.models.inheritance;

import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class InheritanceSystem extends TestSystem {
    public InheritanceSystem() {
        project = XMLProjectReader.read("org/eclipse/persistence/testing/models/inheritance/inheritance-project.xml", getClass().getClassLoader());
    }

    public void addDescriptors(DatabaseSession session) {
        // Oracle has bug in outjoins that require outerjoin of inheritance type.
        // This should really be handled by the platform during expression normalization...
        // Id for Entomologist can be negative (millis cast to int wraps...)
        project.getDescriptor(Entomologist.class).setIdValidation(IdValidation.NONE);

        session.addDescriptors(project);

        // For using read all subclasses views.
        DatabasePlatform platform = session.getLogin().getPlatform();
        if (platform.isOracle() || platform.isSybase()) {
            ClassDescriptor computerDescriptor = session.getDescriptor(Computer.class);
            ClassDescriptor vehicleDescriptor = session.getDescriptor(Vehicle.class);
            computerDescriptor.getInheritancePolicy().setReadAllSubclassesViewName("AllComputers");
            vehicleDescriptor.getInheritancePolicy().setReadAllSubclassesViewName("AllVehicles");
        }
        
        // Enable outer-join on AnimalMatt hierarchy.
        session.getDescriptor(Animal_Matt.class).getInheritancePolicy().setShouldOuterJoinSubclasses(true);
    }
    
    public void createTables(DatabaseSession session) {
        dropTableConstraints(session);
        new InheritanceTableCreator().replaceTables(session);
        
        SchemaManager schemaManager = new SchemaManager(session);
        if (session.getLogin().getPlatform().isOracle()) {
            schemaManager.replaceObject(Computer.oracleView());
            schemaManager.replaceObject(Vehicle.oracleView());
        } else if (session.getLogin().getPlatform().isSybase()) {
            schemaManager.replaceObject(Computer.sybaseView());
            schemaManager.replaceObject(Vehicle.sybaseView());
        //CREATE VIEW statement was added in MySQL 5.0.1.  Uncomment it when we support MySQL 5
        //} else if (session.getLogin().getPlatform().isMySQL()) {
            //schemaManager.replaceObject(Computer.sybaseView());
            //schemaManager.replaceObject(Vehicle.mySQLView());
        }
    }
    
    /**
     * Drop table constraints
     */
    public void dropTableConstraints(Session session) {
        if (!SchemaManager.FAST_TABLE_CREATOR) {
            if (session.getLogin().getPlatform().isOracle()) {
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table BUS CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table CAR CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table COMPANY CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table FUEL_VEH CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table NH_COMP CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table INH_MF CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table KING_DEVELOPER CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table KING_PERSONG CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PARTNUMS CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
                
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table VEHICLE CASCADE CONSTRAINTS"));
                } catch (Exception e) {}
            }
         
            // Drop old constraints.
            try {
                if (session.getPlatform().supportsUniqueKeyConstraints()
                        && !session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
                    session.executeNonSelectingSQL("Alter TABLE PROJECT_WORKER_BATCH DROP CONSTRAINT PROJECT_WORKER_BATCH_HD");
                    session.executeNonSelectingSQL("Alter TABLE PROJECT_BATCH DROP CONSTRAINT PROJECT_WORKER_BATCH_FK");
                    session.executeNonSelectingSQL("Alter TABLE ALLIGATOR DROP CONSTRAINT FK_ALLIGATOR_VICTIM_ID");
                    session.executeNonSelectingSQL("Alter TABLE PERSON2 DROP CONSTRAINT PERSON2_PERSON2_FRND");
                    session.executeNonSelectingSQL("Alter TABLE PERSON2 DROP CONSTRAINT PERSON2_PERSON2_REP");
                    session.executeNonSelectingSQL("Alter TABLE PERSON2 DROP CONSTRAINT PERSON2_PERSON2_BS");
                }
            } catch (Exception ignore) {}
        }
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();

        Cat cat = Cat.example1();
        session.writeObject(cat);
        manager.registerObject(cat, "catExample1");

        Dog dog = Dog.example1();
        session.writeObject(dog);
        manager.registerObject(dog, "dogExample1");

        cat = Cat.example2();
        session.writeObject(cat);
        manager.registerObject(cat, "catExample2");

        dog = Dog.example2();
        session.writeObject(dog);
        manager.registerObject(dog, "dogExample2");

        cat = Cat.example3();
        session.writeObject(cat);
        manager.registerObject(cat, "catExample3");

        dog = Dog.example3();
        session.writeObject(dog);
        manager.registerObject(dog, "dogExample3");

        Company company = Company.example1();
        session.writeObject(company);
        manager.registerObject(company, "example1");

        manager.registerObject(((Vector)company.getVehicles().getValue()).firstElement(), "example1");

        company = Company.example2();
        session.writeObject(company);
        manager.registerObject(company, "example2");

        company = Company.example3();
        session.writeObject(company);
        manager.registerObject(company, "example3");

        Person person = Person.example1();
        session.writeObject(person);
        manager.registerObject(person, "example1");

        //populate the data for duplicate field testing
        session.writeObject(A_King2.exp1());
        session.writeObject(A_King2.exp2());
        session.writeObject(A_1_King2.exp3());
        session.writeObject(A_2_King2.exp4());
        session.writeObject(A_2_1_King2.exp5());

        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        person = Person.example2();
        unitOfWork.registerObject(person);
        unitOfWork.commit();
        manager.registerObject(person, "example2");
        manager.registerObject(person.bestFriend, "example5");
        manager.registerObject(person.representitive, "example4");

        person = Person.example3();
        session.writeObject(person);
        manager.registerObject(person, "example3");

        Computer computer = Computer.example1();
        session.writeObject(computer);
        manager.registerObject(computer, "example1");

        computer = Computer.example2();
        session.writeObject(computer);
        manager.registerObject(computer, "example2");

        computer = Computer.example3();
        session.writeObject(computer);
        manager.registerObject(computer, "example3");

        computer = Computer.example4();
        session.writeObject(computer);
        manager.registerObject(computer, "example4");

        computer = Computer.example5();
        session.writeObject(computer);
        manager.registerObject(computer, "example5");

        JavaProgrammer JP = JavaProgrammer.example1();
        session.writeObject(JP);
        manager.registerObject(JP, "example1");

        JP = JavaProgrammer.example2();
        session.writeObject(JP);
        manager.registerObject(JP, "example2");

        // Added to test bug 3019934.
        unitOfWork = session.acquireUnitOfWork();
        Alligator alligator = new Alligator();
        alligator.setFavoriteSwamp("Florida");
        alligator.setLatestVictim(JavaProgrammer.steve());
        unitOfWork.registerObject(alligator);
        manager.registerObject(alligator, "example1");
        unitOfWork.commit();
        
        //Added to test bug 6111278
        
        unitOfWork = session.acquireUnitOfWork();
		Entomologist bugguy = new Entomologist();
        bugguy.setId((int)System.currentTimeMillis());
        bugguy.setName("Gary");
        bugguy = (Entomologist)unitOfWork.registerObject(bugguy);
        Insect insect = new GrassHopper();
        insect.setIn_numberOfLegs(4);
        insect.setEntomologist(bugguy);
        bugguy.getInsectCollection().add(insect);
        unitOfWork.commit();

    }
}
