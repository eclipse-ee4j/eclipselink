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

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Car;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department.KrassDep;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee.KrassEmp;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.MotorVehicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.TransmissionType;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Truck;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum;
import org.junit.Test;

public class TestArguments extends QueryTest {

    private final Set<Department> ALL_DEPARTMENTS = new HashSet<Department>();
    private final Department dep10 = new Department(10, "ten");
    private final Department dep20 = new Department(20, "twenty");

    private void init() throws SQLException {
        clearAllTables();
        ALL_DEPARTMENTS.add(dep10);
        ALL_DEPARTMENTS.add(dep20);
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee knut = new Employee(1, "Knut", "Maier", dep10);
            Employee fred = new Employee(2, "Fred", "Schmidt", null);
            Cubicle green = new Cubicle(Integer.valueOf(1), Integer.valueOf(2), "green", knut);
            knut.setCubicle(green);
            em.persist(dep10);
            em.persist(dep20);
            em.persist(green);
            em.persist(knut);
            em.persist(fred);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL})
    public void testArgumentInArithmeticExpr() throws SQLException {
        init();

        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query updateQuery = em
                    .createQuery("UPDATE Employee e SET e.salary = e.salary*(1+(:percent/100)) WHERE EXISTS (SELECT p FROM e.projects p WHERE p.name LIKE :projectName)");

            updateQuery.setParameter("percent", new Integer(15));
            updateQuery.setParameter("projectName", "testing project");

            updateQuery.executeUpdate();
            getEnvironment().commitTransaction(em);
        } catch (Exception ex) {
            getEnvironment().rollbackTransaction(em);
            throw new RuntimeException(ex);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testLongVarBinaryParameters() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // the mapped query is not executable on DB hence it will only be created and the parameter is set to test type
            // validation
            Query q1 = em
                    .createQuery("SELECT bfa.primitiveByteArray2Blob FROM BasicTypesFieldAccess bfa WHERE bfa.wrapperByteArray2Longvarbinary IS NULL AND (?1) IS NULL AND (?2) IS NULL");
            q1.setParameter(1, new Byte[10]);
            q1.setParameter(2, new byte[10]);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSinglePk() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e where e.department = :dep");
            query.setParameter("dep", dep10);
            List<?> result = query.getResultList();
            Iterator<?> iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Employee, "wrong instance: " + object.getClass().getName());
            Employee employee = (Employee) object;
            verify(employee.getId() == 1, "wrong id: " + employee.getId());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("boxing")
    @Test
    public void testCompoundPk() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Cubicle cubicle = em.find(Cubicle.class, new CubiclePrimaryKeyClass(1, 2));
            Query query = em.createQuery("select e from Employee e where e.cubicle = :cub");
            query.setParameter("cub", cubicle);
            List<?> result = query.getResultList();
            Iterator<?> iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof Employee, "wrong instance: " + object.getClass().getName());
            Employee employee = (Employee) object;
            verify(employee.getId() == 1, "wrong id: " + employee.getId());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testInvalidParameterHandling() {
        assertInvalidQuery("SELECT e FROM Employee e WHERE e.id = ?");
    }

    @Test
    public void testSubclassParameter() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.department=?1");
            q.setParameter(1, new KrassDep("foo"));
            q.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testIllegalSubclassParameter() {
        boolean errorCaught = false;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.department=?1");
            q.setParameter(1, new KrassEmp("foo"));
            q.getResultList();
        } catch (IllegalArgumentException iaex) {
            errorCaught = true;
        } finally {
            closeEntityManager(em);
        }
        assertTrue("Unable to detect illegal typed input parameter.", errorCaught);
    }

    @SuppressWarnings("boxing")
    @Test
    public void testEnumParameters() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            MotorVehicle motorVehicle = new MotorVehicle();
            motorVehicle.setTransmissionType(TransmissionType.AUTOMATIC);
            Truck truck = new Truck();
            truck.setTransmissionType(TransmissionType.STICK_SHIFT);
            Car car = new Car();
            em.persist(motorVehicle);
            em.persist(truck);
            em.persist(car);
            getEnvironment().commitTransactionAndClear(em);
            short mvId = motorVehicle.getId();
            short truckId = truck.getId();
            Query query = em.createQuery("select m from MotorVehicle m where m.transmissionType = :tr");
            query.setParameter("tr", TransmissionType.AUTOMATIC);
            List<?> result = query.getResultList();
            Iterator<?> iter = result.iterator();
            verify(iter.hasNext(), "no results");
            Object object = iter.next();
            verify(object instanceof MotorVehicle, "wrong instance: " + object.getClass().getName());
            MotorVehicle vehicle = (MotorVehicle) object;
            verify(vehicle.getId() == mvId, "wrong id: " + vehicle.getId());
            verify(!iter.hasNext(), "too many rows");
            query.setParameter("tr", TransmissionType.STICK_SHIFT);
            result = query.getResultList();
            iter = result.iterator();
            verify(iter.hasNext(), "no results");
            object = iter.next();
            verify(object instanceof MotorVehicle, "wrong instance: " + object.getClass().getName());
            vehicle = (MotorVehicle) object;
            verify(vehicle.getId() == truckId, "wrong id: " + vehicle.getId());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    protected void assertValidParameterForBasicTypesFieldAccess(final String fieldname, Object value) {
        EntityManager em = getEnvironment().getEntityManager();
        // queries
        List<Query> orderedParamQueries = new ArrayList<Query>();
        List<Query> namedParamQueries = new ArrayList<Query>();

        orderedParamQueries
                .add(em.createQuery("SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa." + fieldname + " = ?1"));
        orderedParamQueries.add(em.createQuery("UPDATE BasicTypesFieldAccess btfa SET btfa." + fieldname
                + " = ?1 WHERE btfa.id=1"));

        namedParamQueries.add(em.createQuery("SELECT btfa FROM BasicTypesFieldAccess btfa WHERE btfa." + fieldname
                + " = :param1"));
        namedParamQueries.add(em.createQuery("UPDATE BasicTypesFieldAccess btfa SET btfa." + fieldname
                + " = :param1 WHERE btfa.id=1"));

        for (Query q : orderedParamQueries) {
            if (Calendar.class.isAssignableFrom(value.getClass())) {
                q.setParameter(1, (Calendar) value, TemporalType.TIMESTAMP);
            } else if (value.getClass().isAssignableFrom(Date.class)) {
                q.setParameter(1, (Date) value, TemporalType.TIMESTAMP);
            } else {
                q.setParameter(1, value);
            }
        }

        for (Query q : namedParamQueries) {
            if (Calendar.class.isAssignableFrom(value.getClass())) {
                q.setParameter("param1", (Calendar) value, TemporalType.TIMESTAMP);
            } else if (value.getClass().isAssignableFrom(Date.class)) {
                q.setParameter("param1", (Date) value, TemporalType.TIMESTAMP);
            } else {
                q.setParameter("param1", value);
            }
        }

    }

    // primitive types
    @Test
    public void testPrimitiveBoolean() {
        assertValidParameterForBasicTypesFieldAccess("primitiveBoolean", true);
    }

    @Test
    public void testPrimitiveByte() {
        assertValidParameterForBasicTypesFieldAccess("primititveByte", new Byte((byte) 2));
    }

    @Test
    public void testPrimitiveChar() {
        assertValidParameterForBasicTypesFieldAccess("primitiveChar", 'c');
    }

    @Test
    public void testPrimitiveShort() {
        assertValidParameterForBasicTypesFieldAccess("primitiveShort", (short) 2);
    }

    @Test
    public void testPrimitiveInt() {
        assertValidParameterForBasicTypesFieldAccess("primitiveInt", 1);
    }

    @Test
    public void testPrimitiveLong() {
        assertValidParameterForBasicTypesFieldAccess("primitiveLong", 1L);
    }

    @Test
    public void testPrimitiveFloat() {
        assertValidParameterForBasicTypesFieldAccess("primitiveFloat", 0.2F);
    }

    @Test
    public void testPrimitiveDouble() {
        assertValidParameterForBasicTypesFieldAccess("primitiveDouble", 0.1234);
    }

    // wrappers of primitive types
    @Test
    public void testWrapperBoolean() {
        assertValidParameterForBasicTypesFieldAccess("wrapperBoolean", new Boolean(true));
    }

    @Test
    public void testWrapperByte() {
        assertValidParameterForBasicTypesFieldAccess("wrapperByte", new Byte((byte) 2));
    }

    @Test
    public void testWrapperCharacter() {
        assertValidParameterForBasicTypesFieldAccess("wrapperCharacter", new Character('c'));
    }

    @Test
    public void testWrapperShort() {
        assertValidParameterForBasicTypesFieldAccess("wrapperShort", new Short((short) 1));
    }

    @Test
    public void testWrapperInteger() {
        assertValidParameterForBasicTypesFieldAccess("wrapperInteger", new Integer(1));
    }

    @Test
    public void testWrapperLong() {
        assertValidParameterForBasicTypesFieldAccess("wrapperLong", new Long(1L));
    }

    @Test
    public void testWrapperDouble() {
        assertValidParameterForBasicTypesFieldAccess("wrapperDouble", new Double(0.00));
    }

    @Test
    public void testWrapperFloat() {
        assertValidParameterForBasicTypesFieldAccess("wrapperFloat", new Float(1F));
    }

    // immutable reference types
    @Test
    public void testString2Varchar() {
        assertValidParameterForBasicTypesFieldAccess("string2Varchar", "foo");
    }

    @Test
    public void testString2Clob() {
        assertValidParameterForBasicTypesFieldAccess("string2Clob", "bar");
    }

    @Test
    public void testBigDecimal() {
        assertValidParameterForBasicTypesFieldAccess("bigDecimal", new BigDecimal(1));
    }

    @Test
    public void testBigInteger() {
        assertValidParameterForBasicTypesFieldAccess("bigInteger", new BigInteger(1, new Random(10L)));
    }

    // mutable types
    @Test
    public void testUtilDate() {
        assertValidParameterForBasicTypesFieldAccess("utilDate", new Date(System.currentTimeMillis()));
    }

    @Test
    public void testUtilCalendar() {
        assertValidParameterForBasicTypesFieldAccess("utilCalendar", Calendar.getInstance());
    }

    @Test
    public void testSqlDate() {
        assertValidParameterForBasicTypesFieldAccess("sqlDate", new java.sql.Date(System.currentTimeMillis()));
    }

    @Test
    public void testSqlTime() {
        assertValidParameterForBasicTypesFieldAccess("sqlTime", new java.sql.Time(System.currentTimeMillis()));
    }

    @Test
    public void testSqlTimestamp() {
        assertValidParameterForBasicTypesFieldAccess("sqlTimestamp", new java.sql.Timestamp(System.currentTimeMillis()));
    }

    // arrays
    @Test
    public void testPrimitiveByteArray2Binary() {
        assertValidParameterForBasicTypesFieldAccess("primitiveByteArray2Binary", new byte[] { (byte) 1, (byte) 1 });
    }

    @Test
    public void testPrimitiveByteArray2Longvarbinary() {
        assertValidParameterForBasicTypesFieldAccess("primitiveByteArray2Longvarbinary", new byte[] { (byte) 1, (byte) 1 });
    }

    @Test
    public void testPrimitiveByteArray2Blob() {
        assertValidParameterForBasicTypesFieldAccess("primitiveByteArray2Blob", new byte[] { (byte) 1, (byte) 1 });
    }

    @Test
    public void testPrimitiveCharArray2Varchar() {
        assertValidParameterForBasicTypesFieldAccess("primitiveCharArray2Varchar", new char[] { 'c', 'b' });
    }

    @Test
    public void testPrimitiveCharArray2Clob() {
        assertValidParameterForBasicTypesFieldAccess("primitiveCharArray2Clob", new char[] { 'c', 'b' });
    }

    @Test
    public void testWrapperByteArray2Binary() {
        assertValidParameterForBasicTypesFieldAccess("wrapperByteArray2Binary", new Byte[] { 'c', 'b' });
    }

    @Test
    public void testWrapperByteArray2Longvarbinary() {
        assertValidParameterForBasicTypesFieldAccess("wrapperByteArray2Longvarbinary", new Byte[] { 'c', 'b' });
    }

    @Test
    public void testWrapperByteArray2Blob() {
        assertValidParameterForBasicTypesFieldAccess("wrapperByteArray2Blob", new Byte[] { 'c', 'b' });
    }

    @Test
    public void testWrapperCharArray2Varchar() {
        assertValidParameterForBasicTypesFieldAccess("wrapperCharacterArray2Varchar", new Character[] { 'c', 'b' });
    }

    @SuppressWarnings("boxing")
    @Test
    public void testWrapperCharArray2Clob() {
        assertValidParameterForBasicTypesFieldAccess("wrapperCharacterArray2Clob", new Character[] { 'c', 'b' });
    }

    @Test
    public void testEnumString() {
        assertValidParameterForBasicTypesFieldAccess("enumString", UserDefinedEnum.EMIL);
    }

    @Test
    public void testEnumOrdinal() {
        assertValidParameterForBasicTypesFieldAccess("enumOrdinal", UserDefinedEnum.HUGO);
    }

}
