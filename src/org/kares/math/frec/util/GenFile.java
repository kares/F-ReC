/*
 * Copyright 2004 Karol Bucek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kares.math.frec.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.kares.math.frec.core.FunctionTree;

    /**
     * Class <code> GenFile </code> is used for saving genetic
     * results computed by the kernel module. This class can save
     * a whole generation at once. String-UTF is used for reading and writing
     * to a (text) file.
     */

public class GenFile {
    
    private long position = 0;
    private int elemCounter = 0; // lines

    private final RandomAccessFile file;

    public static final String defaultFileName = "function.gen";

    public GenFile(String fileName) {
        this(fileName, "rw");
    }

    /**
     * Constructor creates a new <code>GenFile</code>
     * using the provided file name.
     *
     * @param fileName File name of the genetic file.
     */
    public GenFile(String fileName, String mode) {
        try {
            file = new RandomAccessFile(fileName, mode);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static GenFile getReadOnlyInstance(final String fileName) {
        return new GenFile(fileName, "r");
    }

    public static GenFile getReadWriteInstance(final String fileName) {
        return new GenFile(fileName, "rw");
    }

    public static GenFile getSyncWriteInstance(final String fileName) {
        return new GenFile(fileName, "rws");
    }

    public FunctionTree read() {
        FunctionTree data;
        try {
            String dataFormatted = file.readUTF();
            return FunctionTree.parse(dataFormatted);
        }
        catch(IOException e) { 
            throw new RuntimeException(e);
        }
    }

    public FunctionTree[] read(int size) {
        FunctionTree[] dataArray = new FunctionTree[size];
        for (int i=0; i<size; i++) {
            dataArray[i] = read();
        }
        return dataArray;
    }
    
    private void doWrite(final String data) {
        try {
            file.writeUTF(data);
            elemCounter++;
        }
        catch (IOException e) { 
            throw new RuntimeException(e);
        }
    }    

    public void write(FunctionTree data) {
        doWrite( data.format() );
    }
    
    public void write(FunctionTree[] data) {
        for ( int i=0; i<data.length; i++ ) {
            write(data[i]);
        }
    }

    public void close() {
        try {
            file.close();
        }
        catch (IOException e) {
            // ignore
        }
    }

}