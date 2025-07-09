# JsonShield

JsonShield is a Java library that provides data masking capabilities for JSON documents, helping protect sensitive information when logging or transmitting JSON data. It supports both Jackson and Gson libraries, offering consistent masking behavior across different JSON implementations.

## Features

- Masks sensitive data in JSON strings and Java objects
- Supports both Jackson and Gson implementations
- Configurable masking strategies (whitelist/blacklist)
- Handles complex nested structures
- Preserves JSON structure while masking values
- Type-aware masking:
    - Strings → "*****"
    - Numbers → 0 or 0.0
    - Booleans → false
    - Null values remain null
    - Arrays and objects are recursively processed

## Example

```java
JsonShield jsonShield = JsonShieldJackson.createJsonShield(JsonShieldConfiguration.useBlackListStrategy().addFields("cardNumber", "cvv", "email", "phone", "accountBalance", "dob").build());
String maskedJson = maskUtils.mask(inputJson);
```

Input JSON:

```json
{
  "id": "ABC123",
  "name": "John Doe",
  "dob": "1970-02-26",
  "accountBalance": 57002.6,
  "contact": {
    "email": "john@example.com",
    "phone": "123-456-7890",
    "verified": true
  },
  "payment": {
    "cardNumber": "4111111111111111",
    "cvv": "123",
    "amount": 89.71
  }
}
```

Output (masked) JSON:

```json
{
  "id": "ABC123",
  "name": "John Doe",
  "dob": "*****",
  "accountBalance": 0.0,
  "contact": {
    "email": "*****",
    "phone": "*****",
    "verified": true
  },
  "payment": {
    "cardNumber": "*****",
    "cvv": "*****",
    "amount": 89.71
  }
}
```

## Requirements

- Java 24
- Maven 3.x

## Dependencies

Main dependencies include:
- Jackson 2.19.1
- Gson 2.13.1
- JUnit Jupiter 5.13.3

## Getting Started

To use JsonShield in your project, add the BOM to your dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.stockman</groupId>
            <artifactId>jsonshield-bom</artifactId>
            <version>1.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Then add either the Jackson or Gson implementation:

For Jackson:

```xml
<dependency>
    <groupId>dev.stockman</groupId>
    <artifactId>jsonshield-jackson</artifactId>
</dependency>
```

For Gson:

```xml
<dependency>
    <groupId>dev.stockman</groupId>
    <artifactId>jsonshield-gson</artifactId>
</dependency>
```

## Project Structure

The project consists of several modules:

- **jsonshield-core**: Core functionality and interfaces
- **jsonshield-jackson**: Jackson implementation
- **jsonshield-gson**: GSON implementation
- **jsonshield-test**: Shared test cases for implementations
- **jsonshield-bom**: Bill of Materials for dependency management
- **jsonshield-report**: Aggregated test coverage reports

## Building from Source

```bash
mvn clean install
```

Test coverage reports will be generated in the `jsonshield-report` module.
