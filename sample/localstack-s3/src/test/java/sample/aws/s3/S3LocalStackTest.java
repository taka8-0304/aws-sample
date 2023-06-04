package sample.aws.s3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sample.aws.dns.TestHostResolverCustomizer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3LocalStackTest {

	private static final Logger log = LoggerFactory.getLogger(S3LocalStackTest.class);

	@Test
	public void testPathFormat() {
		final var bucketName = "sample-bucket-p";
		var client = S3Client.builder().endpointOverride(URI.create("http://localhost:4566")).forcePathStyle(true)
				.build();
		this.createBucket(client, bucketName);
		this.putObject(client, bucketName, "test.txt", "test for path style");
		var stored = this.getObject(client, bucketName, "test.txt");
		log.info("testPathFormat stored=<{}>", stored);
	}

	@Test
	public void testVirtualHostFormatFail() {
		var exception = Assertions.assertThrows(S3Exception.class, () -> {
			final var bucketName = "sample-bucket-v1";
			TestHostResolverCustomizer.addHostAliases(Map.of(bucketName + ".localhost", "127.0.0.1"));
			var client = S3Client.builder().endpointOverride(URI.create("http://localhost:4566")).forcePathStyle(false)
					.build();
			this.createBucket(client, bucketName);
			this.putObject(client, bucketName, "test.txt", "test for path style");
			var stored = this.getObject(client, bucketName, "test.txt");
			log.info("testPathFormat stored=<{}>", stored);
		});
		log.info("testVirtualHostFormatFail ERROR", exception);
	}

	@Test
	public void testVirtualHostFormatSuccess() {
		final var bucketName = "sample-bucket-v2";
		TestHostResolverCustomizer.addHostAliases(Map.of(bucketName + ".s3.localhost.localstack.cloud", "127.0.0.1"));
		var client = S3Client.builder().endpointOverride(URI.create("http://s3.localhost.localstack.cloud:4566"))
				.forcePathStyle(false).build();
		this.createBucket(client, bucketName);
		this.putObject(client, bucketName, "test.txt", "test for path style");
		var stored = this.getObject(client, bucketName, "test.txt");
		log.info("testPathFormat stored=<{}>", stored);
	}

	private void createBucket(S3Client client, String bucketName) {
		client.createBucket(b -> b.bucket(bucketName));
	}

	private void putObject(S3Client client, String bucketName, String key, String value) {
		client.putObject(b -> {
			b.bucket(bucketName).key(key);
		}, RequestBody.fromString(value));
	}

	private String getObject(S3Client client, String bucketName, String key) {
		try (var out = new ByteArrayOutputStream()) {
			client.getObject(b -> {
				b.bucket(bucketName).key(key);
			}).transferTo(out);
			out.flush();
			return out.toString();
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to get object for <" + bucketName + ":" + key + ">.");
		}
	}

}
