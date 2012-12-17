import java.util.Comparator;


public class HuffmanTreeComparator implements Comparator<HuffmanTree> {

	@Override
	public int compare(HuffmanTree arg0, HuffmanTree arg1) {
		if(arg0.weight < arg1.weight) return -1;
		else if(arg0.weight > arg1.weight) return 1;
		else if(arg0.left == null && arg1.right == null) {
			if(arg0.value < arg1.value) return -1;
			if(arg0.value > arg1.value) return 1;
		}
		
		return 0;
	}

}
