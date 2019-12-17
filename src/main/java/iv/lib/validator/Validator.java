package iv.lib.validator;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class Validator {
    private static final String VALIDATOR_NOT_READY = "Validator not ready";
    private static final String FIELDS_LIST_EMPTY = "List fields is empty";
    private static final String SEPARATOR = ".";


    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> messages = new LinkedList<>();
    private List<NullField> fields = new LinkedList<>();

    public Validator notNullJSON(Object field, String name) {
        if (field == null)
            messages.add(String.format("JSON field %s must be not null", name));
        return this;
    }

    public Validator mustNullJSON(Object field, String name) {
        if (field != null)
            messages.add(String.format("JSON field %s must be null", name));
        return this;
    }

    public Validator notNullParam(Object param, String name) {
        if (param == null)
            messages.add(String.format("Request param %s must be not null", name));
        return this;
    }

    public Validator load(StringBuilder buffer) {
        for (String str : messages)
            buffer.append(str.concat("; "));
        return this;
    }

    public Validator addField(String fieldName, FieldType type) {
        fields.add(NullField.create(fieldName, type));
        return this;
    }

    public Validator addField(String fieldPath) {
        return addField(fieldPath, FieldType.NOT_NULL);
    }

    public Validator addNullField(String fieldPath) {
        return addField(fieldPath, FieldType.NULLABLE);
    }

    public Validator validStructure(Object object) {
        if (fields.isEmpty()) throw new IllegalArgumentException(FIELDS_LIST_EMPTY);

        fields.forEach(field -> {
            try {
                String[] inner = field.getFieldPath().split("\\" + SEPARATOR);
                StringBuilder msg = new StringBuilder();

                Field currentField;
                for (String currentName : inner) {
                    currentField = object.getClass().getDeclaredField(currentName);
                    currentField.setAccessible(true);
                    msg.append(SEPARATOR.concat(currentName));

                    switch (field.getType()) {
                        case NOT_NULL:
                            if (currentField.get(object) == null) {
                                notNullJSON(null, msg.toString());
                                break;
                            }
                            break;
                        case NULLABLE:
                            if (currentField.get(object) != null) {
                                mustNullJSON(true, msg.toString());
                                break;
                            }
                            break;
                    }


                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });

        return this;
    }

    public StringBuilder export() {
        StringBuilder buffer = new StringBuilder();
        load(buffer);
        return buffer;
    }

    public Validator reset() {
        return clearMessage().clearFields();
    }

    private Validator clearMessage() {
        messages.clear();
        return this;
    }

    private Validator clearFields() {
        fields.clear();
        return this;
    }

    @Getter
    static class NullField {
        private FieldType type;
        private String fieldPath;

        public static NullField create(String path, FieldType t) {
            NullField field = new NullField();
            field.type = t;
            field.fieldPath = path;
            return field;
        }
    }

    public enum FieldType {
        NOT_NULL,
        NULLABLE
    }
}
