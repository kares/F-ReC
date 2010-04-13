package frec.core;

import frec.util.RandomHelper;

/**
 * Class <code> Genetix </code> is a class that manages the whole genetic
 * programming performed over <code>Function</code> objects. This class is
 * designed to provide the basic genetic algorithm (standart scheme) with some
 * optimalization for the purposes of the F-ReC project. For more effectivity it
 * is advised to overrride this class (at least its <code>run()</code>
 * method).
 */
public class GPModelGenetix extends Genetix {

    protected int currentCodeMin;
    protected int currentCodeMax;

    private float reproductProbability = 0.95f;
    private float selectionProbability = 0.85f;

    /**
     * Creates a new <code>Genetix</code> object that will work over the
     * default symbol set. The symbol set will be set for the
     * <code>Function</code> class later. The default symbol set consists of
     * the following functions (function symbols):
     * <code> x+y, x-y, x*y, x/y, x^2, x^3, e^x, abs(x), sqrt(x), ln(x), log(x), min(x,y), max(x,y), sin(x), cos(x), tan(x), asin(x), acos(x), atan(x) </code>
     */
    public GPModelGenetix() {
        currentCodeMin = GenetixFunction.getFunctionCodeMinLength();
        currentCodeMax = GenetixFunction.getFunctionCodeMaxLength();
    }

    /**
     * Creates a new <code>Genetix</code> object that will work over the
     * provided symbol set. The symbol set will be set for the
     * <code>Function</code> class later.
     * 
     * @param symbol
     *            Symbols to be used by functions.
     * @param arity
     *            Arity of the provided symbols.
     */
    /*
    public GPModelGenetix(char[] symbol, byte[] arity) {
        this();
        setSymbols(symbol, arity);
    }
    */

    protected void computeInit() {
        initializeGeneration();
        checkFitnessErrors();
        final int generationSize = getGenerationSize();
        int max = 100;
        while ( getCurrentGeneration().length < generationSize / 2 ) {
            if (--max == 0) break;
            initializeGeneration();
            checkFitnessErrors();
        }
        max = 100;
        while ( getCurrentGeneration().length < generationSize ) {
            if (--max == 0) break;
            final int len = getCurrentGeneration().length;
            super.addNewToGeneration(generationSize - len);
            checkFitnessErrors();
        }
        checkPopulationErrors();
        if ( getCurrentGeneration().length < generationSize ) {
            final int len = getCurrentGeneration().length;
            super.addNewToGeneration(generationSize - len);
        }
        GenetixFunction.setFunctionCodeLengthLimits(++currentCodeMin, ++currentCodeMax);
    }

    protected void computeNext() {
        final int generationSize = getGenerationSize();
        int select_size = Math.round(generationSize * selectionProbability);
        selectBest(select_size);
        crossGeneration();
        mutateGeneration();
        reproductGeneration();
        addNewToGeneration(generationSize / 10);
        computeFitness();
        checkFitnessErrors();
        checkPopulationErrors();
        if ( getCurrentGeneration().length < generationSize ) {
            final int len = getCurrentGeneration().length;
            super.addNewToGeneration(generationSize - len);
        }
        selectBest(generationSize);
        if ( getGenerationCounter() % 2 == 0 ) {
            GenetixFunction.setFunctionCodeLengthLimits(--currentCodeMin, --currentCodeMax);
        } else {
            GenetixFunction.setFunctionCodeLengthLimits(++currentCodeMin, ++currentCodeMax);
        }
    }

    /**
     * This is used for initialization, the starting generation is initialized
     * randomly and its fitness values are computed.
     */
    protected void initializeGeneration() {
        final int generationSize = getGenerationSize();
        setCurrentGeneration( generateFunctions(generationSize, 1 + (currentCodeMin + currentCodeMax) / 2) );
        computeFitness();
    }

    /**
     * This method mutates the actual generation. All the functions might be
     * mutated, the mutation process is managed using a mutation probability.
     */
    protected void mutateGeneration() {
        float prob = getMutationProbability();
        GenetixFunction.Tuple gen_list = getCurrentGenerationAsTuple();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        for (int i = 0; i < currentGeneration.length; i++) {
            if (RandomHelper.randomBoolean(prob)) {
                GenetixFunction mut = (GenetixFunction) currentGeneration[i].clone();
                mut.mutateFunction(isArbitraryMutations());
                gen_list.add(mut);
            }
        }
        setCurrentGeneration(gen_list);
    }

    /**
     * This method crosses the functions of the actual generation. All the
     * functions might contribute to the new generation, the process is managed
     * using a crossing probability.
     */
    protected void crossGeneration() {
        float prob = getCrossingProbability();
        GenetixFunction.Tuple gen_list = getCurrentGenerationAsTuple();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int len = currentGeneration.length;
        for (int i = 0; i < len; i++) {
            if (RandomHelper.randomBoolean(prob * (1 - i / currentGeneration.length))) {
                int rnd = RandomHelper.ascRandomInt(len);
                while (rnd == i) rnd = RandomHelper.ascRandomInt(len);
                GenetixFunction new1 = currentGeneration[i];
                GenetixFunction new2 = currentGeneration[rnd];
                new1.crossFunctions(new2, isArbitraryCrossings());
                gen_list.add(new1);
                gen_list.add(new2);
            }
        }
        setCurrentGeneration(gen_list);
    }

    /**
     * This method crosses the functions of the actual generation. All the
     * functions might contribute to the new generation, the process is managed
     * using a crossing probability.
     */
    protected void reproductGeneration() {
        float prob = reproductProbability;
        GenetixFunction.Tuple gen_list = getCurrentGenerationAsTuple();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int len = currentGeneration.length;
        for (int i = 0; i < len; i++) {
            if (RandomHelper.randomBoolean(prob)) {
                prob = reproductProbability * ((i + 1) / (len + 1));
                prob = reproductProbability - prob;
                gen_list.add(currentGeneration[i]);
            }
        }
        setCurrentGeneration(gen_list);
    }

    /**
     * This method adds (randomly generated) functions to the current
     * generation, the number of functions added is specified by the parameter.
     * 
     * @param limit the size of the set of functions added to the current generation
     */
    protected void addNewToGeneration(int limit) {
        //if (currentGeneration.length < generationSize && currentGeneration.length + limit > generationSize) {
        //    limit = generationSize - currentGeneration.length;
        //}
        GenetixFunction.Tuple gen_list = getCurrentGenerationAsTuple();
        gen_list.addAll( generateFunctions(limit) );
        setCurrentGeneration(gen_list);
    }

    /**
     * Sets the reproduction probability.
     */
    public void setReproductProbability(float prob) {
        this.reproductProbability = prob;
    }

    /**
     * Sets the reproduction probability.
     */
    public float getReproductProbability() {
        return reproductProbability;
    }

    /**
     * Sets the selection probability.
     */
    public void setSelectionProbability(float prob) {
        this.selectionProbability = prob;
    }

    /**
     * Sets the reproduction probability.
     */
    public float getSelectionProbability() {
        return selectionProbability;
    }
    
}
