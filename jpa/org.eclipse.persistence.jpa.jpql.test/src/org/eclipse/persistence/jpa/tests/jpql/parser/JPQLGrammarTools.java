/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;

/**
 * This utility class provides an easy way of creating a list of {@link JPQLGrammar}, which is
 * easier to update when a new grammar is added.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class JPQLGrammarTools {

    /**
     * Cannot instantiate this <code>JPQLGrammarTools</code>.
     */
    private JPQLGrammarTools() {
        super();
    }

    public static JPQLGrammar[] allDefaultJPQLGrammars() {
        return allDefaultJPQLGrammars(JPAVersion.VERSION_1_0);
    }

    public static JPQLGrammar[] allDefaultJPQLGrammars(JPAVersion minVersion) {
        switch (minVersion) {
            case VERSION_1_0: {
                return new JPQLGrammar[] {
                    JPQLGrammar1_0.instance(),
                    JPQLGrammar2_0.instance(),
                    JPQLGrammar2_1.instance()
                };
            }
            case VERSION_2_0: {
                return new JPQLGrammar[] {
                    JPQLGrammar2_0.instance(),
                    JPQLGrammar2_1.instance()
                };
            }
            case VERSION_2_1: {
                return new JPQLGrammar[] {
                    JPQLGrammar2_1.instance()
                };
            }
            default: {
                return new JPQLGrammar[0];
            }
        }
    }

    public static JPQLGrammar[] allEclipseLinkJPQLGrammars() {
        return allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_1_x);
    }

    public static JPQLGrammar[] allEclipseLinkJPQLGrammars(EclipseLinkVersion minVersion) {
        switch (minVersion) {
            case VERSION_1_x: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar1.instance(),
                    EclipseLinkJPQLGrammar2_0.instance(),
                    EclipseLinkJPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar2_2.instance(),
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_0: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar2_0.instance(),
                    EclipseLinkJPQLGrammar2_2.instance(),
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_1: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar2_2.instance(),
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_2: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar2_2.instance(),
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_3: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_4: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_5: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            default: {
                return new JPQLGrammar[0];
            }
        }
    }

    public static JPQLGrammar[] allJPQLGrammars() {
        return allJPQLGrammars(JPAVersion.VERSION_1_0);
    }

    public static JPQLGrammar[] allJPQLGrammars(JPAVersion minVersion) {
        switch (minVersion) {
            case VERSION_1_0: {
                return new JPQLGrammar[] {
                    JPQLGrammar1_0.instance(),
                    JPQLGrammar2_0.instance(),
                    JPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar1.instance(),
                    EclipseLinkJPQLGrammar2_0.instance(),
                    EclipseLinkJPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar2_2.instance(),
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_0: {
                return new JPQLGrammar[] {
                    JPQLGrammar2_0.instance(),
                    JPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar2_0.instance(),
                    EclipseLinkJPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar2_2.instance(),
                    EclipseLinkJPQLGrammar2_3.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            case VERSION_2_1: {
                return new JPQLGrammar[] {
                    JPQLGrammar2_1.instance(),
                    EclipseLinkJPQLGrammar2_4.instance(),
                    EclipseLinkJPQLGrammar2_5.instance()
                };
            }
            default: {
                return new JPQLGrammar[0];
            }
        }
    }
}
