# SzyfrowanieDESX

A Java implementation of DES (Data Encryption Standard) and DESX (DES eXtended) cryptographic algorithms with a user-friendly JavaFX graphical interface.

## Overview

This project provides a complete implementation of the DES and DESX encryption algorithms, allowing users to encrypt and decrypt both text and files. DESX extends the standard DES algorithm by adding pre- and post-whitening keys to increase security against brute force and differential cryptanalysis attacks.

## Features

- **DES Encryption/Decryption**: Complete implementation of the Data Encryption Standard algorithm
- **DESX Encryption/Decryption**: Enhanced version with additional whitening keys (k1, k2)
- **Text Processing**: Encrypt/decrypt text input directly
- **File Processing**: Support for encrypting/decrypting files of various formats
- **Key Generation**: Automatic generation of cryptographic keys
- **Graphical Interface**: Intuitive JavaFX-based user interface
- **Flexible Input**: Support for both text input and file selection

## Architecture

The project follows a modular architecture with clear separation of concerns:

```
SzyfrowanieDESX/
├── Model/                  # Core cryptography logic
│   └── src/main/java/pl/kryptografia/model/
│       ├── DES.java       # DES algorithm implementation
│       ├── DESX.java      # DESX algorithm implementation
│       └── Values.java    # Cryptographic constants and tables
└── View/                  # JavaFX user interface
    └── src/main/java/pl/kryptografia/view/
        ├── MainApplication.java    # JavaFX application entry point
        └── MainController.java     # UI controller logic
```

## Algorithm Details

### DES (Data Encryption Standard)
- **Block size**: 64 bits (8 bytes)
- **Key size**: 64 bits (56 effective bits)
- **Structure**: Feistel network with 16 rounds
- **Input handling**: Automatic padding for inputs not divisible by 8 bytes

### DESX (DES eXtended)
- **Enhancement**: Uses three keys instead of one
- **Formula**: `C = k2 XOR DES(k, p XOR k1)`
- **Keys**:
  - `k`: Standard DES key (64 bits)
  - `k1`: Pre-whitening key (64 bits)
  - `k2`: Post-whitening key (64 bits)
- **Security**: Increased resistance to attacks compared to standard DES

## Requirements

- **Java**: JDK 11 or higher
- **JavaFX**: Included in the project dependencies
- **Maven**: For dependency management and building

## Building and Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Urteusz/SzyfrowanieDESX.git
   cd SzyfrowanieDESX
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn javafx:run
   ```

## Usage

### GUI Application
1. Launch the application using the instructions above
2. Choose between text input or file input using radio buttons
3. Enter or generate encryption keys (DES key, k1, k2 for DESX)
4. Input your plaintext or select a file to encrypt
5. Click encrypt to generate the ciphertext
6. Save the encrypted result or decrypt it back to plaintext

### Key Management
- **Generate Keys**: Use the "Generate Keys" button for automatic key creation
- **Manual Entry**: Enter keys manually in hexadecimal format
- **Key Format**: All keys should be 64-bit values in hexadecimal notation

### File Support
- **Input**: Any file type can be encrypted
- **Output**: Encrypted files maintain original extension or save as .txt for text mode
- **Binary Safety**: Proper handling of binary data during encryption/decryption

## Security Notes

⚠️ **Important**: This implementation is for educational purposes. While DES and DESX are correctly implemented, DES is considered cryptographically weak by modern standards due to its small key size. For production use, consider modern algorithms like AES.

## Project Structure Details

- **Model Module**: Contains the core cryptographic implementations
- **View Module**: Handles the user interface and user interactions
- **Modular Design**: Uses Java 9+ module system for clean separation

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests to improve the implementation or add new features.

## License

This project is available for educational and research purposes. Please check the repository for specific licensing terms.

## Authors

- **Urteusz** - Initial implementation and development

## Educational Value

This project demonstrates:
- Implementation of classical cryptographic algorithms
- JavaFX application development
- Modular Java architecture
- File I/O operations with encryption
- Hexadecimal data representation and manipulation