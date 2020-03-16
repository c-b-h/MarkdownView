package se.ingenuity.markdownview.util;

import androidx.annotation.NonNull;

import java.util.List;

import se.ingenuity.markdownview.function.ToIntFunction;

class ArrayUtils {
    private ArrayUtils() {
    }

    static <T> int[] map(@NonNull List<T> collection,
                         @NonNull ToIntFunction<T> transformation) {
        final int[] result = new int[collection.size()];
        for (int index = 0; index < result.length; index++) {
            result[index] = transformation.applyAsInt(collection.get(index));
        }

        return result;
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
