# J-MASM2

Welcome to the official J-Masm 2 repository!

## What is J-MASM 2?

J-Masm 2 is the second version of J-MASM, a Java-based assembler/interpreter for the Micro-Assembly programming language.

Built using our [LibMicroAssembly](https://github.com/fy-nite/C-Masm) library, J-MASM 2 is a complete rewrite of the original J-MASM project, which was based on the original concept design for the Micro-Assembly language.
J-MASM 2 is designed to be a more efficient and user-friendly assembler/interpreter, with a focus on performance and ease of use.

## Features

- **Java-based**: J-MASM 2 is written in Java, making it cross-platform and easy to use on any operating system.
- **Java-based Native Interface**: J-MASM 2 supports the Micro-Assembly native interface, allowing you to call native functions from your Micro-Assembly code that was written in Java.
- **Easy to use**: J-MASM 2 is designed to be easy to use, with a simple and intuitive interface that makes it easy to write and run Micro-Assembly programs.
- **Fast and efficient**: J-MASM 2 is designed to be fast and efficient, with a focus on performance and speed.
- **Open source**: J-MASM 2 is open source, allowing you to modify and customize the code to suit your needs.
- **Cross-platform**: J-MASM 2 is designed to be cross-platform, allowing you to run it on any operating system that supports Java.
- **Easy to install**: J-MASM 2 is easy to install, with a simple installation process that allows you to get up and running quickly.
- **Drag and drop replacement**: J-MASM 2 is a straight drag and drop replacement for the original J-MASM project, allowing you to get the most out of your computer hardware and Micro-Assembly code.

## Getting Started

To get started with J-MASM 2, follow these steps:

1. Clone the C-MASM repository:
   ```bash
   git clone https://github.com/fy-nite/C-Masm.git
   cd C-Masm
   ```

2. Build the C-MASM library using the configuration script:
   ```bash
   ./configure.ps1  # On Windows using PowerShell
   # or equivalent script on your platform
   ```
   
   After building, copy the library file to the J-MASM 2 resources directory:
   ```bash
   cp libMicroAssembly.* /path/to/jmasmtwo/src/main/resources/native/
   ```
   (Use the appropriate extension: .dll for Windows, .so for Linux, or .dylib for macOS)

3. Build the J-MASM 2 project:
   ```bash
   cd /path/to/jmasmtwo
   mvn clean package
   ```

4. Run the J-MASM 2 application:
   ```bash
   java -jar target/J-MASM2-<version number>.jar
   ```
   Replace `<version number>` with the actual version number of your build.

## Contributing

We welcome contributions to J-MASM 2! If you would like to contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

We appreciate any feedback or suggestions you may have.

## License

J-MASM 2 is licensed under the GNU Affero General Public License v3.0. See the [LICENSE](LICENSE) file for more information.