package es.viferpar.microservices.apigateway.config;

import java.util.function.Function;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

  @Bean
  public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {

    final Function<PredicateSpec, Buildable<Route>> routeFunction = p -> p.path("/get")
        .filters(
            f -> f.addRequestHeader("MyHeader", "MyUri").addRequestParameter("Param", "MyValue"))
        .uri("http://httpbin.org:80");

    final Function<PredicateSpec, Buildable<Route>> redirectExchange =
        p -> p.path("/currency-exchange/**").uri("lb://currency-exchange");

    final Function<PredicateSpec, Buildable<Route>> redirectConversion =
        p -> p.path("/currency-conversion/**").uri("lb://currency-conversion");

    final Function<PredicateSpec, Buildable<Route>> redirectConversionFeign =
        p -> p.path("/currency-conversion-feign/**").uri("lb://currency-conversion");

    final Function<PredicateSpec, Buildable<Route>> redirectConversionNew =
        p -> p.path("/currency-conversion-new/**")
            .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
                "/currency-conversion-feign/${segment}"))
            .uri("lb://currency-conversion");

    return builder.routes().route(routeFunction).route(redirectExchange).route(redirectConversion)
        .route(redirectConversionFeign).route(redirectConversionNew).build();

  }

}
