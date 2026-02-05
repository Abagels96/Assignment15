-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: momtracker
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `length_in_minutes` int DEFAULT NULL,
  `eat_time` datetime(6) DEFAULT NULL,
  `end_date_time` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_date_time` datetime(6) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `activity_type` varchar(31) NOT NULL,
  `meal_description` varchar(255) DEFAULT NULL,
  `meal_type` enum('BREAKFAST','DINNER','LUNCH','SNACK') DEFAULT NULL,
  `quality` enum('EXCELLENT','FAIR','GOOD','POOR') DEFAULT NULL,
  `rating` enum('FIVE','FOUR','ONE','THREE','TWO') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (NULL,NULL,NULL,1,NULL,'2025-12-24 18:50:00.000000','EAT','Hot dogs and a ham sandwich',NULL,NULL,NULL),(15,NULL,NULL,3,NULL,'2025-12-24 18:51:44.931000','SHOWER',NULL,NULL,NULL,'FOUR'),(NULL,NULL,'2025-12-24 15:55:00.000000',4,'2025-12-24 13:06:00.000000','2025-12-24 19:06:00.000000','SLEEP',NULL,NULL,'GOOD',NULL),(NULL,NULL,NULL,6,NULL,'2026-01-18 03:31:56.895000','EAT','hamburger helper',NULL,NULL,NULL),(NULL,NULL,'2026-01-18 17:32:00.000000',7,'2026-01-18 14:32:00.000000','2026-01-18 20:32:00.000000','SLEEP',NULL,NULL,'FAIR',NULL),(5,NULL,NULL,8,NULL,'2026-01-18 03:33:17.851000','SHOWER',NULL,NULL,NULL,'ONE'),(NULL,NULL,'2026-01-25 19:30:00.000000',9,'2026-01-24 17:23:00.000000','2026-01-24 23:23:00.000000','SLEEP',NULL,NULL,'FAIR',NULL),(NULL,NULL,NULL,10,NULL,'2026-01-26 02:56:59.043000','EAT','pasta',NULL,NULL,NULL),(10,NULL,NULL,11,NULL,'2026-01-31 15:16:55.902000','SHOWER',NULL,NULL,NULL,'THREE'),(NULL,NULL,'2026-01-31 11:18:00.000000',12,'2026-01-31 09:17:00.000000','2026-01-31 15:17:00.000000','SLEEP',NULL,NULL,'GOOD',NULL),(NULL,NULL,'2026-01-31 10:40:00.000000',13,'2026-01-31 09:40:00.000000','2026-01-31 15:40:00.000000','SLEEP',NULL,NULL,'POOR',NULL),(5,NULL,NULL,14,NULL,'2026-01-31 15:50:17.639000','SHOWER',NULL,NULL,NULL,'THREE'),(NULL,NULL,NULL,15,NULL,'2026-01-31 15:50:49.646000','EAT','cereal',NULL,NULL,NULL),(NULL,NULL,NULL,16,NULL,'2026-02-02 19:38:00.183000','EAT','Chicken wings',NULL,NULL,NULL),(NULL,NULL,NULL,17,NULL,'2026-02-02 21:14:25.559000','EAT','peanuts',NULL,NULL,NULL),(NULL,NULL,NULL,18,NULL,'2026-02-04 19:02:10.193000','EAT','chicken drumsticks',NULL,NULL,NULL),(NULL,NULL,NULL,19,NULL,'2026-02-04 19:06:34.906000','EAT','bread',NULL,NULL,NULL),(10,NULL,NULL,20,NULL,'2026-02-04 19:36:55.651000','SHOWER',NULL,NULL,NULL,'TWO'),(NULL,NULL,'2026-02-04 15:50:00.000000',21,'2026-02-04 13:36:00.000000','2026-02-04 19:36:00.000000','SLEEP',NULL,NULL,'GOOD',NULL);
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session`
--

