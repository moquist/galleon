#!/bin/bash
lein voom freshen
lein voom build-deps

if [ ! -f galleon-conf.edn ]; then
  echo "\nI didn't find your galleon-conf.edn, I'll copy it for you.\n"
  cp galleon-conf-dist.edn galleon-conf.edn
fi

lein immutant test
