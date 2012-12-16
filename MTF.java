import java.io.IOException;
import java.io.OutputStream;

/**
 * Move To Front compressor
 */
public class MTF extends StreamBlockAlgorithm {
    private byte[] compress(final byte[] data, int size) {
        byte[] result = new byte[size];
        MTFList charmap = MTFList.buildMTFList(255);
        MTFList current;

        for(int i = 0; i < size; i ++) {
            result[i] = charmap.get(data[i]);
            charmap = charmap.moveToFront();
        }

        return result;
    }

    public void transformBlock(final byte[] data, int size, OutputStream out) throws IOException {
        assert (data != null && size > 0);

        // Write data (L)
        out.write(compress(data, size));
    }

    public static void main(String[] args) throws IOException {
        new MTF().doTransform(args, 258);
    }    
}
