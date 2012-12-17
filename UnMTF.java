import java.io.IOException;
import java.io.OutputStream;

public class UnMTF extends StreamBlockAlgorithm {

    public byte[] uncompress(final byte[] data, final int size) {
        assert (data != null);

        // Uncompress
        byte[] undata = new byte[size];
        MTFList charMap = MTFList.buildMTFList(255);
        for (int i = 0; i < size; i++) {
            if (i <= 1) {
                undata[i] = data[i];
            } else {
                undata[i] = charMap.getByteAt(data[i]);
                charMap = charMap.moveToFront();
            }
        }
        return undata;
    }

    @Override
    public void transformBlock(final byte[] data, final int size,
            OutputStream out) throws IOException {
        assert (data != null && size <= data.length);

        // Get and write uncompressed data
        out.write(uncompress(data, size));
    }

    /**
     * Uncompress data (main function)
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
        // index in 2 bytes-long
        new UnMTF().doTransform(args, 0x1002);
    }

}
