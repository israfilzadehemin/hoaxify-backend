package com.hoaxify.ws.user;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(    validatedBy = {UniqueUsernameValidator.class}
)
public @interface UniqueUsername {
    String message() default "{hoaxify.constraint.username.UniqueUsername}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
