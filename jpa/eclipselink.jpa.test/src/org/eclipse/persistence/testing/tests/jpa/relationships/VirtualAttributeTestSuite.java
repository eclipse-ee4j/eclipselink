/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.relationships;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.virtualattribute.*;

public class VirtualAttributeTestSuite extends JUnitTestCase {

    protected static int id;

    public VirtualAttributeTestSuite() {
        super();
    }
    
    public VirtualAttributeTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("VirtualAttributeTestSuite");
        suite.addTest(new VirtualAttributeTestSuite("testSetup"));
        suite.addTest(new VirtualAttributeTestSuite("testInsertVirtualAttribute")); 
        suite.addTest(new VirtualAttributeTestSuite("testReadVirtualAttribute")); 
        suite.addTest(new VirtualAttributeTestSuite("testUpdateVirtualAttribute")); 
        suite.addTest(new VirtualAttributeTestSuite("testDeleteVirtualAttribute"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new VirtualAttributeTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testInsertVirtualAttribute(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            OneToOneVirtualAttributeHolder holder = new OneToOneVirtualAttributeHolder();
            VirtualAttribute attribute = new VirtualAttribute();
            attribute.setDescription("virtualAttribute");
            holder.setVirtualAttribute(attribute);
            em.persist(holder);
            em.flush();
            id = holder.getId();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
    }
    
    public void testReadVirtualAttribute(){
        OneToOneVirtualAttributeHolder holder = createEntityManager().find(OneToOneVirtualAttributeHolder.class, id);
        assertNotNull("Object with virtual attributes could not be read.", holder);
        assertNotNull("Object held as a virtual attribute was not read with it's owner", holder.getVirtualAttribute());
    }
        
    public void testUpdateVirtualAttribute(){
        OneToOneVirtualAttributeHolder holder = null;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            holder = em.find(OneToOneVirtualAttributeHolder.class, id);
            VirtualAttribute attribute = new VirtualAttribute();
            attribute.setDescription("virtualAttribute2");
            holder.setVirtualAttribute(attribute);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        clearCache();
        holder = em.find(OneToOneVirtualAttributeHolder.class, id);
        closeEntityManager(em);
        assertNotNull("Updated object with virtual attributes could not be read.", holder);
        assertNotNull("Updated object held as a virtual attribute was not read with it's owner", holder.getVirtualAttribute());
        assertTrue("Virtual Attribute Object was not updated.", holder.getVirtualAttribute().getDescription().equals("virtualAttribute2"));
    }
    
    public void testDeleteVirtualAttribute(){
        OneToOneVirtualAttributeHolder holder = null;
        VirtualAttribute attribute = null;
        int attributeId = 0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            holder = em.find(OneToOneVirtualAttributeHolder.class, id);
    
            attribute = holder.getVirtualAttribute();
            attributeId = attribute.getId();
            holder.setVirtualAttribute(null);
            em.remove(attribute);       
            em.remove(holder);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        holder = em.find(OneToOneVirtualAttributeHolder.class, id);
        assertNull("Object holding virtual attribute was not properly deleted", holder);
        attribute =em.find(VirtualAttribute.class, attributeId);
        assertNull("Virtual Attribute Object was not properly removed.", attribute);
    } 
    
}
