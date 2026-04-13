#!/bin/sh
set -eu

: "${PORT:=80}"

envsubst '$PORT $BACKEND_URL' \
  < /etc/nginx/templates/default.conf.template \
  > /etc/nginx/conf.d/default.conf

envsubst '$BACKEND_URL $FRONTEND_API_BASE_URL' \
  < /usr/share/nginx/html/env.js.template \
  > /usr/share/nginx/html/env.js
