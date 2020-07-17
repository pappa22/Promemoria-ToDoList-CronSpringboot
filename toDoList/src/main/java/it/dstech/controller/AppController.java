package it.dstech.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.validation.Valid;

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



@EnableScheduling
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

	@RequestMapping(value = { "/login", "/" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView model = new ModelAndView();
		model.setViewName("login");
		return model;
	}
	
	 	@ModelAttribute("user")
	    public UserRegistrationDao userRegistrationDto() {
	        return new UserRegistrationDao();
	    }

//	    @GetMapping
//	    public String showRegistrationForm(Model model) {
//	        return "registration";
//	    }

	 	@GetMapping(value="/registration")
	    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDao userDao, BindingResult result) throws MessagingException, IOException {
	 		ModelAndView model = new ModelAndView();
	 		User userEsistente = userService.findByEmail(userDao.getEmail());
	        if (userEsistente != null) {
	            result.rejectValue("email", null, "There is already an account registered with that email");
	        }

	        if (result.hasErrors()) {
	        	model.setViewName("error");
	            return model;
	        }

	        userService.save(userDao);
	        mailService.inviaMail(userDao.getEmail(), "Confirm registration", "User has been registered successfully");
	        model.addObject("successMessage", "User has been registered successfully, go back to login page");
	        model.setViewName("registration");
			return model;
	    }

	@GetMapping(value = "/user/home")
    public ModelAndView userIndex(Activity activity) throws MessagingException {
		ModelAndView model = new ModelAndView();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userService.findByEmail(auth.getName());   	    
	    List<Activity> activities = user.getActivities();	    
	    model.addObject("authUser", user.getEmail());
	    model.addObject("authUserImage", Base64.getEncoder().encodeToString(user.getImage()));
        model.addObject("activities", activities);
        model.addObject("activity", new Activity());
        model.addObject("title", "Activities");  
        model.setViewName("user/index");
        return model;
    }
    
    @PostMapping(value="/save")
    public ModelAndView save (@ModelAttribute Activity activity, RedirectAttributes redirectAttributes) {
    	ModelAndView model = new ModelAndView();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userService.findByEmail(auth.getName());   	
	    activity.setUser(user);
        Activity currActivity = activityService.save(activity);
        userService.addActivities(user, currActivity);
        if(currActivity != null) {
            LocalDateTime date = currActivity.getExpiredDate();
			int minute = date.getMinute();
			int hours = date.getHour();
			int day = date.getDayOfMonth();
			int month = date.getMonth().getValue();
			String expression = "";
			if ((minute - 5) < 0) {
				expression += " 0 " + (minute + 5) + " " + (hours - 1) + " " + day + " " + month + " ?";
				if((hours - 1) < 0) {
					expression += " 0 " + (minute + 5) + " " + (hours + 23) + " " + day + " " + month + " ?";
				}
			} else {
				expression += " 0 " + (minute - 5) + " " + hours + " " + day + " " + month + " ?";
			}
			CronTrigger trigger = new CronTrigger(expression, TimeZone.getTimeZone(TimeZone.getDefault().getID()));
			RunnableImpl runnable = new RunnableImpl(currActivity, mailService);
            scheduler.schedule(runnable, trigger);
            redirectAttributes.addFlashAttribute("successmessage", "Activity is saved successfully");
            model.setViewName("redirect:/user/home");
            return model;
        }else {
            model.addObject("errormessage", "Activity is not save, Please try again");
            model.addObject("activity", activity);
            model.setViewName("user/index");
            return model;
        }
    }
}