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

package org.kares.math.frec.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kares.math.frec.util.GenFile;
import org.kares.math.frec.util.RandomHelper;

/**
 * <code>Genetix</code> represents the computation "engine" that 
 * manages the whole genetic programming thing performed over 
 * <code>FunctionTree</code> instances.
 * 
 * This class is an abstract base designed to provide some default 
 * genetic algorithm scheme. Concrete implementations are required
 * to implement the initialization and the iteration logic.
 */
public abstract class Genetix implements Runnable {
    
    /** 
     * The (actual) generation (includes the fitness).
     */
    private GenetixFunction[] currentGeneration;
    
    /** 
     * The size of one generation, default is 100.
     */
    private int generationSize = 100;
    
    /** How many generations have been created so far. */
    private int generationCounter = 0;
    
    /** 
     * How many generations will be created before computing ends, default = 100.
     */
    private int generationLimit = 100;
    
    private boolean arbitraryMutations = false;
    private boolean arbitraryCrossings = false;    
    
    /**
     * The approximated sample data.
     */
    private double[] dataX, dataY;

    private float mutationProbability = 0.03f;
    private float crossingProbability = 0.90f;

    private long instancesCreated = 0; // UNUSED

    private double bestFitness = -1;

    /** This indicates whether saving is enabled (running in application mode). */
    private boolean isSaving = false;
    private GenFile genFile;
    
    /**
     * An empty constructor.
     */
    protected Genetix() {
        // NOOP
    }

    /**
     * Get the available genetix implementations.
     * @return Set of {@link Genetix} classes.
     */
    public static Set getGenetixModels() {
        Set genetixClasses = new LinkedHashSet();
        genetixClasses.add(GYModelGenetix.class);
        genetixClasses.add(GPModelGenetix.class);
        genetixClasses.add(GAModelGenetix.class);
        return genetixClasses;
    }

    /**
     * @see GenetixFunction#getFunctionCodeMinLength()
     */
    public static int getMinFunctionLength() {
    	return GenetixFunction.getFunctionCodeMinLength();
    }
    
    /**
     * Sets the minimal length of functions that will be created.
     * The length here corresponds to Read's code length.
     * @see GenetixFunction#setFunctionCodeMinLength(int)
     */
    public static void setMinFunctionLength(int min_len) {
    	GenetixFunction.setFunctionCodeMinLength(min_len);
    }

    /**
     * @see GenetixFunction#getFunctionCodeMaxLength()
     */
    public static int getMaxFunctionLength() {
    	return GenetixFunction.getFunctionCodeMaxLength();
    }
    
    /**
     * Sets the maximal length of functions that will be created.
     * The length here corresponds to Read's code length.
     * @see GenetixFunction#setFunctionCodeMaxLength(int)
     */
    public static void setMaxFunctionLength(int max_len) {
        GenetixFunction.setFunctionCodeMaxLength(max_len);
    }

    /**
     * Start the computation.
     * @see #compute()
     * @see #notifyComputed()
     */        
    public void run() {
        compute();
        if ( computeStopped() ) computing = null;
        else notifyComputed();
    }

    /**
     * Compute this genetix.
     * Performs initialization {@link #computeInit()} and then
     * iterates to the next generation {@link #computeNext()}.
     * This is repeated until the {@link #getGenerationLimit()}
     * is reached. 
     */
    public void compute() {
        computing = Boolean.TRUE;
        computeInit();
        while (generationCounter < generationLimit) {
            if ( computeStopped() ) return;
            if ( isSaving ) saveGeneration();
            computeNext();
            //debugGeneration( System.out );
            generationCounter++;
        }
        computing = null;
    }

    /**
     * Performs the initial computation (initialize the first generation).
     */
    protected abstract void computeInit() ;

    /**
     * Perform the next iteration (a full generation lifecycle).
     */
    protected abstract void computeNext() ;

    private volatile Boolean computing = null;

    private boolean computeStopped() {
        if ( computing == null ) return false;
        return ! computing.booleanValue();
    }

    /**
     * Halt the computation (if it's happening).
     * NOTE: It won't stop immediately but when the current iteration finishes.
     */
    public void stopCompute() {
        computing = Boolean.FALSE;
    }

    /**
     * Generate some "random" population instances.
     * @param size The size of instances to generate.
     * @return The generated functions.
     */
    protected GenetixFunction[] generateFunctions(int size) {
        instancesCreated += size;
        return GenetixFunction.generate(size);
    }

    /**
     * Generate some "random" population instances.
     * @param size The size of instances to generate.
     * @param length The code length of the generated functions.
     * @return The generated functions.
     */
    protected GenetixFunction[] generateFunctions(int size, int length) {
        instancesCreated += size;
        return GenetixFunction.generate(size, length);
    }

