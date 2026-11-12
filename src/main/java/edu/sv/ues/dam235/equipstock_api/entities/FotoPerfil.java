package edu.sv.ues.dam235.equipstock_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "fotos_perfil")
public class FotoPerfil {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "datos", nullable = false, columnDefinition = "bytea")
    private byte[] datos;
}