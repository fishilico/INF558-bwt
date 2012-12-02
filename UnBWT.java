import java.io.IOException;
import java.io.OutputStream;

/**
 * Burrows-Wheeler decompressor
 */
public class UnBWT extends StreamBlockAlgorithm {

    /**
     * Uncompress data[offset..size-1]
     * 
     * @param origIdx
     *            starting index
     * @param data
     * @param size
     * @param offset
     * @return byte[] array of uncompressed data
     */
    public byte[] uncompress(final int origIdx, final byte[] data,
            final int size, final int offset) {
        assert (data != null && origIdx >= 0 && origIdx < size && offset >= 0 && offset < size);
        // Compute uncompressed size
        int unsize = size - offset;
        int[] charCounter = new int[256];
        int[] previousCount = new int[unsize];

        // Count occurences of each caracters in previous positions
        for (int i = 0; i < unsize; i++) {
            final int counter = charCounter[data[offset + i] & 0xff];
            previousCount[i] = counter;
            charCounter[data[offset + i] & 0xff] = counter + 1;
        }

        // Cumulative, in order to have
        // charCounter[c] = nb. of occurences of c' < c
        for (int c = 0, sum = 0; c < 256; c++) {
            final int s = sum;
            sum += charCounter[c];
            charCounter[c] = s;
        }

        // Uncompress
        byte[] undata = new byte[unsize];
        for (int j = unsize - 1, i = origIdx; j >= 0; j--) {
            final byte c = data[offset + i];
            undata[j] = c;
            i = previousCount[i] + charCounter[c & 0xff];
        }
        return undata;
    }

    @Override
    public void transformBlock(byte[] data, int size, OutputStream out)
            throws IOException {
        // Read origin index
        int origIdx = 0;
        int j = 0;
        for (int sz = size; sz > 0; sz >>= 8, j++) {
            origIdx |= (((int) data[j]) & 0xff) << (8 * j);
        }
        // Get and write uncompressed data
        out.write(uncompress(origIdx, data, size, j));
    }

    /**
     * Uncompress data (main function)
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
        // an index in 256 bytes is encoded in 1 byte
        new UnBWT().doTransform(args, 256 + 1);
    }

}
