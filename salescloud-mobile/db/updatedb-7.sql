KUN PRODUKTION:

ALTER TABLE `campaignproduct` drop column `p_num_values`;
ALTER TABLE `campaignproduct` drop column `p_amounts`;

ALTER TABLE `productbundle` drop column `p_num_values`;
ALTER TABLE `productbundle` drop column `NUM_VALUES`;

KUN STAGING:

ALTER TABLE `campaign` drop column `productText`;


