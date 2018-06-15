/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/04/2014 - Rick Curtis
//       - 450010 : Add java se test bucket
//     01/13/2015 - Rick Curtis
//       - 438871 : Add support for writing statement terminator character(s) when generating ddl to script.
package org.eclipse.persistence.jpa.test.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class EmfRunnerInjector {
    private boolean _debug = false;
    private final Map<String, EntityManagerFactory> _emfs;

    public EmfRunnerInjector() {
        _emfs = new HashMap<String, EntityManagerFactory>();
    }

    public void close() {
        for (EntityManagerFactory emf : _emfs.values()) {
            try {
                d("Closing [ " + emf + " ]");
                emf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method will create / inject EntityManagerFactory and SQLListeners into test instances
     *
     * @param testInstance
     */
    public void inject(Object testInstance) throws Exception {
        if (testInstance == null) {
            return;
        }

        Class<?> cls = testInstance.getClass();
        // Check for duplicate injected EMFs
        Set<EntityManagerFactory> injectedEmfs = new HashSet<EntityManagerFactory>();
        Set<Field> injectedSqlListeneFields = new HashSet<Field>();

        // PU name -> Field
        Map<String, Field> annotatedSqlListenerFields = getSqlListenerFieldMap(cls);
        for (Field emfField : cls.getDeclaredFields()) {
            Emf emfAnno = emfField.getAnnotation(Emf.class);
            if (emfAnno == null) {
                continue;
            }
            SqlCollector sqlCollector = null;
            String emfName = emfAnno.name();
            Field listenerField = annotatedSqlListenerFields.get(emfName);

            if (listenerField != null) {
                sqlCollector = new SqlCollector();
            }
            Map<String,Object> props = null;
            if(testInstance instanceof PUPropertiesProvider) {
                props = ((PUPropertiesProvider)testInstance).getAdditionalPersistenceProperties(emfName);
            }
            EntityManagerFactory factory = getEntityManagerFactory(emfAnno, props);
            if (!injectedEmfs.add(factory)) {
                throw new RuntimeException("Attempted to inject the same EntityManagerFactory multiple times into "
                    + testInstance + ". Please remove the duplicate @Emf annotation, or change the name so that each "
                    + "annotation is for a distinct EntityManagerFactory.");
            }
            emfField.setAccessible(true);
            emfField.set(testInstance, factory);
            d("Injected " + factory + " into " + testInstance);

            if (sqlCollector != null) {
                ((JpaEntityManagerFactory) factory).getServerSession().getEventManager().addListener(sqlCollector);
                sqlCollector.inject(testInstance, listenerField);
                injectedSqlListeneFields.add(listenerField);
                d("Injected " + sqlCollector + " into " + listenerField);
            }
        }
        // Check for @SQLListeners that weren't injected
        validateSQLListenerAnnotations(annotatedSqlListenerFields, injectedSqlListeneFields);
    }

    private void validateSQLListenerAnnotations(Map<String, Field> sqlListeners, Set<Field> injectedSqlListeners) {
        boolean die = false;
        List<Field> uninjectedSqlFields = new ArrayList<Field>();
        for (Entry<String, Field> entry : sqlListeners.entrySet()) {
            String puName = entry.getKey();
            Field annotatedField = entry.getValue();
            if (!injectedSqlListeners.contains(annotatedField)) {
                System.err.println("Encountered @SQLListener annotation for pu [" + puName + "] on field ["
                    + annotatedField + "], but did not find a corresponding @Emf annotation for that pu name.");
                uninjectedSqlFields.add(annotatedField);
                die = true;
            }
        }
        if (die) {
            throw new RuntimeException("Error injecting @SQLListeners, review previous error messages.");
        }
    }

    private Map<String, Field> getSqlListenerFieldMap(Class<?> cls) {
        Map<String, Field> res = new HashMap<String, Field>();
        for (Field field : cls.getDeclaredFields()) {
            SQLListener listener = field.getAnnotation(SQLListener.class);
            if (listener != null) {
                if (!List.class.isAssignableFrom(field.getType())) {
                    throw new RuntimeException("@SQLListener anntation can only be placed on a List<String> field. Found on [" + field + "].");
                }
                String puName = listener.name();
                Field old = res.put(puName, field);
                if (old != null) {
                    // Found two SQLListener annotations for the same PU
                    throw new RuntimeException("Found multiple fields [" + old + ", " + field
                        + "] annotated as a SQLListener for pu [" + puName + "]. Remove one of the anntations.");
                }
            }
        }
        return res;
    }

    private EntityManagerFactory getEntityManagerFactory(Emf anno, Map<String, Object> additionalProperties) {
        String name = anno.name();

        // Check cache
        EntityManagerFactory emf = _emfs.get(name);
        if (emf != null) {
            return emf;
        }

        List<String> mappingFiles = Arrays.asList(anno.mappingFiles());
        List<String> classes = new ArrayList<String>();
        Properties persistenceProperties = new Properties();

        for (Class<?> cls : anno.classes()) {
            classes.add(cls.getName());
        }

        for (Property prop : anno.properties()) {
            persistenceProperties.put(prop.name(), prop.value());
        }

        if (additionalProperties != null) {
            persistenceProperties.putAll(additionalProperties);
        }


        if (anno.createTables() != DDLGen.NONE) {
            persistenceProperties.put(PersistenceUnitProperties.DDL_GENERATION, anno.createTables().toString());
        }

        SEPersistenceUnitInfo pu = createSEPUInfo(anno.name(), classes, mappingFiles, persistenceProperties);

        Properties props = new Properties();
        props.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, pu);

        emf = Persistence.createEntityManagerFactory(name, props);

        _emfs.put(name, emf);

        return emf;
    }

    private SEPersistenceUnitInfo createSEPUInfo(String puName, List<String> classes, List<String> mappingFiles,
        Properties persistenceProperties) {
        SEPersistenceUnitInfo pu = new SEPersistenceUnitInfo();
        pu.setClassLoader(Thread.currentThread().getContextClassLoader());
        pu.setPersistenceUnitName(puName);
        pu.setManagedClassNames(classes);
        pu.setMappingFileNames(mappingFiles);
        try {
            pu.setPersistenceUnitRootUrl(new File(".").toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        pu.setProperties(persistenceProperties);

        return pu;
    }

    private void d(String msg) {
        if (_debug) {
            System.err.println(msg);
        }
    }

    private class SqlCollector extends SessionEventAdapter {
        private List<String> _sql;

        SqlCollector() {
            _sql = new ArrayList<String>();
        }

        @Override
        public void preExecuteQuery(SessionEvent event) {
            super.preExecuteQuery(event);
            _sql.add(event.getQuery().getSQLString());
        }

        private void inject(Object instance, Field into) {
            into.setAccessible(true);
            try {
                into.set(instance, _sql);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
