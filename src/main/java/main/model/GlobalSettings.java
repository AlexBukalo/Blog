package main.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "global_settings")
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private String value;

    public GlobalSettings(String code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }
    public GlobalSettings(){}
}
