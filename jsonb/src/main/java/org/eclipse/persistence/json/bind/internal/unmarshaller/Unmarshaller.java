package org.eclipse.persistence.json.bind.internal.unmarshaller;


import org.eclipse.persistence.json.bind.internal.JsonTextProcessor;
import org.eclipse.persistence.json.bind.internal.MappingContext;

import javax.json.Json;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * JSONB unmarshaller.
 * Uses {@link JsonParser} to navigate through json string.
 *
 * @author Roman Grigoriadi
 */
public class Unmarshaller extends JsonTextProcessor {

    private final JsonParser parser;

    private final Type rootType;

    /**
     * Stack of processed objects.
     * As events are discovered by {@link JsonParser} objects are created and pushed to this stack.
     */
    private CurrentItem<?> currentItem;

    /**
     * Currently processed JSON item key name.
     */
    private String currentFieldName;

    /**
     * Create unmarshaller instance
     * @param mappingContext Context of class mappings.
     * @param json JSON to parse.
     * @param rootType Class of a root object to be created.
     */
    public Unmarshaller(MappingContext mappingContext, String json, Type rootType) {
        super(mappingContext);
        this.rootType = rootType;
        this.parser = Json.createParser(new StringReader(json));
    }

    /**
     * Drive the {@link JsonParser} and processes its events.
     * @param <T> Type of result.
     * @return Result instance of a root object.
     */
    @SuppressWarnings("unchecked")
    public <T> T parse() {
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_OBJECT:
                case START_ARRAY:
                    onObjectStarted(JsonValueType.of(event));
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    onObjectEnded();
                    break;
                case VALUE_FALSE:
                case VALUE_TRUE:
                case VALUE_STRING:
                case VALUE_NUMBER:
                    onValue(parser.getString(), JsonValueType.of(event));
                    break;
                case VALUE_NULL:
                    onValue(null, JsonValueType.NULL);
                    break;
                case KEY_NAME:
                    currentFieldName = parser.getString();
                    break;
                default:
                    throw new JsonbException("Unexpected parser event: " + event);
            }
        }
        return (T) currentItem.getInstance();
    }

    /**
     * Determine class mappings and create an instance of a new item.
     * Currently processed item is pushed to stack, for waiting till new object is finished.
     */
    private void onObjectStarted(JsonValueType jsonValueType) {
        //Create root item object when parser encounters first parenthesis.
        if (currentItem == null) {
            currentItem = new CurrentItemBuilder(mappingContext).withType(rootType != Object.class ? rootType : jsonValueType.getConversionType()).build();
            return;
        }
        CurrentItem wrapper = currentItem;
        currentItem = wrapper.newItem(currentFieldName, jsonValueType);
    }

    private void onObjectEnded() {
        //root object finished
        if (currentItem.getWrapper() == null) {
            return;
        }
        CurrentItem<?> finished = currentItem;
        finished.getWrapper().appendItem(finished);
        currentItem = finished.getWrapper();
    }

    /**
     * Create supported type from JSON value.
     * @param value A JSON value.
     */
    private void onValue(String value, JsonValueType type) {
        currentItem.appendValue(currentFieldName, value, type);
    }


}
