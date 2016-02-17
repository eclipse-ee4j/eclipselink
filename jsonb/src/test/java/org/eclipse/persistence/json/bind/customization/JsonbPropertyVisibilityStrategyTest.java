/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.customization;

import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbVisibility;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Tests custom {@link PropertyVisibilityStrategy}
 *
 * @author Roman Grigoriadi
 */
public class JsonbPropertyVisibilityStrategyTest {

    public static class FieldPojo {

        private String afield;
        private String bfield;
        public String cfield;
        public String dfield;

        public FieldPojo(String afield, String bfield, String cfield, String dfield) {
            this.afield = afield;
            this.bfield = bfield;
            this.cfield = cfield;
            this.dfield = dfield;
        }
    }

    public static class GetterPojo {
        public String getAgetter() {
            return "avalue";
        }
        public String getBgetter() {
            return "bvalue";
        }
        private String getCgetter() {
            return "cvalue";
        }
        private String getDgetter() {
            return "dvalue";
        }
    }

    @JsonbVisibility(TestVisibilityStrategy.class)
    public static final class AnnotatedPojo {

        private String afield;
        private String bfield;
        public String cfield;
        public String dfield;

        public AnnotatedPojo(String afield, String bfield, String cfield, String dfield) {
            this.afield = afield;
            this.bfield = bfield;
            this.cfield = cfield;
            this.dfield = dfield;
        }

        public String getAgetter() {
            return "avalue";
        }
        public String getBgetter() {
            return "bvalue";
        }
        private String getCgetter() {
            return "cvalue";
        }
        private String getDgetter() {
            return "dvalue";
        }
    }


    public static final class TestVisibilityStrategy implements PropertyVisibilityStrategy {
        @Override
        public boolean isVisible(Field field) {
            final String fieldName = field.getName();
            return fieldName.equals("bfield") || fieldName.equals("cfield");
        }

        @Override
        public boolean isVisible(Method method) {
            final String methodName = method.getName();
            return methodName.equals("getBgetter") || methodName.equals("getCgetter");
        }
    }

    /**
     * Tests applying for both public and nonpublic fields.
     */
    @Test
    public void testFieldVisibilityStrategy() {
        JsonbConfig customizedConfig = new JsonbConfig();
        customizedConfig.setProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY, new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                final String fieldName = field.getName();
                return fieldName.equals("afield") || fieldName.equals("dfield");
            }

            @Override
            public boolean isVisible(Method method) {
                throw new IllegalStateException("Not supported");
            }
        });

        FieldPojo fieldPojo = new FieldPojo("avalue", "bvalue", "cvalue", "dvalue");

        Jsonb jsonb = JsonbBuilder.create(customizedConfig);
        assertEquals("{\"afield\":\"avalue\",\"dfield\":\"dvalue\"}", jsonb.toJson(fieldPojo));
    }

    /**
     * Tests applying for both public and nonpublic getters.
     */
    @Test
    public void testMethodVisibilityStrategy() {
        JsonbConfig customizedConfig = new JsonbConfig();
        customizedConfig.setProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY, new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                throw new IllegalStateException("Not supported");
            }

            @Override
            public boolean isVisible(Method method) {
                final String methodName = method.getName();
                return methodName.equals("getAgetter") || methodName.equals("getDgetter");
            }
        });

        GetterPojo getterPojo = new GetterPojo();

        Jsonb jsonb = JsonbBuilder.create(customizedConfig);
        assertEquals("{\"agetter\":\"avalue\",\"dgetter\":\"dvalue\"}", jsonb.toJson(getterPojo));
    }

    @Test
    public void testAnnotatedPojo() {
        final Jsonb jsonb = JsonbBuilder.create();
        AnnotatedPojo fieldPojo = new AnnotatedPojo("avalue", "bvalue", "cvalue", "dvalue");
        assertEquals("{\"bfield\":\"bvalue\",\"bgetter\":\"bvalue\",\"cfield\":\"cvalue\",\"cgetter\":\"cvalue\"}", jsonb.toJson(fieldPojo));
    }
}
