/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 Contributors to the Eclipse Foundation. All rights reserved
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.datatypes.DataTypesTableCreator;
import org.eclipse.persistence.testing.models.jpa.datatypes.WrapperTypes;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Door;
import org.eclipse.persistence.testing.models.jpa.advanced.Room;

/**
 * <p>
 * <b>Purpose</b>: Test Entity alias generation EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for alias generation EJBQL functionality
 * </ul>
 * @see EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
public class JUnitJPQLJakartaDataNoAliasTest extends JUnitTestCase {

    private static final String STRING_DATA = "A String";
    private static final String STRING_DATA_LIKE_EXPRESSION = "A%"; // should match STRING_DATA
    private static final Room[] ROOMS = new Room[]{
        null, // Skip array index 0
        aRoom(1, 1, 1, 1, Room.Status.FREE, 1001, 100, 100, new Date(1L)),
        aRoom(2, 1, 1, 1, Room.Status.FREE, 2001, 200, 200, new Date(2L)),
        aRoom(3, 1, 1, 1, Room.Status.OCCUPIED, 3001, 300, 300, new Date(3L)),
        aRoom(4, 1, 1, 1, Room.Status.OCCUPIED, 4001, 400, 400, new Date(4L)),
    };
    private static final long ROOMS_COUNT = ROOMS.length - 1; // we ignore the first one with index 0

    private static int wrapperId;

    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLJakartaDataNoAliasTest() {
        super();
    }

    public JUnitJPQLJakartaDataNoAliasTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced-jakartadata";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLInheritanceTest");
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSetup"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAlias"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasOBJECT"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasCOUNT"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasCASTCOUNT"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testCorrectAliases"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasWhere"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasFromWhere"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasFromWhereAnd"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasFromWhereAndUPPER"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasWhereWithBraces01"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasWhereWithBraces02"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testNoAliasWhereWithBraces03"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testGeneratedSelectNoAliasFromWhere"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testGeneratedSelect"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testUpdateQueryLengthInAssignmentAndExpression"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryLengthInAssignmentAndExpression"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryImplicitThisVariableInPath"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryImplicitThisVariableInAggregateFunctionPath"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryImplicitThisVariableInArithmeticExpression"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryImplicitThisVariableAndEnum"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryImplicitThisVariableAndRelationalAttributes"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryIdFunctionNestedInArithmeticFunction_Where"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testSelectQueryIdFunctionNestedInStringFunction_Where"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testUpdateImplicitVariableInArithmeticExpression"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testDeleteQueryLengthInExpressionOnLeft"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testDeleteQueryLengthInExpressionOnRight"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("tesUpdateQueryWithThisVariable"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testThisVariableInPathExpressionUpdate"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testThisVariableInPathAndIdFunctionExpressionUpdate"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testThisVariableInPathExpressionDelete"));
        suite.addTest(new JUnitJPQLJakartaDataNoAliasTest("testThisVariableInLikeExpressionDelete"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();
        final ServerSession session = getPersistenceUnitServerSession();

        //set the session for the comparer to use
        comparer.setSession(session);

        new DataTypesTableCreator().replaceTables(session);
        new AdvancedTableCreator().replaceTables(session);
        clearCache();
        resetWrapperTypes();
    }

    public void testNoAlias() {
        EntityManager em = createEntityManager();

        WrapperTypes wrapperTypes = (WrapperTypes) em.createQuery("SELECT this FROM WrapperTypes").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAlias Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasOBJECT() {
        EntityManager em = createEntityManager();

        WrapperTypes wrapperTypes = (WrapperTypes) em.createQuery("SELECT OBJECT(this) FROM WrapperTypes").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasOBJECT Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasCOUNT() {
        EntityManager em = createEntityManager();

        long result = em.createQuery("SELECT COUNT(this) FROM WrapperTypes", Long.class).getSingleResult();
        Assert.assertTrue("NoAliasCOUNT Test Failed", result > 0L);
    }

    public void testNoAliasCASTCOUNT() {
        EntityManager em = createEntityManager();

        String result = em.createQuery("SELECT CAST(COUNT(this) AS CHAR) FROM WrapperTypes", String.class).getSingleResult();
        Assert.assertTrue("NoAliasCOUNT Test Failed", result.length() > 0);
    }

    public void testCorrectAliases() {
        EntityManager em = createEntityManager();

        WrapperTypes wrapperTypes = (WrapperTypes) em.createQuery("SELECT this FROM WrapperTypes this").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("CorrectAliases Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasWhere() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("SELECT this FROM WrapperTypes this WHERE id = :idParam");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasWhere Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasFromWhere() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("SELECT this FROM WrapperTypes WHERE id = :idParam");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasFromWhere Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasFromWhereAnd() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("SELECT this FROM WrapperTypes WHERE id = :idParam AND stringData = :stringDataParam");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        wrapperTypesQuery.setParameter("stringDataParam", STRING_DATA);
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasFromWhereAnd Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasFromWhereAndUPPER() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("SELECT this FROM WrapperTypes WHERE id = :idParam AND UPPER(stringData) = :stringDataParam");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        wrapperTypesQuery.setParameter("stringDataParam", STRING_DATA.toUpperCase());
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasFromWhereAndUPPER Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasWhereWithBraces01() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("FROM WrapperTypes WHERE (id = :idParam)");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasWhereWithBraces01 Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasWhereWithBraces02() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("FROM WrapperTypes WHERE ((id = :idParam))");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasWhereWithBraces02 Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasWhereWithBraces03() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("FROM WrapperTypes WHERE (id <= :idParam AND UPPER(stringData) NOT LIKE UPPER(:stringParam) AND UPPER(stringData) NOT LIKE 'dgdgs') ORDER BY id DESC, stringData");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        wrapperTypesQuery.setParameter("stringParam", "asdadads");
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasWhereWithBraces03 Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }


    public void testGeneratedSelect() {
        EntityManager em = createEntityManager();

        WrapperTypes wrapperTypes = (WrapperTypes) em.createQuery("FROM WrapperTypes").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAlias Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testGeneratedSelectNoAliasFromWhere() {
        EntityManager em = createEntityManager();

        Query wrapperTypesQuery = em.createQuery("FROM WrapperTypes WHERE id = :idParam");
        wrapperTypesQuery.setParameter("idParam", wrapperId);
        WrapperTypes wrapperTypes = (WrapperTypes) wrapperTypesQuery.getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes) getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("GeneratedSelectNoAliasFromWhere Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testUpdateQueryLengthInAssignmentAndExpression() {
        resetRooms(false);
        long numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "UPDATE Room SET length = length + 1").executeUpdate());
        assertTrue("All rooms should be updated", numberOfChanges == ROOMS_COUNT);

        long numberOfRoomsWithLengthChanged = getAllRooms()
                .filter(room -> room.getLength() == 2)
                .count();
        assertTrue("All rooms should have increased length", numberOfRoomsWithLengthChanged == ROOMS_COUNT);
    }

    public void testSelectQueryLengthInAssignmentAndExpression() {
        resetRooms(false);
        List<Room> roomsWithIdOne = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "SELECT this FROM Room WHERE id + length = length + 1", Room.class).getResultList());
        assertTrue("Number of rooms with ID = 1", roomsWithIdOne.size() == 1);
    }


    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2182
    public void testSelectQueryImplicitThisVariableInPath() {
        resetRooms(false);
        List<Integer> roomsLengthWithIdOne = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "SELECT length FROM Room WHERE length IS NOT NULL AND id = :idParam", Integer.class)
                .setParameter("idParam", ROOMS[1].getId())
                .getResultList());
        assertTrue("Number of rooms with ID = 1", roomsLengthWithIdOne.size() == 1);
        assertTrue("Room ID = " + ROOMS[1].getId() + " has length of ", roomsLengthWithIdOne.get(0) == ROOMS[1].getLength());
    }


    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2192
    public void testSelectQueryImplicitThisVariableInAggregateFunctionPath() {
        resetRooms(false);
        Integer roomsMaxWidth = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                        "SELECT MAX(width) FROM Room", Integer.class)
                .getSingleResult());
        assertTrue("Some Room with max width exist ", roomsMaxWidth != null);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2247
    public void testSelectQueryImplicitThisVariableInArithmeticExpression() {
        resetRooms(false);
        int roomCapacity = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                        "SELECT length * width * height  FROM Room WHERE id = :idParam", Integer.class)
                .setParameter("idParam", ROOMS[1].getId())
                .getSingleResult());
        assertEquals(ROOMS[1].getLength() * ROOMS[1].getWidth() * ROOMS[1].getHeight(), roomCapacity);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2185
    public void testSelectQueryImplicitThisVariableAndEnum() {
        resetRooms(false);
        List<Room> rooms = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                        "SELECT NEW org.eclipse.persistence.testing.models.jpa.advanced.Room(id, width, length, height, status) " +
                                "FROM Room " +
                                "WHERE id = :idParam AND status=org.eclipse.persistence.testing.models.jpa.advanced.Room.Status.FREE " +
                                "ORDER BY width DESC, id ASC",
                        Room.class)
                .setParameter("idParam", ROOMS[1].getId())
                .getResultList());
        assertEquals(ROOMS[1], rooms.get(0));
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2286
    public void testSelectQueryIdFunctionNestedInArithmeticFunction_Where() {
        final String typeName = getPlatform().isMySQL() ? "UNSIGNED": "INTEGER";
        resetRooms(false);
        Room room = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                        "SELECT this " +
                                "FROM Room " +
                                "WHERE ABS(CAST(ID(this) AS " + typeName + "))= :idParam",
                        Room.class)
                .setParameter("idParam", ROOMS[1].getId())
                .getSingleResult());
        assertEquals(ROOMS[1], room);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2286
    public void testSelectQueryIdFunctionNestedInStringFunction_Where() {
        resetRooms(false);
        Room room = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                        "SELECT this " +
                                "FROM Room " +
                                "WHERE LOWER(CAST(ID(this) AS CHAR))= :idParam",
                        Room.class)
                .setParameter("idParam", String.valueOf(ROOMS[1].getId()))
                .getSingleResult());
        assertEquals(ROOMS[1], room);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2188
    public void testSelectQueryImplicitThisVariableAndRelationalAttributes() {
        resetRooms(false);
        List<Door> doors = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                                "FROM Door " +
                                "WHERE room.id = :idParam " +
                                "ORDER BY width",
                        Door.class)
                .setParameter("idParam", ROOMS[1].getId())
                .getResultList());
        assertEquals(ROOMS[1].getDoors().get(0).getId(), doors.get(0).getId());
    }

    public void testUpdateImplicitVariableInArithmeticExpression() {
        resetRooms(false);
        int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "UPDATE Room SET width = width * :widthMultiplicator WHERE id = :id")
                .setParameter("widthMultiplicator", 5)
                .setParameter("id", 1)
                .executeUpdate());
        assertTrue("Number of rooms with ID = 1 updated", numberOfChanges == 1);
        int roomWidth = findRoomById(1).getWidth();
        assertTrue("Room ID = 1 has width of ", roomWidth == 5);
    }

    public void testDeleteQueryLengthInExpressionOnLeft() {
        resetRooms(true);
        assertTrue("Number of remaining rooms", getAllRooms().count() == ROOMS_COUNT);
        int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "DELETE FROM Room WHERE length = id - 1").executeUpdate());
        long allRoomsCount = getAllRooms().count();
        assertTrue("Number of rooms with ID = 1 deleted", numberOfChanges == 1);
        assertTrue("Number of remaining rooms", allRoomsCount == ROOMS_COUNT - 1);
    }

    public void testDeleteQueryLengthInExpressionOnRight() {
        resetRooms(true);
        int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "DELETE FROM Room WHERE id = length + 1").executeUpdate());
        assertTrue("Number of rooms with ID = 1 deleted", numberOfChanges == 1);
        assertTrue("Number of remaining rooms", getAllRooms().count() == ROOMS_COUNT - 1);
    }

    public void tesUpdateQueryWithThisVariable() {
        resetRooms(false);
        long numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "UPDATE Room SET length = this.length + 1").executeUpdate());
        assertTrue("All rooms should be updated", numberOfChanges == ROOMS_COUNT);

        long numberOfRoomsWithLengthChanged = getAllRooms()
                .filter(room -> room.getLength() == 2)
                .count();
        assertTrue("All rooms should have increased length", numberOfRoomsWithLengthChanged == ROOMS_COUNT);
    }


    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2197
    public void testThisVariableInPathExpressionUpdate() {
        resetRooms(false);
        int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "UPDATE Room SET this.length = 10 WHERE this.id = 1").executeUpdate());
        assertTrue("Number of rooms with ID = 1 modified is " + numberOfChanges, numberOfChanges == 1);
        int length = findRoomById(1).getLength();
        assertTrue("Length of room with ID = 1 is " + length, length == 10);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2184
    public void testThisVariableInPathAndIdFunctionExpressionUpdate() {
        resetRooms(false);
        int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "UPDATE Room SET length = 10 WHERE ID(this) = :idParam")
                .setParameter("idParam", 1)
                .executeUpdate());
        assertTrue("Number of rooms with ID = 1 modified is " + numberOfChanges, numberOfChanges == 1);
        int length = findRoomById(1).getLength();
        assertTrue("Length of room with ID = 1 is " + length, length == 10);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2198
    public void testThisVariableInPathExpressionDelete() {
        resetRooms(true);
        int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "DELETE FROM Room WHERE this.length < 10").executeUpdate());
        assertTrue("Number of rooms deleted is " + numberOfChanges, numberOfChanges == ROOMS_COUNT);
        long numberOfRemainingRooms = getAllRooms().count();
        assertTrue("Number of remaining rooms is " + numberOfRemainingRooms, numberOfRemainingRooms == 0);
    }

    // Covers https://github.com/eclipse-ee4j/eclipselink/issues/2199
    public void testThisVariableInLikeExpressionDelete() {
        try {
            int numberOfChanges = getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                    "DELETE FROM WrapperTypes WHERE this.stringData LIKE '" + STRING_DATA_LIKE_EXPRESSION + "'").executeUpdate());
            assertTrue("Number of wrapper types deleted", numberOfChanges == 1);
                long remainingTypes = getAllWrapperTypes().count();
            assertTrue("Number of remaining wrapper types is " + remainingTypes, remainingTypes == 0);
        } finally {
            resetWrapperTypes();
        }
    }

    private Stream<Room> getAllRooms() {
        return getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "SELECT r FROM Room r", Room.class).getResultStream());
    }

    private Room findRoomById(int i) {
        return getEntityManagerFactory().callInTransaction(em -> {
            return em.find(Room.class, 1);
        });
    }

    private static Room aRoom(int id, int width, int length, int height, Room.Status status, int doorId, int doorWidth, int doorHeight, Date doorWarrantyDate) {
        Room room = new Room();
        room.setId(id);
        room.setWidth(width);
        room.setLength(length);
        room.setHeight(height);
        room.setStatus(status);
        List<Door> doors = new ArrayList<>();
        doors.add(new Door(doorId, doorWidth, doorHeight, room, doorWarrantyDate));
        room.setDoors(doors);
        return room;
    }

    private void resetRooms(boolean noDoors) {
        getEntityManagerFactory().runInTransaction(em -> {
            em.createQuery("DELETE FROM Door ").executeUpdate();
            em.createQuery("DELETE FROM Room").executeUpdate();
            for (int i = 1; i < ROOMS.length; i++) {
                em.persist(ROOMS[i]);
            }
            if (noDoors) {
                em.createQuery("DELETE FROM Door ").executeUpdate();
            }
        });
    }

    private Stream<WrapperTypes> getAllWrapperTypes() {
        return getEntityManagerFactory().callInTransaction(em -> em.createQuery(
                "SELECT wt FROM WrapperTypes wt", WrapperTypes.class).getResultStream());
    }

    private void resetWrapperTypes() {
        getEntityManagerFactory().runInTransaction(em -> {
            em.createQuery("DELETE FROM WrapperTypes").executeUpdate();
            WrapperTypes wt = new WrapperTypes(BigDecimal.ZERO, BigInteger.ZERO, Boolean.FALSE,
                    Byte.valueOf("0"), 'A', Short.valueOf("0"),
                    0, 0L, 0.0f, 0.0, STRING_DATA);
            em.persist(wt);
            wrapperId = wt.getId();
        });
    }

}
