# How to test

Once you have started the application, you can test the endpoints either through \
postman or through web

You must get a valid omdb apikey before testing.

The first section explains how to test through web. Scroll down to see how to test through postman

Running unit and integration tests is mentioned at the end of this file.

## Testing through web
If you want to test through web, then navigate to http://localhost:8080/swagger-ui/index.html

Here you will see 3 endpoints.

1. To search for a movie, click on `GET /api/bestmovie`. 

    Click on `Try it out` on the right side and enter the movie name and the omdb apikey
2. To add a rating to movie, click on `POST /api/rating` under the ratings-controller section. \
    Enter the apikey, proper movie name for the `title` parameter, valid number for the `rating` parameter in the request body.
3. To list top 10 movies ordered by rating and box office value, click on `GET /api/topmovies` under movie-controller section.

There is a possibility that you will see lot of movies in the above output with 0 for rating, votes and boxoffice value. 
This is because those details are not yet fetched from OMDB.

So first, search for those movies using `GET /api/bestmovie` and then run this endpoint again.


## Testing through postman

Install [postman](https://www.postman.com/downloads/) first

1. Searching for a movie

 create a new GET request by passing `http://localhost:8080/api/bestmovie?title=Chicago&apikey=<>` for url

2. To add a rating to movie

 create a new POST request by passing `http://localhost:8080/api/rating?apikey=<>` for the url 
 
 Click on Body, select `raw` and content type as JSON in the right most drop down button. use the below content

```json
{
    "title": "chicago",
    "rating": 8
}
```

3. To list top 10 rated movies

 create a get request for `http://localhost:8080/api/topmovies`


## Running unit and integ tests

Assuming that, you have already installed maven, run the below command

```bash
mvn test
```
