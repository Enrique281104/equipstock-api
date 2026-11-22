package edu.sv.ues.dam235.equipstock_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "codigos_recuperacion")
public class CodigoRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, unique = true)   // un código activo por correo
    private String email;

    @Column(name = "codigo_hash", nullable = false, length = 100)
    private String codigoHash;

    @Column(nullable = false)
    private LocalDateTime expira;

    @Column(nullable = false)
    private int intentos;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
}