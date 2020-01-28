ALTER TABLE `productbundle` CHANGE `NABSCODE` `productId` varchar (40) ; 
ALTER TABLE `product` DROP `NABSCODE`; 
update product set InternalName = 'Rabat pris voice Central' where id in (1447, 19458);
update product set productId = 'GTDCWNORT' where id = 1445;
update product set productId = '3238100' where id = 19456;
update product set productId = '3238200' where id = 19457;
update product set productId = '3238300' where id = 19458;
