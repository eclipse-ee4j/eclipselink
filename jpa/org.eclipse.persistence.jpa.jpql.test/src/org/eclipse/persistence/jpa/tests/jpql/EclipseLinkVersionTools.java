/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;

/**
 * This utility is a shortcut to the various methods defined on {@link EclipseLinkVersion} when the
 * provider version is retrieved from {@link JPQLGrammar}.
 *
 * @version 2.6
 * @since 2.5
 * @author Pascal Filion
 */
public final class EclipseLinkVersionTools {

    /**
     * Creates a new <code>EclipseLinkVersionTools</code>.
     */
    private EclipseLinkVersionTools() {
        super();
    }

    public static boolean isEquals1_x(JPQLGrammar jpqlGrammar) {
        return isEquals1_x(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals1_x(String value) {
        return EclipseLinkVersion.VERSION_1_x == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_0(JPQLGrammar jpqlGrammar) {
        return isEquals2_0(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_0(String value) {
        return EclipseLinkVersion.VERSION_2_0 == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_1(JPQLGrammar jpqlGrammar) {
        return isEquals2_1(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_1(String value) {
        return EclipseLinkVersion.VERSION_2_1 == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_2(JPQLGrammar jpqlGrammar) {
        return isEquals2_2(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_2(String value) {
        return EclipseLinkVersion.VERSION_2_2 == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_3(JPQLGrammar jpqlGrammar) {
        return isEquals2_3(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_3(String value) {
        return EclipseLinkVersion.VERSION_2_3 == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_4(JPQLGrammar jpqlGrammar) {
        return isEquals2_4(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_4(String value) {
        return EclipseLinkVersion.VERSION_2_4 == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_5(JPQLGrammar jpqlGrammar) {
        return isEquals2_5(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_5(String value) {
        return EclipseLinkVersion.VERSION_2_5 == EclipseLinkVersion.value(value);
    }

    public static boolean isEquals2_6(JPQLGrammar jpqlGrammar) {
        return isEquals2_6(jpqlGrammar.getProviderVersion());
    }

    public static boolean isEquals2_6(String value) {
        return EclipseLinkVersion.VERSION_2_6 == EclipseLinkVersion.value(value);
    }

    public static boolean isNewerThan(String value, EclipseLinkVersion version) {
        return EclipseLinkVersion.value(value).isNewerThan(version);
    }

    public static boolean isNewerThan1_x(JPQLGrammar grammar) {
        return isNewerThan1_x(grammar.getProviderVersion());
    }

    public static boolean isNewerThan1_x(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_1_x);
    }

    public static boolean isNewerThan2_0(JPQLGrammar grammar) {
        return isNewerThan2_0(grammar.getProviderVersion());
    }

    public static boolean isNewerThan2_0(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_2_0);
    }

    public static boolean isNewerThan2_1(JPQLGrammar grammar) {
        return isNewerThan2_1(grammar.getProviderVersion());
    }

    public static boolean isNewerThan2_1(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_2_1);
    }

    public static boolean isNewerThan2_2(JPQLGrammar grammar) {
        return isNewerThan2_2(grammar.getProviderVersion());
    }

    public static boolean isNewerThan2_2(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_2_2);
    }

    public static boolean isNewerThan2_3(JPQLGrammar grammar) {
        return isNewerThan2_3(grammar.getProviderVersion());
    }

    public static boolean isNewerThan2_3(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_2_3);
    }

    public static boolean isNewerThan2_4(JPQLGrammar grammar) {
        return isNewerThan2_4(grammar.getProviderVersion());
    }

    public static boolean isNewerThan2_4(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_2_4);
    }

    public static boolean isNewerThan2_5(JPQLGrammar grammar) {
        return isNewerThan2_5(grammar.getProviderVersion());
    }

    public static boolean isNewerThan2_5(String value) {
        return isNewerThan(value, EclipseLinkVersion.VERSION_2_5);
    }

    public static boolean isNewerThanOrEqual(String value, EclipseLinkVersion version) {
        return EclipseLinkVersion.value(value).isNewerThanOrEqual(version);
    }

    public static boolean isNewerThanOrEqual1_x(JPQLGrammar grammar) {
        return isNewerThanOrEqual1_x(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual1_x(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_1_x);
    }

    public static boolean isNewerThanOrEqual2_0(JPQLGrammar grammar) {
        return isNewerThanOrEqual2_0(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual2_0(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_2_0);
    }

    public static boolean isNewerThanOrEqual2_1(JPQLGrammar grammar) {
        return isNewerThanOrEqual2_1(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual2_1(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_2_1);
    }

    public static boolean isNewerThanOrEqual2_2(JPQLGrammar grammar) {
        return isNewerThanOrEqual2_2(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual2_2(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_2_2);
    }

    public static boolean isNewerThanOrEqual2_3(JPQLGrammar grammar) {
        return isNewerThanOrEqual2_3(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual2_3(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_2_3);
    }

    public static boolean isNewerThanOrEqual2_4(JPQLGrammar grammar) {
        return isNewerThanOrEqual2_4(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual2_4(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_2_4);
    }

    public static boolean isNewerThanOrEqual2_5(JPQLGrammar grammar) {
        return isNewerThanOrEqual2_5(grammar.getProviderVersion());
    }

    public static boolean isNewerThanOrEqual2_5(String value) {
        return isNewerThanOrEqual(value, EclipseLinkVersion.VERSION_2_5);
    }

    public static boolean isOlderThan(String value, EclipseLinkVersion version) {
        return EclipseLinkVersion.value(value).isOlderThan(version);
    }

    public static boolean isOlderThan2_0(JPQLGrammar grammar) {
        return isOlderThan2_0(grammar.getProviderVersion());
    }

    public static boolean isOlderThan2_0(String value) {
        return isOlderThan(value, EclipseLinkVersion.VERSION_2_0);
    }

    public static boolean isOlderThan2_1(JPQLGrammar grammar) {
        return isOlderThan2_1(grammar.getProviderVersion());
    }

    public static boolean isOlderThan2_1(String value) {
        return isOlderThan(value, EclipseLinkVersion.VERSION_2_1);
    }

    public static boolean isOlderThan2_2(JPQLGrammar grammar) {
        return isOlderThan2_2(grammar.getProviderVersion());
    }

    public static boolean isOlderThan2_2(String value) {
        return isOlderThan(value, EclipseLinkVersion.VERSION_2_2);
    }

    public static boolean isOlderThan2_3(JPQLGrammar grammar) {
        return isOlderThan2_3(grammar.getProviderVersion());
    }

    public static boolean isOlderThan2_3(String value) {
        return isOlderThan(value, EclipseLinkVersion.VERSION_2_3);
    }

    public static boolean isOlderThan2_4(JPQLGrammar grammar) {
        return isOlderThan2_4(grammar.getProviderVersion());
    }

    public static boolean isOlderThan2_4(String value) {
        return isOlderThan(value, EclipseLinkVersion.VERSION_2_4);
    }

    public static boolean isOlderThan2_5(JPQLGrammar grammar) {
        return isOlderThan2_5(grammar.getProviderVersion());
    }

    public static boolean isOlderThan2_5(String value) {
        return isOlderThan(value, EclipseLinkVersion.VERSION_2_5);
    }

    public static boolean isOlderThanOrEqual(String value, EclipseLinkVersion version) {
        return EclipseLinkVersion.value(value).isOlderThanOrEqual(version);
    }

    public static boolean isOlderThanOrEqual2_0(JPQLGrammar grammar) {
        return isOlderThanOrEqual2_0(grammar.getProviderVersion());
    }

    public static boolean isOlderThanOrEqual2_0(String value) {
        return isOlderThanOrEqual(value, EclipseLinkVersion.VERSION_2_0);
    }

    public static boolean isOlderThanOrEqual2_1(JPQLGrammar grammar) {
        return isOlderThanOrEqual2_1(grammar.getProviderVersion());
    }

    public static boolean isOlderThanOrEqual2_1(String value) {
        return isOlderThanOrEqual(value, EclipseLinkVersion.VERSION_2_1);
    }

    public static boolean isOlderThanOrEqual2_2(JPQLGrammar grammar) {
        return isOlderThanOrEqual2_2(grammar.getProviderVersion());
    }

    public static boolean isOlderThanOrEqual2_2(String value) {
        return isOlderThanOrEqual(value, EclipseLinkVersion.VERSION_2_2);
    }

    public static boolean isOlderThanOrEqual2_3(JPQLGrammar grammar) {
        return isOlderThanOrEqual2_3(grammar.getProviderVersion());
    }

    public static boolean isOlderThanOrEqual2_3(String value) {
        return isOlderThanOrEqual(value, EclipseLinkVersion.VERSION_2_3);
    }

    public static boolean isOlderThanOrEqual2_4(JPQLGrammar grammar) {
        return isOlderThanOrEqual2_4(grammar.getProviderVersion());
    }

    public static boolean isOlderThanOrEqual2_4(String value) {
        return isOlderThanOrEqual(value, EclipseLinkVersion.VERSION_2_4);
    }

    public static boolean isOlderThanOrEqual2_5(JPQLGrammar grammar) {
        return isOlderThanOrEqual2_5(grammar.getProviderVersion());
    }

    public static boolean isOlderThanOrEqual2_5(String value) {
        return isOlderThanOrEqual(value, EclipseLinkVersion.VERSION_2_5);
    }
}
