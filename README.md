# AWS AssumeRole using java example

This code gives opportunity to use AWS Java SDK on your laptop for local development.

Assume role in terminal gives opportunity to work with AWS stack in terminal, but not from your favourite IDE.

This is example for java developers in case you want use your IDE on laptop and 
connect to real AWS assets.

## You need

1 Add aws-java-sdk dependencies to the project. For example here I work with AWS AssumeRole and ASW S3:

    compile group: 'com.amazonaws', name: 'aws-java-sdk-sts', version: '1.11.762'
    compile group: 'com.amazonaws', name: 'aws-java-sdk-s3', version: '1.11.762'

2 Use next class to see example of code to write. Feel free to copy-paste:

    com.boyar_roman.aws.assume_role_example.AwsAssumeRoleExample
    
 It contains two important methods:
 - getDefaultCredentials  - to assume-role to AWS using your profileName
 - getAmazonS3Client - to create AWS S3Client 

## Precondition

    1. You should have configured aws assets on your laptop in the folder [~/.aws/]
    2. There is should be profileName(which you want to use) in file ~/.aws/config
    3. Must be set up "assumerole" or "gimme-aws-creds" utility's. (depends on what you use)

### Example steps to use with "gimme-aws-creds" utility

    1. Run in terminal command "gimme-aws-creds" and wait for getting access
    2. Run your code using IDE (no need to export something or build jar)
    3. Be happy with. Go on!
