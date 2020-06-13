package com.example.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.model.Catalog;
import com.example.model.CatalogItem;
import com.example.model.Movie;
import com.example.model.Rating;
import com.example.model.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	/*
	@Autowired
	private WebClient.Builder webClientBuilder;
	*/
	@RequestMapping("/{userId}")
	public Catalog getCatalog(@PathVariable("userId") String userId) {
		
		// Hardcoding for now, will get this data from ratingsdata microservice
		//List<Rating> ratings = Arrays.asList(new Rating("1234",4), new Rating("5678",3));
		//No, calling the ratingsdata microservice
		//Now, replace the url, so that it discovers from Eureka server
		//UserRating ratings = restTemplate.getForObject("http://localhost:9093/ratingsdata/users/" + userId, UserRating.class);
		UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
		
		// Call movieinfo microservice for each movieId to get the movie details.
		List<CatalogItem> catalogItem = ratings.getUserRating().stream().map(rating -> {
			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + userId, Movie.class);
			// The above line of RestTemplate will be replaced by the below lines for WebClient
			/*Movie movie = webClientBuilder.build()
							.get()
							.uri("http://localhost:9092/movies/" + userId)
							.retrieve()
							.bodyToMono(Movie.class)
							.block();*/ //block() is used to wait until the response is retrieved and converted to object.
			//The above is still synchronous due to block(). If you want to convert to a completely asynchronous call, you need to modify the return type for this method also. You will need to send the Mono object in that case.
			
			
			System.out.println();
			return new CatalogItem(movie.getName(), "Test Description", rating.getRating());

		}).collect(Collectors.toList());
		
		Catalog catalog = new Catalog();
		catalog.setCatalogItem(catalogItem);
		
		return catalog;
	}

}
