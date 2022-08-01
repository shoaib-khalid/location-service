##################################################
# location-service-1.0.22 | 29-July-2022
##################################################
Change store search sql using predicate
##################################################
# location-service-1.0.21 | 29-July-2022
##################################################
Change product search sql using predicate

##################################################
# location-service-1.0.19 | 19-July-2022
##################################################
1. Add request param isMainLevel for get featured product
##################################################
# location-service-1.0.18 | 13-July-2022
##################################################
1. Code refactoring and chnage query for trending products
##################################################
# location-service-1.0.17 | 6-July-2022
##################################################
1. Fix parent category asset url
###################################
##################################################
# location-service-1.0.16 | 5-July-2022
##################################################
1. Hnadling null for asset url
##################################################
# location-service-1.0.15 | 5-July-2022
##################################################
1. set asset service url
2. change config 

##Config changes 
asset.service.url = https://assets.symplified.it //inject production asset service url

##DB CHANGES :

	UPDATE location_config 
	SET imageUrl = REPLACE(imageUrl, 'https://symplified.it', '') 
	WHERE imageUrl LIKE '%https://symplified.it%';

  UPDATE location_config 
	SET imageUrl = REPLACE(imageUrl, 'https://symplified.biz', '') 
	WHERE imageUrl LIKE '%https://symplified.biz%';

##################################################
# location-service-1.0.14 | 1-July-2022
##################################################
1. Add snooze in all endpoint related to store details
##################################################
# location-service-1.0.13 | 1-July-2022
##################################################
1. Add snooze in feature store

##################################################
# location-service-1.0.12 | 27-June-2022
##################################################
1. New endpoint for product trending

##DB CHANGES :

    CREATE INDEX product_seoName_IDX USING BTREE ON symplified.product (seoName);
    
##################################################
# location-service-1.0.11 | 24-June-2022
##################################################
1. Code refactoring




##################################################
# location-service-1.0.10 | 24-June-2022
##################################################
1. Products endpoint shows product that have picture first

##################################################
# location-service-1.0.9 | 7-June-2022
##################################################
1. Modify query param city id in array for endpoint categories/parent-categories , featured/store, featured/product , /product , /store

##################################################
# location-service-1.0.8 | 7-June-2022
##################################################

1. Create new endpoint for /location-area

##DB CHANGES :

CREATE TABLE `location_area` (
  `userLocationCityId` varchar(100) DEFAULT NULL,
  `storeCityId` varchar(100) DEFAULT NULL,
  KEY `userLocationCityId` (`userLocationCityId`),
  CONSTRAINT `location_area_ibfk_1` FOREIGN KEY (`userLocationCityId`) REFERENCES `region_city` (`id`)
);

INSERT INTO `location_area` (`userLocationCityId`,`storeCityId`) VALUES
('Sunway','SubangJaya'),
('Bangi','BandarBaruBangi'),
('Kajang','BandarBaruBangi'),
('Putrajaya','BandarBaruBangi'),
('Cyberjaya','BandarBaruBangi'),
('BatuCaves','SelayangJaya'),
('Selayang','SelayangJaya'),
('SriGombak','SelayangJaya'),
('BatuCaves','Selayang'),
('SriGombak','Selayang'),
('SubangJaya','Puchong'),
('Sunway','Puchong'),
('Puchong','PetalingJaya'),
('Sunway','PetalingJaya'),
('Bangi','Kajang'),
('Bangi','Dengkil'),
('Putrajaya','Dengkil'),
('Cyberjaya','Dengkil'),
('Bangi','Cyberjaya'),
('Putrajaya','Cyberjaya'),
('Selayang','BatuCaves'),
('SriGombak','BatuCaves'),
('Bangi','BandarSeriPutra'),
('Kajang','BandarSeriPutra'),
('Putrajaya','BandarSeriPutra'),
('Cyberjaya','BandarSeriPutra'),
('Kajang','Bangi'),
('Putrajaya','Bangi'),
('Cyberjaya','Bangi');

##################################################
# location-service-1.0.7 | 3-June-2022
##################################################

1. Add new endpoint featured/product


##################################################
# location-service-1.0.6 | 2-June-2022
##################################################

1. Change endpoint /categories/
2. Change endpoint /featured/

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

* WARNING : Please update database in store table city column , make sure the city is exactly with region_city(id). Once update , can proceed with adding foreign key.

ALTER TABLE symplified.store ADD CONSTRAINT store_FK FOREIGN KEY (city) REFERENCES symplified.region_city(id);

##################################################
# location-service-1.0.2 | 27-May-2022
##################################################

1. Add new endpoint /config/location & /config/store
2. Checking any duplicate id in region_city and remove the duplicate id, once you remove the duplicated keys. You may insert the data the one that you remove.

-- Checking any duplicate id, then remove one of the id,

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
  `sequence` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `location_config_FK` (`cityId`),
  CONSTRAINT `location_config_FK` FOREIGN KEY (`cityId`) REFERENCES `region_city` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)

CREATE TABLE `store_display_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `isDisplay` tinyint DEFAULT '0',
  `storeId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `store_display_config_FK` (`storeId`),
  CONSTRAINT `store_display_config_FK` FOREIGN KEY (`storeId`) REFERENCES `store` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)

NOTE : Please change imageUrl for production

INSERT INTO location_config (imageUrl, cityId, isDisplay, `sequence`) VALUES
('https://symplified.it/store-assets/Subang_Jaya_320x208.png', 'SubangJaya', 1, 8),
('https://symplified.it/store-assets/Petalin%20Jaya_320x208.png', 'PetalingJaya', 1, 7),
('https://symplified.it/store-assets/Putrajaya_320x208.png', 'Putrajaya', 1, 2),
('https://symplified.it/store-assets/Bangi_320x208.png', 'BandarBaruBangi', 1, 1),
('https://symplified.it/store-assets/Batu_Caves_320x208.png', 'BatuCaves', 1, 5),
('https://symplified.it/store-assets/Cyberjaya_320x208.png', 'Cyberjaya', 1, 3),
('https://symplified.it/store-assets/Selayang_320x208.png', 'Selayang', 1, 4),
('https://symplified.it/store-assets/Sri_Gombak_320x208.png', 'SriGombak', 1, 6),
('https://symplified.it/store-assets/Sunway_320x208.png', 'Sunway', 1, 9),
('https://symplified.it/store-assets/Puchong_320x208.png', 'Puchong', 1, 10),
('https://symplified.it/store-assets/Kajang_320x208.png', 'Kajang', 1, 11);

##################################################
# location-service-1.0.1 | 27-May-2022
##################################################

1. Add new endpoint /products-location

##################################################
# location-service-1.0.0 | 25-May-2022
##################################################

1. Add new endpoint /categories-location/child-category and /categories-location/parent-category




