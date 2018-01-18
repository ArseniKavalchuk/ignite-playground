package ru.synesis.kipod.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Base64;

/**
 * Helper functions on descriptors. Will be useful to implement custom SQL
 * function
 *
 * <pre>
 * select * from face_event where distance(face1, face2) < X
 * </pre>
 *
 */
public class DescriptorHelper {
    public static final int SIZE_512 = 512;
    public static final int SIZE_256 = 256;

    public static int getDefaultSize() {
        return SIZE_512;
    }

    public static float[] fromByteArray(byte[] encoded) {
        ShortBuffer sBuf = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] shortArray = new short[sBuf.limit()];
        sBuf.get(shortArray);
        float[] floatArray = new float[shortArray.length];
        for (int i = 0; i < shortArray.length; i++) {
            floatArray[i] = MathHelper.toFloat(shortArray[i]);
        }
        return floatArray;
    }

    public static float[] fromEncodedString(String encoded) {
        byte[] buffer = Base64.getDecoder().decode(encoded);
        return fromByteArray(buffer);
    }

    public static float distance(float[] one, float[] two) {
        float distance = 0;
        if (one == null || two == null) return distance;
        if (one != null && two != null && one.length != two.length) {
            throw new RuntimeException("Incompatible descriptors: one.length=" + one.length + ", two.length=" + two.length);
        }
        float t;
        for (int i = 0; i < one.length; i++) {
            t = two[i] - one[i];
            distance += t * t;
        }
        return distance;
    }
}
