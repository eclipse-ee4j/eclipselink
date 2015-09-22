/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.model.FieldModel;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 */
public class Marshaller extends JsonTextProcessor {

    private Type rootRuntimeType;

    public Marshaller(MappingContext mappingContext) {
        super(mappingContext);
    }

    public Marshaller(MappingContext mappingContext, Type rootRuntimeType) {
        super(mappingContext);
        this.rootRuntimeType = rootRuntimeType;
    }

    /**
     * Marshals a given object.
     *
     * @param object object to marshal.
     * @return JSON representation of object
     */
    public String marshall(Object object) {
        return marshallInternal(object);
    }

    public void marshall(Object object, Appendable appendable) throws IOException {
        appendable.append(marshallInternal(object));
    }

    public void marshall(Object object, OutputStream stream) throws IOException {
        stream.write(marshallInternal(object).getBytes("UTF-8"));
    }


    /**
     * Marshals a given object.
     *
     * @param object object to marshal.
     * @return JSON representation of object
     */
    private String marshallInternal(Object object) {
        if (object == null
                || object instanceof Optional && !((Optional) object).isPresent()
                || object instanceof OptionalInt && !((OptionalInt) object).isPresent()
                || object instanceof OptionalLong && !((OptionalLong) object).isPresent()
                || object instanceof OptionalDouble && !((OptionalDouble) object).isPresent()) {
            return NULL;

        } else if (object instanceof Optional) {
            return marshallInternal(((Optional) object).get());

        } else if (object instanceof Collection) {
            return marshallCollection((Collection<?>) object);

        } else if (object instanceof Map) {
            return marshallMap((Map<?, ?>) object);

        } else if (object.getClass().isArray()) {
            return marshallArray(object);

        } else if (converter.supportsToJson(object.getClass())) {
            return converter.toJson(object);

        } else {
            return marshallObject(object);
        }
    }

    private String marshallObject(Object object) {
        // Deal with inheritance
        final List<FieldModel> allFields = new LinkedList<>();
        for (Class clazz = object.getClass(); clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
            final List<FieldModel> fields = new ArrayList<>(mappingContext.getOrCreateClassModel(clazz).getFields().values());
            Collections.sort(fields);
            allFields.addAll(0, fields);
        }

        return allFields.stream()
                .map((model) -> marshallField(object, model))
                .filter(Objects::nonNull)
                .collect(joining(",", "{", "}"));
    }

    private String marshallField(Object object, FieldModel fieldModel) {
        final Object value = fieldModel.getValue(object);
        if (value != null) {
            if (value instanceof OptionalInt && !((OptionalInt) value).isPresent()
                    || value instanceof OptionalLong && !((OptionalLong) value).isPresent()
                    || value instanceof OptionalDouble && !((OptionalDouble) value).isPresent()) {
                return null;
            }
            return keyValue(fieldModel.getWriteName(), marshallInternal(value));
        }

        // Null value is returned in case this field doesn't need to be marshaled
        return null;
    }

    private String marshallArray(Object array) {
        if (array.getClass().getComponentType().isPrimitive()) {
            return marshallCollection(boxArray(array));
        } else {
            return marshallCollection(Arrays.asList((Object[])array));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> boxArray(Object primitiveArray) {
        final int length = Array.getLength(primitiveArray);
        final Collection<T> result = new ArrayList<>(length);

        for (int i = 0; i < Array.getLength(primitiveArray); i++) {
            final Object wrapped = Array.get(primitiveArray, i);
            result.add((T) wrapped);
        }
        return result;
    }

    private String marshallCollection(Collection<?> collection) {
        return collection.stream()
                .map(this::marshallInternal)
                .collect(joining(",", "[", "]"));
    }

    private String marshallMap(Map<?, ?> map) {
        return map.keySet().stream()
                .map((key) -> keyValue(key.toString(), marshallInternal(map.get(key))))
                .collect(joining(",", "{", "}"));
    }

    private String keyValue(String key, Object value) {
        return quoteString(key) + ":" + value;
    }

    private String quoteString(String string) {
        return quoteString("\"", string);
    }

    private String quoteString(String quote, String string) {
        return String.join("", quote, string, quote);
    }
}
