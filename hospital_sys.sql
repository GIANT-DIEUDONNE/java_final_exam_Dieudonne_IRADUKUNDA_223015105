-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 17, 2025 at 05:41 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hospital_sys`
--

-- --------------------------------------------------------

--
-- Table structure for table `guest`
--

DROP TABLE IF EXISTS `guest`;
CREATE TABLE IF NOT EXISTS `guest` (
  `GuestID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `PasswordHash` varchar(255) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `FullName` varchar(100) NOT NULL,
  `Role` enum('Guest','Admin','Staff') DEFAULT 'Guest',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `LastLogin` datetime DEFAULT NULL,
  PRIMARY KEY (`GuestID`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `guest`
--

INSERT INTO `guest` (`GuestID`, `Username`, `PasswordHash`, `Email`, `FullName`, `Role`, `CreatedAt`, `LastLogin`) VALUES
(1, 'HAKIZIMANA Bahati', '673d190b758967621da243f06c350ce68be4276174dc886560239fea923d4a5a', 'hakizimanabahati@gmail.com', 'HAKIZIMANA Bahati', 'Guest', '2025-10-15 18:06:44', '2025-11-01 11:46:41'),
(2, 'IRADUKUNDA Olivier', 'c0df64036c36df6eabd6951f3442a74a38e5ce8556f4d579b83eda535495f1cc', 'iradukundaolivier@gmail.com', 'IRADUKUNDA Olivier', 'Guest', '2025-10-15 18:06:44', '2025-10-29 15:02:13'),
(4, 'IRADUKUNDA Dieudonne', '62ff107aa2d8adc98175eabf86a8cff243871c11e1a214063762c020cf186d05', 'iradukundadieudonne23@gmail.com', 'IRADUKUNDA DIEUDONNE', 'Admin', '2025-10-20 13:05:33', '2025-12-17 19:27:05'),
(5, 'habingabire olivier', '001dfb5676627c25bcac7bf3393d3e8b1e2e9a0735947365fe618a168b202a19', 'habingabire@gmail.com', 'habingabire olivier', 'Guest', '2025-10-20 13:40:41', '2025-10-21 14:00:52'),
(7, 'Bernard', '3a4119714c139eae939ab7d10ef5d2507b973438056750032eaf0da04abdd8cc', 'niyonshutibernard@gmail.com', 'NIYONSHUTI Bernard', 'Staff', '2025-10-21 14:57:39', '2025-11-01 13:31:20'),
(8, 'BYIRINGIRO Emmanuel', 'b89ccde275c52c0304766ff90887c10c0718ed895939428be36348f4b434bb75', 'byiringiro@gmail.com', 'Emmanuel', 'Guest', '2025-10-22 12:41:35', NULL),
(9, 'olivier', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'iradukundaolivier420@gmail.com', 'MUKUNDE LEANDRE KAYIBANDA', 'Staff', '2025-10-22 14:43:29', NULL),
(10, 'ferdinand', '2d44fbaffc5efea7b774505d73f25f1cda61228c60b7534f51a70273f7778eb8', 'ferdinand@gmail.com', 'IRAKOZE Ferdinand', 'Staff', '2025-10-23 14:06:04', '2025-10-23 14:06:50'),
(11, 'heshima', '35d5eca8c3de4817f9e6e0f7d70679cb577a076d0b5bba73da8561479831f54c', 'heshima@gmail.com', 'heshima herbert', 'Guest', '2025-10-30 12:31:47', '2025-10-30 12:32:08'),
(12, 'obed', '40121f45592fb63589ed7e3e3f23a9e707a62a2eb17dde6be415b25a3a7408a2', 'obednteziryayo174@gmail.com', 'Obed NTEZIRYAYO', 'Guest', '2025-12-16 17:45:30', '2025-12-16 17:45:51');

-- --------------------------------------------------------

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
CREATE TABLE IF NOT EXISTS `invoice` (
  `InvoiceID` int NOT NULL AUTO_INCREMENT,
  `StaffID` int DEFAULT NULL,
  `Amount` decimal(10,2) NOT NULL,
  `Date` date NOT NULL,
  `Status` enum('Pending','Paid','Cancelled') DEFAULT 'Pending',
  `RoomID` int DEFAULT NULL,
  PRIMARY KEY (`InvoiceID`),
  KEY `StaffID` (`StaffID`),
  KEY `fk_invoice_room` (`RoomID`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `invoice`
--

INSERT INTO `invoice` (`InvoiceID`, `StaffID`, `Amount`, `Date`, `Status`, `RoomID`) VALUES
(1, 3, 320.00, '2025-10-10', 'Paid', 1),
(2, 3, 75.00, '2025-10-11', 'Pending', 2),
(3, 4, 7000.00, '2025-02-09', 'Cancelled', 3);

-- --------------------------------------------------------

--
-- Table structure for table `invoiceservice`
--

DROP TABLE IF EXISTS `invoiceservice`;
CREATE TABLE IF NOT EXISTS `invoiceservice` (
  `InvoiceID` int NOT NULL,
  `ServiceID` int NOT NULL,
  PRIMARY KEY (`InvoiceID`,`ServiceID`),
  KEY `ServiceID` (`ServiceID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `invoiceservice`
--

INSERT INTO `invoiceservice` (`InvoiceID`, `ServiceID`) VALUES
(1, 1),
(1, 3),
(1, 4),
(2, 2),
(3, 5);

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
CREATE TABLE IF NOT EXISTS `reservation` (
  `ReservationID` int NOT NULL AUTO_INCREMENT,
  `GuestID` int NOT NULL,
  `OrderNumber` varchar(50) NOT NULL,
  `Date` date NOT NULL,
  `Status` enum('Pending','Confirmed','Cancelled','Completed') DEFAULT 'Pending',
  `TotalAmount` decimal(10,2) DEFAULT '0.00',
  `PaymentMethod` enum('Cash','Card','Online') DEFAULT 'Cash',
  `Notes` text,
  PRIMARY KEY (`ReservationID`),
  UNIQUE KEY `OrderNumber` (`OrderNumber`),
  KEY `GuestID` (`GuestID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reservation`
--

INSERT INTO `reservation` (`ReservationID`, `GuestID`, `OrderNumber`, `Date`, `Status`, `TotalAmount`, `PaymentMethod`, `Notes`) VALUES
(1, 1, 'RES001', '2025-10-10', 'Confirmed', 300.00, 'Card', 'reservation confirmed'),
(2, 2, 'RES002', '2025-10-12', 'Pending', 150.00, 'Cash', 'reservation pending'),
(3, 4, 'RES04', '2025-09-06', 'Completed', 1785.00, 'Cash', 'reservation complete'),
(4, 3, 'RES011', '2025-09-30', 'Cancelled', 10000.00, 'Cash', 'reservation cancelled');

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
CREATE TABLE IF NOT EXISTS `room` (
  `RoomID` int NOT NULL AUTO_INCREMENT,
  `ReservationID` int DEFAULT NULL,
  `Name` varchar(50) NOT NULL,
  `Description` text,
  `Category` varchar(50) DEFAULT NULL,
  `PriceOrValue` decimal(10,2) NOT NULL,
  `Status` enum('Available','Occupied','Maintenance') DEFAULT 'Available',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`RoomID`),
  KEY `ReservationID` (`ReservationID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `room`
--

INSERT INTO `room` (`RoomID`, `ReservationID`, `Name`, `Description`, `Category`, `PriceOrValue`, `Status`, `CreatedAt`) VALUES
(1, 1, 'Emergency Room (ER)', 'Handles immediate medical emergencies', 'emergency', 2000.00, 'Occupied', '2025-10-15 18:07:43'),
(2, 1, 'Pediatric Intensive Care Unit (PICU)', 'For critically ill children', 'general', 150.00, 'Occupied', '2025-10-15 18:07:43'),
(3, 2, 'Operating Room (OR)', 'Equipped for surgical procedures', 'surgary', 7000.00, 'Available', '2025-10-15 18:07:43'),
(4, NULL, 'Internal medecine', 'internal organ diseases', 'OPD', 8000.00, 'Available', '2025-10-27 13:43:07');

-- --------------------------------------------------------

--
-- Table structure for table `roomservice`
--

DROP TABLE IF EXISTS `roomservice`;
CREATE TABLE IF NOT EXISTS `roomservice` (
  `RoomID` int NOT NULL,
  `ServiceID` int NOT NULL,
  PRIMARY KEY (`RoomID`,`ServiceID`),
  KEY `ServiceID` (`ServiceID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `roomservice`
--

INSERT INTO `roomservice` (`RoomID`, `ServiceID`) VALUES
(1, 1),
(1, 3),
(2, 2),
(2, 3),
(3, 1);

-- --------------------------------------------------------

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
CREATE TABLE IF NOT EXISTS `service` (
  `ServiceID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Description` text,
  `Category` varchar(50) DEFAULT NULL,
  `PriceOrValue` decimal(10,2) NOT NULL,
  `Status` enum('Active','Inactive') DEFAULT 'Active',
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ServiceID`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `service`
--

INSERT INTO `service` (`ServiceID`, `Name`, `Description`, `Category`, `PriceOrValue`, `Status`, `CreatedAt`) VALUES
(1, 'medical service', 'Consultation with doctors', 'general', 10000.00, 'Active', '2025-10-15 18:08:03'),
(2, 'body checks', 'checking my body movement', 'medical', 400.00, 'Active', '2025-10-15 18:08:03'),
(3, 'surgary', 'Administrative assistance for patients', 'internal medecine', 200.00, 'Active', '2025-10-15 18:08:03'),
(5, 'Maternity', 'service offered for women pregnancy', 'Meternity', 190.34, 'Active', '2025-10-23 13:55:01');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
CREATE TABLE IF NOT EXISTS `staff` (
  `StaffID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Identifier` varchar(50) DEFAULT NULL,
  `Status` enum('Active','Inactive') DEFAULT 'Active',
  `Location` varchar(100) DEFAULT NULL,
  `Contact` varchar(50) DEFAULT NULL,
  `AssignedSince` date DEFAULT NULL,
  PRIMARY KEY (`StaffID`),
  UNIQUE KEY `Identifier` (`Identifier`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`StaffID`, `Name`, `Identifier`, `Status`, `Location`, `Contact`, `AssignedSince`) VALUES
(1, 'GENERAL SURGARY', 'S001', 'Active', 'CHUB', '+250788850634', '2025-04-05'),
(2, 'OPHTHALOMOLOGISTICS', 'S010', 'Active', 'KABUTARE', '+250790684556', '2024-01-16'),
(3, 'DENTIST', 'S012', 'Active', 'CHUK', '+250790222835', '2024-03-20'),
(4, 'ORTHOPED', 'S045', 'Active', 'CHUB', '+250785643510', '2025-10-23'),
(5, 'INTERNAL MEDICINE', 'S030', 'Active', 'CHUB', '+250788957732', '2025-10-23'),
(6, 'PHYSIOTHERAPY', 'PH110', 'Active', 'CHUB', '+250790684556', '2025-01-23');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
