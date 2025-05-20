package com.example.smarthome.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lights")
@Getter
@Setter
@ToString(callSuper = true)
public class Light extends BaseAppliance {
    @Override
    public String getType() {
        return "light";
    }
}