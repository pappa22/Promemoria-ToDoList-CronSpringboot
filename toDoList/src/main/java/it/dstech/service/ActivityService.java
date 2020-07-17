package it.dstech.service;

import java.util.List;

import it.dstech.models.Activity;


public interface ActivityService {

    Activity save(Activity activity);
    Activity findByID(Long id);
    void delete(Long id);
	List<Activity> getAllActivities();	
    
}