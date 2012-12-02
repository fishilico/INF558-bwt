/**
 * (int, E[]) tuple
 * 
 * @param <E>
 */
public final class IntArrayTuple<E> {
    public int integer;
    public E[] array;

    public IntArrayTuple(int i, E[] a) {
        integer = i;
        array = a;
    }
}
