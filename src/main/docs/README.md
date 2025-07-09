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

- Java ${java.version}
- Maven 3.x

## Dependencies

Main dependencies include:
- Jackson ${jackson.version}
- Gson ${gson.version}
- JUnit Jupiter ${junit.version}

## Getting Started

To use JsonShield in your project, add the BOM to your dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.stockman</groupId>
            <artifactId>jsonshield-bom</artifactId>
            <version>${project.version}</version>
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

## License

This project is licensed under the MIT License - see below for details:

```text
MIT License
Copyright (c) 2025 dev.stockman
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

```

## Contributing

Contributions are welcome! Here's how you can help:

### Submitting Changes

1. Fork the repository
2. Create a new branch for your feature or bugfix:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes and commit them:
   ```bash
   git commit -m "feat: add new feature"
   ```
   Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages
4. Push to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a Pull Request

### Development Guidelines

- Write tests for new features
- Maintain code coverage above 80%
- Follow existing code style and formatting
- Update documentation for significant changes
- Add JavaDoc for public APIs

### Setting Up Development Environment

1. Clone the repository
2. Install Java ${java.version}
3. Install Maven 3.x
4. Build the project:
   ```bash
   mvn clean install
   ```

### Reporting Issues

- Use the GitHub issue tracker
- Provide a minimal reproducible example
- Include version information
- Describe expected vs actual behavior
