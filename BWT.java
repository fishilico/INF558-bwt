import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Burrows-Wheeler compressor
 */
public class BWT extends StreamBlockAlgorithm {
    @Override
    public void transformBlock(final byte[] data, int size, OutputStream out)
            throws IOException {
        assert (data != null && size > 0);

        final int[] V = getSortedCyclicShift(data, size);
        assert (V.length == size);

        // Compute I
        int I = -1;
        for (int j = 0; j < V.length; j++) {
            if (V[j] == 0) {
                I = j;
                break;
            }
        }
        assert (I >= 0 && I < V.length);

        // Write I in the same number of bytes as size, Little Endianess
        for (int sz = size; sz > 0; sz >>= 8) {
            out.write(I & 0xff);
            I >>= 8;
        }

        // Write L
        for (int j = 0; j < V.length; j++) {
            out.write(data[(V[j] + size - 1) % size]);
        }
    }

    /**
     * Sort cyclic shiftes versions of data
     * 
     * @param data
     * @param size
     * @return indexes in data
     */
    private int[] getSortedCyclicShift(final byte[] data, final int size) {
        assert (data != null && size > 0);

        // Cyclic shifted strings comparator
        final Comparator<Integer> suffixCmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int i1 = o1.intValue();
                int i2 = o2.intValue();
                // Compare at most size bytes, in a cyclic way
                for (int j = 0; j < size; j++) {
                    // Unsigned comparaison
                    final int d1 = (data[i1] >= 0 ? data[i1] : 256 + data[i1]);
                    final int d2 = (data[i1] >= 0 ? data[i2] : 256 + data[i2]);
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

        // Transform indexes in an int[] array
        int[] intIndexes = new int[size];
        int i = 0;
        for (int idx : indexes) {
            intIndexes[i] = idx;
            i++;
        }
        return intIndexes;
    }

    /**
     * Compress data (main function)
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new BWT().doTransform(args, 512);
    }
}
