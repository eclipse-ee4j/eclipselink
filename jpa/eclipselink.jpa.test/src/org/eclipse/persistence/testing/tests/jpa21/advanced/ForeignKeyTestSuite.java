/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import javax.persistence.EntityManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa21.advanced.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.Shoe;
import org.eclipse.persistence.testing.models.jpa21.advanced.Sprinter;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class ForeignKeyTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;

    public ForeignKeyTestSuite() {}
    
    public ForeignKeyTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ForeignKeyTestSuite");
        
        suite.addTest(new ForeignKeyTestSuite("testSetup"));
        suite.addTest(new ForeignKeyTestSuite("testInheritancePrimaryKeyForeignKey"));
        suite.addTest(new ForeignKeyTestSuite("testCollectionTableForeignKey"));
        suite.addTest(new ForeignKeyTestSuite("testJoinTableForeignKeys"));
        suite.addTest(new ForeignKeyTestSuite("testMapKeyForeignKey"));
        suite.addTest(new ForeignKeyTestSuite("testManyToOneForeignKey"));
        suite.addTest(new ForeignKeyTestSuite("testElementCollectionForeignKeys"));
        
        suite.addTest(XMLForeignKeyTestSuite.suite());
        
        return suite;
    }
    
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache();
    }
    
    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }
        
        super.tearDown();
    }
    
    /**
     * Tests an inheritance primary key foreign key setting. 
     */
    public void testInheritancePrimaryKeyForeignKey() {
        ClassDescriptor sprinterDescriptor = JUnitTestCase.getServerSession().getDescriptor(Sprinter.class);
        DatabaseTable table = sprinterDescriptor.getTable("JPA21_SPRINTER");
        
        assertFalse("Foreign key name was null", table.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", table.getForeignKeyName().equals("Sprinter_Foreign_Key"));
        assertFalse("Foreign key definition was null", table.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", table.getForeignKeyDefinition().equals("Sprinter_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a collection table foreign key setting. 
     */
    public void testCollectionTableForeignKey() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession().getDescriptor(Runner.class);
        DirectCollectionMapping mapping = (DirectCollectionMapping) runnerDescriptor.getMappingForAttributeName("personalBests");
        DatabaseTable table = mapping.getReferenceTable();
        
        assertFalse("Foreign key name was null", table.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", table.getForeignKeyName().equals("Runner_PBS_Foreign_Key"));
        assertFalse("Foreign key definition was null", table.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", table.getForeignKeyDefinition().equals("Runner_PBS_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a join table foreign key settings. 
     */
    public void testJoinTableForeignKeys() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession().getDescriptor(Runner.class);
        ManyToManyMapping mapping = (ManyToManyMapping) runnerDescriptor.getMappingForAttributeName("races");
        DatabaseTable table = mapping.getRelationTable();
        
        assertFalse("Foreign key name was null", table.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", table.getForeignKeyName().equals("Runners_Races_Foreign_Key"));
        assertFalse("Foreign key definition was null", table.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", table.getForeignKeyDefinition().equals("Runners_Races_Foreign_Key_Definition"));
        
        assertFalse("Inverse Foreign key name was null", table.getInverseForeignKeyName() == null);
        assertTrue("Inverse Foreign key name was not set correctly", table.getInverseForeignKeyName().equals("Runners_Races_Inverse_Foreign_Key"));
        assertFalse("Inverse Foreign key definition was null", table.getInverseForeignKeyDefinition() == null);
        assertTrue("Inverse Foreign key definition was not set correctly", table.getInverseForeignKeyDefinition().equals("Runners_Races_Inverse_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a map key foreign key setting. 
     */
    public void testMapKeyForeignKey() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession().getDescriptor(Runner.class);
        OneToManyMapping mapping = (OneToManyMapping) runnerDescriptor.getMappingForAttributeName("shoes");
        ForeignReferenceMapping keyMapping = (ForeignReferenceMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        
        assertFalse("Foreign key name was null", keyMapping.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", keyMapping.getForeignKeyName().equals("Runner_ShoeTag_Foreign_Key"));
        assertFalse("Foreign key definition was null", keyMapping.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", keyMapping.getForeignKeyDefinition().equals("Runner_ShoeTag_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a many to one foreign key setting. 
     */
    public void testManyToOneForeignKey() {
        ClassDescriptor shoeDescriptor = JUnitTestCase.getServerSession().getDescriptor(Shoe.class);
        OneToOneMapping mapping = (OneToOneMapping) shoeDescriptor.getMappingForAttributeName("runner");
        
        assertFalse("Foreign key name was null", mapping.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", mapping.getForeignKeyName().equals("Shoes_Runner_Foreign_Key"));
        assertFalse("Foreign key definition was null", mapping.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", mapping.getForeignKeyDefinition().equals("Shoes_Runner_Foreign_Key_Definition"));
    }
    
    /**
     * Tests an element collection foreign key settings.
     */
    public void testElementCollectionForeignKeys() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession().getDescriptor(Runner.class);
        DirectMapMapping mapping = (DirectMapMapping) runnerDescriptor.getMappingForAttributeName("endorsements");
        ForeignReferenceMapping keyMapping = (ForeignReferenceMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        
        assertFalse("Foreign key name was null", mapping.getReferenceTable().getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", mapping.getReferenceTable().getForeignKeyName().equals("Endorsements_Foreign_Key"));
        assertFalse("Foreign key definition was null", mapping.getReferenceTable().getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", mapping.getReferenceTable().getForeignKeyDefinition().equals("Endorsements_Foreign_Key_Definition"));
        
        assertFalse("Map key foreign key name was null", keyMapping.getForeignKeyName() == null);
        assertTrue("Map key foreign key name was not set correctly", keyMapping.getForeignKeyName().equals("Endorsements_Key_Foreign_Key"));
        assertFalse("Map key foreign key definition was null", keyMapping.getForeignKeyDefinition() == null);
        assertTrue("Map key foreign key definition was not set correctly", keyMapping.getForeignKeyDefinition().equals("Endorsements_Key_Foreign_Key_Definition"));
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
    }
}
