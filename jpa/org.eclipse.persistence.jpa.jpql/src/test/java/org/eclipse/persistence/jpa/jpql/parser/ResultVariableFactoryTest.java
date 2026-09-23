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

package org.eclipse.persistence.jpa.jpql.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.jpa.jpql.WordParser;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
public final class ResultVariableFactoryTest {

    public ResultVariableFactoryTest() {
    }

    @Test
    public void testBuildExpressionDoesNotShareSelectClauseVisitor() throws Exception {

        ResultVariableFactory factory = new ResultVariableFactory();
        CountDownLatch supportedParentVisited = new CountDownLatch(1);
        CountDownLatch unsupportedBuildCompleted = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<ResultVariable> supportedResult = executor.submit(() -> buildResultVariable(
                    factory,
                    new TestExpression(true, supportedParentVisited, unsupportedBuildCompleted)
            ));

            boolean parentVisited = supportedParentVisited.await(10, TimeUnit.SECONDS);
            if (!parentVisited && supportedResult.isDone()) {
                supportedResult.get();
            }
            assertTrue(parentVisited);

            Future<ResultVariable> unsupportedResult = executor.submit(() -> {
                try {
                    return buildResultVariable(factory, new TestExpression(false, null, null));
                }
                finally {
                    unsupportedBuildCompleted.countDown();
                }
            });

            assertFalse(unsupportedResult.get(10, TimeUnit.SECONDS).hasSelectExpression());
            assertTrue(supportedResult.get(10, TimeUnit.SECONDS).hasSelectExpression());
        }
        finally {
            unsupportedBuildCompleted.countDown();
            executor.shutdownNow();
        }
    }

    private ResultVariable buildResultVariable(ResultVariableFactory factory,
                                               AbstractExpression parent) {

        AbstractExpression expression = new UnknownExpression(null, "e");
        return (ResultVariable) factory.buildExpression(
                parent,
                new WordParser("AS result"),
                Expression.AS,
                null,
                expression,
                false
        );
    }

    private static final class TestExpression extends AbstractExpression implements ParentExpression {

        private final List<IdentificationVariable> identificationVariablesWithoutAlias = new ArrayList<>();
        private final CountDownLatch supportedParentVisited;
        private final boolean supported;
        private final CountDownLatch unsupportedBuildCompleted;

        TestExpression(boolean supported,
                       CountDownLatch supportedParentVisited,
                       CountDownLatch unsupportedBuildCompleted) {

            super(null);
            this.supported = supported;
            this.supportedParentVisited = supportedParentVisited;
            this.unsupportedBuildCompleted = unsupportedBuildCompleted;
        }

        @Override
        public void accept(ExpressionVisitor visitor) {

            if (supported) {
                visitor.visit(new SelectClause(this));
                supportedParentVisited.countDown();

                try {
                    assertTrue(unsupportedBuildCompleted.await(10, TimeUnit.SECONDS));
                }
                catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(exception);
                }
            }
            else {
                visitor.visit(new CollectionMemberExpression(this, null));
            }
        }

        @Override
        public void acceptChildren(ExpressionVisitor visitor) {
        }

        @Override
        public JPQLQueryBNF getQueryBNF() {
            return null;
        }

        @Override
        public List<IdentificationVariable> getIdentificationVariablesWithoutAlias() {
            return identificationVariablesWithoutAlias;
        }

        @Override
        public boolean isGenerateImplicitThisAlias() {
            return false;
        }

        @Override
        protected void parse(WordParser wordParser, boolean tolerant) {
        }

        @Override
        public void setGenerateImplicitThisAlias(boolean generateImplicitThisAlias) {
        }

        @Override
        protected void toParsedText(StringBuilder writer, boolean actual) {
        }
    }
}
