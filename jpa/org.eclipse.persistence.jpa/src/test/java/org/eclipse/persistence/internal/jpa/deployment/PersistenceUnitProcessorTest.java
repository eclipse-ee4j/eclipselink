/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.deployment;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.ArchiveFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class PersistenceUnitProcessorTest {

    @Test
    void testComputePURootURLForZipFile() throws Exception {
        // Test cases for expected behavior.
        checkPURootCustom(
                "zip", "/foo/bar.jar!/META-INF/persistence.xml",
                "file:/foo/bar.jar"
        );

        // WAR files have a special location available.
        checkPURootCustom(
                "zip", "/foo/bar.war!/WEB-INF/classes/META-INF/persistence.xml",
                "jar:file:/foo/bar.war!/WEB-INF/classes/"
        );

        // WAR files have a special location available.
        // Simulate event when custom persistence descriptor file name is used and specified e.g.
        // System.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "WEB-INF/classes/META-INF/my-persistence.xml");
        checkPURootCustomWithCustomDescriptorLocation(
                "zip", "/foo/bar.war!/WEB-INF/classes/META-INF/my-persistence.xml",
                "file:/foo/bar.war",
                "WEB-INF/classes/META-INF/my-persistence.xml"
        );

        // Same as the previous one, but not a WAR!
        checkPURootFailsCustom(
                "zip", "/foo/bar.jar!/WEB-INF/classes/META-INF/persistence.xml"
        );

        // META-INF in some other directory (not conforming to JPA spec!).
        checkPURootFailsCustom(
                "zip", "/foo/bar.jar!/foo/META-INF/persistence.xml"
        );

        // Test cases for specific issues.

        // 463571

        checkPURootCustom(
                "zip", "/C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml",
                "file:/C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar"
        );

        checkPURootCustom(
                "zip", "/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml",
                "file:/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar"
        );

        checkPURootCustom(
                "zip", "/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml",
                "file:/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar"
        );
    }

    @Test
    void testComputePURootURLForJarFile() throws Exception {
        // Test cases for expected behavior.
        checkPURootSimple(
                "jar:file:/foo/bar.jar!/META-INF/persistence.xml",
                "file:/foo/bar.jar"
        );

        checkPURootSimple(
                "jar:file:/foo/bar.war!/WEB-INF/classes/META-INF/persistence.xml",
                "jar:file:/foo/bar.war!/WEB-INF/classes/"
        );

        checkPURootFailsSimple(
                "jar:file:/foo/bar.jar!/WEB-INF/classes/META-INF/persistence.xml"
        );

        checkPURootFailsSimple(
                "jar:file:/foo/bar.jar!/foo/META-INF/persistence.xml"
        );
    }

    @Test
    void testComputePURootURLForWsjarFile() throws Exception {
        // Test cases for expected behavior.
        // The results differ slightly from the other archive URLs!
        checkPURootCustom(
                "wsjar", "file:/foo/bar.jar!/META-INF/persistence.xml",
                "jar:file:/foo/bar.jar!/"
        );

        checkPURootCustom(
                "wsjar", "file:/foo/bar.war!/WEB-INF/classes/META-INF/persistence.xml",
                "jar:file:/foo/bar.war!/WEB-INF/classes/"
        );

        checkPURootFailsCustom(
                "wsjar", "file:/foo/bar.jar!/WEB-INF/classes/META-INF/persistence.xml"
        );

        checkPURootFailsCustom(
                "wsjar", "file:/foo/bar.jar!/foo/META-INF/persistence.xml"
        );
    }

    @Test
    void testGetArchiveFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(SystemProperties.ARCHIVE_FACTORY, AF1.class.getName());
        ArchiveFactory af = PersistenceUnitProcessor.getArchiveFactory(PersistenceUnitProcessorTest.class.getClassLoader(), props);
        Assertions.assertInstanceOf(AF1.class, af);
    }

    @Test
    void testGetArchiveFactoryOverride() {
        String orig = System.getProperty(SystemProperties.ARCHIVE_FACTORY, "--noval--");
        try {
            System.setProperty(SystemProperties.ARCHIVE_FACTORY, AF2.class.getName());
            Map<String, Object> props = new HashMap<>();
            props.put(SystemProperties.ARCHIVE_FACTORY, AF1.class.getName());
            ArchiveFactory af = PersistenceUnitProcessor.getArchiveFactory(PersistenceUnitProcessorTest.class.getClassLoader(), props);
            Assertions.assertInstanceOf(AF2.class, af);
        } finally {
            if ("--noval--".equals(orig)) {
                System.clearProperty(SystemProperties.ARCHIVE_FACTORY);
            } else {
                System.setProperty(SystemProperties.ARCHIVE_FACTORY, orig);
            }
        }
    }

    @Test
    @SuppressWarnings({"removal", "deprecation"})
    void testV1() {
        String pu = """
            <?xml version="1.0" encoding="UTF-8"?>
            <persistence xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence persistence_1_0.xsd" version="1.0">
                <persistence-unit name="default" transaction-type="RESOURCE_LOCAL">
                    <provider>
                        org.eclipse.persistence.jpa.PersistenceProvider
                    </provider>
                    <!-- mapping files that are packaged in xml-only-model-tests.jar. -->
                    <mapping-file>META-INF/entity-mappings.xml</mapping-file>
                    <jar-file>model.jar</jar-file>
                    <class>org.eclipse.persistence.testing.models.Beer</class>
                    <exclude-unlisted-classes>false</exclude-unlisted-classes>
                    <properties>
                        <property name="eclipselink.session-name" value="default-session"/>
                        <!--a comment -->
                        <property name="eclipselink.weaving.eager" value="true"/>
                    </properties>
                </persistence-unit>
            
                <persistence-unit name="euc_true">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <properties>
                        <!-- a comment -->
                        <property name="eclipselink.logging.level" value="OFF"/>
                    </properties>
                </persistence-unit>
            
                <!-- another case for exclude-unlisted-classes -->
                <persistence-unit name="euc_empty" transaction-type="JTA">
                    <provider>org.eclipse.persistence.jpa.UnknownProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <non-jta-data-source>jdbc/EclipseLinkNonJTADS</non-jta-data-source>
                    <mapping-file>META-INF/some-entity-mappings.xml</mapping-file>
                    <class>org.eclipse.persistence.testing.models.Beer0</class>
                    <class>org.eclipse.persistence.testing.models.Beer1</class>
                    <class>org.eclipse.persistence.testing.models.Beer2</class>
                    <exclude-unlisted-classes />
                </persistence-unit>
            </persistence>""";

        List<SEPersistenceUnitInfo> persistenceUnits = readPersistenceXML(pu);
        Assertions.assertEquals(3, persistenceUnits.size());

        // the first unit
        SEPersistenceUnitInfo info = persistenceUnits.get(0);
        Assertions.assertEquals("1.0", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("default", info.getPersistenceUnitName());
        Assertions.assertEquals(jakarta.persistence.spi.PersistenceUnitTransactionType.RESOURCE_LOCAL, info.getTransactionType());
        Assertions.assertEquals("org.eclipse.persistence.jpa.PersistenceProvider", info.getPersistenceProviderClassName());
        Assertions.assertEquals(1, info.getMappingFileNames().size());
        Assertions.assertEquals("META-INF/entity-mappings.xml", info.getMappingFileNames().get(0));
        Assertions.assertEquals(1, info.getJarFiles().size());
        Assertions.assertEquals("model.jar", info.getJarFiles().iterator().next());
        Assertions.assertEquals(1, info.getManagedClassNames().size());
        Assertions.assertFalse(info.excludeUnlistedClasses());
        Assertions.assertEquals(2, info.getProperties().size());
        Assertions.assertEquals("default-session", info.getProperties().get("eclipselink.session-name"));

        // the second unit
        info = persistenceUnits.get(1);
        Assertions.assertEquals("1.0", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("euc_true", info.getPersistenceUnitName());
        Assertions.assertEquals(jakarta.persistence.spi.PersistenceUnitTransactionType.RESOURCE_LOCAL, info.getTransactionType());
        Assertions.assertEquals("org.eclipse.persistence.jpa.PersistenceProvider", info.getPersistenceProviderClassName());
        Assertions.assertEquals(0, info.getMappingFileNames().size());
        Assertions.assertEquals(0, info.getJarFiles().size());
        Assertions.assertEquals(0, info.getManagedClassNames().size());
        Assertions.assertTrue(info.excludeUnlistedClasses());
        Assertions.assertEquals(1, info.getProperties().size());
        Assertions.assertEquals("OFF", info.getProperties().get("eclipselink.logging.level"));


        // the third unit
        info = persistenceUnits.get(2);
        Assertions.assertEquals("1.0", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("euc_empty", info.getPersistenceUnitName());
        Assertions.assertEquals(jakarta.persistence.spi.PersistenceUnitTransactionType.JTA, info.getTransactionType());
        Assertions.assertEquals("org.eclipse.persistence.jpa.UnknownProvider", info.getPersistenceProviderClassName());
        Assertions.assertEquals("jdbc/EclipseLinkDS", ((DataSourceImpl) info.getJtaDataSource()).getName());
        Assertions.assertEquals("jdbc/EclipseLinkNonJTADS", ((DataSourceImpl) info.getNonJtaDataSource()).getName());
        Assertions.assertEquals(1, info.getMappingFileNames().size());
        Assertions.assertEquals("META-INF/some-entity-mappings.xml", info.getMappingFileNames().get(0));
        Assertions.assertEquals(0, info.getJarFiles().size());
        Assertions.assertEquals(3, info.getManagedClassNames().size());
        Assertions.assertEquals(0, info.getProperties().size());
        // v1.0 has a bug in default value, fixed in 2.0+
        Assertions.assertFalse(info.excludeUnlistedClasses());
    }

    @Test
    void testV2() {
        String pu = """
            <persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd" version="2.2">
                <persistence-unit name="ALL">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <class>org.eclipse.persistence.testing.models.jpa.cacheable.CacheableFalseDetail</class>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <shared-cache-mode>ALL</shared-cache-mode>
                    <validation-mode>AUTO</validation-mode>
                    <properties>
                        <property name="eclipselink.target-database" value="org.eclipse.persistence.platform.database.MySQLPlatform"/>
                    </properties>
                </persistence-unit>
                <persistence-unit name="NONE">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <class>org.eclipse.persistence.testing.models.jpa.cacheable.CacheableFalseEntity</class>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <shared-cache-mode>NONE</shared-cache-mode>
                    <validation-mode>NONE</validation-mode>
                    <properties>
                        <property name="eclipselink.target-database" value="org.eclipse.persistence.platform.database.MySQLPlatform"/>
                    </properties>
                </persistence-unit>
                <persistence-unit name="ENABLE_SELECTIVE">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <class>org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueEntity</class>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
                    <validation-mode>CALLBACK</validation-mode>
                    <properties>
                        <property name="eclipselink.target-database" value="org.eclipse.persistence.platform.database.MySQLPlatform"/>
                    </properties>
                </persistence-unit>
                <persistence-unit name="DISABLE_SELECTIVE">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <class>org.eclipse.persistence.testing.models.jpa.cacheable.ProtectedEmbeddable</class>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <shared-cache-mode>DISABLE_SELECTIVE</shared-cache-mode>
                    <properties>
                        <property name="eclipselink.target-database" value="org.eclipse.persistence.platform.database.MySQLPlatform"/>
                    </properties>
                </persistence-unit>
                <persistence-unit name="UNSPECIFIED">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <class>org.eclipse.persistence.testing.models.jpa.cacheable.ProtectedEmbeddable</class>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <shared-cache-mode>UNSPECIFIED</shared-cache-mode>
                    <properties>
                        <property name="eclipselink.target-database" value="org.eclipse.persistence.platform.database.MySQLPlatform"/>
                    </properties>
                </persistence-unit>
                <persistence-unit name="CacheUnlisted">
                    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                    <jta-data-source>jdbc/EclipseLinkDS</jta-data-source>
                    <class>org.eclipse.persistence.testing.models.jpa.cacheable.SharedEmbeddable</class>
                    <exclude-unlisted-classes>true</exclude-unlisted-classes>
                    <properties>
                        <property name="eclipselink.target-database" value="org.eclipse.persistence.platform.database.MySQLPlatform"/>
                    </properties>
                </persistence-unit>
            </persistence>""";

        List<SEPersistenceUnitInfo> persistenceUnits = readPersistenceXML(pu);
        Assertions.assertEquals(6, persistenceUnits.size());

        SEPersistenceUnitInfo info = persistenceUnits.get(0);
        Assertions.assertEquals("2.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("ALL", info.getPersistenceUnitName());
        Assertions.assertEquals(SharedCacheMode.ALL, info.getSharedCacheMode());
        Assertions.assertEquals(ValidationMode.AUTO, info.getValidationMode());

        info = persistenceUnits.get(1);
        Assertions.assertEquals("2.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("NONE", info.getPersistenceUnitName());
        Assertions.assertEquals(SharedCacheMode.NONE, info.getSharedCacheMode());
        Assertions.assertEquals(ValidationMode.NONE, info.getValidationMode());

        info = persistenceUnits.get(2);
        Assertions.assertEquals("2.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("ENABLE_SELECTIVE", info.getPersistenceUnitName());
        Assertions.assertEquals(SharedCacheMode.ENABLE_SELECTIVE, info.getSharedCacheMode());
        Assertions.assertEquals(ValidationMode.CALLBACK, info.getValidationMode());

        info = persistenceUnits.get(3);
        Assertions.assertEquals("2.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("DISABLE_SELECTIVE", info.getPersistenceUnitName());
        Assertions.assertEquals(SharedCacheMode.DISABLE_SELECTIVE, info.getSharedCacheMode());
        Assertions.assertEquals(ValidationMode.AUTO, info.getValidationMode());

        info = persistenceUnits.get(4);
        Assertions.assertEquals("2.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("UNSPECIFIED", info.getPersistenceUnitName());
        Assertions.assertEquals(SharedCacheMode.UNSPECIFIED, info.getSharedCacheMode());
        Assertions.assertEquals(ValidationMode.AUTO, info.getValidationMode());

        info = persistenceUnits.get(5);
        Assertions.assertEquals("2.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("CacheUnlisted", info.getPersistenceUnitName());
        Assertions.assertEquals(SharedCacheMode.DISABLE_SELECTIVE, info.getSharedCacheMode());
        Assertions.assertEquals(ValidationMode.AUTO, info.getValidationMode());
    }

    @Test
    void testV3() {
        String pu = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_2.xsd" version="3.2">
                <persistence-unit name="default" transaction-type="RESOURCE_LOCAL" xmlns:cdi="https://jakarta.ee/xml/ns/persistence-cdi">
                    <qualifier>org.acme.Q1, org.acme.Q2</qualifier>
                    <scope>org.acme.CustomScope</scope>
                    <class>org.eclipse.persistence.testing.models.Beer</class>
                    <properties>
                        <property name="eclipselink.session-name" value="default-session"/>
                        <!--a comment -->
                        <property name="eclipselink.weaving.eager" value="true"/>
                    </properties>
                    <cdi:scope>com.example.jpa.ACustomScope</cdi:scope>
                    <cdi:qualifier>com.example.jpa.CustomQualifier</cdi:qualifier>
                </persistence-unit>
            
                <persistence-unit name="empty"/>
            </persistence>""";

        List<SEPersistenceUnitInfo> persistenceUnits = readPersistenceXML(pu);
        Assertions.assertEquals(2, persistenceUnits.size());

        // the first unit
        SEPersistenceUnitInfo info = persistenceUnits.get(0);
        Assertions.assertEquals("3.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("default", info.getPersistenceUnitName());
        Assertions.assertNull(info.getPersistenceProviderClassName());
        Assertions.assertEquals("org.acme.CustomScope", info.getScopeAnnotationName());
        Assertions.assertEquals(2, info.getQualifierAnnotationNames().size());
        Assertions.assertEquals("org.acme.Q1", info.getQualifierAnnotationNames().get(0));
        Assertions.assertEquals("org.acme.Q2", info.getQualifierAnnotationNames().get(1));
        Assertions.assertTrue(info.getMappingFileNames().isEmpty());
        Assertions.assertTrue(info.getJarFiles().isEmpty());
        Assertions.assertEquals(1, info.getManagedClassNames().size());
        Assertions.assertTrue(info.excludeUnlistedClasses());
        Assertions.assertEquals(2, info.getProperties().size());
        Assertions.assertEquals("default-session", info.getProperties().get("eclipselink.session-name"));

        // the second unit
        info = persistenceUnits.get(1);
        Assertions.assertEquals("3.2", info.getPersistenceXMLSchemaVersion());
        Assertions.assertEquals("empty", info.getPersistenceUnitName());
        Assertions.assertNull(info.getPersistenceProviderClassName());
        Assertions.assertTrue(info.getQualifierAnnotationNames().isEmpty());
        Assertions.assertNull(info.getScopeAnnotationName());
        Assertions.assertTrue(info.getMappingFileNames().isEmpty());
        Assertions.assertTrue(info.getJarFiles().isEmpty());
        Assertions.assertTrue(info.getManagedClassNames().isEmpty());
        Assertions.assertTrue(info.excludeUnlistedClasses());
        Assertions.assertTrue(info.getProperties().isEmpty());
        Assertions.assertEquals(TestArchive.class.getClassLoader(), info.getClassLoader());
    }

    private static final String INVALID_PU_TRANSACTION_TYPE = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd" version="3.0">
                <persistence-unit name="default" transaction-type="I-N-V-A-L-I-D"/>
            </persistence>""";
    private static final String INVALID_PU_CACHE_MODE = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd" version="3.0">
                <persistence-unit name="default">
                    <shared-cache-mode>INVALID-MODE</shared-cache-mode>
                </persistence-unit>
            </persistence>""";
    private static final String INVALID_PU_VALIDATION_MODE = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd" version="3.0">
                <persistence-unit name="default">
                    <validation-mode>NO-VALIDATION</validation-mode>
                </persistence-unit>
            </persistence>""";
    private static final String INVALID_PU_NEW_ELEMENT = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd" version="3.0">
                <persistence-unit name="default">
                    <new-element>new feature</new-element>
                </persistence-unit>
            </persistence>""";

    @ParameterizedTest
    @ValueSource(strings = {INVALID_PU_TRANSACTION_TYPE, INVALID_PU_CACHE_MODE, INVALID_PU_VALIDATION_MODE, INVALID_PU_NEW_ELEMENT})
    void testInvalidValues(String pu) {
        try {
            readPersistenceXML(pu);
            Assertions.fail("PersistenceUnitLoadingException should have been thrown.");
        } catch (EclipseLinkException ee) {
            Assertions.assertInstanceOf(PersistenceUnitLoadingException.class, ee);
        }
    }

    private List<SEPersistenceUnitInfo> readPersistenceXML(String s) {
        return PersistenceUnitProcessor.processPersistenceArchive(new TestArchive(s), TestArchive.class.getClassLoader());
    }

    // Stub handler for nonstandard schemes (zip: and wsjar:).
    private static final URLStreamHandler dummyHandler =
            new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) {
                    return null;
                }
            };

    private static void checkPURootSimple(String inputUrl, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput,
                PersistenceUnitProcessor.computePURootURL(
                        new URL(inputUrl),
                        "META-INF/persistence.xml").toString());
    }

    private static void checkPURootCustom(String inputScheme, String inputFile, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput,
                PersistenceUnitProcessor.computePURootURL(
                        new URL(inputScheme, "", -1, inputFile, dummyHandler),
                        "META-INF/persistence.xml").toString());
    }

    private static void checkPURootCustomWithCustomDescriptorLocation(String inputScheme, String inputFile, String expectedOutput, String descriptorLocation) throws Exception {
        Assertions.assertEquals(expectedOutput,
                PersistenceUnitProcessor.computePURootURL(
                        new URL(inputScheme, "", -1, inputFile, dummyHandler),
                        descriptorLocation).toString());
    }

    private static void checkPURootFailsCustom(String inputScheme, String inputFile) {
        Assertions.assertThrows(ValidationException.class, () -> PersistenceUnitProcessor.computePURootURL(
                new URL(inputScheme, "", -1, inputFile, dummyHandler),
                "META-INF/persistence.xml"));
    }

    private static void checkPURootFailsSimple(String input) {
        Assertions.assertThrows(ValidationException.class, () -> PersistenceUnitProcessor.computePURootURL(
                new URL(input),
                "META-INF/persistence.xml"));
    }

    public static class AF1 extends ArchiveFactoryImpl {}
    public static class AF2 extends ArchiveFactoryImpl {}

    private static final class TestArchive extends ArchiveBase implements Archive {

        private final String content;
        public TestArchive(String content) {
            super(create(), "META-INF/persistence.xml");
            this.content = content;
        }

        private static URL create() {
            try {
                return URI.create("file:///META-INF/persistence.xml").toURL();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        @Override
        public InputStream getDescriptorStream() {
            return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void close() {

        }

        @Override
        public Iterator<String> getEntries() {
            return null;
        }

        @Override
        public InputStream getEntry(String entryPath) {
            return null;
        }

        @Override
        public URL getEntryAsURL(String entryPath) {
            return null;
        }
    }
}
