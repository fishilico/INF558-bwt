#!/bin/sh

die() {
    echo $*
    exit 1
}

# Print hexa diff
hexdiff() {
    FILE1="$1"
    FILE2="$2"
    if ! diff -q "$FILE1" "$FILE2"
    then
        HEX1="${FILE1%.tmp}.hex.tmp"
        HEX2="${FILE2%.tmp}.hex.tmp"
        xxd "$FILE1" > "$HEX1"
        xxd "$FILE2" > "$HEX2"
        diff -y "$HEX1" "$HEX2"
        false
    else
        true
    fi
}

# Print file size
echo_filesize() {
	SIZE=$(ls -l "$1" |cut -d' ' -f5)
	if [ $# -ge 2 ]
	then
		INSIZE=$(ls -l "$2" |cut -d' ' -f5)
		PERCENT=$(($SIZE*100/$INSIZE))
		echo -n "[$SIZE $PERCENT%] "
	else
		echo -n "[$SIZE] "
	fi
}

# Run tests on one input
run_tests() {
    FILEPREFIX="$1"
    INFILE="$2"
    echo -n "Testing $FILEPREFIX..."
    echo_filesize "$INFILE"

    # Buid a set of temporary files
    BWTFILE="$FILEPREFIX.bwt.tmp"
    UNBWTFILE="$FILEPREFIX.unbwt.tmp"
    MTFFILE="$FILEPREFIX.mtf.tmp"
    UNMTFFILE="$FILEPREFIX.unmtf.tmp"
    HUFFFILE="$FILEPREFIX.huff.tmp"
    UNHUFFFILE="$FILEPREFIX.unhuff.tmp"

    # Testing BWT, there may be a .out testing file
    echo -n "BWT..."
    java -ea -cp .. BWT "$INFILE" "$BWTFILE" || die "Java BWT failed with $INFILE"
    echo_filesize "$BWTFILE" "$INFILE"
    OUTFILE="$FILEPREFIX.out"
    if [ -r "$OUTFILE" ]
    then
        diff "$OUTFILE" "$BWTFILE" || die "Bad BWT output for $INFILE"
    fi
    echo -n "UnBWT..."
    java -ea -cp .. UnBWT "$BWTFILE" "$UNBWTFILE" || die "Java UnBWT failed with $BWTFILE"
    hexdiff "$INFILE" "$UNBWTFILE" || die "Bad UnBWT output for $INFILE"

    # Testing MTF
    echo -n "MTF..."
    java -ea -cp .. MTF "$BWTFILE" "$MTFFILE" || die "Java MTF failed with $BWTFILE"
    echo_filesize "$MTFFILE" "$INFILE"
    echo -n "UnMTF..."
    java -ea -cp .. UnMTF "$MTFFILE" "$UNMTFFILE" || die "Java UnMTF failed with $BTFFILE"
    hexdiff "$BWTFILE" "$UNMTFFILE" || die "Bad UnMTF output for $BWTFILE"

    # Testing Huffman
    echo -n "Huffman..."
    java -ea -cp .. Huffman "$MTFFILE" "$HUFFFILE" || die "Java MTF failed with $MTFFILE"
    echo_filesize "$HUFFFILE" "$INFILE"
    echo -n "UnHuffman..."
    java -ea -cp .. UnHuffman "$HUFFFILE" "$UNHUFFFILE" || die "Java UnMTF failed with $HUFFFILE"
    hexdiff "$MTFFILE" "$UNHUFFFILE" || die "Bad UnMTF output for $MTFFILE"

    echo "OK"
    rm -f "$BWTFILE" "$UNBWTFILE"
    rm -f "$MTFFILE" "$UNMTFFILE"
    rm -f "$HUFFFILE" "$UNHUFFFILE"
}

# Test files from input
for INFILE in *.in
do
    run_tests "${INFILE%.in}" "$INFILE"
done

# Test random input
for SIZE in 100 500 1000 10000 100000
do
    FILEPREFIX="random-$SIZE"
    INFILE="$FILEPREFIX.in.tmp"
    head -c $SIZE /dev/urandom > "$INFILE"
    run_tests "$FILEPREFIX" "$INFILE"
    rm "$INFILE"
done
