-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 11, 2023 at 05:31 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pharma`
--
CREATE DATABASE IF NOT EXISTS `pharma` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `pharma`;

-- --------------------------------------------------------

--
-- Table structure for table `daily_sales`
--

CREATE TABLE `daily_sales` (
  `ID` bigint(20) NOT NULL,
  `date` varchar(50) NOT NULL,
  `day` varchar(15) NOT NULL,
  `total_sale` mediumint(8) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employee_salary_history`
--

CREATE TABLE `employee_salary_history` (
  `ID` int(10) UNSIGNED NOT NULL,
  `employee_name` varchar(50) NOT NULL,
  `salary` int(10) UNSIGNED NOT NULL,
  `date` varchar(50) NOT NULL,
  `payer_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `login`
--

CREATE TABLE `login` (
  `ID` smallint(5) UNSIGNED NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `position` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `login_history`
--

CREATE TABLE `login_history` (
  `ID` int(11) NOT NULL,
  `userID` smallint(5) UNSIGNED NOT NULL,
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `medicine`
--

CREATE TABLE `medicine` (
  `ID` bigint(20) NOT NULL,
  `meds_name` varchar(50) NOT NULL,
  `price` float NOT NULL,
  `stock` smallint(11) UNSIGNED NOT NULL,
  `formulation` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `monthly_income`
--

CREATE TABLE `monthly_income` (
  `ID` smallint(5) UNSIGNED NOT NULL,
  `month_and_year` varchar(30) NOT NULL,
  `income` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `monthly_sales`
--

CREATE TABLE `monthly_sales` (
  `ID` smallint(6) NOT NULL,
  `month_and_year` varchar(50) NOT NULL,
  `total_sale` mediumint(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `other_expenses_history`
--

CREATE TABLE `other_expenses_history` (
  `ID` int(10) UNSIGNED NOT NULL,
  `name` varchar(100) NOT NULL,
  `amount` mediumint(8) UNSIGNED NOT NULL,
  `date` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `restock_expenses_history`
--

CREATE TABLE `restock_expenses_history` (
  `ID` int(10) UNSIGNED NOT NULL,
  `meds_name` varchar(50) NOT NULL,
  `quantity` smallint(5) UNSIGNED NOT NULL,
  `price` float UNSIGNED NOT NULL,
  `date` varchar(50) DEFAULT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sold_medicine`
--

CREATE TABLE `sold_medicine` (
  `ID` bigint(20) NOT NULL,
  `items` varchar(700) NOT NULL,
  `total_price` smallint(5) UNSIGNED NOT NULL,
  `date_sold` datetime NOT NULL,
  `login_id` smallint(5) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `weekly_sales`
--

CREATE TABLE `weekly_sales` (
  `ID` smallint(6) NOT NULL,
  `date` varchar(50) NOT NULL,
  `week` varchar(10) NOT NULL,
  `total_sale` mediumint(8) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `daily_sales`
--
ALTER TABLE `daily_sales`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `employee_salary_history`
--
ALTER TABLE `employee_salary_history`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `login_history`
--
ALTER TABLE `login_history`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `userID` (`userID`);

--
-- Indexes for table `medicine`
--
ALTER TABLE `medicine`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `monthly_income`
--
ALTER TABLE `monthly_income`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `monthly_sales`
--
ALTER TABLE `monthly_sales`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `other_expenses_history`
--
ALTER TABLE `other_expenses_history`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `restock_expenses_history`
--
ALTER TABLE `restock_expenses_history`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `sold_medicine`
--
ALTER TABLE `sold_medicine`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `medicine_id` (`items`),
  ADD KEY `login_id` (`login_id`);

--
-- Indexes for table `weekly_sales`
--
ALTER TABLE `weekly_sales`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `daily_sales`
--
ALTER TABLE `daily_sales`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_salary_history`
--
ALTER TABLE `employee_salary_history`
  MODIFY `ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `login`
--
ALTER TABLE `login`
  MODIFY `ID` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `login_history`
--
ALTER TABLE `login_history`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `medicine`
--
ALTER TABLE `medicine`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `monthly_income`
--
ALTER TABLE `monthly_income`
  MODIFY `ID` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `monthly_sales`
--
ALTER TABLE `monthly_sales`
  MODIFY `ID` smallint(6) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `other_expenses_history`
--
ALTER TABLE `other_expenses_history`
  MODIFY `ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `restock_expenses_history`
--
ALTER TABLE `restock_expenses_history`
  MODIFY `ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sold_medicine`
--
ALTER TABLE `sold_medicine`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `weekly_sales`
--
ALTER TABLE `weekly_sales`
  MODIFY `ID` smallint(6) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `login_history`
--
ALTER TABLE `login_history`
  ADD CONSTRAINT `login_history_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `login` (`ID`);

--
-- Constraints for table `sold_medicine`
--
ALTER TABLE `sold_medicine`
  ADD CONSTRAINT `sold_medicine_ibfk_2` FOREIGN KEY (`login_id`) REFERENCES `login` (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
