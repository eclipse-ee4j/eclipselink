/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     12/01/2010-2.2 Guy Pelletier
//       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
package org.eclipse.persistence.testing.tests.jpa.xml.metadatacomplete;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete.Drywall;
import org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete.Hammer;
import org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete.Screwdriver;
import org.junit.Assert;

/**
 * The mapping files for this test model are located here:
 * resource/eclipselink-xml-only-model/metadata-complete-mappings.xml
 * resource/eclipselink-xml-only-model/xml-mapping-metadata-complete-mappings.xml
 */
public class XmlMetadataCompleteTest extends JUnitTestCase {

    private String m_persistenceUnit;

    public XmlMetadataCompleteTest() {
        super();
    }

    public XmlMetadataCompleteTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Metadata complete model");

        suite.addTest(new XmlMetadataCompleteTest("testMetadataComplete"));
        suite.addTest(new XmlMetadataCompleteTest("testXMLMappingMetadataComplete"));

        return suite;
    }

    /**
     * Verifies the metadata-complete flag.
     *  - Hammer : metadata-complete=true
     *  - Screwdriver : metadata-complete=false (default)
     *  - Tool : metadata-complete=true
     * <p>
     *  There is NO {@code <xml-mapping-metadata-mapping>} setting.
     */
    public void testMetadataComplete() {
        ServerSession session = getServerSession("METADATA_COMPLETE");

        /******************** Check the Hammer descriptor *********************/

        ClassDescriptor descriptor = session.getDescriptor(Hammer.class);

        // Verify the descriptor table name.
        Assert.assertNotEquals("The table annotation from the Hammer class was used.", "IGNORE_HAMMER_TABLE", descriptor.getTableName());

        // Verify the 'weight' attribute was mapped.
        assertNotNull("The 'weight' attribute from the Hammer class was NOT mapped.", descriptor.getMappingForAttributeName("weight"));

        // Verify the 'color' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'color' attribute from the Hammer class was NOT mapped", descriptor.getMappingForAttributeName("color"));
        Assert.assertNotEquals("The 'color' attribute used a @Column specification despite a metadata-complete setting of true.", "IGNORED_COLOR", descriptor.getMappingForAttributeName("color").getField().getName());

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'name' mapping didn't pick up the @Column annotation.
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        Assert.assertNotEquals("The 'brand' attribute used a @Column specification despite a metadata-complete setting of true.", "IGNORED_BRAND", descriptor.getMappingForAttributeName("brand").getField().getName());

        /****************** Check the Screwdriver descriptor ******************/

        descriptor = session.getDescriptor(Screwdriver.class);

        // Verify the 'type' attribute was mapped.
        assertNotNull("The 'type' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("type"));

        // Verify the 'style' attribute was mapped and picked up the @Column.
        assertNotNull("The 'style' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("style"));
        assertEquals("The 'style' attribute did NOT used a @Column specification.", "IN_STYLE", descriptor.getMappingForAttributeName("style").getField().getName());

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'brand' mapping didn't pick up the @Column annotation.
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        Assert.assertNotEquals("The 'brand' attribute used a @Column specification despite a metadata-complete setting of true.", "IGNORED_BRAND", descriptor.getMappingForAttributeName("brand").getField().getName());

        /******************** Check the Drywall descriptor ********************/

        descriptor = session.getDescriptor(Drywall.class);

        // Verify the 'length' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'length' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("length"));
        Assert.assertNotEquals("The 'length' attribute used a @Column specification despite a metadata-complete setting of true.", "IGNORED_LENGTH", descriptor.getMappingForAttributeName("length").getField().getName());

        // Verify the 'width' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'width' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("width"));
        Assert.assertNotEquals("The 'width' attribute used a @Column specification despite a metadata-complete setting of true.", "IGNORED_WIDTH", descriptor.getMappingForAttributeName("width").getField().getName());

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Material class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'cost' attribute was mapped and picked up the @Column from the mapped superclass marked as metadata complete = false.
        assertNotNull("The 'cost' attribute from the Meterial class was NOT mapped.", descriptor.getMappingForAttributeName("cost"));
        assertEquals("The 'cost' attribute did NOT used a @Column specification.", "MUCHO_EXPENSIVE", descriptor.getMappingForAttributeName("cost").getField().getName());
    }

    /**
     * Verifies the xml-mapping-metadata-complete flag.
     *  - Hammer : metadata-complete=false
     *  - Screwdriver : metadata-complete=false (default)
     *  - Tool : metadata-complete=false
     * <p>
     *  There is {@code <xml-mapping-metadata-mapping>} setting meaning the settings
     *  above should be ignored.
     */
    public void testXMLMappingMetadataComplete() {
        ServerSession session = getServerSession("XML_MAPPING_METADATA_COMPLETE");

        /******************** Check the Hammer descriptor *********************/

        ClassDescriptor descriptor = session.getDescriptor(Hammer.class);

        // Verify the descriptor table name.
        Assert.assertNotEquals("The table annotation from the Hammer class was used.", "IGNORE_HAMMER_TABLE", descriptor.getTableName());

        // Verify the 'weight' attribute was mapped.
        assertNotNull("The 'weight' attribute from the Hammer class was NOT mapped.", descriptor.getMappingForAttributeName("weight"));

        // Verify the 'color' attribute didn't pick up the @Column annotation
        assertNotNull("The 'color' attribute from the Hammer class was NOT mapped.", descriptor.getMappingForAttributeName("color"));
        Assert.assertNotEquals("The 'color' mapping used a @Column specification despite an xml-mapping-metadata-complete setting.", "IGNORED_COLOR", descriptor.getMappingForAttributeName("color").getField().getName());

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'brand' mapping didn't pick up the @Column annotation
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        Assert.assertNotEquals("The 'brand' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", "IGNORED_BRAND", descriptor.getMappingForAttributeName("brand").getField().getName());

        /****************** Check the Screwdriver descriptor ******************/

        descriptor = session.getDescriptor(Screwdriver.class);

        // Verify the 'type' attribute was mapped.
        assertNotNull("The 'type' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("type"));

        // Verify the 'style' mapping didn't pick up the @Column annotation
        assertNotNull("The 'style' attribute from the Screwdriver class was NOT mapped.", descriptor.getMappingForAttributeName("style"));
        Assert.assertNotEquals("The 'style' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", "IN_STYLE", descriptor.getMappingForAttributeName("style").getField().getName());

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'brand' mapping didn't pick up the @Column annotation
        assertNotNull("The 'brand' attribute from the Tool class was NOT mapped.", descriptor.getMappingForAttributeName("brand"));
        Assert.assertNotEquals("The 'brand' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", "IGNORED_BRAND", descriptor.getMappingForAttributeName("brand").getField().getName());

        /******************** Check the Drywall descriptor ********************/

        descriptor = session.getDescriptor(Drywall.class);

        // Verify the 'length' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'length' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("length"));
        Assert.assertNotEquals("The 'length' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", "IGNORED_LENGTH", descriptor.getMappingForAttributeName("length").getField().getName());

        // Verify the 'width' attribute didn't pick up the @Column annotation.
        assertNotNull("The 'width' attribute from the Drywall class was NOT mapped", descriptor.getMappingForAttributeName("width"));
        Assert.assertNotEquals("The 'width' attribute used a @Column specification despite an xml-mapping-metadata-complete setting.", "IGNORED_WIDTH", descriptor.getMappingForAttributeName("width").getField().getName());

        // Verify the 'id' attribute was mapped.
        assertNotNull("The 'id' attribute from the Material class was NOT mapped.", descriptor.getMappingForAttributeName("id"));

        // Verify the 'cost' attribute was mapped and picked up the @Column from the mapped superclass marked as metadata complete = false.
        assertNotNull("The 'cost' attribute from the Meterial class was NOT mapped.", descriptor.getMappingForAttributeName("cost"));
        Assert.assertNotEquals("The 'cost' attribute did used a @Column specification despite an xml-mapping-metadata-complete setting.", "MUCHO_EXPENSIVE", descriptor.getMappingForAttributeName("cost").getField().getName());
    }

}
