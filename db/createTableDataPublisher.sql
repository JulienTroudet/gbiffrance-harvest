CREATE TABLE `DataPublisher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `administrativeContact` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `technicalContact` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8