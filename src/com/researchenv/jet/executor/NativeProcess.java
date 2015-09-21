/*
Copyright (c) 2015, Erbe Pandini Rodrigues
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of JExpTools nor the names of its
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

package com.researchenv.jet.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

//@author Erbe Pandini Rodrigues

/**
 * Class to run native process.
 * 
 * <ol>
 * <li><a href="#limitations"><h4>Limitations</h4></a></li>
 * <li><a href="#simpleusage"><h4>Example 1 - Simple usage</h4></a></li>
 * <li><a href="#threadusage"><h4>Example 2 - Using with threads</h4></a></li>
 * <li><a href="#executorusage"><h4>Example 3 - Using with Executor</h4></a></li>
 * </ol>
 * <br>
  <h3 id="simpleusage">Limitations</h3>
 * Native processes calls java.lang.Process internally. See java.lang.Process documentation
 * to check its behavior. Once a process is started, it keeps running until it finishes. 
 * If a program which calls a native process is aborted, the process will continue running.
 * It is not automatically aborted. In this case, this process should be terminated manually.
 * 
 * <h3 id="simpleusage">Example 1 - Simple usage</h3>
 * <pre>
 *
 * import com.researchenv.exptools.executor.NPLInterface;
 * import com.researchenv.exptools.executor.NativeProcess;
 * import java.util.logging.Level;
 * import java.util.logging.Logger;
 * 
 * public class Main {
 *      public static void main(String[] args) {
 *           try {
 *
 *              //Create a native process with comand and arguments 
 *              NativeProcess p = new NativeProcess("/bin/ls", "-la");
 *
 *              //Add a listener which notifies when process starts and finishes execution
 *              p.addProcessListener(new NPLInterface() {
 *                  {@literal @}Override
 *                  public void statusChanged(NPLInterface.ProcessStatus status) {
 *                      //status can be STARTED or FINISHED
 *                      System.out.println(status);
 *                  }
 *              });
 *
 *              //Run the process (this is a bloking execution)
 *              p.run();
 *
 *          } catch (Exception ex) {
 *              Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
 *          }
 *      }
 *  }
 * </pre>
 * 
 * <h3 id="threadusage">Example 2 - Using with threads</h3>
 * <pre>
 * 
 * import com.researchenv.exptools.executor.NPLInterface;
 * import com.researchenv.exptools.executor.NativeProcess;
 * import java.util.logging.Level;
 * import java.util.logging.Logger;
 * 
 * public class Main {
 *      public static void main(String[] args) {
 *          try {
 *
 *          NativeProcess p = new NativeProcess("/bin/ls", "-la");
 *
 *          p.addProcessListener(new NPLInterface() {
 *               {@literal @}@Override
 *              public void statusChanged(NPLInterface.ProcessStatus status) {
 *                  System.out.println(status);
 *              }
 *          });
 *
 *          //Create a thread and pass p as argument 
 *          Thread thread = new Thread(p);
 *
 *          //Start the thread (this is non blocking call)
 *          thread.start();
 *
 *          } catch (Exception ex) {
 *              Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
 *         }
 *      }
 *  }
 * </pre>
 * 
 * <h3 id="executorusage">Example 3 - Using with {@link Executor} class </h3>
 * <p>Suppose there is a native program (c/c++/whatever) named filter in /home/user directory, which receives 
 * two inputs: a jpeg image path to be loaded for filtering (/home/user/img/imageX.jpeg) and an output file name
 * (/home/user/result/resultX.dat) to create a file and store the filter result. Many copies of this program can be executed
 * in parallel using Executor and NativeProcess as shown below.</p> 
 * <pre>
 *
 * import com.researchenv.exptools.executor.Executor;
 * import com.researchenv.exptools.executor.NPLInterface;
 * import com.researchenv.exptools.executor.NativeProcess;
 * import java.util.logging.Level;
 * import java.util.logging.Logger;
 * 
 * public class Main { 
 *      Executor{@literal <NativeProcess>} executor = new Executor{@literal <>}(2);
 *    
 *      executor.setFinishedListener(new FinishedListener{@literal <NativeProcess>()} {
 *          {@literal @}Override
 *          public void finished(NativeProcess source) {
 *              System.out.println("Program "+source.getCommand()+" id="+source.getId()+" finishedOk="+source.finishedOk());
 *          }
 *    
 *      });
 * 
 *      try {
 *          
 *          NativeProcess n1 = new NativeProcess("/home/user/filter", "/home/user/img/image1.jpeg","/home/user/result/result1.dat");
 *          NativeProcess n2 = new NativeProcess("/home/user/filter", "/home/user/img/image2.jpeg","/home/user/result/result2.dat");
 *          NativeProcess n3 = new NativeProcess("/home/user/filter", "/home/user/img/image3.jpeg","/home/user/result/result3.dat");
 *       
 *          executor.add(n1);
 *          executor.add(n2);
 *          executor.add(n3);
 *        
 *          executor.start();
 * 
 *          executor.waitForFinished();
 * 
 *      } catch (Exception exception) {
 *          Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, exception);
 *      } 
 *  } 
 * </pre>
 * @author Erbe Pandini Rodrigues
 */

