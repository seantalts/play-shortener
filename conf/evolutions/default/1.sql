# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "shortened_urls" ("id" SERIAL NOT NULL PRIMARY KEY,"url" text NOT NULL);
create unique index "idx_url" on "shortened_urls" ("url");

# --- !Downs

drop table "shortened_urls";

