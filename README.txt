usage:

#1
add
<message-interpolator>org.os890.bv.addon.label.impl.AdvancedMessageInterpolator</message-interpolator>
to validation.xml

#2
define a custom (composite-)bv-constraint - e.g.:

@ReportAsSingleViolation

@Size
@Constraint(validatedBy = {})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface MySize {
    String message();

    String propertyName();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @OverridesAttribute(constraint = Size.class, name = "min")
    int min() default 0;

    @OverridesAttribute(constraint = Size.class, name = "max")
    int max() default Integer.MAX_VALUE;

    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    @interface List {
        MySize[] value();
    }
}

#3 define your message-texts - e.g.:
nameLength=The length of '{propertyName}' should be between {min} and {max}
firstName=Firstname
lastName=Surname

#4 use the constraint - e.g.:
public class Person {
    @MySize(min = 1, max = 100, message = "{nameLength}", propertyName = "{firstName}")
    private String firstName;

    @MySize(min = 2, max = 100, message = "{nameLength}", propertyName = "{lastName}")
    private String lastName;

    //...
}

#5 verify the result - e.g.:
example violation-message-text:
The length of 'Surname' should be between 2 and 100