DROP TABLE IF EXISTS `spring_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session` (
  `PRIMARY_ID` char(36) NOT NULL,
  `SESSION_ID` char(36) NOT NULL,
  `CREATION_TIME` bigint NOT NULL,
  `LAST_ACCESS_TIME` bigint NOT NULL,
  `MAX_INACTIVE_INTERVAL` int NOT NULL,
  `EXPIRY_TIME` bigint NOT NULL,
  `PRINCIPAL_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`),
  UNIQUE KEY `SPRING_SESSION_IX1` (`SESSION_ID`),
  KEY `SPRING_SESSION_IX2` (`EXPIRY_TIME`),
  KEY `SPRING_SESSION_IX3` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session`
--

LOCK TABLES `spring_session` WRITE;
/*!40000 ALTER TABLE `spring_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `spring_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session_attributes`
--

DROP TABLE IF EXISTS `spring_session_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session_attributes` (
  `SESSION_PRIMARY_ID` char(36) NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session_attributes`
--

LOCK TABLES `spring_session_attributes` WRITE;
/*!40000 ALTER TABLE `spring_session_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `spring_session_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `task_id` bigint NOT NULL AUTO_INCREMENT,
  `frequency` enum('DAILY','MONTHLY','WEEKLY') DEFAULT NULL,
  `is_completed` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`task_id`),
  KEY `FKbhwpp8tr117vvbxhf5sbkdkc9` (`user_id`),
  CONSTRAINT `FKbhwpp8tr117vvbxhf5sbkdkc9` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_completion`
--

DROP TABLE IF EXISTS `task_completion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_completion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `completed` bit(1) NOT NULL,
  `completion_date` date NOT NULL,
  `activity_id` bigint DEFAULT NULL,
  `task_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKa7k1yxx2ma3vghvtuexr2tbj7` (`task_id`,`completion_date`),
  KEY `FKnijcmr25vminyv97bqsosy8q2` (`activity_id`),
  CONSTRAINT `FK2mq03rtqcy5jvlco1co1fco6a` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`),
  CONSTRAINT `FKnijcmr25vminyv97bqsosy8q2` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_completion`
--

LOCK TABLES `task_completion` WRITE;
/*!40000 ALTER TABLE `task_completion` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_completion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `track`
--

DROP TABLE IF EXISTS `track`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `track` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_date` date NOT NULL,
  `status` enum('DONE','NOT_STARTED') NOT NULL,
  `activity_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `task_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKs9sxcw4kxer1bsk4mtdo805pb` (`activity_id`),
  KEY `FKnqx65i07mb50vffkv4b3nuhei` (`user_id`),
  CONSTRAINT `FKnqx65i07mb50vffkv4b3nuhei` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKsdtvrv0hnm0wxhft4adqstdu4` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `track`
--

LOCK TABLES `track` WRITE;
/*!40000 ALTER TABLE `track` DISABLE KEYS */;
INSERT INTO `track` VALUES (1,'2026-02-02','DONE',17,6,NULL),(2,'2026-02-04','DONE',18,6,NULL),(3,'2026-02-04','DONE',19,6,NULL),(4,'2026-02-04','DONE',20,6,NULL),(5,'2026-02-04','DONE',21,6,NULL);
/*!40000 ALTER TABLE `track` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) NOT NULL,
  `child_ages` varchar(255) NOT NULL,
  `child_names` varchar(255) DEFAULT NULL,
  `num_children` int NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (6,'{bcrypt}$2a$10$J3ak5H3rekdteqDcKBso5uSiW8Qxh.3ksT7HsmM87S86JtV9Vgi.e','Willyou','Abby','10,8,5,4,2,0','Noah,Jordan, Alexis, Samantha, Frank, Jessica',6),(7,'{bcrypt}$2a$10$B7X9bhpZUmb4TqhrmTgU5eq/JXiH0rEgbaTT9kNc4DYHi4.dCQnIe','abigail123','Hannah','5,3,2','Noah,Jacob, Catherine',3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-05  8:40:37
