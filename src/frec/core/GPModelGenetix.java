package frec.core;

import frec.util.*;
import java.util.ArrayList;

/**
 * Class <code> Genetix </code> is a class that manages the whole genetic
 * programming performed over <code>Function</code> objects. This class is
 * designed to provide the basic genetic algorithm (standart scheme) with some
 * optimalization for the purposes of the F-ReC project. For more effectivity it
 * is advised to overrride this class (at least its <code>run()</code>
 * method).
 */

public class GPModelGenetix extends Genetix {
    
    protected GenList gen_list;

    protected int adv_min;

    protected int adv_max;

    private float reproduct_prob = 0.95f;

    private float selection_prob = 0.85f;

    /**
     * Creates a new <code>Genetix</code> object that will work over the
     * default symbol set. The symbol set will be set for the
     * <code>Function</code> class later. The default symbol set consists of
     * the following functions (function symbols):
     * <code> x+y, x-y, x*y, x/y, x^2, x^3, e^x, abs(x), sqrt(x), ln(x), log(x), min(x,y), max(x,y), sin(x), cos(x), tan(x), asin(x), acos(x), atan(x) </code>
     */

    public GPModelGenetix() {
        gen_list = new GenList(gen_size, gen_size / 3);
        int[] limits = GenetixFunction.getFunctionCodeLengthLimits();
        adv_min = limits[0];
        adv_max = limits[1];
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

    public GPModelGenetix(char[] symbol, byte[] arity) {
        this();
        setSymbols(symbol, arity);
    }

    public void run() {
        initializeGeneration();
        checkFitnessErrors();
        int max = 100;
        while (gen.length < gen_size / 2) {
            if (--max == 0) break;
            initializeGeneration();
            checkFitnessErrors();
        }
        max = 100;
        while (gen.length < gen_size) {
            if (--max == 0) break;
            super.addNewToGeneration(gen_size - gen.length);
            checkFitnessErrors();
        }
        checkPopulationErrors();
        if (gen.length < gen_size)
            super.addNewToGeneration(gen_size - gen.length);

        GenetixFunction.setFunctionCodeLengthLimits(++adv_min, ++adv_max);

        if (isSaving)
            while (genCounter < maxCounter) {
                saveGeneration();
                int select_size = Math.round((float) gen_size * selection_prob);
                selectBest(select_size);
                crossGeneration();
                mutateGeneration();
                reproductGeneration();
                addNewToGeneration(gen_size / 10);
                gen = new GenetixFunction[gen_list.size()];
                gen = (GenetixFunction[]) gen_list.toArray(gen);
                gen_list.clear();
                validate(gen);
                checkFitnessErrors();
                checkPopulationErrors();
                if (gen.length < gen_size)
                    super.addNewToGeneration(gen_size - gen.length);
                selectBest(gen_size);
                if (genCounter % 2 == 0)
                    GenetixFunction.setFunctionCodeLengthLimits(--adv_min,
                            --adv_max);
                else
                    GenetixFunction.setFunctionCodeLengthLimits(++adv_min,
                            ++adv_max);
                genCounter++;
            }
        else
            while (genCounter < maxCounter) {
                int select_size = Math.round((float) gen_size * selection_prob);
                selectBest(select_size);
                crossGeneration();
                mutateGeneration();
                reproductGeneration();
                addNewToGeneration(gen_size / 10);
                gen = new GenetixFunction[gen_list.size()];
                gen = (GenetixFunction[]) gen_list.toArray(gen);
                gen_list.clear();
                validate(gen);
                checkFitnessErrors();
                checkPopulationErrors();
                if (gen.length < gen_size)
                    super.addNewToGeneration(gen_size - gen.length);
                selectBest(gen_size);
                if (genCounter % 2 == 0)
                    GenetixFunction.setFunctionCodeLengthLimits(--adv_min,
                            --adv_max);
                else
                    GenetixFunction.setFunctionCodeLengthLimits(++adv_min,
                            ++adv_max);
                genCounter++;
            }

        hasFinished = true;
    }

    /**
     * This is used for initialization, the starting generation is initialized
     * randomly and its fitness values are computed.
     */

    protected synchronized void initializeGeneration() {
        gen = GenetixFunction.generate(gen_size, 1 + (adv_min + adv_max) / 2);
        validate(gen);
    }

    /**
     * This method mutates the actual generation. All the functions might be
     * mutated, the mutation process is managed using a mutation probability.
     */

    protected synchronized void mutateGeneration() {
        float prob = getMutationProbability();
        for (int i = 0; i < gen.length; i++)
            if (Generator.randomBoolean(prob)) {
                GenetixFunction mut = (GenetixFunction) gen[i].clone();
                mut.mutateFunction(usesArbitraryMutations());
                gen_list.add(mut);
            }
    }

    /**
     * This method crosses the functions of the actual generation. All the
     * functions might contribute to the new generation, the process is managed
     * using a crossing probability.
     */

    protected synchronized void crossGeneration() {
        int len = gen.length;
        float prob = getCrossingProbability();
        for (int i = 0; i < gen.length; i++)
            if (Generator.randomBoolean(prob * (1 - i / gen.length))) {
                int rnd = Generator.ascRandomInt(len);
                while (rnd == i)
                    rnd = Generator.ascRandomInt(len);
                GenetixFunction new1 = gen[i];
                GenetixFunction new2 = gen[rnd];
                new1.crossFunctions(new2, usesArbitraryCrossings());
                gen_list.add(new1);
                gen_list.add(new2);
            }
    }

    /**
     * This method crosses the functions of the actual generation. All the
     * functions might contribute to the new generation, the process is managed
     * using a crossing probability.
     */

    protected synchronized void reproductGeneration() {
        int len = gen.length;
        float prob = reproduct_prob;
        for (int i = 0; i < gen.length; i++)
            if (Generator.randomBoolean(prob)) {
                prob = reproduct_prob * ((i + 1) / (gen.length + 1));
                prob = reproduct_prob - prob;
                gen_list.add(gen[i]);
            }
    }

    /**
     * This method adds (randomly generated) functions to the current
     * generation, the number of functions added is specified by the parameter.
     * 
     * @param limit
     *            The size of the set of functions added to the current
     *            generation.
     */

    protected synchronized void addNewToGeneration(int limit) {
        if (gen.length + limit > gen_size) limit = gen_size - gen.length;
        GenetixFunction[] rnd_gen = GenetixFunction.generate(limit);
        gen_list.addAll(rnd_gen);
    }

    /**
     * Sets the reproduction probability.
     */

    public void setReproductProbability(float prob) {
        this.reproduct_prob = prob;
    }

    /**
     * Sets the reproduction probability.
     */

    public float getReproductProbability() {
        return reproduct_prob;
    }

    /**
     * Sets the selection probability.
     */

    public void setSelectionProbability(float prob) {
        this.selection_prob = prob;
    }

    /**
     * Sets the reproduction probability.
     */

    public float getSelectionProbability() {
        return selection_prob;
    }
}
