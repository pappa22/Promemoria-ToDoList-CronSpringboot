package it.dstech.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.dstech.models.Activity;
import it.dstech.repository.ActivityRepository;


@Service
public class ActivityServiceImpl implements ActivityService {
	
	@Autowired
	private ActivityRepository activityRepository;

	@Override
	public Activity save(Activity activity) {
	    /* For solve Failed to convert property value of type 'java.lang.String' to required type 'java.time.LocalDateTime' for property 'expiredDate' */
	    String date = activity.getDate();
	    String expiredDate = date.replace("T", " ");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
	    LocalDateTime dateTime = LocalDateTime.parse(expiredDate, formatter);	
	    activity.setExpiredDate(dateTime);	    
		
		return  activityRepository.save(activity);
	}

	@Override
	public Activity findByID(Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if(activity.isPresent()) {
            return activity.get();
        } else {
            return null;
        }
	}

	@Override
	public void delete(Long id) {
		activityRepository.deleteById(id);
	}

	@Override
	public List<Activity> getAllActivities() {
		return (List<Activity>) activityRepository.findAll();
	}

}
