-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.21 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for book_store
CREATE DATABASE IF NOT EXISTS `book_store` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `book_store`;

-- Dumping structure for procedure book_store.add_book_sale
DELIMITER //
CREATE PROCEDURE `add_book_sale`(
	IN `id` INT,
	OUT `price` INT
)
    MODIFIES SQL DATA
    DETERMINISTIC
    SQL SECURITY INVOKER
BEGIN
INSERT INTO book_sale(book_id) VALUES (id);
SELECT book.price
INTO price
FROM book WHERE book_id = id;
END//
DELIMITER ;

-- Dumping structure for table book_store.author
CREATE TABLE IF NOT EXISTS `author` (
  `author_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  PRIMARY KEY (`author_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Dumping data for table book_store.author: ~5 rows (approximately)
/*!40000 ALTER TABLE `author` DISABLE KEYS */;
INSERT INTO `author` (`author_id`, `first_name`, `middle_name`, `last_name`) VALUES
	(1, 'Isaac', NULL, 'Asimov'),
	(2, 'Douglas', 'Noël', 'Adams'),
	(3, 'Ray', 'Douglas', 'Bradbury'),
	(4, 'George', NULL, 'Orwell'),
	(5, 'John', 'Ronald Reuel', 'Tolkien');
/*!40000 ALTER TABLE `author` ENABLE KEYS */;

-- Dumping structure for table book_store.book
CREATE TABLE IF NOT EXISTS `book` (
  `book_id` int NOT NULL AUTO_INCREMENT,
  `author_id` int NOT NULL,
  `book_name` varchar(50) NOT NULL,
  `price` int NOT NULL,
  PRIMARY KEY (`book_id`),
  KEY `FK_book_author` (`author_id`),
  CONSTRAINT `FK_book_author` FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- Dumping data for table book_store.book: ~11 rows (approximately)
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` (`book_id`, `author_id`, `book_name`, `price`) VALUES
	(1, 1, 'I, Robot', 200),
	(2, 1, 'The End of Eternity', 100),
	(3, 1, 'The Bicentennial Man', 150),
	(4, 3, 'The Martian Chronicles', 100),
	(5, 3, 'Fahrenheit 451', 360),
	(6, 4, 'Nineteen Eighty-Four', 300),
	(7, 5, 'The Hobbit, or There and Back Again', 150),
	(8, 5, 'The Fellowship of the Ring', 150),
	(9, 5, 'The Two Towers', 150),
	(10, 5, 'The Return of the King', 150),
	(11, 5, 'The Silmarillion', 150);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;

-- Dumping structure for table book_store.book_sale
CREATE TABLE IF NOT EXISTS `book_sale` (
  `sale_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sale_id`),
  KEY `FK__book` (`book_id`),
  CONSTRAINT `FK__book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

-- Dumping data for table book_store.book_sale: ~100 rows (approximately)
/*!40000 ALTER TABLE `book_sale` DISABLE KEYS */;
INSERT INTO `book_sale` (`sale_id`, `book_id`, `date_time`) VALUES
	(1, 9, '2020-12-01 10:34:08'),
	(2, 8, '2020-12-01 10:46:23'),
	(3, 1, '2020-12-01 13:12:06'),
	(4, 7, '2020-12-01 13:56:51'),
	(5, 8, '2020-12-01 16:25:26'),
	(6, 4, '2020-12-01 17:36:03'),
	(7, 3, '2020-12-01 18:01:06'),
	(8, 3, '2020-12-01 18:18:01'),
	(9, 7, '2020-12-01 18:41:37'),
	(10, 5, '2020-12-01 18:54:41'),
	(11, 5, '2020-12-01 19:17:25'),
	(12, 11, '2020-12-01 20:01:04'),
	(13, 1, '2020-12-02 10:30:05'),
	(14, 1, '2020-12-02 12:12:10'),
	(15, 3, '2020-12-02 13:39:59'),
	(16, 2, '2020-12-02 14:19:30'),
	(17, 8, '2020-12-02 18:22:30'),
	(18, 11, '2020-12-02 18:36:08'),
	(19, 8, '2020-12-02 18:40:10'),
	(20, 8, '2020-12-02 18:42:39'),
	(21, 5, '2020-12-02 19:12:49'),
	(22, 11, '2020-12-02 19:25:32'),
	(23, 1, '2020-12-02 20:43:48'),
	(24, 1, '2020-12-02 20:53:49'),
	(25, 3, '2020-12-02 20:57:51'),
	(26, 5, '2020-12-03 10:09:35'),
	(27, 2, '2020-12-03 10:22:11'),
	(28, 2, '2020-12-03 10:27:28'),
	(29, 3, '2020-12-03 10:48:20'),
	(30, 10, '2020-12-03 10:56:03'),
	(31, 10, '2020-12-03 12:27:57'),
	(32, 8, '2020-12-03 13:05:51'),
	(33, 5, '2020-12-03 13:25:38'),
	(34, 8, '2020-12-03 13:40:31'),
	(35, 3, '2020-12-03 13:56:14'),
	(36, 4, '2020-12-03 14:21:24'),
	(37, 1, '2020-12-03 16:32:06'),
	(38, 5, '2020-12-03 16:38:24'),
	(39, 1, '2020-12-03 16:56:21'),
	(40, 5, '2020-12-03 18:29:36'),
	(41, 11, '2020-12-03 18:46:42'),
	(42, 10, '2020-12-03 20:10:27'),
	(43, 11, '2020-12-03 20:34:37'),
	(44, 10, '2020-12-04 11:39:24'),
	(45, 5, '2020-12-04 11:40:38'),
	(46, 3, '2020-12-04 12:00:37'),
	(47, 8, '2020-12-04 12:21:22'),
	(48, 7, '2020-12-04 13:47:44'),
	(49, 5, '2020-12-04 14:33:21'),
	(50, 9, '2020-12-04 14:36:32'),
	(51, 9, '2020-12-04 15:57:12'),
	(52, 5, '2020-12-04 17:01:41'),
	(53, 11, '2020-12-04 18:28:14'),
	(54, 4, '2020-12-04 19:08:20'),
	(55, 4, '2020-12-04 19:49:14'),
	(56, 9, '2020-12-04 20:30:05'),
	(57, 4, '2020-12-05 10:28:06'),
	(58, 9, '2020-12-05 10:33:15'),
	(59, 9, '2020-12-05 10:53:50'),
	(60, 2, '2020-12-05 11:03:32'),
	(61, 10, '2020-12-05 11:20:09'),
	(62, 4, '2020-12-05 11:59:51'),
	(63, 1, '2020-12-05 12:51:28'),
	(64, 11, '2020-12-05 13:29:29'),
	(65, 2, '2020-12-05 13:45:52'),
	(66, 2, '2020-12-05 14:24:17'),
	(67, 10, '2020-12-05 15:34:48'),
	(68, 8, '2020-12-05 16:26:24'),
	(69, 10, '2020-12-05 17:19:49'),
	(70, 5, '2020-12-05 17:46:17'),
	(71, 5, '2020-12-05 17:52:53'),
	(72, 3, '2020-12-05 18:04:38'),
	(73, 9, '2020-12-05 18:15:25'),
	(74, 4, '2020-12-05 18:24:21'),
	(75, 5, '2020-12-05 18:42:22'),
	(76, 1, '2020-12-05 19:29:35'),
	(77, 2, '2020-12-05 19:41:31'),
	(78, 5, '2020-12-05 20:53:53'),
	(79, 2, '2020-12-06 11:20:19'),
	(80, 4, '2020-12-06 12:12:44'),
	(81, 2, '2020-12-06 12:14:13'),
	(82, 11, '2020-12-06 15:11:28'),
	(83, 1, '2020-12-06 17:35:00'),
	(84, 11, '2020-12-06 18:01:25'),
	(85, 9, '2020-12-06 18:31:15'),
	(86, 3, '2020-12-06 18:59:11'),
	(87, 5, '2020-12-06 19:09:42'),
	(88, 9, '2020-12-06 19:13:51'),
	(89, 3, '2020-12-07 10:15:00'),
	(90, 4, '2020-12-07 11:53:58'),
	(91, 5, '2020-12-07 12:36:31'),
	(92, 11, '2020-12-07 13:01:00'),
	(93, 4, '2020-12-07 13:05:56'),
	(94, 9, '2020-12-07 15:54:32'),
	(95, 7, '2020-12-07 16:00:36'),
	(96, 1, '2020-12-07 16:27:26'),
	(97, 4, '2020-12-07 16:43:42'),
	(98, 9, '2020-12-07 17:06:25'),
	(99, 2, '2020-12-07 17:37:40'),
	(100, 7, '2020-12-07 19:29:46');
/*!40000 ALTER TABLE `book_sale` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
