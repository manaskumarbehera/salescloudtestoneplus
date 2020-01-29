cd /home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-waf-oneplus
../gradlew clean cleanEclipse eclipse --refresh-dependencies install
cd /home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-core
../gradlew clean cleanEclipse eclipse --refresh-dependencies install
cd /home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-mobile
../gradlew clean cleanEclipse eclipse --refresh-dependencies war
