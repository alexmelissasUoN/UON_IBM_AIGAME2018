package springboot.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;

import springboot.service.IdealService;
import springboot.service.PersonalityInsightService;
import springboot.service.PlayerService;
import springboot.service.UserService;
import springboot.util.ContentLoader;
import springboot.util.PlayerConfig;
import springboot.domain.Ideal;
import springboot.domain.Player;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@RestController
public class AnalysisController {
	@Autowired
	private PersonalityInsightService piService;
	@Autowired
	private UserService userService;
	@Autowired
	private IdealService idealService;
	@Autowired
	private PlayerService playerService;
	
	
    @RequestMapping("/analysis")
    public String result(HttpServletRequest request) {
    	// Store Json result to database
    	ContentLoader contentLoader = (ContentLoader) request.getSession().getAttribute("content");
    	Profile profile = piService.analysis(contentLoader);
    	
    	String id = (String) request.getSession().getAttribute("id");
    	String characterName = userService.getUserById(id).getUsername();
    	Ideal ideal = new Ideal(id);
    	Player player = new Player(id);
    	player.setAttributes(PlayerConfig.getBasicStatus(1));
    	player.setCharacterName(characterName);
    	
    	if(profile.getWordCount() == null || profile.getWordCount() <= 100) {
    	} else {
        	ideal.setJsonResult(profile.toString().replace("\n", "").replace("      ", " "));
    	}

    	idealService.addIdeal(ideal);
    	playerService.addPlayer(player);
    	
    	// TODO exception handling
    	return "Saved";
    }
    
    // Authorization callback
    @GetMapping("/tweets")
	public void analysis(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	// Get the tweets of user
		String id = (String) request.getSession().getAttribute("id");
    	Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
		RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
		String verifier = request.getParameter("oauth_verifier");
		
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			// Twitter User
			User user = twitter.verifyCredentials();
			
			// STORE TOKEN
			springboot.domain.User newUser = userService.getUserById(id);
			newUser.setAccessToken(accessToken.getToken());
			newUser.setAccessTokenSecret(accessToken.getTokenSecret());
			userService.updateUser(id, newUser);
			
//			user = twitter.showUser(accessToken.getUserId());
//			System.out.println(user);
			
			// Get tweets and add text into ContentLoader
			ContentLoader contentLoader = new ContentLoader();
			List<Status> statuses = twitter.getUserTimeline();			
			System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                contentLoader.addInput(status.getText());
            }
            
            request.getSession().setAttribute("content", contentLoader);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		response.sendRedirect(request.getContextPath() + "/analysis");
		return;
	}
}
