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

import java.io.File;

/**
 * Generates a set of output file names in a folder structure suitable for saving data.
 * 
 * <ol>
 * <li><a href="#usage1"><h4>Example 1 - output file with default index label</h4></a></li>
 * <li><a href="#usage2"><h4>Example 2 - output file with index filled with zeros on the left</h4></a></li>
 * <li><a href="#usage3"><h4>Example 3 - output file with index position changed</h4></a></li>
 * </ol>
 * 
 * <h3 id="usage1"> Example 1</h3>
 * <pre>
 *  import com.researchenv.exptools.files.OutputFiles;
 *  import java.io.File;
 * 
 *  public class Main {
 *      public static void main(String[] args) {
 *          
 *          File dir = new File("/home/user");
 *          <b>String file = "result_"</b>;
 *          String extension = ".dat";
 *       
 *          OutputFiles outputFiles = new OutputFiles(dir, file, extension);
 *   
 *          for (int i = 0; i {@literal <} 5; i++) {
 *              System.out.println(outputFiles.getNextOuput().getAbsolutePath());
 *          }
 *      }
 *  }
 *  
 * <b>Output:</b>
 * /home/user/result_0.dat
 * /home/user/result_1.dat
 * /home/user/result_2.dat
 * /home/user/result_3.dat
 * /home/user/result_4.dat
 * </pre>
 *
 * <h3 id="usage2"> Example 2 </h3>
 * 
 * <pre>
 * Changing line <b>String file = "result_"</b> by:
 *
 * <b>String file = "result_" + OutputFiles.Label.zeros(3)</b>;
 *
 * <b>Output:</b>
 * /home/user/result_000.dat
 * /home/user/result_001.dat
 * /home/user/result_002.dat
 * /home/user/result_003.dat
 * /home/user/result_004.dat
 * </pre>
 *
 * <h3 id="usage3"> Example 3 </h3>
 * 
 * <pre>
 * Changing line <b>String file = "result_"</b> by:
 *
 * <b>String file = "result_" + OutputFiles.Label.index() +"_xy"</b>;
 *
 *  Output:
 * /home/user/result_0_xy.dat
 * /home/user/result_1_xy.dat
 * /home/user/result_2_xy.dat
 * /home/user/result_3_xy.dat
 * /home/user/result_4_xy.dat
 * </pre>
 * 
 * 
   Automatically generates output files with index given a directory, filename and extension. 
 * @author Erbe Pandini Rodrigues
 */
public class OutputFiles {

    private final File rootDir;
    private final String filename;
    private final String extension;
    private int index;
    
    /**
     * @param dir A directory for files
     * @param filename A file name
     * @param extension A file extension
     */
    public OutputFiles(File dir, String filename, String extension) {
        rootDir = dir;
        this.filename=filename;
        int idx;
        this.extension = extension.startsWith(".")?extension:"."+extension;
        index=0;
    }
    
    /**
     * Generates files with incrementing index.
     * @return a file with index automatically incremented.
     */
    public File getNextOuput(){
        File output = new File(rootDir, setFileName(filename));
        index++;
        return output;
    }

    /**
     * Set the file names according to labels and indexes.
     * @param filename The file name without index or label.
     * @return The output filename with labels and index.
     */
    private String setFileName(String filename) {
        if(filename.contains(Label.index())){
            return filename.replace(Label.index(), String.valueOf(index))+extension;
        } else if(filename.contains(Label.index0())){
            return filename.replace(Label.index0(), String.format("%0"+Label.zeros+"d", index))+extension;
        }
        return filename+String.valueOf(index)+extension;
    }
    
    /**
     * Provides some labels option for output files
     */
    public static class Label{
        private static int zeros;
        
        /**
         * Index label
         * @return A tag for index label
         */
        public static String index(){
            return "${index}";
        }
        
        /**
         * Index label filled with zeros on the left
         * @param zeros The number of zeros in the label.
         * @return A tag for index label left filled with zeros.
         */
        public static String zeros(int zeros){
            Label.zeros=zeros;
            return "${index:zeros"+String.valueOf(zeros)+"}";
        }
        
        private static String index0(){
            return "${index:zeros"+String.valueOf(zeros)+"}";
        }
    
    }
}
