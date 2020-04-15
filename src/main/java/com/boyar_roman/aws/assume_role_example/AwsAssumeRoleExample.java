package com.boyar_roman.aws.assume_role_example;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.internal.AllProfiles;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.auth.profile.internal.BasicProfileConfigLoader;
import com.amazonaws.auth.profile.internal.ProfileAssumeRoleCredentialsProvider;
import com.amazonaws.auth.profile.internal.ProfileStaticCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.STSProfileCredentialsServiceLoader;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AwsAssumeRoleExample {
    private final static String PROFILE_NAME = "profile_name_in_~/.aws/config";
    private final static String REGION = "region_for_your_purpose";
    private final static String BUCKET_NAME = "bucket_name";

    public static void main(String[] args) {
        AwsAssumeRoleExample assumeRoleExample = new AwsAssumeRoleExample();

        AmazonS3 amazonS3Client = assumeRoleExample.getAmazonS3Client(PROFILE_NAME, REGION);

        //next code -- only example
        for (S3ObjectSummary s3ObjectSummary : amazonS3Client.listObjects(BUCKET_NAME).getObjectSummaries()) {
            System.out.println(s3ObjectSummary.getKey());
        }
    }

    //Here is creating AmazonS3Client
    private AmazonS3 getAmazonS3Client(String profileName, String region) {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                        getDefaultCredentials(profileName)
                )
                .withRegion(region)
                .build();
    }

    //Here is assume-role actually
    private AWSCredentialsProvider getDefaultCredentials(String profileName) {
        final AllProfiles allProfiles = new AllProfiles(Stream
                .concat(
                        BasicProfileConfigLoader.INSTANCE
                                .loadProfiles(AwsProfileFileLocationProvider.DEFAULT_CONFIG_LOCATION_PROVIDER.getLocation())
                                .getProfiles()
                                .values()
                                .stream(),
                        BasicProfileConfigLoader.INSTANCE
                                .loadProfiles(AwsProfileFileLocationProvider.DEFAULT_CREDENTIALS_LOCATION_PROVIDER.getLocation())
                                .getProfiles()
                                .values()
                                .stream()
                )
                .map(profile -> new BasicProfile(
                        profile.getProfileName().replaceFirst("^profile ", ""),
                        profile.getProperties())
                )
                .collect(
                        Collectors.toMap(
                                profile -> profile.getProfileName(),
                                profile -> profile,
                                (left, right) -> {
                                    final Map<String, String> properties = new HashMap<>(left.getProperties());
                                    properties.putAll(right.getProperties());
                                    return new BasicProfile(left.getProfileName(), properties);
                                }
                        )
                )
        );

        final BasicProfile profile = Optional
                .ofNullable(allProfiles.getProfile(profileName))
                .orElseThrow(() -> new RuntimeException(
                        String.format("Profile '%s' not found in %s", profileName, allProfiles.getProfiles().keySet()))
                );
        if (profile.isRoleBasedProfile()) {
            return new ProfileAssumeRoleCredentialsProvider(
                    STSProfileCredentialsServiceLoader.getInstance(),
                    allProfiles,
                    profile
            );
        } else {
            return new ProfileStaticCredentialsProvider(profile);
        }
    }
}
