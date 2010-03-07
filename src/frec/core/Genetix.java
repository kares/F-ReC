
package frec.core;

import frec.util.*;
import java.util.Arrays;

    /**
     * Class <code> Genetix </code> is a class that manages the whole
     * genetic programming performed over <code>Function</code> objects.
     * This class is designed to provide the basic genetic algorithm 
     * (standart scheme) with some optimalization for the purposes of the
     * F-ReC project. For more effectivity it is advised to overrride this
     * class (at least its <code>run()</code> method).
     */

public abstract class Genetix implements Runnable
{
    /** The (actual) generation (includes the fitness). */
    protected GenetixFunction[] gen;
    
    /** 
     * The size of the generation (how many elements are used). 
     * Default = 100.
     */
    protected int gen_size = 100;
    
    /** Generation counter - how many generations have been created yet. */
    protected int genCounter = 0;
    
    /** 
     * Generation counter limit - how many generations will be created. 
     * Default = 100. 
     */
    protected int maxCounter = 100;    
    
    /** This indicates whether computing is done. */
    protected boolean hasFinished = false;

    /** This indicates whether saving is enabled (running in application mode). */
    protected boolean isSaving = false;    
    
    private boolean arbitraryMutations = false;
    private boolean arbitraryCrossings = false;    
    
    private float[] data_x;
    private float[] data_y;

    private float mutation_prob = 0.03f;
    private float crossing_prob = 0.90f;
    
    private float bestFitness = Float.NaN;

    private GenFile genFile;
    
    /**
     * Creates a new <code>Genetix</code> object that will work over the default
     * symbol set. The symbol set will be set for the <code>Function</code> class later. 
     * The default symbol set consists of the following functions (function symbols): 
     * <code> x+y, x-y, x*y, x/y, x^2, x^3, e^x, abs(x), sqrt(x), ln(x), log(x), min(x,y), max(x,y), sin(x), cos(x), tan(x), asin(x), acos(x), atan(x) </code>
     *
     */      
    
    protected Genetix()
    {
        long seed = 0x00000000ffffffffL;
        long rnd = 0x0000000021abcdefL;
        seed = seed & (((System.currentTimeMillis() << 1) * seed + rnd ) >> 2 );
        rnd = rnd ^ Double.doubleToLongBits(Math.random());
        seed = ((seed + rnd) >> 1) * (seed >> 1);
        int[] s = new int[600];
        for (int i=0; i<600; i++)
            s[i] = (int)((seed ^ System.currentTimeMillis()) + rnd * i)
                + ((int)(Math.random() * (double)Integer.MAX_VALUE) >> 1);
        Generator.init(s);
        if (isSaving) genFile = new GenFile();
    }

    /**
     * This method causes this object (as a <code>Thread</code>) to
     * start execution.
     */        
    
    public abstract void run() ;

    /**
     * This is used for initialization, the starting generation
     * is initialized randomly and its fitness values are computed.
     *
     */        
    
    protected synchronized void initializeGeneration()
    {
        gen = GenetixFunction.generate(gen_size);
        validate(gen);
    }

    /**
     * This is used for initialization, the starting generation
     * is initialized randomly and its fitness values are computed.
     * Functions of the first generation are created to have the
     * specified length.
     *
     * @param code_len Length of the functions forming the initial generation.
     *
     */            
    
    protected synchronized void initializeGeneration(int code_len)
    {
        gen = GenetixFunction.generate(gen_size, code_len);
        validate(gen);
    }
        
    /**
     * This method validates the functions provided, meaning that it
     * computes the fitness values.
     *
     * @param function Array containing the functions to be validated.
     * @return function Array containing the functions to be validated.
     */                
    
    protected synchronized void validate(GenetixFunction[] gp)
    {
        float[] func_y = new float[data_x.length];
        for (int i=0; i<gp.length; i++)
        {
            for (int j=0; j<func_y.length; j++)
                func_y[j] = gp[i].getFunctionValue(data_x[j]);
            gp[i].setFitness(GenMath.E(data_y, func_y));
        }
    }
    
    /**
     * This method checks for bad functions in the current generation.
     * Bad functions are functions with not defined function values and
     * so the fitness value of such function is also undefined.
     *
     */    

    protected synchronized void checkFitnessErrors()
    {
        int ind = 0;
        int[] index = new int[gen.length];
        for (int i=0; i<gen.length; i++)
            if (gen[i].isValid())
                index[ind++] = i;

        if (ind==gen.length) return;
        GenetixFunction[] tmp_gen = gen;
        gen = new GenetixFunction[ind];
        for (int i=0; i<ind; i++)
            gen[i] = tmp_gen[index[i]];
    }
    
    /**
     * This method checks for equal functions in the current generation.
     * First it checks all the functions calling the <code>f.check()</code>
     * method. Then it checks if there are functiona equal to each other in
     * the current generation.
     *
     */    

