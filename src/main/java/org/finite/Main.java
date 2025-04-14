package org.finite;

import java.io.File;
import java.io.IOException;

public class Main {
    
    /**
     * Sets up native library loading paths.
     */
    public static void loadNativeLibrary() throws IOException {
        // Set the native library directory property
        String nativeDir = new File("src/main/resources/native").getAbsolutePath();
        System.setProperty("native.library.dir", nativeDir);
        
        // Get platform-specific library name
        String osName = System.getProperty("os.name").toLowerCase();
        String libName;
        if (osName.contains("win")) {
            libName = "microasm.dll";
        } else if (osName.contains("mac")) {
            libName = "libmicroasm.dylib";
        } else {
            libName = "libmicroasm.so";
        }
        
        System.out.println("OS: " + osName);
        System.out.println("Expected library name: " + libName);
        System.out.println("Library search location: " + nativeDir);
        
        // Check if library exists in the specified location
        File libFile = new File(nativeDir, libName);
        if (!libFile.exists()) {
            System.err.println("WARNING: Native library not found at: " + libFile.getAbsolutePath());
        } else {
            System.out.println("Native library found at: " + libFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        System.out.println("MicroASM Java Host Test");

        // --- Configuration ---
        String bytecodeFile = "example.bin";  // Ensure this file exists and is copied to output
        int ramSize = 65536;  // 64 KB
        boolean debugMode = true;
        String[] scriptArgs = { "arg1_from_java", "arg2" };  // Example args for the script
        // --- End Configuration ---

        // Check if bytecode file exists
        File file = new File(bytecodeFile);
        if (!file.exists()) {
            System.err.println("Error: Bytecode file not found: " + file.getAbsolutePath());
            System.err.println("Please ensure 'example.bin' is compiled and copied to the output directory.");
            return;
        }

        MicroAsmRuntime.MasmInterpreterHandle handle = MicroAsmRuntime.MasmInterpreterHandle.ZERO;
        try {
            // 1. Create Interpreter
            System.out.println("Creating interpreter...");
            handle = MicroAsmRuntime.createInterpreter(ramSize, debugMode ? 1 : 0);
            if (handle.isZero()) {
                System.err.println("Failed to create interpreter: " + 
                    (MicroAsmRuntime.getLastError() != null ? MicroAsmRuntime.getLastError() : "Unknown error"));
                System.err.println("Ensure the runtime library exports the required functions with the correct names.");
                return;
            }
            System.out.println("Interpreter created.");

            // 2. Load Bytecode
            System.out.println("Loading bytecode from '" + bytecodeFile + "'...");
            int loadResult = MicroAsmRuntime.loadBytecode(handle, bytecodeFile);
            MicroAsmRuntime.MasmResult result = MicroAsmRuntime.resultFromCode(loadResult);
            if (result != MicroAsmRuntime.MasmResult.OK) {
                System.err.println("Failed to load bytecode: [" + result + "] " + 
                    (MicroAsmRuntime.getLastError() != null ? MicroAsmRuntime.getLastError() : "Unknown error"));
                return;
            }
            System.out.println("Bytecode loaded.");

            // 3. Execute
            System.out.println("Executing bytecode...");
            System.out.println("--- Script Output Start ---");
            
            int execResultCode = MicroAsmRuntime.execute(handle, scriptArgs);
            MicroAsmRuntime.MasmResult execResult = MicroAsmRuntime.resultFromCode(execResultCode);
            
            System.out.println("--- Script Output End ---");

            if (execResult != MicroAsmRuntime.MasmResult.OK) {
                System.err.println("Execution failed: [" + execResult + "] " + 
                    (MicroAsmRuntime.getLastError() != null ? MicroAsmRuntime.getLastError() : "Interpreter internal error"));
                // Don't exit immediately, try to get register info if possible
            } else {
                System.out.println("Execution finished (HLT likely reached).");
            }

            // 4. Example: Inspect a register after execution (e.g., R0)
            try {
                int r0Value = MicroAsmRuntime.getRegister(handle, 0);
                System.out.println("Value of R0 after execution: " + r0Value);
            } catch (RuntimeException e) {
                System.out.println("Could not get R0 value: " + 
                    (MicroAsmRuntime.getLastError() != null ? MicroAsmRuntime.getLastError() : "Unknown error"));
            }

            // 5. Example: Write/Read RAM
            int testAddress = 100; // Choose an address not likely used by stack/data
            int testValue = 12345;
            System.out.println("Writing " + testValue + " to RAM address " + testAddress + "...");
            int writeResult = MicroAsmRuntime.writeRamInt(handle, testAddress, testValue);
            if (MicroAsmRuntime.resultFromCode(writeResult) == MicroAsmRuntime.MasmResult.OK) {
                System.out.println("Reading back from RAM address " + testAddress + "...");
                try {
                    int readValue = MicroAsmRuntime.readRamInt(handle, testAddress);
                    System.out.println("Read value: " + readValue + " (Expected: " + testValue + ")");
                    if (readValue != testValue) {
                        System.out.println("RAM Read/Write Mismatch!");
                    }
                } catch (RuntimeException e) {
                    System.out.println("Failed to read RAM: " + 
                        (MicroAsmRuntime.getLastError() != null ? MicroAsmRuntime.getLastError() : "Unknown error"));
                }
            } else {
                System.out.println("Failed to write RAM: " + 
                    (MicroAsmRuntime.getLastError() != null ? MicroAsmRuntime.getLastError() : "Unknown error"));
            }

        }  catch (Exception e) {
            System.err.println("An unexpected Java error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 6. Destroy Interpreter
            if (handle != null && !handle.isZero()) {
                System.out.println("Destroying interpreter...");
                MicroAsmRuntime.destroyInterpreter(handle);
                System.out.println("Interpreter destroyed.");
            }
        }

        System.out.println("Host program finished.");
    }
}