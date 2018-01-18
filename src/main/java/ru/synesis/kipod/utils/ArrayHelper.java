package ru.synesis.kipod.utils;

/* Description: ru.synesis.utils/ArrayHelper
 *
 * Copyright (c) 2017 Synesis LLC.
 * Author Sergey Dobrodey <sergey.dobrodey@synesis.ru>, Synesis LLC www.synesis.ru.
 */

public class ArrayHelper {
    public static int[][] chunkIntArray(int[] array, int numOfChunks) {
        assert (numOfChunks != 0);
        int[][] output = new int[numOfChunks][];
        int offset = 0;
        int i = 0;
        while (offset < array.length) {
            int chunkSize = (int) Math.ceil((double) (array.length - offset) / numOfChunks);
            int[] temp = new int[chunkSize];
            System.arraycopy(array, offset, temp, 0, chunkSize);
            output[i] = temp;
            i++;
            numOfChunks--;
            offset += chunkSize;
        }
        return output;
    }
}
