CREATE SEQUENCE IF NOT EXISTS my_seq INCREMENT 1 START 1 CACHE 100;

drop table if exists movie;

CREATE TABLE movie (
       id INT PRIMARY KEY DEFAULT NEXTVAL('my_seq'),
       year TEXT NOT NULL ,
       category TEXT NOT NULL ,
       nominee TEXT NOT NULL ,
       description TEXT NOT NULL ,
       won BOOLEAN DEFAULT FALSE,
       ratings numeric,
       votes numeric,
       boxofficevalue numeric
);
