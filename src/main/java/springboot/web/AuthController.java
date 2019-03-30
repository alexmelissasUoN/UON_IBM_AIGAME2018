package springboot.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import springboot.domain.User;
import springboot.service.TwitterService;
import springboot.service.UserService;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

@Controller
public class AuthController {
	@Autowired
	private UserService userService;
	@Autowired
	private TwitterService twitterService;

	// TODO furthur auth or unauth
	
	@GetMapping("/auth/{id}")
	protected void auth(HttpServletRequest request, HttpServletResponse response, @PathVariable String id)
			throws IOException {
		if (!checkUser(request, response, id)) {
			return;
		}

		String consumerKey = "kLdFjFhJkiiWMb2SU4ZNtpGlf";
		String consumerSecret = "VUXAlVuDbdOYDGhbImgYOfbX91xqtvSdFnXn3kzM6ZNoOWv6fa";

		// Get request token and token secret
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		RequestToken requestToken = null;

		try {
			requestToken = twitter.getOAuthRequestToken();
			// Store twitter and request token in session
			request.getSession().setAttribute("id", id);
			request.getSession().setAttribute("twitter", twitter);
			request.getSession().setAttribute("requestToken", requestToken);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		// Redirect to authorization page
		response.sendRedirect(requestToken.getAuthorizationURL());
		return;
	}

	@GetMapping("/noauth/{id}")
	public void noauth(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
		if (!checkUser(request, response, id)) {
			return;
		}
		
		User user = userService.getUserById(id);
		if (user.getAccessToken() != null || user.getAccessTokenSecret() != null) {
			user.setAccessToken(null);
			user.setAccessTokenSecret(null);
			userService.updateUser(id, user);
		}
		
		twitterService.withoutTwitter(id);
	}

	public boolean checkUser(HttpServletRequest request, HttpServletResponse response, String id) {
		// check the user
		User user = userService.getUserById(id);
		if (user == null) {
			try {
				request.getRequestDispatcher("/404").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
}
