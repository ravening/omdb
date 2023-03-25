# Assumptions

1. It is assumed that OMDB response does not change drastically and returns only one entry for a movie
2. It is assumed that users are ok with getting stale data for upto 5 minutes. After searching for a movie, the result will be cached. \
    If for any reason, this data is changed on the OMDB side, users will not see it for another 5 minutes as caches are cleared every 5 minutes
3. It is assumed that, if a user searches for a movie and if its not present in csv file then award won will  be set to false
4. It is assumed that users will see the proper results for top rated movies only after searching for atleast 10 movies
5. It is assumed that security is not needed and thus authentication and authorization are not performed
