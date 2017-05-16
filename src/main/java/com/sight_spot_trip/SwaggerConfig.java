package com.sight_spot_trip;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//see https://github.com/springfox/springfox/blob/master/docs/asciidoc/getting_started.adoc#springfox-spring-data-rest
//@Import({springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class})
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.select()
//				.apis(Predicates.and(
//						Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")), 
//						withClassAnnotations(RepositoryRestResource.class, RepositoryRestController.class)))
				.apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
				.paths(Predicates.not(PathSelectors.regex("/error.*")))
				.build()
				.tags(new Tag("untagged", "This is untagged"), tags())
				.apiInfo(apiInfo())
				.protocols(protocols())
				.securitySchemes(securitySchemes())
				.securityContexts(securityContexts());
	}
	

	// Had to create this because needed an `or` on the annotations
	@SafeVarargs
	private final Predicate<RequestHandler> withClassAnnotations(final Class<? extends Annotation>... annotation) {
		return input -> Arrays.stream(annotation)
				.anyMatch(aClass -> input.declaringClass().isAnnotationPresent(aClass));
	}
	
	private Tag[] tags() {
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("sightspot", "Sight Spot Trip operation"));
		return tags.toArray(new Tag[tags.size()]);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("sightspot-API Swagger Documentation")
				.description("sightspot-API Swagger Documentation")
				.termsOfServiceUrl("http://liudonghua.com")
				.contact(new Contact("liudonghua", "liudonghua.com", "liudonghua123@gmail.com"))
				.license("Apache License Version 2.0")
				.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
				.version("2.0")
				.build();
	}
	
	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add("http");
		return protocols;
	}
	
	private List<? extends SecurityScheme> securitySchemes() {
        List<SecurityScheme> authorizationTypes = Arrays.asList(new ApiKey("token", "token", "query"));
        return authorizationTypes;
    }
	
	private List<SecurityContext> securityContexts() {
		List<SecurityContext> securityContexts   = Arrays.asList(SecurityContext.builder().forPaths(Predicates.not(PathSelectors.regex("^(/error.*|/api/auth/login)$"))).securityReferences(securityReferences()).build());
		return securityContexts;
	}
	
	private List<SecurityReference> securityReferences() {
		List<SecurityReference> securityReferences = Arrays.asList(SecurityReference.builder().reference("token").scopes(new AuthorizationScope[0]).build());
		return securityReferences;
	}
	
}