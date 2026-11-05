/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.persistence.asm.Opcodes;
import org.eclipse.persistence.asm.Type;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLUMN_RESULT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLUMN_RESULTS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONSTRUCTOR_RESULT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONSTRUCTOR_RESULTS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY_RESULT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY_RESULTS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_NATIVE_QUERY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_QUERY_OPTIONS;

/**
 * INTERNAL: Adapts a method declaration to the existing native named-query metadata.
 * No method is invoked and no declaring class is initialized during discovery.
 */
public final class NativeQueryMetadata extends NamedNativeQueryMetadata {

    NativeQueryMetadata(MetadataMethod method, MetadataAccessor accessor) {
        super(asNamedQuery(method), accessor);
    }

    private static void addHint(List<MetadataAnnotation> hints, String name, Object value) {
        if (hints.stream().noneMatch(hint -> name.equals(hint.getAttributeString("name")))) {
            MetadataAnnotation hint = new MetadataAnnotation();
            hint.addAttribute("name", name);
            hint.addAttribute("value", value.toString());
            hints.add(hint);
        }
    }

    private static MetadataAnnotation asNamedQuery(MetadataMethod method) {
        MetadataAnnotation declaration = method.getAnnotation(JPA_NATIVE_QUERY);
        MetadataAnnotation query = new MetadataAnnotation();
        query.setName(declaration.getName());
        query.addAttribute("name", getQueryName(method));
        query.addAttribute("query", declaration.getAttributeString("value"));

        Object[] entities = getResults(method, JPA_ENTITY_RESULT, JPA_ENTITY_RESULTS);
        Object[] constructors = getResults(method, JPA_CONSTRUCTOR_RESULT, JPA_CONSTRUCTOR_RESULTS);
        Object[] columns = getResults(method, JPA_COLUMN_RESULT, JPA_COLUMN_RESULTS);
        query.addAttribute("entities", entities);
        query.addAttribute("classes", constructors);
        query.addAttribute("columns", columns);
        if (entities.length == 0 && constructors.length == 0 && columns.length == 0) {
            query.addAttribute("resultClass", getResultType(method));
        }

        MetadataAnnotation options = method.getAnnotation(JPA_QUERY_OPTIONS);
        if (options != null) {
            List<MetadataAnnotation> hints = new ArrayList<>();
            for (Object hint : options.getAttributeArray("hints")) {
                hints.add((MetadataAnnotation) hint);
            }
            String storeMode = options.getAttributeString("cacheStoreMode");
            String retrieveMode = options.getAttributeString("cacheRetrieveMode");
            addHint(hints, QueryHints.CACHE_STORE_MODE, storeMode == null ? "USE" : storeMode);
            addHint(hints, QueryHints.CACHE_RETRIEVE_MODE, retrieveMode == null ? "USE" : retrieveMode);
            Integer timeout = options.getAttributeInteger("timeout");
            if (timeout != null && timeout != -1) {
                addHint(hints, QueryHints.QUERY_TIMEOUT, timeout);
            }
            query.addAttribute("hints", hints.toArray());
            query.addAttribute("flush", options.getAttributeString("flush"));
        }
        return query;
    }

    /** INTERNAL: Render source names only at the public named-query boundary. */
    static String getQueryName(MetadataMethod method) {
        return method.getMetadataClass().getCanonicalName() + "#" + method.getName() + "("
                + method.getParameters().stream().map(name -> getTypeName(method, name))
                        .collect(Collectors.joining(",")) + ")";
    }

    private static Object[] getResults(MetadataMethod method, String single, String multiple) {
        List<Object> results = new ArrayList<>();
        MetadataAnnotation result = method.getAnnotation(single);
        if (result != null) {
            results.add(result);
        }
        MetadataAnnotation container = method.getAnnotation(multiple);
        if (container != null) {
            results.addAll(Arrays.asList(container.getAttributeArray("value")));
        }
        return results.toArray();
    }

    private static String getResultType(MetadataMethod method) {
        String type = method.getReturnType();
        if ("java.util.List".equals(type) || "java.util.stream.Stream".equals(type)) {
            String signature = method.getGenericReturnSignature();
            int argument = signature == null ? -1 : signature.indexOf('<');
            if (argument >= 0) {
                StringBuilder erased = new StringBuilder();
                int depth = 0;
                boolean classType = false;
                for (int index = argument + 1; index < signature.length(); index++) {
                    char next = signature.charAt(index);
                    if (next == '<') {
                        depth++;
                    } else if (next == '>' && depth > 0) {
                        depth--;
                    } else if (depth == 0) {
                        if (!classType) {
                            if (next == '[') {
                                erased.append(next);
                                continue;
                            } else if (next == 'L') {
                                classType = true;
                            } else if (!erased.isEmpty() && "BCDFIJSZ".indexOf(next) >= 0) {
                                return Type.getType(erased.append(next).toString()).getClassName();
                            } else {
                                break; // Raw, wildcard and type-variable result classes are not inferred.
                            }
                        }
                        erased.append(next == '.' ? '$' : next);
                        if (next == ';') {
                            return Type.getType(erased.toString()).getClassName();
                        }
                    }
                }
            }
            throw new IllegalArgumentException("Cannot determine the result class of @NativeQuery "
                    + getQueryName(method) + " from its return type");
        }
        return type;
    }

    private static String getTypeName(MetadataMethod method, String name) {
        String type = name.startsWith("[") ? Type.getType(name.replace('.', '/')).getClassName() : name;
        int array = type.indexOf('[');
        String component = array < 0 ? type : type.substring(0, array);
        String canonical = method.getMetadataFactory().getMetadataClass(component, true).getCanonicalName();
        return array < 0 ? canonical : canonical + type.substring(array);
    }

    /** INTERNAL: Collect declarations without changing the existing overload or query registries. */
    public static void process(MetadataClass type, MetadataAccessor accessor) {
        if (accessor instanceof ClassAccessor classAccessor && classAccessor.ignoreAnnotations()) {
            return;
        }
        for (MetadataMethod first : type.getMethods().values()) {
            for (MetadataMethod method = first; method != null; method = method.getNext()) {
                if ((method.getModifiers() & (Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC)) == 0
                        && method.isAnnotationPresent(JPA_NATIVE_QUERY)) {
                    accessor.getProject().addQuery(new NativeQueryMetadata(method, accessor));
                }
            }
        }
    }
}
