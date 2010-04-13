
package frec.core;

import frec.util.RandomHelper;

/**
 *
 * 
 */
public class GYModelGenetix extends GPModelGenetix
{
    
    public GYModelGenetix() {
        super();
    }

    protected void computeInit() {
        final int generationSize = getGenerationSize();
        int initSize = 4;
        int selectSize = 0;
        int size = generationSize / 10;
        if (size == 0) size = 1;
        int counter = 0;

        GenetixFunction.Tuple initGeneration = new GenetixFunction.Tuple();
        // advanced initialization:
        while ( initGeneration.size() < generationSize ) {
            if (RandomHelper.randomBoolean()) {
                initializeGeneration(); // GP ~ Genetix
            } else {
                initializeGeneration(initSize + (selectSize % 3)); // Genetix
            }
            selectSize = 1 + RandomHelper.randomInt(10);

            checkFitnessErrors();
            GenetixFunction[] currentGeneration = getCurrentGeneration();
            if ( currentGeneration.length < size && counter++ < 100 ) continue;
            counter = 0;
            selectBest(selectSize);

            initGeneration.addAll(currentGeneration);
        }

        while ((size = initGeneration.size()) > generationSize) {
            initGeneration.remove(RandomHelper.randomInt(size));
        }
        setCurrentGeneration( initGeneration.snapshot() );
    }

    protected void computeNext() {
        final int generationSize = getGenerationSize();
        int selectSize = Math.round(generationSize * getSelectionProbability());
        selectBest(selectSize);
        GenetixFunction.setFunctionCodeLengthLimits(++currentCodeMin, currentCodeMax *= 2);
        mutateGeneration(); //GP
        reproductGeneration(); //GP
        crossGeneration(); //GP
        GenetixFunction.setFunctionCodeLengthLimits(--currentCodeMin, currentCodeMax /= 2);
        checkFitnessErrors();
        checkPopulationErrors();
        while ( getCurrentGeneration().length < generationSize ) {
            addNewToGeneration(generationSize - getCurrentGeneration().length);
            checkFitnessErrors();
        }
        selectBest(generationSize);
    }

    protected void crossGeneration() {
        final GenetixFunction[] currentGeneration = getCurrentGeneration();
        final int len = currentGeneration.length;
        final float prob = getCrossingProbability();

        GenetixFunction.Tuple validCrosses = getCurrentGenerationAsTuple();
        
        for (int i=0; i<len; i++) {
            if ( RandomHelper.randomBoolean(prob * (1 - i / len)) )
            {
                int rnd = RandomHelper.ascRandomInt(len);
                while (rnd == i) rnd = RandomHelper.ascRandomInt(len);
                
                GenetixFunction fx1 = currentGeneration[i], fx2 = currentGeneration[rnd];
                /*
                final int new_len = 2 * ( fx1.length()-1 ) * ( fx2.length()-1 );
                GenetixFunction[] crossed = new GenetixFunction[new_len];
                int j = 0;
                for (int pos1=1; pos1<fx1.length(); pos1++)
                {
                    for (int pos2=1; pos2<fx2.length(); pos2++)
                    {
                        GenetixFunction new1 = (GenetixFunction) fx1.clone();
                        GenetixFunction new2 = (GenetixFunction) fx2.clone();
                        new1.crossFunctions(new2, pos1, pos2);
                        crossed[j++] = new1; crossed[j++] = new2;
                    }
                }
                */
                int allCrosings = 2 * ( fx1.length() - 1 ) * ( fx2.length() - 1 );
                while ( allCrosings-- > 0 ) {
                    int pos1 = 1 + RandomHelper.randomInt(fx1.length() - 1);
                    int pos2 = 1 + RandomHelper.randomInt(fx2.length() - 1);
                    GenetixFunction new1 = (GenetixFunction) fx1.clone();
                    GenetixFunction new2 = (GenetixFunction) fx2.clone();
                    new1.crossFunctions(new2, pos1, pos2);
                    GenetixFunction[] crossed = new GenetixFunction[] {new1, new2};
                    if ( addValidCrossedFunctions(validCrosses, crossed) ) break;
                }
            }
        }        
        setCurrentGeneration( validCrosses.snapshot() );
    } 
    
    private boolean addValidCrossedFunctions(
            final GenetixFunction.Tuple to, 
            final GenetixFunction[] crossed) {
        computeFitness(crossed);
        final int minLen = GenetixFunction.getFunctionCodeMinLength();
        final int maxLen = GenetixFunction.getFunctionCodeMaxLength();
        boolean added = false;
        for (int i=0; i<crossed.length; i++) {
            boolean validLen = isArbitraryCrossings();
            if ( ! validLen ) {
                int len = crossed[i].length();
                validLen = len >= minLen && len <= maxLen;
            }
            if ( validLen && crossed[i].isFitnessValid() ) {
                added = true;
                to.add(crossed[i]);
            }
        }
        return added;
    }
    
}
