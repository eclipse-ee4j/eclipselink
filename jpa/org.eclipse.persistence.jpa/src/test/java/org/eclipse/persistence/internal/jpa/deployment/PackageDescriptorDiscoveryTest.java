/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.asm.ASMFactory;
import org.eclipse.persistence.asm.ClassWriter;
import org.eclipse.persistence.asm.Opcodes;
import org.eclipse.persistence.jpa.Archive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests generated with AI assistance and reviewed for package discovery contracts. */
class PackageDescriptorDiscoveryTest {
    private static final String PREFIX = "org.eclipse.persistence.internal.jpa.deployment.packagedescriptors.";
    private static final String DISCOVERED = PREFIX + "discovered";
    private static final String UNMARKED = PREFIX + "unmarked";
    private static final String DESCRIPTOR_RESOURCE = DISCOVERED.replace('.', '/') + "/package-info.class";

    @TempDir
    Path root;

    @Test
    void classRetainedDiscoverableAnnotationEnrollsOnlyItsPackage() throws Exception {
        SEPersistenceUnitInfo info = unit();
        assertEquals(List.of(DISCOVERED), info.getAllPackageDescriptors());
        assertTrue(info.getManagedPackageDescriptors().isEmpty());
        info.addManagedPackageDescriptor(UNMARKED);
        assertEquals(Set.of(DISCOVERED, UNMARKED), Set.copyOf(info.getAllPackageDescriptors()));
        info.setExcludeUnlistedClasses(true);
        assertEquals(List.of(UNMARKED), info.getAllPackageDescriptors());
    }

    @Test
    void effectiveArchiveFactoryOverridesDefaultDiscovery() throws Exception {
        SEPersistenceUnitInfo info = unit();
        assertEquals(List.of(DISCOVERED), info.getAllPackageDescriptors());
        Map<String, Object> overrides = Map.of(SystemProperties.ARCHIVE_FACTORY, EmptyArchiveFactory.class.getName());
        assertTrue(PersistenceUnitProcessor.getPackageDescriptors(info, getClass().getClassLoader(), overrides).isEmpty());
        assertEquals(List.of(DISCOVERED), info.getAllPackageDescriptors(), "An override must not mutate the PUI view");
    }

