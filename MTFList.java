public class MTFList {
    public MTFList next;
    public MTFList prev;
    public byte content;

    private MTFList toMove;
    
    MTFList(int i) {
        next = null;
        prev = null;
        content = (byte)i;
    }

    MTFList(int i, MTFList previous) {
        content = (byte)i;
        next = null;
        prev = previous;
        toMove = null;
        if(i > 0) next = new MTFList(i - 1, this);
    }

    static MTFList buildMTFList(int i) {
        return new MTFList(i - 1, new MTFList(i));
    }

    MTFList moveToFront() {
        if(toMove.prev == null) return toMove;
        toMove.prev.next = toMove.next;
        toMove.next = this;
        this.prev = toMove;
        return toMove;
    }

    byte get(byte b) {
    	return get(b, (byte)0, this);
    }
    
    byte get(byte b, byte acc, MTFList root) {
    	if(content == b) {
        	root.toMove = this;
        	return acc;
        }
        if(next == null) return -1;
        return next.get(b, (byte)(acc + 1), root);
    }
}
