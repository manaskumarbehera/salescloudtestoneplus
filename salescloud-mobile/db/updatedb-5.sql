ALTER TABLE `product` DROP COLUMN `EDITABLE`;
ALTER TABLE `product` DROP COLUMN `INSPREADSHEET`;
ALTER TABLE `product` DROP COLUMN `INSPREADSHEETONLYIFSELECTED`;

ALTER TABLE `productgroup` ADD COLUMN `DTYPE` varchar(31) DEFAULT NULL AFTER `ID`;
ALTER TABLE `productgroup` DROP `FULLPATHSORTINDEX`; 
UPDATE `productgroup` set `DTYPE`='MobileProductGroup';

ALTER TABLE `businessarea` MODIFY COLUMN `INTROTEXT` longtext; 

ALTER TABLE `contract` ADD COLUMN `OFFERINTROTEXT` varchar(1000) DEFAULT NULL AFTER `CONTRACTLENGTH`;

update contract set OFFERINTROTEXT = 'Tak for en behagelig samtale.\n\nSom lovet sender jeg dig her et tilbud på TDC Erhverv Mobil.\n\nDette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en mobilløsning, tilpasset præcis jeres virksomhed.\n\nHvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\nVenlig hilsen'
where business_area_id = 1394

update contract set OFFERINTROTEXT = 'Tak for en behagelig samtale.\n\nSom lovet sender jeg dig her et tilbud på TDC Erhverv Omstilling.\n\nDette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en mobilløsning, tilpasset præcis jeres virksomhed.\n\nHvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\nVenlig hilsen'
where business_area_id = 19401