    /**
     * Generate some "random" population instances.
     * @param size The size of instances to generate.
     * @param shorter A flag whether "shorter" functions should be preffered.
     * @return The generated functions.
     */
    protected GenetixFunction[] generateFunctions(int size, boolean shorter) {
        instancesCreated += size;
        return GenetixFunction.generate(size, shorter);
    }

    /**
     * Sets the current generation with random functions 
     * and computes the fitness values.
     */
    protected void initializeGeneration() {
        setCurrentGeneration( generateFunctions(generationSize) );
        computeFitness();
    }

    /**
     * Sets the current generation with random functions 
     * and computes the fitness values.
     * @param length Length of the functions forming the initial generation.
     */
    protected void initializeGeneration(final int length) {
        setCurrentGeneration( generateFunctions(generationSize, length) );
        computeFitness();
    }
        
    /**
     * Compute the fitness values for the current generation of functions.
     */
    protected void computeFitness() {
        computeFitness( getCurrentGeneration() );
    }
    
    /**
     * Check for invalid fitness values in the current generation 
     * and keep only the valid functions.
     */
    protected void checkFitnessErrors() {
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        int size = 0;
        int[] index = new int[currentGeneration.length];
        for ( int i=0; i<currentGeneration.length; i++ ) {
            if ( currentGeneration[i].isFitnessValid() ) index[size++] = i;
        }
        if ( size == currentGeneration.length ) return;
        GenetixFunction[] validGeneration = new GenetixFunction[size];
        for (int i=0; i<size; i++) {
            validGeneration[i] = currentGeneration[index[i]];
        }
        setCurrentGeneration( validGeneration );
    }
    
    /**
     * Performs various function error checks on the current generation.
     * @see GenetixFunction#checkFunction()
     */    
    protected void checkPopulationErrors() {
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int[] index = new int[currentGeneration.length];
        int invalidSize = 0;

        for (int i=0; i<currentGeneration.length; i++) {
            final boolean valid = currentGeneration[i].checkFunction();
            if ( ! valid ) {
                index[i] = -1;
                invalidSize++;
            }
        }
        
        for (int i=0; i<currentGeneration.length; i++) {
            if ( index[i] == -1 ) continue;
            for (int j=i+1; j<currentGeneration.length; j++) {
                if ( index[j] == -1 ) continue;
                if ( currentGeneration[i].equals(currentGeneration[j]) ) {
                    index[j] = -1;
                    invalidSize++;
                }
            }
        }
        
        if ( invalidSize > 0 ) {
            final int size = currentGeneration.length - invalidSize;
            GenetixFunction[] validGeneration = new GenetixFunction[size];
            int j = 0;
            for (int i=0; i<size; i++) {
                while ( index[j] == -1 ) j++;
                validGeneration[i] = currentGeneration[j++];
            }
            setCurrentGeneration(validGeneration);
        }
    }    

    /**
     * This method selects the best functions from the current generation.
     * @param limit The limit of functions to be selected.
     */    
    protected void selectBest(int limit) {
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        if ( currentGeneration.length < limit ) {
            limit = currentGeneration.length;
        }
        if ( limit <= 0 ) return;
        Arrays.sort( currentGeneration );
        GenetixFunction[] selected = new GenetixFunction[limit];
        System.arraycopy(currentGeneration, 0, selected, 0, limit);
        setCurrentGeneration(selected);
        bestFitness = selected[0].getFitness();
    }
    
    /**
     * Performs mutation on the actual generation.
     * All functions might be mutated, the mutation process is 
     * decided using a mutation probability.
     */        
    protected void mutateGeneration() {
       for (int i=0; i<currentGeneration.length; i++) {
           final GenetixFunction fx = currentGeneration[i];
           if ( RandomHelper.randomBoolean(mutationProbability) ) {
               if (fx.getFitness() > 3 * bestFitness) {
                   GenetixFunction org = (GenetixFunction) fx.clone();
                   fx.mutateFunction(arbitraryMutations);
                   if ( ! computeFitness(fx) ) currentGeneration[i] = org;
               }
           }
       }
    }
    
    /**
     * Crosses the functions of the actual generation among each other.
     * The crossing is implemented in a standard fashion - parents do not
     * necessarily make it to the new generation if they were selected to
     * be crossed. This is probably not wise and  concrete implemenations 
     * are welcome to define their own crossing scheme.
     */
    protected void crossGeneration() {
        final float prob = getCrossingProbability();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int len = currentGeneration.length;
        for (int i = 0; i < len; i++) {
            if ( RandomHelper.randomBoolean(prob) ) {
                int rnd = RandomHelper.randomInt(len);
                while (rnd == i) rnd = RandomHelper.randomInt(len);
                GenetixFunction f1 = currentGeneration[i];
                GenetixFunction f2 = currentGeneration[rnd];
                f1.crossFunctions(f2, isArbitraryCrossings());
            }
        }
        setCurrentGeneration(currentGeneration);
    }

