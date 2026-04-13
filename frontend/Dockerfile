FROM nginx:1.29-alpine

COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY env.js.template /usr/share/nginx/html/env.js.template
COPY docker-entrypoint-env.sh /docker-entrypoint.d/40-env-config.sh
RUN chmod +x /docker-entrypoint.d/40-env-config.sh
COPY index.html /usr/share/nginx/html/index.html
COPY styles.css /usr/share/nginx/html/styles.css
COPY app.js /usr/share/nginx/html/app.js
