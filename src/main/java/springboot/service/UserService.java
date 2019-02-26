package springboot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springboot.domain.User;
import springboot.domain.UserRepository;

@Service("userService")
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	public void addUser(User user) {
		userRepository.save(user);
	}
	
	public Optional<User> getUserById(String id) {
		return userRepository.findById(id);
	}
	
	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public void deleteUserById(String id) {
		userRepository.deleteById(id);
	}

	public void updateUser(String id, User newUser) {
		userRepository.findById(id)
						.map(user -> {
							user.setUsername(newUser.getUsername());
							user.setPassword(newUser.getPassword());
							
							// Use to store the authorization information after create account
							// Modification of username/password do not need to contains token info 
							if(newUser.getAccessToken() != null && newUser.getAccessTokenSecret() != null) {
								user.setAccessToken(newUser.getAccessToken());
								user.setAccessTokenSecret(newUser.getAccessTokenSecret());
							}
							return userRepository.save(user);
						});
	}

	public boolean isExist(String username) {
		return userRepository.existsByUsername(username);
	}

	public User login(User loginUser) {
		Optional<User> refUser = userRepository.findByUsername(loginUser.getUsername());
		User user = null;
		if(!refUser.isPresent()) {
		} else {
			user = refUser.get();
			if(!user.getPassword().equals(loginUser.getPassword())) {
				user = null;
			}
		}
		return user;
	}
}
