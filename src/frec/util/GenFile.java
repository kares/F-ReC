
package frec.util;

import java.io.File;
import java.io.RandomAccessFile;
import frec.core.GenetixFunction;

    /**
     * Class <code> GenFile </code> is used for saving genetic
     * results computed by the kernel module. This class can save
     * a whole generation at once. String-UTF is used for reading and writing
     * to a (text) file.
     */

public final class GenFile 
{    
    private File file;
    private long pos = 0;
    private int elemCounter = 0; // lines
    
    /**
     * Constructor creates a new <code>GenFile</code>
     * using the default file name = "function.gen"
     */    
    
    public GenFile()
    {
        this(defaultFileName);
    }
    
    private static String defaultFileName = "function.gen";
    
    /**
     * Constructor creates a new <code>GenFile</code>
     * using the provided file name.
     *
     * @param fileName File name of the genetic file.
     */        
    
    public GenFile(String fileName)
    {
        file = new File(fileName);
    }
    
    /**
     * Sets the current position in this file
     * to the specified position.
     *
     * @param pos The new position in the file.
     */        
    
    public void seek(long pos) 
    {
        this.pos = pos;
    }

    /**
     * Reads a line from this file.
     *
     * @return Line read from the file.
     */            
    
    public String read() 
        throws java.io.IOException
    {
        String data = null;
        RandomAccessFile raf = null;
        try
        { 
            raf = new RandomAccessFile(file, "r");
            raf.seek(pos);
            data = raf.readUTF();
            pos = raf.getFilePointer();
            raf.close();
        }
        catch(java.io.IOException e) { raf.close(); }
        return data;
    }

    /**
     * Read lines from this file.
     *
     * @param len The number of lines to be read.
     * @return Lines read from the file.
     */                
    
    public String[] read(int len) 
        throws java.io.IOException    
    {
        String[] data = null;
        RandomAccessFile raf = null;
        try
        {
            raf = new RandomAccessFile(file, "r");
            raf.seek(pos);
            data = new String[len];
            for (int i=0; i<len; i++)
                data[i] = raf.readUTF();
            pos = raf.getFilePointer();
            raf.close();
        }
        catch(java.io.IOException e) { raf.close(); }
        return data;
    }    
    
    /**
     * Writes a line to this file.
     *
     * @param data Line (<code>String</code>) to be writen to this file.
     */                
    
    public void write(String data) 
        throws java.io.IOException    
    {
        RandomAccessFile raf = null;
        try
        { 
            raf = new RandomAccessFile(file, "rw");
            raf.seek(pos);
            raf.writeUTF(data + "\n");
            pos = raf.getFilePointer();
            raf.close();
            elemCounter++;
        }
        catch(java.io.IOException e) { raf.close(); }
    }    

    /**
     * Writes a function (<code>GPFunction</code> object) to this file. 
     * The function is converted using the <code>toString()</code> method.
     *
     * @param data Function to be writen to this file.
     */                    
    
    public void write(GenetixFunction data)  
        throws java.io.IOException
    {
        write(data.toString());
    }    

    /**
     * Writes functions (<code>GPFunction</code> objects) to this file.
     * Each function is converted using the <code>toString()</code> method.
     *
     * @param data Array of functions to be writen to this file (each starting a new line).
     */                        
    
    public void write(GenetixFunction[] data)
        throws java.io.IOException
    {
        RandomAccessFile raf = null;
        try
        { 
            raf = new RandomAccessFile(file, "rw");
            raf.seek(pos);
            for (int i=0; i<data.length; i++)
                raf.writeUTF(data[i].toString() + "\n");
            pos = raf.getFilePointer();
            elemCounter++;
            raf.close();
        }
        catch(java.io.IOException e) { raf.close(); }
    }

}