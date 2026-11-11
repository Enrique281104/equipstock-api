package edu.sv.ues.dam235.equipstock_api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilDTO {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9+()\\- ]*$")
    private String telefono;
}