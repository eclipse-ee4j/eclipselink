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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.bind.JsonbException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 */
public class Marshaller {
    private static final String DOUBLE_INFINITY = "INFINITY";
    private static final String DOUBLE_NAN = "NaN";
    private static final String NULL = "null";

    private final Context context;

    public Marshaller(Context context) {
        this.context = context;
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

    public String marshall(Object object, Type runtimeType) throws JsonbException {
        // TODO fix runtimeType when clearance comes
        return marshall(object);
    }

    public void marshall(Object object, Appendable appendable) throws IOException {
        appendable.append(marshallInternal(object));
    }

    public void marshall(Object object, Type runtimeType, Appendable appendable) throws IOException {
        // TODO fix runtimeType when clearance comes
        marshall(object, appendable);
    }

    public void marshall(Object object, OutputStream stream) throws IOException {
        stream.write(marshallInternal(object).getBytes("UTF-8"));
    }

    public void marshall(Object object, Type runtimeType, OutputStream stream) throws IOException {
        // TODO fix runtimeType
        marshall(object, stream);
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

        } else if (object instanceof CharSequence
                || object instanceof Character
                || object.getClass().isEnum()
                || object instanceof URI
                || object instanceof URL) {
            return quoteString(object.toString());

        } else if (object instanceof Double) {
            return marshallDouble((Double) object);

        } else if (object instanceof Number || object instanceof Boolean) {
            return object.toString();

        } else if (object instanceof Optional) {
            return marshallInternal(((Optional) object).get());

        } else if (object instanceof OptionalInt) {
            return String.valueOf(((OptionalInt) object).getAsInt());

        } else if (object instanceof OptionalLong) {
            return String.valueOf(((OptionalLong) object).getAsLong());

        } else if (object instanceof OptionalDouble) {
            return String.valueOf(((OptionalDouble) object).getAsDouble());

        } else if (object instanceof JsonObject) {
            return marshallJsonObject((JsonObject) object);

        } else if (object instanceof JsonStructure) {
            return marshallJsonStructure((JsonStructure) object);

        } else if (object instanceof JsonString) {
            return quoteString(object.toString());

        } else if (object instanceof JsonValue) {
            return object.toString();

        } else if (object instanceof Instant) {
            return quoteString(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format((Instant) object));

        } else if (object instanceof Date) {
            return marshallCalendar(new Calendar.Builder().setInstant((Date) object).build());

        } else if (object instanceof Calendar) {
            return marshallCalendar((Calendar) object);

        } else if (object instanceof TimeZone) {
            return quoteString(((TimeZone) object).getID());

        } else if (object instanceof Duration || object instanceof Period) {
            return quoteString(object.toString());

        } else if (object instanceof ZoneId) {
            return quoteString(((ZoneId) object).getId());

        } else if (object instanceof LocalDate) {
            return quoteString(((LocalDate) object).format(DateTimeFormatter.ISO_LOCAL_DATE));

        } else if (object instanceof LocalTime) {
            return quoteString(((LocalTime) object).format(DateTimeFormatter.ISO_LOCAL_TIME));

        } else if (object instanceof LocalDateTime) {
            return quoteString(((LocalDateTime) object).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        } else if (object instanceof ZonedDateTime) {
            return quoteString(((ZonedDateTime) object).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        } else if (object instanceof OffsetTime) {
            return quoteString(((OffsetTime) object).format(DateTimeFormatter.ISO_OFFSET_TIME));

        } else if (object instanceof OffsetDateTime) {
            return quoteString(((OffsetDateTime) object).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        } else if (object instanceof Collection) {
            return marshallCollection((Collection<?>) object);

        } else if (object instanceof Map) {
            return marshallMap((Map<?, ?>) object);

        } else if (object.getClass().isArray()) {
            return marshallArray(object);

        } else {
            return marshallObject(object);
        }
    }

    private String marshallDouble(Double value) {
        if (value.isInfinite()) {
            return quoteString(DOUBLE_INFINITY);
        } else if (value.isNaN()) {
            return quoteString(DOUBLE_NAN);
        }
        return value.toString();
    }

    private String marshallJsonObject(JsonObject object) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(object);
        jsonWriter.close();

        return stringWriter.toString();
    }

    private String marshallJsonStructure(JsonStructure structure) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.write(structure);
        jsonWriter.close();

        return stringWriter.toString();
    }

    private String marshallCalendar(Calendar calendar) {
        final LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(), ZoneOffset.systemDefault());

        DateTimeFormatter formatter;
        if (calendar.isSet(Calendar.HOUR)) {
            formatter = DateTimeFormatter.ISO_DATE_TIME;
        } else {
            formatter = DateTimeFormatter.ISO_DATE;
        }

        return quoteString(localDate.format(formatter));
    }

    private String marshallObject(Object object) {
        // Deal with inheritance
        final List<FieldModel> allFields = new LinkedList<>();
        for (Class clazz = object.getClass(); clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
            final List<FieldModel> fields = context.getClassModel(clazz).getFieldModels();
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
