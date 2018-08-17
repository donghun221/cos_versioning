// Copyright 2018 Tencent Inc. or its affiliates. All Rights Reserved.
package com.tencent.cos.versioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.BucketLifecycleConfiguration;
import com.qcloud.cos.model.BucketVersioningConfiguration;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.COSVersionSummary;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.ListVersionsRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.SetBucketLifecycleConfigurationRequest;
import com.qcloud.cos.model.SetBucketVersioningConfigurationRequest;
import com.qcloud.cos.model.VersionListing;
import com.qcloud.cos.region.Region;

import dnl.utils.text.table.TextTable;

public class Operation {
	private static final String SECRET_ID = "your secret Id";
	private static final String SECRET_KEY = "Your secret key";
	private static final String REGION = "Your region";
	private static COSClient CLIENT = null;
	
	static {
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region(REGION));
        CLIENT = new COSClient(cred, clientConfig); 
	}
	
	// Enable bucket versioning
	public static void enableBucketVersion(String bucket) {
		BucketVersioningConfiguration configuration = new BucketVersioningConfiguration().withStatus(BucketVersioningConfiguration.ENABLED);
		
        SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(bucket, configuration);
        CLIENT.setBucketVersioningConfiguration(request);
	}
	
	// Disable bucket versioning
	public static void disableBucketVersion(String bucket) {
		BucketVersioningConfiguration configuration = new BucketVersioningConfiguration().withStatus(BucketVersioningConfiguration.SUSPENDED);
		
        SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(bucket, configuration);
        CLIENT.setBucketVersioningConfiguration(request);
	}

	// Print result as table
	private static void printTable(String[] column, String[][] data) {
		TextTable table = new TextTable(column ,data);
		table.printTable();
	}
	
	public static void listBucketsWithVersionStatus() {
		String[] column = {"Name", "Owner", "CreationDate", "Versioning"};
		List<String[]> dataList = new ArrayList<String[]>();
		
		List<Bucket> bucketList = CLIENT.listBuckets();
		for (Bucket bucket : bucketList) {
			if (bucket.getName().length() > 40 || !CLIENT.doesBucketExist(bucket.getName())) {
				continue;
			}
			
			BucketVersioningConfiguration conf = CLIENT.getBucketVersioningConfiguration(bucket.getName());

			String[] element = {
					bucket.getName(),
					bucket.getOwner().getDisplayName(),
					bucket.getCreationDate().toString(),
					conf.getStatus()};
			dataList.add(element);
		}

		String[][] data = new String[dataList.size()][column.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = dataList.get(i);
		}
		
		printTable(column, data);
	}
	
	public static void listVersionBucket() {
		String[] column = {"Name", "Owner", "CreationDate", "Versioning"};
		List<String[]> dataList = new ArrayList<String[]>();
		
		List<Bucket> bucketList = CLIENT.listBuckets();
		for (Bucket bucket : bucketList) {
			BucketVersioningConfiguration conf = null;
			try {
				conf = CLIENT.getBucketVersioningConfiguration(bucket.getName());
				if (conf.getStatus().equals(BucketVersioningConfiguration.ENABLED)) {
					String[] element = {
							bucket.getName(),
							bucket.getOwner().getDisplayName(),
							bucket.getCreationDate().toString(),
							conf.getStatus()};
					dataList.add(element);	
				}
			} catch (Exception e) {
				continue;
			}
		}

		String[][] data = new String[dataList.size()][column.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = dataList.get(i);
		}
		
		printTable(column, data);
	}
	
	public static void listObject(String bucket) {
		String[] column = {"Key","BucketName", "eTag", "Size", "LastModified", "StorageClass", "Owner"};
		List<String[]> dataList = new ArrayList<String[]>();
		String[][] data = null;
		
		try {
			List<COSObjectSummary> res = CLIENT.listObjects(bucket).getObjectSummaries();
			for (COSObjectSummary obj : res) {
				String[] element = {
						obj.getKey(),
						obj.getBucketName(),
						obj.getETag(),
						obj.getSize() + "",
						obj.getLastModified().toString(),
						obj.getStorageClass(),
						obj.getOwner().getDisplayName()
				};
				dataList.add(element);
			}
			
			data = new String[dataList.size()][column.length];
		} catch (Exception e) {
			data = new String[0][0];
		} finally {
			for (int i = 0; i < data.length; i++) {
				data[i] = dataList.get(i);
			}
			printTable(column, data);
		}
	}
	
	public static void listVersions(String bucket) {
		String[] column = {"Key","VersionID", "DeleteMarker", "LastModified"};
		List<String[]> dataList = new ArrayList<String[]>();
		String[][] data = null;
		
		ListVersionsRequest request = new ListVersionsRequest()
				.withBucketName(bucket)
				.withMaxResults(500);
		
		VersionListing res = CLIENT.listVersions(request);
        int numVersions = 0, numPages = 0;
        
        while(true) {
            numPages++;
            for (COSVersionSummary objectSummary : res.getVersionSummaries()) {
            		String[] element = {objectSummary.getKey(), 
                            objectSummary.getVersionId(),
                            objectSummary.isDeleteMarker() + "",
                            objectSummary.getLastModified().toString()};
            		dataList.add(element);
                numVersions++;
            }
            // Check whether there are more pages of versions to retrieve. If
            // there are, retrieve them. Otherwise, exit the loop.
            if (res.isTruncated()) {
                res = CLIENT.listNextBatchOfVersions(res);
            } else {
                break;
            }
        }
        
		data = new String[dataList.size()][column.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = dataList.get(i);
		}
		printTable(column, data);
	}
	
	public static void deleteVersion(String bucket, String key, String version) {
		CLIENT.deleteVersion(bucket, key, version);
	}
	
	public static void restoreVersion(String bucket, String key, String version) {
		CopyObjectRequest req = new CopyObjectRequest(bucket, key, bucket, key);
		req.setSourceVersionId(version);
		ObjectMetadata meta = new ObjectMetadata();
		req.setNewObjectMetadata(meta);
		CLIENT.copyObject(req);
	}

	public static void setVersionLifecycle(String bucket) {
		BucketLifecycleConfiguration conf = new BucketLifecycleConfiguration();
        BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule()
                .withId("delete non current version older than 90 days")
                .withNoncurrentVersionExpirationInDays(90)
                .withExpiredObjectDeleteMarker(true)
                .withStatus(BucketLifecycleConfiguration.ENABLED);
        conf.setRules(Arrays.asList(rule));
		SetBucketLifecycleConfigurationRequest req = new SetBucketLifecycleConfigurationRequest(bucket, conf);
		CLIENT.setBucketLifecycleConfiguration(req);
	}
	
	public static void getVersionLifecycle(String bucket) {
		BucketLifecycleConfiguration conf = CLIENT.getBucketLifecycleConfiguration(bucket);
		
		String[] column = {"Bucket", "Rule Count", "NoncurrentVersionExpirationDate", "deleteMarkerIfOnlyHasMarker"};		
		BucketLifecycleConfiguration.Rule rule = conf.getRules().get(0);
		String[][] data = {{bucket, conf.getRules().size() + "", rule.getNoncurrentVersionExpirationInDays() + "", rule.isExpiredObjectDeleteMarker() + ""}};
		printTable(column, data);
	}
}
