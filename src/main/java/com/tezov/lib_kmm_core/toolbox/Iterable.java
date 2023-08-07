package com.tezov.lib_kmm_core.toolbox;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Iterable {

    public static <T> java.lang.Iterable<T> from(List<T> original, boolean reverse) {
        if (reverse) {
            return Reversed.from(original);
        } else {
            return original;
        }
    }

    public static class Reversed<T> implements java.lang.Iterable<T> {
        private final ListIterator<T> original;

        private Reversed(List<T> original, Integer index) {
            if (index == null) {
                index = original.size();
            }
            this.original = original.listIterator(index);
        }

        public static <T> Reversed<T> from(List<T> original) {
            return new Reversed<>(original, null);
        }

        public static <T> Reversed<T> from(List<T> original, int index) {
            return new Reversed<>(original, index);
        }

        @Override
        public Iterator<T> iterator() {
            return new IteratorReversed(original);
        }

        public ListIterator<T> listIterator() {
            return new IteratorReversed(original);
        }

        private class IteratorReversed implements ListIterator<T> {
            private final ListIterator<T> iterator;

            public IteratorReversed(ListIterator<T> iterator) {
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasPrevious();
            }

            @Override
            public T next() {
                return iterator.previous();
            }

            @Override
            public boolean hasPrevious() {
                return iterator.hasNext();
            }

            @Override
            public T previous() {
                return iterator.next();
            }

            @Override
            public int nextIndex() {
                return iterator.previousIndex();
            }

            @Override
            public int previousIndex() {
                return iterator.nextIndex();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public void set(T t) {
                iterator.set(t);
            }

            @Override
            public void add(T t) {
                iterator.add(t);
            }

        }

    }

}
