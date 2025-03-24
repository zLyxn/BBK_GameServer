const CACHE_NAME = 'dashboard-cache-v1';
const urlsToCache = [
  '/',
  '/status',
  '/start',
  '/stop',
  '/exit',
  '/sendCommand',
  '/threads'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        return cache.addAll(urlsToCache);
      })
  );
});

self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        if (response) {
          return response;
        }
        return fetch(event.request);
      })
  );
});