package com.hoaxify.ws.shared;

import com.hoaxify.ws.file.FileService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FileTypeValidator implements ConstraintValidator<FileType, String> {

    private final FileService fileService;

    String[] types;

    @Override
    public void initialize(FileType constraintAnnotation) {
        this.types = constraintAnnotation.types();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isEmpty()) return true;

        String fileType = fileService.detectType(value);

        String supportedTypes = Arrays.stream(this.types).collect(Collectors.joining(" ,"));
        constraintValidatorContext.disableDefaultConstraintViolation();
        HibernateConstraintValidatorContext unwrapped = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
        unwrapped.addMessageParameter("types", supportedTypes);
        unwrapped.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate()).addConstraintViolation();

        return Arrays.stream(types).anyMatch(t -> fileType.contains(t));

    }
}
