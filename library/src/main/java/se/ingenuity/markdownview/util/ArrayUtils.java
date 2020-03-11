package se.ingenuity.markdownview.util;

import androidx.annotation.NonNull;

import se.ingenuity.markdownview.function.IntPredicate;

class ArrayUtils {
    private ArrayUtils() {
    }

    static boolean any(@NonNull int[] array, @NonNull IntPredicate predicate) {
        for (int item : array) {
            if (predicate.test(item)) {
                return true;
            }
        }

        return false;
    }

    static void reverse(@NonNull Object[] array) {
        final int half = array.length / 2;
        for (int index = 0; index < half; index++) {
            final Object temp = array[index];
            array[index] = array[array.length - 1 - index];
            array[array.length - 1 - index] = temp;
        }
    }
}
