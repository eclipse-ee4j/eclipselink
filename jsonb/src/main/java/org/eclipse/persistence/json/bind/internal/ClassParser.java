package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.FieldModel;

import java.lang.reflect.Field;

/**
 * Created a class internal model.
 *
 * @author Dmitry Kornilov
 */
class ClassParser {
    public ClassModel parse(Class clazz) {
        final ClassModel classModel = new ClassModel(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            final FieldModel fieldModel = parseField(field);
            if (fieldModel != null) {
                classModel.getFieldModels().add(fieldModel);
            }
        }
        return classModel;
    }

    private FieldModel parseField(Field field) {
        return new FieldModel(field.getName(), field.getDeclaringClass());
    }
}