    /**
     * NOTE: NOT USED !
     */
    void saveGeneration() {
        if ( true ) return; // TODO saving disabled !
        if (genFile == null) throw new IllegalStateException("no gen file");
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        for (int i=0; i<currentGeneration.length; i++) {
            genFile.write( currentGeneration[i].getFunctionTree() );
        }
    }

    /**
     * @param outStream
     * NOTE: NOT USED !
     */
    void printGeneration(final PrintWriter outStream) {
        outStream.println("GENERATION " + generationCounter);
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        for (int i=0; i<currentGeneration.length; i++) {
            outStream.println( currentGeneration[i].formatFunction() );
        }
    }

    /**
     * @param outStream
     * NOTE: NOT USED !
     */
    void printGeneration(final PrintStream outStream) {
        outStream.println("GENERATION " + generationCounter);
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        for (int i=0; i<currentGeneration.length; i++) {
            outStream.println( currentGeneration[i].formatFunction() );
        }
    }

    /**
     * @param outStream
     * NOTE: NOT USED !
     */
    void debugGeneration(final PrintStream outStream) {
        outStream.println("GENERATION " + generationCounter);
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        for (int i=0; i<currentGeneration.length; i++) {
            outStream.println( currentGeneration[i] );
        }
    }

    /**
     * This method adds (randomly generated) functions to the current generation.
     * @param limit The size of the functions added to the current generation.
     */         
    protected void addNewToGeneration(int limit) {
        if (currentGeneration.length + limit > generationSize) {
            limit = generationSize - currentGeneration.length;
        }
        GenetixFunction[] randomGeneration = generateFunctions(limit, true);
        computeFitness(randomGeneration);
        
        int len = currentGeneration.length + limit;
        GenetixFunction[] newGeneration = new GenetixFunction[len];
        len = currentGeneration.length;
        System.arraycopy(currentGeneration, 0, newGeneration, 0, len);
        System.arraycopy(randomGeneration, 0, newGeneration, len, limit);
        setCurrentGeneration(newGeneration);
    }

    /**
     * Computes (and sets) the fitness for the given function.
     * @param fx
     * @return Trues if the fitness is valid.
     */
    public strictfp boolean computeFitness(final GenetixFunction fx) {
        // dataY and the funcY values (based on dataX)
        double arithmeticDiff = 0;
        for ( int j=0; j<dataX.length; j++ ) {
            double funcYj = fx.getFunctionValue( dataX[j] );
            if ( Double.isNaN(funcYj) ) {
                arithmeticDiff = Double.NaN;
                break;
            }
            arithmeticDiff += Math.abs(dataY[j] - funcYj);
        }
        return fx.setFitness( arithmeticDiff );
        //return arithmeticDiff;
    }

    /**
     * @see #computeFitness(GenetixFunction)
     */
    public void computeFitness(final GenetixFunction[] fxs) {
        for (int i=0; i<fxs.length; i++) {
            computeFitness(fxs[i]);
        }
    }

    /**
     * @return The current generation.
     */
    public GenetixFunction[] getCurrentGeneration() {
        return this.currentGeneration;
    }

    protected GenetixFunction.Tuple getCurrentGenerationAsTuple() {
        return new GenetixFunction.Tuple(this.currentGeneration);
    }

    protected void setCurrentGeneration(GenetixFunction[] gen) {
        this.currentGeneration = gen;
    }

    protected void setCurrentGeneration(GenetixFunction.Tuple tuple) {
        this.currentGeneration = tuple.snapshot();
    }

    /**
     * Sets the training data. The training data represent 
     * the aproximated function. 
     *
     * @param dataX The x (real) values of the training data.
     * @param dataY The y values (f(x) values) of the training data.
     */     
    public void setApproximatingData(double[] dataX, double[] dataY) {
        if (dataX == null) dataX = new double[0];
        if (dataY == null) dataY = new double[0];
        final int len = dataX.length;
        if (len != dataY.length) {
            throw new IllegalArgumentException(
                    "dataX.length (" + dataX.length + ") != " +
                    "dataY.length (" + dataY.length + ")");
        }
        double[] _dataX = new double[len];
        double[] _dataY = new double[len];
        for (int i=0; i<len; i++) {
            _dataX[i] = dataX[i];
            _dataY[i] = dataY[i];
        }
        this.dataX = _dataX;
        this.dataY = _dataY;
    }

