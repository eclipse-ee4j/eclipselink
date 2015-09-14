package org.eclipse.persistence.json.bind.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A model for Java class.
 *
 * @author Dmitry Kornilov
 */
public class ClassModel {
    private final Class clazz;

    /**
     * Indicates that this class is nillable.
     */
    private boolean nillable;

    /**
     * A list of class fields.
     */
    private List<FieldModel> fields = new ArrayList<>();

    public ClassModel(Class clazz) {
        this.clazz = clazz;
    }

    public List<FieldModel> getFieldModels() {
        return fields;
    }
}
