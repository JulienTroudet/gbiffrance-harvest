CREATE TABLE `Dataset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `dataPublisher_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB93E4C581289A3B6` (`dataPublisher_id`),
  CONSTRAINT `FKB93E4C581289A3B6` FOREIGN KEY (`dataPublisher_id`) REFERENCES `DataPublisher` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8