/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     19/07/2018 - Jody Grassel 
 *       - 537795 : CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
 ******************************************************************************/

package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.Dto01;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestQueryCase {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { EntityTbl01.class }, 
            properties = { 
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;
    
    private boolean populated = false;
    
    @Test
    public void testQueryCase1() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        
        populate();
        
        em.getTransaction().begin();
        
        TypedQuery<EntityTbl01> query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                    + "WHERE t.itemString1 = ( "
                        + "CASE t.itemInteger1 "
                            + "WHEN 1000 THEN '047010' "
                            + "WHEN 100 THEN '023010' "
                            + "ELSE '033020' "
                        + "END )", EntityTbl01.class);
        
        List<EntityTbl01> dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(0, dto01.size());
        
        em.getTransaction().rollback();
    }
    
    @Test
    public void testQueryCase2() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        
        populate();
        
        em.getTransaction().begin();
        
        TypedQuery<EntityTbl01> query = em.createQuery(""
                + "SELECT t FROM EntityTbl01 t "
                    + "WHERE t.itemString1 = ( "
                        + "CASE "
                            + "WHEN t.itemInteger1 = 1000 THEN '047010' "
                            + "WHEN t.itemInteger1 = 100 THEN '023010' "
                            + "ELSE '033020' "
                        + "END )", EntityTbl01.class);
        
        List<EntityTbl01> dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(0, dto01.size());
        
        em.getTransaction().rollback();
    }
    
    @Test
    public void testQueryCase3() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        
        populate();
        
        em.getTransaction().begin();
        
        TypedQuery<Dto01> query = em.createQuery(""
                + "SELECT new org.eclipse.persistence.jpa.test.query.model.Dto01("
                    + "t.itemString1, "               // String
                    + "CASE t.itemString2 "           // String
                        + "WHEN 'J' THEN 'Japan' "
                        + "ELSE 'Other' "
                    + "END  "
                    + ", "
                      + "SUM("      // Returns Long (4.8.5)
                          + "CASE "
                              + "WHEN t.itemString3 = 'C' "
                              + "THEN 1 ELSE 0 "
                          + "END" 
                      +") "
                    + ", "
                      + "SUM("      // Returns Long (4.8.5)
                          + "CASE "
                              + "WHEN t.itemString4 = 'D' "
                              + "THEN 1 ELSE 0 "
                          + "END" 
                      + ") " 
                    + ") "
                + "FROM EntityTbl01 t "
                + "GROUP BY t.itemString1, t.itemString2", Dto01.class);
        
        List<Dto01> dto01 = query.getResultList();
        assertNotNull(dto01);
        assertEquals(1, dto01.size());
        assertEquals("A", dto01.get(0).getStr1());
        assertEquals("Other", dto01.get(0).getStr2());
        assertNull(dto01.get(0).getStr3());
        assertNull(dto01.get(0).getStr4());
        assertEquals(new Integer(2), dto01.get(0).getInteger1());
        assertEquals(new Integer(2), dto01.get(0).getInteger2());
        
        em.getTransaction().rollback();
    }
    
    private void populate() {
        if (populated)
            return;
        
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            EntityTbl01 tbl1 = new EntityTbl01();
            tbl1.setKeyString("Key01");
            tbl1.setItemString1("A");
            tbl1.setItemString2("B");
            tbl1.setItemString3("C");
            tbl1.setItemString4("D");
            tbl1.setItemInteger1(1);
            em.persist(tbl1);

            EntityTbl01 tbl2 = new EntityTbl01();
            tbl2.setKeyString("Key02");
            tbl2.setItemString1("A");
            tbl2.setItemString2("B");
            tbl2.setItemString3("C");
            tbl2.setItemString4("D");
            tbl2.setItemInteger1(2);
            em.persist(tbl2);
            
            em.getTransaction().commit();
        } catch (Throwable t) {
            
        }
       
        em.close();
        
        populated = true;
    }
}
