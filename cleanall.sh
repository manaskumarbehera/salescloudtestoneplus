rm -rf ~/.m2/repository/dk/escapetech/oneplus/salescloud-*
cd /home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-waf-oneplus
../gradlew clean cleanIdea idea install
cd /home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-core
../gradlew clean cleanIdea idea
cd /home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-mobile
../gradlew clean cleanIdea idea
