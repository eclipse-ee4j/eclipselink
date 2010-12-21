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

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.EmploymentPeriod;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Hobby;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MotorVehicle;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestExtendedQueries extends JPA1Base {

    // the random generator will use this final seed in order to enable reproduceable testing
    private static final long RANDOM_SEED = 1232133213;
    // should not be changed
    private static final int NUMBER_OF_DEPARTMENTS = 10;
    private static final int NUMBER_OF_EMPLOYEES = 100;
    // number of maximum tries during random distribution
    private static final int MAX_NUMBER_OF_TRIES = 100;
    // since there is a one-to-one relationship for vehicle driver this must at least match the number of employees
    private static final short NUMBER_OF_MOTORVEHICLES = NUMBER_OF_EMPLOYEES;
    private static final short NUMBER_OF_BICYCLES = 5;
    // this maps take note of how much entities are used in relationships
    private static final Map<String, Department> ALL_DEPARTMENTS = new HashMap<String, Department>();
    private static final Map<String, Integer> NUMBER_OF_GIVEN_NAMES_USED = new HashMap<String, Integer>();
    private static final Map<String, Integer> NUMBER_OF_SURNAMES_USED = new HashMap<String, Integer>();
    private static final Map<Short, List<Integer>> BIKES_RIDERS = new HashMap<Short, List<Integer>>();
    private static final Map<Short, Integer> MOTORVEHICLES_DRIVERS = new HashMap<Short, Integer>();
    private static final Set<String> EMP_HOBBIES = new HashSet<String>();
    // an array of given names
    private static final String[] GIVEN_NAMES = { "Hadmar", "Hagen", "Hakan", "Hakon", "Hannes", "Hanns", "Hans", "Hansj\u00f6rg",
            "Hansklaus", "Hanspeter", "Harald", "Harold", "Harro", "Harry", "Hartmut", "Hartwig", "Hartwin", "Harvey", "Hasso",
            "Hauke", "H\u00e4nel", "Heiko", "Heyko", "Heimo", "Hein", "Heiner", "Heinrich", "Heinz", "Helge", "Helmar", "Helmut",
            "Hendrik", "Henning", "Henri", "Henrik", "Henryk", "Herbert", "Heribert", "Hermann", "Hieronymus", "Hilarius",
            "Hilmar", "Hinrich", "Hjalmar", "Holger", "Horst", "Hubert", "Hubertus", "Hugo", "Humphrey", "Hanna", "Hannah",
            "Hannelore", "Harriet", "Hedi", "Hedwig", "Heide", "Heidemarie", "Heidi", "Heike", "Heinke", "Helen", "Helena",
            "Helene", "Helga", "Helgard", "Hella", "Helma", "Henrike", "Herma", "Hermine", "Hertha", "Hieu", "Hilda", "Hilde",
            "Hildegard", "Hilke", "Hulda", "Holde", "Holda", "Hedda", "Heida", "Hazel" };
    // an array of surnames
    private static final String[] SURNAMES = { "Haak", "Haas", "Habermann", "Hache", "Hackmann", "Halbach", "Haller", "Hamann",
            "Hamburger", "Hamilton", "Hampe", "Hanisch", "Hanson", "Hanstein", "Hardenberg", "Harding", "Hardy", "Harnack",
            "Hartenthaler", "Hartfelder", "Hartig", "Hary", "Haus Lancaster", "Hausmann", "Hausner", "Havenstein", "Hawke",
            "Hayek", "Hecht", "Heer", "Hegemann", "Heidegger", "Heider", "Heim", "Hein", "Heinichen", "Heinrich", "Heise",
            "Helbig", "Hellmann", "Helmig", "Helms", "Hemsterhuis", "Hendrix", "Hennig", "Henning", "Henry", "Henschel",
            "Hense", "Hentschel", "Herbst", "Herder", "Hermann", "Hermelink", "Herold", "Herrmann", "Herzog", "Hesse",
            "Hesselbarth", "Hettner", "Heuser", "Hewitt", "Hilbert", "Hildebrand", "Hildebrandt", "Hilgenfeld" };
    // list of hobbienames
    private static final String[] HOBBY_NAMES = { "reading", "cooking", "watching tv", "cleaning the house", "swimming",
            "computers", "stamps" };
    // array of all hobbies
    private static final Hobby[] HOBBIES = new Hobby[HOBBY_NAMES.length];
    // array of all bicycles
    private static final Bicycle[] BICYCLES = new Bicycle[NUMBER_OF_BICYCLES];
    // array of all motorvehicles
    private static final MotorVehicle[] MOTORVEHICLES = new MotorVehicle[NUMBER_OF_MOTORVEHICLES];
    // randomizer for distributing relationships between Employees and other entities
    private static final Random nameRandomizer = new Random(RANDOM_SEED);
    private short vehiclePK = 0;

    protected void resetData() {
        NUMBER_OF_GIVEN_NAMES_USED.clear();
        NUMBER_OF_SURNAMES_USED.clear();
        EMP_HOBBIES.clear();
        MOTORVEHICLES_DRIVERS.clear();
    }

    protected String getRandomSurname() {
        String tmpName = SURNAMES[nameRandomizer.nextInt(SURNAMES.length)];
        if (!NUMBER_OF_SURNAMES_USED.containsKey(tmpName)) {
            NUMBER_OF_SURNAMES_USED.put(tmpName, new Integer(1));
        } else {
            Integer tmpNum = NUMBER_OF_SURNAMES_USED.get(tmpName);
            tmpNum = Integer.valueOf(tmpNum.intValue() + 1);
            NUMBER_OF_SURNAMES_USED.put(tmpName, tmpNum);
        }
        return tmpName;
    }

    protected String getRandomGivenName() {
        String tmpName = GIVEN_NAMES[nameRandomizer.nextInt(GIVEN_NAMES.length)];
        if (!NUMBER_OF_GIVEN_NAMES_USED.containsKey(tmpName)) {
            NUMBER_OF_GIVEN_NAMES_USED.put(tmpName, new Integer(1));
        } else {
            Integer tmpNum = NUMBER_OF_GIVEN_NAMES_USED.get(tmpName);
            tmpNum = Integer.valueOf(tmpNum.intValue() + 1);
            NUMBER_OF_GIVEN_NAMES_USED.put(tmpName, tmpNum);
        }
        return tmpName;
    }

    /**
     * returns a hobby for given employee, it is not possible to assign a hobby more than once to a employee
     * 
     * @param empID
     *            the ID of the employee
     * @return a hobby for the given employee ID
     * @throws Exception
     *             is thrown if there are no hobbies left which have not already been assigned to this employee
     */
    protected Hobby getRandomHobby(int empID) {
        boolean matched = false;
        int index = 0;
        for (int number_of_tries = 0; !matched; number_of_tries++) {
            index = nameRandomizer.nextInt(HOBBIES.length - 1);
            if (!EMP_HOBBIES.contains(empID + "_" + index)) {
                EMP_HOBBIES.add(empID + "_" + index);
                matched = true;
            }
            if (number_of_tries >= MAX_NUMBER_OF_TRIES) {
                throw new RuntimeException("Can't find anymore randomly distributed hobbies");
            }
        }
        return HOBBIES[index];
    }

    @SuppressWarnings("boxing")
    protected Bicycle getRandomBike(int empID) {
        short index = (short) nameRandomizer.nextInt(BICYCLES.length - 1);
        // take a note about this relation
        if (!BIKES_RIDERS.containsKey(BICYCLES[index].getId())) {
            ArrayList<Integer> tmpAl = new ArrayList<Integer>();
            tmpAl.add(empID);
            BIKES_RIDERS.put(BICYCLES[index].getId(), tmpAl);
        } else {
            List<Integer> tmpAl = BIKES_RIDERS.get(BICYCLES[index].getId());
            tmpAl.add(empID);
        }
        return BICYCLES[index];
    }

    @SuppressWarnings("boxing")
    protected MotorVehicle getRandomMotorVehicle(int empID) {
        // take note about this relation
        boolean matched = false;
        short index = 0;
        for (int number_of_tries = 0; !matched; number_of_tries++) {
            index = (short) nameRandomizer.nextInt(MOTORVEHICLES.length - 1);
            if (!MOTORVEHICLES_DRIVERS.containsKey(MOTORVEHICLES[index].getId())) {
                MOTORVEHICLES_DRIVERS.put(MOTORVEHICLES[index].getId(), empID);
                matched = true;
            }
            if (number_of_tries >= MAX_NUMBER_OF_TRIES) {
                throw new RuntimeException("Can't find anymore randomly distributed motorvehicles");
            }
        }
        return MOTORVEHICLES[index];
    }

    @Override
    protected void setup() {
        resetData();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            // fills the hobby-array
            for (int i = 0; i < HOBBY_NAMES.length; i++) {
                Hobby aHobby = new Hobby(HOBBY_NAMES[i]);
                HOBBIES[i] = aHobby;
                em.persist(aHobby);
            }
            // creates & persists some departments
            for (int i = 0; i < NUMBER_OF_DEPARTMENTS; i++) {
                Department tmpDep = new Department(i, "Department_" + i);
                // ALL_DEPARTMENTS.put(tmpDep.getName(), tmpDep);
                em.persist(tmpDep);
            }
            // creates & persists some motorvehicles
            for (int i = 0; i < NUMBER_OF_MOTORVEHICLES; i++) {
                MotorVehicle tmpMVehicle = new MotorVehicle();
                tmpMVehicle.setId(Short.valueOf(++this.vehiclePK));
                tmpMVehicle.setBrand("foo-car");
                MOTORVEHICLES[i] = tmpMVehicle;
                em.persist(tmpMVehicle);
            }
            // creates & persists some bicycles
            for (int i = 0; i < NUMBER_OF_BICYCLES; i++) {
                Bicycle tmpBike = new Bicycle();
                tmpBike.setId(Short.valueOf(++this.vehiclePK));
                tmpBike.setBrand("foo-brand");
                tmpBike.setRiders(new ArrayList<Employee>());
                BICYCLES[i] = tmpBike;
                em.persist(tmpBike);
            }
            // creates & persists some empoyees
            for (int i = 0; i < NUMBER_OF_EMPLOYEES; i++) {
                /*
                 * sets a department tries to set 10 employees to each department example: employees 1 to 10 are related to
                 * department 1. employees 11 to 20 are mapped to department 2 and so on
                 */
                Employee tmpEmp = new Employee(i, this.getRandomGivenName(), this.getRandomSurname(), ALL_DEPARTMENTS
                        .get("Department_" + Math.ceil(i / 100) * 10));
                // add two hobbies
                tmpEmp.addHobby(this.getRandomHobby(tmpEmp.getId()));
                tmpEmp.addHobby(this.getRandomHobby(tmpEmp.getId()));
                tmpEmp.addHobby(this.getRandomHobby(tmpEmp.getId()));
                tmpEmp.addHobby(this.getRandomHobby(tmpEmp.getId()));
                tmpEmp.addHobby(this.getRandomHobby(tmpEmp.getId()));
                // every second employee gets a motorvehicle
                if (i % 2 == 0) {
                    getRandomMotorVehicle(tmpEmp.getId()).setDriver(tmpEmp);
                }
                // the others get a bicycle
                else {
                    getRandomBike(tmpEmp.getId()).getRiders().add(tmpEmp);
                }
                // set the period
                tmpEmp.setEmploymentPeriod(new EmploymentPeriod());
                tmpEmp.getEmploymentPeriod().setStartDate(new Date(System.currentTimeMillis() - 1000000000));
                tmpEmp.getEmploymentPeriod().setEndDate(new Date(System.currentTimeMillis() + 1000000000));
                em.persist(tmpEmp);
            }
            env.commitTransactionAndClear(em);
            // em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * @Test public void testQueryWithMemberOf() throws Exception { EntityManager em = getEnvironment().getEntityManager(); try
     * { Bicycle bike = new Bicycle(); bike.setId((short)12); bike.setBrand("foo"); bike.setColor("brown");
     * bike.setNumberOfGears((short)1); em.persist(bike);
     * 
     * //test string-mapping of enums Query query = em.createQuery("Select Distinct Object(emp) FROM Employee emp WHERE :param
     * NOT MEMBER emp.bicycles"); query.setParameter("param", bike); query.getResultList(); } finally { closeEntityManager(em);
     * } }
     */
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @Test
    public void testQueryWithUpdateAndSet() throws Exception {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Query query = em
                    .createQuery("UPDATE Employee emp SET emp.salary = emp.salary + :param1 WHERE emp.firstname = SUBSTRING(:string, :int1, :int2)");
            query.setParameter("param1", Integer.valueOf(13));
            query.setParameter("string", "moo");
            query.setParameter("int1", Integer.valueOf(1));
            query.setParameter("int2", Integer.valueOf(2));
            query.executeUpdate();
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @Test
    public void testQueryWithBuiltinAbs() throws Exception {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // test string-mapping of enums
            Query query = em.createQuery("Select DISTINCT Object(emp) From Employee emp WHERE emp.salary > ABS(:dbl)");
            query.setParameter("dbl", new Double(1180D));
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    @Test
    public void testQueryWithMemberOf() throws Exception {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // test string-mapping of enums
            Query query = em
                    .createQuery("Select Distinct Object(emp) FROM Employee emp WHERE emp.firstname = SUBSTRING(:string, :int1, :int2)");
            query.setParameter("string", "moo");
            query.setParameter("int1", Integer.valueOf(1));
            query.setParameter("int2", Integer.valueOf(2));
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testQueryWithComparisonExpressionAndEnums() throws Exception {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // test string-mapping of enums
            Query query4 = em
                    .createQuery("SELECT btfa.id FROM BasicTypesFieldAccess btfa WHERE btfa.enumString = org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum.HUGO");
            query4.getResultList();
            // test ordinal-mapping of enums
            Query query5 = em
                    .createQuery("SELECT btfa.id FROM BasicTypesFieldAccess btfa WHERE btfa.enumOrdinal = org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum.HUGO");
            query5.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @ToBeInvestigated
    @Test
    public void testQueryWithInExpressionAndEnums() throws Exception {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // test string-mapping of enums
            Query query4 = em
                    .createQuery("SELECT btfa.id FROM BasicTypesFieldAccess btfa WHERE btfa.enumString IN (org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum.HUGO, org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum.EMIL)");
            query4.getResultList();
            // test ordinal-mapping of enums
            Query query5 = em
                    .createQuery("SELECT btfa.id FROM BasicTypesFieldAccess btfa WHERE btfa.enumOrdinal IN (com.sap.jpa.example.UserDefinedEnum.HUGO, com.sap.jpa.example.UserDefinedEnum.EMIL)");
            query5.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testQueryWithAggregateWithHavingExpression() throws Exception {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // this one tests aggregate expression in having condition
            Query query2 = em
                    .createQuery("SELECT  e.firstname FROM Employee e JOIN e.hobbies h GROUP BY e.firstname HAVING COUNT(DISTINCT e.id) = 1");
            query2.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testQueryWithInExpressionAndParameters() throws Exception {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // this one tests for the IN predicate in combination with two parameters
            Query query1 = em.createQuery("select e.id from Employee e where e.id IN (?1, ?2)");
            query1.setParameter(1, Integer.valueOf(1));
            query1.setParameter(2, Integer.valueOf(2));
            query1.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicQuery() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e");
            List result = query.getResultList();
            verify(result.size() == NUMBER_OF_EMPLOYEES, "wrong resultcount for employees");
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * check for a thrown exception in case of an illegal OrderBy-clause
     */
    @ToBeInvestigated
    @Test
    public void testInvalidOrderBy() {
        boolean passed = false;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            em.createQuery("SELECT e.firstname FROM Employee e ORDER BY e.salary");
        } catch (IllegalArgumentException iAEx) {
            passed = true;
        } finally {
            closeEntityManager(em);
        }
        verify(passed, "missing IllegalArgumentException");
    }

    /**
     * check for a thrown exception in case of an illegal OrderBy-clause
     */
    @ToBeInvestigated
    @Test
    public void testInvalidOrderByWithCmr() {
        boolean passed = false;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            em.createQuery("SELECT e.department.name FROM Employee e ORDER BY e.department.id");
        } catch (IllegalArgumentException iAEx) {
            passed = true;
        } finally {
            closeEntityManager(em);
        }
        verify(passed, "missing IllegalArgumentException");
    }

    @Test
    public void testOrderBy() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            em.createQuery("SELECT e FROM Employee e ORDER BY e.salary ASC, e.firstname DESC, e.lastname DESC");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testOrderByCmr() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            em.createQuery("SELECT e.department FROM Employee e ORDER BY e.department.id ASC");
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * check for the right occurence of all names in the result of the query
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSurnamesOfEmployees() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("SELECT e from Employee e WHERE e.lastname=?1");
            Iterator<String> iter = NUMBER_OF_SURNAMES_USED.keySet().iterator();
            while (iter.hasNext()) {
                String surname = iter.next();
                Integer surnamesUsed = NUMBER_OF_SURNAMES_USED.get(surname);
                query.setParameter(1, surname);
                List result = query.getResultList();
                verify(result.size() == surnamesUsed.intValue(), "the number of persons with given name " + surname
                        + " in the result does not match the number of names used when the entities where created.");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGivenNamesOfEmployees() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("SELECT e from Employee e WHERE e.firstname=?1");
            Iterator<String> iter = NUMBER_OF_GIVEN_NAMES_USED.keySet().iterator();
            while (iter.hasNext()) {
                String givenName = iter.next();
                Integer givenNamesUsed = NUMBER_OF_GIVEN_NAMES_USED.get(givenName);
                query.setParameter(1, givenName);
                List result = query.getResultList();
                verify(result.size() == givenNamesUsed.intValue(), "the number of persons with given name " + givenName
                        + " in the result does not match the number of names used when the entities where created.");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNumberOfDrivers() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("SELECT d.firstname FROM MotorVehicle mv JOIN mv.driver d WHERE mv.id = ?1");
            Iterator<Short> iter = MOTORVEHICLES_DRIVERS.keySet().iterator();
            while (iter.hasNext()) {
                Short vehicleID = iter.next();
                // can only be 1 since the one-to-one relationship for drivers
                int numberOfDrivers = 1;
                query.setParameter(1, vehicleID);
                List result = query.getResultList();
                verify(result.size() == numberOfDrivers, "the number of drivers for motorvehicle does not match "
                        + result.size() + " != " + numberOfDrivers);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNumberOfDrivenVehicles() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("SELECT d.firstname FROM MotorVehicle mv JOIN mv.driver d");
            List result = query.getResultList();
            verify(result.size() == MOTORVEHICLES_DRIVERS.size(), "there number of driver-relationships between ");
        } finally {
            closeEntityManager(em);
        }
    }
}
