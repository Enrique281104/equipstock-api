package edu.sv.ues.dam235.equipstock_api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CambioPasswordDTO {

    @NotBlank
    private String actual;

    @NotBlank
    @Size(min = 8, max = 72)
    private String nueva;
}