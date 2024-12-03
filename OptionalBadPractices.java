import java.util.Optional;

/**
 * This class demonstrates several bad practices when using Optional in Java.
 * These practices can introduce unnecessary complexity and reduce code clarity.
 *
 * 1. **Using Optional as a Class Field:**
 *    Using Optional as a class field (e.g., `private Optional<String> name;`) violates the principles of encapsulation.
 *    `Optional` is not a type that should be directly stored within a class. Encapsulation should hide the implementation details
 *    of class members, and using `Optional` as a field unnecessarily exposes the potential for missing values in a way
 *    that complicates the class's internal state management.
 *
 * 2. **Using Optional as a Method Argument:**
 *    Using Optional as a method argument (e.g., `public void printName(Optional<String> name)`) is considered bad practice.
 *    This introduces unnecessary overhead, as the method signature now forces the caller to deal with the possibility of null values.
 *    Instead, simple nullable types should be used directly as arguments.
 *
 * 3. **Calling `get()` Without Checking Presence:**
 *    Calling `get()` without first checking if the Optional contains a value (e.g., `value.get()`) will lead to a `NoSuchElementException`
 *    when the Optional is empty. This should be avoided by using `isPresent()` or other safe methods to handle the absence of a value.
 *
 * 4. **Creating Optional from null:**
 *    Creating an `Optional` with a `null` value (e.g., `Optional.of(null)`) results in a `NullPointerException`. 
 *    The correct approach is to use `Optional.ofNullable()` to safely handle null values without throwing exceptions.
 *
 * 5. **Nesting Optionals:**
 *    Nesting Optionals (e.g., `Optional<Optional<String>>`) introduces unnecessary complexity and makes the code harder to understand.
 *    This should be avoided, and a simpler design should be used to represent the absence or presence of a value.
 *
 * 6. **Using `orElse()` to Return a Default Value:**
 *    Always returning a default value using `orElse()` (e.g., `optional.orElse(defaultValue)`) even when it is unnecessary adds overhead 
 *    and goes against the intent of `Optional`, which should be used for managing the absence of a value, not to supply defaults 
 *    in every case.
 *
 * 7. **Redundancy with `isPresent()` and `get()`:**
 *    Combining `isPresent()` and `get()` creates redundancy (e.g., `if (optional.isPresent()) { optional.get(); }`).
 *    This pattern is less efficient and harder to read compared to using more functional approaches like `ifPresent()` or 
 *    `map()` for better readability and less boilerplate code.
 */
final class OptionalBadPractices {

    // Using Optional as a Class Field
    private Optional<String> name;  // Optional as field acess - violates the principles of encapsulation (SOLID)

    // Using Optional as a Method Parameter
     printName(final Optional<String> name) {  // Optional as method parameter 
        if (name.isPresent()) {
            System.out.println(name.get());
        } else {
            System.out.println("Name is absent");
        }
    }

    // Calling get() without Checking Presence
    String getValue() {
        final Optional<String> value = Optional.of("Avoid");
        return value.get();  
    }

    // Creating Optional from null
    Optional<String> createOptionalWithNull() {
        return Optional.of(null);  // creating Optional from null - NPE
    }

    // Nested Optional
    void printNestedOptional() {
        final Optional<Optional<String>> nestedOptional = Optional.of(Optional.of("Nested ref"));
        if (nestedOptional.isPresent() && nestedOptional.get().isPresent()) {
            System.out.println(nestedOptional.get().get());  // nested Optional
        }
    }

    // Bad practices from use orElse
    String getNameOrDefault() {
        Optional<String> name = Optional.ofNullable(null);
        return name.orElse("Name"); 
    }

    // Calling isPresent() + get() without Checking Presence 
    void checkAndGet() {
        final Optional<String> value = Optional.of("Avoid");
        if (value.isPresent()) {
            System.out.println(value.get());  // use ifPresent() or map()
        }
    }

    // psvm app
    public static void main(String[] args) {
        final OptionalBadPractices badPractices = new OptionalBadPractices();

        // Example bad practices...
        badPractices.printName(Optional.of("Avoid"));
        System.out.println(badPractices.getValue());
        System.out.println(badPractices.createOptionalWithNull());
        badPractices.printNestedOptional();
        System.out.println(badPractices.getNameOrDefault());
        badPractices.checkAndGet();
    }
}
