package it.dstech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.dstech.models.Activity;
import it.dstech.models.Role;
import it.dstech.models.User;
import it.dstech.repository.RoleRepository;
import it.dstech.repository.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User save(UserRegistrationDao registration) {
		User user = new User();
		user.setEmail(registration.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(registration.getPassword()));
		try {
			user.setImage(registration.getImage().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Role userRole = roleRepo.findByRole("USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		return userRepository.save(user);
	}

	@Override
	public void deleteById(Long userId) {
		userRepository.deleteById(userId);

	}

	@Override
	public void addActivities(User user, Activity activities) {
		user.getActivities().add(activities);
		userRepository.save(user);
	}

}
