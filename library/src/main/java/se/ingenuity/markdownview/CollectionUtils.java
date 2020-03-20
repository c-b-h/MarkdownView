package se.ingenuity.markdownview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;

import se.ingenuity.markdownview.function.Predicate;

class CollectionUtils {
    private CollectionUtils() {
    }

    static <T> boolean any(@NonNull Collection<T> collection,
                           @NonNull Predicate<T> predicate) {
        for (final T item : collection) {
            if (predicate.test(item)) {
                return true;
            }
        }

        return false;
    }
}
