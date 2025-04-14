package org.finite;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import java.io.File;

/**
 * JNA wrapper for the MicroASM runtime library.
 * Provides methods to interact with the MicroASM interpreter.
 */
public class MicroAsmRuntime {
    
    // Define the JNA interface for the native library
    public interface MicroAsmLib extends Library {
        // Use the same C function names as defined in the DLL
        Pointer masm_create_interpreter(int ramSize, int debugMode);
        void masm_destroy_interpreter(Pointer handle);
        int masm_load_bytecode(Pointer handle, String bytecodeFile);
        int masm_execute(Pointer handle, int argc, String[] argv);
        int masm_get_register(Pointer handle, int registerIndex, IntByReference outValue);
        int masm_read_ram_int(Pointer handle, int address, IntByReference outValue);
        int masm_write_ram_int(Pointer handle, int address, int value);
        Pointer masm_get_last_error();
    }
    
    // Instance of the native library
    private static MicroAsmLib LIB;
    
    // Load the native library using JNA
    static {
        try {
            // Set JNA to use only our specific native directory
            String nativeDir = System.getProperty("native.library.dir", 
                               "src/main/resources/native");
            File nativeDirFile = new File(nativeDir);
            
            System.out.println("Looking for native library in: " + nativeDirFile.getAbsolutePath());
            
            // Force JNA to only look in our specified directory
            System.setProperty("jna.library.path", nativeDirFile.getAbsolutePath());
            
            // Get platform-specific library name
            String osName = System.getProperty("os.name").toLowerCase();
            String libName;
            if (osName.contains("win")) {
                libName = "microasm";
            } else {
                libName = "microasm";  // JNA will add lib prefix automatically for Unix/Mac
            }
            
            // Verify the library file exists
            String fileName = System.mapLibraryName(libName);
            File libraryFile = new File(nativeDirFile, fileName);
            
            if (!libraryFile.exists()) {
                System.err.println("Native library file not found: " + libraryFile.getAbsolutePath());
                System.err.println("Please make sure the native library is placed in the correct directory.");
                throw new UnsatisfiedLinkError("Library file not found: " + fileName);
            }
            
            System.out.println("Loading native library: " + libraryFile.getAbsolutePath());
            
            // Load the library
            LIB = Native.load(libName, MicroAsmLib.class);
            System.out.println("Native library loaded successfully");
        } catch (Exception e) {
            System.err.println("Fatal error loading native library: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // --- Opaque Handle ---
    public static class MasmInterpreterHandle {
        private final Pointer handle;
        public static final MasmInterpreterHandle ZERO = new MasmInterpreterHandle(Pointer.NULL);
        
        public MasmInterpreterHandle(Pointer handle) {
            this.handle = handle;
        }
        
        public Pointer getPointer() {
            return handle;
        }
        
        public boolean isZero() {
            return handle == null || handle.equals(Pointer.NULL);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MasmInterpreterHandle other = (MasmInterpreterHandle) obj;
            if (handle == null) return other.handle == null;
            return handle.equals(other.handle);
        }
        
        @Override
        public int hashCode() {
            return handle != null ? handle.hashCode() : 0;
        }
    }
    
    // --- Error Codes Enum ---
    public enum MasmResult {
        OK(0),
        ERROR_GENERAL(-1),
        ERROR_INVALID_HANDLE(-2),
        ERROR_LOAD_FAILED(-3),
        ERROR_EXECUTION_FAILED(-4),
        ERROR_INVALID_ARGUMENT(-5),
        ERROR_MEMORY(-6);
        
        private final int code;
        
        MasmResult(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
        
        public static MasmResult fromCode(int code) {
            for (MasmResult result : values()) {
                if (result.code == code) {
                    return result;
                }
            }
            return ERROR_GENERAL;
        }
    }
    
    // --- Public API Methods ---
    
    /**
     * Creates a new MicroASM interpreter instance.
     */
    public static MasmInterpreterHandle createInterpreter(int ramSize, int debugMode) {
        Pointer handle = LIB.masm_create_interpreter(ramSize, debugMode);
        return new MasmInterpreterHandle(handle);
    }
    
    /**
     * Destroys a MicroASM interpreter instance.
     */
    public static void destroyInterpreter(MasmInterpreterHandle handle) {
        LIB.masm_destroy_interpreter(handle.getPointer());
    }
    
    /**
     * Loads bytecode from a file into the interpreter.
     */
    public static int loadBytecode(MasmInterpreterHandle handle, String bytecodeFile) {
        return LIB.masm_load_bytecode(handle.getPointer(), bytecodeFile);
    }
    
    /**
     * Executes the loaded bytecode.
     */
    public static int execute(MasmInterpreterHandle handle, String[] args) {
        return LIB.masm_execute(handle.getPointer(), args != null ? args.length : 0, args);
    }
    
    /**
     * Gets the value of a register.
     */
    public static int getRegister(MasmInterpreterHandle handle, int registerIndex) {
        IntByReference value = new IntByReference();
        int result = LIB.masm_get_register(handle.getPointer(), registerIndex, value);
        if (result != MasmResult.OK.getCode()) {
            throw new RuntimeException("Failed to get register value: " + getLastError());
        }
        return value.getValue();
    }
    
    /**
     * Reads an integer from RAM.
     */
    public static int readRamInt(MasmInterpreterHandle handle, int address) {
        IntByReference value = new IntByReference();
        int result = LIB.masm_read_ram_int(handle.getPointer(), address, value);
        if (result != MasmResult.OK.getCode()) {
            throw new RuntimeException("Failed to read RAM: " + getLastError());
        }
        return value.getValue();
    }
    
    /**
     * Writes an integer to RAM.
     */
    public static int writeRamInt(MasmInterpreterHandle handle, int address, int value) {
        return LIB.masm_write_ram_int(handle.getPointer(), address, value);
    }
    
    /**
     * Gets the last error message.
     */
    public static String getLastError() {
        Pointer errorPtr = LIB.masm_get_last_error();
        if (errorPtr == null || errorPtr.equals(Pointer.NULL)) {
            return null;
        }
        return errorPtr.getString(0);
    }
    
    /**
     * Helper method to convert raw result code to enum
     */
    public static MasmResult resultFromCode(int code) {
        return MasmResult.fromCode(code);
    }
}
