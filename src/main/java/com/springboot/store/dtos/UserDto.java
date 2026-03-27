package com.springboot.store.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDto {
    //@JsonProperty("User_id") for renaming fields
    private Long id;
    private String name;
   // @JsonIgnore for ignoring fields  and there's @JsonFormat
   //@JsonInclude() for specifying which fields to include eg Non null
    private String email;


}
