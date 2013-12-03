# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "clicks" ("url_id" INTEGER NOT NULL,"referrer" text,"timestamp" timestamp with time zone NOT NULL);
create index "clicks_uid_idx" on "clicks" ("url_id");
create table "shortened_urls" ("id" SERIAL NOT NULL PRIMARY KEY,"url" text NOT NULL);
create unique index "surls_url_idx" on "shortened_urls" ("url");

# --- !Downs

drop table "clicks";
drop table "shortened_urls";