    protected synchronized void checkPopulationErrors()
    {
        for (int i=0; i<gen.length; i++)
            if (!gen[i].checkFunction())
                gen[i] = null;
        
        int[] index = new int[gen.length];
        int ind = 0;
        for (int i=0; i<gen.length; i++)
        {
            if (gen[i]==null) index[i] = -1;
            if (index[i]==-1)
            {
                ind++;
                continue;
            }
            for (int j=i+1; j<gen.length; j++)
                if ((index[j]!=-1) 
                && (gen[j]!=null)
                && (gen[i].equals(gen[j])))
                    index[j] = -1;
        }
        
        if (ind!=0)
        {
            GenetixFunction[] tmp_gen = gen;
            ind = gen.length - ind;
            gen = new GenetixFunction[ind];
            int j = 0;
            for (int i=0; i<ind; i++)
            {
                while (index[j]==-1) j++;
                gen[i] = tmp_gen[j++];
            }
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
    
    protected synchronized void selectBest(int limit)
    {
        if (gen.length < limit) limit = gen.length;
        Arrays.sort(gen);
        GenetixFunction[] sel = 
            new GenetixFunction[limit];
        System.arraycopy(gen, 0, sel, 0, limit);
        gen = sel;
        bestFitness = gen[0].getFitness();
    }
    
    /**
     * This method mutates the actual generation.
     * All the functions might be mutated, the mutation
     * process is managed using a mutation probability.
     *
     */        

    protected synchronized void mutateGeneration()
    {   
       for (int i=0; i<gen.length; i++)
           if (Generator.randomBoolean(mutation_prob))
               if (gen[i].getFitness() > 3 * bestFitness)
               {
                   GenetixFunction org = (GenetixFunction)gen[i].clone();
                   gen[i].mutateFunction(arbitraryMutations);
                   float[] func_y = new float[data_x.length];
                   for (int j=0; j<func_y.length; j++)
                       func_y[j] = gen[i].getFunctionValue(data_x[j]);
                   if (!gen[i].setFitness(GenMath.E(data_y, func_y)))
                       gen[i] = org;
               }
    }
    
    /**
     * This method crosses the functions of the actual generation.
     * All the functions might contribute to the new generation, 
     * the process is managed using a crossing probability.
     *
     */            

    protected synchronized void crossGeneration()
    {
        int len = gen.length;
        int new_len = 2 * len;
        GenetixFunction[] new_gen = 
            new GenetixFunction[new_len];
        int size = 0;
        while (size < new_len)
            if (Generator.randomBoolean(crossing_prob))
            {
                int rnd1 = 1+Generator.ascRandomInt(len-1);
                int rnd2 = Generator.ascRandomInt(len);
                if (rnd1 < rnd2) rnd1 = rnd2;
                rnd2 = Generator.randomInt(rnd1);
                new_gen[size] = (GenetixFunction)gen[rnd1].clone();
                new_gen[size+1] = (GenetixFunction)gen[rnd2].clone();
                new_gen[size++].crossFunctions(new_gen[size++], arbitraryCrossings);
            }
            
        validate(new_gen);
        len = len + new_len;
        GenetixFunction[] all_gen = new GenetixFunction[len];
        len = gen.length;
        System.arraycopy(gen, 0, all_gen, 0, len);
        System.arraycopy(new_gen, 0, all_gen, len, new_len);
        gen = new_gen;
    }

    /**
     * This causes to save the current generation of functions (including the fitness values).
     * GenFile object is used for saving to a file.
     *
     */     
    
    protected synchronized void saveGeneration()
    {
        try
        {
            genFile.write("GENERATION"+genCounter);
            genFile.write(gen);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This causes to print the current generation of functions (including the fitness values) 
     * on the standart output.
     *
     */         
    
    public synchronized void printGeneration()
    {
        System.out.println("GENERATION "+genCounter);
        for (int i=0; i<gen.length; i++)
            System.out.println(gen[i]);
    }
    
    /**
     * This method adds (randomly generated) functions to the current generation,
     * the number of functions added is specified by the parameter.
     *
     * @param add_limit The size of the set of functions added to the current generation.
     */         

    protected synchronized void addNewToGeneration(int limit)
    {
        if (gen.length + limit > gen_size)
            limit = gen_size - gen.length;
        GenetixFunction[] rnd_gen = 
            GenetixFunction.generate(limit, true);
        validate(rnd_gen);
        int len = gen.length + limit;
        GenetixFunction[] new_gen = new GenetixFunction[len];
        len = gen.length;
        System.arraycopy(gen, 0, new_gen, 0, len);
        System.arraycopy(rnd_gen, 0, new_gen, len, limit);
        gen = new_gen;        
    }

    /**
     * This method sets the training data. This data represents the 
     * aproximated function. 
     *
     * @param data_x The x (real) values of the training data.
     * @param data_y The y values (f(x) values) of the training data.
     */     
    
    public void setApproximatingData(float[] data_x, float[] data_y)
    {
        this.data_x = data_x;
        this.data_y = data_y;
    }
    
    /**
     * This method is used to receive the approximated data
     * domain (x) values. 
     *
     * @return The x (real) values of the training data.
     */     
    
    public float[] getApproximatingDataX()
    {
        return data_x;
    }    

    /**
     * This method is used to receive the approximated data
     * function (y) values. 
     *
     * @return The y (real) values of the training data.
     */     
    
    public float[] getApproximatingDataY()
    {
        return data_y;
    }       
    
    /**
     * Returns the current value of the genetic counter.
     */     

    public int getGenerationCounter()
    {
        return genCounter;
    }

    /**
     * Returns the current size of the generation.
     */         
    
    public int getGenerationSize()
    {
        return gen_size;
    }
    
    /**
     * Sets the size of the generation.
     */         

    public void setGenerationSize(int size)
    {
        this.gen_size = size;
    }
    
    /**
     * Sets the generation maximum (the last generation to be created).
     */         

    public int getGenerationCounterLimit()
    {
        return maxCounter;
    }    
    
    /**
     * Sets the generation maximum (the last generation to be created).
     */         

    public void setGenerationCounterLimit(int max)
    {
        this.maxCounter = max;
    }    
    
    /**
     * Returns the mutation probability.
     */         

    public float getMutationProbability()
    {
        return mutation_prob;
    }
    
    /**
     * Sets the mutation probability.
     */         

    public void setMutationProbability(float prob)
    {
        this.mutation_prob = prob;
    }
    
    /**
     * Returns the crossing probability.
     */         
    
    public float getCrossingProbability()
    {
        return crossing_prob;
    }

    /**
     * Sets the crossing probability.
     */             
    
    public void setCrossingProbability(float prob)
    {
        this.crossing_prob = prob;
    }  
    
    /**
     * Returns the crossing probability.
     */         
    
    public boolean usesArbitraryMutations()
    {
        return this.arbitraryMutations;
    }

    /**
     * Sets the crossing probability.
     */             
    
    public void setArbitraryCrossings(boolean flag)
    {
        this.arbitraryCrossings = flag;
    }
    
    /**
     * Returns the crossing probability.
     */         
    
    public boolean usesArbitraryCrossings()
    {
        return this.arbitraryCrossings;
    }

    /**
     * Sets the crossing probability.
     */             
    
    public void setArbitraryMutations(boolean flag)
    {
        this.arbitraryMutations = flag;
    }    

    /**
     * Sets the minimal length of functions to be created.
     */             
    
    public static void setMinFunctionLength(int min_len)
    {
        GenetixFunction.setFunctionCodeLengthLimits(min_len, -1);
    }

    /**
     * Sets the maximal length of functions to be created.
     */             
    
    public static void setMaxFunctionLength(int max_len)
    {
        GenetixFunction.setFunctionCodeLengthLimits(-1, max_len);
    }    
    
    /**
     * Sets the mutation probability.
     */             
    
    public static void setSymbols(char[] symbol, byte[] arity)
    {
        Function.setAllowedSymbols(symbol, arity);
        byte min = arity[0];
        byte max = arity[0];
        for (int i=1; i<arity.length; i++)
        {
            if (arity[i]<min) min = arity[i];
            else
            if (arity[i]>max) max = arity[i];
        }
        if (min==1) min = 0;
        LimitedTree.setCodeLimits(min, max);
    }

    /**
     * Tests whether the computing has finished (<code>Genetix</code> is ready).
     * 
     */             
    
    public boolean hasFinishedComputing()
    {
        return hasFinished;
    }
    
    /**
     * This enables <code>Genetix</code> to save all the
     * functions produced during the computation.
     * By default the saving mode is set to false.
     * 
     * @param mode The save mode (true means saving is enabled)
     */             
    
    public void setSavingMode(boolean mode)
    {
        isSaving = mode;
        if ((mode)&&(genFile==null)) 
            genFile = new GenFile();
    }    
    
    /**
     * Returns the result of the computing, this means returning
     * the best functions of the last generation. The number of those
     * functions is specified by the parameter.
     *
     * @param size The array size of the best functions to be returned.
     */             
    
    public String[] getBestResults(int size)
    {
        selectBest(size);
        if (gen.length < size) size = gen.length;
        
        String[] funcs = new String[size];
        for (int i=0; i<size; i++)
            funcs[i] = gen[i].parseFunction();
        
        return funcs;
    }
    
    public String getBestFitness()
    {
        if (bestFitness==Float.NaN)
            return "not available";
        else return String.valueOf(bestFitness);
    }
    
}