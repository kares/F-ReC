
package org.kares.math.frec.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kares.math.frec.util.GenFile;
import org.kares.math.frec.util.RandomHelper;

/**
 * Class <code> Genetix </code> is a class that manages the whole
 * genetic programming performed over <code>FunctionTree</code> objects.
 * This class is designed to provide the basic genetic algorithm
 * (standart scheme) with some optimalization for the purposes of the
 * F-ReC project. For more effectivity it is advised to overrride this
 * class (at least its <code>run()</code> method).
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
    
    private double[] dataX, dataY;

    private float mutationProbability = 0.03f;
    private float crossingProbability = 0.90f;

    private long instancesCreated = 0;

    private double bestFitness = Float.NaN;

    /** This indicates whether saving is enabled (running in application mode). */
    private boolean isSaving = false;
    private GenFile genFile;
    
    protected Genetix() {
        // NOOP
    }

    public static Set getGenetixModels() {
        Set genetixClasses = new LinkedHashSet();
        genetixClasses.add(GYModelGenetix.class);
        genetixClasses.add(GPModelGenetix.class);
        genetixClasses.add(GAModelGenetix.class);
        return genetixClasses;
    }

    /**
     * Sets the minimal length of functions to be created.
     */
    public static void setMinFunctionLength(int min_len) {
        GenetixFunction.setFunctionCodeLengthLimits(min_len, -1);
    }

    /**
     * Sets the maximal length of functions to be created.
     */
    public static void setMaxFunctionLength(int max_len) {
        GenetixFunction.setFunctionCodeLengthLimits(-1, max_len);
    }

    /**
     * This method causes this object (as a <code>Thread</code>) to
     * start execution.
     */        
    public void run() {
        compute();
        if ( computeStopped() ) computing = null;
        else notifyComputed();
    }

    /**
     * Compute this genetix.
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

    protected abstract void computeInit() ;

    protected abstract void computeNext() ;

    private volatile Boolean computing = null;

    private boolean computeStopped() {
        if ( computing == null ) return false;
        return ! computing.booleanValue();
    }

    public void stopCompute() {
        computing = Boolean.FALSE;
    }

    protected GenetixFunction[] generateFunctions(int size) {
        instancesCreated += size;
        return GenetixFunction.generate(size);
    }

    protected GenetixFunction[] generateFunctions(int size, int length) {
        instancesCreated += size;
        return GenetixFunction.generate(size, length);
    }

    protected GenetixFunction[] generateFunctions(int size, boolean shorter) {
        instancesCreated += size;
        return GenetixFunction.generate(size, shorter);
    }

    /**
     * This is used for initialization, the starting generation
     * is initialized randomly and its fitness values are computed.
     */
    protected void initializeGeneration() {
        setCurrentGeneration( generateFunctions(generationSize) );
        computeFitness( getCurrentGeneration() );
    }

    /**
     * This is used for initialization, the starting generation
     * is initialized randomly and its fitness values are computed.
     * Functions of the first generation are created to have the
     * specified length.
     *
     * @param codeLength Length of the functions forming the initial generation.
     */
    protected void initializeGeneration(int codeLength) {
        setCurrentGeneration( generateFunctions(generationSize, codeLength) );
        computeFitness();
    }
        
    /**
     * This method validates the functions provided, meaning that it
     * computes the fitness values.
     *
     * @param genFxs functions to be validated.
     */
    protected void computeFitness() {
        computeFitness( getCurrentGeneration() );
    }
    
    /**
     * This method checks for bad functions in the current generation.
     * Bad functions are functions with not defined function values and
     * so the fitness value of such function is also undefined.
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
     * This method checks for equal functions in the current generation.
     * First it checks all the functions calling the <code>f.removeInverseElements()</code>
     * method. Then it checks if there are functiona equal to each other in
     * the current generation.
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
     * This method selects the best functions from the current
     * generation, the number of selected functions is specified
     * by the parameter.
     *
     * @param limit The limit of functions selected.
     *
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
     * This method mutates the actual generation.
     * All the functions might be mutated, the mutation
     * process is managed using a mutation probability.
     *
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
     * This method crosses the functions of the actual generation.
     * All the functions might contribute to the new generation, 
     * the process is managed using a crossing probability.
     */
    protected void crossGeneration() {
        int len = currentGeneration.length;
        int new_len = 2 * len;
        GenetixFunction[] newGeneration = new GenetixFunction[new_len];
        int i = 0;
        while ( i < new_len ) {
            //if (RandomHelper.randomBoolean(crossingProbability))
            //{
                int rnd1 = 1 + RandomHelper.ascRandomInt(len-1);
                int rnd2 = RandomHelper.ascRandomInt(len);
                if ( rnd1 < rnd2 ) rnd1 = rnd2;
                rnd2 = RandomHelper.randomInt(rnd1);
                newGeneration[i] = (GenetixFunction) currentGeneration[rnd1].clone();
                newGeneration[i+1] = (GenetixFunction) currentGeneration[rnd2].clone();
                newGeneration[i].crossFunctions(newGeneration[i+1], arbitraryCrossings);
                i += 2;
            //}
        }
            
        computeFitness(newGeneration);
        len = len + new_len;
        GenetixFunction[] all_gen = new GenetixFunction[len];
        len = currentGeneration.length;
        System.arraycopy(currentGeneration, 0, all_gen, 0, len);
        System.arraycopy(newGeneration, 0, all_gen, len, new_len);
        setCurrentGeneration(newGeneration);
    }

    /**
     * This causes to save the current generation of functions (including the fitness values).
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
     * This causes to print the current generation of functions (including the fitness values).
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
     * This method adds (randomly generated) functions to the current generation,
     * the number of functions added is specified by the parameter.
     *
     * @param limit the size of the set of functions added to the current generation.
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

    public void computeFitness(final GenetixFunction[] fxs) {
        for (int i=0; i<fxs.length; i++) {
            computeFitness(fxs[i]);
        }
    }

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
     * This method sets the training data. This data represents the 
     * aproximated function. 
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
     * This method is used to receive the approximated data
     * domain (x) values. 
     *
     * @return The x (real) values of the training data.
     */     
    public double[] getApproximatingDataX() {
        return dataX;
    }    

    /**
     * This method is used to receive the approximated data
     * function (y) values. 
     *
     * @return The y (real) values of the training data.
     */     
    
    public double[] getApproximatingDataY() {
        return dataY;
    }       
    
    /**
     * Returns the current value of the genetic counter.
     */     
    public int getGenerationCounter() {
        return generationCounter;
    }

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
     * Sets the generation maximum (the last generation to be created).
     */         
    public int getGenerationLimit() {
        return generationLimit;
    }    
    
    /**
     * Sets the generation maximum (the last generation to be created).
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

    private ComputedCallback computedCallback;

    protected void notifyComputed() {
        if ( computedCallback != null ) computedCallback.onComputed();
    }

    public void setComputedCallback(ComputedCallback computedCallback) {
        this.computedCallback = computedCallback;
    }

    public static interface ComputedCallback {
        void onComputed();
    }

    /**
     * This enables <code>Genetix</code> to save all the
     * functions produced during the computation.
     * By default the saving mode is set to false.
     * 
     * @param mode The save mode (true means saving is enabled)
     */             
    public void setSavingMode(boolean mode) {
        isSaving = mode;
        //if (isSaving && genFile==null) genFile = new GenFile();
    }    
    
    /**
     * Returns the result of the computing, this means returning
     * the best functions of the last generation. The number of those
     * functions is specified by the parameter.
     *
     * @param size The array size of the best functions to be returned.
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

    public String[] getBestFunctionsFormatted(int size) {
        final GenetixFunction[] bestFunctions = getBestFunctions(size);
        final String[] formatted = new String[bestFunctions.length];
        for (int i=0; i<size; i++) {
            formatted[i] = bestFunctions[i].formatFunction();
        }
        return formatted;
    }

    public double getBestFitness() {
        if ( bestFitness==Float.NaN ) {
            throw new IllegalStateException("not available");
        }
        return bestFitness;
    }

    /**
     * Method used to get a statistical report over the total instances created.
     * @return the instance counter
     */
    public long getFunctionsCreated() {
        return instancesCreated;
    }

}