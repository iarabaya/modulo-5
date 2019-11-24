package com.codeoftheweb.salvo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {

	    SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository ){
		return (args) -> {
			Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov.ar",passwordEncoder().encode("24")));
			Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov",passwordEncoder().encode("42")));
			Player player3 = playerRepository.save(new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb")));
			Player player4 = playerRepository.save(new Player("t.almeida@ctu.gov",passwordEncoder().encode("mole")));

			// to save a couple of players
			//playerRepository.save(new Player("Jack Bauer"));
			//playerRepository.save(new Player("Chloe O'Brian"));
			//playerRepository.save(new Player("Kim Bauer"));
			//playerRepository.save(new Player("Tony Almeida"));

			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date2.toInstant().plusSeconds(3000));

			Game game1 = gameRepository.save(new Game(date1));
			Game game2 = gameRepository.save(new Game(date2));
			Game game3 = gameRepository.save(new Game(date3));

			GamePlayer gp1 = new GamePlayer(date1,game1,player1);
			GamePlayer gp2 = new GamePlayer(date1, game1,player2);

			GamePlayer gp3 = new GamePlayer(date2, game2,player1);
			GamePlayer gp4 = new GamePlayer(date2, game2,player2);

			GamePlayer gp5 = new GamePlayer(date3, game3,player2);
			GamePlayer gp6 = new GamePlayer(date3, game3,player4);

			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);

			gamePlayerRepository.save(gp3);
			gamePlayerRepository.save(gp4);

			gamePlayerRepository.save(gp5);
			gamePlayerRepository.save(gp6);

			List<String> location1 = new ArrayList<>(Arrays.asList("H2","H3","H4"));
			List<String> location2 = new ArrayList<>(Arrays.asList("E1", "F1", "G1"));
			List<String> location3 = new ArrayList<>(Arrays.asList("B4", "B5"));
			List<String> location4 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));

			List<String> location5 = new ArrayList<>(Arrays.asList("F1", "F2"));
			List<String> location6 = new ArrayList<>(Arrays.asList("C6", "C7"));
			List<String> location7 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
			List<String> location8 = new ArrayList<>(Arrays.asList("G6", "H6"));

			Ship ship1 = new Ship(Ship.ShipType.DESTROYER, location1,gp1);
			Ship ship2 = new Ship(Ship.ShipType.SUBMARINE, location2,gp1);
			Ship ship3 = new Ship(Ship.ShipType.PATROL_BOAT, location3,gp1);

			Ship ship4 = new Ship(Ship.ShipType.DESTROYER, location4,gp2);
			Ship ship5 = new Ship(Ship.ShipType.CARRIER, location5,gp2);
			Ship ship6 = new Ship(Ship.ShipType.SUBMARINE, location6,gp2);

			Ship ship7 = new Ship(Ship.ShipType.PATROL_BOAT, location7,gp3);
			Ship ship8 = new Ship(Ship.ShipType.CARRIER, location8,gp3);
			Ship ship9 = new Ship(Ship.ShipType.BATTLESHIP, location1,gp3);

			Ship ship10 = new Ship(Ship.ShipType.BATTLESHIP, location2,gp4);
			Ship ship11 = new Ship(Ship.ShipType.SUBMARINE, location3,gp4);
			Ship ship12 = new Ship(Ship.ShipType.PATROL_BOAT, location4,gp4);

			shipRepository.saveAll(Arrays.asList(ship1,ship2,ship3,ship4,ship5,ship6,ship7,ship8,ship9,ship10,ship11,ship12));

			Salvo salvo1 = new Salvo(1,Arrays.asList("B5","C5","F1"), gp1);
			Salvo salvo2 = new Salvo(2,Arrays.asList("B4","B5","B6"),gp2);
			Salvo salvo3 = new Salvo(1,Arrays.asList("F2","D5"),gp3);
			Salvo salvo4 = new Salvo(2,Arrays.asList("E1", "H3", "A2"),gp4);


			salvoRepository.saveAll(Arrays.asList(salvo1,salvo2,salvo3,salvo4));

			Score sc1 = new Score(1.0,game1,player1);
			Score sc2 = new Score(0.0,game2,player2);
			Score sc3 = new Score(0.5, game3,player4);
			Score sc4 = new Score(0.5, game3,player2);

			scoreRepository.saveAll(Arrays.asList(sc1,sc2,sc3,sc4));


		};
	}

}

@Configuration
	class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

		@Autowired
		PlayerRepository playerRepository;

		@Autowired
		PasswordEncoder passwordEncoder;

		@Override
		public void init(AuthenticationManagerBuilder authentication) throws Exception {
			authentication.userDetailsService(inputName-> {
				Player player = playerRepository.findByUserName(inputName);
				if (player != null) {
					return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("Unknown user: " + inputName);
				}
			});
		}
	}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/api/login", "/api/players").permitAll()
			.antMatchers("/web/games.html", "/web/js/*", "/web/css/*", "/api/games","/api/leaderboard").permitAll()
			.antMatchers("/web/game.html*","/api/*").hasAuthority("USER")
			.anyRequest().denyAll();

		http.formLogin()
			.usernameParameter("username")
			.passwordParameter("password")
			.loginPage("/api/login");
		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}

