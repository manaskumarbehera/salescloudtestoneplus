// Før ny software
DROP table ppr_tag;
DROP table bundleproductrelationtag;

// Efter ny software
UPDATE campaign set DTYPE='MobileCampaign';
UPDATE campaign set ALLOWMIXBUNDLES=1;

UPDATE businessarea set businessAreaId = 1 where name = 'TDC Erhverv Mobil';
UPDATE businessarea set businessAreaId = 2 where name = 'TDC Erhverv Omstilling';

UPDATE productbundle set p_amounts = amounts;
UPDATE productbundle set p_num_values = 3;
UPDATE productbundle set num_values = 3;

UPDATE productbundle set ADDPRODUCTPRICES = 1;

UPDATE bundleproduct set ADDPRODUCTPRICE = 1;

UPDATE `productbundle` set `ADDTOCONTRACTDISCOUNT` = 0 where `ADDTOCONTRACTDISCOUNT` is NULL;
ALTER TABLE `productbundle` drop column `DISCOUNTPRODUCTID`;
ALTER TABLE `productbundle` drop column `DISCOUNTPRODUCTTEXT`;
ALTER TABLE `productbundle` drop column `DISCOUNTKVIKCODE`;
ALTER TABLE `productbundle` drop column `DISCOUNTINTERNALNAME`;