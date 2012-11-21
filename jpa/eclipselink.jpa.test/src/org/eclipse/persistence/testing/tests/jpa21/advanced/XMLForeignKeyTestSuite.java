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
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
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
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Shoe;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Sprinter;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class XMLForeignKeyTestSuite extends JUnitTestCase {
    protected static final String XML_PU = "xml-default";
    
    public XMLForeignKeyTestSuite() {}
    
    public XMLForeignKeyTestSuite(String name) {
        super(name);
    }
    
    @Override
    public void setUp () {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XMLForeignKeyTestSuite");
        
        suite.addTest(new XMLForeignKeyTestSuite("testInheritancePrimaryKeyForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testCollectionTableForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testJoinTableForeignKeys"));
        suite.addTest(new XMLForeignKeyTestSuite("testMapKeyForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testManyToOneForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testElementCollectionForeignKeys"));
        
        return suite;
    }
    
    /**
     * Tests an inheritance primary key foreign key setting. 
     */
    public void testInheritancePrimaryKeyForeignKey() {
        ClassDescriptor sprinterDescriptor = JUnitTestCase.getServerSession(XML_PU).getDescriptor(Sprinter.class);
        DatabaseTable table = sprinterDescriptor.getTable("JPA21_XML_SPRINTER");
        
        assertFalse("Foreign key name was null", table.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", table.getForeignKeyName().equals("XML_Sprinter_Foreign_Key"));
        assertFalse("Foreign key definition was null", table.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", table.getForeignKeyDefinition().equals("XML_Sprinter_Foreign_Key_Definition"));
    }
    
    /**
     * Tests an inheritance primary key foreign key setting. 
     */
    public void testCollectionTableForeignKey() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession(XML_PU).getDescriptor(Runner.class);
        DirectCollectionMapping mapping = (DirectCollectionMapping) runnerDescriptor.getMappingForAttributeName("personalBests");
        DatabaseTable table = mapping.getReferenceTable();
        
        assertFalse("Foreign key name was null", table.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", table.getForeignKeyName().equals("XML_Runner_PBS_Foreign_Key"));
        assertFalse("Foreign key definition was null", table.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", table.getForeignKeyDefinition().equals("XML_Runner_PBS_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a join table foreign key settings. 
     */
    public void testJoinTableForeignKeys() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession(XML_PU).getDescriptor(Runner.class);
        ManyToManyMapping mapping = (ManyToManyMapping) runnerDescriptor.getMappingForAttributeName("races");
        DatabaseTable table = mapping.getRelationTable();
        
        assertFalse("Foreign key name was null", table.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", table.getForeignKeyName().equals("XMLRunners_Races_Foreign_Key"));
        assertFalse("Foreign key definition was null", table.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", table.getForeignKeyDefinition().equals("XMLRunners_Races_Foreign_Key_Definition"));
        
        assertFalse("Inverse Foreign key name was null", table.getInverseForeignKeyName() == null);
        assertTrue("Inverse Foreign key name was not set correctly", table.getInverseForeignKeyName().equals("XMLRunners_Races_Inverse_Foreign_Key"));
        assertFalse("Inverse Foreign key definition was null", table.getInverseForeignKeyDefinition() == null);
        assertTrue("Inverse Foreign key definition was not set correctly", table.getInverseForeignKeyDefinition().equals("XMLRunners_Races_Inverse_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a map key foreign key setting. 
     */
    public void testMapKeyForeignKey() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession(XML_PU).getDescriptor(Runner.class);
        OneToManyMapping mapping = (OneToManyMapping) runnerDescriptor.getMappingForAttributeName("shoes");
        ForeignReferenceMapping keyMapping = (ForeignReferenceMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        
        assertFalse("Foreign key name was null", keyMapping.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", keyMapping.getForeignKeyName().equals("XMLRunner_ShoeTag_Foreign_Key"));
        assertFalse("Foreign key definition was null", keyMapping.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", keyMapping.getForeignKeyDefinition().equals("XMLRunner_ShoeTag_Foreign_Key_Definition"));
    }
    
    /**
     * Tests a many to one foreign key setting. 
     */
    public void testManyToOneForeignKey() {
        ClassDescriptor shoeDescriptor = JUnitTestCase.getServerSession(XML_PU).getDescriptor(Shoe.class);
        OneToOneMapping mapping = (OneToOneMapping) shoeDescriptor.getMappingForAttributeName("runner");
        
        assertFalse("Foreign key name was null", mapping.getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", mapping.getForeignKeyName().equals("XMLShoes_Runner_Foreign_Key"));
        assertFalse("Foreign key definition was null", mapping.getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", mapping.getForeignKeyDefinition().equals("XMLShoes_Runner_Foreign_Key_Definition"));
    }
    
    /**
     * Tests an element collection foreign key settings.
     */
    public void testElementCollectionForeignKeys() {
        ClassDescriptor runnerDescriptor = JUnitTestCase.getServerSession(XML_PU).getDescriptor(Runner.class);
        DirectMapMapping mapping = (DirectMapMapping) runnerDescriptor.getMappingForAttributeName("endorsements");
        ForeignReferenceMapping keyMapping = (ForeignReferenceMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        
        assertFalse("Foreign key name was null", mapping.getReferenceTable().getForeignKeyName() == null);
        assertTrue("Foreign key name was not set correctly", mapping.getReferenceTable().getForeignKeyName().equals("XMLEndorsements_Foreign_Key"));
        assertFalse("Foreign key definition was null", mapping.getReferenceTable().getForeignKeyDefinition() == null);
        assertTrue("Foreign key definition was not set correctly", mapping.getReferenceTable().getForeignKeyDefinition().equals("XMLEndorsements_Foreign_Key_Definition"));
        
        assertFalse("Map key foreign key name was null", keyMapping.getForeignKeyName() == null);
        assertTrue("Map key foreign key name was not set correctly", keyMapping.getForeignKeyName().equals("XMLEndorsements_Key_Foreign_Key"));
        assertFalse("Map key foreign key definition was null", keyMapping.getForeignKeyDefinition() == null);
        assertTrue("Map key foreign key definition was not set correctly", keyMapping.getForeignKeyDefinition().equals("XMLEndorsements_Key_Foreign_Key_Definition"));
    }
}
