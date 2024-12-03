                                                #  Avoiding `Optional` in Java/Kotlin #

## **Introduction**
Java pacakge `java.util.Optional` was introduced in Java 8 to address issues related to `null` values. While its intent was to reduce `NullPointerException` and make code safer, its design and implementation have several critical flaws that make it a less ideal solution for "modern software" development.
**Optional in Java 8+ was meant to solve issues related to null, but its implementation in java.util has significant shortcomings. Optional is considered suboptimal in many cases.**

This document explores the problems with Monad `Optional`, identifies where its use can cause issues, and suggests alternatives.
**(I won't explain what each concept is, as the focus is mainly on contracts and adhering to SOLID principles. Every Java developer or programmer working with typed, object-oriented programming should understand what this entails.)**

## **Why is Optional a failed idea?**

- It is not a true Monad:
- Compared to monads in Haskell or Option in Scala, Optional does not fully support a fluent, functional programming style
- mistake `java.util.Optional` Java standard API.

# Monad 
**A structure in functional programming that represents computations as sequences of steps. A Monad consists/wrapper of:
- A unit constructor (unit or of): Wraps a value into a monad.**
- **Example: function/method bind or flatMap (fmap): Allows combining operations on values wrapped in a monad.**
- A monad adheres to the laws of associativity and identity.
- In Java Optional is a simplified form of a monad, but it does not meet all the requirements (lacking support for the identity function)

### Lack of support in class fields:

- It should not be used as a class field (violates the principles of object encapsulation
- Not part of the system-wide API:
- It does not have native integration with Java's API outside the java.util package.
- Complexity in handling:


- **Methods like get(), orElse(), and isPresent() lead to unreadable code and...**
- `Optional#of,`
- `Optional#ofNullable,`
- `Optional#ifPresent`,
- `Optional#ifPresentOrElse`,
- `Optional#flatMap`,
- `Optional#filter`
- `Optional#orElseThrow`,
- `Optional#orElseGet`,
- `Optional#empty`,
- `Optional#isEmpty`,
- `Optional#stream`,
- `Optional#toString`,
- `Optional#or`
- `more...`
- `#AvoidOptional`

### **Debugging Difficulties**:
   - Errors like `NoSuchElementException` from improper use (`get()` without checking presence) are cryptic.
   - Stack traces for `Optional` exceptions are harder to trace compared to explicit null checks.


- Each `Optional object` adds an extra layer of allocation compared to directly storing the value.
In the case of large datasets or frequently created Optional instances, this can lead `to increased memory usage and higher Garbage Collector (GC) costs.`
It also changes the semantics of the code.
- Optional requires to change their approach to handling missing values, which can cause inconsistencies within a team.
Improper use fields in classes leads to **design anti-patterns.**
- Complex functional method chains on Optional can be less efficient than simple imperative code.
Multiple levels of "wrapping" values in Optional increase CPU costs.
- When using Optional as a parameter, the method caller must create an Optional instance before calling the method, which adds unnecessary overhead.
- Many limitations in usage (e.g., not as a class field, not as a method parameter).
- Limited support in the Java API, which leads to conversions between Optional and null.
- Forces unreadable and overly complex constructs.
- Generates performance issues and runtime errors.
- more...


### Optional in Java 8+ was meant to solve issues related to null (XD):
- implementation in java.util has significant flaws and mistakes that, in my opinion, should never have occurred.

* **Let’s get into the specifics and real reasons why we should avoid using this monad from java.util, along with descriptions of individual methods. I will describe each method (function), the issue it presents, and how the code should be structured instead.**
- Optional should not be used as a class field (it violates the principles of object encapsulation).
- It has no native integration with Java's API outside the java.util package.
`
`

`java.util.Optional#method`
#### 1. Optional#of(T value)
- **Creates an Optional with the given value. Throws a NullPointerException if the value is null.**

- **The Optional.of() method does not provide protection against null, which was one of the main issues that Optional was designed to solve. If null is passed as the argument, it will throw an exception, which is counterproductive to the goal of avoiding NPE (NullPointerException).**

- Instead of using `Optional.of()`, you should always use `Optional.ofNullable(T value)`, as it can handle null values gracefully without throwing an exception.


```java
Optional<String> opt = Optional.of(null); // throws NullPointerException

// Correct Usage with ofNullable:
Optional<String> opt = Optional.ofNullable(null); // returns Optional.empty()
// Using Optional.ofNullable() ensures that null values are safely handled and that no unnecessary exceptions are thrown.
```

#### 2. Optional#ofNullable(T value)
- **Creates an Optional with the given value, or Optional.empty() if the value is null.**

- **While `Optional.ofNullable()` is a better alternative to `Optional.of()`, it introduces the possibility of hiding potential issues caused by null. Developers might become overly reliant on this method, using it as a workaround instead of addressing the root cause of the null values. This approach can encourage the continued presence of null values in the system, rather than removing them altogether.**

- **Consider using the Null Object Pattern to completely eliminate null values. `Null Object Patter`n involves creating a default object that represents the absence of a value, instead of relying on null. This helps avoid unnecessary checks for null and provides a clearer, more predictable way to handle missing values.**



```java
Optional<String> opt = Optional.ofNullable(null); // returns Optional.empty()
```

#### 3. Optional#isPresent()
- **Returns true if the Optional contains a value, otherwise false.**

- **Using isPresent() leads to procedural code that resembles the old approach of checking null with if statements. It forces developers to manually check whether a value is present inside the Optional, which reduces the code's functional style. This defeats the purpose of Optional as a tool for avoiding null checks and introduces unnecessary complexity and verbosity.**

- **Use ifPresent() or other functional methods like map(), flatMap(), or filter(), which align better with a functional programming approach and make the code more concise and readable. These methods handle the presence check implicitly and allow for more declarative code.** 



```java
Optional<String> opt = Optional.of("Avoid");
if (opt.isPresent()) {
    System.out.println(opt.get());  // Procedural code, uses if and get()
}

// Correct Usage with ifPresent():
Optional<String> opt = Optional.of("Avoid");
opt.ifPresent(System.out::println);  // More functional approach, no explicit null checks

//Additional Functional Alternatives:
Optional<String> opt = Optional.of("Avoid");
opt.map(String::toUpperCase).ifPresent(System.out::println);  // Transformation and action in one step
// Using ifPresent() and other functional methods leads to cleaner, more declarative code that reduces manual checks and embraces the functional programming style.
```

#### 4. Optional#ifPresent(Consumer<? super T> action)
- **Performs an action if the Optional contains a value. If the Optional is empty, no action is performed.**

- **While ifPresent() is useful for executing an action when a value is present, it can make code harder to read and maintain when the logic inside the action becomes more complex. Additionally, it is limited because it only handles the case when the value is present and does not offer a way to handle the case where the Optional is empty.**

- **Use ifPresentOrElse() (introduced in Java 9), which allows you to handle both the case where the Optional contains a value and the case where it is empty. This provides more flexibility and makes the code more concise by not requiring additional if checks or extra logic.**


```java
Optional<String> opt = Optional.of("Avoid");
opt.ifPresent(v -> {
    // complex logic
    System.out.println(v);  // If logic becomes more complex, readability suffers
});

// Example with ifPresentOrElse():
Optional<String> opt = Optional.of("Avoid");
opt.ifPresentOrElse(
    v -> System.out.println(v),  // Action if present
    () -> System.out.println("No value present")  // Action if empty
);
// ifPresentOrElse() allows you to handle both the presence and absence of a value in a single, more readable and concise method, reducing the need for multiple if or conditional checks. This improves the clarity of the code, especially in more complex scenarios.
```

#### 5. Optional#ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) (Java 9+)
- **Performs one action if the Optional contains a value, or another action if the Optional is empty. This allows handling both cases (present and empty) in a single method.**

- **While ifPresentOrElse() improves the flexibility of handling both present and absent values, it can lead to unreadable code when the actions (the action and emptyAction) have different levels of complexity. If either of these actions involves complex logic, such as nested lambdas or multiple statements, the code can quickly become hard to read and maintain. Additionally, debugging such code can be challenging due to the increased complexity introduced by lambda expressions.**


```java
Optional<String> opt = Optional.of("Avoid");

opt.ifPresentOrElse(
    v -> {
        // Complex logic
        System.out.println("Action: " + v);
        // Further processing
    },
    () -> {
        // Complex logic
        System.out.println("Empty: No value found");
        // Further processing
    }
);

// In cases where the action or the emptyAction involves multiple statements or complex logic, the ifPresentOrElse() method can become cluttered with long lambdas, reducing clarity and making it harder to follow the flow of the program.

// Correct Usage:
// While ifPresentOrElse() can be useful for simple cases, for more complex logic, consider refactoring the code into separate methods or using map()/flatMap() in combination with orElse() or orElseGet() to handle the actions more cleanly.

Optional<String> opt = Optional.of("Avoid");

opt.ifPresentOrElse(
    this::handlePresent,  // Refactor complex logic into separate methods
    this::handleEmpty
);

// Refactored methods
public void handlePresent(String value) {
    System.out.println("Action: " + value);
    // ...
}

public void handleEmpty() {
    System.out.println("Empty: No value found");
    // ...
}
```
- By refactoring complex logic into dedicated methods, you maintain the readability of your code while still taking advantage of ifPresentOrElse() for handling both the present and empty cases. This way, the lambda expressions remain simple, and debugging becomes easier.


#### 6. Optional#map(Function<? super T, ? extends U> mapper)
- **Transforms the value inside the Optional using a provided function. If the Optional is empty, it returns Optional.empty(), otherwise it applies the function to the value and wraps the result in a new Optional.**
 
- **The use of map() often forces developers to apply additional transformations, especially when dealing with complex types or chaining multiple transformations. This can lead to more complex code that is harder to read and maintain.**

- **map() doesn't handle null values well. If the transformation function can potentially return null, you'll need to apply additional safeguards like Optional.ofNullable() to avoid NullPointerException, which can make the code more convoluted.**


```java
Optional<String> opt = Optional.of("Avoid");

Optional<Integer> length = opt.map(String::length);  // OK - if the function never returns null

// if the transformation function can return null:
// Could return null if length < 3
// In the example above, if toUpperCase() or substring() return null, the code can break. Handling this with // Optional.ofNullable() would add complexity.
Optional<String> transformed = opt.map(s -> s.toUpperCase().substring(0, 3));  
```

- **Use flatMap() instead of map().**

#### 7. Optional#flatMap(flatMap(Function<? super T, Optional<U>> mapper))
- **Transforms the value inside the Optional using a provided function, where the function itself returns an Optional<U>. If the Optional is empty, it returns Optional.empty(). This method helps avoid nested Optional<Optional<U>> structures, allowing for cleaner handling of transformations that may return Optional values.**

- **If the functions being passed to flatMap() are complex or involve multiple steps, they can lead to excessive nesting or chaining of logic. This can make the code harder to follow and maintain, as the transformation process becomes more opaque.**

- **When using flatMap() repeatedly in a chain, it can make the code more difficult to read and debug, especially if the transformations involve complex operations or conditional logic. Multiple calls to flatMap() create a series of nested transformations, which can result in a less intuitive flow, especially when mixed with other functional operations.**


```java
Optional<String> opt = Optional.of("Avoid");

Optional<String> result = opt.flatMap(s -> Optional.ofNullable(s.toUpperCase()))
                             .flatMap(s -> Optional.ofNullable(s.substring(0, 3)))
                             .flatMap(s -> Optional.ofNullable(s + "_STYPES"));
// In this example, the use of multiple flatMap() calls results in an increasingly complex and deeply nested transformation logic. 
// While each individual flatMap() may seem simple, chaining them together leads to less readable and harder-to-debug code, especially if the transformations become more intricate.

// Issues with Readability and Debugging: The more flatMap() is used in a chain, the harder it becomes to trace the transformations step by step. 
// Each transformation is executed sequentially, but when combined with conditionals, transformations can become difficult to understand at a glance.
```

#### 8. Optional#orElse(T other)
- **Returns the value contained in the Optional if present; otherwise, it returns the provided default value other. The default value is evaluated even when the Optional contains a value.**

- **The primary issue with orElse() is that the default value (other) is always evaluated, even if the Optional is not empty. This can result in unnecessary computation or side effects, especially when the default value is a complex expression or an expensive computation.**

- **If the default value involves a costly computation or side effect, using orElse() would waste resources in situations where the Optional is already populated, making it inefficient.**


```java
Optional<String> opt = Optional.of("Hello");

// The default value ("default_value") is evaluated even though the Optional is not empty
String value = opt.orElse(computeDefaultValue());

// Where computeDefaultValue() could be an expensive operation
String computeDefaultValue() {
    // Expensive computation
    return "default_value";
}
// In this example, computeDefaultValue() is always executed, even though the Optional contains a value, resulting in unnecessary computation.
```

- **Use orElseGet(Supplier<? extends T> other), which only evaluates the default value when the Optional is empty. This avoids the unnecessary computation of the default value when it is not required, leading to better performance.**


#### 9. Optional#orElseGet(Supplier<? extends T> supplier)
- **Returns the value contained in the Optional if present; otherwise, it invokes the Supplier to obtain the default value. The Supplier is only evaluated when the Optional is empty.**

- **While orElseGet() is more efficient than orElse(), it requires a Supplier function to be provided, which introduces slightly more complexity in syntax. This may seem cumbersome in cases where a simple default value can be used, especially for developers not familiar with functional programming constructs.**

- **The need to wrap the default value into a Supplier can make the code appear more verbose compared to the simpler orElse() method, particularly when no complex computation is required for the default value.**


```java
Optional<String> opt = Optional.empty();

// Using orElseGet requires wrapping the default value computation in a Supplier
String value = opt.orElseGet(() -> computeDefault());

// Where computeDefault() could be an expensive operation
String computeDefault() {
    // Expensive computation
    return "default_value";
}

// Here, using orElseGet() requires the lambda expression () -> computeDefault() to wrap the logic that computes the default value, which adds a small but noticeable layer of syntactic complexity.
```

- **orElseGet() is more efficient than orElse() because it only computes the default value when necessary, but it introduces more syntactic complexity.**
- **Use orElseGet() when the default value requires computation or a side effect, and use orElse() for simple constants or easy-to-define default values where performance is not an issue.**


#### 10. Optional#stream() (Java 9+)
- **stream() method returns a Stream containing the value inside the Optional if present, or an empty Stream if the Optional is empty. This method allows Optional to be seamlessly integrated into Java's Stream API, enabling you to chain further stream operations.**

```java
Optional<String> opt = Optional.of("Hello");

// Converts the Optional into a Stream containing the value
Stream<String> stream = opt.stream();

// The introduction of stream() on Optional (in Java 9) is seen as an attempt to integrate Optional more seamlessly into the Stream API. However, it may appear "artificial" or forced. 
// This method doesn't provide much additional value beyond what is already achievable using Optional's other methods like map(), flatMap(), or ifPresent().
```

#### 11. Optional#empty()
- **empty() method creates an empty Optional, which is essentially a container that holds no value. This is useful when you want to represent the absence of a value in an Optional.**

```java
Optional<String> emptyOpt = Optional.empty();

// One of the main criticisms of Optional.empty() is that it can be overused as a default value, which can lead to a pattern that is similar to using null. While Optional is meant to represent the presence or absence of a value, relying on Optional.empty() too frequently may defeat the purpose of providing a more explicit way of handling null. In some cases, Optional.empty() can make the code harder to understand because it introduces another abstraction layer without adding much clarity.
```

- **While Optional is meant to be a safer alternative to null, using Optional.empty() too often risks reintroducing the same confusion around the meaning of "no value". A value of Optional.empty() can be interpreted in different ways, depending on context, which can lead to misunderstandings or errors when different developers interpret the absence of a value.**

- **Sometimes, using empty collections or other idiomatic representations of "no value" (such as an empty list or map) can be more semantically clear than using Optional.empty(). For example, returning an empty List or Map directly from a method may better convey that no elements are present, while still allowing the consumer of the method to process it as a collection.**

- **While it converts Optional into a Stream, it doesn't offer the same full-fledged support as regular collections (like Lists or Sets). You can treat an Optional as a stream, but you can't directly perform collection-like operations (iltering multiple values or sorting).**


#### 12. Optional#isEmpty() (Java 11+)
-- **sEmpty() method returns true if the Optional is empty (i.e., it contains no value). It is the opposite of the isPresent() method, which checks if the Optional contains a value.**

```java
Optional<String> opt = Optional.empty();
boolean isEmpty = opt.isEmpty();  // Returns true
```

- **isEmpty() creates redundancy with the already existing isPresent() method, which serves as a way to check if the Optional contains a value. Both methods do essentially the same thing but with opposite semantics. This can lead to confusion and inconsistency in code, as developers now have to choose between isPresent() and isEmpty(), which do the same thing in a negated manner.**


#### 13. Optional#get() 
- **get() method in the Optional class returns the value contained within the Optional if it is present. If the Optional is empty (i.e., does not contain a value), it throws a NoSuchElementException.**

```java
Optional<String> optional = Optional.of("Hello");
String value = optional.get();  // Returns "Hello"

Optional<String> emptyOptional = Optional.empty();
String emptyValue = emptyOptional.get();  // Throws NoSuchElementException
```

- **One of the key principles of monads, including Optional, is to safely handle the absence of values without requiring explicit checks. The get() method violates this principle because it forces the programmer to manually handle the empty case by throwing an exception, rather than offering a more functional way of managing the absence of a value (e.g., using methods like map(), ifPresent(), or orElse()).**

- **A Monad should offer a way to deal with "no value" without throwing exceptions, which breaks the principle that developers should not manually check for the absence of values.**

- **Requires isPresent() Check Before Calling get():
Using get() in combination with isPresent() before calling it reintroduces an old, unsafe approach reminiscent of checking null. This is not in line with the modern, functional programming style.**

```java
Optional<String> opt = Optional.ofNullable(null);

// Bad practice: we have to check if there is a value before calling get()
if (opt.isPresent()) {
    String value = opt.get();  // Can throw NoSuchElementException if Optional is empty
}

// The get() method does not provide any alternative when the Optional is empty, which forces programmers to deal with exceptions (e.g., NoSuchElementException). This makes the method unsafe to use compared to methods that provide a default value, like orElse() or orElseGet().

// BAD PRACTICE DONT NOT USE get
String value = opt.get();  // NoSuchElementException
```

#### 14. Optional#filter(Predicate<? super T> predicate) 
**And many, many more issues and problems with the methods in the facade API of java.util.Optional that I have listed—those which cause mess and clutter in your code.**

####  Optional#isPresent() + get() – Anti-pattern
**- Using the combination of isPresent() and get() introduces redundancy and manual management of the presence of a value**

```java
if (opt.isPresent()) {
    String value = optionalValue.get();
    System.out.println(value);
}
```

#### And..
- **Using Optional as a Method Parameter**
- **Using Optional as a Class Field**
-  It should not be used as a class field (violates the principles of object encapsulation)
- **Calling get() without Checking Presence**
- **Creating Optional from null**
-  **Nested Optional (Anti-pattern)**

## Further Analysis of the Optional Class Methods and Monad
- **Usage of Optional largely depends on context. However, in this analysis, I aim to demonstrate why it is better to avoid using Optional altogether.**

**This repository explores the problems with `Optional`, identifies where its use can cause issues, and suggests alternatives.**

- **1. Use libraries like [vavr](https://github.com/vavr-io/vavr) for Option, which is a more feature-complete monadic type**
- **Javaslang**
- **Vavr (formerly Javaslang) is a Popular Functional Library in Java**
- **It implements Option in a way that is closer to true monads.**
- **Fully Functional API**
- **Exceptionally readable and efficient**
- **2. [guava](https://github.com/google/guava) Optional wrapper**
- **3. Null Object Pattern**
- **4. Create default, no-op implementations to handle missing values.**
- **5. Use @Nullable and @NonNull annotations to ensure null-safety during compilation [jetbrains](https://github.com/JetBrains/JetBrains.Annotations)**
- **5. Result/Either Constructs property**
- **Either Allows Handling Two Possible States (Value and Error)**

## Summary
**Optional from java.util is a limited implementation of the Option concept.**
**This is Not an Argument to Immediately Use Vavr
Rather, it highlights the conflicts and errors you may encounter when using java.util.Optional. The main focus is on performance and convenience.**
**This Illustrates the Practical Use of This Monad
Choose what you think is better; however, personally, I hate it...**
**This Represents the Minority That Reveals the True Nature of the Optional Concept in java.util.**

**Finally, Here's a Dose of Bad Practices with Optional That You Should Avoid If You Plan to Use It.**

**I Base This on My Skills, Knowledge, and Experience in English.
If I’ve made any linguistic mistakes — my apologies. Additionally, I’d like to note that I’m not a programmer by profession.**


**If you are interested in exploring functional programming and its applications within this project visit the repository at [vavr-in-action](https://github.com/noyzys/bukkit-vavr-in-action), [fp-practice](https://github.com/noyzys/fp-practice).**