package iv.lib.validator.dto;

import iv.lib.validator.Validatable;
import iv.lib.validator.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Validatable {
    @Nullable
    private String login;
    @Nullable
    private String password;
    @Nullable
    private DetailsDTO details;

    @Override
    public Validator createValidator() {
        Validator testValidator = details != null
            ? details.createValidator()
            : null;
        return Validator
                    .create()
                    .addField("login")
                    .addField("password")
                    .addField("details")
                    .addChild(testValidator, "details");
    }
}
