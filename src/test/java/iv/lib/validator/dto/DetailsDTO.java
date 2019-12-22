package iv.lib.validator.dto;

import iv.lib.validator.Validatable;
import iv.lib.validator.Validator;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Data
public class DetailsDTO implements Validatable {
    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private Long age;
    @Nullable
    private List<Integer> magicNumbers;

    private DetailsDTO() {}

    public static DetailsDTO create(String f, String l, Long age, int ... numbers) {
        DetailsDTO dto = new DetailsDTO();

        dto.firstName = f;
        dto.lastName = l;
        dto.age = age;
        dto.magicNumbers = numbers != null
                        ?   Arrays
                                .stream(numbers)
                                .boxed()
                                .collect(Collectors.toList())
                        : null;
        return dto;
    }

    @Override
    public Validator createValidator() {
        return Validator.create()
                .addField("firstName")
                .addField("lastName")
                .addField("age")
                .addField("magicNumbers");
    }
}
