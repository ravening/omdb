# TODO

1. Add spring security to protect the endpoints
2. Use webflux to increase the response time and to handle more requests
3. Use sophisticated caches like hazelcast or redis rather than basic cache
4. Externalize the values in [properties](src/main/resources/application.properties) to read from external source to \
   store db username/password and other sensitive info. Also use profiles for local/test/beta and live environment.
5. Check if Movie entity has to be split into Movie and Rating and provie relationship betwen them
6. Use the @Version annotation on entity to prevent dirty writes so that Locks can be avoided
7. Spring boot actuator is already provided with health info and other app related info. So we can expose these \
   data to prometheus and can easily create graphs in Grafana to monitor the application performance
