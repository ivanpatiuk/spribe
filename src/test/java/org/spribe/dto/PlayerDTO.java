package org.spribe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
 * This class is used for mapping JSON responses into Java objects. As most of the responses from the controller contain
 * the same fields, it is enough to map them into 1 common object. Optional fields are ignored and are validated by JSON schema validation.
 */
public class PlayerDTO {

    private Integer age;
    private String gender;
    private Long id;
    private String login;
    private String password;
    private String role;
    private String screenName;
}
