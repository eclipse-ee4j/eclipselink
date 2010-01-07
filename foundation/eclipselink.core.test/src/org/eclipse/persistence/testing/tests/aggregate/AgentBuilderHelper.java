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
 *     ailitchev - jpa 2.0 element collections support
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.List;

import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.Customer;
import org.eclipse.persistence.testing.models.aggregate.House;

/*
 * Builder is a new class created for testing of the new functionality that
 * allows the same AggregateCollection class to be used by several classes.
 * Class Builder is exactly the same as class Agent -
 * this is the helper that makes easier to update the tests designed to use Agent
 * to use Builder, too. 
 */
public class AgentBuilderHelper {

    public static List getCustomers(Object object) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            return ((Agent)object).getCustomers();
        } else if (Builder.class.equals(cls)) {
            return ((Builder)object).getCustomers();
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static List getHouses(Object object) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            return ((Agent)object).getHouses();
        } else if (Builder.class.equals(cls)) {
            return ((Builder)object).getHouses();
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static String getName(Class cls) {
        if(Agent.class.equals(cls)) {
            return "Agent";
        } else if (Builder.class.equals(cls)) {
            return "Builder";
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static String getNameInBrackets(Class cls) {
        if(Agent.class.equals(cls)) {
            return "(Agent)";
        } else if (Builder.class.equals(cls)) {
            return "(Builder)";
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static String getLastName(Object object) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            return ((Agent)object).getLastName();
        } else if (Builder.class.equals(cls)) {
            return ((Builder)object).getLastName();
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }    

    public static void setLastName(Object object, String lastName) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            ((Agent)object).setLastName(lastName);
        } else if (Builder.class.equals(cls)) {
            ((Builder)object).setLastName(lastName);
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }    
    
    public static String getFirstName(Object object) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            return ((Agent)object).getFirstName();
        } else if (Builder.class.equals(cls)) {
            return ((Builder)object).getFirstName();
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }    

    public static void setFirstName(Object object, String firstName) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            ((Agent)object).setFirstName(firstName);
        } else if (Builder.class.equals(cls)) {
            ((Builder)object).setFirstName(firstName);
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }    
    
    public static void addCustomer(Object object, Customer customer) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            ((Agent)object).addCustomer(customer);
        } else if (Builder.class.equals(cls)) {
            ((Builder)object).addCustomer(customer);
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static void addHouse(Object object, House house) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            ((Agent)object).addHouse(house);
        } else if (Builder.class.equals(cls)) {
            ((Builder)object).addHouse(house);
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static void removeCustomer(Object object, Customer customer) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            ((Agent)object).removeCustomer(customer);
        } else if (Builder.class.equals(cls)) {
            ((Builder)object).removeCustomer(customer);
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }

    public static void removeHouse(Object object, House house) {
        Class cls = object.getClass();
        if(Agent.class.equals(cls)) {
            ((Agent)object).removeHouse(house);
        } else if (Builder.class.equals(cls)) {
            ((Builder)object).removeHouse(house);
        } else {
            throw new TestProblemException("Class " + cls + " can't be used here - must be either Agent or Builder");
        }
    }
}
