/*
 * Copyright (c) 2014, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2022 IBM Corporation and/or its affiliates. All rights reserved.
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
//     08/18/2014-2.5 Jody Grassel (IBM Corporation)
//       - 440802: xml-mapping-metadata-complete does not exclude @Entity annotated entities

package org.eclipse.persistence.testing.tests.jpa.xml.xmlmetadatacomplete;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.xmlmetadatacomplete.AnnotationOnlyEntity;
import org.eclipse.persistence.testing.models.jpa.xml.xmlmetadatacomplete.XLMMappingMetadataCompleteTableManager;
import org.eclipse.persistence.testing.models.jpa.xml.xmlmetadatacomplete.XMLOnlyEntity;

public class XMLMappingMetadataCompleteJunitTestCase extends JUnitTestCase {

    public XMLMappingMetadataCompleteJunitTestCase() {
        super();
    }

    public XMLMappingMetadataCompleteJunitTestCase(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "ecl-xml-mapping-metadata-complete";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XML Mapping Metadata Complete");

        suite.addTest(new XMLMappingMetadataCompleteJunitTestCase("testSetup"));
        suite.addTest(new XMLMappingMetadataCompleteJunitTestCase("testEntityByAnnotationOnlyIsIgnored"));
        suite.addTest(new XMLMappingMetadataCompleteJunitTestCase("testEntityByXML"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = getPersistenceUnitServerSession();
        new XLMMappingMetadataCompleteTableManager().replaceTables(session);
        clearCache();
    }

    public void testEntityByAnnotationOnlyIsIgnored() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            AnnotationOnlyEntity anoOnlyEnt = new AnnotationOnlyEntity();
            anoOnlyEnt.setId(1);
            anoOnlyEnt.setStrData("Some String");

            try {
                em.persist(anoOnlyEnt);
                fail("AnnotationOnlyEntity should not be a valid entity type.");
            } catch (Throwable t) {
                // Expected
            }
        } finally {
            if (em != null) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);;
                }
                closeEntityManager(em);
            }
        }
    }

    public void testEntityByXML() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            XMLOnlyEntity xmlOnlyEnt = new XMLOnlyEntity();
            xmlOnlyEnt.setId(1);
            xmlOnlyEnt.setStrData("Some String");

            // Expecting this to persist successfully
            em.persist(xmlOnlyEnt);

            commitTransaction(em);

            em.clear();
            this.clearCache();

            XMLOnlyEntity xmlOnlyEnt_find = em.find(XMLOnlyEntity.class, 1);
            assertNotNull(xmlOnlyEnt_find);
            assertNotSame(xmlOnlyEnt, xmlOnlyEnt_find);
        } finally {
            if (em != null) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);;
                }
                closeEntityManager(em);
            }
        }
    }
}
