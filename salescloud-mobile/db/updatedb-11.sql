ALTER TABLE `orderline` ADD COLUMN `count_new` int(11) NULL DEFAULT '0';
ALTER TABLE `orderline` ADD COLUMN `count_existing` int(11) NULL DEFAULT '0';
UPDATE `orderline` set `count_new` = `itemcount`;
ALTER TABLE `orderline` DROP COLUMN `itemcount`;

