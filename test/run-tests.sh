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
    [ -r "$OUTFILE" ] || die "Unable to read $OUTFILE"
    java -cp .. BWT "$INFILE" "$BWTFILE" || die "Java BWT failed"
    diff "$OUTFILE" "$BWTFILE" || die "Bad BWT output for $FILEPREFIX"
    java -cp .. UnBWT "$OUTFILE" "$UNBWTFILE" || die "Java UnBWT failed"
    diff "$INFILE" "$UNBWTFILE" || die "Bad UnBWT output for $FILEPREFIX"
done
