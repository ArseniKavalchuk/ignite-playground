package ru.synesis.kipod.utils;

/* Description: ru.synesis.utils/MathHelper
 *
 * Copyright (c) 2017 Synesis LLC.
 * Author Sergey Dobrodey <sergey.dobrodey@synesis.ru>, Synesis LLC www.synesis.ru.
 */

public class MathHelper {

    private static final float A_KOEFF = 4.802f;
    private static final float C_KOEFF = 0.725f;
    private static final float MAX_DISTANCE_SIGMOID = 1.5f;
    private static final float MIN_DISTANCE_SIGMOID = 0.0f;

    public static float similarityToDistance(float s) {
        if (s >= distanceToSimilarity(MIN_DISTANCE_SIGMOID)) {
            return MIN_DISTANCE_SIGMOID;
        }
        if (s <= distanceToSimilarity(MAX_DISTANCE_SIGMOID)) {
            return MAX_DISTANCE_SIGMOID;
        }
        return (float) (C_KOEFF - Math.log(1.0 / (1.0 - s) - 1.0) / A_KOEFF);
    }

    public static float distanceToSimilarity(float d) {
        if (d <= MIN_DISTANCE_SIGMOID) {
            return 1.f;
        }
        return (float) (1.0 - 1.0 / (1.0 + Math.exp(-A_KOEFF * (d - C_KOEFF))));
    }

    public static short toHalfFloat(final float v) {
        if (Float.isNaN(v))
            throw new UnsupportedOperationException("NaN to half conversion not supported!");
        if (v == Float.POSITIVE_INFINITY)
            return (short) 0x7c00;
        if (v == Float.NEGATIVE_INFINITY)
            return (short) 0xfc00;
        if (v == 0.0f)
            return (short) 0x0000;
        if (v == -0.0f)
            return (short) 0x8000;
        if (v > 65504.0f)
            return 0x7bff; // max value supported by half float
        if (v < -65504.0f)
            return (short) (0x7bff | 0x8000);
        if (v > 0.0f && v < 5.96046E-8f)
            return 0x0001;
        if (v < 0.0f && v > -5.96046E-8f)
            return (short) 0x8001;

        final int f = Float.floatToIntBits(v);

        return (short) (((f >> 16) & 0x8000) | ((((f & 0x7f800000) - 0x38000000) >> 13) & 0x7c00) | ((f >> 13) & 0x03ff));
    }

    public static float toFloat(final short half) {
        switch ((int) half) {
        case 0x0000:
            return 0.0f;
        case 0x8000:
            return -0.0f;
        case 0x7c00:
            return Float.POSITIVE_INFINITY;
        case 0xfc00:
            return Float.NEGATIVE_INFINITY;
        // @TODO: support for NaN ?
        default:
            return Float.intBitsToFloat(((half & 0x8000) << 16) | (((half & 0x7c00) + 0x1C000) << 13) | ((half & 0x03FF) << 13));
        }
    }

}
