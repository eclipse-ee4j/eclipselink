/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.persistence.testing.tck;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Arrays;

import org.eclipse.persistence.indirection.WeavedAttributeValueHolderInterface;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * Verifies the enhanced fixtures in the actual Failsafe JVM before test discovery.
 * Session-open failures abort the run; ordinary execution-listener failures do not.
 */
public final class TckWeavingPreflight implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        verifyFixture("fetchdefaults.DefaultFetchBook", "publisher", "cover");
        verifyFixture("fetchdefaults.DefaultFetchCover");
        verifyFixture("fetchdefaults.DefaultFetchPublisher");
        verifyFixture("namedgraph.GraphRoot", "child");
        verifyFixture("namedgraph.GraphChild", "graphDetail");
        verifyFixture("namedgraph.SpecialGraphChild", "graphDetail");
        verifyFixture("namedgraph.GraphDetail");
        verifyFixture("namedgraph.SameNameGraphRoot", "child");
        verifyFixture("namedgraph.SameNameGraphChild", "graphDetail");
        verifyFixture("namedgraph.SameNameGraphDetail");
        verifyFixture("fetchoption.FetchOptionBook", "publisher", "defaultFetchPublisher", "lazyFetchPublisher");
        verifyFixture("fetchoption.FetchOptionPublisher");
        System.out.println("Verified static weaving for the 12 TCK fetch fixtures.");
    }

    private static void verifyFixture(String relativeName, String... lazyAttributes) {
        String name = "ee.jakarta.tck.persistence.jpa40." + relativeName;
        try {
            Class<?> type = Class.forName(name, false, Thread.currentThread().getContextClassLoader());
            URI output = origin(TckWeavingPreflight.class);
            URI actual = origin(type);
            if (output == null || !output.equals(actual)) {
                throw new IllegalStateException("Fixture was not loaded from the runner's test output: " + actual);
            }
            if (!PersistenceWeaved.class.isAssignableFrom(type)) {
                throw new IllegalStateException("Missing PersistenceWeaved marker");
            }
            // A raw subclass can inherit weaving markers from an enhanced parent.
            if (Arrays.stream(type.getDeclaredMethods()).noneMatch(method -> method.getName().startsWith("_persistence_get_"))) {
                throw new IllegalStateException("Missing declared woven accessors");
            }
            if (lazyAttributes.length > 0 && !PersistenceWeavedLazy.class.isAssignableFrom(type)) {
                throw new IllegalStateException("Missing PersistenceWeavedLazy marker");
            }
            for (String attribute : lazyAttributes) {
                Method getter = type.getMethod("_persistence_get_" + attribute + "_vh");
                // SpecialGraphChild inherits its graphDetail holder from GraphChild.
                Field holder = getter.getDeclaringClass().getDeclaredField("_persistence_" + attribute + "_vh");
                Method setter = type.getMethod("_persistence_set_" + attribute + "_vh", WeavedAttributeValueHolderInterface.class);
                if (getter.getReturnType() != WeavedAttributeValueHolderInterface.class
                        || holder.getType() != WeavedAttributeValueHolderInterface.class
                        || setter.getReturnType() != void.class) {
                    throw new IllegalStateException("Invalid woven value-holder members for " + attribute);
                }
            }
        } catch (ReflectiveOperationException | URISyntaxException | LinkageError | IllegalStateException exception) {
            throw new IllegalStateException("TCK static weaving preparation failed for " + name
                    + ". Run the normal Maven verify lifecycle to regenerate the enhanced fixtures.", exception);
        }
    }

    private static URI origin(Class<?> type) throws URISyntaxException {
        CodeSource source = type.getProtectionDomain().getCodeSource();
        return source == null || source.getLocation() == null ? null : source.getLocation().toURI();
    }
}
