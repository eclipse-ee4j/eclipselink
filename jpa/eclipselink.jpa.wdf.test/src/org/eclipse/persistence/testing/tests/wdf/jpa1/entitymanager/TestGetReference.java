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

package org.eclipse.persistence.testing.tests.wdf.jpa1.entitymanager;

import java.io.IOException;
import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Account;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CreditCardAccount;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Patent;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.PatentId;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNode;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestGetReference extends JPA1Base {
    private final Department _dep = new Department(1, "eins");
    private final Department _dep2 = new Department(2, "zwei");
    private final Employee _emp = new Employee(7, "first", "last", _dep);
    private final Cubicle _cub = new Cubicle(new Integer(1), new Integer(2), "yellow", _emp);
    private final Patent _pat = new Patent("12345", 2007, "whatever", Date.valueOf("2007-01-01"));
    private final CreditCardAccount _ccacc = new CreditCardAccount();

    @Override
    public void setup() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            _ccacc.setNumber(1);
            _ccacc.setOwner("me");
            _ccacc.setCardNumber(123);
            env.beginTransaction(em);
            em.persist(_dep);
            em.persist(_dep2);
            em.persist(_emp);
            em.persist(_cub);
            em.persist(_pat);
            em.persist(_ccacc);
            em.flush();
            env.commitTransactionAndClear(em);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSimple() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.getReference(Employee.class, new Integer(7));
            verify(em.contains(emp), "Object not managed");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPositivTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.getReference(Employee.class, new Integer(7));
            verify(em.contains(emp), "Object not managed");
            verify(emp.getId() == 7, "wrong id");
            verify(emp.getDepartment().getName().equals("eins"), "wrong department");
            emp = em.getReference(Employee.class, new Integer(7));
            verify(emp.getId() == 7, "wrong id");
            Department dep = em.getReference(Department.class, new Integer(1));
            verify(em.contains(dep), "Object not loaded");
            verify(dep.getId() == 1, "wrong id");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPositivNonTx() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Employee emp = em.getReference(Employee.class, new Integer(7));
            try {
                verify(emp.getId() == 7, "wrong id");
                verify(emp.getDepartment().getName().equals("eins"), "wrong department");
                emp = em.getReference(Employee.class, new Integer(7));
                verify(emp.getId() == 7, "wrong id");
                Department dep = em.getReference(Department.class, new Integer(1));
                verify(dep.getId() == 1, "wrong id");
            } catch (PersistenceException e) {
                if (getEnvironment().usesExtendedPC()) {
                    throw e;
                }
                // transaction-scoped pc might throw exception while trying to
                // load hollow entity outside tx
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPositivTxPropertyAccess() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = em.getReference(Department.class, Integer.valueOf(1));
            verify(em.contains(dep), "Object not managed");
            verify(dep.getId() == 1, "wrong id");
            verify(dep.getName().equals("eins"), "wrong name");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPositivNonTxPropertyAccess() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Department dep = em.getReference(Department.class, Integer.valueOf(1));
            verify(dep.getId() == 1, "wrong id");
            try {
                verify(dep.getName().equals("eins"), "wrong name");
            } catch (PersistenceException e) {
                if (getEnvironment().usesExtendedPC()) {
                    throw e;
                }
                // transaction-scoped pc might throw exception while trying to
                // load hollow entity outside tx
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNegativ() {
        /*
         * If the requested instance does not exist in the database, the EntityNotFoundException is thrown when the instance
         * state is first accessed. (The persistence provider runtime is permitted to throw the EntityNotFoundException when
         * getReference is called.)
         */
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            final JPAEnvironment env = getEnvironment();
            Employee employee = null;
            boolean operationFailed = false;
            env.beginTransaction(em);
            try {
                employee = em.getReference(Employee.class, new Integer(17 + 4));
            } catch (EntityNotFoundException e) {
                // $JL-EXC$ expected behavior
                operationFailed = true;
            }
            if (employee != null) {
                try {
                    employee.getFirstName();
                } catch (EntityNotFoundException e) {
                    // $JL-EXC$ expected behavior
                    operationFailed = true;
                }
            }
            env.rollbackTransactionAndClear(em);
            verify(operationFailed, "no EntityNotFoundException");
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * IllegalArgumentException, if the first argument does not denote an entity type or the second argument is not a valid type
     * for that entity's primary key.
     */
    @Test
    public void testIllegalArguments() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            try {
                em.getReference(String.class, new Integer(17 + 4));
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
            try {
                em.getReference(Employee.class, "illegal key");
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
            try {
                em.getReference(Employee.class, null);
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
            try {
                em.getReference(null, "illegal key");
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindWithCompositeKey() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Integer one = new Integer(1);
            Integer two = new Integer(2);
            CubiclePrimaryKeyClass cubKey = new CubiclePrimaryKeyClass(one, two);
            Cubicle cub = em.getReference(Cubicle.class, cubKey);
            verify(cub.getFloor().equals(one) && cub.getPlace().equals(two), "wrong cubicle");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testEmbeddedIdPropertyAccess() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            PatentId id = _pat.getId();
            Patent pat = em.getReference(Patent.class, id);
            verify(pat.getId().equals(_pat.getId()), "wrong patent");
            verify(pat.getDescription().equals(_pat.getDescription()), "wrong description");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * Clarification with Mike Keith on this: > > 2) Ceck on flush -> another example > > On the DB: > > Employee(1) ->
     * Address(2); > Address(3) may or may not exist > > We perform the folloing operations: > > Employee emp =
     * em.find(Employee.class, 1); > Address newAddress = em.getReference(Address.class, 3); > // depending on the
     * implementation, newAddress may be "hollow" > emp.setAddress(newAddress); > em.flush(); > > In which state is newAddress
     * upon flush? Is the entity > manager required to assert the existence of newAddress upon > flush? Again, this would
     * pervert the idea of lazy loading. > > Should we specifiy that checks on flush should not apply to > "hollow" entities?
     * 
     * 
     * The getReference API comment states that the existence assertion is not expected until the state of the entity is first
     * accessed, so no assertion should be expected in this case. However, we might want to ensure it is in the spec text, not
     * just a comment, and we might also want to qualify that the assertion may only occur when accessing non-identifier state.
     */
    public void testNonExistingEntityInRelation() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Employee employee = null;
            Department nonExistingDepartment = null;
            env.beginTransaction(em);
            employee = em.find(Employee.class, Integer.valueOf(7));
            try {
                nonExistingDepartment = em.getReference(Department.class, Integer.valueOf(999));
            } catch (EntityNotFoundException e) {
                // $JL-EXC$ expected behavior
                return; // getReference checks -> fail fast
            }
            employee.setDepartment(nonExistingDepartment);
            em.flush(); // must succeed now if getReference did not fail fast
        } finally {
            env.rollbackTransactionAndClear(em);
            closeEntityManager(em);
        }
    }

    @Test
    public void testInheritance() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            CreditCardAccount acc = em.getReference(CreditCardAccount.class, Long.valueOf(1));
            // verify method declared by superclass
            verify("me".equals(acc.getOwner()), "wrong owner");
            env.rollbackTransactionAndClear(em);

            env.beginTransaction(em);
            acc = (CreditCardAccount) em.getReference(Account.class, Long.valueOf(1));
            // verify method declared by subclass of Account
            verify(Long.valueOf(123).equals(acc.getCardNumber()), "wrong card number");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNegativInheritance() {
        /*
         * If the requested instance does not exist in the database, the EntityNotFoundException is thrown when the instance
         * state is first accessed. (The persistence provider runtime is permitted to throw the EntityNotFoundException when
         * getReference is called.)
         */
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CreditCardAccount account = null;
            boolean operationFailed = false;
            env.beginTransaction(em);
            try {
                account = em.getReference(CreditCardAccount.class, Long.valueOf(999)); // does not exist
            } catch (EntityNotFoundException e) {
                // $JL-EXC$ expected behavior
                operationFailed = true;
            }
            if (account != null) {
                try {
                    account.getOwner();
                } catch (EntityNotFoundException e) {
                    // $JL-EXC$ expected behavior
                    operationFailed = true;
                }
            }
            env.rollbackTransactionAndClear(em);
            verify(operationFailed, "no EntityNotFoundException");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPersist() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Employee emp = null;
            boolean operationFailed = false;
            env.beginTransaction(em);
            try {
                emp = em.getReference(Employee.class, Integer.valueOf(99));
            } catch (EntityNotFoundException e) {
                // $JL-EXC$ expected behavior
                operationFailed = true;
            }
            env.rollbackTransactionAndClear(em);

            if (emp != null) {
                env.beginTransaction(em);
                try {
                    em.persist(emp);
                    em.flush();
                } catch (PersistenceException e) {
                    // $JL-EXC$ expected behavior
                    operationFailed = true;
                }
                env.rollbackTransactionAndClear(em);
                verify(operationFailed, "persisting a hollow entity succeeded (should fail)");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemove() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = new Employee(11, "homer", "simpson", _dep); // field access, no versioning
            em.persist(emp);
            Department dep = new Department(11, "eleven"); // property access, versioning
            em.persist(dep);
            Review rev = new Review(11, Date.valueOf("2007-01-01"), "eleven"); // field access, versioning
            em.persist(rev);
            env.commitTransactionAndClear(em);

            // update to increase version counter
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(11));
            dep.setName("updated");
            rev = em.find(Review.class, Integer.valueOf(11));
            rev.setReviewText("updated");
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            emp = em.getReference(Employee.class, Integer.valueOf(11));
            em.remove(emp);
            em.flush();
            env.commitTransactionAndClear(em);
            verify(em.find(Employee.class, Integer.valueOf(11)) == null, "employee not removed");

            env.beginTransaction(em);
            dep = em.getReference(Department.class, Integer.valueOf(11));
            em.remove(dep);
            em.flush();
            env.commitTransactionAndClear(em);
            verify(em.find(Department.class, Integer.valueOf(11)) == null, "department not removed");

            env.beginTransaction(em);
            rev = em.getReference(Review.class, Integer.valueOf(11));
            em.remove(rev);
            em.flush();
            env.commitTransactionAndClear(em);
            verify(em.find(Review.class, Integer.valueOf(11)) == null, "review not removed");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveNonExisting() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            try {
                Employee emp = em.getReference(Employee.class, Integer.valueOf(99)); // versioning, entity does not exist
                em.remove(emp);
                em.flush();
                flop("PersistenceException not thrown as expected");
            } catch (PersistenceException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransactionAndClear(em);

            env.beginTransaction(em);
            try {
                Department dep = em.getReference(Department.class, Integer.valueOf(99)); // versioning, entity does not exist
                em.remove(dep);
                em.flush();
                flop("PersistenceException not thrown as expected");
            } catch (PersistenceException e) {
                // $JL-EXC$ expected behavior
            }
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMerge() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Employee emp = null;

            // case 1: hollow entity is managed
            env.beginTransaction(em);
            emp = em.getReference(Employee.class, Integer.valueOf(7));
            em.merge(emp);
            em.flush();
            env.rollbackTransactionAndClear(em);

            // case 2: hollow entity is detached
            emp = em.getReference(Employee.class, Integer.valueOf(7));
            boolean shouldFail = isHollow(emp);
            env.beginTransaction(em);
            try {
                em.clear();
                verify(!em.contains(emp), "emp not detached");
                em.merge(emp);
                em.flush();
                if (shouldFail) {
                    flop("merge of a detached hollow entity succeeded (should fail)");
                }
            } catch (PersistenceException e) {
                if (!shouldFail) {
                    throw e;
                }
            } catch (IllegalArgumentException e) {
                if (!shouldFail) {
                    throw e;
                }
            }
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeIntoHollow() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        int id = 20;
        try {

            env.beginTransaction(em);
            Employee emp = new Employee(id, "first", "last", _dep);
            em.persist(emp);
            env.commitTransactionAndClear(em);

            Employee empDetached = em.find(Employee.class, Integer.valueOf(id));
            em.clear(); // detach entity
            empDetached.setFirstName("updated");

            env.beginTransaction(em);
            emp = em.getReference(Employee.class, Integer.valueOf(id));
            em.merge(empDetached);
            em.flush();
            env.commitTransactionAndClear(em);

            emp = em.find(Employee.class, Integer.valueOf(id));
            verify("updated".equals(emp.getFirstName()), "wrong first name: " + emp.getFirstName());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRefresh() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Employee emp = null;
            env.beginTransaction(em);
            emp = em.getReference(Employee.class, Integer.valueOf(7));
            em.refresh(emp);
            em.flush();
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testReadLock() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = em.getReference(Department.class, Integer.valueOf(1));
            em.lock(dep, LockModeType.READ);
            em.flush();
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testWriteLock() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = em.find(Department.class, Integer.valueOf(1));
            int version = dep.getVersion();
            env.rollbackTransactionAndClear(em);

            env.beginTransaction(em);
            dep = em.getReference(Department.class, Integer.valueOf(1));
            em.lock(dep, LockModeType.WRITE);
            em.flush();
            verify(dep.getVersion() > version, "version not incremented");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testCascadeRemove() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            CascadingNode parent = new CascadingNode(1, null);
            CascadingNode child = new CascadingNode(2, parent);
            em.persist(parent);
            em.persist(child);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            parent = em.getReference(CascadingNode.class, Integer.valueOf(1));
            em.remove(parent);
            em.flush();
            env.commitTransactionAndClear(em);
            parent = em.find(CascadingNode.class, Integer.valueOf(1));
            verify(parent == null, "parent not removed");
            child = em.find(CascadingNode.class, Integer.valueOf(2));
            verify(child == null, "child not removed");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testCascadePersistOnFlush() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            CascadingNode parent = new CascadingNode(11, null);
            CascadingNode child = new CascadingNode(12, parent);
            em.persist(parent);
            em.persist(child);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            em.getReference(CascadingNode.class, Integer.valueOf(11));
            em.flush();
            env.rollbackTransactionAndClear(em);

            env.beginTransaction(em); // cleanup in order -> FK
            em.merge(child);
            em.merge(parent);
            em.remove(child);
            em.remove(parent);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSerializeLoaded() throws IOException, ClassNotFoundException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: entity with standard serialization
            Employee emp = em.getReference(Employee.class, new Integer(7));
            // load entity
            emp.getFirstName();
            Employee resultEmp = AbstractBaseTest.serializeDeserialize(emp);
            verify(emp.getId() == resultEmp.getId(), "wrong id: " + resultEmp.getId());
            verify(emp.getFirstName().equals(resultEmp.getFirstName()), "wrong first name: " + resultEmp.getFirstName());
            em.clear();

            // case 2: entity with writeReplace
            Department dep = em.getReference(Department.class, new Integer(1));
            // load entity
            dep.getName();
            Department resultDep = AbstractBaseTest.serializeDeserialize(dep);
            verify(dep.getId() == resultDep.getId(), "wrong id: " + resultDep.getId());
            verify(Department.HUGO.equals(resultDep.getName()), "wrong name: " + resultDep.getName());
            em.clear();

            // case 3: related entities
            emp = em.getReference(Employee.class, new Integer(7));
            emp.getFirstName();
            dep = em.getReference(Department.class, new Integer(2));
            dep.getName();
            emp.setDepartment(dep);
            Cubicle cub = em.getReference(Cubicle.class, new CubiclePrimaryKeyClass(1, 2));
            cub.getColor();
            emp.setCubicle(cub);
            cub.setEmployee(emp);
            Cubicle resultCub = AbstractBaseTest.serializeDeserialize(cub);
            resultDep = resultCub.getEmployee().getDepartment();
            verify(Department.HUGO.equals(resultDep.getName()), "wrong name: " + resultDep.getName());
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSerializeHollow() throws IOException, ClassNotFoundException {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            // case 1: entity with standard serialization
            Employee emp = em.getReference(Employee.class, new Integer(7));
            boolean shouldFail = isHollow(emp);
            try {
                Employee resultEmp = AbstractBaseTest.serializeDeserialize(emp);
                verify(emp.getId() == resultEmp.getId(), "wrong id: " + resultEmp.getId());
                verify(emp.getFirstName().equals(resultEmp.getFirstName()), "wrong first name: " + resultEmp.getFirstName());
            } catch (PersistenceException e) {
                if (!shouldFail) {
                    throw e;
                }
            }
            em.clear();

            // case 2: entity with writeReplace
            Department dep = em.getReference(Department.class, new Integer(1));
            shouldFail = isHollow(dep);
            try {
                Department resultDep = AbstractBaseTest.serializeDeserialize(dep);
                verify(dep.getId() == resultDep.getId(), "wrong id: " + resultDep.getId());
                verify(Department.HUGO.equals(resultDep.getName()), "wrong name: " + resultDep.getName());
            } catch (PersistenceException e) {
                if (!shouldFail) {
                    throw e;
                }
            }
            em.clear();
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    private boolean isHollow(Object entity) {
        return false; // TODO move to EclipseLink
        // return entity instanceof LazilyLoadable && ((LazilyLoadable)
        // entity)._isPending();
    }
}
