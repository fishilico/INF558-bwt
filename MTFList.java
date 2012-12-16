/**
 * Implementation of lists of byte that allow move-to-front operation
 * and access to an element by its position in the list
 */
public class MTFList {
    public MTFList next;
    public MTFList prev;
    public byte content;

    private MTFList toMove;
    
    private MTFList(int i) {
        next = null;
        prev = null;
        content = (byte)i;
    }

    private MTFList(int i, MTFList previous) {
        content = (byte)i;
        next = null;
        prev = previous;
        toMove = null;
        if(i > 0) next = new MTFList(i - 1, this);
    }

    /**
     * Build a MTFList that contain every integer between 0 and i
     * in reverse order
     * 
     * @return MTFList the built list
     */
    static MTFList buildMTFList(int i) {
        return new MTFList(i - 1, new MTFList(i));
    }

    /**
     * Build and return a new version of the list whith the last gotten item at the front
     * 
     * @return MTFList the new list
     */
    public MTFList moveToFront() {
    	if(toMove == null) return this;
        
        if(toMove.prev == null) return toMove;
        toMove.prev.next = toMove.next;
        toMove.next = this;
        this.prev = toMove;
        return toMove;
    }

    /**
     * Get the position in the list of the byte b
     * Update toMove
     * 
     * @param b
     * @return byte position (0 if b was not found)
     */
    public byte get(byte b) {
    	return get(b, (byte)0, this);
    }
    
    /**
     * Recursive function for get
     * 
     * @param b
     * @param acc (current position in the list)
     * @param root (the MTFList on which get was initially called)
     * @return byte position (0 if b was not found)
     */
    private byte get(byte b, byte acc, MTFList root) {
    	if(content == b) {
        	root.toMove = this;
        	return acc;
        }
        if(next == null) return 0;
        return next.get(b, (byte)(acc + 1), root);
    }
    
    /**
     * Get the byte at that is at position b
     * Update toMove
     * 
     * @param b
     * @return byte
     */
    public byte getByteAt(byte b) {
    	return getByteAt(b, this);
    }
    
    /**
     * Recursive function for getByteAt
     * 
     * @param b
     * @param root (the MTFList on which get was initially called)
     * @return byte (byte that was at position b, 0 if b is too large)
     */
    private byte getByteAt(byte b, MTFList root) {
    	if(b == 0) {
    		root.toMove = this;
    		return content;
    	}
    	if(next == null) return 0;
    	return next.getByteAt((byte)(b - 1), root);
    }
}
