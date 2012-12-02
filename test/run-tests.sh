#!/bin/sh

die() {
    echo $*
    exit 1
}

# Test files from input
for INFILE in *.in
do
    FILEPREFIX="${INFILE%.in}"
    OUTFILE="$FILEPREFIX.out"
    BWTFILE="$FILEPREFIX.bwt.tmp"
    UNBWTFILE="$FILEPREFIX.unbwt.tmp"
    echo -n "Testing $FILEPREFIX..."
    [ -r "$OUTFILE" ] || die "Unable to read $OUTFILE"
    java -ea -cp .. BWT "$INFILE" "$BWTFILE" || die "Java BWT failed"
    diff "$OUTFILE" "$BWTFILE" || die "Bad BWT output for $FILEPREFIX"
    java -ea -cp .. UnBWT "$OUTFILE" "$UNBWTFILE" || die "Java UnBWT failed"
    if ! diff -q "$INFILE" "$UNBWTFILE"
    then
        xxd "$INFILE" > "$FILEPREFIX.in.hex.tmp"
        xxd "$UNBWTFILE" > "$FILEPREFIX.unbwt.hex.tmp"
        diff -y "$FILEPREFIX.in.hex" "$FILEPREFIX.unbwt.hex.tmp"
        die "Bad UnBWT output for $FILEPREFIX"
    fi
    echo OK
    rm "$BWTFILE" "$UNBWTFILE"
done

# Test random input
for SIZE in 100 500 1000 10000
do
    FILEPREFIX="random-$SIZE"
    INFILE="$FILEPREFIX.in.tmp"
    BWTFILE="$FILEPREFIX.bwt.tmp"
    UNBWTFILE="$FILEPREFIX.unbwt.tmp"
    echo -n "Testing $FILEPREFIX..."
    head -c $SIZE /dev/urandom > "$INFILE"
    java -ea -cp .. BWT "$INFILE" "$BWTFILE" || die "Java BWT failed"
    java -ea -cp .. UnBWT "$BWTFILE" "$UNBWTFILE" || die "Java UnBWT failed"
    if ! diff -q "$INFILE" "$UNBWTFILE"
    then
        xxd "$INFILE" > "$FILEPREFIX.in.hex.tmp"
        xxd "$UNBWTFILE" > "$FILEPREFIX.unbwt.hex.tmp"
        diff -y "$FILEPREFIX.in.hex" "$FILEPREFIX.unbwt.hex.tmp"
        die "Bad UnBWT output for $FILEPREFIX"
    fi
    echo OK
    rm "$INFILE" "$BWTFILE" "$UNBWTFILE"
done
