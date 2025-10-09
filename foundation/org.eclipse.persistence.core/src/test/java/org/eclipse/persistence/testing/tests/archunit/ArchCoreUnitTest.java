/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Test;

import java.util.Arrays;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchCoreUnitTest {

    // Verify that package org.eclipse.persistence.internal.localization doesn't have any dependencies to other packages
    @Test
    public void checkLocalization() {
        final String[] sourcePackageNames = new String[]{
                "org.eclipse.persistence.internal.localization"
        };

        checkPackageDependencies(sourcePackageNames, null);
    }

    // Verify that package org.eclipse.persistence.logging doesn't have any dependencies to other packages than resource bundles
    @Test
    public void checkLogging() {
        final String[] sourcePackageNames = new String[]{
                "org.eclipse.persistence.logging"
        };
        final String[] dependencyPackages = new String[]{
                "org.eclipse.persistence.internal.localization"};

        checkPackageDependencies(sourcePackageNames, dependencyPackages);
    }

    // Verify that package org.eclipse.persistence.logging.jul doesn't have any dependencies to other packages than resource bundles
    @Test
    public void checkLoggingJul() {
        final String[] sourcePackageNames = new String[]{
                "org.eclipse.persistence.logging.jul"
        };
        final String[] dependencyPackages = new String[]{
                "org.eclipse.persistence.logging",
                "org.eclipse.persistence.internal.localization"};

        checkPackageDependencies(sourcePackageNames, dependencyPackages);
    }

    private void checkPackageDependencies(String[] sourcePackageNames, String[] dependencyPackageNames) {
        //Add to the checked package list Java system packages and source package
        String[] defaultPackageNames = new String[sourcePackageNames.length + 1];
        defaultPackageNames[0] = "java..";
        System.arraycopy(sourcePackageNames, 0, defaultPackageNames, 1, sourcePackageNames.length);
        String[] packageNames = defaultPackageNames;
        if (dependencyPackageNames != null) {
            packageNames = Arrays.copyOf(defaultPackageNames, defaultPackageNames.length + dependencyPackageNames.length);
            System.arraycopy(dependencyPackageNames, 0, packageNames, defaultPackageNames.length, dependencyPackageNames.length);
        }
        JavaClasses importedClasses = new ClassFileImporter().importPackages(sourcePackageNames);
        ArchRule rule = classes().that().resideInAnyPackage(sourcePackageNames)
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(packageNames);
        rule.check(importedClasses);
    }
}
