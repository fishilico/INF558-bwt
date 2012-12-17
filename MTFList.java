/**
 * Implementation of lists of byte that allow move-to-front operation and access
 * to an element by its position in the list
 */
public final class MTFList {
    public MTFList next;
    public MTFList prev;
    public byte content;

    private MTFList toMove;

    private MTFList(int i, MTFList p, MTFList n) {
        content = (byte) i;
        next = n;
        prev = p;
        if (next != null)
            next.prev = this;
        if (prev != null)
            prev.next = this;
    }

    /**
     * Build a MTFList that contain every integer between 0 and i in reverse
     * order
     * 
     * @return MTFList the built list
     */
    static public MTFList buildMTFList(int i) {
        MTFList root = new MTFList(i, null, null);
        MTFList l = root;
        for (int j = i - 1; j >= 0; j--) {
            l = new MTFList(j, l, null);
        }
        return root;
    }

    /**
     * Build and return a new version of the list whith the last gotten item at
     * the front
     * 
     * @return MTFList the new list
     */
    public MTFList moveToFront() {
        // Return this if it's the first element
        if (toMove == null)
            return this;
        if (toMove.prev == null)
            return toMove;

        // Relink toMove at first position
        toMove.prev.next = toMove.next;
        if (toMove.next != null)
            toMove.next.prev = toMove.prev;
        toMove.next = this;
        toMove.prev = null;
        this.prev = toMove;
        return toMove;
    }

    /**
     * Get the position in the list of the byte b Update toMove
     * 
     * @param b
     * @return byte position (0 if b was not found)
     */
    public byte get(byte b) {
        byte i = 0;
        for (MTFList l = this; l != null; l = l.next, i++) {
            if (l.content == b) {
                this.toMove = l;
                return i;
            }
        }
        return 0;
    }

    /**
     * Get the byte at that is at position b Update toMove
     * 
     * @param b
     * @return byte
     */
    public byte getByteAt(byte b) {
        MTFList l = this;
        while (l != null && b != 0) {
            l = l.next;
            b--;
        }
        this.toMove = l;
        return l.content;
    }
}
