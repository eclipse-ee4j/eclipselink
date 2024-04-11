/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
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
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar4_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar5_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_1;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_2;

/**
 * This utility class provides an easy way of creating a list of {@link JPQLGrammar}, which is
 * easier to update when a new grammar is added.
 *
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
            case VERSION_3_1: {
                return new JPQLGrammar[] {
                        JPQLGrammar3_1.instance()
                };
            }
            case VERSION_3_2: {
                return new JPQLGrammar[] {
                        JPQLGrammar3_2.instance()
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
            case VERSION_4_0: {
                return new JPQLGrammar[] {
                    EclipseLinkJPQLGrammar4_0.instance()
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
            case VERSION_3_1: {
                return new JPQLGrammar[] {
                    JPQLGrammar3_1.instance(),
                    EclipseLinkJPQLGrammar4_0.instance()
                };
            }
            case VERSION_3_2: {
                return new JPQLGrammar[] {
                        JPQLGrammar3_2.instance(),
                        EclipseLinkJPQLGrammar5_0.instance()
                };
            }
            default: {
                return new JPQLGrammar[0];
            }
        }
    }
}
