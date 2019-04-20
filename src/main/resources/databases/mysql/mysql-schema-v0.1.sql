-- MySQL Script for online_market
-- Miercoles Abril 10 08:50 AM 2019
-- Model Version: 0.1 

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Drop Schema online_market
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `online_market`;

-- -----------------------------------------------------
-- Create Schema online_market
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `online_market` DEFAULT CHARACTER SET utf8 ;

-- -----------------------------------------------------
-- Use Schema online_market
-- -----------------------------------------------------
USE `online_market`;

-- -----------------------------------------------------
-- Create Table online_market.customer
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `online_market`.`customer` (
   `id`      INT NOT NULL, 
   `fname`   VARCHAR(50) NOT NULL, 
   `lname`   VARCHAR(50) NOT NULL, 
   `address` VARCHAR(50) NOT NULL, 
   `email`   VARCHAR(50) NOT NULL, 
   `phone`   BIGINT, 
   `active`  BOOLEAN NOT NULL
) ENGINE=InnoDB CHARACTER SET utf8;

-- -----------------------------------------------------
-- Add Primary Key for Table online_market.customer
-- -----------------------------------------------------
ALTER TABLE `online_market`.`customer` ADD PRIMARY KEY (`id`);

-- -----------------------------------------------------
-- Add AUTO_INCREMENT for Table online_market.customer
-- -----------------------------------------------------
ALTER TABLE `online_market`.`customer` CHANGE COLUMN `id` `id` INT NOT NULL AUTO_INCREMENT;

-- -----------------------------------------------------
-- Add Unique Key for Table online_market.customer
-- -----------------------------------------------------
ALTER TABLE `online_market`.`customer` ADD UNIQUE KEY (`email`);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;