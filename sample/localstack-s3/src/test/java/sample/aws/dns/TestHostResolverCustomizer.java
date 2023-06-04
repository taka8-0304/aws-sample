package sample.aws.dns;

import java.util.Map;

import org.burningwave.core.assembler.StaticComponentContainer.Configuration;
import org.burningwave.tools.net.HostResolutionRequestInterceptor;
import org.burningwave.tools.net.MappedHostResolver;

public class TestHostResolverCustomizer {

	static {
		Configuration.Default.setFileName("burningwave.static.test.properties");
	}

	public static void addHostAliases(Map<String, String> hostAliases) {
		HostResolutionRequestInterceptor.INSTANCE.install(new MappedHostResolver(hostAliases));
	}

}
