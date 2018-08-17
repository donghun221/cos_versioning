# How to run it?

1: Down load project

2: cd to project directory and run mvn package

3: run command 
E.g: java -jar target/demo.jar -listVersion demo-1252246555

# Supported Command
 - listBucket
 - listObject [bucket name as args]
 - enableBucketVersion [bucket name as args]
 - disableBucketVersion [bucket name as args]
 - listVersion [bucket name as args]
 - deleteVersion [bucketName] [keyName] [VersionId]
 - restoreVersion [bucketName] [keyName] [VersionId]
 - setVersionLiftcycle [bucketName]
 - getVersionLifecycle [bucketName]
