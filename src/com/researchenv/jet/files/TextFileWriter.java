/*
Copyright (c) 2015, researchenv
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of JET nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.researchenv.jet.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <ol>
 * <li><a href="#usage1"><h4>Example 1 - Usage with number paramters (2 columns)</h4></a></li>
 * <li><a href="#usage2"><h4>Example 2 - Flushing data before closing</h4></a></li>
 * <li><a href="#usage3"><h4>Example 3 - Usage with number paramters (random columns)</h4></a></li>
 * <li><a href="#usage4"><h4>Example 4 - Writing formatted output </h4></a></li>
 * <li><a href="#usage5"><h4>Example 5 - Writing string output</h4></a></li>
 * </ol>
 * 
 * 
 * <h3 id="usage1"> Example 1</h3>
 * <pre>
 * import com.researchenv.exptools.files.TextFileWriter;
 * import java.io.File;
 * import java.io.IOException;
 * import java.util.logging.Level;
 * import java.util.logging.Logger;
 * 
 * public class Main {
 *    public static void main(String[] args) {
 *         try {
 *             //Creates a file /home/user/test.dat to write data to.
 *             TextFileWriter t = new TextFileWriter(new File("/home/user/test.dat"));
 * 
 *             //Writes 3 lines in two columns (x,y data)
 *             //Each call writes in a new line.
 *             t.writeNewLine(1, 1.0);
 *             t.writeNewLine(2, 4.0);
 *             t.writeNewLine(3, 9.0);
 *             //Flushes the buffer and closes the file.
 *             t.close();
 *            
 *         } catch (IOException ex) {
 *             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
 *         }
 *     }
 * }
 * 
 * <b>Output</b>: test.dat
 * 1 1.0
 * 2 4.0
 * 3 9.0
 * </pre>
 * 
 * <h3 id="usage2"> Example 2</h3>
 * <pre>
 * //Write 100 lines and 2 columns (x,y data).
 * for (int x = 0; x < 100; x++) {
 *     double y = (double)(x*x);
 *     t.writeNewLine(x,y);

 *     //Flushes the buffer and writes to the file after 10 iteraction.
 *     //This ensures the data will be written before calling TextFileWriter.close()
 *     <b>if(x%10==0) t.flush()</b>;
 * }
 * t.close()
 *
 * </pre>
 * 
 * <h3 id="usage3"> Example 3</h3>
 * <pre>
 * //Writes 4 lines and varying columns
 * t.writeNewLine(1, 2.0, 4, 7.553);
 * t.writeNewLine(2, 6.0);
 * t.writeNewLine(3, 9, 7.0, 1, 3, 5.97);
 * t.writeNewLine(1, 2, 3);
 * t.close();
 * 
 * <b>Output</b>: test.dat
 * 1 2.0 4 7.553
 * 2 6.0
 * 3 9 7.0 1 3 5.97
 * 1 2 3
 * </pre>
 * 
 * <h3 id="usage4"> Example 4</h3>
 * <pre>
 * //Writes 2 lines and 3 columns with c style formated output.
 * t.writeNewLine("%4.3f %03d %s", 4.55555f,   2, "string 1");
 * t.writeNewLine("%5.4f %d %s", 7.932345f, 42, "string 2");
 * t.close();
 * 
 * <b>Output</b>: test.dat
 * 4.556 002 string 1
 * 7.9323 42 string 2
 * </pre>
 *
 * 
 * <h3 id="usage5"> Example 5</h3>
 * <pre>
 * //Writes 2 lines.
 * t.writeNewLine("string line 1");
 * t.writeNewLine("string line 2");
 * t.close();
 *
 * <b>Output</b>: test.dat
 * string line 1
 * string line 2
 * </pre>
 * 
 * Writes number values or string to a file as text.
 * @author Erbe Pandini Rodrigues
 */
public class TextFileWriter implements AutoCloseable{

    private final BufferedWriter bw;
    
    /**
     * @param file a file to write to.
     * @throws IOException 
     */
    public TextFileWriter(File file) throws IOException {
        bw = new BufferedWriter(new FileWriter(file));
    }

    /**
     * Writes the arguments values, separated by one space, in a new line.
     * @param <T> Extends java Number class.
     * @param t A numeric value (int, double, float, Double, etc).
     * @throws IOException 
     */
    public <T extends Number> void writeNewLine(T... t) throws IOException{
        String line="";
        for (T t1 : t) {
            line += String.valueOf(t1) + " ";
        }
        writeNewLine(line.trim());
    }

    /**
     * Writes formated output to a new line.
     * @param format Text with format.
     * @param value List of values to write.
     * @throws IOException 
     */
    public void writeNewLine(String format, Object... value) throws IOException{
        String line=String.format(format, value);
        writeNewLine(line);
    }
    
    /**
     * Writes the string in a new line.
     * @param s A string.
     * @throws IOException 
     */
    public void writeNewLine(String s) throws IOException{
        bw.write(s);
        bw.newLine();
    }

    /**
     * Flushes the buffer. Calling it, forces the buffer to be written.
     * @throws IOException 
     */
    public void flush() throws IOException {
       bw.flush();
    }
    
    /**
     * Flushes the buffer and close file.
     * @throws IOException 
     */
    @Override
    public void close() throws IOException{
        bw.close();
    }
}
