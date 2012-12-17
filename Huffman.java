import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Huffman {
    /**
     * Send a Huffman encoded version of input on OutputStream out
     * 
     * @param input
     * @param out
     * @throws IOException
     */
    private void compress(final char[] input, final OutputStream out)
            throws IOException {
        int[] occur = new int[256];
        for (char c : input) {
            occur[c] += 1;
        }

        HuffmanTree t = HuffmanTree.buildFromOccurenceMap(occur);

        for (int i = 0; i < 256; i++) {
            out.write((byte) (occur[i] >> 24));
            out.write((byte) (occur[i] >> 16));
            out.write((byte) (occur[i] >> 8));
            out.write((byte) occur[i]);
        }

        out.write((byte) (input.length >> 24));
        out.write((byte) (input.length >> 16));
        out.write((byte) (input.length >> 8));
        out.write((byte) input.length);

        t.encode(input, out);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        InputStream in = (args.length >= 1 ? new FileInputStream(args[0])
                : System.in);
        OutputStream out = (args.length >= 2 ? new FileOutputStream(args[1])
                : System.out);

        CharArrayWriter cawIn = new CharArrayWriter();
        int nextByte = 0;
        while ((nextByte = in.read()) != -1) {
            cawIn.write(nextByte);
        }
        in.close();

        new Huffman().compress(cawIn.toCharArray(), out);
    }

}
