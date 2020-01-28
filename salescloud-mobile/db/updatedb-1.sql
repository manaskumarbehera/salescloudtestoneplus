Update `businessarea` set `NAME` = "TDC Erhverv Mobil";

ALTER TABLE `SALESPERSONROLE` ADD COLUMN `AGENT` tinyint(1) NULL DEFAULT '0' AFTER `ID`, ADD COLUMN `AGENT_SA` tinyint(1) NULL DEFAULT '0' AFTER `AGENT`, ADD COLUMN `PARTNER` tinyint(1) NULL DEFAULT '0' AFTER `AGENT_SA`, ADD COLUMN `PARTNER_EC` tinyint(1) NULL DEFAULT '0' AFTER `PARTNER`, MODIFY COLUMN `ADDRESS` varchar(255) NULL AFTER `PARTNER_EC`, MODIFY COLUMN `CITY` varchar(255) NULL AFTER `ADDRESS`, MODIFY COLUMN `COMMENT` varchar(255) NULL AFTER `CITY`, MODIFY COLUMN `COMPANYID` varchar(255) NULL AFTER `COMMENT`, MODIFY COLUMN `COMPANYNAME` varchar(255) NULL AFTER `COMPANYID`, MODIFY COLUMN `EMAIL` varchar(255) NULL AFTER `COMPANYNAME`, MODIFY COLUMN `NAME` varchar(255) NULL AFTER `EMAIL`, MODIFY COLUMN `PHONE` varchar(255) NULL AFTER `NAME`, MODIFY COLUMN `POSITION` varchar(255) NULL AFTER `PHONE`, MODIFY COLUMN `ZIPCODE` varchar(255) NULL AFTER `POSITION`, CHARSET=latin1 COLLATE=latin1_swedish_ci; 

ALTER TABLE `product` ADD COLUMN `PRODUCTID` varchar(40) NULL AFTER `INTERNALNAME`; 
ALTER TABLE `product` ADD COLUMN `BUSINESSAREA_ID` bigint(20) NOT NULL DEFAULT 1394 AFTER `AMOUNTS`; 
update `product` set productId=nabsCode;
update `productgroup` set BUSINESSAREA_ID = 1394;

update SALESPERSONROLE set partner=1 where id in (select id from PARTNERROLE);

ALTER TABLE `bundleproduct` MODIFY COLUMN `PRODUCTID` bigint(20) NOT NULL FIRST, ADD INDEX `FK_bundleproduct_PRODUCTBUNDLEID` (`PRODUCTBUNDLEID`) USING BTREE, DROP PRIMARY KEY, ADD PRIMARY KEY (`PRODUCTID`, `PRODUCTBUNDLEID`) USING BTREE, CHARSET=latin1 COLLATE=latin1_swedish_ci; 

DROP TABLE `PARTNERROLE`;

ALTER TABLE `businessarea` ADD COLUMN `INTROTEXT` varchar(255) NULL AFTER `CUMULATIVEDISCOUNTS`; 

update businessarea set introtext = "TDC Erhverv Mobil er abonnementer til både mindere og større virksomheder med mere værdi for pengene. Abonnementerne er mere end blot MB og Minutter. De giver mulighed for attraktive Pluspakker, der giver kunderne nye værktøjer og en nemmere hverdag.";

update productgroup set businessarea_id = 1394 where businessarea_id is null;

update baserole set DTYPE = "SalespersonRole", ROLENAME = "salesperson" where ROLENAME = "partner";

ALTER TABLE `campaign` MODIFY COLUMN `fromdate` date; 
ALTER TABLE `campaign` MODIFY COLUMN `todate` date; 

alter table pageinfo drop key `PAGEID`;

update `productbundle` set active = 1;
update `productbundle` set bundletype = 0 WHERE bundletype is null;