public class NativeProcess implements Runnable{
    private final ProcessBuilder process;
    private ArrayList<NPLInterface> listenerList;
    private Process p;
    private final int id;
    private static int idCounter=0;
    /**
     * Creates a class to run a native program.
     * 
     * @param program The program to run (complete path)
     * @param args Program arguments list
     * @throws java.lang.Exception If the program path is invalid or does not exist throws an exception.
     */
    public NativeProcess(String program, String... args) throws Exception{
        File f = new File(program);
        if(!f.exists()) throw new Exception("Executable file "+program+" not found");
        if(!f.canExecute()) throw new Exception("Can't execute "+program);
        
        listenerList = new ArrayList<>();
        
        ArrayList<String> list =  new ArrayList<>();
        list.add(program);
        list.addAll(Arrays.asList(args));
        process = new ProcessBuilder(list);
        id=idCounter++;
    }
    
    /**
     * Adds listener to the process to notify when the process starts or finishes
     * @param nPLInterface The NativeProcess listener interface.
     */
    public void addProcessListener(NPLInterface nPLInterface){
        listenerList.add(nPLInterface);
    } 

    /**
     * Removes listeners.
     * @param nPLInterface The NativeProcess listener interface.
     */
    public void removeProcessListener(NPLInterface nPLInterface){
        listenerList.remove(nPLInterface);
    } 
    
    
    /**
     * Notify the listeners about changes process in status.
     * @param status 
     */
    private void notifyListeners(NPLInterface.ProcessStatus status){
        for (NPLInterface listener : listenerList) {
            listener.statusChanged(status);
        }
    }
    
    /**
     * Run the process.
     */
    @Override
    public void run() {
        try {
            notifyListeners(NPLInterface.ProcessStatus.STARTED);
            p = process.start();
            p.waitFor();
            notifyListeners(NPLInterface.ProcessStatus.FINISHED);

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(NativeProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Returns if the process has finished and exited returning value 0.
     * In this case an error (value other than 0) means segmentation fault or another runtime error.
     * The true returned value does not mean the program has no logical programming error.
     *  
     * @return True if the process exited normally. If exited with error code or the process
     * has not been already started, returns false.
     */
    public boolean finishedOk(){
        if(p==null) throw new IllegalThreadStateException("The process has not started yet");
        return p.exitValue()==0;
    }
    
    /**
     * Get process exit code.
     * @return Process exit code. 0 means process exited normally.
     * @throws IllegalThreadStateException 
     */
    public int getExitCode() throws IllegalThreadStateException{
        if(p==null) throw new IllegalThreadStateException("The process has not started yet");
        return p.exitValue();
    }
    
    /**
     * Returns the java Process class object related to the running process.
     * this can be used to get system PID and standard output, for example.
     * If the process has not been started yet, returns null.
     * 
     * @return java process object.
     */
    public Process getProcess()
    {
        return p;   
    }
    
    /**
     * An id for the process. It's not the system PID.
     * @return process id
     */
    public int getId(){
        return id;
    }
    
    /**
     * Gets a string with the program complete path.
     * @return The program absolute path
     */
    public String getProgram(){
        String command = process.command().get(0);
        return command;
    }
    
    /**
     * Gets a list whit arguments.
     * @return Arguments list.
     */
    public ArrayList<String> getArgs(){
        ArrayList<String> list  = new ArrayList<>(process.command());
        if(!list.isEmpty()){
            list.remove(0);
        }
        return list;
    }
    
    /**
     * Gets the complete command with program and arguments.
     * @return Program and arguments concatenated string.
     */
    public String getCommand(){
        String out="";
        for(String s:process.command()){
            out+=s+" ";
        }
        return out.substring(0, out.length()-1);
    }
}