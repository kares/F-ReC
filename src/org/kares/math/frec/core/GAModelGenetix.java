package org.kares.math.frec.core;

/**
 * Class <code> Genetix </code> is a class that manages the whole genetic
 * programming performed over <code>Function</code> objects. This class is
 * designed to provide the basic genetic algorithm (standart scheme) with some
 * optimalization for the purposes of the F-ReC project. For more effectivity it
 * is advised to overrride this class (at least its <code>run()</code>
 * method).
 *
 * The <code>compute()</code> behaviour is the following:
 * <ul>
 * <li> <code> generationSize = 100; </code>
 * <li> <code> init(2, 6); </code>
 * <li> <code> check(); </code>
 * <li>
 * <code> while (currentGeneration.length < generationSize) { add(generationSize - currentGeneration.length); check(); } </code>
 * <li> <code> select(50); </code>
 * <li> <code> generationSize = 50; </code>
 * <li> <code> while (generationCounter < generationLimit) { </code>
 * <ul>
 * <li> <code> if (save) saveGen(); </code>
 * <li> <code> mutate(); </code>
 * <li> <code> select(35); </code>
 * <li> <code> cross(); </code>
 * <li> <code> add(20); </code>
 * <li> <code> check(); </code>
 * <li> <code> generationCounter++; </code>
 * </ul>
 * <li> <code> } </code>
 * </ul>
 */
public class GAModelGenetix extends Genetix {
    
    /**
     * Creates a new <code>Genetix</code> object that will work over the
     * default symbol set. The symbol set will be set for the
     * <code>Function</code> class later. The default symbol set consists of
     * the following functions (function symbols):
     * <code> x+y, x-y, x*y, x/y, x^2, x^3, e^x, abs(x), sqrt(x), ln(x), log(x), min(x,y), max(x,y), sin(x), cos(x), tan(x), asin(x), acos(x), atan(x) </code>
     */
    public GAModelGenetix() {
        super();
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
    public GAModelGenetix(char[] symbol, byte[] arity) {
        super();
        setSymbols(symbol, arity);
    }
    */

    protected void computeInit() {
        int generationSize = getGenerationSize();
        generationSize *= 2;
        initializeGeneration();
        checkFitnessErrors();
        while (getCurrentGeneration().length < generationSize) {
            final int len = getCurrentGeneration().length;
            addNewToGeneration(generationSize - len);
            checkFitnessErrors();
        }

        generationSize /= 2;
        selectBest(generationSize);
    }

    protected void computeNext() {
        int generationSize = getGenerationSize();
        mutateGeneration();
        selectBest(3 * generationSize / 4);
        crossGeneration();
        checkFitnessErrors();
        checkPopulationErrors();
        selectBest(generationSize);
        final int len = getCurrentGeneration().length;
        addNewToGeneration(generationSize - len);
        checkFitnessErrors();
    }

}
