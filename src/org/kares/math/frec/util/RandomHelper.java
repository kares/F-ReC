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

package org.kares.math.frec.util;

import java.util.Random;

/**
 * Class <code> RandomHelper </code> implements a pseudo-random number generator.
 * The Mersenne Twister generator's java implementation is used optimized for
 * enhanced performance. This generator is fast and provides very good results
 * in generating random data.
 */
public abstract class RandomHelper {

    /**
     * Constructor using the default seed.
     * @return random
     */
    public static synchronized Random newRandom() {
        return new MersenneTwister(shared.nextLong());
    }

    /**
     * Constructor using a given seed.  Though you pass this seed in
     * as a long, it's best to make sure it's actually an integer.
     * @param seed
     * @return random
     */
    public static Random newRandom(final long seed) {
        return new MersenneTwister(seed);
    }

    /**
     * Constructor using an array of integers as seed.
     * Your array must have a non-zero length.  Only the first 624 integers
     * in the array are used; if the array is shorter than this then
     * integers are repeatedly used in a wrap-around fashion.
     * @param seed
     * @return random
     */
    public static Random newRandom(final int[] seed) {
        return new MersenneTwister(seed);
    }

    private static final MersenneTwister shared;
    static {
        long seed = 0x00000000ffffffffL;
        long rnd = 0x0000000021abcdefL;
        seed = seed & (((System.currentTimeMillis() << 1) * seed + rnd ) >> 2 );
        rnd = rnd ^ Double.doubleToLongBits(Math.random());
        seed = ((seed + rnd) >> 1) * (seed >> 1);
        final int[] s = new int[600];
        for (int i=0; i<600; i++) {
            s[i] = (int)((seed ^ System.currentTimeMillis()) + rnd * i)
                    + ((int)(Math.random() * (double)Integer.MAX_VALUE) >> 1);
        }
        shared = new MersenneTwister(s);
    }

    /**
     * Generates a pseudo-random boolean.
     * @return random boolean
     */
    public static synchronized boolean randomBoolean() {
        return shared.nextBoolean();
    }

    /**
     * Generates a pseudo-random boolean (generates a coin flip with a
     * probability of returning true). The result is true with the provided
     * probability.
     * @param probab
     * @return random boolean
     */
    public static synchronized boolean randomBoolean(final float probab) {
        return shared.nextBoolean(probab);
    }

    /**
     * Generates a pseudo-random double from <0,1>, 0.0 and 1.0 are both valid
     * results.
     * @return random double
     */
    public static synchronized double randomDouble() {
        return shared.nextDouble();
    }

    /**
     * Generates a pseudo-random float.
     * @return random float
     */
    public static synchronized float randomFloat() {
        return shared.nextFloat();
    }

    /**
     * Generates a pseudo-random integer from [0,max-1].
     * @param max
     * @return random integer
     */
    public static synchronized int randomInt(int max) {
        return shared.nextInt(max);
    }

    /**
     * Generates a pseudo-random integer from [0,max-1]. Smaller numbers have
     * higher probability to be generated.
     * @param max
     * @return random integer
     */
    public static synchronized int ascRandomInt(int max) {
        float prob = 1 / (float) max;
        while (true) {
            int rnd = randomInt(max);
            float rnd_prob = (float) (max - rnd) * prob;
            if (randomBoolean(rnd_prob)) return rnd;
        }
    }

}
