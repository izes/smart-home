package com.example.smarthome.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Fan.class, name = "fan"),
        @JsonSubTypes.Type(value = Light.class, name = "light"),
        @JsonSubTypes.Type(value = AirConditioner.class, name = "airconditioner")
})
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseAppliance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonProperty
    private String name;

    public abstract String getType();

    @Column(name = "is_on")
    @JsonProperty("isOn")
    private boolean on;

    public void turnOn() {
        this.on = true;
    }

    public void turnOff() {
        this.on = false;
    }
}