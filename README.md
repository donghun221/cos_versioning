# How to run it?

1: Down load project

2: cd to project directory and run mvn package

3: run command 
E.g: java -jar target/demo.jar -listVersion demo-1252246555

# Supported Command
 - listBucket
 - listObject [bucketName]
 - enableBucketVersion [bucketName]
 - disableBucketVersion [bucketName]
 - listVersion [bucketName]
 - deleteVersion [bucketName] [keyName] [VersionId]
 - restoreVersion [bucketName] [keyName] [VersionId]
 - setVersionLiftcycle [bucketName]
 - getVersionLifecycle [bucketName]
 
# Running example
 - java -jar target/demo.jar -[command] [args]
