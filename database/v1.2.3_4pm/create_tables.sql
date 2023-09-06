
CREATE TABLE `orders` (
                          `total_price` decimal(38,2) DEFAULT '0.00',
                          `created_dt` datetime(6) DEFAULT NULL,
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `order_date` datetime(6) NOT NULL,
                          `orders_account_id` bigint DEFAULT NULL,
                          `orders_shipping_address_id` bigint DEFAULT NULL,
                          `updated_dt` datetime(6) DEFAULT NULL,
                          `order_number` varchar(255) NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UK_facafmsdjis54fumvjed88yvu` (`orders_account_id`),
                          UNIQUE KEY `UK_poyi7irlwb6a8xoqmeyi80tam` (`orders_shipping_address_id`),
                          CONSTRAINT `FKpv2wsbgdlf05tcymwhhld5ikj` FOREIGN KEY (`orders_shipping_address_id`) REFERENCES `orders_address` (`id`),
                          CONSTRAINT `FKsr8i3b6omt0pwx7j828271ld3` FOREIGN KEY (`orders_account_id`) REFERENCES `orders_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_account` (
                                  `created_dt` datetime(6) DEFAULT NULL,
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `updated_dt` datetime(6) DEFAULT NULL,
                                  `email_address` varchar(200) NOT NULL DEFAULT '',
                                  `first_name` varchar(200) NOT NULL DEFAULT '',
                                  `last_name` varchar(200) NOT NULL DEFAULT '',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `UK_k732ux7cwqdx4etbqrsce2ar5` (`email_address`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_address` (
                                  `is_shipping_address` bit(1) NOT NULL,
                                  `state` varchar(2) NOT NULL DEFAULT '',
                                  `created_dt` datetime(6) DEFAULT NULL,
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `updated_dt` datetime(6) DEFAULT NULL,
                                  `postal_code` varchar(10) NOT NULL DEFAULT '',
                                  `address1` varchar(200) NOT NULL DEFAULT '',
                                  `address2` varchar(200) DEFAULT '',
                                  `city` varchar(200) NOT NULL DEFAULT '',
                                  `country` varchar(100) NOT NULL DEFAULT '',
                                  `province` varchar(200) DEFAULT '',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `order_line_items` (
                                    `price` decimal(38,2) NOT NULL,
                                    `quantity` int NOT NULL,
                                    `total_price` decimal(38,2) DEFAULT NULL,
                                    `created_dt` datetime(6) DEFAULT NULL,
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `order_id` bigint DEFAULT NULL,
                                    `product_id` bigint NOT NULL,
                                    `updated_dt` datetime(6) DEFAULT NULL,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_account_addresses` (
                                            `addresses_id` bigint NOT NULL,
                                            `orders_account_id` bigint NOT NULL,
                                            PRIMARY KEY (`addresses_id`,`orders_account_id`),
                                            UNIQUE KEY `UK_tevlbknu2xuqw3gclj1u4ftko` (`addresses_id`),
                                            KEY `FK4feipnevrcgykl8mvshgcqqiu` (`orders_account_id`),
                                            CONSTRAINT `FK4feipnevrcgykl8mvshgcqqiu` FOREIGN KEY (`orders_account_id`) REFERENCES `orders_account` (`id`),
                                            CONSTRAINT `FK63oenx5nqjksqlo0m4y9qx4mv` FOREIGN KEY (`addresses_id`) REFERENCES `orders_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_order_line_items` (
                                           `order_line_items_id` bigint NOT NULL,
                                           `orders_id` bigint NOT NULL,
                                           PRIMARY KEY (`order_line_items_id`,`orders_id`),
                                           UNIQUE KEY `UK_o61x047ux9nbd024nnx3ic8af` (`order_line_items_id`),
                                           KEY `FKl3pvnok4r4sd35u5nikhfmp9p` (`orders_id`),
                                           CONSTRAINT `FKl3pvnok4r4sd35u5nikhfmp9p` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`),
                                           CONSTRAINT `FKm0e5kk32751gt5iqn11vdbipv` FOREIGN KEY (`order_line_items_id`) REFERENCES `order_line_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_account_addresses` (
                                            `addresses_id` bigint NOT NULL,
                                            `orders_account_id` bigint NOT NULL,
                                            PRIMARY KEY (`addresses_id`,`orders_account_id`),
                                            UNIQUE KEY `UK_tevlbknu2xuqw3gclj1u4ftko` (`addresses_id`),
                                            KEY `FK4feipnevrcgykl8mvshgcqqiu` (`orders_account_id`),
                                            CONSTRAINT `FK4feipnevrcgykl8mvshgcqqiu` FOREIGN KEY (`orders_account_id`) REFERENCES `orders_account` (`id`),
                                            CONSTRAINT `FK63oenx5nqjksqlo0m4y9qx4mv` FOREIGN KEY (`addresses_id`) REFERENCES `orders_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders_order_line_items` (
                                           `order_line_items_id` bigint NOT NULL,
                                           `orders_id` bigint NOT NULL,
                                           PRIMARY KEY (`order_line_items_id`,`orders_id`),
                                           UNIQUE KEY `UK_o61x047ux9nbd024nnx3ic8af` (`order_line_items_id`),
                                           KEY `FKl3pvnok4r4sd35u5nikhfmp9p` (`orders_id`),
                                           CONSTRAINT `FKl3pvnok4r4sd35u5nikhfmp9p` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`),
                                           CONSTRAINT `FKm0e5kk32751gt5iqn11vdbipv` FOREIGN KEY (`order_line_items_id`) REFERENCES `order_line_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;