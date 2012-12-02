import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Burrows-Wheeler compressor
 */
public class BWT extends StreamBlockAlgorithm {
    /**
     * Compress size bytes of data
     * 
     * @param data
     * @param size
     * @return tuple (index of original, Byte[] compressed data)
     */
    public IntArrayTuple<Byte> compress(final byte[] data, int size) {
        assert (data != null && size > 0);

        final Integer[] indexes = getSortedCyclicShift(data, size);
        assert (indexes.length == size);

        // Compute index of the original string (I in BW article)
        IntArrayTuple<Byte> result = new IntArrayTuple<Byte>(-1, new Byte[size]);
        for (int j = 0; j < indexes.length; j++) {
            if (indexes[j] == 0) {
                result.integer = j;
                break;
            }
        }
        assert (result.integer >= 0 && result.integer < indexes.length);
        for (int j = 0; j < indexes.length; j++) {
            result.array[j] = new Byte(data[(indexes[j] + size - 1) % size]);
        }
        return result;
    }

    @Override
    public void transformBlock(final byte[] data, int size, OutputStream out)
            throws IOException {
        assert (data != null && size > 0);

        // Get compressed data
        IntArrayTuple<Byte> intArray = compress(data, size);

        // Write I in the same number of bytes as size, Little Endianess
        for (int i = intArray.integer, sz = size; sz > 0; sz >>= 8, i >>= 8) {
            out.write(i & 0xff);
        }

        // Write data (L)
        for (byte b : intArray.array) {
            out.write(b);
        }
    }

    /**
     * Sort cyclic shiftes versions of data
     * 
     * @param data
     * @param size
     * @return indexes in data
     */
    private Integer[] getSortedCyclicShift(final byte[] data, final int size) {
        assert (data != null && size > 0);

        // Cyclic shifted strings comparator
        final Comparator<Integer> suffixCmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int i1 = o1.intValue();
                int i2 = o2.intValue();
                // Compare at most size bytes, in a cyclic way
                for (int j = 0; j < size; j++) {
                    // Unsigned byte sorting
                    final int d1 = ((int) data[i1]) & 0xff;
                    final int d2 = ((int) data[i2]) & 0xff;
                    final int c = Integer.compare(d1, d2);
                    if (c != 0) {
                        return c;
                    }
                    i1 = (i1 + 1) % size;
                    i2 = (i2 + 1) % size;
                }
                return 0;
            }
        };

        // Build a TreeSet for shift indexes
        TreeSet<Integer> indexes = new TreeSet<Integer>(suffixCmp);
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        return indexes.toArray(new Integer[size]);
    }

    /**
     * Compress data (main function)
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new BWT().doTransform(args, 256);
    }
}
