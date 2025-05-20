package com.example.smarthome.service;

import com.example.smarthome.domain.BaseAppliance;
import com.example.smarthome.domain.Fan;
import com.example.smarthome.domain.Light;
import com.example.smarthome.domain.AirConditioner;
import com.example.smarthome.repository.FanRepository;
import com.example.smarthome.repository.LightRepository;
import com.example.smarthome.repository.AirConditionerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplianceService {
    private static final Logger logger = LoggerFactory.getLogger(ApplianceService.class);

    private final LightRepository lightRepository;
    private final FanRepository fanRepository;
    private final AirConditionerRepository airConditionerRepository;
    private final EventService eventService;

    public List<BaseAppliance> getAllAppliances() {
        List<BaseAppliance> allAppliances = new ArrayList<>();
        allAppliances.addAll(lightRepository.findAll());
        allAppliances.addAll(fanRepository.findAll());
        allAppliances.addAll(airConditionerRepository.findAll());
        return allAppliances;
    }

    public Optional<BaseAppliance> getApplianceById(String id) {
        Optional<BaseAppliance> appliance = lightRepository.findById(id)
                .map(a -> (BaseAppliance) a);
        if (appliance.isPresent())
            return appliance;

        appliance = fanRepository.findById(id)
                .map(a -> (BaseAppliance) a);
        if (appliance.isPresent())
            return appliance;

        return airConditionerRepository.findById(id)
                .map(a -> (BaseAppliance) a);
    }

    @Transactional
    public BaseAppliance saveAppliance(BaseAppliance appliance) {
        BaseAppliance savedAppliance;
        if (appliance instanceof Light) {
            savedAppliance = lightRepository.save((Light) appliance);
        } else if (appliance instanceof Fan) {
            savedAppliance = fanRepository.save((Fan) appliance);
        } else if (appliance instanceof AirConditioner) {
            savedAppliance = airConditionerRepository.save((AirConditioner) appliance);
        } else {
            throw new IllegalArgumentException("Unknown appliance type: " + appliance.getClass().getName());
        }
        return savedAppliance;
    }

    @Transactional
    public void deleteAppliance(String id) {
        Optional<BaseAppliance> appliance = getApplianceById(id);
        if (appliance.isPresent()) {
            if (lightRepository.existsById(id)) {
                lightRepository.deleteById(id);
            } else if (fanRepository.existsById(id)) {
                fanRepository.deleteById(id);
            } else if (airConditionerRepository.existsById(id)) {
                airConditionerRepository.deleteById(id);
            }
        } else {
            throw new RuntimeException("Appliance not found with id: " + id);
        }
    }

    @Transactional
    public BaseAppliance updateAppliance(String id, BaseAppliance updatedAppliance) {
        logger.info("Updating appliance with id {}", id);

        BaseAppliance appliance;

        if (updatedAppliance instanceof Fan) {
            appliance = fanRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Fan not found with id " + id));
            updateFan((Fan) appliance, (Fan) updatedAppliance);
            appliance = fanRepository.save((Fan) appliance);

        } else if (updatedAppliance instanceof Light) {
            appliance = lightRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Light not found with id " + id));
            updateLight((Light) appliance, (Light) updatedAppliance);
            appliance = lightRepository.save((Light) appliance);

        } else if (updatedAppliance instanceof AirConditioner) {
            appliance = airConditionerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("AC not found with id " + id));
            updateAirConditioner((AirConditioner) appliance, (AirConditioner) updatedAppliance);
            appliance = airConditionerRepository.save((AirConditioner) appliance);

        } else {
            throw new IllegalArgumentException("Unsupported appliance type");
        }

        logger.info("Appliance updated successfully: {}", appliance);
        return appliance;
    }

    private void updateFan(Fan existing, Fan updated) {
        logger.info("Updating fan - ID: {}, Current speed: {}, New speed: {}",
                existing.getId(), existing.getSpeed(), updated.getSpeed());

        existing.setName(updated.getName());
        existing.setSpeed(updated.getSpeed());

        Fan savedFan = fanRepository.save(existing);
        logger.info("Fan updated successfully - ID: {}, Speed: {}, IsOn: {}",
                savedFan.getId(), savedFan.getSpeed(), savedFan.isOn());
    }

    private void updateLight(Light existing, Light updated) {
        existing.setName(updated.getName());
        existing.setOn(updated.isOn());
    }

    private void updateAirConditioner(AirConditioner existing, AirConditioner updated) {
        existing.setName(updated.getName());
        existing.setOn(updated.isOn());
        existing.setTemperature(updated.getTemperature());
        existing.setMode(updated.getMode());
    }

    @Transactional
    public void resetAllAppliances() {
        List<BaseAppliance> appliances = getAllAppliances();
        for (BaseAppliance appliance : appliances) {
            appliance.turnOff();
            if (appliance instanceof Fan) {
                ((Fan) appliance).setSpeed(0);
            } else if (appliance instanceof AirConditioner) {
                AirConditioner ac = (AirConditioner) appliance;
                ac.setMode("off");
                ac.setTemperature(22);
            }
            saveAppliance(appliance);
        }
        eventService.triggerSystemReset();
    }
}