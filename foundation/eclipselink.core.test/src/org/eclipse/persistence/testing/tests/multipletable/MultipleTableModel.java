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
package org.eclipse.persistence.testing.tests.multipletable;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.multipletable.Cow;
import org.eclipse.persistence.testing.models.multipletable.Horse;
import org.eclipse.persistence.testing.models.multipletable.Human;
import org.eclipse.persistence.testing.models.multipletable.SuperCow;
import org.eclipse.persistence.testing.models.multipletable.SuperHorse;
import org.eclipse.persistence.testing.models.multipletable.SuperSwan;
import org.eclipse.persistence.testing.models.multipletable.Swan;
import org.eclipse.persistence.testing.models.multipletable.MultipleTableSystem;

/**
 * Testing model for multiple table tests
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date May 28, 2007
 */
public class MultipleTableModel extends TestModel {
    public MultipleTableModel() {
        setDescription("This model tests multiple tables.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new MultipleTableSystem());
    }

    public void addTests() {
        addTestsToTestCollection(this);
    }
    public static void addTestsToTestCollection(TestCollection collection) {
        collection.addTest(new ReadAllTest(Cow.class));
        collection.addTest(new ReadAllTest(SuperCow.class));
        collection.addTest(new ReadAllTest(Horse.class));
        collection.addTest(new ReadAllTest(SuperHorse.class));
        collection.addTest(new ReadAllTest(Swan.class));
        collection.addTest(new ReadAllTest(SuperSwan.class));
        
        collection.addTest(getCowTest());
        collection.addTest(getSuperCowTest());
        collection.addTest(getHorseTest());
        collection.addTest(getSuperHorseTest());
        collection.addTest(getSwanTest());
        collection.addTest(getSuperSwanTest());
        collection.addTest(getHumanTest());
    }
    
    public static MultipleTableTest getCowTest() {
        Cow cow = new Cow();
        cow.setCalfCountId(101);
        cow.setCalfCount(18);
        cow.setAgeId(102);
        cow.setAge(10);
        cow.setWeightId(103);
        cow.setWeight(500);
        cow.setName("Lucky");
        
        return new MultipleTableTest(cow);
    }
    
    public static MultipleTableTest getSuperCowTest() {
        SuperCow cow = new SuperCow();
        cow.setCalfCountId(201);
        cow.setCalfCount(18);
        cow.setAgeId(202);
        cow.setAge(10);
        cow.setWeightId(203);
        cow.setWeight(500);
        cow.setSpeed(25);
        cow.setName("SuperLucky");
        
        return new MultipleTableTest(cow);
    }
    
    public static MultipleTableTest getHorseTest() {
        Horse horse = new Horse();
        horse.setFoalCount(9);
        horse.setAge(10);
        horse.setWeight(500);
        horse.setName("Pinky");
        
        return new MultipleTableTest(horse);
    }
    
    public static MultipleTableTest getSuperHorseTest() {
        SuperHorse horse = new SuperHorse();
        horse.setFoalCount(9);
        horse.setAge(10);
        horse.setWeight(500);
        horse.setSpeed(25);
        horse.setName("SuperPinky");
        
        return new MultipleTableTest(horse);
    }
    
    public static MultipleTableTest getSwanTest() {
        Swan swan = new Swan();
        swan.setCygnetCount(24);
        swan.setAge(10);
        swan.setWeight(500);
        swan.setName("Naws");
        
        return new MultipleTableTest(swan);
    }
    
    public static MultipleTableTest getSuperSwanTest() {
        SuperSwan swan = new SuperSwan();
        swan.setCygnetCount(24);
        swan.setAge(10);
        swan.setWeight(500);
        swan.setSpeed(25);
        swan.setName("RepusNaws");
        
        return new MultipleTableTest(swan);
    }
    
    public static MultipleTableTest getHumanTest() {
        Human human = new Human();
        human.setKidCount(2);
        human.setName("Wayne");
        
        return new MultipleTableTest(human);
    }
    
    public static class ReadAllTest extends TestCase {
        Class cls;
        public ReadAllTest(Class cls) {
            super();
            setName(Helper.getShortClassName(cls) + "ReadAllTest");
            this.cls = cls;
        }
        public void test() {
            getSession().readAllObjects(cls);
        }
    }
}