    @Test
    void effectiveLoaderIsUsedWithoutMutatingThePersistenceUnit() throws Exception {
        SEPersistenceUnitInfo info = unit();
        ClassLoader unavailableDescriptors = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public InputStream getResourceAsStream(String name) {
                return name.endsWith("/package-info.class") ? null : super.getResourceAsStream(name);
            }
        };
        assertTrue(PersistenceUnitProcessor.getPackageDescriptors(info, unavailableDescriptors, Map.of()).isEmpty());
        assertEquals(List.of(DISCOVERED), info.getAllPackageDescriptors());
    }

    @Test
    void descriptorClassFilesAreExcludedFromThe40ClassSet() throws Exception {
        SEPersistenceUnitInfo info = unit();
        assertTrue(PersistenceUnitProcessor.buildClassSet(info, Map.of()).isEmpty());
        info.setPersistenceXMLSchemaVersion("3.2");
        assertEquals(Set.of(DISCOVERED + ".package-info", UNMARKED + ".package-info"),
                PersistenceUnitProcessor.buildClassSet(info, Map.of()));
    }

    @Test
    void versionOnlyMultiReleaseDescriptorUsesOneLogicalPackageName() throws Exception {
        URL jar = packageJar(true, Map.of(
                "META-INF/versions/9/" + DESCRIPTOR_RESOURCE, descriptorBytes(true),
                "META-INF/versions/11/" + DESCRIPTOR_RESOURCE, descriptorBytes(true)));
        try (URLClassLoader loader = descriptorLoader(jar)) {
            SEPersistenceUnitInfo info = jarUnit(jar, loader);
            assertEquals(List.of(DISCOVERED), info.getAllPackageDescriptors());
            info.addManagedPackageDescriptor(DISCOVERED);
            assertEquals(List.of(DISCOVERED), info.getAllPackageDescriptors());
        }
    }

    @Test
    void unavailableMultiReleaseVersionDoesNotEnrollItsPackage() throws Exception {
        URL jar = packageJar(true, Map.of("META-INF/versions/" + (Runtime.version().feature() + 1)
                + "/" + DESCRIPTOR_RESOURCE, descriptorBytes(true)));
        // The parent already has an annotated descriptor with this name. An
        // inapplicable physical entry must not enroll that unrelated resource.
        try (URLClassLoader loader = new URLClassLoader(new URL[] {jar}, getClass().getClassLoader())) {
            assertTrue(jarUnit(jar, loader).getAllPackageDescriptors().isEmpty());
        }
    }

    @Test
    void nonMultiReleaseJarDoesNotEnrollVersionedEntries() throws Exception {
        URL jar = packageJar(false, Map.of("META-INF/versions/9/" + DESCRIPTOR_RESOURCE, descriptorBytes(true)));
        try (URLClassLoader loader = new URLClassLoader(new URL[] {jar}, getClass().getClassLoader())) {
            assertTrue(jarUnit(jar, loader).getAllPackageDescriptors().isEmpty());
        }
    }

    @Test
    void invalidMultiReleaseDirectoriesDoNotEnrollPackages() throws Exception {
        for (String version : List.of("8", "09", "unknown")) {
            URL jar = packageJar(true, Map.of("META-INF/versions/" + version + "/" + DESCRIPTOR_RESOURCE,
                    descriptorBytes(true)));
            try (URLClassLoader loader = new URLClassLoader(new URL[] {jar}, getClass().getClassLoader())) {
                assertTrue(jarUnit(jar, loader).getAllPackageDescriptors().isEmpty(), version);
            }
        }
    }

    @Test
    void effectiveMultiReleaseVersionDeterminesDiscoverability() throws Exception {
        URL suppressed = packageJar(true, Map.of(DESCRIPTOR_RESOURCE, descriptorBytes(true),
                "META-INF/versions/9/" + DESCRIPTOR_RESOURCE, descriptorBytes(false)));
        try (URLClassLoader loader = descriptorLoader(suppressed)) {
            assertTrue(jarUnit(suppressed, loader).getAllPackageDescriptors().isEmpty());
        }
        URL selected = packageJar(true, Map.of(DESCRIPTOR_RESOURCE, descriptorBytes(false),
                "META-INF/versions/9/" + DESCRIPTOR_RESOURCE, descriptorBytes(true),
                "META-INF/versions/" + (Runtime.version().feature() + 1) + "/" + DESCRIPTOR_RESOURCE,
                descriptorBytes(false)));
        try (URLClassLoader loader = descriptorLoader(selected)) {
            assertEquals(List.of(DISCOVERED), jarUnit(selected, loader).getAllPackageDescriptors());
        }
    }

    private SEPersistenceUnitInfo jarUnit(URL jar, ClassLoader loader) {
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setPersistenceXMLSchemaVersion("4.0");
        info.setClassLoader(loader);
        info.setJarFileUrls(List.of(jar));
        return info;
    }

    private URLClassLoader descriptorLoader(URL jar) {
        return new URLClassLoader(new URL[] {jar}, getClass().getClassLoader()) {
            @Override
            public URL getResource(String name) {
                // Keep descriptor lookup within the fixture JAR while retaining
                // the normal loader for the custom annotation and its meta-annotations.
                return name.equals(DESCRIPTOR_RESOURCE) ? findResource(name) : super.getResource(name);
            }
        };
    }

    private URL packageJar(boolean multiRelease, Map<String, byte[]> entries) throws Exception {
        Path jar = Files.createTempFile(root, "packages-", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (multiRelease) {
            manifest.getMainAttributes().putValue("Multi-Release", "true");
        }
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey()));
                output.write(entry.getValue());
                output.closeEntry();
            }
        }
        return jar.toUri().toURL();
    }

    private byte[] descriptorBytes(boolean discoverable) {
        ClassWriter writer = ASMFactory.createClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_INTERFACE | Modifier.ABSTRACT | Opcodes.ACC_SYNTHETIC,
                DISCOVERED.replace('.', '/') + "/package-info", null, "java/lang/Object", null);
        if (discoverable) {
            writer.visitAnnotation("L" + PREFIX.replace('.', '/') + "PackageMarker;", false).visitEnd();
        }
        writer.visitEnd();
        return writer.toByteArray();
    }

    private SEPersistenceUnitInfo unit() throws Exception {
        for (String name : List.of(DISCOVERED, UNMARKED)) {
            String resource = name.replace('.', '/') + "/package-info.class";
            Path target = root.resolve(resource);
            Files.createDirectories(target.getParent());
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
                Files.copy(java.util.Objects.requireNonNull(stream, resource), target);
            }
        }
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setPersistenceXMLSchemaVersion("4.0");
        info.setPersistenceUnitRootUrl(root.toUri().toURL());
        info.setClassLoader(getClass().getClassLoader());
        info.setExcludeUnlistedClasses(false);
        return info;
    }

    public static class EmptyArchiveFactory extends ArchiveFactoryImpl {
        @Override
        public Archive createArchive(URL rootUrl, Map properties) {
            return null;
        }
    }
}
