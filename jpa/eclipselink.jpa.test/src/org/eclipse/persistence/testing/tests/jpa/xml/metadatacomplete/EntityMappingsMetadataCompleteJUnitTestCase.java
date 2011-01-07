/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.metadatacomplete;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete.Drywall;
import org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete.Hammer;
//import org.eclipse.persistence.testing.tests.jpa.TestingProperties;
import org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete.Screwdriver;

/**
 * The mapping files for this test model are located here:
 * resource/eclipselink-xml-only-model/metadata-complete-mappings.xml
 * resource/eclipselink-xml-only-model/xml-mapping-metadata-complete-mappings.xml
 */
public class EntityMappingsMetadataCompleteJUnitTestCase extends JUnitTestCase {
    
    private String m_persistenceUnit;
    
    public EntityMappingsMetadataCompleteJUnitTestCase() {
        super();
    }
    
    public EntityMappingsMetadataCompleteJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Metadata complete model");
        
        suite.addTest(new EntityMappingsMetadataCompleteJUnitTestCase("testMetadataComplete"));
        suite.addTest(new EntityMappingsMetadataCompleteJUnitTestCase("testXMLMappingMetadataComplete"));
        
        return suite;
    }
    
    /**
     * Verifies the metadata-complete flag.
     *  - Hammer : metadata-complete=true
     *  - Screwdriver : metadata-complete=false (default)
     *  - Tool : metadata-complete=true
     *  
     *  There is NO <xml-mapping-metadata-mapping> setting.
     */
    public void testMetadataComplete() {
        ServerSession session = getServerSession("METADATA_COMPLETE");
        
        /******************** Check the Hammer descriptor *********************/
        
        ClassDescriptor descriptor = session.getDescriptor(Hammer.class);
        
        // Verify the descriptor table name.
        assertFalse("The table annotation from the Hammer class was used.", descriptor.getTableName().equals("IGNORE_HAMMER_TABLE"));
        
        // Verify the 'weight' attribute was mapped.
        assertNotNull("The 'weight' attribute from the Hammer class was NOT mapped.", descriptor.getMappingForAttributeName("weight"));
        
        // Verify the 'color' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'color' attribute from the Hammer class was NOT mapped", descriptor.getMappingForAttributeName("color"));
        assertFalse("The 'color' attribute used a @Column specification despite a metadata-complete setting of true.", descriptor.getMappingForAttributeName("color").getField().getName().equals("IGNORED_COLOR"));
        
        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));
        
        // Verify the 'name' mapping didn't pick up the @Column annotation.
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        assertFalse("The 'brand' attribute used a @Column specification despite a metadata-complete setting of true.", descriptor.getMappingForAttributeName("brand").getField().getName().equals("IGNORED_BRAND"));
        
        /****************** Check the Screwdriver descriptor ******************/
        
        descriptor = session.getDescriptor(Screwdriver.class);
        
        // Verify the 'type' attribute was mapped.
        assertNotNull("The 'type' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("type"));
        
        // Verify the 'style' attribute was mapped and picked up the @Column.
        assertNotNull("The 'style' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("style"));
        assertTrue("The 'style' attribute did NOT used a @Column specification.", descriptor.getMappingForAttributeName("style").getField().getName().equals("IN_STYLE"));
        
        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));
        
        // Verify the 'brand' mapping didn't pick up the @Column annotation.
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        assertFalse("The 'brand' attribute used a @Column specification despite a metadata-complete setting of true.", descriptor.getMappingForAttributeName("brand").getField().getName().equals("IGNORED_BRAND"));
        
        /******************** Check the Drywall descriptor ********************/
        
        descriptor = session.getDescriptor(Drywall.class);
    
        // Verify the 'length' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'length' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("length"));
        assertFalse("The 'length' attribute used a @Column specification despite a metadata-complete setting of true.", descriptor.getMappingForAttributeName("length").getField().getName().equals("IGNORED_LENGTH"));
        
        // Verify the 'width' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'width' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("width"));
        assertFalse("The 'width' attribute used a @Column specification despite a metadata-complete setting of true.", descriptor.getMappingForAttributeName("width").getField().getName().equals("IGNORED_WIDTH"));
        
        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Material class was NOT mapped.", descriptor.getMappingForAttributeName("id"));
        
        // Verify the 'cost' attribute was mapped and picked up the @Column from the mapped superclass marked as metadata complete = false.
        assertNotNull("The 'cost' attribute from the Meterial class was NOT mapped.", descriptor.getMappingForAttributeName("cost"));
        assertTrue("The 'cost' attribute did NOT used a @Column specification.", descriptor.getMappingForAttributeName("cost").getField().getName().equals("MUCHO_EXPENSIVE"));
    }
    
    /**
     * Verifies the xml-mapping-metadata-complete flag.
     *  - Hammer : metadata-complete=false
     *  - Screwdriver : metadata-complete=false (default)
     *  - Tool : metadata-complete=false
     *  
     *  There is <xml-mapping-metadata-mapping> setting meaning the settings
     *  above should be ignored.
     */
    public void testXMLMappingMetadataComplete() {
        ServerSession session = getServerSession("XML_MAPPING_METADATA_COMPLETE");
        
        /******************** Check the Hammer descriptor *********************/
        
        ClassDescriptor descriptor = session.getDescriptor(Hammer.class);
        
        // Verify the descriptor table name.
        assertFalse("The table annotation from the Hammer class was used.", descriptor.getTableName().equals("IGNORE_HAMMER_TABLE"));
        
        // Verify the 'weight' attribute was mapped.
        assertNotNull("The 'weight' attribute from the Hammer class was NOT mapped.", descriptor.getMappingForAttributeName("weight"));
        
        // Verify the 'color' attribute didn't pick up the @Column annotation
        assertNotNull("The 'color' attribute from the Hammer class was NOT mapped.", descriptor.getMappingForAttributeName("color"));
        assertFalse("The 'color' mapping used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("color").getField().getName().equals("IGNORED_COLOR"));
        
        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));
        
        // Verify the 'brand' mapping didn't pick up the @Column annotation
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        assertFalse("The 'brand' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("brand").getField().getName().equals("IGNORED_BRAND"));
        
        /****************** Check the Screwdriver descriptor ******************/
        
        descriptor = session.getDescriptor(Screwdriver.class);
        
        // Verify the 'type' attribute was mapped.
        assertNotNull("The 'type' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("type"));
        
        // Verify the 'style' mapping didn't pick up the @Column annotation
        assertNotNull("The 'style' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("style"));
        assertFalse("The 'style' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("style").getField().getName().equals("IN_STYLE"));

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'brand' mapping didn't pick up the @Column annotation
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        assertFalse("The 'brand' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("brand").getField().getName().equals("IGNORED_BRAND"));
        
        /******************** Check the Drywall descriptor ********************/
        
        descriptor = session.getDescriptor(Drywall.class);
    
        // Verify the 'length' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'length' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("length"));
        assertFalse("The 'length' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("length").getField().getName().equals("IGNORED_LENGTH"));
        
        // Verify the 'width' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'width' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("width"));
        assertFalse("The 'width' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("width").getField().getName().equals("IGNORED_WIDTH"));
        
        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Material class was NOT mapped.", descriptor.getMappingForAttributeName("id"));
        
        // Verify the 'cost' attribute was mapped and picked up the @Column from the mapped superclass marked as metadata complete = false.
        assertNotNull("The 'cost' attribute from the Meterial class was NOT mapped.", descriptor.getMappingForAttributeName("cost"));
        assertFalse("The 'cost' attribute did used a @Column specification despite an xml-mapping-metadata-complete setting.", descriptor.getMappingForAttributeName("cost").getField().getName().equals("MUCHO_EXPENSIVE"));
    }
    
}
