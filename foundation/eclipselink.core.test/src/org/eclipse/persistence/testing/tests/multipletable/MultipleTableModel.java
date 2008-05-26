/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.multipletable.Cow;
import org.eclipse.persistence.testing.models.multipletable.Horse;
import org.eclipse.persistence.testing.models.multipletable.Human;
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
        addTest(getCowTest());
        addTest(getHorseTest());
        addTest(getSwanTest());
        addTest(getHumanTest());
    }
    
    public static MultipleTableTest getCowTest() {
        Cow cow = new Cow();
        cow.setCowId(99);
        cow.setCalfCount(18);
        cow.setName("Lucky");
        
        return new MultipleTableTest(cow);
    }
    
    public static MultipleTableTest getHorseTest() {
        Horse horse = new Horse();
        horse.setFoalCount(9);
        horse.setName("Pinky");
        
        return new MultipleTableTest(horse);
    }
    
    public static MultipleTableTest getSwanTest() {
        Swan swan = new Swan();
        swan.setCygnetCount(24);
        swan.setName("Naws");
        
        return new MultipleTableTest(swan);
    }
    
    public static MultipleTableTest getHumanTest() {
        Human human = new Human();
        human.setKidCount(2);
        human.setName("Wayne");
        
        return new MultipleTableTest(human);
    }
}