import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Code factorisation for every algorithm which handles data in a
 * block-streaming fashion
 */
public abstract class StreamBlockAlgorithm {
    /**
     * Transform a block of data
     * 
     * @param data
     * @param size
     * @param out
     * @throws IOException
     */
    abstract public void transformBlock(final byte[] data, int size,
            OutputStream out) throws IOException;

    /**
     * Transform a stream of data
     * 
     * @param in
     * @param out
     * @param block_size
     * @throws IOException
     */
    public void transformStream(final InputStream in, OutputStream out,
            int block_size) throws IOException {
        byte[] data = new byte[block_size];
        int offset = 0;
        int numread = 0;

        // Read block of datas
        while (numread >= 0) {
            numread = in.read(data, offset, block_size - offset);
            if (numread < 0) {
                // End of stream, transform a block
                if (offset > 0)
                    transformBlock(data, offset, out);
            } else {
                offset += numread;
                assert (offset <= block_size);
                if (offset == block_size) {
                    // Transform a block
                    transformBlock(data, offset, out);
                    offset = 0;
                }
            }
        }
    }

    /**
     * Do the transformation according to the parameters
     * 
     * @param args
     * @throws IOException
     */
    public void doTransform(String[] args, int block_size) throws IOException {
        InputStream in = (args.length >= 1 ? new FileInputStream(args[0])
                : System.in);
        OutputStream out = (args.length >= 2 ? new FileOutputStream(args[1])
                : System.out);
        transformStream(in, out, block_size);
    }
}
