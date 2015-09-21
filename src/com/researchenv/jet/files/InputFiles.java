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
import java.io.FileFilter;

/**
 * Gives quick access to input files.
 * 
 * <ol>
 * <li><a href="#usage1"><h4>Exeample 1 - Simple usage</h4></a></li>
 * <li><a href="#usage1"><h4>Exeample 2 - Filtering files</h4></a></li>
 * </ol>
 * <h3 id="usage1"> Example 1</h3>
 * <pre>
 *  import com.researchenv.exptools.files.InputFiles;
 *  import java.io.File;
 *  import java.util.logging.Level;
 *  import java.util.logging.Logger;
 * 
 *  public class Main {
 *      public static void main(String[] args) {
 *          
 *      try {
 *          
 *          //List only subdirectories
 *          //File[] files  = InputFiles.getFiles("/home/user",InputFiles.FileType.DIRECTORIES_ONLY);         
 * 
 *          //List files and subdirectories
 *          //File[] files  = InputFiles.getFiles("/home/user",InputFiles.FileType.FILES_AND_DIRECTORIES);         
 *          
 *          //List just files
 *          <b>File[] files  = InputFiles.getFiles("/home/user",InputFiles.FileType.FILES_ONLY);</b>
 *          
 *          for(File file : files) {
 *              //writes listed files to standard output 
 *              System.out.println(file.getAbsolutePath());
 *          }
 *          
 *      } catch (Exception ex) {
 *          Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
 *      }
 *  }
 *  
 * <b>Output:</b> (the /home/user directory files excluding subdirectories)
 * /home/user/result_0.dat
 * /home/user/result_1.dat
 * /home/user/data1.zip
 * /home/user/picture.jpg
 * </pre>
 * 
 * 
 * <h3 id="usage2"> Example 2</h3>
 * <pre>
 *  import com.researchenv.exptools.files.InputFiles;
 *  import java.io.File;
 *  import java.util.logging.Level;
 *  import java.util.logging.Logger;
 * 
 *  public class Main {
 *      public static void main(String[] args) {
 *          
 *      try {
 *          
 *          //List just <b>jpg</b> files
 *          File[] files  = InputFiles.getFiles("/home/user",<b>"*.jpg"</b>,InputFiles.FileType.FILES_ONLY);
 *          
 *          for(File file : files) {
 *              //writes listed files to standard output              
 *              System.out.println(file.getAbsolutePath());
 *          }
 *          
 *      } catch (Exception ex) {
 *          Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
 *      }
 *  }
 *  
 * <b>Output:</b> (the /home/user directory <b>jpg</b> files only)
 * /home/user/picture.jpg
 * </pre>
 * 
 * @author Erbe Pandini Rodrigues
 */
public class InputFiles {

    public enum FileType{
        FILES_ONLY,
        DIRECTORIES_ONLY,
        FILES_AND_DIRECTORIES
    }

    /**
     * List files contained in a directory. 
     * @param dir A directory to list the files
     * @param filetype Files only, directories only or files and directories
     * @return List of directory files
     * @throws Exception 
     */
    public static File[] getFiles(String dir, FileType filetype) throws Exception {
        return getFiles(dir, null, filetype);
    }

    /**
     * List files contained in a directory, with applied filter. 
     * @param dir A directory to list the files
     * @param filter String with filter, ex: *.*, *.dat, etc
     * @param filetype Files only, directories only or files and directories
     * @return List of directory files
     * @throws Exception 
     */
    public static File[] getFiles(String dir, String filter, final FileType filetype) throws Exception {
        File file = new File(dir);
        testFile(file);
        final String regExp = filter==null? null: filter.replaceAll("\\*", ".*").replaceAll("\\?", ".");
        
        if (file.exists() && file.isDirectory()) {
            
            File[] list = file.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    switch (filetype) {
                        case FILES_ONLY:
                            if(pathname.isDirectory()) return false;
                            break;
                        case DIRECTORIES_ONLY:
                            if(!pathname.isDirectory()) return false;
                            break;
                    }

                    return regExp == null?true:pathname.getName().matches(regExp);
                }
            });

            return list;
        }
        return null;
    }
    
    private static void testFile(File file) throws Exception{
        if(!file.exists()) throw new Exception("Directory "+file.getAbsolutePath()+" not found");
        if(!file.isDirectory()) throw new Exception(file.getAbsolutePath()+" is not a directory");
    }
}