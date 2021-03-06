import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

public class HuffmanTree {
    public char value;
    public int weight;
    public HuffmanTree left, right;
    private boolean[][] encodingTable;

    private HuffmanTree(char ch, int wght) {
        weight = wght;
        value = ch;
    }

    private HuffmanTree(HuffmanTree l, HuffmanTree r) {
        value = 0;
        left = l;
        right = r;
        weight = l.weight + r.weight;
        value = l.value;
    }

    private void buildEncodingTable() {
        if (left == null)
            return;
        boolean[] prefix = new boolean[256];
        prefix[255] = false;
        left.buildEncodingTable(encodingTable, prefix, 1);
        prefix[255] = true;
        right.buildEncodingTable(encodingTable, prefix, 1);
    }

    private void buildEncodingTable(boolean[][] table, boolean[] prefix,
            int prefixLength) {
        if (left == null) {
            table[value] = new boolean[prefixLength];
            for (int i = 0; i < prefixLength; i++)
                table[value][i] = prefix[255 - i];
            return;
        }
        prefix[255 - prefixLength] = false;
        left.buildEncodingTable(table, prefix, prefixLength + 1);
        prefix[255 - prefixLength] = true;
        right.buildEncodingTable(table, prefix, prefixLength + 1);
    }

    public static HuffmanTree buildFromOccurenceMap(int[] occurMap) {
        if (occurMap.length < 256)
            return null;

        Comparator<HuffmanTree> comparator = new HuffmanTreeComparator();
        PriorityQueue<HuffmanTree> q = new PriorityQueue<HuffmanTree>(256,
                comparator);

        for (int i = 0; i < 256; i++)
            q.add(new HuffmanTree((char) i, occurMap[i]));

        for (int i = 0; i < 255; i++) {
            HuffmanTree l = q.poll();
            HuffmanTree r = q.poll();
            q.add(new HuffmanTree(l, r));
        }

        HuffmanTree result = q.poll();

        result.encodingTable = new boolean[256][];
        result.buildEncodingTable();

        return result;
    }

    public void encode(final char[] cArray, OutputStream out)
            throws IOException {
        int offset = 128;
        byte nextbyte = 0;

        for (char c : cArray) {
            for (boolean b : encodingTable[c]) {
                if (b)
                    nextbyte += offset;
                offset >>= 1;

                if (offset <= 0) {
                    out.write(nextbyte);
                    offset = 128;
                    nextbyte = 0;
                }
            }
        }
        if (nextbyte != 0) {
            out.write(nextbyte);
        }
    }

    public void decode(final char[] cArray, int offset, int size,
            OutputStream out) throws IOException {
        int byteOffset = 128;
        HuffmanTree position = this;
        for (int i = offset; size > 0; i++) {
            final char c = (i < cArray.length) ? cArray[i] : 0;
            for (int j = 0; size > 0 && j < 8; j++) {
                position = ((byteOffset & c) > 0) ? position.right
                        : position.left;

                // Write byte if we're on a leaf
                if (position.left == null && position.right == null) {
                    out.write(position.value);
                    size--;
                    position = this;
                }
                byteOffset >>= 1;
            }
            byteOffset = 128;
        }
    }
}
