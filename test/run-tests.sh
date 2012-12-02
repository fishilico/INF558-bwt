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
    TMPFILE="$FILEPREFIX.tmp"
    [ -r "$OUTFILE" ] || die "Unable to read $OUTFILE"
    java -cp .. BWT "$INFILE" "$TMPFILE" || die "Java failed"
    diff "$OUTFILE" "$TMPFILE" || die "Bad output for $FILEPREFIX"
done
