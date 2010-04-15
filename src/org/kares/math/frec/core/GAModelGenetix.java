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

/**
 * Implements the "standard" genetic algorithm (GA) computing scheme.
 *
 * @see #computeInit()
 * @see #computeNext()
 * @author kares
 */
public class GAModelGenetix extends Genetix {
    
    /**
     */
    public GAModelGenetix() {
        super();
    }

    /**
     * Initializes and validates the generated functions
     * until there's enough valid ones to form the initial
     * generation.
     * 
     * @see org.kares.math.frec.core.Genetix#computeInit()
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

    /**
     * A single iteration is done as follows :
     * <ol>
     * 	<li>
     * 	mutate instances in the current generation
     *  </li>
     *  <li>
     *  cross the best functions available (parents do not usually
     *  survive it to the next generation - only their children)
     *  </li>
     *  <li>
     *  select the best functions and add some random instances
     *  to keep variability
     *  </li>
     * </ol>
     * 
     * @see org.kares.math.frec.core.Genetix#computeNext()
     */
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

    /**
     * Crosses the functions of the actual generation among each other.
     */
    /*
    protected void crossGeneration() {
        int len = currentGeneration.length;
        int new_len = 2 * len;
        GenetixFunction[] newGeneration = new GenetixFunction[new_len];
        int i = 0;
        while ( i < new_len ) {
            //if ( RandomHelper.randomBoolean(crossingProbability) ) {
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
    */
    
}
