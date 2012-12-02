import java.io.IOException;
import java.io.OutputStream;

/**
 * Burrows-Wheeler compressor
 */
public class BWT extends StreamBlockAlgorithm {

    public void transformBlock(final byte[] data, int size, OutputStream out)
            throws IOException {
        out.write(data);
    }

    public static void main(String[] args) throws IOException {
        new BWT().doTransform(args, 512);
    }
}
