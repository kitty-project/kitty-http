#!/usr/bin/env sh

target="./target"
cd $target || exit

# Sign with GPG/PGP
gpg -ab kitty-http-0.0.1.jar
gpg -ab kitty-http-0.0.1-javadoc.jar
gpg -ab kitty-http-0.0.1-sources.jar 
