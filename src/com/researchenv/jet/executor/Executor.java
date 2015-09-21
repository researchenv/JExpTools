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

package com.researchenv.jet.executor;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executor runs runnable classes in parallel, with a maximum defined number of threads at time. 
 * 
 * <h2>{@link NativeProcess Using Executor class to run native programs with NativeProcess}</h2>
 *
 * <h2>Simple usage</h2>
 * 
 * <h3>1. Create a class which implements Runnable to execute your calculation</h3>
 *
 * <pre>
 * public class Sum implements <b>Runnable</b>{
 *     private final int x;
 *     private final int y;
 *     private int res;
 *     
 *     public Sum(int x, int y){
 *         this.x=x;
 *         this.y=y;
 *     }
 *     
 *     {@literal @}Override
 *     public void <b>run()</b> {
 *         //Do the work here.
 *         res=x+y;
 *     }
 *         
 *     int <b>getResult</b>(){
 *         //Return the result of calculation here.
 *         return res;
 *     }
 * }
 * </pre>
 *
 * <h3>2. Create an Executor to run <b>Sum</b> class</h3>
 * 
 * <pre>
 * import com.researchenv.exptools.executor.Executor;
 * 
 * public class Main {
 *      public static void main(String[] args) {
 *          
 *          //Create the Executor to run your class (Sum)
 *          //Maximum threads running in parallel is 3 
 *          Executor{@literal <}<b>Sum</b>{@literal >} executor = new Executor<>(<b>3</b>);
 * 
 *          //Set the executor's {@link FinishedListener}
 *          executor.setFinishedListener(new FinishedListener{@literal <Sum>}() {
 *              {@literal @}Override
 *              public void finished(<b>Sum</b> source) {
 *                  //this is called when a thread is finished
 *                  System.out.println("Thread finished: result=" +source.<b>getResult()</b>);
 *              }
 *          });
 *
 *          //Add instances of your class (Sum)
 *          Sum s1 = new Sum(1,2);
 *          Sum s2 = new Sum(2,3);
 *          Sum s3 = new Sum(4,5);
 *          Sum s4 = new Sum(0,42);
 *
 *          executor.add(s1);
 *          executor.add(s2));
 *          executor.add(s3));
 *          executor.add(s4));
 * 
 *          //Start the executor
 *          executor.start();
 *
 *          //Blocs the execution until all executor threads is finished (optional)
 *          executor.waitForFinished();
 *      }
 * }
 * </pre>
 *
 * @author Erbe Pandini Rodrigues
 * @param <T> The class type to run on executor. The class will be returned by a listener after
 * execution is finished.
 */
public class Executor<T extends Object & Runnable > {

    /**
     * The max number of available processors.
     */
    public final int MAX_AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executorService;
    private final CompletionService<T> completionService;
    private final ArrayList<Callable<T>> list;
    private FinishedListener<T> finishedListener;
    private boolean isRunning;

    /**
     * Create an executor to run Runnable objects in separated threads. The minimum allowed threads
     * number is 1. The maxThreads can't exceed the number of available processors. The maximum number
     * of processor are stored in Executor.MAX_AVAILABLE_PROCESSORS;
     * @since 1.0.0
     * @param maxThreads Maximum number of threads running at same time. I doesn't exceed the machine maximum
     * number of available processors.
     */
    public Executor(int maxThreads) {

        maxThreads = maxThreads < 1 ? 1 : maxThreads;
        maxThreads = maxThreads > MAX_AVAILABLE_PROCESSORS ? MAX_AVAILABLE_PROCESSORS : maxThreads;

        executorService = Executors.newFixedThreadPool(maxThreads);
        completionService = new ExecutorCompletionService<>(executorService);
        list = new ArrayList<>();
        finishedListener = null;
        isRunning=false;
    }
    
    /**
     * Add a Runnable object to Executor list. Each object will run in a separated thread.
     * Does nothing after Executor.start() is called.
     * @since 1.0.0
     * @param runnable A Runnable object to run.
     */
    public void add(T runnable) {
        if(!isRunning){
            list.add(new Run(runnable));
        }
    }

    /**
     * Set a listener to the executor. The listener is called every time a Runnable finishes execution.
     * @since 1.0.0
     * @param finishedListener A finished listener.
     */
    public void setFinishedListener(FinishedListener<T> finishedListener) {
        this.finishedListener = finishedListener;
    }

    /**
     * Starts the execution of Runnable objects. Does nothing if the execution has started.
     * @since 1.0.0
     */
    public void start() {
        if (!isRunning) {
            
            isRunning=true;
            
            for (Callable<T> list1 : list) {
                completionService.submit(list1);
            }

            executorService.shutdown();

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < list.size(); i++) {
                        try {
                            Future<T> result = completionService.take();
                            if (finishedListener != null) {
                                finishedListener.finished(result.get());
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    isRunning=false;
                }
            });
            t.start();
        }
    }

    /**
     * Blocks the execution until all threads are finished.
     * @since 1.0.0
     */
    public void waitForFinished() {
        try {
            while (!executorService.awaitTermination(2, TimeUnit.SECONDS));
        } catch (InterruptedException ex) {
            Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Run<T extends Runnable> implements Callable<T> {

        private final T r;

        public Run(T r) {
            this.r = r;
        }

        @Override
        public T call() throws Exception {
            r.run();
            return r;
        }
    }
}