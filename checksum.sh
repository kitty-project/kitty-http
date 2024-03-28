#!/usr/bin/env sh

target="./target"
cd $target || exit

# Checksum
md5sum kitty-http-0.0.1.jar > kitty-http-0.0.1.jar.md5
sha1sum kitty-http-0.0.1.jar > kitty-http-0.0.1.jar.sha1
md5sum kitty-http-0.0.1-javadoc.jar > kitty-http-0.0.1-javadoc.jar.md5
sha1sum kitty-http-0.0.1-javadoc.jar > kitty-http-0.0.1-javadoc.jar.sha1
md5sum kitty-http-0.0.1-sources.jar > kitty-http-0.0.1-sources.jar.md5
sha1sum kitty-http-0.0.1-sources.jar > kitty-http-0.0.1-sources.jar.sha1
