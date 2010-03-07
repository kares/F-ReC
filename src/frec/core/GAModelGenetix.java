package frec.core;

/**
 * Class <code> Genetix </code> is a class that manages the whole genetic
 * programming performed over <code>Function</code> objects. This class is
 * designed to provide the basic genetic algorithm (standart scheme) with some
 * optimalization for the purposes of the F-ReC project. For more effectivity it
 * is advised to overrride this class (at least its <code>run()</code>
 * method).
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

    public GAModelGenetix(char[] symbol, byte[] arity) {
        super();
        setSymbols(symbol, arity);
    }

    /**
     * This method causes this object (as a <code>Thread</code>) to start
     * execution. The default behaviour is the following:
     * <ul>
     * <li> <code> gen_size = 100; </code>
     * <li> <code> init(2, 6); </code>
     * <li> <code> check(); </code>
     * <li>
     * <code> while (gen.length < gen_size) { add(gen_size - gen.length); check(); } </code>
     * <li> <code> select(50); </code>
     * <li> <code> gen_size = 50; </code>
     * <li> <code> while (genCounter < maxCounter) { </code>
     * <ul>
     * <li> <code> if (save) saveGen(); </code>
     * <li> <code> mutate(); </code>
     * <li> <code> select(35); </code>
     * <li> <code> cross(); </code>
     * <li> <code> add(20); </code>
     * <li> <code> check(); </code>
     * <li> <code> genCounter++; </code>
     * </ul>
     * <li> <code> } </code>
     * </ul>
     */

    public void run() {
        gen_size *= 2;
        initializeGeneration();
        // System.out.println("inited:");
        // printGeneration();
        checkFitnessErrors();
        // System.out.println("checkedf:");
        // printGeneration();
        while (gen.length < gen_size) {
            // System.out.println("GOING TO ADD len="+gen.length);
            addNewToGeneration(gen_size - gen.length);
            // System.out.println("ADDED len="+gen.length);
            checkFitnessErrors();
            // System.out.println("FCHECK len="+gen.length);
            // checkPopulationErrors();
            // System.out.println("PCHECK len="+gen.length);
        }
        // System.out.println("added&checked:");
        // printGeneration();
        gen_size /= 2;
        selectBest(gen_size);
        // System.out.println("selected:");
        // printGeneration();
        if (isSaving)
            while (genCounter < maxCounter) {
                saveGeneration();
                mutateGeneration();
                // System.out.println("_mutated:");
                // printGeneration();
                selectBest(3 * gen_size / 4);
                // System.out.println("_selected:");
                // printGeneration();
                crossGeneration();
                // System.out.println("_crossed:");
                // printGeneration();
                checkFitnessErrors();
                // System.out.println("_checkedf:");
                // printGeneration();
                checkPopulationErrors();
                // System.out.println("_checkedg:");
                // printGeneration();
                selectBest(gen_size);
                // System.out.println("_selected:");
                // printGeneration();
                addNewToGeneration(gen_size - gen.length);
                // System.out.println("_added:");
                // printGeneration();
                checkFitnessErrors();
                genCounter++;
            }
        else
            while (genCounter < maxCounter) {
                mutateGeneration();
                selectBest(3 * gen_size / 4);
                crossGeneration();
                checkFitnessErrors();
                checkPopulationErrors();
                selectBest(gen_size);
                addNewToGeneration(gen_size - gen.length);
                checkFitnessErrors();
                genCounter++;
            }

        hasFinished = true;
    }

}
