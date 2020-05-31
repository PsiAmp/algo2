package practice;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MinPriorityQueue<T extends Comparable<T>> {

    private static final int MIN_CAPACITY = 10;

    private int size = 0;
    private T[] values;
    private Comparator<T> comp;

    public MinPriorityQueue() {
        this(MIN_CAPACITY);
    }

    public MinPriorityQueue(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity can't be less than 1");
        values = (T[]) new Comparable[capacity + 1];
    }

    public MinPriorityQueue(int size, Comparator<T> comp) {
        this.size = size;
        this.comp = comp;
    }

    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException("Argument can't be null");

        if (size == values.length - 1)
            resize(values.length * 2);

        values[++size] = value;
        moveUp(size);
    }

    public T removeMax() {
        if (isEmpty()) return null;

        T max = values[1];
        values[1] = values[size--];
        values[size + 1] = null;
        moveDown(1);
        if (size * 4 < values.length - 1 && size > MIN_CAPACITY)
            resize(values.length / 2);
        return max;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize(int size) {
        values = Arrays.copyOf(values, size);
    }

    private void moveUp(int i) {
        while (i > 1 && isGreater(i / 2, i)) {
            swap(i, i / 2);
            i = i / 2;
        }
    }

    private void moveDown(int i) {
        int child = i * 2;
        while (child <= size) {
            if (child < size && isGreater(child, child + 1)) child++;
            if (isGreater(child, i)) break;
            swap(i, child);
            i = child;
            child = i * 2;
        }
    }

    private void swap(int i, int j) {
        T t = values[i];
        values[i] = values[j];
        values[j] = t;
    }

    private boolean isGreater(int i, int j) {
        if (comp != null)
            return comp.compare(values[i], values[j]) > 0;
        return values[i].compareTo(values[j]) > 0;
    }

    public static void main(String[] args) {
        String[] a = {"b", "a", "c", "e", "g", "f"};

        Integer[] aa = new Integer[1000];
        for (int i = 0; i < aa.length; i++) {
            aa[i] = i;
        }
        Collections.shuffle(Arrays.asList(aa));


        MinPriorityQueue<Integer> mpq = new MinPriorityQueue<>(aa.length);
        long timeMillis = System.currentTimeMillis();
        for (Integer integer : aa) {
            mpq.insert(integer);
        }
        System.out.println(System.currentTimeMillis() - timeMillis);


        while (!mpq.isEmpty()) {
            System.out.println(mpq.removeMax());
        }

    }
}
