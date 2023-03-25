# Scaling

Since this application can be run on docker, we can easily deploy multiple instances of it \
on kubernetes. If one of the instances goes down, kubernetes will automatically bring up another one.

We can also add a loadbalancer in front it which distributes the load across all running instances.

Since the application is stateless and each have its own cache, horizontal scaling is easy. \
We can do database sharding as well and use consistent hashing to distribute the load.

Using spring boot webflux might speed up the response time


If we want handle 10million requests per day then its approximately 120 requests per second.

By default tomcat supports 200 threads which means it can handle 200 requests at a time.

Only the first request for movie might take around 1 second and susequent calls will be in milliseconds.

So handling these many requests shouldnt be an issue.
