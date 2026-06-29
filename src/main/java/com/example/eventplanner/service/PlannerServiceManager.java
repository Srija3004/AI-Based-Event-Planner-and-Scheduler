package com.example.eventplanner.service;

import com.example.eventplanner.model.Service;
import com.example.eventplanner.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class PlannerServiceManager {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Service> getServicesByPlanner(Long plannerId) {
        return serviceRepository.findByPlanner_Id(plannerId);
    }

    public Service addService(Service service) {
        return serviceRepository.save(service);
    }

    public Service updateService(Service updatedService) {
        return serviceRepository.save(updatedService);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
