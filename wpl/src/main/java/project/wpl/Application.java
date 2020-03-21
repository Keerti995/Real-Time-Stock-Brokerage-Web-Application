package project.wpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import project.wpl.repository.BankAccountRepository;
import project.wpl.repository.RegistrationRepository;
import project.wpl.repository.RoleRepository;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Arrays;


@EnableCaching
@EnableJdbcHttpSession
@EnableJpaRepositories("project/wpl/repository")
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	static {
		//for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){

					public boolean verify(String hostname,
										  javax.net.ssl.SSLSession sslSession) {
						if (hostname.equals("localhost")) {
							return true;
						}
						return false;
					}
				});


		/*	System.setProperty("javax.net.ssl.trustStore","c:/apachekeys/client1.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "admin123");
			System.setProperty("javax.net.ssl.keyStore", "c:/apachekeys/client1.jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "admin123");*/




	}

	@Autowired
	RegistrationRepository registrationRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	@Bean
	public CacheManager cacheManager() {
		// configure and return an implementation of Spring's CacheManager SPI
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("pastweek")));
		return cacheManager;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}


	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

}
