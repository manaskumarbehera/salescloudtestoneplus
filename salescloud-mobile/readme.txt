Sådan overføres database fra produktion til test server:

Lav først backup på serveren: https://mariadb102184-salescloud.jelastic.dogado.eu

Filen gemmes i Downloads folder, f.eks. som scprod(18).sql
Kør scriptet copy_db_to_staging_server.sh med 18 som argument.

På test serveren:
sudo mysql -u root -p

drop database scprod
CREATE DATABASE scprod CHARACTER SET utf8 COLLATE utf8_bin;

sudo mysql -u root -pposi10v scprod < /tmp/scprod\ \(18\).sql

