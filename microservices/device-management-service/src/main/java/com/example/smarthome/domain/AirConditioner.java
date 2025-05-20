package com.example.smarthome.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "air_conditioners")
@Getter
@Setter
@ToString(callSuper = true)
public class AirConditioner extends BaseAppliance {
    private Integer temperature;
    private String mode;

    @Override
    public String getType() {
        return "airconditioner";
    }

    @Override
    public void turnOn() {
        super.turnOn();
        if (temperature == null || temperature == 0) {
            temperature = 25;
        }
    }

    @Override
    public void turnOff() {
        super.turnOff();
        temperature = 0;
    }
}