package com.federico.negocio.libs.commons.libs_msvc_commons.domain;


import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PuntoVenta")
public class PuntoVenta implements Serializable{

    @Id
    private Integer id;
    private String puntoVenta;
}
