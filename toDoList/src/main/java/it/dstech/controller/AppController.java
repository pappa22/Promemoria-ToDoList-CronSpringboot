package it.dstech.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.dstech.models.Activity;
import it.dstech.models.RunnableImpl;
import it.dstech.models.User;
import it.dstech.service.ActivityService;
import it.dstech.service.MailService;
import it.dstech.service.UserRegistrationDao;
import it.dstech.service.UserService;

@MultipartConfig
@Controller
public class AppController {

	@Autowired
	private UserService userService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private TaskScheduler scheduler;

	@Autowired
	private MailService mailService;
	
	private Logger logger = LoggerFactory.getLogger(AppController.class);

	@GetMapping(value = { "/login", "/" })
	public ModelAndView login() {
		ModelAndView model = new ModelAndView();
		model.setViewName("login");
		return model;
	}

//	@ModelAttribute("user")
	public UserRegistrationDao userRegistrationDto() {
		return new UserRegistrationDao();
	}

//	    @GetMapping
//	    public String showRegistrationForm(Model model) {
//	        return "registration";
//	    }
	@GetMapping(value = "/registrati")
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("user", userRegistrationDto());
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@PostMapping(value = "/registration")
	public ModelAndView registerUserAccount( @Valid UserRegistrationDao userDao,
			BindingResult result) throws MessagingException, IOException {
		ModelAndView model = new ModelAndView();
		logger.info(String.format("userD %s", userDao.getEmail()));
		User userEsistente = userService.findByEmail(userDao.getEmail());
		if (userEsistente != null) {
			result.rejectValue("email", null, "Utente già presente con questa email");
		}

		if (result.hasErrors()) {
			model.setViewName("error");
			return model;
		}

		userService.save(userDao);
		mailService.inviaMail(userDao.getEmail(), "Registrazione", "User registrato con successo");
		model.addObject("messaggio", "User registrato con successo");
		model.setViewName("login");
		return model;
	}

	@GetMapping(value = "/user/home")
	public ModelAndView userIndex(Activity activity) throws MessagingException {
		ModelAndView model = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		List<Activity> activities = user.getActivities();
		model.addObject("emailUser", user.getEmail());
		model.addObject("imageUser", Base64.getEncoder().encodeToString(user.getImage()));
		model.addObject("listaActivities", activities);
		model.addObject("activity", new Activity());
		model.setViewName("user/homepage");
		return model;
	}

	@PostMapping(value = "/save")
	public ModelAndView save(@ModelAttribute Activity activity, RedirectAttributes redirectAttributes) {
		ModelAndView model = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		activity.setUser(user);
		Activity currActivity = activityService.save(activity);
		userService.addActivities(user, currActivity);
		if (currActivity != null) {
			LocalDateTime date = currActivity.getExpiredDate();
			int minute = date.getMinute();
			int hours = date.getHour();
			int day = date.getDayOfMonth();
			int month = date.getMonth().getValue();
			String expression = "";
			if ((minute - 5) < 0) {
				expression += " 0 " + (minute + 5) + " " + (hours - 1) + " " + day + " " + month + " ?";
				if ((hours - 1) < 0) {
					expression += " 0 " + (minute + 5) + " " + (hours + 23) + " " + day + " " + month + " ?";
				}
			} else {
				expression += " 0 " + (minute - 5) + " " + hours + " " + day + " " + month + " ?";
			}
			CronTrigger trigger = new CronTrigger(expression, TimeZone.getTimeZone(TimeZone.getDefault().getID()));
			RunnableImpl runnable = new RunnableImpl(currActivity, mailService);
			scheduler.schedule(runnable, trigger);
			redirectAttributes.addFlashAttribute("messaggio", "Activity aggiunta");
			model.setViewName("redirect:/user/home");
			return model;
		} else {
			model.addObject("messaggio", "Si è verificato un errore, riprova");
			model.addObject("activity", activity);
			model.setViewName("user/homepage");
			return model;
		}
	}
}