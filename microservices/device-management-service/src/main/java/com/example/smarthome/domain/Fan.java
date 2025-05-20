package com.example.smarthome.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fans")
@Getter
@Setter
@ToString(callSuper = true)
public class Fan extends BaseAppliance {
    private Integer speed;

    @Override
    public String getType() {
        return "fan";
    }

    public int getSpeed() {
        return speed != null ? speed : 0;
    }

    public void setSpeed(int speed) {
        if (speed >= 0 && speed <= 2) {
            this.speed = speed;
            this.setOn(speed > 0);
        } else {
            throw new IllegalArgumentException("Speed must be between 0 and 2");
        }
    }
}