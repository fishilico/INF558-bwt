import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnHuffman {
    private void uncompress(char[] data, OutputStream out) throws IOException {
        int[] occur = new int[256];
        for (int i = 0; i < 256; i++) {
            occur[i] = data[4 * i + 3] + 256 * data[4 * i + 2] + 65536
                    * data[4 * i + 1] + 16777216 * data[4 * i];
        }

        int size = data[4 * 256 + 3] + 256 * data[4 * 256 + 2] + 65536
                * data[4 * 256 + 1] + 16777216 * data[4 * 256];

        HuffmanTree t = HuffmanTree.buildFromOccurenceMap(occur);

        t.decode(data, 1028, size, out);
        out.flush();
    }

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

        new UnHuffman().uncompress(cawIn.toCharArray(), out);
    }
}
