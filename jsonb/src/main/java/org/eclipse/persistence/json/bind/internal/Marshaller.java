package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.FieldModel;

import javax.json.bind.JsonbException;
import java.lang.reflect.Field;

/**
 * Jsonb marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 */
public class Marshaller {
    private Context context;

    public Marshaller(Context context) {
        this.context = context;
    }

    public String marshall(Object object) {
        final StringBuilder builder = new StringBuilder();
        marshallObject(builder, object);
        return builder.toString();
    }

    private void marshallObject(StringBuilder builder, Object object) {
        if (object == null) {
            builder.append("null");
        } else if (object instanceof String || object instanceof Character) {
            builder.append("\"").append(object).append("\"");
        } else if (object instanceof Number || object instanceof Boolean) {
            builder.append(object.toString());
        } else {
            final ClassModel classModel = context.getClassModel(object.getClass());
            builder.append("{");

            boolean putComma = false;
            for (FieldModel fieldModel : classModel.getFieldModels()) {
                if (putComma) {
                    builder.append(",");
                } else {
                    putComma = true;
                }
                marshallField(builder, object, fieldModel);
            }
            builder.append("}");
        }
    }

    private void marshallField(StringBuilder builder, Object object, FieldModel fieldModel) {
        builder.append("\"").append(fieldModel.getWriteName()).append("\"").append(":");
        try {
            final Field field = object.getClass().getDeclaredField(fieldModel.getName());
            field.setAccessible(true);
            final Object value = field.get(object);
            marshallObject(builder, value);
            // TODO type dependent marshalling
        } catch (IllegalAccessException | NoSuchFieldException e) {
            // TODO logging
            // TODO error message enum
            throw new JsonbException("Marshalling error", e);
        }
    }
}
