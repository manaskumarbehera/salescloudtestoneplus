cd /home/jan/dev/java/projects/jyskit/salescloud/salescloud-waf
gradle clean cleanEclipse eclipse --refresh-dependencies install
cd /home/jan/dev/java/projects/jyskit/salescloud/salescloud-core
gradle clean cleanEclipse eclipse --refresh-dependencies install
cd /home/jan/dev/java/projects/jyskit/salescloud/salescloud-mobile
gradle clean cleanEclipse eclipse --refresh-dependencies war