    /**
     * @see #setApproximatingData(double[], double[])
     */
    public void setApproximatingData(float[] dataX, float[] dataY) {
        if (dataX == null) dataX = new float[0];
        if (dataY == null) dataY = new float[0];
        final int len = dataX.length;
        if (len != dataY.length) {
            throw new IllegalArgumentException(
                    "dataX.length (" + dataX.length + ") != " +
                    "dataY.length (" + dataY.length + ")");
        }
        double[] _dataX = new double[len];
        double[] _dataY = new double[len];
        for (int i=0; i<len; i++) {
            _dataX[i] = dataX[i];
            _dataY[i] = dataY[i];
        }
        this.dataX = _dataX;
        this.dataY = _dataY;
    }

    /**
     * @return The x values of the training data.
     */     
    public double[] getApproximatingDataX() {
        return dataX;
    }    

    /**
     * @return The f(x) values of the training data.
     */     
    public double[] getApproximatingDataY() {
        return dataY;
    }       
    
    /**
     * Returns the current value of the generation counter.
     */     
    public int getGenerationCounter() {
        return generationCounter;
    }

    /**
     * Resets the generation counter to zero.
     */
    public void resetGenerationCounter() {
        generationCounter = 0;
    }

    /**
     * Returns the current size of the generation.
     */         
    public int getGenerationSize() {
        return generationSize;
    }
    
    /**
     * Sets the size of the generation.
     */         
    public void setGenerationSize(int size) {
        this.generationSize = size;
    }
    
    /**
     * @return The generation limit 
     * (how many generations should the genetic iteration create).
     */         
    public int getGenerationLimit() {
        return generationLimit;
    }    
    
    /**
     * Sets the generation limit 
     * (how many generations should the genetic iteration create).
     */         
    public void setGenerationLimit(int max) {
        this.generationLimit = max;
    }    
    
    /**
     * Returns the mutation probability.
     */         
    public float getMutationProbability() {
        return mutationProbability;
    }
    
    /**
     * Sets the mutation probability.
     */         
    public void setMutationProbability(float prob) {
        this.mutationProbability = prob;
    }
    
    /**
     * Returns the crossing probability.
     */         
    public float getCrossingProbability() {
        return crossingProbability;
    }

    /**
     * Sets the crossing probability.
     */             
    public void setCrossingProbability(float prob) {
        this.crossingProbability = prob;
    }  
    
    public boolean isArbitraryMutations() {
        return this.arbitraryMutations;
    }

    public void setArbitraryMutations(boolean flag) {
        this.arbitraryMutations = flag;
    }

    public boolean isArbitraryCrossings() {
        return this.arbitraryCrossings;
    }

    public void setArbitraryCrossings(boolean flag) {
        this.arbitraryCrossings = flag;
    }

    public void setSavingMode(boolean mode) {
        isSaving = mode;
        //if (isSaving && genFile==null) genFile = new GenFile();
    } 
    
    private ComputedCallback computedCallback;

    /**
     * Notify computed.
     */
    protected void notifyComputed() {
        if ( computedCallback != null ) computedCallback.onComputed();
    }

    /**
     * Set a notifications callback to be invoked when computation ends.
     * @param computedCallback
     */
    public void setComputedCallback(ComputedCallback computedCallback) {
        this.computedCallback = computedCallback;
    }

    public static interface ComputedCallback {
        void onComputed();
    }   
    
    /**
     * Returns the best functions of the last generation.
     * Should be considered as the computation result.
     *
     * @param size The size of the array to be returned.
     */
    public GenetixFunction[] getBestFunctions(int size) {
        //selectBest(size);
        if (currentGeneration.length < size) size = currentGeneration.length;
        Arrays.sort(currentGeneration);
        final GenetixFunction[] bestFunctions = new GenetixFunction[size];
        for (int i=0; i<size; i++) {
            bestFunctions[i] = currentGeneration[i];
        }
        return bestFunctions;
    }

    /**
     * @see #getBestFunctions(int)
     * @see GenetixFunction#formatFunction()
     */
    public String[] getBestFunctionsFormatted(int size) {
        final GenetixFunction[] bestFunctions = getBestFunctions(size);
        final String[] formatted = new String[bestFunctions.length];
        for (int i=0; i<size; i++) {
            formatted[i] = bestFunctions[i].formatFunction();
        }
        return formatted;
    }

    /**
     * @return The best fitness there is in the current generation.
     */
    public double getBestFitness() {
        if ( bestFitness < 0 ) {
            throw new IllegalStateException("not available");
        }
        return bestFitness;
    }

    /**
     * Method used to get a statistical report over the total instances created.
     * @return the instance counter
     */
    /*
    public long getFunctionsCreated() {
        return instancesCreated;
    }
	*/

}