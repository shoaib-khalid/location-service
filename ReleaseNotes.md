##################################################
# location-service-1.0.5 | 31-May-2022
##################################################

1. Create new endpoint parent-category/products
##################################################
# location-service-1.0.4 | 31-May-2022
##################################################

1. Create new endpoint parent-category/stores (get unique store based on location and parent category id)

##################################################
# location-service-1.0.3 | 30-May-2022
##################################################
1. Modify response body for getByQueryProduct , getDisplayStoreConfig

##DB Changes:

CREATE INDEX store_city_IDX
ON store (city);

CREATE INDEX store_postcode_IDX
ON store (postcode);

CREATE INDEX store_regionCountryStateId_IDX
ON store (regionCountryStateId);

##################################################
# location-service-1.0.2 | 27-May-2022
##################################################

1. Add new endpoint /config/location & /config/store
2. Checking any duplicate id in region_city and remove the duplicate id, once you remove the duplicated keys. You may insert the data the one that you remove.

SELECT id, COUNT(id)
FROM region_city
GROUP BY id
HAVING COUNT(id) > 1 ;

##DB Changes:

ALTER TABLE symplified.region_city ADD CONSTRAINT `PRIMARY` PRIMARY KEY (id);

CREATE TABLE `location_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `imageUrl` varchar(300) DEFAULT NULL,
  `cityId` varchar(100) DEFAULT NULL,
  `isDisplay` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `location_config_FK` (`cityId`),
  CONSTRAINT `location_config_FK` FOREIGN KEY (`cityId`) REFERENCES `region_city` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) 

CREATE TABLE `store_display_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `isDisplay` tinyint DEFAULT '0',
  `storeId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
##################################################
# location-service-1.0.1 | 27-May-2022
##################################################

1. Add new endpoint /products-location

##################################################
# location-service-1.0.0 | 25-May-2022
##################################################

1. Add new endpoint /categories-location/child-category and /categories-location/parent-category




