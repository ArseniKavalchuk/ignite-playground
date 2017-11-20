package ru.synesis.kipod.ignite;

import java.util.Random;

public final class NumberGen {

    private NumberGen() {
    }

    private static final Random RND = new Random();
    private static final int[] N = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private static final char[] L = { 'A', 'B', 'C', 'E', 'H', 'I', 'K', 'M', 'O', 'P', 'T', 'X' };
    private static final int[] R = { 1, 2, 3, 4, 5, 6, 7 };

    public static String generateNumber() {
        StringBuilder res = new StringBuilder()
            .append(N[RND.nextInt(10)])
            .append(N[RND.nextInt(10)])
            .append(N[RND.nextInt(10)])
            .append(N[RND.nextInt(10)])
            .append(L[RND.nextInt(12)])
            .append(L[RND.nextInt(12)])
            .append(R[RND.nextInt(7)]);
        return res.toString();
    }
    
    public static void main(String... args) {
        System.out.println(generateNumber());
    }

}
