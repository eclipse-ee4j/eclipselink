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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistExtension;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals.ClassType;
import org.eclipse.persistence.jpa.tests.jpql.EclipseLinkVersionTools;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * @version 2.5.1
 * @since 2.5
 * @author Pascal Filion
 */
public final class EclipseLinkEclipseLinkContentAssistExtensionTest extends AbstractContentAssistTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected ContentAssistExtension buildContentAssistExtension() {
        return new ContentAssistExtension() {
            public Iterable<String> classNames(String prefix, ClassType type) {
                if (type == ClassType.INSTANTIABLE) {
                    return filter(EclipseLinkEclipseLinkContentAssistExtensionTest.this.classNames(), prefix);
                }
                return filter(EclipseLinkEclipseLinkContentAssistExtensionTest.this.enumTypes(), prefix);
            }
            public Iterable<String> columnNames(String tableName, String prefix) {
                return filter(EclipseLinkEclipseLinkContentAssistExtensionTest.this.columnNames(tableName), prefix);
            }
            public Iterable<String> tableNames(String prefix) {
                return filter(EclipseLinkEclipseLinkContentAssistExtensionTest.this.tableNames(), prefix);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> clauses(String afterIdentifier, String beforeIdentifier, boolean subquery) {

        List<String> proposals = super.clauses(afterIdentifier, beforeIdentifier, subquery);

        if (subquery) {
            return proposals;
        }

        if (afterIdentifier == SELECT) {

            if (beforeIdentifier != FROM     &&
                beforeIdentifier != WHERE    &&
                beforeIdentifier != GROUP_BY &&
                beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == FROM) {

            if (beforeIdentifier != WHERE    &&
                beforeIdentifier != GROUP_BY &&
                beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == WHERE) {

            if (beforeIdentifier != GROUP_BY &&
                beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == GROUP_BY) {

            if (beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == HAVING) {

            if (beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == ORDER_BY) {

            if (beforeIdentifier != UNION  &&
                beforeIdentifier != EXCEPT &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }

        return proposals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> fromClauseInternalClauses(String afterIdentifier) {

        if (EclipseLinkVersionTools.isNewerThan2_4(getGrammar())) {
            List<String> proposals = new ArrayList<String>();

            if (afterIdentifier == FROM) {
                proposals.addAll(super.fromClauseInternalClauses(FROM));
                proposals.add(START_WITH);
                proposals.add(CONNECT_BY);
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == JOIN) {
                proposals.add(START_WITH);
                proposals.add(CONNECT_BY);
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == START_WITH) {
                proposals.add(CONNECT_BY);
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == CONNECT_BY) {
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == ORDER_SIBLINGS_BY) {
                proposals.add(AS_OF);
            }

            return proposals;
        }

        return super.fromClauseInternalClauses(afterIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        return true;
    }
}
