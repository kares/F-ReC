package frec.util;

/**
 * Class <code> Generator </code> implements a pseudo-random number generator.
 * The Mersenne Twister generator's java implementation is used optimized for
 * enhanced performance. This generator is fast and provides very good results
 * in generating random data.
 */

public final class Generator {
    // Period parameters
    private static final int N = 624;

    private static final int M = 397;

    private static final int MATRIX_A = 0x9908b0df; // private static final *
                                                    // constant vector a

    private static final int UPPER_MASK = 0x80000000; // most significant w-r
                                                        // bits

    private static final int LOWER_MASK = 0x7fffffff; // least significant r
                                                        // bits

    // Tempering parameters
    private static final int TEMPERING_MASK_B = 0x9d2c5680;

    private static final int TEMPERING_MASK_C = 0xefc60000;

    private static int mt[]; // the array for the state vector

    private static int mti; // mti==N+1 means mt[N] is not initialized

    private static int mag01[];

    // a good initial seed (of int size, though stored in a long)
    // private static final long GOOD_SEED = 4357;

    /**
     * Initializes the generator using the default seed (current time in
     * milliseconds is used).
     */
    public static void init() {
        setSeed(System.currentTimeMillis());
    }

    /**
     * Initializes the generator using a given seed. It's best to make sure that
     * the seed is actually an (unsigned) 32-bit integer. Mersenne Twister only
     * uses the first 32 bits for its seed.
     */
    public static void init(final long seed) {
        setSeed(seed);
    }

    /**
     * Initializes the generator using an array seed.
     */
    public static void init(final int[] array) {
        setSeed(array);
    }

    private static boolean initialized = false;

    public static boolean isInitialized() {
        return initialized;
    }

    private static synchronized void setSeed(final long seed) {
        mt = new int[N];
        mag01 = new int[2];
        mag01[0] = 0x0;
        mag01[1] = MATRIX_A;
        mt[0] = (int) (seed & 0xfffffff);
        for (mti = 1; mti < N; mti++) {
            mt[mti] = (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
            mt[mti] &= 0xffffffff;
        }
        initialized = true;
    }

    private static synchronized void setSeed(final int[] array) {
        int i, j, k;
        setSeed(19650218);
        i = 1;
        j = 0;
        k = (N > array.length ? N : array.length);
        for (; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1664525))
                    + array[j] + j; /* non linear */
            mt[i] &= 0xffffffff; /* for WORDSIZE > 32 machines */
            i++;
            j++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
            if (j >= array.length) j = 0;
        }
        for (k = N - 1; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1566083941))
                    - i; /* non linear */
            mt[i] &= 0xffffffff; /* for WORDSIZE > 32 machines */
            i++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
        }
        mt[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */
        initialized = true;
    }

    /**
     * Generates a pseudo-random boolean.
     */

    public static final boolean randomBoolean() {
        int y;
        if (mti >= N) // generate N words at one time
        {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

        return (boolean) ((y >>> 31) != 0);
    }

    /**
     * Generates a pseudo-random boolean (generates a coin flip with a
     * probability of returning true). The result is true with the provided
     * probability.
     */

    public static final boolean randomBoolean(final float probability) {
        int y;

        if (probability < 0.0f || probability > 1.0f)
            throw new IllegalArgumentException(
                    "probability must be between 0.0 and 1.0 inclusive.");
        if (probability == 0.0f)
            return false; // fix half-open issues
        else
            if (probability == 1.0f) return true; // fix half-open issues
        if (mti >= N) // generate N words at one time
        {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

        return (y >>> 8) / ((float) (1 << 24)) < probability;
    }

    /**
     * Generates a pseudo-random double from <0,1>, 0.0 and 1.0 are both valid
     * results.
     */

    public static final double randomDouble() {
        int y;
        int z;

        if (mti >= N) // generate N words at one time
        {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

        if (mti >= N) // generate N words at one time
        {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (z >>> 1) ^ mag01[z & 0x1];
            }
            for (; kk < N - 1; kk++) {
                z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (z >>> 1) ^ mag01[z & 0x1];
            }
            z = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (z >>> 1) ^ mag01[z & 0x1];

            mti = 0;
        }

        z = mt[mti++];
        z ^= z >>> 11; // TEMPERING_SHIFT_U(z)
        z ^= (z << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(z)
        z ^= (z << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(z)
        z ^= (z >>> 18); // TEMPERING_SHIFT_L(z)

        return ((((long) (y >>> 6)) << 27) + (z >>> 5)) / (double) (1L << 53);
    }

    /**
     * Generates a pseudo-random float.
     */

    public static final float randomFloat() {
        int y;

        if (mti >= N) // generate N words at one time
        {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

        return (y >>> 8) / ((float) (1 << 24));
    }

    /**
     * Generates a pseudo-random integer from [0,n-1].
     */

    public static final int randomInt(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive");

        if ((n & -n) == n) // i.e., n is a power of 2
        {
            int y;

            if (mti >= N) // generate N words at one time
            {
                int kk;

                for (kk = 0; kk < N - M; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                for (; kk < N - 1; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
                mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

                mti = 0;
            }

            y = mt[mti++];
            y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
            y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
            y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
            y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

            return (int) ((n * (long) (y >>> 1)) >> 31);
        }

        int bits, val;
        do {
            int y;

            if (mti >= N) // generate N words at one time
            {
                int kk;

                for (kk = 0; kk < N - M; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                for (; kk < N - 1; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
                mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

                mti = 0;
            }

            y = mt[mti++];
            y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
            y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
            y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
            y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

            bits = (y >>> 1);
            val = bits % n;
        }
        while (bits - val + (n - 1) < 0);
        return val;
    }

    /**
     * Generates a pseudo-random integer from [0,n-1]. Smaller numbers have
     * higher probability to be generated.
     */

    public static final int ascRandomInt(int n) {
        float prob = 1 / (float) n;
        while (true) {
            int rnd = randomInt(n);
            float rnd_prob = (float) (n - rnd) * prob;
            if (randomBoolean(rnd_prob)) return rnd;
        }
    }

}
