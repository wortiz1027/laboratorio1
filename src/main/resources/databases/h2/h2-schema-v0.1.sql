DROP TABLE CUSTOMER;

CREATE TABLE CUSTOMER ( 
   id      INT NOT NULL, 
   fname   VARCHAR(50) NOT NULL, 
   lname   VARCHAR(50) NOT NULL, 
   address VARCHAR(50) NOT NULL, 
   email   VARCHAR(50) NOT NULL, 
   phone   BIGINT, 
   active  BOOLEAN NOT NULL
);

ALTER TABLE CUSTOMER ADD PRIMARY KEY (ID);