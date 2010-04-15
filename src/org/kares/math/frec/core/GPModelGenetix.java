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

import org.kares.math.frec.util.RandomHelper;

/**
 * Implements the genetic programming (GP) computing scheme.
 * The most noticeable difference between GA and GP is that
 * in the GP model parents might "survive" multiple generations.
 * This seems more appropriate for genetic computing over 
 * syntax trees for data approximation. 
 * 
 * @author kares
 */
public class GPModelGenetix extends Genetix {

    protected int currentCodeMin;
    protected int currentCodeMax;

    private float reproductProbability = 0.95f;
    private float selectionProbability = 0.85f;

    /**
     */
    public GPModelGenetix() {
        currentCodeMin = GenetixFunction.getFunctionCodeMinLength();
        currentCodeMax = GenetixFunction.getFunctionCodeMaxLength();
    }

    /**
     * @see org.kares.math.frec.core.Genetix#computeInit()
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
        //GenetixFunction.setFunctionCodeLengthLimits(++currentCodeMin, ++currentCodeMax);
    }

    /**
     * @see org.kares.math.frec.core.Genetix#computeNext()
     */
    protected void computeNext() {
        final int generationSize = getGenerationSize();
        int select_size = Math.round(generationSize * getSelectionProbability());
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
        //if ( getGenerationCounter() % 2 == 0 ) {
        //    GenetixFunction.setFunctionCodeLengthLimits(--currentCodeMin, --currentCodeMax);
        //} else {
        //    GenetixFunction.setFunctionCodeLengthLimits(++currentCodeMin, ++currentCodeMax);
        //}
    }

    /**
     * @see org.kares.math.frec.core.Genetix#initializeGeneration()
     */
    protected void initializeGeneration() {
        final int generationSize = getGenerationSize();
        setCurrentGeneration( generateFunctions(generationSize, 1 + (currentCodeMin + currentCodeMax) / 2) );
        computeFitness();
    }

    /**
     * @see org.kares.math.frec.core.Genetix#mutateGeneration()
     */
    protected void mutateGeneration() {
        final float prob = getMutationProbability();
        GenetixFunction.Tuple newGeneration = getCurrentGenerationAsTuple();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        for (int i = 0; i < currentGeneration.length; i++) {
            if ( RandomHelper.randomBoolean(prob) ) {
                GenetixFunction mut = (GenetixFunction) currentGeneration[i].clone();
                mut.mutateFunction( isArbitraryMutations() );
                newGeneration.add(mut);
            }
        }
        setCurrentGeneration(newGeneration);
    }

    /**
     * @see org.kares.math.frec.core.Genetix#crossGeneration()
     */
    protected void crossGeneration() {
        final float prob = getCrossingProbability();
        GenetixFunction.Tuple newGeneration = getCurrentGenerationAsTuple();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int len = currentGeneration.length;
        for (int i = 0; i < len; i++) {
            if ( RandomHelper.randomBoolean(prob * (1 - i / len)) ) {
                int rnd = RandomHelper.ascRandomInt(len);
                while (rnd == i) rnd = RandomHelper.ascRandomInt(len);
                GenetixFunction new1 = currentGeneration[i];
                GenetixFunction new2 = currentGeneration[rnd];
                new1.crossFunctions(new2, isArbitraryCrossings());
                newGeneration.add(new1);
                newGeneration.add(new2);
            }
        }
        setCurrentGeneration(newGeneration);
    }

    /**
     * This method crosses the functions of the actual generation. All the
     * functions might contribute to the new generation, the process is managed
     * using a crossing probability.
     */
    protected void reproductGeneration() {
        float prob = getReproductProbability();
        GenetixFunction.Tuple newGeneration = getCurrentGenerationAsTuple();
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int len = currentGeneration.length;
        for (int i = 0; i < len; i++) {
            if ( RandomHelper.randomBoolean(prob) ) {
                prob = getReproductProbability() * ((i + 1) / (len + 1));
                prob = getReproductProbability() - prob;
                newGeneration.add(currentGeneration[i]);
            }
        }
        setCurrentGeneration(newGeneration);
    }

    /**
     * This method adds (randomly generated) functions to the current
     * generation, the number of functions added is specified by the parameter.
     * 
     * @param limit the size of the set of functions added to the current generation
     */
    protected void addNewToGeneration(int limit) {
        GenetixFunction.Tuple newGeneration = getCurrentGenerationAsTuple();
        newGeneration.addAll( generateFunctions(limit) );
        setCurrentGeneration(newGeneration);
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
